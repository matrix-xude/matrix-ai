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
