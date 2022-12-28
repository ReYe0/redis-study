package com.study.redis.controller;

import com.study.redis.pojo.VoucherOrder;
import com.study.redis.service.VoucherOrderService;
import com.study.redis.service.VoucherService;
import com.study.redis.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {
    @Resource
    private VoucherOrderService voucherOrderService;

    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {
        return voucherOrderService.seckillVoucher(voucherId);
    }
}
