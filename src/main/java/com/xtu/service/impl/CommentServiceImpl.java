package com.xtu.service.impl;

import com.xtu.mapper.CommentMapper;
import com.xtu.mapper.OrderMapper;
import com.xtu.mapper.ProductMapper;
import com.xtu.pojo.*;
import com.xtu.service.CommentService;
import com.xtu.utils.JwtUtils;
import com.xtu.utils.Result;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public Result getReviewList(HttpServletRequest request, Integer page, Integer pageSize, String status) {
        log.info("========== 获取评价列表开始 ==========");
        log.info("页码: {}, 每页数量: {}, 状态: {}", page, pageSize, status);

        try {
            String token = request.getHeader("token");
            if (token == null || token.isEmpty()) {
                return Result.error(401, "未登录，请先登录");
            }

            Claims claims = JwtUtils.parseToken(token);
            Integer userId = (Integer) claims.get("id");

            if (page == null || page < 1) {
                page = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;
            }

            List<ReviewVO> reviewList = new ArrayList<>();

            if ("pending".equals(status)) {
                reviewList = getPendingReviews(userId);
            } else if ("completed".equals(status)) {
                reviewList = getCompletedReviews(userId);
            } else {
                List<ReviewVO> pending = getPendingReviews(userId);
                List<ReviewVO> completed = getCompletedReviews(userId);
                reviewList.addAll(pending);
                reviewList.addAll(completed);
            }

            int total = reviewList.size();

            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, total);

            List<ReviewVO> pagedList = fromIndex < total ?
                    reviewList.subList(fromIndex, toIndex) : new ArrayList<>();

            Map<String, Object> data = new HashMap<>();
            data.put("list", pagedList);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            log.info("查询到评价总数: {}, 当前页评价数: {}", total, pagedList.size());

            return Result.success(200, "获取成功", data);

        } catch (Exception e) {
            log.error("========== 获取评价列表失败 ==========", e);
            return Result.error(500, "获取评价列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result submitReview(HttpServletRequest request, ReviewSubmitRequest submitRequest) {
        log.info("========== 提交评价开始 ==========");
        log.info("评价数据: {}", submitRequest);

        try {
            String token = request.getHeader("token");
            if (token == null || token.isEmpty()) {
                return Result.error(401, "未登录，请先登录");
            }

            Claims claims = JwtUtils.parseToken(token);
            Integer userId = (Integer) claims.get("id");

            if (submitRequest.getOrderId() == null) {
                return Result.error(400, "订单ID不能为空");
            }

            if (submitRequest.getProductId() == null) {
                return Result.error(400, "商品ID不能为空");
            }

            if (submitRequest.getRating() == null || submitRequest.getRating() < 1 || submitRequest.getRating() > 5) {
                return Result.error(400, "评分必须为1-5分");
            }

            if (submitRequest.getComment() == null || submitRequest.getComment().trim().isEmpty()) {
                return Result.error(400, "评价内容不能为空");
            }

            if (submitRequest.getComment().length() < 5 || submitRequest.getComment().length() > 200) {
                return Result.error(400, "评价内容长度在 5 到 200 个字符");
            }

            Order order = orderMapper.getOrderById(submitRequest.getOrderId());
            if (order == null) {
                return Result.error(404, "订单不存在");
            }

            if (!order.getUserId().equals(userId)) {
                return Result.error(403, "无权操作此订单");
            }

            if (order.getStatus() != 3) {
                return Result.error(400, "只能评价已完成的订单");
            }

            boolean hasPurchased = checkUserHasPurchasedProduct(order, submitRequest.getProductId());
            if (!hasPurchased) {
                return Result.error(400, "该商品不在订单中");
            }

            Comment comment = new Comment();
            comment.setUserId(userId);
            comment.setGoodsId(submitRequest.getProductId());
            comment.setContent(submitRequest.getComment());
            comment.setScore(submitRequest.getRating());
            comment.setCreateTime(LocalDateTime.now());

            int result = commentMapper.insertComment(comment);
            if (result <= 0) {
                return Result.error(500, "评价提交失败");
            }

            log.info("用户 {} 对订单 {} 中的商品 {} 评价成功，评分: {}",
                    userId, submitRequest.getOrderId(), submitRequest.getProductId(), submitRequest.getRating());

            Map<String, Object> data = new HashMap<>();
            data.put("commentId", comment.getCommentId());
            data.put("message", "评价成功");

            return Result.success(200, "评价成功", data);

        } catch (Exception e) {
            log.error("========== 提交评价失败 ==========", e);
            return Result.error(500, "评价失败: " + e.getMessage());
        }
    }
    private List<ReviewVO> getPendingReviews(Integer userId) {
        List<ReviewVO> pendingReviews = new ArrayList<>();

        List<Order> deliveringOrders = orderMapper.getOrdersByUserIdWithStatus(userId, 2);
        List<Order> completedOrders = orderMapper.getOrdersByUserIdWithStatus(userId, 3);

        List<Order> allRelevantOrders = new ArrayList<>();
        allRelevantOrders.addAll(deliveringOrders);
        allRelevantOrders.addAll(completedOrders);

        for (Order order : allRelevantOrders) {
            List<OrderItemEntity> items = orderMapper.getOrderItemsByOrderId(order.getId());

            List<Integer> reviewedProductIds = getReviewedProductIds(userId, items);

            for (OrderItemEntity item : items) {
                if (!reviewedProductIds.contains(item.getProductId())) {
                    ReviewVO reviewVO = new ReviewVO();
                    reviewVO.setId(reviewedProductIds.size() + pendingReviews.size() + 1);
                    reviewVO.setOrderId(order.getId());
                    reviewVO.setProductId(item.getProductId());

                    ProductInfo productInfo = new ProductInfo();
                    productInfo.setName(item.getProductName());
                    productInfo.setSpecification(item.getSpecification());
                    productInfo.setPrice(item.getPrice());
                    productInfo.setImageUrl(item.getImageUrl());
                    reviewVO.setProduct(productInfo);

                    reviewVO.setStatus("pending");
                    reviewVO.setRating(0);
                    reviewVO.setComment("");
                    reviewVO.setCreateTime(order.getCreateTime().format(FORMATTER));
                    reviewVO.setReviewTime(null);

                    pendingReviews.add(reviewVO);
                }
            }
        }

        return pendingReviews;
    }

    private List<ReviewVO> getCompletedReviews(Integer userId) {
        List<ReviewVO> completedReviews = new ArrayList<>();

        List<Comment> comments = commentMapper.selectCommentsByUserId(userId, 0, 1000);

        for (Comment comment : comments) {
            ReviewVO reviewVO = new ReviewVO();
            reviewVO.setId(comment.getCommentId());
            reviewVO.setOrderId(findOrderByProductId(userId, comment.getGoodsId()));
            reviewVO.setProductId(comment.getGoodsId());

            Product product = productMapper.getProductById(comment.getGoodsId());
            if (product != null) {
                ProductInfo productInfo = new ProductInfo();
                productInfo.setName(product.getName());
                productInfo.setSpecification(product.getSpecification());
                productInfo.setPrice(product.getPrice());
                productInfo.setImageUrl(product.getImageUrl());
                reviewVO.setProduct(productInfo);
            }

            reviewVO.setStatus("completed");
            reviewVO.setRating(comment.getScore());
            reviewVO.setComment(comment.getContent());
            reviewVO.setCreateTime(comment.getCreateTime().format(FORMATTER));
            reviewVO.setReviewTime(comment.getCreateTime().format(FORMATTER));

            completedReviews.add(reviewVO);
        }

        return completedReviews;
    }

    private List<Integer> getReviewedProductIds(Integer userId, List<OrderItemEntity> items) {
        List<Integer> reviewedIds = new ArrayList<>();
        List<Comment> userComments = commentMapper.selectCommentsByUserId(userId, 0, 1000);

        for (Comment comment : userComments) {
            for (OrderItemEntity item : items) {
                if (comment.getGoodsId().equals(item.getProductId())) {
                    reviewedIds.add(item.getProductId());
                }
            }
        }

        return reviewedIds;
    }

    private Integer findOrderByProductId(Integer userId, Integer productId) {
        List<Order> orders = orderMapper.getOrdersByUserId(userId);
        for (Order order : orders) {
            List<OrderItemEntity> items = orderMapper.getOrderItemsByOrderId(order.getId());
            for (OrderItemEntity item : items) {
                if (item.getProductId().equals(productId)) {
                    return order.getId();
                }
            }
        }
        return null;
    }

    private boolean checkUserHasPurchasedProduct(Order order, Integer productId) {
        List<OrderItemEntity> items = orderMapper.getOrderItemsByOrderId(order.getId());
        for (OrderItemEntity item : items) {
            if (item.getProductId().equals(productId)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkUserHasPurchased(Integer userId, Integer goodsId) {
        List<Order> orders = orderMapper.getOrdersByUserId(userId);
        for (Order order : orders) {
            if (order.getStatus() == 3) {
                var items = orderMapper.getOrderItemsByOrderId(order.getId());
                for (var item : items) {
                    if (item.getProductId().equals(goodsId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
