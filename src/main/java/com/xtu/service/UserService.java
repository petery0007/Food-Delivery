package com.xtu.service;

import com.xtu.pojo.PasswordUpdateRequest;
import com.xtu.pojo.User;
import com.xtu.pojo.UserInfoVO;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    Result register(User user);

    Result login(User user);

    Result updateUser(HttpServletRequest request, User user);

    Result updatePassword(HttpServletRequest request,
                          PasswordUpdateRequest passwordUpdateRequest);

    Result getUserInfo(HttpServletRequest request);
}
