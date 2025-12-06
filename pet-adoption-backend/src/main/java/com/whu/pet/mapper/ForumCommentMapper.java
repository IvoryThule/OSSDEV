package com.whu.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whu.pet.entity.ForumComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ForumCommentMapper extends BaseMapper<ForumComment> {
    
    @Select("SELECT c.*, u.nickname, u.avatar, ru.nickname as reply_to_nickname " +
            "FROM forum_comment c " +
            "LEFT JOIN sys_user u ON c.user_id = u.id " +
            "LEFT JOIN sys_user ru ON c.reply_to_user_id = ru.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.create_time ASC")
    List<ForumComment> selectCommentsByPostId(@Param("postId") Long postId);
}
