package com.xtu.service;

import com.xtu.pojo.ReviewSubmitRequest;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

public interface CommentService {
    Result getReviewList(HttpServletRequest request, Integer page, Integer pageSize, String status);

    Result submitReview(HttpServletRequest request, ReviewSubmitRequest submitRequest);
}
