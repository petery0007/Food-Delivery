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

    @GetMapping()
    public Result getAllProducts(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        return productService.getAllProducts(page, pageSize);
    }

    @GetMapping("/list")
    public Result getProductsByKeywordsAndSpecification(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @ModelAttribute ProductInfo productInfo){
        return productService.getProductsByPage(page, pageSize, productInfo);
    }

    @PostMapping("/status/{id}")
    public Result updateStatus(@PathVariable Integer id, @RequestBody ProductStatusRequest productStatusRequest) {
        log.info("修改商品状态，id: {}, status: {}", id, productStatusRequest);
        return productService.updateStatusById(id, productStatusRequest.getStatus());
    }

    @DeleteMapping("/{id}")
    public Result deleteProduct(@PathVariable Integer id) {
        return productService.deleteProductById(id);
    }

    @PostMapping("/add")
    public Result addProduct(@RequestBody Product product) {
        log.info("添加商品，product: {}", product);
        return productService.addProduct(product);
    }
}
