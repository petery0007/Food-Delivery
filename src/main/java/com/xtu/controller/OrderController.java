package com.xtu.controller;

import com.xtu.pojo.OrderCreateRequest;
import com.xtu.service.CartService;
import com.xtu.service.OrderService;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //提交订单
    @PostMapping("/user/order/create")
    public Result createOrder(HttpServletRequest request, @RequestBody OrderCreateRequest orderRequest){
        log.info("创建订单，订单数据: {}", orderRequest);
        return orderService.createOrder(request, orderRequest);
    }

    // 获取订单列表
    @GetMapping("/user/order/list")
    public Result getOrderList(HttpServletRequest request,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("获取订单列表，页码: {}, 每页数量: {}", page, pageSize);
        return orderService.getOrderList(request, page, pageSize);
    }

    // 支付订单
    @PostMapping("/user/order/pay/{orderId}")
    public Result payOrder(HttpServletRequest request,@PathVariable Integer orderId){
        log.info("支付订单，订单id: {}", orderId);
        return orderService.payOrder(request, orderId);
    }

    // 取消订单
    @PutMapping("/user/order/cancel/{orderId}")
    public Result cancelOrder(@PathVariable Integer orderId){
        return orderService.cancelOrder(orderId);
    }

    // 删除订单
    @DeleteMapping("/user/order/delete/{orderId}")
    public Result deleteOrder(@PathVariable Integer orderId){
        return orderService.deleteOrder(orderId);
    }

    // 统一订单状态更新接口（配送员和用户共用）
    @PutMapping("/order/update/{orderId}")
    public Result updateOrderStatus(HttpServletRequest request, @PathVariable Integer orderId){
        log.info("更新订单状态，订单id: {}", orderId);
        return orderService.updateOrderStatus(request, orderId);
    }

}
