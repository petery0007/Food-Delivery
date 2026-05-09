package com.xtu.interceptor;

import com.xtu.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenIntercepter implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String requestURI = request.getRequestURI();
//        if(requestURI.contains("/login")){
//            log.info("登录操作，放行");
//            return true;
//        }

        String token = request.getHeader("token");
        if(token == null || token.isEmpty()){
            log.info("用户未登录，请先登录");
            response.setStatus(401);
            return false;
        }
        try {
            JwtUtils.parseToken(token);
        }catch (Exception e){
            log.info("令牌无效");
            response.setStatus(401);
            return false;
        }
        log.info("令牌有效,放行");
        return true;
    }
}
