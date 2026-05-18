package com.xtu.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateRequest {
    private Integer userId;
    private String receiver;
    private String phone;
    private String address;
    private String deliveryType;
    private String paymentType;
    private String remark;
    private List<OrderItem> items;
    private BigDecimal goodsTotal;
    private BigDecimal deliveryFee;
    private BigDecimal totalAmount;
}
