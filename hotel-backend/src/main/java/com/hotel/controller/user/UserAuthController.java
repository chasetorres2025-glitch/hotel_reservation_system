package com.hotel.controller.user;

import com.hotel.common.Result;
import com.hotel.dto.LoginDTO;
import com.hotel.dto.RegisterDTO;
import com.hotel.service.UserService;
import com.hotel.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户端 - 认证控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserAuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        UserVO user = userService.register(registerDTO);
        return Result.success("注册成功", user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UserVO user = userService.login(loginDTO);
        return Result.success("登录成功", user);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success("退出成功", null);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getInfo() {
        UserVO user = userService.getCurrentUserInfo();
        return Result.success(user);
    }
}
