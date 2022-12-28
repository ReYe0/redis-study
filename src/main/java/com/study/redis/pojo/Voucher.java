package com.study.redis.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 商铺id
     */
    private Long shopId;

    /**
     * 代金券标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 使用规则
     */
    private String rules;

    /**
     * 支付金额
     */
    private Long payValue;

    /**
     * 抵扣金额
     */
    private Long actualValue;

    /**
     * 优惠券类型
     */
    private Integer type;

    /**
     * 优惠券类型
     */
    private Integer status;
    /**
     * 库存
     */
    private Integer stock;

    /**
     * 生效时间
     */
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    private LocalDateTime endTime;

    public Voucher(Long id, Long shopId, String title, String subTitle, String rules, Long payValue, Long actualValue, Integer type, Integer status, Integer stock, LocalDateTime beginTime, LocalDateTime endTime) {
        this.id = id;
        this.shopId = shopId;
        this.title = title;
        this.subTitle = subTitle;
        this.rules = rules;
        this.payValue = payValue;
        this.actualValue = actualValue;
        this.type = type;
        this.status = status;
        this.stock = stock;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public Voucher next;

    /**
     * 创建时间
     */
//    private LocalDateTime createTime;


    /**
     * 更新时间
     */
//    private LocalDateTime updateTime;


}