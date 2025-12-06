package com.whu.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whu.pet.entity.ForumComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ForumCommentMapper extends BaseMapper<ForumComment> {
    
    @Select("SELECT c.*, u.nickname as userName, u.avatar as userAvatar, ru.nickname as reply_to_nickname " +
            "FROM forum_comment c " +
            "LEFT JOIN sys_user u ON c.user_id = u.id " +
            "LEFT JOIN sys_user ru ON c.reply_to_user_id = ru.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.create_time ASC")
    List<ForumComment> selectCommentsByPostId(@Param("postId") Long postId);

    @Select("<script>" +
            "SELECT c.*, u.nickname as userName, u.avatar as userAvatar, p.title as postTitle " +
            "FROM forum_comment c " +
            "LEFT JOIN sys_user u ON c.user_id = u.id " +
            "LEFT JOIN forum_post p ON c.post_id = p.id " +
            "WHERE 1=1 " +
            "<if test='postId != null'>AND c.post_id = #{postId}</if> " +
            "<if test='userId != null'>AND c.user_id = #{userId}</if> " +
            "<if test='keyword != null and keyword != \"\"'>AND c.content LIKE CONCAT('%', #{keyword}, '%')</if> " +
            "ORDER BY c.create_time DESC" +
            "</script>")
    IPage<ForumComment> selectCommentPageWithUser(Page<ForumComment> page, 
                                                   @Param("postId") Long postId, 
                                                   @Param("userId") Long userId, 
                                                   @Param("keyword") String keyword);
}
