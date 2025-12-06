package com.whu.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whu.pet.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPost> {
    
    @Select("SELECT p.*, u.nickname, u.avatar FROM forum_post p " +
            "LEFT JOIN sys_user u ON p.user_id = u.id " +
            "WHERE p.title LIKE CONCAT('%', #{keyword}, '%') OR p.content LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY p.is_top DESC, p.create_time DESC")
    IPage<ForumPost> selectPostPage(Page<ForumPost> page, @Param("keyword") String keyword);

    @Select("SELECT p.*, u.nickname, u.avatar FROM forum_post p " +
            "LEFT JOIN sys_user u ON p.user_id = u.id " +
            "ORDER BY p.is_top DESC, p.create_time DESC")
    IPage<ForumPost> selectPostPageNoKeyword(Page<ForumPost> page);
    
    @Select("SELECT p.*, u.nickname, u.avatar FROM forum_post p " +
            "LEFT JOIN sys_user u ON p.user_id = u.id " +
            "WHERE p.id = #{id}")
    ForumPost selectPostDetail(@Param("id") Long id);
}
