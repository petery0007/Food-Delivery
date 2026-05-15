NEW_FILE_CODE
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
    peisong_id    VARCHAR(50)  NULL COMMENT '配送员ID',
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

-- ============================================
-- 订单测试数据
-- ============================================

-- ==================== 订单主表数据 ====================

-- 用户 zhangsan (userId=2) 的订单 - 已完成订单（余额支付）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
(2, '张三', '13800000002', '北京市朝阳区建国路100号XX小区3号楼502室', 'express', 'balance', '请轻拿轻放', 59.80, 0.00, 59.80, 3, '2026-05-10 10:30:00', '2026-05-12 14:20:00', 'chenzi');

-- 用户 zhangsan (userId=2) 的订单 - 配送中订单（货到付款）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (2, '张三', '13800000002', '北京市朝阳区建国路100号XX小区3号楼502室', 'express', 'cod', '尽快送达', 125.50, 8.00, 133.50, 1, '2026-05-14 15:20:00', NULL, 'lilei');

-- 用户 lisi (userId=3) 的订单 - 待收货订单（余额支付）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (3, '李四', '13800000003', '上海市浦东新区世纪大道200号YY花园8栋1201室', 'express', 'balance', '', 45.00, 8.00, 53.00, 2, '2026-05-13 09:15:00', NULL, 'hanmeimei');

-- 用户 wangwu (userId=4) 的订单 - 待支付订单（余额支付）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (4, '王五', '13800000004', '广州市天河区天河路300号ZZ大厦15层', 'express', 'balance', '周末配送', 88.00, 0.00, 88.00, 0, '2026-05-15 08:00:00', NULL, NULL);

-- 用户 wangwu (userId=4) 的订单 - 已完成订单（货到付款）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (4, '王五', '13800000004', '广州市天河区天河路300号ZZ大厦15层', 'express', 'cod', '', 35.00, 8.00, 43.00, 3, '2026-05-08 14:30:00', '2026-05-10 16:45:00', 'zhangwei');

-- 用户 zhaoliu (userId=5) 的订单 - 已完成订单（余额支付）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (5, '赵六', '13800000005', '深圳市南山区科技园南路500号AA科技园B座', 'self', 'balance', '自提', 156.00, 0.00, 156.00, 3, '2026-05-11 11:00:00', '2026-05-11 18:30:00', NULL);

-- 用户 zhaoliu (userId=5) 的订单 - 已取消订单
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (5, '赵六', '13800000005', '深圳市南山区科技园南路500号AA科技园B座', 'express', 'balance', '地址填错了', 72.50, 8.00, 80.50, 4, '2026-05-12 16:45:00', '2026-05-12 17:00:00', NULL);

-- 用户 sunqi (userId=6) 的订单 - 配送中订单（余额支付）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (6, '孙七', '13800000006', '杭州市西湖区文三路400号BB小区6栋301室', 'express', 'balance', '放快递柜', 210.00, 0.00, 210.00, 1, '2026-05-14 13:20:00', NULL, 'liufang');

-- 用户 zhouba (userId=7) 的订单 - 待收货订单（货到付款）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (7, '周八', '13800000007', '成都市武侯区人民南路600号CC广场', 'express', 'cod', '电话联系', 95.00, 0.00, 95.00, 2, '2026-05-13 17:30:00', NULL, 'wangqiang');

-- 用户 zhouba (userId=7) 的订单 - 已完成订单（余额支付）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (7, '周八', '13800000007', '成都市武侯区人民南路600号CC广场', 'self', 'balance', '自提', 48.00, 0.00, 48.00, 3, '2026-05-09 10:00:00', '2026-05-09 19:00:00', NULL);

-- 用户 wujiu (userId=8) 的订单 - 待支付订单（货到付款）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (8, '吴九', '13800000008', '南京市鼓楼区中山北路700号DD大厦', 'express', 'cod', '', 67.00, 8.00, 75.00, 0, '2026-05-15 09:30:00', NULL, NULL);

-- 用户 zhengshi (userId=9) 的订单 - 已完成订单（余额支付）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (9, '郑十', '13800000009', '武汉市江汉区解放大道800号EE小区', 'express', 'balance', '谢谢', 180.00, 0.00, 180.00, 3, '2026-05-07 12:00:00', '2026-05-09 15:30:00', 'chenli');

