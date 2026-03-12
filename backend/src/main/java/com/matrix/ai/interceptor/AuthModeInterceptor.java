package com.matrix.ai.interceptor;

import com.matrix.ai.annotation.RequireAuth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证模式拦截器
 *
 * <p>解析 {@link RequireAuth} 注解，设置认证模式</p>
 */
@Component
@RequiredArgsConstructor
public class AuthModeInterceptor implements HandlerInterceptor {

    private static final String AUTH_MODE_ATTR = "authMode";

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
        // 只处理方法级别的注解
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // 优先获取方法上的注解
            RequireAuth methodAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);
            if (methodAuth != null) {
                request.setAttribute(AUTH_MODE_ATTR, methodAuth.mode().name());
                return true;
            }

            // 其次获取类上的注解
            RequireAuth typeAuth = handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
            if (typeAuth != null) {
                request.setAttribute(AUTH_MODE_ATTR, typeAuth.mode().name());
                return true;
            }

            // 默认 REQUIRED 模式（不设置，由 JwtInterceptor 默认处理）
        }

        return true;
    }
}
