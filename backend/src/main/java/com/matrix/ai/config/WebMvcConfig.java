package com.matrix.ai.config;

import com.matrix.ai.interceptor.AuthModeInterceptor;
import com.matrix.ai.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AuthModeInterceptor authModeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 认证模式拦截器（解析 @RequireAuth 注解，必须在 JwtInterceptor 之前执行）
        registry.addInterceptor(authModeInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/register",    // 注册接口（无需 Token）
                        "/api/auth/login",       // 登录接口（无需 Token）
                        "/api/health",           // 健康检查（无需 Token）
                        "/swagger-ui/**",        // 排除 Swagger UI
                        "/v3/api-docs/**",       // 排除 OpenAPI 文档
                        "/webjars/**"            // 排除静态资源
                );

        // 2. JWT 认证拦截器（验证 Token）
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/register",    // 注册接口（无需 Token）
                        "/api/auth/login",       // 登录接口（无需 Token）
                        "/api/health",           // 健康检查（无需 Token）
                        "/swagger-ui/**",        // 排除 Swagger UI
                        "/v3/api-docs/**",       // 排除 OpenAPI 文档
                        "/webjars/**"            // 排除静态资源
                );
    }
}