-- 用户 qianbai (userId=10) 的订单 - 配送中订单（货到付款）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (10, '钱百', '13800000010', '西安市雁塔区长安南路900号FF花园', 'express', 'cod', '小心易碎', 320.00, 0.00, 320.00, 1, '2026-05-14 16:00:00', NULL, 'yangming');

-- 用户 admin (userId=1) 的订单 - 已完成订单（余额支付）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (1, '管理员', '13800000001', '北京市海淀区中关村大街1号', 'express', 'balance', '测试订单', 25.00, 8.00, 33.00, 3, '2026-05-06 10:00:00', '2026-05-07 14:00:00', 'huangjun');

-- 用户 zhangsan (userId=2) 的订单 - 待支付订单（余额支付）
INSERT INTO orders (user_id, receiver, phone, address, delivery_type, payment_type, remark, goods_total, delivery_fee, total_amount, status, create_time, finish_time, peisong_id) VALUES
    (2, '张三', '13800000002', '北京市朝阳区建国路100号XX小区3号楼502室', 'express', 'balance', '', 42.00, 8.00, 50.00, 0, '2026-05-15 10:00:00', NULL, NULL);


-- ==================== 订单明细表数据 ====================

-- 订单1 (orderId=1) 的明细 - 番茄 x 2, 黄瓜 x 1
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (1, 1, '番茄', 3.50, 2, '新鲜蔬菜', 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg', 7.00),
                                                                                                                      (1, 3, '黄瓜', 3.00, 1, '新鲜蔬菜', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.alLlZG8QXvwGtA3r-on33wHaFQ?rs=1&pid=ImgDetMain', 3.00),
                                                                                                                      (1, 21, '白菜', 2.20, 5, '新鲜蔬菜', 'https://img95.699pic.com/photo/60021/3201.jpg_wh860.jpg', 11.00),
                                                                                                                      (1, 7, '苹果', 4.50, 8, '新鲜水果', 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain', 36.00),
                                                                                                                      (1, 2, '生菜', 2.80, 1, '新鲜蔬菜', 'https://tse1-mm.cn.bing.net/th/id/OIP-C.j2OALSUDcnMgInX8hW0ITQHaE7?rs=1&pid=ImgDetMain', 2.80);

-- 订单2 (orderId=2) 的明细 - 草莓 x 2, 樱桃 x 1, 蓝莓 x 1
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (2, 9, '草莓', 15.00, 2, '新鲜水果', 'https://img95.699pic.com/photo/50472/6070.jpg_wh860.jpg', 30.00),
                                                                                                                      (2, 53, '樱桃', 35.00, 1, '新鲜水果', 'https://img95.699pic.com/photo/60073/3550.jpg_wh860.jpg', 35.00),
                                                                                                                      (2, 54, '蓝莓', 25.00, 1, '新鲜水果', 'https://img95.699pic.com/photo/60022/8913.jpg_wh860.jpg', 25.00),
                                                                                                                      (2, 8, '香蕉', 3.80, 5, '新鲜水果', 'https://imgs.699pic.com/images/505/395/907.jpg!detail.v1', 19.00),
                                                                                                                      (2, 14, '新鲜玉米', 4.00, 4, '新鲜蔬菜', 'https://img95.699pic.com/photo/60037/4532.jpg_wh860.jpg', 16.00);

-- 订单3 (orderId=3) 的明细 - 土豆 x 5, 洋葱 x 3
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (3, 11, '土豆', 1.80, 5, '新鲜蔬菜', 'https://img95.699pic.com/photo/60058/5980.jpg_wh860.jpg', 9.00),
                                                                                                                      (3, 40, '洋葱', 2.50, 3, '新鲜蔬菜', 'https://img95.699pic.com/photo/60064/9302.jpg_wh860.jpg', 7.50),
                                                                                                                      (3, 41, '大蒜', 8.00, 2, '新鲜蔬菜', 'https://cdn.pixabay.com/photo/2017/02/25/13/24/garlic-2097759_1280.jpg', 16.00),
                                                                                                                      (3, 42, '生姜', 9.50, 1, '新鲜蔬菜', 'https://img95.699pic.com/photo/60053/5206.jpg_wh860.jpg', 9.50),
                                                                                                                      (3, 12, '有机番茄', 7.00, 2, '有机蔬菜', 'https://img95.699pic.com/photo/60058/5981.jpg_wh860.jpg', 14.00);

-- 订单4 (orderId=4) 的明细 - 西瓜 x 2, 橙子 x 3
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (4, 51, '西瓜', 3.00, 2, '新鲜水果', 'https://img95.699pic.com/photo/60015/4981.jpg_wh300.jpg!/fh/300/quality/90', 6.00),
                                                                                                                      (4, 49, '橙子', 5.50, 3, '新鲜水果', 'https://img95.699pic.com/photo/60063/7761.jpg_wh860.jpg', 16.50),
                                                                                                                      (4, 10, '葡萄', 12.00, 2, '新鲜水果', 'https://img95.699pic.com/photo/60080/6037.jpg_wh860.jpg', 24.00),
                                                                                                                      (4, 50, '梨', 4.00, 3, '新鲜水果', 'https://pic.52112.com/180327/180327_109/pSqG2Yuchm_small.jpg', 12.00),
                                                                                                                      (4, 52, '芒果', 12.00, 2, '新鲜水果', 'https://img.huabaike.com/tukuimgs/772/20200923133206_118354.jpg', 24.00);

-- 订单5 (orderId=5) 的明细 - 芹菜 x 2, 韭菜 x 1
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (5, 38, '芹菜', 3.20, 2, '新鲜蔬菜', 'https://img95.699pic.com/photo/60013/8817.jpg_wh860.jpg', 6.40),
                                                                                                                      (5, 39, '韭菜', 4.00, 1, '新鲜蔬菜', 'https://th.bing.com/th/id/R.181d10634b035dd42f7ca74e7c80971c?rik=r3VTGvZ7FU7fuw&riu=http%3a%2f%2fseopic.699pic.com%2fphoto%2f50049%2f9013.jpg_wh1200.jpg&ehk=v%2be0yA3PmC8eBgdKM4gbbC%2bKSnVBnoyNRX%2fObgzeYmA%3d&risl=&pid=ImgRaw&r=0', 4.00),
                                                                                                                      (5, 1, '番茄', 3.50, 3, '新鲜蔬菜', 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg', 10.50),
                                                                                                                      (5, 3, '黄瓜', 3.00, 2, '新鲜蔬菜', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.alLlZG8QXvwGtA3r-on33wHaFQ?rs=1&pid=ImgDetMain', 6.00),
                                                                                                                      (5, 43, '冬瓜', 1.50, 5, '新鲜蔬菜', 'https://img95.699pic.com/photo/50245/0811.jpg_wh860.jpg', 7.50);

-- 订单6 (orderId=6) 的明细 - 有机西兰花 x 3, 有机胡萝卜 x 2, 有机菠菜 x 1
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (6, 4, '有机西兰花', 8.00, 3, '有机蔬菜', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.CD7tFLbqezUtiyFX-7swqAHaE7?rs=1&pid=ImgDetMain', 24.00),
                                                                                                                      (6, 5, '有机胡萝卜', 5.50, 2, '有机蔬菜', 'https://img95.699pic.com/photo/50322/5525.jpg_wh860.jpg', 11.00),
                                                                                                                      (6, 6, '有机菠菜', 6.00, 1, '有机蔬菜', 'https://img95.699pic.com/photo/50322/5526.jpg_wh860.jpg', 6.00),
                                                                                                                      (6, 34, '有机青椒', 6.50, 4, '有机蔬菜', 'https://img95.699pic.com/photo/60037/4533.jpg_wh860.jpg', 26.00),
                                                                                                                      (6, 12, '有机番茄', 7.00, 5, '有机蔬菜', 'https://img95.699pic.com/photo/60058/5981.jpg_wh860.jpg', 35.00),
                                                                                                                      (6, 13, '桃子', 9.00, 6, '新鲜水果', 'https://img95.699pic.com/photo/50407/6632.jpg_wh300.jpg!/fh/300/quality/90', 54.00);

-- 订单7 (orderId=7) 的明细 - 菠萝 x 2, 猕猴桃 x 1
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (7, 53, '菠萝', 8.00, 2, '新鲜水果', 'https://img95.699pic.com/photo/60070/4995.jpg_wh860.jpg', 16.00),
                                                                                                                      (7, 54, '猕猴桃', 10.00, 1, '新鲜水果', 'https://img95.699pic.com/photo/50020/2526.jpg_wh860.jpg', 10.00),
                                                                                                                      (7, 7, '苹果', 4.50, 4, '新鲜水果', 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain', 18.00),
                                                                                                                      (7, 8, '香蕉', 3.80, 3, '新鲜水果', 'https://imgs.699pic.com/images/505/395/907.jpg!detail.v1', 11.40),
                                                                                                                      (7, 50, '梨', 4.00, 4, '新鲜水果', 'https://pic.52112.com/180327/180327_109/pSqG2Yuchm_small.jpg', 16.00);

-- 订单8 (orderId=8) 的明细 - 樱桃 x 3, 蓝莓 x 2, 草莓 x 2
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (8, 53, '樱桃', 35.00, 3, '新鲜水果', 'https://img95.699pic.com/photo/60073/3550.jpg_wh860.jpg', 105.00),
                                                                                                                      (8, 54, '蓝莓', 25.00, 2, '新鲜水果', 'https://img95.699pic.com/photo/60022/8913.jpg_wh860.jpg', 50.00),
                                                                                                                      (8, 9, '草莓', 15.00, 2, '新鲜水果', 'https://img95.699pic.com/photo/50472/6070.jpg_wh860.jpg', 30.00),
                                                                                                                      (8, 10, '葡萄', 12.00, 2, '新鲜水果', 'https://img95.699pic.com/photo/60080/6037.jpg_wh860.jpg', 24.00);

-- 订单9 (orderId=9) 的明细 - 南瓜 x 3, 丝瓜 x 2, 苦瓜 x 2
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (9, 44, '南瓜', 2.80, 3, '新鲜蔬菜', 'https://img95.699pic.com/photo/60015/5210.jpg_wh860.jpg', 8.40),
                                                                                                                      (9, 45, '丝瓜', 5.00, 2, '新鲜蔬菜', 'https://img95.699pic.com/photo/50390/6735.jpg_wh860.jpg', 10.00),
                                                                                                                      (9, 46, '苦瓜', 4.50, 2, '新鲜蔬菜', 'https://img95.699pic.com/photo/50120/4895.jpg_wh860.jpg', 9.00),
                                                                                                                      (9, 4, '有机西兰花', 8.00, 3, '有机蔬菜', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.CD7tFLbqezUtiyFX-7swqAHaE7?rs=1&pid=ImgDetMain', 24.00),
                                                                                                                      (9, 5, '有机胡萝卜', 5.50, 4, '有机蔬菜', 'https://img95.699pic.com/photo/50322/5525.jpg_wh860.jpg', 22.00),
                                                                                                                      (9, 14, '新鲜玉米', 4.00, 5, '新鲜蔬菜', 'https://img95.699pic.com/photo/60037/4532.jpg_wh860.jpg', 20.00);

-- 订单10 (orderId=10) 的明细 - 白菜 x 5, 土豆 x 10
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (10, 21, '白菜', 2.20, 5, '新鲜蔬菜', 'https://img95.699pic.com/photo/60021/3201.jpg_wh860.jpg', 11.00),
                                                                                                                      (10, 11, '土豆', 1.80, 10, '新鲜蔬菜', 'https://img95.699pic.com/photo/60058/5980.jpg_wh860.jpg', 18.00),
                                                                                                                      (10, 3, '黄瓜', 3.00, 3, '新鲜蔬菜', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.alLlZG8QXvwGtA3r-on33wHaFQ?rs=1&pid=ImgDetMain', 9.00),
                                                                                                                      (10, 1, '番茄', 3.50, 2, '新鲜蔬菜', 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg', 7.00),
                                                                                                                      (10, 42, '生姜', 9.50, 1, '新鲜蔬菜', 'https://img95.699pic.com/photo/60053/5206.jpg_wh860.jpg', 9.50);

-- 订单11 (orderId=11) 的明细 - 大蒜 x 2, 生姜 x 1
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (11, 41, '大蒜', 8.00, 2, '新鲜蔬菜', 'https://cdn.pixabay.com/photo/2017/02/25/13/24/garlic-2097759_1280.jpg', 16.00),
                                                                                                                      (11, 42, '生姜', 9.50, 1, '新鲜蔬菜', 'https://img95.699pic.com/photo/60053/5206.jpg_wh860.jpg', 9.50),
                                                                                                                      (11, 38, '芹菜', 3.20, 4, '新鲜蔬菜', 'https://img95.699pic.com/photo/60013/8817.jpg_wh860.jpg', 12.80),
                                                                                                                      (11, 39, '韭菜', 4.00, 3, '新鲜蔬菜', 'https://th.bing.com/th/id/R.181d10634b035dd42f7ca74e7c80971c?rik=r3VTGvZ7FU7fuw&riu=http%3a%2f%2fseopic.699pic.com%2fphoto%2f50049%2f9013.jpg_wh1200.jpg&ehk=v%2be0yA3PmC8eBgdKM4gbbC%2bKSnVBnoyNRX%2fObgzeYmA%3d&risl=&pid=ImgRaw&r=0', 12.00),
                                                                                                                      (11, 40, '洋葱', 2.50, 6, '新鲜蔬菜', 'https://img95.699pic.com/photo/60064/9302.jpg_wh860.jpg', 15.00);

-- 订单12 (orderId=12) 的明细 - 西瓜 x 5, 橙子 x 4, 桃子 x 3
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (12, 51, '西瓜', 3.00, 5, '新鲜水果', 'https://img95.699pic.com/photo/60015/4981.jpg_wh300.jpg!/fh/300/quality/90', 15.00),
                                                                                                                      (12, 49, '橙子', 5.50, 4, '新鲜水果', 'https://img95.699pic.com/photo/60063/7761.jpg_wh860.jpg', 22.00),
                                                                                                                      (12, 13, '桃子', 9.00, 3, '新鲜水果', 'https://img95.699pic.com/photo/50407/6632.jpg_wh300.jpg!/fh/300/quality/90', 27.00),
                                                                                                                      (12, 52, '芒果', 12.00, 4, '新鲜水果', 'https://img.huabaike.com/tukuimgs/772/20200923133206_118354.jpg', 48.00),
                                                                                                                      (12, 53, '菠萝', 8.00, 5, '新鲜水果', 'https://img95.699pic.com/photo/60070/4995.jpg_wh860.jpg', 40.00),
                                                                                                                      (12, 54, '猕猴桃', 10.00, 3, '新鲜水果', 'https://img95.699pic.com/photo/50020/2526.jpg_wh860.jpg', 30.00);

-- 订单13 (orderId=13) 的明细 - 樱桃 x 5, 蓝莓 x 3
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (13, 53, '樱桃', 35.00, 5, '新鲜水果', 'https://img95.699pic.com/photo/60073/3550.jpg_wh860.jpg', 175.00),
                                                                                                                      (13, 54, '蓝莓', 25.00, 3, '新鲜水果', 'https://img95.699pic.com/photo/60022/8913.jpg_wh860.jpg', 75.00),
                                                                                                                      (13, 9, '草莓', 15.00, 3, '新鲜水果', 'https://img95.699pic.com/photo/50472/6070.jpg_wh860.jpg', 45.00),
                                                                                                                      (13, 10, '葡萄', 12.00, 2, '新鲜水果', 'https://img95.699pic.com/photo/60080/6037.jpg_wh860.jpg', 24.00);

-- 订单14 (orderId=14) 的明细 - 番茄 x 3, 生菜 x 2
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (14, 1, '番茄', 3.50, 3, '新鲜蔬菜', 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg', 10.50),
                                                                                                                      (14, 2, '生菜', 2.80, 2, '新鲜蔬菜', 'https://tse1-mm.cn.bing.net/th/id/OIP-C.j2OALSUDcnMgInX8hW0ITQHaE7?rs=1&pid=ImgDetMain', 5.60),
                                                                                                                      (14, 3, '黄瓜', 3.00, 3, '新鲜蔬菜', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.alLlZG8QXvwGtA3r-on33wHaFQ?rs=1&pid=ImgDetMain', 9.00);

-- 订单15 (orderId=15) 的明细 - 苹果 x 4, 香蕉 x 3
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, specification, image_url, subtotal) VALUES
                                                                                                                      (15, 7, '苹果', 4.50, 4, '新鲜水果', 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain', 18.00),
                                                                                                                      (15, 8, '香蕉', 3.80, 3, '新鲜水果', 'https://imgs.699pic.com/images/505/395/907.jpg!detail.v1', 11.40),
                                                                                                                      (15, 11, '土豆', 1.80, 5, '新鲜蔬菜', 'https://img95.699pic.com/photo/60058/5980.jpg_wh860.jpg', 9.00),
                                                                                                                      (15, 14, '新鲜玉米', 4.00, 1, '新鲜蔬菜', 'https://img95.699pic.com/photo/60037/4532.jpg_wh860.jpg', 4.00);

