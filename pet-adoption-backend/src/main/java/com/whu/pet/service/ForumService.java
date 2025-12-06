package com.whu.pet.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whu.pet.entity.ForumComment;
import com.whu.pet.entity.ForumLike;
import com.whu.pet.entity.ForumPost;
import com.whu.pet.mapper.ForumCommentMapper;
import com.whu.pet.mapper.ForumLikeMapper;
import com.whu.pet.mapper.ForumPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ForumService extends ServiceImpl<ForumPostMapper, ForumPost> {

    @Autowired
    private ForumCommentMapper commentMapper;

    @Autowired
    private ForumLikeMapper likeMapper;

    public IPage<ForumPost> getPostPage(int pageNum, int pageSize, String keyword) {
        Page<ForumPost> page = new Page<>(pageNum, pageSize);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return baseMapper.selectPostPage(page, keyword);
        }
        return baseMapper.selectPostPageNoKeyword(page);
    }

    public ForumPost getPostDetail(Long id) {
        ForumPost post = baseMapper.selectPostDetail(id);
        if (post != null) {
            // 增加浏览量
            post.setViewCount(post.getViewCount() + 1);
            baseMapper.updateById(post);
        }
        return post;
    }

    public List<ForumComment> getComments(Long postId) {
        return commentMapper.selectCommentsByPostId(postId);
    }

    @Transactional
    public boolean addComment(ForumComment comment) {
        int rows = commentMapper.insert(comment);
        if (rows > 0) {
            // 更新帖子评论数
            ForumPost post = baseMapper.selectById(comment.getPostId());
            if (post != null) {
                post.setCommentCount(post.getCommentCount() + 1);
                baseMapper.updateById(post);
            }
            return true;
        }
        return false;
    }

    @Transactional
    public boolean toggleLike(Long userId, Long targetId, Integer targetType, Integer type) {
        // 检查是否已经操作过
        QueryWrapper<ForumLike> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("target_id", targetId)
               .eq("target_type", targetType);
        
        ForumLike existing = likeMapper.selectOne(wrapper);
        
        if (existing != null) {
            // 如果已经存在，且类型相同，则取消（删除）
            if (existing.getType().equals(type)) {
                likeMapper.deleteById(existing.getId());
                updateLikeCount(targetId, targetType, type, -1);
                return true; // 取消成功
            } else {
                // 如果类型不同（例如之前点赞，现在点踩），则更新
                // 先减少之前的计数
                updateLikeCount(targetId, targetType, existing.getType(), -1);
                // 更新记录
                existing.setType(type);
                likeMapper.updateById(existing);
                // 增加新的计数
                updateLikeCount(targetId, targetType, type, 1);
                return true; // 切换成功
            }
        } else {
            // 新增记录
            ForumLike like = new ForumLike();
            like.setUserId(userId);
            like.setTargetId(targetId);
            like.setTargetType(targetType);
            like.setType(type);
            likeMapper.insert(like);
            updateLikeCount(targetId, targetType, type, 1);
            return true; // 操作成功
        }
    }

    private void updateLikeCount(Long targetId, Integer targetType, Integer type, int delta) {
        if (targetType == 1) { // 帖子
            ForumPost post = baseMapper.selectById(targetId);
            if (post != null) {
                if (type == 1) post.setLikeCount(post.getLikeCount() + delta);
                else post.setDislikeCount(post.getDislikeCount() + delta);
                baseMapper.updateById(post);
            }
        } else { // 评论
            ForumComment comment = commentMapper.selectById(targetId);
            if (comment != null) {
                if (type == 1) comment.setLikeCount(comment.getLikeCount() + delta);
                else comment.setDislikeCount(comment.getDislikeCount() + delta);
                commentMapper.updateById(comment);
            }
        }
    }

    /**
     * 分页获取所有评论（管理员用）
     */
    public IPage<ForumComment> getCommentPage(int pageNum, int pageSize, Long postId, Long userId, String keyword) {
        Page<ForumComment> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ForumComment> wrapper = new QueryWrapper<>();
        if (postId != null) {
            wrapper.eq("post_id", postId);
        }
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("content", keyword);
        }
        wrapper.orderByDesc("create_time");
        return commentMapper.selectCommentPageWithUser(page, postId, userId, keyword);
    }

    /**
     * 删除评论
     */
    @Transactional
    public boolean deleteComment(Long id, Long userId, boolean isAdmin) {
        ForumComment comment = commentMapper.selectById(id);
        if (comment == null) return false;
        
        // 只有管理员或评论作者可以删除
        if (!isAdmin && !comment.getUserId().equals(userId)) {
            return false;
        }
        
        int rows = commentMapper.deleteById(id);
        if (rows > 0) {
            // 更新帖子评论数
            ForumPost post = baseMapper.selectById(comment.getPostId());
            if (post != null && post.getCommentCount() > 0) {
                post.setCommentCount(post.getCommentCount() - 1);
                baseMapper.updateById(post);
            }
            return true;
        }
        return false;
    }
}
