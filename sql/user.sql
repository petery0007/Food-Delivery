-- auto-generated definition
CREATE TABLE user
(
    id        INT AUTO_INCREMENT COMMENT '主键ID'
        PRIMARY KEY,
    username  VARCHAR(50)                   NOT NULL COMMENT '用户名',
    password  VARCHAR(100)                  NOT NULL COMMENT '密码（实际项目需加密存储）',
    phone     VARCHAR(11)                   NOT NULL COMMENT '手机号',
    id_number VARCHAR(18)                   NULL COMMENT '身份证号',
    role      VARCHAR(20)  DEFAULT 'user'   NOT NULL COMMENT '用户角色（user/admin）',
    money     DECIMAL(10,2) DEFAULT 0.00    NULL COMMENT '用户余额',
    CONSTRAINT phone
        UNIQUE (phone),
    CONSTRAINT username
        UNIQUE (username)
) COMMENT '用户表';

-- 1. 管理员用户（余额1000）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('admin', 'admin123', '13800000001', '110101199001011234', 'admin', 1000.00);

-- 2. 普通用户张三（余额100）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('zhangsan', '123456', '13800000002', '310101199505056789', 'user', 100.00);

-- 3. 普通用户李四（无身份证号，余额0）
INSERT INTO `user` (`username`, `password`, `phone`, `role`, `money`)
VALUES ('lisi', '123456', '13800000003', 'user', 0.00);

-- 4. 普通用户王五（余额200.50）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('wangwu', '123456', '13800000004', '440101199808089876', 'user', 200.50);

-- 5. 普通用户赵六（余额500）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('zhaoliu', '123456', '13800000005', '510101200012125432', 'user', 500.00);

-- 6. 普通用户孙七（无身份证号，余额300）
INSERT INTO `user` (`username`, `password`, `phone`, `role`, `money`)
VALUES ('sunqi', '123456', '13800000006', 'user', 300.00);

-- 7. 普通用户周八（余额800）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('zhouba', '123456', '13800000007', '120101199203031234', 'user', 800.00);

-- 8. 普通用户吴九（无身份证号，余额150.75）
INSERT INTO `user` (`username`, `password`, `phone`, `role`, `money`)
VALUES ('wujiu', '123456', '13800000008', 'user', 150.75);

-- 9. 普通用户郑十（余额0）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('zhengshi', '123456', '13800000009', '610101199909096789', 'user', 0.00);

-- 10. 普通用户钱百（余额250）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('qianbai', '123456', '13800000010', '320101199707071234', 'user', 250.00);