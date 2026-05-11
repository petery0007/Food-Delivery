package com.xtu.service.impl;

import com.xtu.mapper.ProductMapper;
import com.xtu.pojo.Product;
import com.xtu.service.ProductService;
import com.xtu.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result getAllProducts(Integer page, Integer pageSize) {
        log.info("获取商品列表，页码: {}, 每页数量: {}", page, pageSize);

        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        int offset = (page - 1) * pageSize;

        List<Product> products = productMapper.selectByPage(offset, pageSize);
        Integer total = productMapper.countProducts();

        Map<String, Object> data = new HashMap<>();
        data.put("list", products);
        data.put("total", total);
//        data.put("page", page);
//        data.put("pageSize", pageSize);
//        data.put("totalPages", (total + pageSize - 1) / pageSize);

        return Result.success(200, "获取成功", data);
    }
}
