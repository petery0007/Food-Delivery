package com.xtu.service;

import com.xtu.utils.Result;

public interface ProductService {
    Result getAllProducts(Integer page, Integer pageSize);
}
