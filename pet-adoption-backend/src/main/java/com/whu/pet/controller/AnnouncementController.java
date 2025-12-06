package com.whu.pet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.whu.pet.common.Result;
import com.whu.pet.entity.Announcement;
import com.whu.pet.security.JwtUtils;
import com.whu.pet.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 获取公告列表（公开-已发布的）
     */
    @GetMapping
    public Result<IPage<Announcement>> getPublishedAnnouncements(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Announcement> page = announcementService.getPublishedAnnouncements(pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取所有公告（管理员）
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<Announcement>> getAllAnnouncements(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        IPage<Announcement> page = announcementService.getAnnouncementPage(pageNum, pageSize, type, status);
        return Result.success(page);
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        return Result.success(announcement);
    }

    /**
     * 发布公告
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> addAnnouncement(@RequestBody Announcement announcement,
                                        @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        announcement.setPublisherId(userId);

        if (announcementService.save(announcement)) {
            return Result.success();
        }
        return Result.error("发布失败");
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        if (announcementService.updateById(announcement)) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        if (announcementService.removeById(id)) {
            return Result.success();
        }
        return Result.error("删除失败");
    }
}
