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


-- 用户
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





-- 配送员
-- 1. 配送员管理员（余额500）
INSERT INTO `PEISONG` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('courier_admin', 'admin123', '13900000001', '110101199001012233', 'PEISONG', 500.00);

-- 2. 配送员 李雷（余额120.50）
INSERT INTO `PEISONG` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('lilei', '123456', '13900000002', '310101199506067788', 'PEISONG', 120.50);

-- 3. 配送员 韩梅梅（无身份证号，余额80）
INSERT INTO `PEISONG` (`username`, `password`, `phone`, `role`, `money`)
VALUES ('hanmeimei', '123456', '13900000003', 'PEISONG', 80.00);

-- 4. 配送员 张伟（余额300.25）
INSERT INTO `PEISONG` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('zhangwei', '123456', '13900000004', '440101199809098877', 'PEISONG', 300.25);

-- 5. 配送员 刘芳（余额180）
INSERT INTO `PEISONG` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('liufang', '123456', '13900000005', '510101200001015544', 'PEISONG', 180.00);

-- 6. 配送员 王强（无身份证号，余额50）
INSERT INTO `PEISONG` (`username`, `password`, `phone`, `role`, `money`)
VALUES ('wangqiang', '123456', '13900000006', 'PEISONG', 50.00);

-- 7. 配送员 陈丽（余额420.60）
INSERT INTO `PEISONG` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('chenli', '123456', '13900000007', '120101199204042233', 'PEISONG', 420.60);

-- 8. 配送员 杨明（无身份证号，余额95.30）
INSERT INTO `PEISONG` (`username`, `password`, `phone`, `role`, `money`)
VALUES ('yangming', '123456', '13900000008', 'PEISONG', 95.30);

-- 9. 配送员 黄俊（余额0）
INSERT INTO `PEISONG` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('huangjun', '123456', '13900000009', '610101199910107788', 'PEISONG', 0.00);

-- 10. 配送员 吴浩（余额280）
INSERT INTO `PEISONG` (`username`, `password`, `phone`, `id_number`, `role`, `money`)
VALUES ('wuhao', '123456', '13900000010', '320101199708082233', 'PEISONG', 280.00);