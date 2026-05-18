package com.xtu.service;

import com.xtu.pojo.OrderCreateRequest;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    Result createOrder(HttpServletRequest request, OrderCreateRequest orderRequest);

    Result getOrderList(HttpServletRequest request, Integer page, Integer pageSize, Integer status);

    Result payOrder(HttpServletRequest request, Integer orderId);

    Result cancelOrder(Integer orderId);

    Result deleteOrder(Integer orderId);

    Result updateOrderStatus(HttpServletRequest request, Integer orderId);

    Result getAllOrders(Integer page, Integer pageSize, Integer status, String receiver, String deliveryStaff);
}
