package com.study.redis.service;

import com.study.redis.pojo.SeckillVoucher;
import com.study.redis.pojo.VoucherOrder;
import com.study.redis.pojo.list.SeckillVoucherList;
import com.study.redis.pojo.list.VoucherOrderList;
import com.study.redis.util.RedisIdWorker;
import com.study.redis.util.Result;
import com.study.redis.util.StudentHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
@Slf4j
public class VoucherOrderService {
    @Resource
    private RedisIdWorker redisIdWorker;

//    @Transactional
    public Result seckillVoucher(Long voucherId) {
        // 1.查询优惠券
        SeckillVoucher voucher = SeckillVoucherList.findById(voucherId);
        // 2.判断秒杀是否开始
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            // 尚未开始
            return Result.fail("秒杀尚未开始！");
        }
        // 3.判断秒杀是否已经结束
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            // 尚未开始
            return Result.fail("秒杀已经结束！");
        }
        // 4.判断库存是否充足
        if (voucher.getStock() < 1) {
            // 库存不足
            return Result.fail("库存不足！");
        }
        Long stuId = StudentHolder.getStudent().getId();
        // TODO 判断用户是否获取过优惠券
        VoucherOrder order = VoucherOrderList.findById(voucherId, stuId);
        if (order != null){
            // 用户已经购买过了
            return Result.fail("用户已经购买过一次！");
        }
        //5，扣减库存
        if(voucher.getStock() > 0){
            voucher.setStock(voucher.getStock()-1);
            Boolean success = SeckillVoucherList.updateById(voucher);
            if (!success) {
                //扣减库存
                return Result.fail("库存不足！");
            }
        }
        //6.创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        // 6.1.订单id
        long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        // 6.2.用户id
//        Long stuId = StudentHolder.getStudent().getId();
        voucherOrder.setUserId(stuId);
        // 6.3.代金券id
        voucherOrder.setVoucherId(voucherId);
//        创建优惠券订单
//        save(voucherOrder);
        VoucherOrderList.add(voucherOrder);
        System.out.println(SeckillVoucherList.findById(voucherId));//剩下的订单
        return Result.ok(orderId);

    }
}
