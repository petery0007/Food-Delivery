package com.xtu.service;

import com.xtu.pojo.*;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface UserService {
    Result register(User user);

    Result login(User user);

    Result updateUser(HttpServletRequest request, User user);

    Result updatePassword(HttpServletRequest request,
                          PasswordUpdateRequest passwordUpdateRequest);

    Result getUserInfo(HttpServletRequest request);

    Result updateUserMoney(HttpServletRequest request, BigDecimal amount);

    Result getAllProducts(Integer page, Integer pageSize);

    Result getProductsByKeywordsAndSpecification(Integer page, Integer pageSize, ProductInfo productInfo);

    Result getAllUser(Integer page, Integer pageSize);

    Result getUsersByKeywords(Integer page, Integer pageSize, UserInfo2 userInfo2);

    Result getAllPeisong(Integer page, Integer pageSize);
}
