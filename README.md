# 🐾 宠物领养管理系统 (Pet Adoption System)

## 项目简介

本项目是武汉大学计算机学院《开源软件生态与开发技术》课程期末考核项目，采用B/S架构开发的宠物领养管理平台。系统旨在为流浪动物救助站和爱心人士提供一个便捷的宠物信息发布与领养申请平台。

## 技术栈

### 后端
- **Spring Boot 2.7.x** - 应用框架
- **Spring Security + JWT** - 认证授权
- **MyBatis-Plus** - ORM框架
- **MySQL 8.0** - 数据库

### 前端
- **Vue 3** - 前端框架
- **Element Plus** - UI组件库
- **Axios** - HTTP请求
- **Vue Router** - 路由

## 系统功能

### 👤 用户模块
- 注册/登录/登出
- 个人信息管理
- 角色：管理员、救助站、领养人

### 🐕 宠物管理
- 宠物信息发布（品种、年龄、健康状况、照片）
- 宠物状态管理（待领养、已领养、已下架）
- 宠物搜索筛选

### 📋 领养申请
- 提交领养申请
- 申请审核（通过/拒绝）
- 申请状态跟踪

### 📢 公告管理
- 发布领养须知
- 活动公告

## 快速开始

### 后端
```bash
cd pet-adoption-backend
mvn spring-boot:run
```

### 前端
```bash
cd pet-adoption-frontend
npm install
npm run dev
```
