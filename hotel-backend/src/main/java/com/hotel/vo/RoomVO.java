package com.hotel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客房信息VO
 */
@Data
public class RoomVO implements Serializable {

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
     * 客房类型名称
     */
    private String typeName;

    /**
     * 客房类型编码
     */
    private String typeCode;

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
     * 状态描述
     */
    private String statusDesc;

    /**
     * 客房描述
     */
    private String description;

    /**
     * 客房图片
     */
    private String imageUrl;

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
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
