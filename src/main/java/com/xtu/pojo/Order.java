package com.xtu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Integer id;// 订单ID
    private Integer userId;// 用户ID
    private String receiver;// 收货人
    private String phone;// 联系电话
    private String address;// 收货地址
    private String deliveryType;// 配送方式
    private String paymentType;// 支付方式
    private String remark;// 备注
    private BigDecimal goodsTotal;// 商品总额
    private BigDecimal deliveryFee;// 配送费
    private BigDecimal totalAmount;// 订单总额
    private Integer status;// 订单状态 0:待支付 1:配送中 2:待收货 3:已完成 4:已取消

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;// 创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;// 完成时间

    private String deliveryStaff;// 配送员


    private List<OrderItemEntity> items;
}