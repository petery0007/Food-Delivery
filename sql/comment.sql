-- 1. 创建评价表（字段严格按要求：评价id、用户id、商品id、评价内容、评分、评价时间）
CREATE TABLE `comment` (
  `comment_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID(主键)',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `goods_id` INT NOT NULL COMMENT '商品ID',
  `content` TEXT NOT NULL COMMENT '评价内容',
  `score` TINYINT UNSIGNED NOT NULL COMMENT '评分(1-5分)',
  `create_time` DATETIME NOT NULL COMMENT '评价时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

