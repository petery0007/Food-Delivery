package com.xtu.service.impl;

import com.xtu.mapper.ProductMapper;
import com.xtu.mapper.UserMapper;
import com.xtu.pojo.*;
import com.xtu.service.UserService;
import com.xtu.utils.JwtUtils;
import com.xtu.utils.Result;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;
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
        //判断角色是否正确
        if(!user.getRole().equals(dto.getRole())){
            return Result.error(400, "角色不匹配");
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

    @Override
    public Result getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseToken(token);
        Integer id = (Integer) claims.get("id");
        User dto = userMapper.selectById(id);
        UserInfo2 userInfo = new UserInfo2(dto.getId(), dto.getUsername(), dto.getRole(), dto.getMoney(),dto.getPhone());
        return Result.success(200, "获取成功", userInfo);
    }

    @Override
    public Result updateUserMoney(HttpServletRequest request, BigDecimal amount) {
        String token = request.getHeader("token");
        Claims claims = JwtUtils.parseToken(token);
        Integer id = (Integer) claims.get("id");
        userMapper.updateUserMoney(id, amount);
        return Result.success(200, "充值成功");
    }

    @Override
    public Result getAllProducts(Integer page, Integer pageSize) {
        log.info("获取商品列表，页码: {}, 每页数量: {}", page, pageSize);
        Integer offset = (page - 1) * pageSize;
        List<Product> products = productMapper.selectByPageShangjia(offset, pageSize);
        Integer total = productMapper.countProductsShangjia();
        Map<String, Object> data = new HashMap<>();
        data.put("list", products);
        data.put("total", total);
        return Result.success(200, "获取成功", data);
    }

    @Override
    public Result getProductsByKeywordsAndSpecification(Integer page, Integer pageSize, ProductInfo productInfo) {
        log.info("获取商品列表，页码: {}, 每页数量: {}", page, pageSize);

        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        int offset = (page - 1) * pageSize;

        if(productInfo == null){
            List<Product> products = productMapper.selectByPageShangjia(offset, pageSize);
            Integer total = productMapper.countProductsShangjia();

            Map<String, Object> data = new HashMap<>();
            data.put("list", products);
            data.put("total", total);

            return Result.success(200, "获取成功", data);
        }
        else if(productInfo.getKeywords() == null){
            List<Product> products = productMapper.selectBySpecificationShangjia(offset, pageSize, productInfo.getSpecification());
            Integer total = productMapper.countProductsBySpecificationShangjia(productInfo.getSpecification());
            if(total == null){
                return Result.error(401, "查询商品不存在");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("list", products);
            data.put("total", total);
            return Result.success(200, "获取成功", data);
        } else if (productInfo.getSpecification() == null) {
            List<Product> products = productMapper.selectByKeywordsShangjia(offset, pageSize, productInfo.getKeywords());
            Integer total = productMapper.countProductsByKeywordsShangjia(productInfo.getKeywords());
            if(total == null){
                return Result.error(401, "查询商品不存在");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("list", products);
            data.put("total", total);
            return Result.success(200, "获取成功", data);

        }



        List<Product> products = productMapper.selectByKeywordsAndSpecificationShangjia(offset, pageSize, productInfo.getSpecification(), productInfo.getKeywords());
        Integer total = productMapper.countProductsByKeywordsAndSpecificationShangjia(productInfo.getSpecification(), productInfo.getKeywords());
        if(total == null){
            return Result.error(401, "查询商品不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", products);
        data.put("total", total);

        return Result.success(200, "获取成功", data);
    }

    @Override
    public Result getAllUser(Integer page, Integer pageSize) {
        log.info("获取用户列表，页码: {}, 每页数量: {}", page);
        Integer offset = (page - 1) * pageSize;
        List<User> users = userMapper.selectAllUserByPage(offset, pageSize);
        Integer total = users.size();
        Map<String, Object> data = new HashMap<>();
        data.put("list", users);
        data.put("total", total);
        return Result.success(200, "获取成功", data);
    }

    @Override
    public Result getUsersByKeywords(Integer page, Integer pageSize, UserInfo2 userInfo2) {
        log.info("获取用户列表，页码: {}, 每页数量: {}", page, pageSize);

        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        int offset = (page - 1) * pageSize;

        if(userInfo2 == null){
            List<User> users = userMapper.selectAllUserByPage(offset, pageSize);
            Integer total = users.size();

            Map<String, Object> data = new HashMap<>();
            data.put("list", users);
            data.put("total", total);

            return Result.success(200, "获取成功", data);
        }
        else if(userInfo2.getUsername() == null){
            List<User> users = userMapper.selectUserByPhone(offset, pageSize, userInfo2.getPhone());
            int total = users.size();
            if(total == 0){
                return Result.error(401, "查询商品不存在");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("list", users);
            data.put("total", total);
            return Result.success(200, "获取成功", data);
        }
        else if (userInfo2.getPhone() == null) {
            List<User> users = userMapper.selectUserByUsername(offset, pageSize, userInfo2.getUsername());
            int total = users.size();
            if(total == 0){
                return Result.error(401, "查询商品不存在");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("list", users);
            data.put("total", total);
            return Result.success(200, "获取成功", data);

        }


        List<User> users = userMapper.selectUserByUsernameAndPhone(offset, pageSize, userInfo2.getUsername(), userInfo2.getPhone());
        int total = users.size();
        if(total == 0){
            return Result.error(401, "查询商品不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", users);
        data.put("total", total);

        return Result.success(200, "获取成功", data);
    }

    @Override
    public Result getAllPeisong(Integer page, Integer pageSize) {
        log.info("获取配送员列表，页码: {}, 每页数量: {}", page);
        Integer offset = (page - 1) * pageSize;
        List<User> peisong = userMapper.selectAllPeisongByPage(offset, pageSize);
        Integer total = peisong.size();
        Map<String, Object> data = new HashMap<>();
        data.put("list", peisong);
        data.put("total", total);
        return Result.success(200, "获取成功", data);
    }
}