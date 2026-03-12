# 登录认证系统设计文档

> 文档版本：1.0
> 创建时间：2026-03-12
> 最后更新：2026-03-12

---

## 一、需求概述

### 1.1 功能目标

实现用户注册、登录、登出功能，支持多端（Web/Android/iOS）统一认证。

### 1.2 核心需求

- **注册**：支持用户名 + 密码、手机号 + 验证码、邮箱 + 密码等方式
- **登录**：支持账号密码、手机验证码、扫码登录等方式
- **登出**：支持 Token 失效/设备下线
- **认证方式**：JWT Token（无状态，适合前后端分离）

### 1.3 扩展需求（后续迭代）

- 扫码登录
- 第三方 OAuth（微信/GitHub/Google）
- Refresh Token 机制
- 多设备管理
- 登录审计

---

## 二、认证方案设计

### 2.1 三种认证方式对比

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| Session + Cookie | 简单易懂，状态可控 | 跨域复杂，不适合分布式 | 单体 Web 应用 |
| JWT Token | 无状态，跨域友好，移动端友好 | Token 无法主动撤销 | 前后端分离/多端 |
| Spring Security | 功能全，支持 RBAC | 配置复杂，学习曲线陡 | 企业级权限系统 |

### 2.2 本项目选择：JWT Token

**理由：**
- 前后端分离架构（有 Android/Web 端）
- 支持多端统一认证
- 轻量级，不依赖重型框架
- 后续可平滑升级到 Spring Security

### 2.3 JWT Token 结构

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.    ← Header（算法：HS256）
eyJ1c2VySWQiOiIxMjMiLCJleHAiOjE1MTYyMzkwMjJ9.  ← Payload（用户 ID+ 过期时间）
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  ← Signature（签名）
```

### 2.4 Token 策略

| Token 类型 | 有效期 | 用途 |
|------------|--------|------|
| Access Token | 2 小时 | 访问受保护接口 |
| Refresh Token | 7 天 | 刷新 Access Token（可选，第二阶段实现） |

---

## 三、数据库设计

### 3.1 用户主表 `user`

```sql
CREATE TABLE `user` (
  `id` VARCHAR(36) NOT NULL COMMENT '用户 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt 加密密码',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号（唯一）',
  `phone_verified` TINYINT DEFAULT 0 COMMENT '手机号是否验证',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱（唯一）',
  `email_verified` TINYINT DEFAULT 0 COMMENT '邮箱是否验证',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像 URL',
  `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-正常，1-封禁，2-注销中',
  `role` VARCHAR(50) DEFAULT 'USER' COMMENT '角色',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` INT DEFAULT 0,
  `deleted` TINYINT DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 3.2 用户设备表 `user_device`（第二阶段）

```sql
CREATE TABLE `user_device` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键 ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户 ID',
  `device_id` VARCHAR(100) NOT NULL COMMENT '设备唯一标识',
  `device_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
  `device_type` VARCHAR(20) NOT NULL COMMENT '设备类型：iOS/Android/Web/PC',
  `device_token` VARCHAR(500) DEFAULT NULL COMMENT '推送 Token',
  `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录 IP',
  `last_login_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_device_id` (`device_id`),
  UNIQUE KEY `uk_user_device` (`user_id`, `device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备表';
```

### 3.3 登录记录表 `login_history`（第二阶段）

```sql
CREATE TABLE `login_history` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键 ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户 ID',
  `login_type` VARCHAR(20) NOT NULL COMMENT '登录方式：PASSWORD/SMS/SCAN/OAUTH',
  `device_id` VARCHAR(100) DEFAULT NULL COMMENT '设备 ID',
  `device_type` VARCHAR(20) DEFAULT NULL COMMENT '设备类型',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP 地址',
  `ip_location` VARCHAR(200) DEFAULT NULL COMMENT 'IP 所在地',
  `status` TINYINT DEFAULT 0 COMMENT '登录状态：0-成功，1-失败',
  `fail_reason` VARCHAR(200) DEFAULT NULL COMMENT '失败原因',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录历史记录表';
```

### 3.4 JWT 黑名单表 `token_blacklist`（可选）

```sql
CREATE TABLE `token_blacklist` (
  `id` VARCHAR(36) NOT NULL COMMENT '主键 ID',
  `token` VARCHAR(500) NOT NULL COMMENT 'JWT Token',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户 ID',
  `expire_at` DATETIME NOT NULL COMMENT 'Token 过期时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_token` (`token`),
  KEY `idx_expire_at` (`expire_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='JWT 黑名单表';
```

> 注：JWT 黑名单建议使用 Redis 实现，设置过期时间自动删除

---

## 四、API 接口设计

### 4.1 注册接口

#### 4.1.1 用户名 + 密码注册

```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test123456",
  "nickname": "测试用户"
}

Response:
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": "uuid-xxx",
    "username": "testuser",
    "token": "eyJhbGc..."
  }
}
```

#### 4.1.2 手机号 + 验证码注册

```
POST /api/auth/register/phone
Content-Type: application/json

{
  "phone": "13800138000",
  "code": "123456",
  "nickname": "测试用户"
}

Response: 同上
```

### 4.2 登录接口

#### 4.2.1 账号密码登录

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",  // 可以是用户名/手机号/邮箱
  "password": "Test123456",
  "deviceId": "device-uuid-xxx",  // 可选
  "deviceName": "iPhone 15 Pro",  // 可选
  "deviceType": "iOS"  // 可选
}

Response:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "uuid-xxx",
    "username": "testuser",
    "token": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",  // 可选，第二阶段
    "expiresIn": 7200  // Token 有效期（秒）
  }
}
```

#### 4.2.2 手机验证码登录

```
POST /api/auth/login/sms
Content-Type: application/json

