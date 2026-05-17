-- 订单主表
CREATE TABLE IF NOT EXISTS orders
(
    id            INT AUTO_INCREMENT COMMENT '主键ID（订单ID）'
    PRIMARY KEY,
    user_id       INT          NOT NULL COMMENT '用户ID',
    receiver      VARCHAR(50)  NOT NULL COMMENT '收货人姓名',
    phone         VARCHAR(20)  NOT NULL COMMENT '联系电话',
    address       VARCHAR(500) NOT NULL COMMENT '收货地址',
    delivery_type VARCHAR(20)  NOT NULL DEFAULT 'express' COMMENT '配送方式：express-快递，self-自提',
    payment_type  VARCHAR(20)  NOT NULL DEFAULT 'balance' COMMENT '支付方式：balance-余额支付，cod-货到付款',
    remark        VARCHAR(500) NULL COMMENT '订单备注',
    goods_total   DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品总额',
    delivery_fee  DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '运费',
    total_amount  DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
    status        INT          NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-配送中，2-待收货，3-已完成，4-已取消',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    finish_time   DATETIME     NULL COMMENT '完成时间',
    delivery_staff    VARCHAR(50)  NULL COMMENT '配送员姓名',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_status (status)
    ) COMMENT '订单主表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_items
(
    id           INT AUTO_INCREMENT COMMENT '主键ID'
    PRIMARY KEY,
    order_id     INT            NOT NULL COMMENT '订单ID（关联orders表）',
    product_id   INT            NOT NULL COMMENT '商品ID',
    product_name VARCHAR(100)   NOT NULL COMMENT '商品名称',
    price        DECIMAL(10, 2) NOT NULL COMMENT '商品单价',
    quantity     INT            NOT NULL DEFAULT 1 COMMENT '购买数量',
    specification VARCHAR(50)   NULL COMMENT '商品规格',
    image_url    VARCHAR(255)   NULL COMMENT '商品图片URL',
    subtotal     DECIMAL(10, 2) NOT NULL COMMENT '小计金额（price * quantity）',
    INDEX idx_order_id (order_id)
    ) COMMENT '订单明细表';

