package com.xtu.service.impl;

import com.xtu.mapper.ProductMapper;
import com.xtu.pojo.Product;
import com.xtu.pojo.ProductInfo;
import com.xtu.pojo.UserInfo2;
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

    @Override
    public Result getProductsByPage(Integer page, Integer pageSize, ProductInfo productInfo) {
        log.info("获取商品列表，页码: {}, 每页数量: {}", page, pageSize);

        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        int offset = (page - 1) * pageSize;

        if(productInfo == null){
            List<Product> products = productMapper.selectByPage(offset, pageSize);
            Integer total = productMapper.countProducts();

            Map<String, Object> data = new HashMap<>();
            data.put("list", products);
            data.put("total", total);

            return Result.success(200, "获取成功", data);
        }
        else if(productInfo.getKeywords() == null){
            List<Product> products = productMapper.selectBySpecification(offset, pageSize, productInfo.getSpecification());
            Integer total = productMapper.countProductsBySpecification(productInfo.getSpecification());
            if(total == null){
                return Result.error(401, "查询商品不存在");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("list", products);
            data.put("total", total);
            return Result.success(200, "获取成功", data);
        } else if (productInfo.getSpecification() == null) {
            List<Product> products = productMapper.selectByKeywords(offset, pageSize, productInfo.getKeywords());
            Integer total = productMapper.countProductsByKeywords(productInfo.getKeywords());
            if(total == null){
                return Result.error(401, "查询商品不存在");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("list", products);
            data.put("total", total);
            return Result.success(200, "获取成功", data);

        }



        List<Product> products = productMapper.selectByKeywordsAndSpecification(offset, pageSize, productInfo.getSpecification(), productInfo.getKeywords());
        Integer total = productMapper.countProductsByKeywordsAndSpecification(productInfo.getSpecification(), productInfo.getKeywords());
        if(total == null){
            return Result.error(401, "查询商品不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", products);
        data.put("total", total);

        return Result.success(200, "获取成功", data);
    }

    @Override
    public Result updateStatusById(Integer id, String status) {
        log.info("更新商品状态，id: {}, status: {}", id, status);
        productMapper.upProductsStatusById(id, status);
        return Result.success(200, "更新成功");
    }

    @Override
    public Result deleteProductById(Integer id) {
        log.info("删除商品，id: {}", id);
        productMapper.deleteProductById(id);
        return Result.success(200, "删除成功");
    }

    @Override
    public Result addProduct(Product product) {
        log.info("添加商品，{}", product);
        productMapper.addProduct(product);
        return Result.success(200, "添加成功");
    }

    @Override
    public Result getProductById(Integer id) {
        log.info("获取商品，id: {}", id);
        Product product = productMapper.getProductById(id);
        if(product == null){
            return Result.success(404, "商品不存在");
        }
        return Result.success(200, "获取成功", product);
    }
}
