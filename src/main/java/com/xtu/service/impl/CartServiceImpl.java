package com.xtu.service.impl;

import com.xtu.mapper.CartMapper;
import com.xtu.pojo.Cart;
import com.xtu.service.CartService;
import com.xtu.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public Result getCart(Integer userId) {
        log.info("获取购物车");
        Cart cart = cartMapper.getCart(userId);
        return Result.success(200, "获取成功", cart);
    }

    @Override
    public Result updateCart(Integer id, Integer quantity) {
        log.info("更新购物车商品");
        cartMapper.updateCart(id, quantity);
        log.info("更新成功");
        return Result.success(200, "数量已更新");
    }

    @Override
    public Result deleteCart(Integer id) {
        log.info("删除购物车商品");
        cartMapper.deleteCart(id);
        log.info("删除成功");
        return Result.success(200, "删除成功");
    }
}
