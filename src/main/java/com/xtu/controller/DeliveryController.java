package com.xtu.controller;

import com.xtu.service.CartService;
import com.xtu.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class DeliveryController {

    @Autowired
    private CartService cartService;

    @GetMapping("/delivery/order/list/{deliveryId}")
    public Result list(@PathVariable Integer deliveryId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("list()");
        return cartService.getDeliveryOrderList(deliveryId, null, page, pageSize);
    }

}
