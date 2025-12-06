package com.whu.pet.controller;

import com.whu.pet.common.Result;
import com.whu.pet.dto.LoginRequest;
import com.whu.pet.dto.RegisterRequest;
import com.whu.pet.entity.User;
import com.whu.pet.security.JwtUtils;
import com.whu.pet.service.UserService;
import com.whu.pet.vo.LoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginRequest request) {
        User user = userService.getByUsername(request.getUsername());

        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        if (!userService.checkPassword(request.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setRole(user.getRole());
        loginVO.setToken(token);

        return Result.success("登录成功", loginVO);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);

        if (userService.register(user)) {
            return Result.success("注册成功", null);
        } else {
            return Result.error("用户名已存在");
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null); // 隐藏密码
        }
        return Result.success(user);
    }
}
