package com.matrix.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matrix.ai.constant.AuthErrorCode;
import com.matrix.ai.dto.request.LoginRequest;
import com.matrix.ai.dto.request.RegisterRequest;
import com.matrix.ai.dto.response.AuthResponse;
import com.matrix.ai.entity.User;
import com.matrix.ai.exception.AuthException;
import com.matrix.ai.mapper.UserMapper;
import com.matrix.ai.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册（用户名 + 密码）
     *
     * @param request 注册请求
     * @return 认证响应
     */
    @Transactional(rollbackFor = Exception.class)
    public AuthResponse register(RegisterRequest request) {
        // 1. 检查用户名是否已存在
        if (isUsernameExists(request.getUsername())) {
            throw new AuthException(AuthErrorCode.USER_ALREADY_EXISTS, "用户名已存在：" + request.getUsername());
        }

        // 2. BCrypt 加密密码
        String encodedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));

        // 3. 创建用户实体
        User user = User.builder()
                .id(java.util.UUID.randomUUID().toString())
                .username(request.getUsername())
                .nickname(request.getNickname() != null ? request.getNickname() : request.getUsername())
                .password(encodedPassword)
                .role("USER")
                .status(0) // 正常
                .gender(0) // 未知
                .phoneVerified(0)
                .emailVerified(0)
                .deleted(0)
                .version(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 4. 保存用户
        userMapper.insert(user);

        log.info("用户注册成功：{}", user.getUsername());

        // 5. 生成 Token 并返回
        return generateAuthResponse(user);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 认证响应
     */
    public AuthResponse login(LoginRequest request) {
        // 1. 根据用户名/手机号/邮箱查询用户
        User user = findByUsernameOrPhoneOrEmail(request.getUsername());
        if (user == null) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS, "账号或密码错误");
        }

        // 2. 检查用户状态
        if (user.getStatus() == 1) {
            throw new AuthException(AuthErrorCode.ACCOUNT_BANNED, "账号已被封禁");
        }
        if (user.getStatus() == 2) {
            throw new AuthException(AuthErrorCode.ACCOUNT_DELETED, "账号已注销");
        }

        // 3. 验证密码
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS, "账号或密码错误");
        }

        log.info("用户登录成功：{}", user.getUsername());

        // 4. 生成 Token 并返回
        return generateAuthResponse(user);
    }

    /**
     * 根据用户名/手机号/邮箱查询用户
     *
     * @param account 账号（可以是用户名、手机号或邮箱）
     * @return 用户实体
     */
    public User findByUsernameOrPhoneOrEmail(String account) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        wrapper.and(w -> w
                .eq(User::getUsername, account)
                .or()
                .eq(User::getPhone, account)
                .or()
                .eq(User::getEmail, account)
        );
        return userMapper.selectOne(wrapper);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 根据用户 ID 查询用户
     *
     * @param userId 用户 ID
     * @return 用户实体
     */
    public User findById(String userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        wrapper.eq(User::getId, userId);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true-已存在，false-不存在
     */
    public boolean isUsernameExists(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    /**
     * 检查手机号是否已注册
     *
     * @param phone 手机号
     * @return true-已注册，false-未注册
     */
    public boolean isPhoneRegistered(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectCount(wrapper) > 0;
    }

    /**
     * 检查邮箱是否已注册
     *
     * @param email 邮箱
     * @return true-已注册，false-未注册
     */
    public boolean isEmailRegistered(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        wrapper.eq(User::getEmail, email);
        return userMapper.selectCount(wrapper) > 0;
    }

    /**
     * 生成认证响应
     *
     * @param user 用户实体
     * @return 认证响应
     */
    private AuthResponse generateAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .token(token)
                .expiresIn(7200L) // 2 小时
                .build();
    }
}
