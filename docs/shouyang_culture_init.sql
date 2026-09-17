-- ============================================================
-- 寿阳文旅云 - 数据库初始化脚本
-- 数据库：shouyang_culture
-- 字符集：utf8mb4
-- 适用：MySQL 8.0+
-- ============================================================

-- 强制设置客户端连接字符集为 utf8mb4，避免中文长度误判
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `shouyang_culture`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `shouyang_culture`;

-- ============================================================
-- 1. 用户表 sys_user
-- ============================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname`    VARCHAR(100) DEFAULT NULL COMMENT '昵称',
  `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `gender`      TINYINT      DEFAULT 0 COMMENT '性别 0未知 1男 2女',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ============================================================
-- 2. 管理员表 sys_admin
-- ============================================================
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname`    VARCHAR(100) DEFAULT NULL COMMENT '昵称',
  `role`        VARCHAR(20)  DEFAULT 'admin' COMMENT '角色 admin普通管理员 super超级管理员',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理员表';

-- ============================================================
-- 3. 轮播图表 banner
-- ============================================================
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title`       VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `image`       VARCHAR(255) NOT NULL COMMENT '图片URL',
  `link_url`    VARCHAR(255) DEFAULT NULL COMMENT '跳转链接',
  `sort`        INT          DEFAULT 0 COMMENT '排序（越小越靠前）',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态 0禁用 1启用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='轮播图表';

