package com.matrix.ai.constant;

/**
 * 认证相关错误码常量
 */
public class AuthErrorCode {

    private AuthErrorCode() {
        // 工具类，禁止实例化
    }

    // 用户相关错误码 (40001-40099)
    public static final int USER_ALREADY_EXISTS = 40001;      // 用户名已存在
    public static final int PHONE_ALREADY_REGISTERED = 40002; // 手机号已注册
    public static final int EMAIL_ALREADY_REGISTERED = 40003; // 邮箱已注册
    public static final int USER_NOT_FOUND = 40401;           // 用户不存在

    // 认证相关错误码 (40101-40199)
    public static final int INVALID_CREDENTIALS = 40101;      // 账号或密码错误
    public static final int TOKEN_INVALID = 40102;            // Token 无效或过期
    public static final int TOKEN_BLACKLISTED = 40103;        // Token 已在黑名单

    // 权限相关错误码 (40301-40399)
    public static final int ACCOUNT_BANNED = 40301;           // 账号已被封禁
    public static final int ACCOUNT_DELETED = 40302;          // 账号已注销

    // 验证码相关错误码 (40051-40099)
    public static final int INVALID_VERIFICATION_CODE = 40051; // 验证码错误
    public static final int VERIFICATION_CODE_EXPIRED = 40052; // 验证码已过期
}
