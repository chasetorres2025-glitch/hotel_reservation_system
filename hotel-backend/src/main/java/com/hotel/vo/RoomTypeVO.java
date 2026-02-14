package com.hotel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客房类型VO
 */
@Data
public class RoomTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型ID
     */
    private Long id;

    /**
     * 类型编码
     */
    private String typeCode;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 基础价格
     */
    private BigDecimal basePrice;

    /**
     * 床型
     */
    private String bedType;

    /**
     * 面积
     */
    private BigDecimal area;

    /**
     * 设施配置
     */
    private String facilities;

    /**
     * 类型描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
