package com.study.redis.service;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.study.redis.pojo.Shop;
import com.study.redis.pojo.ShopList;
import com.study.redis.util.CacheClient;
import com.study.redis.util.RedisData;
import com.study.redis.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.study.redis.constant.RedisConstants.*;

@Service
@Slf4j
public class ShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;//封装之后的工具类

    public Result queryById(Long id) {
        //解决缓存穿透
        //Shop shop = queryWithPassThrough(id);
        Shop shop = cacheClient.queryWithPassThrough(CACHE_SHOP_KEY, id, Shop.class, shopId -> ShopList.findById(shopId), CACHE_SHOP_TTL, TimeUnit.MINUTES);//封装之后的写法
        //互斥锁解决缓存击穿
        // Shop shop = queryWithMutex(id);
//        Shop shop = cacheClient.queryWithMutex(CACHE_SHOP_KEY, id, Shop.class, shopId -> ShopList.findById(shopId), CACHE_SHOP_TTL, TimeUnit.MINUTES);//封装之后的写法
        //逻辑过期解决缓存击穿
        // Shop shop = queryWithLogicalExpire(id);
//        Shop shop = cacheClient.queryWithLogicalExpire(CACHE_SHOP_KEY, id, Shop.class, shopId -> ShopList.findById(shopId), CACHE_SHOP_TTL, TimeUnit.MINUTES);//封装之后的写法
        if (shop == null) {
            return Result.fail("店铺不存在！");
        }
        return Result.ok(shop);
    }

    /**
     * 缓存穿透 缓存空对象解决
     * @param id
     * @return com.study.redis.pojo.Shop
     * @author xuy
     * @date 2022/12/27 16:24
     */
    public Shop queryWithPassThrough(Long id){
        String key = CACHE_SHOP_KEY + id;
        // 1. Mredis查询商铺缓存
        String shopJson  = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
//            3.存在，直接返回
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;

        }
        //判断命中的是不是空值，缓存空对象，解决缓存穿透的策略
        if(shopJson != null){
            //返回一个错误信息
            return null;
        }
        //4.不存在，根据id查询数据库
        Shop shop = ShopList.findById(id);
        //5.不存在，返回错误
        if (shop == null) {
            //缓存空对象，解决缓存穿透
            stringRedisTemplate.opsForValue().set(key, "",CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        //6.存在，写入
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop),CACHE_SHOP_TTL, TimeUnit.MINUTES);
        //7.返回
        return shop;


    }

    /**
     * 缓存击穿 互斥锁解决
     * @param id
     * @return com.study.redis.pojo.Shop
     * @author xuy
     * @date 2022/12/27 16:32
     */
    public Shop queryWithMutex(Long id){
        String key = CACHE_SHOP_KEY + id;
        // 1. Mredis查询商铺缓存
        String shopJson  = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
//            3.存在，直接返回
            return JSONUtil.toBean(shopJson, Shop.class);

        }
        //判断命中的是不是空值，缓存空对象，解决缓存穿透的策略
        if(shopJson != null){
            //返回一个错误信息
            return null;
        }
        //4.实现缓存重建
        //4.1获取互斥锁
        String lockKey = LOCK_SHOP_KEY + id;
        Shop shop = null;
        try {
            boolean isLock = tryLock(lockKey);
            //4.2判断是否获取成功
            if (!isLock){
                //4.3失败，则休眠或者重试
                Thread.sleep(50);
                return queryWithMutex(id);
            }
            //4.4成功，根据id查询数据库

            //4.不存在，根据id查询数据库
            shop = ShopList.findById(id);
            // 模拟重建的延迟
            Thread.sleep(200);
            //5.不存在，返回错误
            if (shop == null) {
                //缓存空对象，解决缓存穿透
                stringRedisTemplate.opsForValue().set(key, "",CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            //6.存在，写入
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop),CACHE_SHOP_TTL, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            //7.释放锁
            unlock(lockKey);
        }
        //8.返回
        return shop;


    }

    /**
     * 利用逻辑过期解决缓存击穿问题中的重建
     * @param id shopId
     * @param expireSeconds 过期时间
     * @return void
     * @author xuy
     * @date 2022/12/27 16:49
     */
    public void saveShop2Redis(Long id,Long expireSeconds) throws InterruptedException {
        Thread.sleep(200);
        //1.查询店铺数据
        Shop shop = ShopList.findById(id);
        //2.封装逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        //3.写入redis
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id ,JSONUtil.toJsonStr(redisData));
    }

    // 创建一个线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 缓存击穿 逻辑过期解决
     * @param id
     * @return com.study.redis.pojo.Shop
     * @author xuy
     * @date 2022/12/27 17:07
     */
    public Shop queryWithLogicalExpire(Long id){
        String key = CACHE_SHOP_KEY + id;
        // 1. Mredis查询商铺缓存
        String shopJson  = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
//            3.存在，直接返回
            return null;

        }
        //4.命中，需要先把JSON序列化为对象
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        Shop shop = JSONUtil.toBean((JSONObject) redisData.getData(), Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        //5.判断是否过期
        if(expireTime.isAfter(LocalDateTime.now())) {
            // 5.1.未过期，直接返回店铺信息
            return shop;
        }
        // 5.2.已过期，需要缓存重建
        // 6.缓存重建
        // 6.1.获取互斥锁
        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        // 6.2.判断是否获取锁成功
        if (isLock){
            CACHE_REBUILD_EXECUTOR.submit( ()->{

                try{
                    //重建缓存
                    this.saveShop2Redis(id,20L);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }finally {
                    unlock(lockKey);
                }
            });
        }
        //7.返回
        return shop;


    }
    @Transactional
    public Result updateById(Shop shop){
        Long id = shop.getId();
        if(id == null){
            return Result.fail("店铺id不能为空");
        }
        // 1.更新数据库
        ShopList.updateById(shop);
        // 2.删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);
        return Result.ok();
    }


    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);//setnx
        return BooleanUtil.isTrue(flag);//帮助拆箱，如果封装类是null会包空指针异常
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
