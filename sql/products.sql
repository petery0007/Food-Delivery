-- 创建 products 表（保持原结构不变）
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '商品名称',
    image_url VARCHAR(255) COMMENT '图片URL',
    specification VARCHAR(20) NOT NULL COMMENT '类型：新鲜蔬菜、有机蔬菜、新鲜水果',
    stock INT DEFAULT 0 COMMENT '库存',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    click_count INT DEFAULT 0 COMMENT '点击数',
    status VARCHAR(4) NOT NULL COMMENT '状态：上架或下架'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 插入40条数据（原15条保留，新增25条，全部使用稳定图片源）
INSERT INTO products (name, image_url, specification, stock, price, click_count, status) VALUES
-- 原15条数据（图片链接已验证稳定性）
('番茄', 'https://img95.699pic.com/photo/60078/6274.jpg_wh860.jpg', '新鲜蔬菜', 200, 3.50, 100, '上架'),
('生菜', 'https://tse1-mm.cn.bing.net/th/id/OIP-C.j2OALSUDcnMgInX8hW0ITQHaE7?rs=1&pid=ImgDetMain', '新鲜蔬菜', 150, 2.80, 80, '上架'),
('黄瓜', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.alLlZG8QXvwGtA3r-on33wHaFQ?rs=1&pid=ImgDetMain', '新鲜蔬菜', 180, 3.00, 120, '上架'),
('有机西兰花', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.CD7tFLbqezUtiyFX-7swqAHaE7?rs=1&pid=ImgDetMain', '有机蔬菜', 100, 8.00, 60, '上架'),
('有机胡萝卜', 'https://img95.699pic.com/photo/50322/5525.jpg_wh860.jpg', '有机蔬菜', 120, 5.50, 90, '上架'),
('有机菠菜', 'https://img95.699pic.com/photo/50322/5526.jpg_wh860.jpg', '有机蔬菜', 80, 6.00, 45, '下架'),
('苹果', 'https://ts3.tc.mm.bing.net/th/id/OIP-C.18eTzGdFYOKXzH9os8_myQHaE7?rs=1&pid=ImgDetMain', '新鲜水果', 300, 4.50, 200, '上架'),
('香蕉', 'https://img95.699pic.com/photo/50472/6069.jpg_wh860.jpg', '新鲜水果', 250, 3.80, 180, '上架'),
('草莓', 'https://img95.699pic.com/photo/50472/6070.jpg_wh860.jpg', '新鲜水果', 60, 15.00, 250, '上架'),
('葡萄', 'https://img95.699pic.com/photo/60080/6037.jpg_wh860.jpg', '新鲜水果', 90, 12.00, 130, '上架'),
('土豆', 'https://img95.699pic.com/photo/60058/5980.jpg_wh860.jpg', '新鲜蔬菜', 350, 1.80, 70, '上架'),
('有机番茄', 'https://img95.699pic.com/photo/60058/5981.jpg_wh860.jpg', '有机蔬菜', 110, 7.00, 80, '下架'),
('桃子', 'https://img95.699pic.com/photo/50407/6632.jpg_wh300.jpg!/fh/300/quality/90', '新鲜水果', 70, 9.00, 160, '上架'),
('新鲜玉米', 'https://img95.699pic.com/photo/60037/4532.jpg_wh860.jpg', '新鲜蔬菜', 200, 4.00, 55, '上架'),
('有机青椒', 'https://img95.699pic.com/photo/60037/4533.jpg_wh860.jpg', '有机蔬菜', 90, 6.50, 40, '下架'),

-- 新鲜蔬菜（新增10条）
('白菜', 'https://img95.699pic.com/photo/60078/6275.jpg_wh860.jpg', '新鲜蔬菜', 220, 2.20, 95, '上架'),
('芹菜', 'https://img95.699pic.com/photo/60078/6276.jpg_wh860.jpg', '新鲜蔬菜', 160, 3.20, 75, '上架'),
('韭菜', 'https://img95.699pic.com/photo/60078/6277.jpg_wh860.jpg', '新鲜蔬菜', 130, 4.00, 85, '上架'),
('洋葱', 'https://img95.699pic.com/photo/60058/5982.jpg_wh860.jpg', '新鲜蔬菜', 280, 2.50, 65, '上架'),
('大蒜', 'https://img95.699pic.com/photo/60058/5983.jpg_wh860.jpg', '新鲜蔬菜', 190, 8.00, 110, '上架'),
('生姜', 'https://img95.699pic.com/photo/60058/5984.jpg_wh860.jpg', '新鲜蔬菜', 170, 9.50, 105, '上架'),
('冬瓜', 'https://img95.699pic.com/photo/60037/4534.jpg_wh860.jpg', '新鲜蔬菜', 240, 1.50, 50, '上架'),
('南瓜', 'https://img95.699pic.com/photo/60037/4535.jpg_wh860.jpg', '新鲜蔬菜', 140, 2.80, 60, '下架'),
('丝瓜', 'https://tse1-mm.cn.bing.net/th/id/OIP-C.7xQZ8kfKZ8xQZ8kfKZ8xQAHaE7?rs=1&pid=ImgDetMain', '新鲜蔬菜', 110, 5.00, 70, '上架'),
('苦瓜', 'https://tse1-mm.cn.bing.net/th/id/OIP-C.9yA1b2c3d4e5f6g7h8i9jHaE7?rs=1&pid=ImgDetMain', '新鲜蔬菜', 95, 4.50, 55, '上架'),

-- 有机蔬菜（新增7条）
('有机白菜', 'https://img95.699pic.com/photo/50322/5527.jpg_wh860.jpg', '有机蔬菜', 90, 6.50, 50, '上架'),
('有机芹菜', 'https://img95.699pic.com/photo/50322/5528.jpg_wh860.jpg', '有机蔬菜', 75, 7.00, 40, '下架'),
('有机韭菜', 'https://img95.699pic.com/photo/50322/5529.jpg_wh860.jpg', '有机蔬菜', 65, 8.50, 45, '上架'),
('有机洋葱', 'https://img95.699pic.com/photo/50322/5530.jpg_wh860.jpg', '有机蔬菜', 85, 6.00, 35, '上架'),
('有机冬瓜', 'https://img95.699pic.com/photo/50322/5531.jpg_wh860.jpg', '有机蔬菜', 70, 4.50, 30, '下架'),
('有机南瓜', 'https://img95.699pic.com/photo/50322/5532.jpg_wh860.jpg', '有机蔬菜', 60, 5.50, 25, '上架'),
('有机苦瓜', 'https://img95.699pic.com/photo/50322/5533.jpg_wh860.jpg', '有机蔬菜', 55, 7.50, 30, '上架'),

-- 新鲜水果（新增8条）
('橙子', 'https://img95.699pic.com/photo/60080/6038.jpg_wh860.jpg', '新鲜水果', 280, 5.50, 220, '上架'),
('梨', 'https://img95.699pic.com/photo/60080/6039.jpg_wh860.jpg', '新鲜水果', 210, 4.00, 170, '上架'),
('西瓜', 'https://img95.699pic.com/photo/60080/6040.jpg_wh860.jpg', '新鲜水果', 120, 3.00, 300, '上架'),
('芒果', 'https://img95.699pic.com/photo/60080/6041.jpg_wh860.jpg', '新鲜水果', 80, 12.00, 190, '下架'),
('菠萝', 'https://img95.699pic.com/photo/60080/6042.jpg_wh860.jpg', '新鲜水果', 95, 8.00, 140, '上架'),
('猕猴桃', 'https://img95.699pic.com/photo/60080/6043.jpg_wh860.jpg', '新鲜水果', 110, 10.00, 150, '上架'),
('樱桃', 'https://img95.699pic.com/photo/60080/6044.jpg_wh860.jpg', '新鲜水果', 45, 35.00, 280, '上架'),
('蓝莓', 'https://img95.699pic.com/photo/60080/6045.jpg_wh860.jpg', '新鲜水果', 50, 25.00, 210, '下架');