# BlogSakura——兼有图库与空间管理的综合博客平台

## 项目介绍

基于SpringBoot + Mybatis-Flex + Redis + RabbitMQ +
WebSocket的综合博客平台，除了提供发表、查看文章等基本博客功能以外，还提供公共图库、私有空间和团队空间模块。管理员能同时管理公共图库、用户和空间等模块，用户不仅能进行图片上传私有空间，还可以搭建团队空间并邀请其他用户共享和实时协同编辑图片。

## 项目架构图（待定）

## 技术选型

### 后端

- Spring Boot3.6：搭建后端系统。
- MyBatis-Flex：MyBatis的增强版本，方便快速实现数据库的基本操作。
- RabbitMQ：消息队列，实现异步解耦、削峰等作用。
- MySQL：数据库持久化。
- Redis：用作缓存，存储阅读数、热门文章等高频数据。
- Caffeine：本地缓存优化。
- 腾讯云COS：对象存储。
- WebSocket: 一种基于TCP协议的全双工通信协议，实现实时协同编辑图片。
- Disruptor: 高性能无锁环形队列。

### 前端

- React 19和TypeScipt: 搭建前端系统。
- Ant Design: 组件库
- Axios：请求库
- react-image-crop: 图像编辑库
- OpenAPI: 前端代码生成

## 项目功能模块（待定）

### 前台

### 后台
