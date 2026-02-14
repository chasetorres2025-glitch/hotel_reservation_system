package com.hotel.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客房查询DTO
 */
@Data
public class RoomQueryDTO implements Serializable {

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
     * 房间号
     */
    private String roomNumber;

    /**
     * 客房类型ID
     */
    private Long typeId;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;
}
