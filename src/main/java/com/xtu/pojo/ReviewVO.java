package com.xtu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewVO {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private ProductInfo product;
    private String status;
    private Integer rating;
    private String comment;
    private String createTime;
    private String reviewTime;
}
