package com.xtu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo2 {
    private Integer id;
    private String username;
    private String role;
    private BigDecimal money;
    private String phone;
}