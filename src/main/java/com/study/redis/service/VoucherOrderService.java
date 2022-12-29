package com.study.redis.service;

import com.study.redis.pojo.SeckillVoucher;
import com.study.redis.pojo.VoucherOrder;
import com.study.redis.pojo.list.SeckillVoucherList;
import com.study.redis.pojo.list.VoucherOrderList;
import com.study.redis.util.RedisIdWorker;
import com.study.redis.util.Result;
import com.study.redis.util.StudentHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
@Slf4j
public class VoucherOrderService {
    @Resource
    private RedisIdWorker redisIdWorker;

    @Transactional
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
        synchronized (stuId.toString().intern()) {
            //获取代理对象(事务)
            VoucherOrderService proxy = (VoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId);
        }
    }

    @Transactional
    public  Result createVoucherOrder(Long voucherId) {
        Long stuId = StudentHolder.getStudent().getId();
        synchronized (stuId.toString().intern()){
            // 5.1.查询订单
            VoucherOrder order = VoucherOrderList.findById(voucherId, stuId);
            // 5.2.判断是否存在
            if (order != null ) {
                // 用户已经购买过了
                return Result.fail("用户已经购买过一次！");
            }
            // 6.扣减库存
            SeckillVoucher voucher = SeckillVoucherList.findById(voucherId);
            if(voucher.getStock() > 0){
                voucher.setStock(voucher.getStock()-1);
                Boolean success = SeckillVoucherList.updateById(voucher);
                if (!success) {
                    // 扣减失败
                    return Result.fail("库存不足！");
                }
            }
            // 7.创建订单
            VoucherOrder voucherOrder = new VoucherOrder();
            // 7.1.订单id
            long orderId = redisIdWorker.nextId("order");
            voucherOrder.setId(orderId);
            // 7.2.用户id
            voucherOrder.setUserId(stuId);
            // 7.3.代金券id
            voucherOrder.setVoucherId(voucherId);
            VoucherOrderList.add(voucherOrder);
            // 7.返回订单id
            return Result.ok(orderId);
        }

    }
}
