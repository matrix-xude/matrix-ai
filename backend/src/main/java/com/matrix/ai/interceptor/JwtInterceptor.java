package com.matrix.ai.interceptor;

import com.matrix.ai.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器
 *
 * <p>支持三种认证模式：</p>
 * <ul>
 *   <li>REQUIRED (default) - 必须提供有效 Token，否则返回 401</li>
 *   <li>OPTIONAL - 有 Token 则解析用户信息，无 Token 放行</li>
 *   <li>NONE - 不认证（通过 excludePathPatterns 排除）</li>
 * </ul>
 *
 * <p>开发环境下，来自 Swagger UI 的请求自动跳过 Token 验证</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 是否启用开发模式（开发模式下 Swagger UI 可跳过 Token 验证）
     */
    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    /**
     * 认证模式请求属性名
     * 在 Controller 中通过 request.setAttribute("authMode", "OPTIONAL") 设置
     */
    private static final String AUTH_MODE_ATTR = "authMode";

    public enum AuthMode {
        REQUIRED,   // 必须认证（默认）
        OPTIONAL,   // 可选认证
        NONE        // 不认证（通过 excludePathPatterns 排除）
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
        // 开发环境下，检查是否来自 Swagger UI
        if (isDevProfile() && isFromSwagger(request)) {
            log.debug("开发模式 + Swagger UI 请求，跳过 Token 验证");
            return true;
        }

        // 获取 Authorization Header
        String authHeader = request.getHeader("Authorization");

        // 没有 Token 的情况
        if (authHeader == null || authHeader.isEmpty()) {
            AuthMode authMode = getAuthMode(request);
            if (authMode == AuthMode.REQUIRED) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":40102,\"message\":\"缺少 Authorization Header\",\"data\":null}");
                return false;
            }
            // OPTIONAL 或 NONE 模式，无 Token 也放行
            log.debug("无 Token，模式={}，放行", authMode);
            return true;
        }

        // 检查 Bearer 前缀
        if (!authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":40102,\"message\":\"Token 格式错误，应为 Bearer <token>\",\"data\":null}");
            return false;
        }

        // 提取 Token
        String token = authHeader.substring(7);

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            // REQUIRED 模式下 Token 无效返回 401，OPTIONAL 模式下放行但无用户信息
            AuthMode authMode = getAuthMode(request);
            if (authMode == AuthMode.REQUIRED) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":40102,\"message\":\"Token 无效或已过期\",\"data\":null}");
                return false;
            }
            log.debug("Token 无效，模式={}，放行但不设置用户信息", authMode);
            return true;
        }

        // Token 有效，将用户信息存入 request 属性
        String userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);

        log.debug("Token 验证通过：userId={}, username={}", userId, username);
        return true;
    }

    /**
     * 检查是否为开发环境
     */
    private boolean isDevProfile() {
        return "dev".equals(activeProfile) || "test".equals(activeProfile);
    }

    /**
     * 检查请求是否来自 Swagger UI
     */
    private boolean isFromSwagger(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return referer != null && referer.contains("/swagger-ui/");
    }

    /**
     * 获取当前请求的认证模式
     */
    private AuthMode getAuthMode(HttpServletRequest request) {
        String mode = (String) request.getAttribute(AUTH_MODE_ATTR);
        if (mode == null) {
            return AuthMode.REQUIRED; // 默认为 REQUIRED
        }
        try {
            return AuthMode.valueOf(mode);
        } catch (IllegalArgumentException e) {
            return AuthMode.REQUIRED;
        }
    }
}
