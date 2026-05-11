-- 创建 product 表
CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '商品名称',
    image_url VARCHAR(255) COMMENT '图片URL',
    specification VARCHAR(20) NOT NULL COMMENT '类型：新鲜蔬菜、有机蔬菜、新鲜水果',
    stock INT DEFAULT 0 COMMENT '库存',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    click_count INT DEFAULT 0 COMMENT '点击数',
    status VARCHAR(4) NOT NULL COMMENT '状态：上架或下架'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 插入15条数据
INSERT INTO product (name, image_url, specification, stock, price, click_count, status) VALUES
('番茄', 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg', '新鲜蔬菜', 200, 3.50, 100, '上架'),
('生菜', 'https://tse1-mm.cn.bing.net/th/id/OIP-C.j2OALSUDcnMgInX8hW0ITQHaE7?o=7rm=3&rs=1&pid=ImgDetMain&o=7&rm=3', '新鲜蔬菜', 150, 2.80, 80, '上架'),
('黄瓜', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.alLlZG8QXvwGtA3r-on33wHaFQ?rs=1&pid=ImgDetMain&o=7&rm=3', '新鲜蔬菜', 180, 3.00, 120, '上架'),
('有机西兰花', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.CD7tFLbqezUtiyFX-7swqAHaE7?o=7rm=3&rs=1&pid=ImgDetMain&o=7&rm=3', '有机蔬菜', 100, 8.00, 60, '上架'),
('有机胡萝卜', 'https://img95.699pic.com/photo/50322/5525.jpg_wh860.jpg', '有机蔬菜', 120, 5.50, 90, '上架'),
('有机菠菜', 'https://gd-hbimg.huaban.com/6a4ea4f88a5a118d61a3e0635873369b50d6d47431a4f-MldWC1_fw658', '有机蔬菜', 80, 6.00, 45, '下架'),
('苹果', 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain&o=7&rm=3', '新鲜水果', 300, 4.50, 200, '上架'),
('香蕉', 'https://www.2008php.com/2020_Website_appreciate/2020-11-18/20201118123305.jpg', '新鲜水果', 250, 3.80, 180, '上架'),
('草莓', 'https://img95.699pic.com/photo/50472/6070.jpg_wh860.jpg', '新鲜水果', 60, 15.00, 250, '上架'),
('葡萄', 'https://img95.699pic.com/photo/60080/6037.jpg_wh860.jpg', '新鲜水果', 90, 12.00, 130, '上架'),
('土豆', 'https://pic.nximg.cn/file/20230512/29159477_142537728102_2.jpg', '新鲜蔬菜', 350, 1.80, 70, '上架'),
('有机番茄', 'https://img95.699pic.com/photo/60058/5981.jpg_wh860.jpg', '有机蔬菜', 110, 7.00, 80, '下架'),
('桃子', 'https://img95.699pic.com/photo/50407/6632.jpg_wh300.jpg!/fh/300/quality/90', '新鲜水果', 70, 9.00, 160, '上架'),
('新鲜玉米', 'https://img95.699pic.com/photo/60037/4532.jpg_wh860.jpg', '新鲜蔬菜', 200, 4.00, 55, '上架'),
('有机青椒', 'https://pic.nximg.cn/file/20241121/7972473_173337537102_2.jpg', '有机蔬菜', 90, 6.50, 40, '下架');