package com.study.redis.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillVoucher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联的优惠券的id
     */
    private Long voucherId;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 生效时间
     */
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    private LocalDateTime endTime;

    public SeckillVoucher next;



    /**
     * 更新时间
     */
//    private LocalDateTime updateTime;

    public SeckillVoucher(Long voucherId, Integer stock, LocalDateTime createTime, LocalDateTime beginTime, LocalDateTime endTime) {
        this.voucherId = voucherId;
        this.stock = stock;
        this.createTime = createTime;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }


}
