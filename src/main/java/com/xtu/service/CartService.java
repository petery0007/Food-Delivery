package com.xtu.service;

import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

public interface CartService {
    Result getCart(Integer userId);
}
