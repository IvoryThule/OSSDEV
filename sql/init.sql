-- 宠物领养管理系统数据库初始化脚本
-- 创建数据库
DROP DATABASE IF EXISTS pet_adoption;
CREATE DATABASE pet_adoption DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pet_adoption;

-- 用户表
CREATE TABLE `sys_user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN-管理员, SHELTER-救助站, USER-普通用户',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 宠物分类表
CREATE TABLE `pet_category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(255) COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物分类表';

-- 宠物信息表
CREATE TABLE `pet` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '宠物ID',
    `name` VARCHAR(50) NOT NULL COMMENT '宠物名字',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `breed` VARCHAR(50) COMMENT '品种',
    `age` VARCHAR(20) COMMENT '年龄',
    `gender` TINYINT COMMENT '性别: 0-未知, 1-公, 2-母',
    `weight` DECIMAL(5,2) COMMENT '体重(kg)',
    `color` VARCHAR(30) COMMENT '毛色',
    `health_status` VARCHAR(100) COMMENT '健康状况',
    `is_vaccinated` TINYINT DEFAULT 0 COMMENT '是否已疫苗: 0-否, 1-是',
    `is_sterilized` TINYINT DEFAULT 0 COMMENT '是否已绝育: 0-否, 1-是',
    `description` TEXT COMMENT '详细描述',
    `image_url` VARCHAR(500) COMMENT '图片URL，多个用逗号分隔',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待领养, 1-申请中, 2-已领养, 3-已下架',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`category_id`) REFERENCES `pet_category`(`id`),
    FOREIGN KEY (`publisher_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物信息表';

-- 领养申请表
CREATE TABLE `adoption_application` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
    `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
    `reason` TEXT COMMENT '领养理由',
    `living_condition` VARCHAR(255) COMMENT '居住条件',
    `experience` VARCHAR(255) COMMENT '养宠经验',
    `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `contact_address` VARCHAR(255) COMMENT '联系地址',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝, 3-已取消',
    `reject_reason` VARCHAR(255) COMMENT '拒绝原因',
    `review_time` DATETIME COMMENT '审核时间',
    `reviewer_id` BIGINT COMMENT '审核人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`pet_id`) REFERENCES `pet`(`id`),
    FOREIGN KEY (`applicant_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养申请表';

-- 公告表
CREATE TABLE `announcement` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `type` TINYINT DEFAULT 0 COMMENT '类型: 0-普通公告, 1-领养须知, 2-活动公告',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`publisher_id`) REFERENCES `sys_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 初始化数据
-- 管理员账号 (密码: admin123)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2b$12$RqB/U1imei8wxCio.SPqgek24Wgc0klRYMpoj3Syga0N6VzneQ/Aa', '系统管理员', 'ADMIN', 1),
('shelter01', '$2b$12$RqB/U1imei8wxCio.SPqgek24Wgc0klRYMpoj3Syga0N6VzneQ/Aa', '爱心救助站', 'SHELTER', 1),
('user01', '$2b$12$RqB/U1imei8wxCio.SPqgek24Wgc0klRYMpoj3Syga0N6VzneQ/Aa', '爱宠人士', 'USER', 1);

-- 宠物分类
INSERT INTO `pet_category` (`name`, `description`) VALUES
('猫咪', '各种品种的猫'),
('狗狗', '各种品种的狗'),
('兔子', '兔子类宠物'),
('仓鼠', '仓鼠类小宠'),
('其他', '其他类型宠物');

-- 示例宠物数据
INSERT INTO `pet` (`name`, `category_id`, `breed`, `age`, `gender`, `weight`, `color`, `health_status`, `is_vaccinated`, `is_sterilized`, `description`, `image_url`, `status`, `publisher_id`) VALUES
('小橘', 1, '中华田园猫', '1岁', 1, 4.5, '橘色', '健康', 1, 1, '性格温顺，亲人，已完成疫苗和绝育，适合家庭领养。', '/pets/hajimi.jpg', 0, 2),
('豆豆', 2, '中华田园犬', '2岁', 1, 12.0, '黄色', '健康', 1, 0, '活泼好动，喜欢户外运动，需要有足够活动空间的家庭。', '/pets/doudou.jpg', 0, 2),
('雪球', 3, '垂耳兔', '6个月', 2, 1.5, '白色', '健康', 1, 0, '安静可爱，适合喜欢安静小动物的朋友。', '/pets/xueqiu.jpg', 0, 2);

-- 示例公告
INSERT INTO `announcement` (`title`, `content`, `type`, `is_top`, `status`, `publisher_id`) VALUES
('领养须知', '1. 领养前请确保有稳定的居住环境\n2. 需要有足够的时间和精力照顾宠物\n3. 领养后需定期回访\n4. 禁止转卖或遗弃', 1, 1, 1, 1),
('本周领养活动', '本周六下午2点在市民广场举办领养日活动，欢迎大家参加！', 2, 0, 1, 1);
