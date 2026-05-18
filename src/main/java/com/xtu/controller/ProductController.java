package com.xtu.controller;

import com.xtu.pojo.*;
import com.xtu.service.ProductService;
import com.xtu.service.UserService;
import com.xtu.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public Result getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    @GetMapping("/{id}/reviews")
    public Result getProductReviews(@PathVariable Integer id,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return productService.getProductReviews(id, page, pageSize);
    }


}
