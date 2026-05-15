package com.xtu.pojo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItem {
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String specification;
    private String imageUrl;
}