{
  "phone": "13800138000",
  "code": "123456",
  "deviceId": "device-uuid-xxx"  // 可选
}

Response: 同上
```

#### 4.2.3 扫码登录

```
# 步骤 1：生成二维码
GET /api/auth/login/qrcode

Response:
{
  "code": 200,
  "data": {
    "qrcodeToken": "uuid-xxx",
    "qrContent": "https://matrix-ai.com/login/scan?qrcode_token=uuid-xxx",
    "expireTime": 900  // 15 分钟
  }
}

# 步骤 2：轮询扫码状态
GET /api/auth/login/qrcode/status?qrcode_token=uuid-xxx

Response:
{
  "code": 200,
  "data": {
    "status": "SUCCESS",  // WAITING/CONFIRMED/SUCCESS/EXPIRED
    "token": "eyJhbGc...",
    "refreshToken": "eyJhbGc..."
  }
}
```

### 4.3 登出接口

```
POST /api/auth/logout
Authorization: Bearer <token>
Content-Type: application/json

{
  "tokenId": "uuid-xxx"  // 可选，指定下线某个 Token
}

Response:
{
  "code": 200,
  "message": "登出成功"
}
```

### 4.4 Token 刷新接口（第二阶段）

```
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGc..."
}

Response:
{
  "code": 200,
  "data": {
    "token": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "expiresIn": 7200
  }
}
```

### 4.5 发送验证码接口

```
POST /api/auth/sms/send
Content-Type: application/json

{
  "phone": "13800138000",
  "type": "REGISTER"  // REGISTER/LOGIN/RESET_PASSWORD
}

Response:
{
  "code": 200,
  "message": "验证码已发送"
}
```

### 4.6 找回密码接口

```
# 步骤 1：验证手机/邮箱
POST /api/auth/password/reset/verify
Content-Type: application/json

{
  "phone": "13800138000",
  "code": "123456"
}

# 步骤 2：重置密码
POST /api/auth/password/reset
Content-Type: application/json

{
  "phone": "13800138000",
  "newPassword": "NewTest123456"
}

Response:
{
  "code": 200,
  "message": "密码重置成功"
}
```

---

## 五、JWT Token 设计

### 5.1 Payload 内容

```json
{
  "userId": "uuid-xxx",
  "username": "testuser",
  "iat": 1678612800,        // 签发时间
  "exp": 1678620000,        // 过期时间（2 小时后）
  "iss": "matrix-ai"        // 签发者
}
```

### 5.2 生成逻辑

```java
public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId());
    claims.put("username", user.getUsername());

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getId())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 7200 * 1000)) // 2 小时
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
}
```

### 5.3 验证逻辑

```java
public Claims parseToken(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody();
}

public boolean validateToken(String token) {
    try {
        Claims claims = parseToken(token);
        // 检查是否在黑名单中
        if (blacklistService.isInBlacklist(token)) {
            return false;
        }
        return !claims.getExpiration().before(new Date());
    } catch (JwtException e) {
        return false;
    }
}
```

### 5.4 Token 拦截器

```java
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            return false;
        }

        // 将用户信息存入 request，供后续使用
        Claims claims = jwtUtil.parseToken(token);
        request.setAttribute("userId", claims.get("userId"));
        request.setAttribute("username", claims.get("username"));

        return true;
    }
}
```

---

## 六、设备 ID 获取方案（Android 端）

### 6.1 方案对比

| 方案 | 稳定性 | 获取难度 | 推荐度 |
|------|--------|----------|--------|
| Android ID | 中（恢复出厂变化） | 简单 | ⭐⭐⭐ |
| OAID | 高 | 中等（需 SDK） | ⭐⭐⭐⭐ |
| UUID（自生成） | 高（卸载不变） | 简单 | ⭐⭐⭐⭐ |
| MediaDrm ID | 高 | 中等 | ⭐⭐⭐ |

### 6.2 推荐方案：UUID 自生成 + Android ID 备用

```kotlin
object DeviceUtil {

    /**
     * 获取设备唯一标识
     * 优先使用 OAID，其次 Android ID，最后生成 UUID
     */
    fun getDeviceId(context: Context): String {
        // 方案 1：OAID（中国信通院标准）
        val oaid = getOAID(context)
        if (!oaid.isNullOrBlank()) {
            return "oaid:$oaid"
        }

        // 方案 2：Android ID
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        if (!androidId.isNullOrBlank()) {
            return "aid:$androidId"
        }

        // 方案 3：生成 UUID 并持久化
        return getOrCreateUUID(context)
    }