-- ============================================================
-- 4. 资讯分类表 news_category
-- ============================================================
DROP TABLE IF EXISTS `news_category`;
CREATE TABLE `news_category` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name`        VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort`        INT         DEFAULT 0 COMMENT '排序',
  `status`      TINYINT     DEFAULT 1 COMMENT '状态 0禁用 1启用',
  `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资讯分类表';

-- ============================================================
-- 5. 资讯表 news
-- ============================================================
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '资讯ID',
  `title`        VARCHAR(200)  NOT NULL COMMENT '标题',
  `category_id`  BIGINT        DEFAULT NULL COMMENT '分类ID',
  `cover_image`  VARCHAR(255)  DEFAULT NULL COMMENT '封面图URL',
  `summary`      VARCHAR(500)  DEFAULT NULL COMMENT '摘要',
  `content`      LONGTEXT      COMMENT '正文（富文本HTML）',
  `author`       VARCHAR(50)   DEFAULT NULL COMMENT '作者/来源',
  `view_count`   INT           DEFAULT 0 COMMENT '浏览量',
  `is_top`       TINYINT       DEFAULT 0 COMMENT '是否置顶 0否 1是',
  `status`       TINYINT       DEFAULT 1 COMMENT '状态 0草稿 1发布',
  `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_top` (`is_top`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资讯表';

-- ============================================================
-- 6. 票务表 ticket
-- ============================================================
DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket` (
  `id`           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '票务ID',
  `name`         VARCHAR(100)   NOT NULL COMMENT '票名',
  `cover_image`  VARCHAR(255)   DEFAULT NULL COMMENT '封面图',
  `venue`        VARCHAR(100)   DEFAULT NULL COMMENT '场馆/地点',
  `price`        DECIMAL(10,2)  DEFAULT 0.00 COMMENT '价格（0=免费）',
  `start_time`   DATETIME       DEFAULT NULL COMMENT '开始时间',
  `end_time`     DATETIME       DEFAULT NULL COMMENT '结束时间',
  `total_count`  INT            DEFAULT 0 COMMENT '总票数',
  `remain_count` INT            DEFAULT 0 COMMENT '剩余票数',
  `description`  TEXT           COMMENT '详情描述',
  `status`       TINYINT        DEFAULT 1 COMMENT '状态 0下架 1上架',
  `create_time`  DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='票务表';

-- ============================================================
-- 7. 场馆表 venue
-- ============================================================
DROP TABLE IF EXISTS `venue`;
CREATE TABLE `venue` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '场馆ID',
  `name`         VARCHAR(100) NOT NULL COMMENT '场馆名称',
  `cover_image`  VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `address`      VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `open_time`    VARCHAR(100) DEFAULT NULL COMMENT '开放时间',
  `contact`      VARCHAR(50)  DEFAULT NULL COMMENT '联系电话',
  `description`  TEXT         COMMENT '场馆介绍',
  `status`       TINYINT      DEFAULT 1 COMMENT '状态 0关闭 1开放',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='场馆表';

-- ============================================================
-- 8. 活动表 activity
-- ============================================================
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title`           VARCHAR(200) NOT NULL COMMENT '活动标题',
  `cover_image`     VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `venue`           VARCHAR(100) DEFAULT NULL COMMENT '活动地点',
  `start_time`      DATETIME     DEFAULT NULL COMMENT '开始时间',
  `end_time`        DATETIME     DEFAULT NULL COMMENT '结束时间',
  `signup_deadline` DATETIME     DEFAULT NULL COMMENT '报名截止时间',
  `max_people`      INT          DEFAULT 0 COMMENT '最大人数（0=不限）',
  `signup_count`    INT          DEFAULT 0 COMMENT '已报名人数',
  `description`     LONGTEXT     COMMENT '活动详情',
  `status`          TINYINT      DEFAULT 1 COMMENT '状态 0未开始 1进行中 2已结束',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='活动表';

-- ============================================================
-- 9. 活动报名表 activity_register
-- ============================================================
DROP TABLE IF EXISTS `activity_register`;
CREATE TABLE `activity_register` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '报名记录ID',
  `activity_id`  BIGINT       NOT NULL COMMENT '活动ID',
  `user_id`      BIGINT       NOT NULL COMMENT '用户ID',
  `name`         VARCHAR(50)  DEFAULT NULL COMMENT '报名人姓名',
  `phone`        VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `remark`       VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `status`       TINYINT      DEFAULT 1 COMMENT '状态 0已取消 1已报名',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='活动报名表';

-- ============================================================
-- 10. 数字展馆表 pavilion
-- ============================================================
DROP TABLE IF EXISTS `pavilion`;
CREATE TABLE `pavilion` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '展馆ID',
  `name`         VARCHAR(100) NOT NULL COMMENT '展馆名称',
  `cover_image`  VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `description`  TEXT         COMMENT '展馆简介',
  `content`      LONGTEXT     COMMENT '详细内容（富文本）',
  `view_count`   INT          DEFAULT 0 COMMENT '浏览量',
  `sort`         INT          DEFAULT 0 COMMENT '排序',
  `status`       TINYINT      DEFAULT 1 COMMENT '状态 0下架 1上架',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字展馆表';

-- ============================================================
-- 11. 非遗文化表 heritage
-- ============================================================
DROP TABLE IF EXISTS `heritage`;
CREATE TABLE `heritage` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '非遗ID',
  `name`         VARCHAR(100) NOT NULL COMMENT '非遗名称',
  `cover_image`  VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `level`        VARCHAR(20)  DEFAULT NULL COMMENT '级别（国家级/省级/市级/县级）',
  `category`     VARCHAR(50)  DEFAULT NULL COMMENT '类别（传统技艺/民俗/传统音乐等）',
  `inheritor`    VARCHAR(50)  DEFAULT NULL COMMENT '传承人',
  `description`  TEXT         COMMENT '简介',
  `content`      LONGTEXT     COMMENT '详细内容',
  `view_count`   INT          DEFAULT 0 COMMENT '浏览量',
  `status`       TINYINT      DEFAULT 1 COMMENT '状态 0下架 1上架',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_level` (`level`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='非遗文化表';

-- ============================================================
-- 12. 文创商品表 product
-- ============================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id`             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name`           VARCHAR(100)   NOT NULL COMMENT '商品名称',
  `cover_image`    VARCHAR(255)   DEFAULT NULL COMMENT '封面图',
  `images`         TEXT           COMMENT '多图（JSON数组）',
  `price`          DECIMAL(10,2)  DEFAULT 0.00 COMMENT '价格',
  `original_price` DECIMAL(10,2)  DEFAULT 0.00 COMMENT '原价',
  `stock`          INT            DEFAULT 0 COMMENT '库存',
  `description`    TEXT           COMMENT '商品描述',
  `content`        LONGTEXT       COMMENT '详情',
  `view_count`     INT            DEFAULT 0 COMMENT '浏览量',
  `status`         TINYINT        DEFAULT 1 COMMENT '状态 0下架 1上架',
  `create_time`    DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文创商品表';

-- ============================================================
-- 13. 景点表 scenic_spot
-- ============================================================
DROP TABLE IF EXISTS `scenic_spot`;
CREATE TABLE `scenic_spot` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '景点ID',
  `name`         VARCHAR(100) NOT NULL COMMENT '景点名称',
  `cover_image`  VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `level`        VARCHAR(20)  DEFAULT NULL COMMENT '等级（5A/4A/3A/无）',
  `address`      VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `open_time`    VARCHAR(100) DEFAULT NULL COMMENT '开放时间',
  `ticket_price` VARCHAR(50)  DEFAULT NULL COMMENT '门票价格（文字描述）',
  `phone`        VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `description`  TEXT         COMMENT '简介',
  `content`      LONGTEXT     COMMENT '详细介绍',
  `view_count`   INT          DEFAULT 0 COMMENT '浏览量',
  `sort`         INT          DEFAULT 0 COMMENT '排序',
  `status`       TINYINT      DEFAULT 1 COMMENT '状态 0下架 1上架',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='景点表';

-- ============================================================
-- 14. 美食表 food
-- ============================================================
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '美食ID',
  `name`         VARCHAR(100) NOT NULL COMMENT '美食名称',
  `cover_image`  VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `category`     VARCHAR(50)  DEFAULT NULL COMMENT '类型（主食/小吃/菜肴等）',
  `description`  TEXT         COMMENT '简介',
  `content`      LONGTEXT     COMMENT '详细介绍（可含做法）',
  `view_count`   INT          DEFAULT 0 COMMENT '浏览量',
  `status`       TINYINT      DEFAULT 1 COMMENT '状态 0下架 1上架',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='美食表';

-- ============================================================
-- 15. 酒店表 hotel
-- ============================================================
DROP TABLE IF EXISTS `hotel`;
CREATE TABLE `hotel` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '酒店ID',
  `name`         VARCHAR(100) NOT NULL COMMENT '酒店名称',
  `cover_image`  VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `star`         INT          DEFAULT 0 COMMENT '星级（1-5）',
  `address`      VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `phone`        VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `price_range`  VARCHAR(50)  DEFAULT NULL COMMENT '价格区间',
  `description`  TEXT         COMMENT '简介',
  `content`      LONGTEXT     COMMENT '详细介绍',
  `view_count`   INT          DEFAULT 0 COMMENT '浏览量',
  `status`       TINYINT      DEFAULT 1 COMMENT '状态 0下架 1上架',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_star` (`star`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='酒店表';

-- ============================================================
-- 16. 旅游攻略表 travel_guide
-- ============================================================
DROP TABLE IF EXISTS `travel_guide`;
CREATE TABLE `travel_guide` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '攻略ID',
  `title`        VARCHAR(200) NOT NULL COMMENT '攻略标题',
  `cover_image`  VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `author`       VARCHAR(50)  DEFAULT NULL COMMENT '作者/来源',
  `days`         VARCHAR(20)  DEFAULT NULL COMMENT '游玩天数',
  `route`        VARCHAR(200) DEFAULT NULL COMMENT '路线概要',
  `description`  TEXT         COMMENT '简介',
  `content`      LONGTEXT     COMMENT '详细攻略',
  `view_count`   INT          DEFAULT 0 COMMENT '浏览量',
  `status`       TINYINT      DEFAULT 1 COMMENT '状态 0下架 1上架',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='旅游攻略表';

-- ============================================================
-- 17. 文旅单位表 organization
-- ============================================================
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '单位ID',
  `name`         VARCHAR(100) NOT NULL COMMENT '单位名称',
  `cover_image`  VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `type`         VARCHAR(50)  DEFAULT NULL COMMENT '类型（文化馆/图书馆/分馆等）',
  `address`      VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `phone`        VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `description`  TEXT         COMMENT '简介',
  `content`      LONGTEXT     COMMENT '详细介绍',
  `sort`         INT          DEFAULT 0 COMMENT '排序',
  `status`       TINYINT      DEFAULT 1 COMMENT '状态 0下架 1上架',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文旅单位表';

-- ============================================================
-- 18. 收藏表 favorite
-- ============================================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id`      BIGINT      NOT NULL COMMENT '用户ID',
  `target_type`  VARCHAR(30) NOT NULL COMMENT '收藏类型（news/scenic/food/hotel/activity/product等）',
  `target_id`    BIGINT      NOT NULL COMMENT '目标ID',
  `create_time`  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收藏表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 超级管理员账号 admin / 123456
-- BCrypt加密后的密码：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIu
INSERT INTO `sys_admin` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIu', '超级管理员', 'super', 1);

-- 测试用户 test / 123456
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `phone`, `gender`, `status`) VALUES
('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIu', '测试用户', '13800138000', 1, 1);

-- 资讯分类（6个）
INSERT INTO `news_category` (`name`, `sort`, `status`) VALUES
('文旅资讯', 1, 1),
('非遗文化', 2, 1),
('活动预告', 3, 1),
('通知公告', 4, 1),
('文化惠民', 5, 1),
('旅游美文', 6, 1);

-- 轮播图（3张，使用占位图）
INSERT INTO `banner` (`title`, `image`, `link_url`, `sort`, `status`) VALUES
('寿阳文旅云欢迎您', 'https://picsum.photos/1200/400?random=1', '/news', 1, 1),
('炫彩非遗·魅力寿阳', 'https://picsum.photos/1200/400?random=2', '/culture/heritage', 2, 1),
('怡然见晋中 休闲寿阳游', 'https://picsum.photos/1200/400?random=3', '/travel/scenic', 3, 1);

-- 示例资讯数据（5条）
INSERT INTO `news` (`title`, `category_id`, `cover_image`, `summary`, `content`, `author`, `view_count`, `is_top`, `status`, `create_time`) VALUES
('寿阳非遗传承人灵石之行：交流互鉴之光照亮非遗传承之路', 2, 'https://picsum.photos/400/250?random=11',
 '为了进一步弘扬中华优秀传统文化的深厚底蕴，推动寿阳县非物质文化遗产的保护工作与传承的科学发展，我县精心组建了一支由40名非遗传承人组成的学习队伍赴灵石交流学习。',
 '<p>为了进一步弘扬中华优秀传统文化的深厚底蕴，推动寿阳县非物质文化遗产的保护工作与传承的科学发展，11月2日，我县精心组建了一支由40名非遗传承人组成的学习队伍赴灵石进行交流学习。</p><p>本次交流活动旨在通过实地考察、现场观摩、座谈交流等形式，学习借鉴灵石县在非遗保护、传承、创新方面的先进经验和成功做法。</p><p>活动期间，交流队伍先后参观了灵石县非遗展示馆、传承所、文创产品开发基地等场所，与当地非遗传承人进行了深入交流，就非遗项目的保护传承、创新发展、市场化运作等话题展开了热烈讨论。</p>',
 '寿阳县文化馆', 128, 1, 1, '2024-11-05 09:00:00'),

('九九重阳 情暖夕阳——文化馆流动文化走进景尚敬老院', 5, 'https://picsum.photos/400/250?random=12',
 '九九重阳节，浓浓敬老情。为进一步弘扬传承尊老、爱老美德，增强敬老、助老意识，寿阳县文化馆组织文化惠民小分队走进景尚敬老院，为老人们带来了一场精彩纷呈的文化盛宴。',
 '<p>九九重阳节，浓浓敬老情。为进一步弘扬传承尊老、爱老美德，增强敬老、助老意识，10月11日上午，寿阳县文化馆组织文化惠民小分队、文化带头人为景尚敬老院的老人们带来了一场精彩纷呈的文化活动。</p><p>活动在欢快的舞蹈中拉开序幕，歌曲、戏曲、小品等节目轮番上演，老人们看得津津有味，不时报以热烈的掌声。文化馆的工作人员还为老人们送上了节日的祝福和慰问品。</p>',
 '寿阳县文化馆', 96, 0, 1, '2024-10-12 10:00:00'),

('【怡然见晋中 休闲寿阳游】"炫彩非遗·魅力寿阳"非遗进景区展演活动热闹非凡', 2, 'https://picsum.photos/400/250?random=13',
 '为庆祝中华人民共和国成立75周年，展示近年来我县非物质文化遗产保护成果，营造喜庆热烈的节日氛围，"炫彩非遗·魅力寿阳"非遗进景区展演活动在景区热闹开展。',
 '<p>为庆祝中华人民共和国成立75周年，展示近年来我县非物质文化遗产保护成果，营造喜庆热烈的节日氛围，进一步提高人民群众对非物质文化遗产的保护意识，传承弘扬中华优秀传统文化，"炫彩非遗·魅力寿阳"非遗进景区展演活动热闹开展。</p><p>活动现场，寿阳打铁虎、寿阳钩艺、寿阳传统布艺等非遗项目传承人现场展示技艺，精美的非遗作品吸引了众多游客驻足观看、拍照留念。</p>',
 '寿阳县文旅局', 215, 1, 1, '2024-10-03 08:30:00'),

('寿阳县庆祝新中国成立75周年群众文化活动启动', 1, 'https://picsum.photos/400/250?random=14',
 '金秋十月，硕果飘香。10月1日上午，"礼赞新中国 逐梦新时代"2024年寿阳县庆祝中华人民共和国成立75周年群众文化活动在平头镇黑水村启动。',
 '<p>金秋十月，硕果飘香。10月1日上午，"礼赞新中国 逐梦新时代"2024年寿阳县庆祝中华人民共和国成立75周年群众文化活动在平头镇黑水村启动。来自全县各乡镇的表演队伍齐聚一堂，用嘹亮的歌声和优美的舞姿表达对祖国的深情祝福。</p><p>本次群众文化活动将持续开展，涵盖广场舞展演、非遗展示、书画展览、电影放映等多种形式，丰富广大群众的精神文化生活。</p>',
 '寿阳县文旅局', 178, 0, 1, '2024-10-01 09:00:00'),

('关于举办寿阳县2024年全民阅读活动的通知', 4, 'https://picsum.photos/400/250?random=15',
 '为深入推进全民阅读，建设"书香寿阳"，经研究决定，在全县范围内开展2024年全民阅读系列活动。现将有关事项通知如下。',
 '<p>为深入推进全民阅读，建设"书香寿阳"，经研究决定，在全县范围内开展2024年全民阅读系列活动。现将有关事项通知如下：</p><p>一、活动主题：书香寿阳·全民阅读</p><p>二、活动时间：2024年11月至12月</p><p>三、活动内容：经典诵读、读书分享、绘本阅读、书画展览等</p><p>四、活动地点：寿阳县图书馆、各乡镇文化站、农家书屋</p><p>欢迎广大市民积极参与！</p>',
 '寿阳县图书馆', 89, 0, 1, '2024-09-28 14:00:00');

-- 示例景点（3个）
INSERT INTO `scenic_spot` (`name`, `cover_image`, `level`, `address`, `open_time`, `ticket_price`, `phone`, `description`, `content`, `view_count`, `sort`, `status`) VALUES
('祁寯藻故里景区', 'https://picsum.photos/400/300?random=21', '4A', '山西省晋中市寿阳县平舒村', '08:00-18:00', '免费', '0354-1234567',
 '祁寯藻故里景区位于寿阳县平舒村，是清代著名政治家、文学家、书法家祁寯藻的故乡。景区占地广阔，文化底蕴深厚。',
 '<p>祁寯藻（1793-1866），字叔颖，号淳甫，晚号观斋，山西寿阳人。清代著名政治家、文学家、书法家，曾历任道光、咸丰、同治三朝大学士，世称"三代帝师"。</p><p>祁寯藻故里景区位于寿阳县平舒村，景区内有祁寯藻故居、祁氏宗祠、书法碑廊、农耕文化展示区等景点。景区建筑风格古朴典雅，园林景色秀丽，是了解清代晋商文化和仕宦文化的重要场所。</p>',
 356, 1, 1),

('龙栖湖度假村', 'https://picsum.photos/400/300?random=22', '3A', '山西省晋中市寿阳县南燕竹镇', '全天开放', '免费', '0354-7654321',
 '龙栖湖度假村位于寿阳县南燕竹镇，是集休闲度假、水上娱乐、餐饮住宿于一体的综合性旅游景区。湖区风光秀丽，是夏日避暑的好去处。',
 '<p>龙栖湖度假村位于寿阳县南燕竹镇，依托龙栖湖水库而建。景区水域面积广阔，湖水清澈，周围群山环抱，林木葱郁，自然风光优美。</p><p>度假村内设有水上乐园、垂钓区、烧烤区、农家乐餐厅、度假别墅等设施，是家庭出游、朋友聚会、团队建设的理想场所。春季可赏花踏青，夏季可戏水避暑，秋季可采摘观景，冬季可赏雪滑冰，四季皆宜。</p>',
 289, 2, 1),

('下洲古村', 'https://picsum.photos/400/300?random=23', '', '山西省晋中市寿阳县宗艾镇下洲村', '全天开放', '免费', '',
 '下洲古村是中国传统村落，保存有大量明清时期的古建筑。古村依山而建，布局合理，石雕、砖雕、木雕精美，具有很高的历史和艺术价值。',
 '<p>下洲古村位于寿阳县宗艾镇，是中国传统村落和中国历史文化名村。古村始建于明代，距今已有600多年历史。村内保存有大量明清时期的古建筑，包括古民居、古庙宇、古戏台、古巷道等。</p><p>下洲古村的建筑风格独特，依山就势，错落有致。古民居的门楼、照壁、窗棂上雕刻精美，题材丰富，体现了晋商文化和北方民居建筑的融合。近年来，下洲古村积极发展乡村旅游，古村焕发出新的生机与活力。</p>',
 412, 3, 1);

-- 示例美食（3个）
INSERT INTO `food` (`name`, `cover_image`, `category`, `description`, `content`, `view_count`, `status`) VALUES
('天下第一挠', 'https://picsum.photos/400/300?random=31', '主食',
 '天下第一挠是寿阳特色面食，以玉米面为主料，搭配特制卤汁，口感筋道，味道香浓。',
 '<p>天下第一挠是寿阳地区的传统特色面食，以其独特的制作工艺和鲜美的口感而闻名。"挠"是一种用玉米面制成的面食，制作时将玉米面放入沸水中，用专用的挠勺不断搅拌，直至成糊状。</p><p>食用时，将挠盛入碗中，浇上用西红柿、豆腐、粉条等制作的卤汁，再加上蒜末、辣椒油等调料，搅拌均匀即可食用。口感筋道爽滑，味道香浓可口，是寿阳人餐桌上的家常美味。</p>',
 245, 1),

('炒不烂', 'https://picsum.photos/400/300?random=32', '主食',
 '炒不烂是寿阳传统小吃，用土豆丝和面粉蒸制后炒制而成，外酥里嫩，香气扑鼻。',
 '<p>炒不烂，又名"拨烂子"，是山西地区的传统特色小吃，寿阳炒不烂尤为出名。主要原料是土豆和面粉。</p><p>制作方法：将土豆擦成丝，用水冲洗去除淀粉，沥干后加入面粉、盐、五香粉等调料，用手抓拌均匀，使每根土豆丝都裹上面粉。然后放入蒸笼中大火蒸15分钟左右，取出晾凉。最后锅中放油，将蒸好的土豆丝放入锅中翻炒，炒至表面金黄酥脆即可。</p><p>炒不烂外酥里嫩，香气扑鼻，可直接食用，也可搭配蘸水或炒菜一起吃，是寿阳人喜爱的家常美食。</p>',
 198, 1),

('炸油糕', 'https://picsum.photos/400/300?random=33', '小吃',
 '炸油糕是寿阳传统节庆食品，用黄米面制成，包入豆馅或菜馅，油炸后外酥里嫩，香甜可口。',
 '<p>炸油糕是山西地区的传统特色食品，在寿阳尤为盛行，是逢年过节、婚丧嫁娶等重要场合必不可少的美食。</p><p>寿阳炸油糕以黄米面（黍子面）为主要原料。制作时，先将黄米面用温水和好，发酵后揉成面团，分成小剂子，包入红豆馅（甜糕）或胡萝卜豆腐馅（菜糕），捏成扁圆形，放入热油锅中炸至金黄色捞出。</p><p>刚出锅的炸油糕外皮酥脆，内里软糯，甜馅香甜可口，菜馅咸香入味，是寿阳人心中最具代表性的家乡味道之一。</p>',
 167, 1);

-- 示例酒店（3个）
INSERT INTO `hotel` (`name`, `cover_image`, `star`, `address`, `phone`, `price_range`, `description`, `content`, `view_count`, `status`) VALUES
('寿阳千缘主题酒店', 'https://picsum.photos/400/300?random=41', 3, '山西省晋中市寿阳县北外环加油站往北600米', '0354-1111111', '¥158-298',
 '寿阳千缘主题酒店是一家以主题文化为特色的精品酒店，客房装修风格多样，设施齐全，服务周到。',
 '<p>寿阳千缘主题酒店位于寿阳县北外环，交通便利，环境优美。酒店拥有各类主题客房，包括浪漫主题、商务主题、亲子主题等，满足不同客人的入住需求。</p><p>酒店客房装修精致，设施齐全，配备空调、电视、免费WiFi、独立卫浴、24小时热水等。酒店还设有餐厅、会议室、停车场等配套设施，为客人提供舒适便捷的入住体验。</p>',
 134, 1),

('寿阳安捷华悦酒店', 'https://picsum.photos/400/300?random=42', 3, '山西省晋中市寿阳县恒阳南路安捷玖號小区西门100号商铺', '0354-2222222', '¥168-328',
 '寿阳安捷华悦酒店是一家商务型酒店，地理位置优越，周边配套完善，是商务出行和旅游度假的理想选择。',
 '<p>寿阳安捷华悦酒店位于寿阳县恒阳南路，地处县城核心区域，周边商场、餐饮、娱乐设施齐全，出行购物十分便利。</p><p>酒店拥有各类客房，房间宽敞明亮，装修现代简约，设施设备完善。酒店提供免费早餐、免费WiFi、免费停车等服务，前台24小时为客人提供服务。酒店还设有会议室，可满足中小型会议需求。</p>',
 112, 1),

('寿阳花盛城市酒店', 'https://picsum.photos/400/300?random=43', 4, '山西省晋中市寿阳县滨阳北路', '0354-3333333', '¥228-458',
 '寿阳花盛城市酒店是一家高品质城市酒店，集住宿、餐饮、会议于一体，以优雅的环境和优质的服务著称。',
 '<p>寿阳花盛城市酒店位于寿阳县滨阳北路，毗邻县城中心商业区，地理位置优越，交通便利。酒店建筑气势恢宏，内部装修豪华典雅，是寿阳县档次较高的酒店之一。</p><p>酒店拥有各类豪华客房和套房，房间宽敞舒适，设施一流，配备高品质床品、智能控制系统、迷你吧等。酒店设有中餐厅、西餐厅、大堂吧、会议室、健身房、SPA中心等配套设施，为客人提供全方位的高品质服务。</p>',
 156, 1);

-- ============================================================
-- 数据库初始化完成
-- ============================================================
-- 表数量：18
-- 管理员账号：admin / 123456
-- 测试用户：test / 123456
-- ============================================================
