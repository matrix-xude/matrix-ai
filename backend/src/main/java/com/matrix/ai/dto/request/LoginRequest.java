package com.matrix.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求
 */
@Data
public class LoginRequest {

    /**
     * 用户名/手机号/邮箱
     */
    @NotBlank(message = "账号不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 设备 ID（可选）
     */
    private String deviceId;

    /**
     * 设备名称（可选）
     */
    private String deviceName;

    /**
     * 设备类型（可选）
     */
    private String deviceType;
}
