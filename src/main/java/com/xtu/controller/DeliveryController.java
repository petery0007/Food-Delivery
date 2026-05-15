package com.xtu.controller;

import com.xtu.service.CartService;
import com.xtu.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    @Autowired
    private CartService cartService;

    @GetMapping("/order/list")
    public Result getDeliveryOrderList(@RequestParam String deliveryId,
                                       @RequestParam(required = false) Integer status,
                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("配送员查询订单列表，deliveryId: {}, status: {}", deliveryId, status);
        return cartService.getDeliveryOrderList(deliveryId, status, pageNum, pageSize);
    }
}
