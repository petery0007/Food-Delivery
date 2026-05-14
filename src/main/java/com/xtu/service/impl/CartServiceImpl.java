package com.xtu.service.impl;

import com.xtu.mapper.CartMapper;
import com.xtu.mapper.ProductMapper;
import com.xtu.pojo.Cart;
import com.xtu.pojo.Product;
import com.xtu.service.CartService;
import com.xtu.utils.JwtUtils;
import com.xtu.utils.Result;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result addCart(HttpServletRequest request,  Cart cart) {
        log.info("添加购物车");
        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseToken(token);
        Integer id = (Integer) claims.get("id");
        int check = cartMapper.check(id, cart.getProductId());
        if(check != 0){
            return Result.error(401, "商品已存在,请前往购物车添加");
        }
        cart.setUserId(id);
        Product p = productMapper.getProductById(cart.getProductId());
        if(p.getStock() < cart.getQuantity()){
            return Result.error(401, "库存不足");
        }
        cart.setPrice(p.getPrice());
        cart.setImageUrl(p.getImageUrl());
        cart.setSpecification(p.getSpecification());
        cartMapper.addCart(cart);
        return Result.success(200, "加入购物车成功");
    }

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
