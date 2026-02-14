package com.hotel.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客房DTO
 */
@Data
public class RoomDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客房ID（修改时必填）
     */
    private Long id;

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
     * 实际价格
     */
    private BigDecimal price;

    /**
     * 状态（0空闲 1预订 2入住 3维护）
     */
    private Integer status;

    /**
     * 客房描述
     */
    private String description;

    /**
     * 客房图片
     */
    private String imageUrl;
}
