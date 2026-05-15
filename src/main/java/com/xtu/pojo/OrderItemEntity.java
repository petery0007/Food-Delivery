package com.xtu.pojo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemEntity {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String specification;
    private String imageUrl;
    private BigDecimal subtotal;
}
