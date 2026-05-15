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

    Result createOrder(HttpServletRequest request, OrderCreateRequest orderRequest);

    Result getOrderList(HttpServletRequest request, Integer page, Integer pageSize);
}
