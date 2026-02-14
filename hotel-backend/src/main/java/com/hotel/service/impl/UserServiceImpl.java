package com.hotel.service.impl;

import com.hotel.common.Constants;
import com.hotel.common.ErrorCode;
import com.hotel.common.PageResult;
import com.hotel.dto.*;
import com.hotel.entity.User;
import com.hotel.exception.BusinessException;
import com.hotel.exception.ErrorCodeEnum;
import com.hotel.mapper.UserMapper;
import com.hotel.service.UserService;
import com.hotel.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HttpSession session;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserVO login(LoginDTO loginDTO) {
        // 查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.LOGIN_ERROR);
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCodeEnum.LOGIN_ERROR);
        }

        // 检查状态
        if (user.getStatus() == Constants.USER_STATUS_DISABLED) {
            throw new BusinessException(ErrorCodeEnum.USER_DISABLED);
        }

        // 保存到Session
        session.setAttribute(Constants.SESSION_USER_KEY, user);

        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO register(RegisterDTO registerDTO) {
        // 检查用户名是否存在
        if (userMapper.checkUsernameExists(registerDTO.getUsername(), null) > 0) {
            throw new BusinessException(ErrorCodeEnum.USER_EXISTS);
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setRole(Constants.ROLE_USER);
        user.setStatus(Constants.USER_STATUS_ENABLED);

        userMapper.insert(user);

        return convertToVO(user);
    }

    @Override
    public void logout() {
        session.removeAttribute(Constants.SESSION_USER_KEY);
    }

    @Override
    public User getCurrentUser() {
        User user = (User) session.getAttribute(Constants.SESSION_USER_KEY);
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        return user;
    }

    @Override
    public UserVO getCurrentUserInfo() {
        User user = getCurrentUser();
        // 重新从数据库获取最新信息
        User dbUser = userMapper.selectById(user.getId());
        return convertToVO(dbUser);
    }

    @Override
    public PageResult<UserVO> listUsers(UserQueryDTO queryDTO) {
        List<User> users = userMapper.selectList(
                queryDTO.getUsername(),
                queryDTO.getRole(),
                queryDTO.getStatus()
        );

        Long total = userMapper.count(
                queryDTO.getUsername(),
                queryDTO.getRole(),
                queryDTO.getStatus()
        );

        List<UserVO> voList = users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO addUser(UserDTO userDTO) {
        // 检查用户名是否存在
        if (userMapper.checkUsernameExists(userDTO.getUsername(), null) > 0) {
            throw new BusinessException(ErrorCodeEnum.USER_EXISTS);
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(StringUtils.defaultIfEmpty(userDTO.getPassword(), "123456")));
        user.setRealName(userDTO.getRealName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setRole(StringUtils.defaultIfEmpty(userDTO.getRole(), Constants.ROLE_USER));
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : Constants.USER_STATUS_ENABLED);

        userMapper.insert(user);

        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO updateUser(UserDTO userDTO) {
        User user = userMapper.selectById(userDTO.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 检查用户名是否重复
        if (StringUtils.isNotBlank(userDTO.getUsername()) &&
                userMapper.checkUsernameExists(userDTO.getUsername(), userDTO.getId()) > 0) {
            throw new BusinessException(ErrorCodeEnum.USER_EXISTS);
        }

        if (StringUtils.isNotBlank(userDTO.getUsername())) {
            user.setUsername(userDTO.getUsername());
        }
        if (StringUtils.isNotBlank(userDTO.getRealName())) {
            user.setRealName(userDTO.getRealName());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (StringUtils.isNotBlank(userDTO.getRole())) {
            user.setRole(userDTO.getRole());
        }
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }

        userMapper.update(user);

        return convertToVO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void resetPassword(Long id) {
        userMapper.updatePassword(id, passwordEncoder.encode("123456"));
    }

    @Override
    @Transactional
    public void changePassword(PasswordDTO passwordDTO) {
        User user = getCurrentUser();

        // 验证原密码
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "原密码错误");
        }

        // 验证新密码和确认密码
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "两次密码输入不一致");
        }

        // 更新密码
        userMapper.updatePassword(user.getId(), passwordEncoder.encode(passwordDTO.getNewPassword()));
    }

    @Override
    @Transactional
    public UserVO updateProfile(UserDTO userDTO) {
        User user = getCurrentUser();

        if (StringUtils.isNotBlank(userDTO.getRealName())) {
            user.setRealName(userDTO.getRealName());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }

        userMapper.update(user);

        // 更新Session中的用户信息
        User dbUser = userMapper.selectById(user.getId());
        session.setAttribute(Constants.SESSION_USER_KEY, dbUser);

        return convertToVO(dbUser);
    }

    /**
     * 转换为VO
     */
    private UserVO convertToVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
