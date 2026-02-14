package com.hotel.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询DTO
 */
@Data
public class UserQueryDTO implements Serializable {

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
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 角色
     */
    private String role;

    /**
     * 状态
     */
    private Integer status;
}
