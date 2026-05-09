package com.xtu.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;// id
    private String username;//用户名
    private String password;//密码
    private String phone;//手机号
    private String idNumber;//身份证号码
    private String role;// 身份
    private BigDecimal money; // 新增余额字段
}
