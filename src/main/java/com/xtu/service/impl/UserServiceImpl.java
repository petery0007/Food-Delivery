package com.xtu.service.impl;

import com.xtu.mapper.UserMapper;
import com.xtu.pojo.PasswordUpdateRequest;
import com.xtu.pojo.User;
import com.xtu.pojo.UserInfo;
import com.xtu.pojo.UserInfoVO;
import com.xtu.service.UserService;
import com.xtu.utils.JwtUtils;
import com.xtu.utils.Result;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public Result register(User user) {
        //判断用户名是否存在
        User exist = userMapper.findByUsername(user.getUsername());
        log.info("用户名：{}",user.getUsername());
        if (exist != null) {
            return Result.error(400, "用户名已存在");
        }
        //校验手机号是否已存在（修复的核心代码！）
        User existPhone = userMapper.selectByPhone(user.getPhone());
        if (existPhone != null) {
            return Result.error(400, "手机号已被注册");
        }
        //插入数据库
        userMapper.insert(user);

        return Result.success(200, "注册成功");
    }

    @Override
    public Result login(User dto) {
        //判断用户名是否为空
        if(dto.getUsername() == null || dto.getUsername().equals("")){
            return Result.error(400, "用户名不能为空");
        }
        //判断用户名是否存在
        User user = userMapper.findByUsername(dto.getUsername());
        if(user == null){
            return Result.error(400, "用户名不存在");
        }
        //判断密码是否正确
        if(!user.getPassword().equals(dto.getPassword())){
            return Result.error(400, "密码错误");
        }
        //生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        String token = JwtUtils.generateToken(claims);

        //封装
        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), user.getRole(), user.getMoney());
        UserInfoVO userInfoVO = new UserInfoVO(userInfo, token);
        return Result.success(200, "登录成功", userInfoVO);
    }

    @Override
    public Result updateUser(HttpServletRequest request, User user) {
        log.info("修改用户信息");
        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseToken(token);
        Integer id = (Integer) claims.get("id");

        User dto = userMapper.selectById(id);
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        userMapper.updateInfoById(dto);
        return Result.success(200, "修改成功");
    }

    @Override
    public Result updatePassword(HttpServletRequest request,
                                 PasswordUpdateRequest passwordUpdateRequest) {
        log.info("修改用户密码");
        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseToken(token);
        Integer id = (Integer) claims.get("id");
        User dto = userMapper.selectById(id);
        if(!dto.getPassword().equals(passwordUpdateRequest.getOldPassword())){
            return Result.error(400, "旧密码错误");
        }
        dto.setPassword(passwordUpdateRequest.getNewPassword());
        userMapper.updatePasswordById(dto);
        return Result.success(200, "修改成功");
    }
}
