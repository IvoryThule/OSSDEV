package com.whu.pet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.whu.pet.common.Result;
import com.whu.pet.entity.User;
import com.whu.pet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表（管理员）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        IPage<User> page = userService.getUserPage(pageNum, pageSize, keyword);
        // 隐藏密码
        page.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(page);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserDetail(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        user.setPassword(null); // 不允许通过此接口修改密码
        if (userService.updateById(user)) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    /**
     * 更新用户状态（管理员）
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        if (userService.updateStatus(id, status)) {
            return Result.success();
        }
        return Result.error("操作失败");
    }

    /**
     * 删除用户（管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        if (userService.removeById(id)) {
            return Result.success();
        }
        return Result.error("删除失败");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(
            @RequestParam Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        if (userService.changePassword(userId, oldPassword, newPassword)) {
            return Result.success("密码修改成功", null);
        }
        return Result.error("原密码错误");
    }
}
