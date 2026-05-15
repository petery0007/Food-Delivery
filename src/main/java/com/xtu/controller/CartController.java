package com.xtu.controller;

import com.xtu.pojo.Cart;
import com.xtu.pojo.OrderCreateRequest;
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

    // 添加购物车
    @PostMapping("/cart/add")
    public Result addCart(HttpServletRequest request, @RequestBody Cart cart){
        log.info("添加购物车，cart: {}", cart);
        return cartService.addCart(request, cart);
    }

    // 获取购物车
    @GetMapping("/cart/{userId}")
    public Result getCart(@PathVariable Integer userId){
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
    public Result createOrder(HttpServletRequest request, @RequestBody OrderCreateRequest orderRequest){
        log.info("创建订单，订单数据: {}", orderRequest);
        return cartService.createOrder(request, orderRequest);
    }

    // 获取订单列表
    @GetMapping("/order/list")
    public Result getOrderList(HttpServletRequest request,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("获取订单列表，页码: {}, 每页数量: {}", page, pageSize);
        return cartService.getOrderList(request, page, pageSize);
    }
}
