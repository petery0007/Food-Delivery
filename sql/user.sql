-- auto-generated definition
CREATE TABLE user
(
    id        INT AUTO_INCREMENT COMMENT '主键ID'
        PRIMARY KEY,
    username  VARCHAR(50)                   NOT NULL COMMENT '用户名',
    password  VARCHAR(100)                  NOT NULL COMMENT '密码（实际项目需加密存储）',
    phone     VARCHAR(11)                   NOT NULL COMMENT '手机号',
    id_number VARCHAR(18)                   NULL COMMENT '身份证号',
    role      VARCHAR(20)  DEFAULT 'user'   NOT NULL COMMENT '用户角色（user/admin/PEISONG）',
    status    ENUM('配送中', '空闲')        NULL COMMENT '配送员状态（仅PEISONG角色有效，普通用户/管理员为NULL）',
    money     DECIMAL(10,2) DEFAULT 0.00    NULL COMMENT '用户余额',
    CONSTRAINT phone
        UNIQUE (phone),
    CONSTRAINT username
        UNIQUE (username)
) COMMENT '用户表';

-- 用户
-- 1. 管理员用户（余额1000）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('admin', 'admin123', '13800000001', '110101199001011234', 'admin', 1000.00, NULL);

-- 2. 普通用户张三（余额100）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('zhangsan', '123456', '13800000002', '310101199505056789', 'user', 100.00, NULL);

-- 3. 普通用户李四（无身份证号，余额0）
INSERT INTO `user` (`username`, `password`, `phone`, `role`, `money`, `status`)
VALUES ('lisi', '123456', '13800000003', 'user', 0.00, NULL);

-- 4. 普通用户王五（余额200.50）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('wangwu', '123456', '13800000004', '440101199808089876', 'user', 200.50, NULL);

-- 5. 普通用户赵六（余额500）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('zhaoliu', '123456', '13800000005', '510101200012125432', 'user', 500.00, NULL);

-- 6. 普通用户孙七（无身份证号，余额300）
INSERT INTO `user` (`username`, `password`, `phone`, `role`, `money`, `status`)
VALUES ('sunqi', '123456', '13800000006', 'user', 300.00, NULL);

-- 7. 普通用户周八（余额800）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('zhouba', '123456', '13800000007', '120101199203031234', 'user', 800.00, NULL);

-- 8. 普通用户吴九（无身份证号，余额150.75）
INSERT INTO `user` (`username`, `password`, `phone`, `role`, `money`, `status`)
VALUES ('wujiu', '123456', '13800000008', 'user', 150.75, NULL);

-- 9. 普通用户郑十（余额0）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('zhengshi', '123456', '13800000009', '610101199909096789', 'user', 0.00, NULL);

-- 10. 普通用户钱百（余额250）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('qianbai', '123456', '13800000010', '320101199707071234', 'user', 250.00, NULL);

-- 配送员
-- 1. 配送员（余额500）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('chenzi', '55524211', '13900000001', '110101199001012233', 'PEISONG', 500.00, '空闲');

-- 2. 配送员 李雷（余额120.50）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('lilei', '123456', '13900000002', '310101199506067788', 'PEISONG', 120.50, '空闲');

-- 3. 配送员 韩梅梅（无身份证号，余额80）
INSERT INTO `user` (`username`, `password`, `phone`, `role`, `money`, `status`)
VALUES ('hanmeimei', '123456', '13900000003', 'PEISONG', 80.00, '空闲');

-- 4. 配送员 张伟（余额300.25）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('zhangwei', '123456', '13900000004', '440101199809098877', 'PEISONG', 300.25, '空闲');

-- 5. 配送员 刘芳（余额180）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('liufang', '123456', '13900000005', '510101200001015544', 'PEISONG', 180.00, '空闲');

-- 6. 配送员 王强（无身份证号，余额50）
INSERT INTO `user` (`username`, `password`, `phone`, `role`, `money`, `status`)
VALUES ('wangqiang', '123456', '13900000006', 'PEISONG', 50.00, '空闲');

-- 7. 配送员 陈丽（余额420.60）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('chenli', '123456', '13900000007', '120101199204042233', 'PEISONG', 420.60, '空闲');

-- 8. 配送员 杨明（无身份证号，余额95.30）
INSERT INTO `user` (`username`, `password`, `phone`, `role`, `money`, `status`)
VALUES ('yangming', '123456', '13900000008', 'PEISONG', 95.30, '空闲');

-- 9. 配送员 黄俊（余额0）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('huangjun', '123456', '13900000009', '610101199910107788', 'PEISONG', 0.00, '空闲');

-- 10. 配送员 吴浩（余额280）
INSERT INTO `user` (`username`, `password`, `phone`, `id_number`, `role`, `money`, `status`)
VALUES ('wuhao', '123456', '13900000010', '320101199708082233', 'PEISONG', 280.00, '空闲');
