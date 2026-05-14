package com.xtu.controller;

import com.xtu.pojo.Product;
import com.xtu.pojo.ProductInfo;
import com.xtu.pojo.ProductStatusRequest;
import com.xtu.service.ProductService;
import com.xtu.service.UserService;
import com.xtu.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // 获取所有商品分页
    @GetMapping("/products")
    public Result getAllProducts(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        return productService.getAllProducts(page, pageSize);
    }

    // 搜索商品列表
    @GetMapping("/products/list")
    public Result getProductsByKeywordsAndSpecification(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                        @ModelAttribute ProductInfo productInfo){
        return productService.getProductsByPage(page, pageSize, productInfo);
    }

    // 修改商品状态
    @PostMapping("/products/status/{id}")
    public Result updateStatus(@PathVariable Integer id, @RequestBody ProductStatusRequest productStatusRequest) {
        log.info("修改商品状态，id: {}, status: {}", id, productStatusRequest);
        return productService.updateStatusById(id, productStatusRequest.getStatus());
    }

    // 删除商品
    @DeleteMapping("/products/{id}")
    public Result deleteProduct(@PathVariable Integer id) {
        return productService.deleteProductById(id);
    }

    // 添加商品
    @PostMapping("/products/add")
    public Result addProduct(@RequestBody Product product) {
        log.info("添加商品，product: {}", product);
        return productService.addProduct(product);
    }

    // 获取所有用户分页
    @GetMapping("/user")
    public Result getAllUsers(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return userService.getAllUser(page, pageSize);
    }
}
