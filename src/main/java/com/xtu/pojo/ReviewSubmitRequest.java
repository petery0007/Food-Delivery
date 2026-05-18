package com.xtu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSubmitRequest {
    private Integer orderId;
    private Integer productId;
    private Integer rating;
    private String comment;
}
