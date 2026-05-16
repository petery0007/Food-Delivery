package com.xtu.service;

import com.xtu.pojo.Cart;
import com.xtu.pojo.OrderCreateRequest;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

public interface CartService {
    Result getCart(Integer userId);

    Result updateCart(Integer id, Integer quantity);

    Result deleteCart(Integer id);

    Result addCart(HttpServletRequest request, Cart cart);

    Result getDeliveryOrderList(Integer deliveryId, Integer status, Integer pageNum, Integer pageSize);
}
