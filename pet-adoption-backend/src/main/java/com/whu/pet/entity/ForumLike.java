package com.whu.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("forum_like")
public class ForumLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long targetId;
    /**
     * 1-帖子, 2-评论
     */
    private Integer targetType;
    private Long userId;
    /**
     * 1-点赞, 2-点踩
     */
    private Integer type;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
