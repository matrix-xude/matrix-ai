-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` VARCHAR(36) NOT NULL COMMENT '用户 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt 加密密码',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号（唯一）',
  `phone_verified` TINYINT DEFAULT 0 COMMENT '手机号是否验证',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱（唯一）',
  `email_verified` TINYINT DEFAULT 0 COMMENT '邮箱是否验证',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像 URL',
  `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-正常，1-封禁，2-注销中',
  `role` VARCHAR(50) DEFAULT 'USER' COMMENT '角色',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` INT DEFAULT 0 COMMENT '乐观锁版本号',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- 计算历史记录表
CREATE TABLE IF NOT EXISTS `calculation_history` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键 ID',
  `num1` DOUBLE NOT NULL COMMENT '第一个操作数',
  `num2` DOUBLE NOT NULL COMMENT '第二个操作数',
  `operator` VARCHAR(10) NOT NULL COMMENT '运算符',
  `result` DOUBLE NOT NULL COMMENT '计算结果',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` INT DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='计算历史记录表';
