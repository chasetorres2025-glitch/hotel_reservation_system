package com.hotel.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 订单DTO
 */
@Data
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客房ID
     */
    @NotNull(message = "客房ID不能为空")
    private Long roomId;

    /**
     * 入住日期
     */
    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;

    /**
     * 退房日期
     */
    @NotNull(message = "退房日期不能为空")
    private LocalDate checkOutDate;

    /**
     * 入住人数
     */
    @NotNull(message = "入住人数不能为空")
    private Integer guestCount;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /**
     * 特殊要求
     */
    private String specialRequest;
}
