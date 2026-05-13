package com.xtu.controller;

import com.xtu.pojo.ProductInfo;
import com.xtu.pojo.User;
import com.xtu.pojo.UserInfoVO;
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

    @GetMapping()
    public Result getAllProducts(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        return productService.getAllProducts(page, pageSize);
    }

    @GetMapping("/list")
    public Result getProductsByKeywordsAndSpecification(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestBody ProductInfo productInfo){
        return productService.getProductsByPage(page, pageSize, productInfo);
    }
}
