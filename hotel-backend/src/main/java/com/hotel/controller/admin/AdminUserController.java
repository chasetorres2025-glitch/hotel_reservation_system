package com.hotel.controller.admin;

import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.dto.*;
import com.hotel.service.UserService;
import com.hotel.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理端 - 用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 用户列表查询
     */
    @GetMapping("/list")
    public Result<PageResult<UserVO>> list(UserQueryDTO queryDTO) {
        PageResult<UserVO> result = userService.listUsers(queryDTO);
        return Result.success(result);
    }

    /**
     * 用户详情查询
     */
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<UserVO> add(@Valid @RequestBody UserDTO userDTO) {
        UserVO user = userService.addUser(userDTO);
        return Result.success("新增成功", user);
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result<UserVO> update(@Valid @RequestBody UserDTO userDTO) {
        UserVO user = userService.updateUser(userDTO);
        return Result.success("修改成功", user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功", null);
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success("状态修改成功", null);
    }

    /**
     * 重置密码
     */
    @PutMapping("/reset-password")
    public Result<Void> resetPassword(@RequestParam Long id) {
        userService.resetPassword(id);
        return Result.success("密码已重置为123456", null);
    }
}
