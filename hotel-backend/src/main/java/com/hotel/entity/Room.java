package com.hotel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客房信息实体类
 */
@Data
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客房ID
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
     * 客房类型名称（关联查询）
     */
    private String typeName;

    /**
     * 客房类型编码（关联查询）
     */
    private String typeCode;
}
