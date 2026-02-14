package com.hotel.service;

import com.hotel.common.PageResult;
import com.hotel.dto.*;
import com.hotel.entity.User;
import com.hotel.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    UserVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     */
    UserVO register(RegisterDTO registerDTO);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 获取当前登录用户
     */
    User getCurrentUser();

    /**
     * 获取当前登录用户信息
     */
    UserVO getCurrentUserInfo();

    /**
     * 分页查询用户列表
     */
    PageResult<UserVO> listUsers(UserQueryDTO queryDTO);

    /**
     * 根据ID查询用户
     */
    UserVO getUserById(Long id);

    /**
     * 新增用户
     */
    UserVO addUser(UserDTO userDTO);

    /**
     * 修改用户
     */
    UserVO updateUser(UserDTO userDTO);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 修改用户状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 重置密码
     */
    void resetPassword(Long id);

    /**
     * 修改密码
     */
    void changePassword(PasswordDTO passwordDTO);

    /**
     * 修改个人信息
     */
    UserVO updateProfile(UserDTO userDTO);
}
