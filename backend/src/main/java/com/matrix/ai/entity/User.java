package com.matrix.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    /**
     * 用户 ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户名（唯一）
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * BCrypt 加密密码
     */
    private String password;

    /**
     * 手机号（唯一）
     */
    private String phone;

    /**
     * 手机号是否验证：0-未验证，1-已验证
     */
    @TableField("phone_verified")
    private Integer phoneVerified;

    /**
     * 邮箱（唯一）
     */
    private String email;

    /**
     * 邮箱是否验证：0-未验证，1-已验证
     */
    @TableField("email_verified")
    private Integer emailVerified;

    /**
     * 头像 URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 状态：0-正常，1-封禁，2-注销中
     */
    private Integer status;

    /**
     * 角色
     */
    private String role;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    private Integer deleted;
}
