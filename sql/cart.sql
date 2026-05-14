CREATE TABLE cart
(
    id            INT AUTO_INCREMENT COMMENT '主键ID'
        PRIMARY KEY,
    user_id       INT          NOT NULL COMMENT '用户ID',
    product_id    INT          NOT NULL COMMENT '商品ID',
    image_url     VARCHAR(255) NULL COMMENT '商品图片URL',
    specification VARCHAR(20)  NULL COMMENT '商品规格',
    price         DECIMAL(10, 2) NOT NULL COMMENT '商品单价',
    quantity      INT          NOT NULL DEFAULT 1 COMMENT '购买数量'
) COMMENT '购物车表';
