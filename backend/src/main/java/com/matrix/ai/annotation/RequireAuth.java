package com.matrix.ai.annotation;

import com.matrix.ai.interceptor.JwtInterceptor;
import java.lang.annotation.*;

/**
 * JWT 认证模式注解
 *
 * <p>用于标记 Controller 方法所需的认证模式：</p>
 * <ul>
 *   <li>{@link JwtInterceptor.AuthMode#REQUIRED} - 必须提供有效 Token（默认）</li>
 *   <li>{@link JwtInterceptor.AuthMode#OPTIONAL} - 可选 Token，有 Token 解析用户信息，无 Token 放行</li>
 *   <li>{@link JwtInterceptor.AuthMode#NONE} - 不认证（通过拦截器排除路径实现）</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>
 * {@code @RequireAuth(mode = AuthMode.OPTIONAL)}
 * public Result<UserInfo> getUserInfo() { ... }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAuth {

    /**
     * 认证模式，默认为 REQUIRED（必须认证）
     */
    JwtInterceptor.AuthMode mode() default JwtInterceptor.AuthMode.REQUIRED;
}
