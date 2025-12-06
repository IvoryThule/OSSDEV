package com.whu.pet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.whu.pet.common.Result;
import com.whu.pet.entity.ForumComment;
import com.whu.pet.entity.ForumPost;
import com.whu.pet.security.JwtUtils;
import com.whu.pet.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/posts")
    public Result<IPage<ForumPost>> getPostList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(forumService.getPostPage(pageNum, pageSize, keyword));
    }

    @GetMapping("/posts/{id}")
    public Result<ForumPost> getPostDetail(@PathVariable Long id) {
        return Result.success(forumService.getPostDetail(id));
    }

    @PostMapping("/posts")
    public Result<Void> createPost(@RequestBody ForumPost post, @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        post.setUserId(userId);
        if (forumService.save(post)) {
            return Result.success();
        }
        return Result.error("发布失败");
    }

    @GetMapping("/posts/{id}/comments")
    public Result<List<ForumComment>> getComments(@PathVariable Long id) {
        return Result.success(forumService.getComments(id));
    }

    @PostMapping("/comments")
    public Result<Void> addComment(@RequestBody ForumComment comment, @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        comment.setUserId(userId);
        if (forumService.addComment(comment)) {
            return Result.success();
        }
        return Result.error("评论失败");
    }

    @PostMapping("/like")
    public Result<Void> toggleLike(@RequestParam Long targetId, 
                                   @RequestParam Integer targetType, 
                                   @RequestParam Integer type,
                                   @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (forumService.toggleLike(userId, targetId, targetType, type)) {
            return Result.success();
        }
        return Result.error("操作失败");
    }
}
