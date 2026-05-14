package com.xtu.controller;

import com.xtu.pojo.PasswordUpdateRequest;
import com.xtu.pojo.ProductInfo;
import com.xtu.pojo.User;
import com.xtu.pojo.UserAmount;
import com.xtu.service.UserService;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;

    // 获取用户信息
    @GetMapping("/user/info")
    public Result getUserInfo(HttpServletRequest request){
        return userService.getUserInfo(request);
    }

    // 更新用户信息
    @PutMapping("/user/info")
    public Result updateUser(HttpServletRequest request, @RequestBody User user){
        return userService.updateUser(request ,user);
    }

    // 修改密码
    @PutMapping("/user/password")
    public Result updatePassword(HttpServletRequest request,
                                 @RequestBody PasswordUpdateRequest passwordUpdateRequest){
        return userService.updatePassword(request , passwordUpdateRequest);
    }

    // 充值
    @PostMapping("/user/recharge")
    public Result recharge(HttpServletRequest request,
                           @RequestBody UserAmount userAmount){
        return userService.updateUserMoney(request ,userAmount.getAmount());
    }

    // 获取用户商品
    @GetMapping("/user/products")
    public Result getUserProducts(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize){
        return userService.getAllProducts(page, pageSize);
    }

    // 获取用户商品列表
    @GetMapping("/user/products/list")
    public Result getUserProductsList(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @ModelAttribute ProductInfo productInfo){
        return userService.getProductsByKeywordsAndSpecification(page, pageSize, productInfo);
    }
}
