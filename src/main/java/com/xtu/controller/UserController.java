package com.xtu.controller;

import com.xtu.pojo.PasswordUpdateRequest;
import com.xtu.pojo.User;
import com.xtu.service.UserService;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;

    @PutMapping("/user/info")
    public Result updateUser(HttpServletRequest request, @RequestBody User user){
        return userService.updateUser(request ,user);
    }

    @PutMapping("/user/password")
    public Result updatePassword(HttpServletRequest request,
                                 @RequestBody PasswordUpdateRequest passwordUpdateRequest){
        return userService.updatePassword(request , passwordUpdateRequest);
    }
}
