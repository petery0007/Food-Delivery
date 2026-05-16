package com.xtu.service.impl;

import com.xtu.mapper.CommentMapper;
import com.xtu.mapper.ProductMapper;
import com.xtu.mapper.UserMapper;
import com.xtu.pojo.Comment;
import com.xtu.pojo.Product;
import com.xtu.pojo.ProductInfo;
import com.xtu.pojo.UserInfo2;
import com.xtu.service.ProductService;
import com.xtu.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

//    @Override
//    public Result getProductById(Integer id) {
//        log.info("获取商品，id: {}", id);
//        Product product = productMapper.getProductById(id);
//        if(product == null){
//            return Result.success(404, "商品不存在");
//        }
//        return Result.success(200, "获取成功", product);
//    }

    @Override
    public Result getProductById(Integer id) {
        log.info("查询商品详情，商品ID: {}", id);

        try {
            Product product = productMapper.getProductById(id);

            if (product == null) {
                return Result.error(404, "商品不存在");
            }

            log.info("商品详情查询成功: {}", product.getName());

            return Result.success(200, "查询成功", product);

        } catch (Exception e) {
            log.error("查询商品详情失败", e);
            return Result.error(500, "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result getProductReviews(Integer productId, Integer page, Integer pageSize) {
        log.info("查询商品评价，商品ID: {}, 页码: {}, 每页数量: {}", productId, page, pageSize);

        try {
            if (page == null || page < 1) {
                page = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;
            }

            Integer offset = (page - 1) * pageSize;

            List<Comment> comments = commentMapper.selectCommentsByGoodsId(productId, offset, pageSize);
            Integer total = commentMapper.countCommentsByGoodsId(productId);

            List<Map<String, Object>> reviewList = new ArrayList<>();

            for (Comment comment : comments) {
                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("id", comment.getCommentId());
                reviewMap.put("productId", comment.getGoodsId());
                reviewMap.put("userId", comment.getUserId());

                String username = getUsernameByUserId(comment.getUserId());
                reviewMap.put("username", username);
                reviewMap.put("userAvatar", "");

                reviewMap.put("rating", comment.getScore());
                reviewMap.put("comment", comment.getContent());
                reviewMap.put("createTime", comment.getCreateTime().format(FORMATTER));
                reviewMap.put("images", new ArrayList<>());

                reviewList.add(reviewMap);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", reviewList);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            log.info("查询到评价总数: {}, 当前页评价数: {}", total, reviewList.size());

            return Result.success(200, "获取成功", data);

        } catch (Exception e) {
            log.error("查询商品评价失败", e);
            return Result.error(500, "查询失败: " + e.getMessage());
        }
    }

    private String getUsernameByUserId(Integer userId) {
        try {
            var user = userMapper.selectById(userId);
            if (user != null) {
                return user.getUsername();
            }
        } catch (Exception e) {
            log.error("查询用户名失败，用户ID: {}", userId, e);
        }
        return "匿名用户";
    }
}
