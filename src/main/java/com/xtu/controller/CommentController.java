package com.xtu.controller;

import com.xtu.pojo.Comment;
import com.xtu.pojo.ReviewSubmitRequest;
import com.xtu.pojo.ReviewVO;
import com.xtu.service.CommentService;
import com.xtu.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/review")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    public Result getReviewList(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) String status) {
        return commentService.getReviewList(request, page, pageSize, status);
    }

    @PostMapping("/submit")
    public Result submitReview(HttpServletRequest request, @RequestBody ReviewSubmitRequest reviewSubmitRequest) {
        return commentService.submitReview(request, reviewSubmitRequest);
    }
}
