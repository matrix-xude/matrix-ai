package com.matrix.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {

    /**
     * Access Token
     */
    private String token;

    /**
     * Refresh Token（预留，第二阶段使用）
     */
    private String refreshToken;

    /**
     * Token 有效期（秒）
     */
    private Long expiresIn;
}
