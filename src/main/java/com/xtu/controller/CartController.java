package com.xtu.controller;

import com.xtu.service.CartService;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class CartController {

    @Autowired
    private CartService cartService;

    // 获取购物车
    @GetMapping("/cart/{userId}")
    public Result getCart(Integer userId){
        return cartService.getCart(userId);
    }

    // 更新购物车商品
    @PutMapping("/cart/update")
    public Result updateCart(Integer id, Integer quantity){
        return cartService.updateCart(id, quantity);
    }

    // 删除购物车商品
    @PutMapping("/cart/{id}")
    public Result deleteCart(Integer id){
        return cartService.deleteCart(id);
    }

    //提交订单
    @PostMapping("/order/create")
    public Result createOrder(HttpServletRequest request){
        return null;//cartService.createOrder(request);
    }
}
