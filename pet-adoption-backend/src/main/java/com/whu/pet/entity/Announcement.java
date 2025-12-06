package com.whu.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
@TableName("announcement")
public class Announcement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    /**
     * 类型: 0-普通公告, 1-领养须知, 2-活动公告
     */
    private Integer type;

    /**
     * 是否置顶: 0-否, 1-是
     */
    private Integer isTop;

    /**
     * 状态: 0-草稿, 1-已发布
     */
    private Integer status;

    private Long publisherId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private String publisherName;
}
