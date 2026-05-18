package com.xtu.service.impl;

import com.xtu.mapper.CartMapper;
import com.xtu.mapper.OrderMapper;
import com.xtu.mapper.ProductMapper;
import com.xtu.mapper.UserMapper;
import com.xtu.pojo.*;
import com.xtu.service.CartService;
import com.xtu.utils.JwtUtils;
import com.xtu.utils.Result;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result addCart(HttpServletRequest request,  Cart cart) {
        log.info("添加购物车");
        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseToken(token);
        Integer id = (Integer) claims.get("id");
        int check = cartMapper.check(id, cart.getProductId());
        if(check != 0){
            return Result.error(400, "商品已存在,请前往购物车添加");
        }
        cart.setUserId(id);
        Product p = productMapper.getProductById(cart.getProductId());
        if(p.getStock() < cart.getQuantity()){
            return Result.error(400, "库存不足");
        }
        cart.setPrice(p.getPrice());
        cart.setImageUrl(p.getImageUrl());
        cart.setSpecification(p.getSpecification());
        cart.setProductName(p.getName());
        cartMapper.addCart(cart);
        return Result.success(200, "加入购物车成功");
    }



    @Override
    public Result getCart(Integer userId) {
        log.info("获取购物车");
        List<Cart> cartList = cartMapper.getCart(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("list", cartList);
        data.put("total", cartList.size());
        return Result.success(200, "获取成功", data);
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




    @Override
    public Result getDeliveryOrderList(Integer deliveryId, Integer status, Integer pageNum, Integer pageSize) {
        log.info("配送员 {} 查询订单列表，状态: {}, 页码: {}", deliveryId, status, pageNum);

        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Integer offset = (pageNum - 1) * pageSize;

        List<Order> orders;
        Integer total;
        User deliveryUser = userMapper.selectById(deliveryId);
        String deliveryName = deliveryUser.getUsername();
        if (status == null) {
            orders = orderMapper.getOrdersByDeliveryIdPage(deliveryName, offset, pageSize);
            total = orderMapper.countOrdersByDeliveryId(deliveryName);
        } else {
            orders = orderMapper.getOrdersByDeliveryIdAndStatusPage(deliveryName, status, offset, pageSize);
            total = orderMapper.countOrdersByDeliveryIdAndStatus(deliveryName, status);
        }

        for (Order order : orders) {
            List<OrderItemEntity> items = orderMapper.getOrderItemsByOrderId(order.getId());
            order.setItems(items);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", orders);
        data.put("total", total);
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        data.put("pages", (total + pageSize - 1) / pageSize);

        return Result.success(200, "获取成功", data);
    }

}
