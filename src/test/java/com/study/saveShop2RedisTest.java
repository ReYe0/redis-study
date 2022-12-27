package com.study;

import com.study.redis.constant.RedisConstants;
import com.study.redis.pojo.Shop;
import com.study.redis.pojo.ShopList;
import com.study.redis.service.ShopService;
import com.study.redis.util.CacheClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class saveShop2RedisTest {
    @Resource
    private ShopService shopService;

    @Resource
    private CacheClient cacheClient;

    @Test
    public void testSaveShop() throws InterruptedException {
//        shopService.saveShop2Redis(2l,10l);
        Shop shop = ShopList.findById(2l);
        cacheClient.setWithLogicalExpire(RedisConstants.CACHE_SHOP_KEY + 1l,shop,10l, TimeUnit.SECONDS);
    }
}
