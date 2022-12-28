package com.study.redis.service;

import com.study.redis.pojo.SeckillVoucher;
import com.study.redis.pojo.Voucher;
import com.study.redis.pojo.list.SeckillVoucherList;
import com.study.redis.pojo.list.VoucherList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.study.redis.constant.RedisConstants.SECKILL_STOCK_KEY;

@Service
@Slf4j
public class VoucherService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

//    @Transactional
    public void addSeckillVoucher(Voucher voucher) {
        // 保存优惠券
        VoucherList.add(voucher);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        SeckillVoucherList.add(seckillVoucher);
        // 保存秒杀库存到Redis中
        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucher.getId(), voucher.getStock().toString());
    }
}
