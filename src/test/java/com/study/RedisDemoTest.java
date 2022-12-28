package com.study;

import com.study.redis.constant.RedisConstants;
import com.study.redis.pojo.Shop;
import com.study.redis.pojo.list.ShopList;
import com.study.redis.service.ShopService;
import com.study.redis.util.CacheClient;
import com.study.redis.util.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisDemoTest {
    @Resource
    private ShopService shopService;

    @Resource
    private CacheClient cacheClient;

    @Resource
    private RedisIdWorker redisIdWorker;

    private ExecutorService es = Executors.newFixedThreadPool(500);

    @Test
    public void testSaveShop() throws InterruptedException {
//        shopService.saveShop2Redis(2l,10l);
        Shop shop = ShopList.findById(2l);
        cacheClient.setWithLogicalExpire(RedisConstants.CACHE_SHOP_KEY + 1l,shop,10l, TimeUnit.SECONDS);
    }

    @Test
    public void testRedisIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);
        Runnable task = ()->{
            for (int i = 0; i < 100; i++) {
                System.out.println("id:"+redisIdWorker.nextId("order"));
            }
            latch.countDown();
        };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            es.submit(task);//线程池是异步的，得借助CountDownLatch
        }
        latch.await();//等待所有countdown结束
        long end = System.currentTimeMillis();
        System.out.println("time:"+(end - begin));
    }
}
