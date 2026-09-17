package com.shouyang.utils;

import com.shouyang.interceptor.JwtInterceptor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 当前登录用户上下文工具类
 * 从 request 作用域中获取 JWT 拦截器存入的用户信息
 * 仅在被 JWT 拦截器拦截的请求中有效
 *
 * @author shouyang
 */
public class UserContext {

    /**
     * 获取当前请求对象
     */
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    /**
     * 获取当前登录用户 ID
     *
     * @return 用户 ID，未登录返回 null
     */
    public static Long getUserId() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        Object userId = request.getAttribute(JwtInterceptor.ATTR_USER_ID);
        if (userId == null) {
            return null;
        }
        return (Long) userId;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，未登录返回 null
     */
    public static String getUsername() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        Object username = request.getAttribute(JwtInterceptor.ATTR_USERNAME);
        return username == null ? null : (String) username;
    }

    /**
     * 获取当前登录用户类型（user/admin）
     *
     * @return 用户类型，未登录返回 null
     */
    public static String getUserType() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        Object userType = request.getAttribute(JwtInterceptor.ATTR_USER_TYPE);
        return userType == null ? null : (String) userType;
    }

    /**
     * 判断当前是否已登录
     */
    public static boolean isLogin() {
        return getUserId() != null;
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        return "admin".equals(getUserType());
    }
}
