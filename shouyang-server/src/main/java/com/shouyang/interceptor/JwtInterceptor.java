package com.shouyang.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shouyang.common.result.Result;
import com.shouyang.common.result.ResultCode;
import com.shouyang.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT 认证拦截器
 * 拦截需要登录的请求，验证 Token 有效性，并将用户信息存入 request 作用域
 *
 * @author shouyang
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 存入 request 中的用户 ID 属性名
     */
    public static final String ATTR_USER_ID = "userId";

    /**
     * 存入 request 中的用户名属性名
     */
    public static final String ATTR_USERNAME = "username";

    /**
     * 存入 request 中的用户类型属性名
     */
    public static final String ATTR_USER_TYPE = "userType";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头获取 Token
        String token = request.getHeader(JwtUtils.TOKEN_HEADER);
        if (token != null && token.startsWith(JwtUtils.TOKEN_PREFIX)) {
            // 去掉 "Bearer " 前缀
            token = token.substring(JwtUtils.TOKEN_PREFIX.length());
        }

        // Token 为空，返回 401
        if (token == null || token.trim().isEmpty()) {
            writeErrorResponse(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 解析 Token
        Claims claims = jwtUtils.parseToken(token);
        if (claims == null) {
            // Token 无效或已过期
            writeErrorResponse(response, ResultCode.TOKEN_INVALID);
            return false;
        }

        // 将用户信息存入 request，供后续 Controller 使用
        Object userId = claims.get("userId");
        Object username = claims.get("username");
        Object userType = claims.get("userType");

        if (userId != null) {
            request.setAttribute(ATTR_USER_ID, Long.valueOf(userId.toString()));
        }
        if (username != null) {
            request.setAttribute(ATTR_USERNAME, username.toString());
        }
        if (userType != null) {
            request.setAttribute(ATTR_USER_TYPE, userType.toString());
        }

        return true;
    }

    /**
     * 向响应写入 JSON 错误信息
     */
    private void writeErrorResponse(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        Result<Void> result = Result.error(resultCode);
        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(result));
        writer.flush();
        writer.close();
    }
}
