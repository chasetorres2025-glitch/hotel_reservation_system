package com.hotel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
public class Order implements Serializable {

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
     * 客房ID
     */
    private Long roomId;

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
     * 订单状态（0待支付 1待确认 2已确认 3已入住 4已完成 5已取消 6已拒绝）
     */
    private Integer status;

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

    // ========== 关联字段 ==========

    /**
     * 用户名（关联查询）
     */
    private String username;

    /**
     * 用户真实姓名（关联查询）
     */
    private String realName;

    /**
     * 房间号（关联查询）
     */
    private String roomNumber;

    /**
     * 客房类型名称（关联查询）
     */
    private String typeName;
}
