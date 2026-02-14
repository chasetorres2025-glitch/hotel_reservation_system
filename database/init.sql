-- 酒店管理系统数据库初始化脚本
-- 数据库版本：MySQL 8.0+
-- 创建日期：2026-02-13

-- 创建数据库
CREATE DATABASE IF NOT EXISTS hotel_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE hotel_system;

-- ============================================
-- 1. 用户表（sys_user）
-- ============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色（admin/user）',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态（1启用 0禁用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ============================================
-- 2. 客房类型表（sys_room_type）
-- ============================================
DROP TABLE IF EXISTS `sys_room_type`;
CREATE TABLE `sys_room_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_code` VARCHAR(20) NOT NULL COMMENT '类型编码',
  `type_name` VARCHAR(50) NOT NULL COMMENT '类型名称',
  `base_price` DECIMAL(10,2) NOT NULL COMMENT '基础价格',
  `bed_type` VARCHAR(50) DEFAULT NULL COMMENT '床型',
  `area` DECIMAL(10,2) DEFAULT NULL COMMENT '面积（㎡）',
  `facilities` VARCHAR(500) DEFAULT NULL COMMENT '设施配置',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '类型描述',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态（1启用 0禁用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客房类型表';

-- ============================================
-- 3. 客房信息表（sys_room）
-- ============================================
DROP TABLE IF EXISTS `sys_room`;
CREATE TABLE `sys_room` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '客房ID',
  `room_number` VARCHAR(20) NOT NULL COMMENT '房间号',
  `type_id` BIGINT(20) NOT NULL COMMENT '客房类型ID',
  `floor` INT(11) DEFAULT NULL COMMENT '楼层',
  `price` DECIMAL(10,2) NOT NULL COMMENT '实际价格',
  `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态（0空闲 1预订 2入住 3维护）',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '客房描述',
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT '客房图片',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_number` (`room_number`),
  KEY `idx_type_id` (`type_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_room_type` FOREIGN KEY (`type_id`) REFERENCES `sys_room_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客房信息表';

-- ============================================
-- 4. 订单表（sys_order）
-- ============================================
DROP TABLE IF EXISTS `sys_order`;
CREATE TABLE `sys_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `room_id` BIGINT(20) NOT NULL COMMENT '客房ID',
  `check_in_date` DATE NOT NULL COMMENT '入住日期',
  `check_out_date` DATE NOT NULL COMMENT '退房日期',
  `guest_count` INT(11) NOT NULL DEFAULT 1 COMMENT '入住人数',
  `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
  `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `paid_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '已支付金额',
  `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '订单状态（0待支付 1待确认 2已确认 3已入住 4已完成 5已取消 6已拒绝）',
  `special_request` VARCHAR(500) DEFAULT NULL COMMENT '特殊要求',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_status` (`status`),
  KEY `idx_check_in_date` (`check_in_date`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_order_room` FOREIGN KEY (`room_id`) REFERENCES `sys_room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

-- ============================================
-- 5. 支付记录表（sys_payment）
-- ============================================
DROP TABLE IF EXISTS `sys_payment`;
CREATE TABLE `sys_payment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
  `payment_no` VARCHAR(50) NOT NULL COMMENT '支付流水号',
  `payment_method` VARCHAR(20) NOT NULL COMMENT '支付方式',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
  `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '支付状态（0待支付 1已支付 2已退款）',
  `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `sys_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入管理员账号（密码：admin123，使用BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`)
VALUES ('admin', '$2a$10$.MwRNfC/c5AXP.av07fAx.LcTwOnPGAZBpsJUaM8j/BR3cE.VIYIK', '系统管理员', '13800138000', 'admin@hotel.com', 'admin', 1);

-- 插入测试用户（密码：user123）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`)
VALUES ('user001', '$2a$10$RrLStHdQK0GFNKk31ZFwoeSG1q2TK5ZmLaXWf4/ZMXESPlqzEvK.6', '张三', '13900139000', 'user001@example.com', 'user', 1);

-- 插入客房类型数据
INSERT INTO `sys_room_type` (`type_code`, `type_name`, `base_price`, `bed_type`, `area`, `facilities`, `description`, `status`)
VALUES 
('STANDARD', '标准间', 288.00, '双床1.2m', 25.00, 'WiFi、空调、电视、独立卫浴', '经济实惠的标准间，适合商务出行', 1),
('BIG_BED', '大床房', 328.00, '大床1.8m', 28.00, 'WiFi、空调、电视、独立卫浴、迷你吧', '温馨舒适的大床房，适合情侣入住', 1),
('DELUXE', '豪华房', 488.00, '大床2.0m', 35.00, 'WiFi、空调、电视、独立卫浴、迷你吧、浴缸', '宽敞豪华的客房，享受尊贵体验', 1),
('SUITE', '套房', 888.00, '大床2.0m', 55.00, 'WiFi、空调、电视、独立卫浴、迷你吧、浴缸、客厅', '独立客厅和卧室，适合家庭或商务接待', 1);

-- 插入客房数据
INSERT INTO `sys_room` (`room_number`, `type_id`, `floor`, `price`, `status`, `description`)
VALUES 
('101', 1, 1, 288.00, 0, '一楼标准间，安静舒适'),
('102', 1, 1, 288.00, 0, '一楼标准间，靠近电梯'),
('103', 1, 1, 288.00, 0, '一楼标准间，采光好'),
('201', 2, 2, 328.00, 0, '二楼大床房，温馨浪漫'),
('202', 2, 2, 328.00, 0, '二楼大床房，视野开阔'),
('203', 2, 2, 328.00, 0, '二楼大床房，安静舒适'),
('301', 3, 3, 488.00, 0, '三楼豪华房，设施齐全'),
('302', 3, 3, 488.00, 0, '三楼豪华房，享受尊贵'),
('401', 4, 4, 888.00, 0, '四楼套房，宽敞豪华'),
('402', 4, 4, 888.00, 0, '四楼套房，家庭首选');

-- 数据库初始化完成
SELECT '数据库初始化完成！' AS message;
