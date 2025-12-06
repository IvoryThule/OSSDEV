
USE pet_adoption;

-- 修改领养申请表，增加双重审核状态
ALTER TABLE `adoption_application` 
ADD COLUMN `admin_status` TINYINT DEFAULT 0 COMMENT '管理员审核状态: 0-待审核, 1-已通过, 2-已拒绝',
ADD COLUMN `publisher_status` TINYINT DEFAULT 0 COMMENT '发布者审核状态: 0-待审核, 1-已通过, 2-已拒绝';

-- 论坛帖子表
CREATE TABLE `forum_post` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `user_id` BIGINT NOT NULL COMMENT '发帖人ID',
    `view_count` INT DEFAULT 0 COMMENT '浏览量',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `dislike_count` INT DEFAULT 0 COMMENT '点踩数',
    `comment_count` INT DEFAULT 0 COMMENT '评论数',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- 论坛评论表
CREATE TABLE `forum_comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '评论人ID',
    `content` TEXT NOT NULL COMMENT '内容',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID',
    `reply_to_user_id` BIGINT DEFAULT NULL COMMENT '回复给谁',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `dislike_count` INT DEFAULT 0 COMMENT '点踩数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`post_id`) REFERENCES `forum_post`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛评论表';

-- 点赞/点踩记录表
CREATE TABLE `forum_like` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `target_id` BIGINT NOT NULL COMMENT '帖子ID或评论ID',
    `target_type` TINYINT NOT NULL COMMENT '1-帖子, 2-评论',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` TINYINT NOT NULL COMMENT '1-点赞, 2-点踩',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞点踩记录表';
