package com.hotel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单VO
 */
@Data
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 客房ID
     */
    private Long roomId;

    /**
     * 房间号
     */
    private String roomNumber;

    /**
     * 客房类型名称
     */
    private String typeName;

    /**
     * 入住日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    /**
     * 退房日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    /**
     * 入住天数
     */
    private Integer days;

    /**
     * 入住人数
     */
    private Integer guestCount;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 已支付金额
     */
    private BigDecimal paidAmount;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单状态描述
     */
    private String statusDesc;

    /**
     * 特殊要求
     */
    private String specialRequest;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
