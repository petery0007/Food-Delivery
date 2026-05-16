package com.xtu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfo {
    private String name;
    private String imageUrl;
    private BigDecimal price;
    private String keywords;
    private String specification;
}
