package com.hotel.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

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
     * 订单状态
     */
    private Integer status;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 入住日期-开始
     */
    private LocalDate checkInDateStart;

    /**
     * 入住日期-结束
     */
    private LocalDate checkInDateEnd;
}
