package com.xtu.controller;

import com.xtu.service.CartService;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result updateCart(Integer productId, Integer quantity){
        return null;//cartService.updateCart(productId, quantity);
    }

    // 删除购物车商品
    @PutMapping("/cart/{id}")
    public Result deleteCart(Integer id){
        return null;//cartService.deleteCart(id);
    }
}
