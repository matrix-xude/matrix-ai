package com.matrix.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT 签名密钥（至少 32 字符）
     */
    private String secretKey;

    /**
     * Token 有效期（毫秒）
     * 默认 2 小时 = 7200000ms
     */
    private Long expiration = 7200000L;

    /**
     * 签发者
     */
    private String issuer = "matrix-ai";
}
