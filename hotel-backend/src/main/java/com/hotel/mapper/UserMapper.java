package com.hotel.mapper;

import com.hotel.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 查询用户列表
     */
    List<User> selectList(@Param("username") String username,
                          @Param("role") String role,
                          @Param("status") Integer status);

    /**
     * 统计用户数量
     */
    Long count(@Param("username") String username,
               @Param("role") String role,
               @Param("status") Integer status);

    /**
     * 新增用户
     */
    int insert(User user);

    /**
     * 更新用户
     */
    int update(User user);

    /**
     * 删除用户
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新用户状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新用户密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 检查用户名是否存在
     */
    int checkUsernameExists(@Param("username") String username, @Param("excludeId") Long excludeId);
}
