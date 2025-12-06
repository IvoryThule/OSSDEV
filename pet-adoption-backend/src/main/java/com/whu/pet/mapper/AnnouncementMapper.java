package com.whu.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whu.pet.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告Mapper接口
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
