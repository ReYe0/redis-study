package com.study.redis.controller;

import com.study.redis.pojo.Voucher;
import com.study.redis.pojo.list.VoucherList;
import com.study.redis.service.VoucherService;
import com.study.redis.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/voucher")
public class VoucherController {
    @Resource
    private VoucherService voucherService;
    /**
     * 新增普通优惠券
     * @param voucher
     * @return com.study.redis.util.Result
     * @author xuy
     * @date 2022/12/28 14:26
     */
    @PostMapping("add")
    public Result addVoucher(@RequestBody Voucher voucher) {
        VoucherList.add(voucher);
        return Result.ok(voucher.getId());
    }
    /**
     * 新增秒杀券
     * @param voucher
     * @return com.study.redis.util.Result
     * @author xuy
     * @date 2022/12/28 14:26
     */
    @PostMapping("seckill")
    public Result addSeckillVoucher(@RequestBody Voucher voucher) {
        voucherService.addSeckillVoucher(voucher);
        return Result.ok(voucher.getId());
    }
}
