package com.xtu.service.impl;

import com.xtu.mapper.CartMapper;
import com.xtu.mapper.OrderMapper;
import com.xtu.mapper.ProductMapper;
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


    // 提交订单
    @Override
    @Transactional
    public Result createOrder(HttpServletRequest request, OrderCreateRequest orderRequest) {
        log.info("========== 创建订单开始 ==========");
        log.info("订单数据: {}", orderRequest);

        try {
            String token = request.getHeader("token");
            if (token == null || token.isEmpty()) {
                return Result.error(401, "未登录，请先登录");
            }

            Claims claims = JwtUtils.parseToken(token);
            Integer userId = (Integer) claims.get("id");
            log.info("当前用户ID: {}", userId);

            if (!userId.equals(orderRequest.getUserId())) {
                log.warn("用户ID不匹配，请求用户ID: {}, token用户ID: {}", orderRequest.getUserId(), userId);
                return Result.error(401, "用户身份验证失败");
            }

            List<OrderItem> items = orderRequest.getItems();
            if (items == null || items.isEmpty()) {
                return Result.error(400, "订单商品不能为空");
            }

            BigDecimal totalAmount = orderRequest.getTotalAmount();
            if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return Result.error(400, "订单金额异常");
            }

            log.info("开始验证库存...");
            for (OrderItem item : items) {
                Product product = productMapper.getProductById(item.getProductId());
                if (product == null) {
                    return Result.error(400, "商品不存在: " + item.getProductName());
                }

                if (product.getStock() < item.getQuantity()) {
                    return Result.error(400, "商品库存不足: " + item.getProductName()
                            + "，当前库存: " + product.getStock());
                }
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setReceiver(orderRequest.getReceiver());
            order.setPhone(orderRequest.getPhone());
            order.setAddress(orderRequest.getAddress());
            order.setDeliveryType(orderRequest.getDeliveryType());
            order.setPaymentType(orderRequest.getPaymentType());
            order.setRemark(orderRequest.getRemark());
            order.setGoodsTotal(orderRequest.getGoodsTotal());
            order.setDeliveryFee(orderRequest.getDeliveryFee());
            order.setTotalAmount(totalAmount);
            order.setStatus(0);
            order.setCreateTime(LocalDateTime.now());

            int insertOrderResult = orderMapper.insertOrder(order);
            if (insertOrderResult <= 0) {
                throw new RuntimeException("订单主表插入失败");
            }

            Integer orderId = order.getId();
            log.info("订单主表插入成功，订单ID: {}", orderId);

            for (OrderItem item : items) {
                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setOrderId(orderId);
                orderItem.setProductId(item.getProductId());
                orderItem.setProductName(item.getProductName());
                orderItem.setPrice(item.getPrice());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setSpecification(item.getSpecification());
                orderItem.setImageUrl(item.getImageUrl());
                orderItem.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));

                int insertItemResult = orderMapper.insertOrderItem(orderItem);
                if (insertItemResult <= 0) {
                    throw new RuntimeException("订单明细插入失败: " + item.getProductName());
                }

                log.info("✓ 订单明细插入成功: {}", item.getProductName());
            }

            log.info("开始扣减库存...");
            for (OrderItem item : items) {
                int updateStock = productMapper.updateStock(item.getProductId(), item.getQuantity());
                if (updateStock <= 0) {
                    throw new RuntimeException("库存扣减失败: " + item.getProductName());
                }
                log.info("✓ 商品 [{}] 库存扣减成功，扣减数量: {}", item.getProductName(), item.getQuantity());
            }

            if ("balance".equals(orderRequest.getPaymentType())) {
                log.info("使用余额支付，开始验证余额...");
                Map<String, Object> userInfo = cartMapper.getUserInfo(userId);
                if (userInfo == null) {
                    return Result.error(400, "用户信息不存在");
                }

                BigDecimal balance = (BigDecimal) userInfo.get("money");
                if (balance == null) {
                    balance = BigDecimal.ZERO;
                }

                log.info("用户当前余额: {}, 订单金额: {}", balance, totalAmount);

                if (balance.compareTo(totalAmount) < 0) {
                    return Result.error(400, "余额不足，当前余额: ¥" + balance
                            + "，需要: ¥" + totalAmount);
                }

                int updateMoney = cartMapper.updateUserMoney(userId, totalAmount.negate());
                if (updateMoney <= 0) {
                    throw new RuntimeException("余额扣除失败");
                }

                log.info("✓ 余额支付成功，扣除金额: {}", totalAmount);
            } else if ("cod".equals(orderRequest.getPaymentType())) {
                log.info("选择货到付款，无需扣款");
            }

            log.info("========== 订单创建成功 ==========");
            log.info("订单ID: {}", orderId);
            log.info("收货人: {}", orderRequest.getReceiver());
            log.info("联系电话: {}", orderRequest.getPhone());
            log.info("收货地址: {}", orderRequest.getAddress());
            log.info("配送方式: {}", orderRequest.getDeliveryType());
            log.info("支付方式: {}", orderRequest.getPaymentType());
            log.info("订单金额: ¥{}", totalAmount);
            log.info("商品数量: {} 件", items.size());

            Map<String, Object> data = new HashMap<>();
            data.put("orderId", orderId);
            data.put("message", "订单创建成功");
            data.put("totalAmount", totalAmount);

            return Result.success(200, "订单创建成功", data);

        } catch (Exception e) {
            log.error("========== 订单创建失败 ==========", e);
            log.error("错误信息: {}", e.getMessage());
            throw new RuntimeException("订单创建失败: " + e.getMessage());
        }
    }

    @Override
    public Result getOrderList(HttpServletRequest request, Integer page, Integer pageSize) {
        try {
            String token = request.getHeader("token");
            Claims claims = JwtUtils.parseToken(token);
            Integer userId = (Integer) claims.get("id");

            if (page == null || page < 1) {
                page = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;
            }

            Integer offset = (page - 1) * pageSize;

            List<Order> orders = orderMapper.getOrdersByUserIdPage(userId, offset, pageSize);

            Integer total = orderMapper.countOrdersByUserId(userId);

            for (Order order : orders) {
                List<OrderItemEntity> items = orderMapper.getOrderItemsByOrderId(order.getId());
                order.setItems(items);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", orders);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("pages", (total + pageSize - 1) / pageSize);

            return Result.success(200, "获取成功", data);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return Result.error(500, "获取订单列表失败: " + e.getMessage());
        }
    }
}
