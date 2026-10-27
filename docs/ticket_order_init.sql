-- ============================================================
-- 寿阳文旅云 - 门票预订订单表（第1次修改新增）
-- 用于支持票务系统的在线预订功能
-- 执行方式：mysql -u root -p shouyang_culture < docs/ticket_order_init.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS `ticket_order` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `ticket_id`   BIGINT       NOT NULL                COMMENT '票务ID',
  `user_id`     BIGINT       NOT NULL                COMMENT '用户ID',
  `name`        VARCHAR(50)  NOT NULL                COMMENT '取票人姓名',
  `phone`       VARCHAR(20)  NOT NULL                COMMENT '联系电话',
  `quantity`    INT          NOT NULL DEFAULT 1      COMMENT '预订数量',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态 0已取消 1有效',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  PRIMARY KEY (`id`),
  KEY `idx_ticket_id` (`ticket_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='门票预订订单表';
