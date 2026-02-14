package com.hotel.controller.user;

import com.hotel.common.Result;
import com.hotel.dto.PasswordDTO;
import com.hotel.dto.UserDTO;
import com.hotel.service.UserService;
import com.hotel.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户端 - 个人信息控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    @Autowired
    private UserService userService;

    /**
     * 个人信息查询
     */
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        UserVO user = userService.getCurrentUserInfo();
        return Result.success(user);
    }

    /**
     * 个人信息修改
     */
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@RequestBody UserDTO userDTO) {
        UserVO user = userService.updateProfile(userDTO);
        return Result.success("修改成功", user);
    }

    /**
     * 密码修改
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        userService.changePassword(passwordDTO);
        return Result.success("密码修改成功", null);
    }
}
