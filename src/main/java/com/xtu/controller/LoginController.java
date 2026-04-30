package com.xtu.controller;

import com.xtu.pojo.User;
import com.xtu.pojo.UserInfoVO;
import com.xtu.service.UserService;
import com.xtu.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class LoginController {
    @Autowired
    private UserService userService;
    @PostMapping("/auth/login")
    public Result login(@RequestBody User user){
        return userService.login(user);
    }
}
