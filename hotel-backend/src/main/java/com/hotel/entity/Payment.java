package com.hotel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付记录ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付流水号
     */
    private String paymentNo;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付状态（0待支付 1已支付 2已退款）
     */
    private Integer status;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
