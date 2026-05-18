package com.xtu.service;

import com.xtu.pojo.Product;
import com.xtu.pojo.ProductInfo;
import com.xtu.pojo.UserInfo2;
import com.xtu.utils.Result;

public interface ProductService {
    Result getAllProducts(Integer page, Integer pageSize);

    Result getProductsByPage(Integer page, Integer pageSize, ProductInfo productInfo);

    Result updateStatusById(Integer id, String status);

    Result deleteProductById(Integer id);

    Result addProduct(Product product);

    Result getProductById(Integer id);

    Result getProductReviews(Integer id, Integer page, Integer pageSize);
}
