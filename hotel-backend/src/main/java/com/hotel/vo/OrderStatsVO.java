package com.hotel.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单统计VO
 */
@Data
public class OrderStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总订单数
     */
    private Integer totalOrders;

    /**
     * 待支付订单数
     */
    private Integer pendingPayment;

    /**
     * 待确认订单数
     */
    private Integer pendingConfirm;

    /**
     * 已确认订单数
     */
    private Integer confirmed;

    /**
     * 已入住订单数
     */
    private Integer checkedIn;

    /**
     * 已完成订单数
     */
    private Integer completed;

    /**
     * 已取消订单数
     */
    private Integer cancelled;

    /**
     * 已拒绝订单数
     */
    private Integer rejected;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 已支付金额
     */
    private BigDecimal paidAmount;
}
