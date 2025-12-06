package com.whu.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("forum_comment")
public class ForumComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private Long parentId;
    private Long replyToUserId;
    private Integer likeCount;
    private Integer dislikeCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private String avatar;
    @TableField(exist = false)
    private String replyToNickname;
}
