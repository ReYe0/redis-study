package com.study.redis.controller;

import com.study.redis.pojo.Shop;
import com.study.redis.service.ShopService;
import com.study.redis.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    private ShopService shopService;

    @GetMapping("/{id}")
    public Result queryShopById(@PathVariable("id") Long id) {
        //这里是直接查询数据库
        return shopService.queryById(id);
    }

    @PutMapping
    public Result updateShop(@RequestBody Shop shop){
        //写入数据库
        return shopService.updateById(shop);
    }
}
