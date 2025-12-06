package com.whu.pet.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whu.pet.entity.Announcement;
import com.whu.pet.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;

/**
 * 公告服务类
 */
@Service
public class AnnouncementService extends ServiceImpl<AnnouncementMapper, Announcement> {

    /**
     * 分页查询公告列表
     */
    public IPage<Announcement> getAnnouncementPage(int pageNum, int pageSize, Integer type, Integer status) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        if (type != null) {
            wrapper.eq("type", type);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("is_top", "create_time");
        return page(page, wrapper);
    }

    /**
     * 获取已发布的公告（公开访问）
     */
    public IPage<Announcement> getPublishedAnnouncements(int pageNum, int pageSize) {
        return getAnnouncementPage(pageNum, pageSize, null, 1);
    }
}