    /**
     * 获取设备名称
     */
    fun getDeviceName(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }

    /**
     * 获取设备类型
     */
    fun getDeviceType(): String {
        return "Android"
    }

    private fun getOrCreateUUID(context: Context): String {
        val prefs = context.getSharedPreferences("device", Context.MODE_PRIVATE)
        var uuid = prefs.getString("device_uuid", null)
        if (uuid == null) {
            uuid = "uuid:${UUID.randomUUID()}"
            prefs.edit().putString("device_uuid", uuid).apply()
        }
        return uuid
    }
}
```

---

## 七、安全设计

### 7.1 密码安全

- 使用 BCrypt 加密，不存储明文密码
- 密码强度要求：至少 8 位，包含大小写字母和数字

```java
// 加密
String encodedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));

// 验证
boolean matches = BCrypt.checkpw(rawPassword, encodedPassword);
```

### 7.2 防止暴力破解

- 密码错误 5 次后，要求输入图形验证码
- 同一 IP 1 分钟内失败 10 次，封禁 30 分钟

### 7.3 Token 安全

- Access Token 有效期 2 小时
- 敏感操作（修改密码、删除账号）需要重新验证
- 登出时 Token 加入黑名单

### 7.4 传输安全

- 全站 HTTPS
- 敏感数据（密码）前端加密后传输（可选）

---

## 八、分阶段实施计划

### 第一阶段（MVP - 核心功能）

| 任务 | 描述 | 优先级 |
|------|------|--------|
| 1. 用户表设计 | 创建 `user` 表 | P0 |
| 2. JWT 工具类 | 生成/验证 Token | P0 |
| 3. 注册接口 | 用户名 + 密码注册 | P0 |
| 4. 登录接口 | 账号密码登录 | P0 |
| 5. 登出接口 | Token 黑名单 | P1 |
| 6. JWT 拦截器 | 验证 Token | P0 |

### 第二阶段（增强功能）

| 任务 | 描述 | 优先级 |
|------|------|--------|
| 1. 手机验证码登录 | 发送验证码 + 验证码登录 | P1 |
| 2. 设备表设计 | 创建 `user_device` 表 | P2 |
| 3. Refresh Token | Token 刷新机制 | P1 |
| 4. 找回密码 | 手机/邮箱验证码重置密码 | P1 |

### 第三阶段（扩展功能）

| 任务 | 描述 | 优先级 |
|------|------|--------|
| 1. 扫码登录 | 二维码生成 + 轮询 + 确认 | P2 |
| 2. 第三方 OAuth | 微信/GitHub登录 | P3 |
| 3. 登录记录 | 创建 `login_history` 表 | P3 |
| 4. 多设备管理 | 查看/下线已登录设备 | P2 |

---

## 九、依赖配置

### 9.1 Maven 依赖

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- Redis（可选，用于 Token 黑名单/验证码） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- 短信服务（可选，阿里云/腾讯云） -->
<!-- 根据具体服务商添加依赖 -->
```

### 9.2 application.yml 配置

```yaml
# JWT 配置
jwt:
  secret-key: your-secret-key-here-at-least-32-chars  # 至少 32 字符
  expiration: 7200000  # 2 小时
  issuer: matrix-ai

# Redis 配置（可选）
spring:
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
```

---

## 十、附录

### 10.1 项目文件结构

```
backend/src/main/java/com/matrix/ai/
├── entity/
│   ├── User.java              # 用户实体
│   └── UserDevice.java        # 用户设备实体（第二阶段）
├── mapper/
│   ├── UserMapper.java
│   └── UserDeviceMapper.java
├── dto/
│   ├── request/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   └── SmsSendRequest.java
│   └── response/
│       ├── AuthResponse.java
│       └── QrCodeResponse.java
├── vo/
│   ├── TokenVO.java
│   └── UserInfoVO.java
├── service/
│   ├── UserService.java
│   ├── AuthService.java
│   └── SmsService.java
├── controller/
│   ├── AuthController.java
│   └── UserController.java
├── config/
│   ├── JwtConfig.java
│   └── WebConfig.java
└── interceptor/
    └── JwtInterceptor.java
```

### 10.2 错误码定义

| 错误码 | 说明 |
|--------|------|
| 40001 | 用户名已存在 |
| 40002 | 手机号已注册 |
| 40003 | 邮箱已注册 |
| 40004 | 验证码错误 |
| 40005 | 验证码已过期 |
| 40101 | 账号或密码错误 |
| 40102 | Token 无效或过期 |
| 40103 | Token 已在黑名单 |
| 40301 | 账号已被封禁 |
| 40401 | 用户不存在 |

---

## 修订历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|----------|------|
| 1.0 | 2026-03-12 | 初始版本 | - |
