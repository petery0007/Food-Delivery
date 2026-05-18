package com.xtu.service.impl;

import com.xtu.mapper.CartMapper;
import com.xtu.mapper.OrderMapper;
import com.xtu.mapper.ProductMapper;
import com.xtu.mapper.UserMapper;
import com.xtu.pojo.*;
import com.xtu.service.OrderService;
import com.xtu.utils.JwtUtils;
import com.xtu.utils.Result;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

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

            log.info("开始删除购物车中的商品...");
            for (OrderItem item : items) {
                cartMapper.deleteCartItemByUserIdAndProductId(userId, item.getProductId());
                log.info("✓ 删除购物车商品: {}", item.getProductName());
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
    public Result getOrderList(HttpServletRequest request, Integer page, Integer pageSize, Integer status) {
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

            List<Order> orders;
            Integer total;

            if (status != null) {
                orders = orderMapper.getOrdersByUserIdWithStatusPage(userId, status, offset, pageSize);
                total = orderMapper.countOrdersByUserIdWithStatus(userId, status);
            } else {
                orders = orderMapper.getOrdersByUserIdPage(userId, offset, pageSize);
                total = orderMapper.countOrdersByUserId(userId);
            }

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

    @Override
    public Result payOrder(HttpServletRequest request, Integer orderId) {
        try {
            Order order = orderMapper.getOrderById(orderId);
            if ("balance".equals(order.getPaymentType())) {
                log.info("使用余额支付，开始验证余额...");
                String token = request.getHeader("token");
                Claims claims = JwtUtils.parseToken(token);
                Integer userId = (Integer) claims.get("id");
                Map<String, Object> userInfo = cartMapper.getUserInfo(userId);
                if (userInfo == null) {
                    return Result.error(400, "用户信息不存在");
                }

                BigDecimal balance = (BigDecimal) userInfo.get("money");
                if (balance == null) {
                    balance = BigDecimal.ZERO;
                }
                BigDecimal totalAmount = order.getTotalAmount();
                log.info("用户当前余额: {}, 订单金额: {}", balance, order.getTotalAmount());

                if (balance.compareTo(totalAmount) < 0) {
                    return Result.error(400, "余额不足，当前余额: ¥" + balance
                            + "，需要: ¥" + totalAmount);
                }

                int updateMoney = cartMapper.updateUserMoney(userId, totalAmount.negate());
                if (updateMoney <= 0) {
                    throw new RuntimeException("余额扣除失败");
                }
                log.info("✓ 余额支付成功，扣除金额: {}", totalAmount);

                // 分配配送员
                List<User> idleDeliveryUsers = userMapper.selectIdleDeliveryUsers();
                if (idleDeliveryUsers == null || idleDeliveryUsers.isEmpty()) {
                    return Result.error(400, "当前没有空闲的配送员");
                }

                Random random = new Random();
                User selectedDelivery = idleDeliveryUsers.get(random.nextInt(idleDeliveryUsers.size()));

                userMapper.updateDeliveryStatus(selectedDelivery.getId(), "配送中");

                orderMapper.assignDelivery(orderId, selectedDelivery.getUsername(), 1);

                log.info("订单 {} 分配配送员成功: {}", order.getId(), selectedDelivery.getUsername());


                return Result.success(200, "支付成功");
            } else if ("cod".equals(order.getPaymentType())) {
                log.info("选择货到付款，无需扣款");
                // 分配配送员
                List<User> idleDeliveryUsers = userMapper.selectIdleDeliveryUsers();
                if (idleDeliveryUsers == null || idleDeliveryUsers.isEmpty()) {
                    return Result.error(400, "当前没有空闲的配送员");
                }

                Random random = new Random();
                User selectedDelivery = idleDeliveryUsers.get(random.nextInt(idleDeliveryUsers.size()));

                userMapper.updateDeliveryStatus(selectedDelivery.getId(), "配送中");

                orderMapper.assignDelivery(orderId, selectedDelivery.getUsername(), 1);

                log.info("订单 {} 分配配送员成功: {}", order.getId(), selectedDelivery.getUsername());

                orderMapper.updateOrderStatus(orderId, 1);
                return Result.success(200, "选择货到付款，无需扣款");
            }
        } catch (Exception e) {
            log.error("支付订单失败", e);
            return Result.error(500, "支付订单失败: " + e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public Result cancelOrder(Integer orderId) {
        log.info("开始取消订单 {}", orderId);

        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        if (order.getStatus() != 0 && order.getStatus() != 1) {
            return Result.error(400, "只能取消待支付或配送中的订单");
        }

        List<OrderItemEntity> items = orderMapper.getOrderItemsByOrderId(orderId);


        // 还原库存
        for (OrderItemEntity item : items) {
            productMapper.restoreStock(item.getProductId(), item.getQuantity());
        }

//        if ("balance".equals(order.getPaymentType())) {
//            cartMapper.updateUserMoney(order.getUserId(), order.getTotalAmount());
//            log.info("订单取消，退还余额: {}", order.getTotalAmount());
//        }

        if (order.getDeliveryStaff() != null && !order.getDeliveryStaff().isEmpty()) {
            Integer deliveryUserId = Integer.parseInt(order.getDeliveryStaff());
            userMapper.updateDeliveryStatus(deliveryUserId, "空闲");
            log.info("订单取消，释放配送员: {}", deliveryUserId);
        }

        orderMapper.cancelOrder(orderId, 4, LocalDateTime.now());

        log.info("订单 {} 取消成功", orderId);

        return Result.success(200, "订单已取消");
    }

    @Override
    @Transactional
    public Result deleteOrder(Integer orderId) {
        log.info("开始删除订单 {}", orderId);

        Order order = orderMapper.getOrderById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        if (order.getStatus() != 3 && order.getStatus() != 4) {
            return Result.error(400, "只能删除已完成或已取消的订单");
        }

        orderMapper.deleteOrderItems(orderId);

        orderMapper.deleteOrder(orderId);

        log.info("订单 {} 删除成功", orderId);

        return Result.success(200, "订单已删除");
    }

    @Override
    @Transactional
    public Result updateOrderStatus(HttpServletRequest request, Integer orderId) {
        log.info("========== 更新订单状态开始 ==========");
        log.info("订单ID: {}", orderId);

        try {
            String token = request.getHeader("token");
            if (token == null || token.isEmpty()) {
                return Result.error(401, "未登录，请先登录");
            }

            Claims claims = JwtUtils.parseToken(token);
            Integer currentUserId = (Integer) claims.get("id");
            log.info("当前操作用户ID: {}", currentUserId);

            Order order = orderMapper.getOrderById(orderId);
            if (order == null) {
                return Result.error(404, "订单不存在");
            }

            Integer currentStatus = order.getStatus();
            log.info("订单当前状态: {}", currentStatus);

            if (currentStatus == 1) {
                return handleDeliveryConfirm(request, orderId, currentUserId);
            } else if (currentStatus == 2) {
                return handleUserConfirmReceive(request, orderId, currentUserId);
            } else {
                return Result.error(400, "订单状态不允许此操作，当前状态: " + currentStatus);
            }

        } catch (Exception e) {
            log.error("========== 更新订单状态失败 ==========", e);
            log.error("错误信息: {}", e.getMessage());
            return Result.error(500, "更新订单状态失败: " + e.getMessage());
        }
    }

    private Result handleDeliveryConfirm(HttpServletRequest request, Integer orderId, Integer currentUserId) {
        log.info("处理配送员确认送达");

        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null || !"PEISONG".equals(currentUser.getRole())) {
            return Result.error(403, "只有配送员可以执行此操作");
        }

        Order order = orderMapper.getOrderById(orderId);
        if (order.getDeliveryStaff() != null && !order.getDeliveryStaff().isEmpty()) {
            Integer assignedDeliveryId = userMapper.selectUserIdByUsername(order.getDeliveryStaff());
            if (!assignedDeliveryId.equals(currentUserId)) {
                return Result.error(403, "只能确认自己配送的订单");
            }
        }

        orderMapper.updateOrderStatus(orderId, 2);

        log.info("配送员确认送达成功，订单ID: {}", orderId);

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("message", "确认送达成功");

        return Result.success(200, "确认送达成功", data);
    }

    private Result handleUserConfirmReceive(HttpServletRequest request, Integer orderId, Integer currentUserId) {
        log.info("处理用户确认收货");

        Order order = orderMapper.getOrderById(orderId);
        if (!order.getUserId().equals(currentUserId)) {
            return Result.error(403, "无权操作此订单");
        }

        LocalDateTime finishTime = LocalDateTime.now();
        orderMapper.updateOrderStatusAndFinishTime(orderId, 3, finishTime);

        if (order.getDeliveryStaff() != null && !order.getDeliveryStaff().isEmpty()) {
            Integer deliveryUserId = userMapper.selectUserIdByUsername(order.getDeliveryStaff());
            if (deliveryUserId != null) {
                userMapper.updateDeliveryStatus(deliveryUserId, "空闲");
                log.info("确认收货后释放配送员: {}", order.getDeliveryStaff());
            }
        }

        log.info("用户确认收货成功，订单ID: {}", orderId);

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("finishTime", finishTime);
        data.put("message", "确认收货成功");

        return Result.success(200, "确认收货成功", data);
    }

    @Override
    public Result getAllOrders(Integer page, Integer pageSize, Integer status, String receiver, String deliveryStaff) {
        log.info("========== 管理员查询订单开始 ==========");
        log.info("页码: {}, 每页数量: {}, 状态: {}, 收货人: {}, 配送员: {}",
                page, pageSize, status, receiver, deliveryStaff);

        try {
            if (page == null || page < 1) {
                page = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;
            }

            Integer offset = (page - 1) * pageSize;

            // 分页查询订单列表
            List<Order> orders = orderMapper.getAllOrdersPage(offset, pageSize, status, receiver, deliveryStaff);

            // 查询总记录数
            Integer total = orderMapper.countAllOrders(status, receiver, deliveryStaff);

            // 为每个订单加载商品明细
            for (Order order : orders) {
                List<OrderItemEntity> items = orderMapper.getOrderItemsByOrderId(order.getId());
                order.setItems(items);
            }

            log.info("查询到订单总数: {}, 当前页订单数: {}", total, orders.size());

            Map<String, Object> data = new HashMap<>();
            data.put("list", orders);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("pages", (total + pageSize - 1) / pageSize);

            return Result.success(200, "获取成功", data);

        } catch (Exception e) {
            log.error("========== 管理员查询订单失败 ==========", e);
            log.error("错误信息: {}", e.getMessage());
            return Result.error(500, "获取订单列表失败: " + e.getMessage());
        }
    }
}
