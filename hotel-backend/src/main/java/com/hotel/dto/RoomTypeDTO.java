package com.hotel.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客房类型DTO
 */
@Data
public class RoomTypeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型ID（修改时必填）
     */
    private Long id;

    /**
     * 类型编码
     */
    @NotNull(message = "类型编码不能为空")
    private String typeCode;

    /**
     * 类型名称
     */
    @NotNull(message = "类型名称不能为空")
    private String typeName;

    /**
     * 基础价格
     */
    @NotNull(message = "基础价格不能为空")
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
     * 状态（1启用 0禁用）
     */
    private Integer status;
}
