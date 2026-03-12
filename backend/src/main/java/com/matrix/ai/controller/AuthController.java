package com.matrix.ai.controller;

import com.matrix.ai.common.Result;
import com.matrix.ai.dto.request.LoginRequest;
import com.matrix.ai.dto.request.RegisterRequest;
import com.matrix.ai.dto.response.AuthResponse;
import com.matrix.ai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "Auth", description = "认证接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    /**
     * 用户注册
     *
     * @param request 注册请求（用户名 + 密码）
     * @return 认证响应（含 Token）
     */
    @Operation(summary = "用户注册", description = "支持用户名 + 密码方式注册，注册成功后自动登录并返回 Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册成功",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "参数错误/用户名已存在",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PostMapping("/register")
    public Result<AuthResponse> register(
            @Valid @RequestBody
            @Parameter(description = "注册请求参数", required = true)
            RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    /**
     * 用户登录
     *
     * @param request 登录请求（账号 + 密码）
     * @return 认证响应（含 Token）
     */
    @Operation(summary = "用户登录", description = "支持用户名/手机号/邮箱 + 密码方式登录，返回 Access Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "参数错误/账号或密码错误/账号已被封禁",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PostMapping("/login")
    public Result<AuthResponse> login(
            @Valid @RequestBody
            @Parameter(description = "登录请求参数", required = true)
            LoginRequest request) {
        return Result.success(userService.login(request));
    }

    /**
     * 用户登出
     *
     * @return 登出结果
     */
    @Operation(summary = "用户登出", description = "客户端删除 Token 即可，服务端暂不处理（JWT 无状态）")
    @ApiResponse(responseCode = "200", description = "登出成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT 无状态，客户端删除 Token 即可
        // 如果需要服务端撤销 Token，可以将 Token 加入黑名单（需要 Redis 支持）
        return Result.success("登出成功", null);
    }
}
