package com.xtu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer id;//id
    private String name;//名称
    private String imageUrl;//图片url
    private String specification;//类型
    private Integer stock;//库存
    private BigDecimal price;//价格
    private Integer clickCount;//点击数
    private String status; //是否上架
}
