package com.xtu.controller;

import com.xtu.pojo.Product;
import com.xtu.pojo.ProductInfo;
import com.xtu.pojo.ProductStatusRequest;
import com.xtu.pojo.UserInfo2;
import com.xtu.service.OrderService;
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

    @Autowired
    private OrderService orderService;

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

    // 搜索用户列表
    @GetMapping("/user/list")
    public Result getusersByKeywords(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                        @ModelAttribute UserInfo2 userInfo2){
        return userService.getUsersByKeywords(page, pageSize, userInfo2);
    }

    // 获取所有配送员分页
    @GetMapping("/peisong")
    public Result getAllPeisong(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return userService.getAllPeisong(page, pageSize);
    }

    // 搜索配送员列表（支持姓名和手机号条件查询）
    @GetMapping("/peisong/list")
    public Result getPeisongByKeywords(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String username,
                                       @RequestParam(required = false) String phone){
        log.info("搜索配送员，page: {}, pageSize: {}, username: {}, phone: {}",
                page, pageSize, username, phone);
        return userService.getPeisongByKeywords(page, pageSize, username, phone);
    }

    // 获取所有订单（支持分页和多条件搜索）
    @GetMapping("/orders")
    public Result getAllOrders(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) Integer status,
                               @RequestParam(required = false) String receiver,
                               @RequestParam(required = false) String deliveryStaff) {
        log.info("管理员查询订单，page: {}, pageSize: {}, status: {}, receiver: {}, deliveryStaff: {}",
                page, pageSize, status, receiver, deliveryStaff);
        return orderService.getAllOrders(page, pageSize, status, receiver, deliveryStaff);
    }


}
