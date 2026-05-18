package com.xtu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String productName;
    private String imageUrl;
    private String specification;
    private BigDecimal price;
    private Integer quantity;
}
