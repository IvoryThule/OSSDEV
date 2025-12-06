package com.whu.pet.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whu.pet.entity.User;
import com.whu.pet.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务类
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据用户名查询用户
     */
    public User getByUsername(String username) {
        return getOne(new QueryWrapper<User>().eq("username", username));
    }

    /**
     * 用户注册
     */
    public boolean register(User user) {
        // 检查用户名是否存在
        if (getByUsername(user.getUsername()) != null) {
            return false;
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 默认角色为普通用户
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        user.setStatus(1);
        return save(user);
    }

    /**
     * 验证密码
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 分页查询用户
     */
    public IPage<User> getUserPage(int pageNum, int pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("username", keyword)
                    .or().like("nickname", keyword)
                    .or().like("phone", keyword);
        }
        wrapper.orderByDesc("create_time");
        return page(page, wrapper);
    }

    /**
     * 更新用户状态
     */
    public boolean updateStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        return updateById(user);
    }

    /**
     * 修改密码
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null || !checkPassword(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }
}
