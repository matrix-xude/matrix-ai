# 登录接入指南（前端/移动端）

> 文档版本：1.0
> 创建时间：2026-03-12
> 最后更新：2026-03-12

---

## 一、快速开始

### 1.1 接口地址

| 接口 | 地址 | 说明 |
|------|------|------|
| 注册 | `POST /api/auth/register` | 用户名 + 密码注册 |
| 登录 | `POST /api/auth/login` | 账号密码登录 |
| 登出 | `POST /api/auth/logout` | 需要 Token |
| 发送验证码 | `POST /api/auth/sms/send` | 手机验证码 |

### 1.2 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

- `code = 200`：请求成功
- `code != 200`：失败，`message` 包含错误信息

---

## 二、Token 使用规范

### 2.1 登录后获取 Token

```json
// POST /api/auth/login 响应
{
  "code": 200,
  "data": {
    "token": "eyJhbGc...",
    "expiresIn": 7200
  }
}
```

### 2.2 存储 Token

| 端 | 存储方式 |
|------|------|
| Web | `localStorage.setItem('token', token)` |
| Android | `SharedPreferences` 存储 |
| iOS | `Keychain` 存储 |

### 2.3 携带 Token

所有受保护的接口需要在 Header 中携带：

```
Authorization: Bearer eyJhbGc...
```

**示例代码：**

```javascript
// Web (axios)
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

```kotlin
// Android (OkHttp)
class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getAppPreferences().getString("token", null)
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
```

### 2.4 Token 过期处理

- Token 有效期：**2 小时**
- 收到 `401` 响应后，清除 Token 并跳转登录页

---

## 三、设备信息规范

登录时需要传递设备信息（可选，建议传递）：

```json
{
  "username": "testuser",
  "password": "Test123456",
  "deviceId": "uuid:xxx-xxx-xxx",
  "deviceName": "iPhone 15 Pro",
  "deviceType": "iOS"
}
```

### 3.1 设备 ID 生成规则

| 端 | 生成方式 |
|------|------|
| Android | 优先 OAID → Android ID → UUID 持久化 |
| iOS | IDFV 或 UUID 持久化 |
| Web | UUID 存储到 localStorage |

### 3.2 设备类型

固定值：`Android` / `iOS` / `Web` / `PC`

---

## 四、接口调用示例

### 4.1 注册（用户名 + 密码）

```javascript
// POST /api/auth/register
{
  "username": "testuser",
  "password": "Test123456",
  "nickname": "测试用户"
}

// 响应
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": "uuid-xxx",
    "token": "eyJhbGc..."
  }
}
```

### 4.2 登录（账号密码）

```javascript
// POST /api/auth/login
{
  "username": "testuser",  // 可以是用户名/手机号/邮箱
  "password": "Test123456",
  "deviceId": "uuid:xxx",
  "deviceType": "Web"
}

// 响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "uuid-xxx",
    "token": "eyJhbGc...",
    "expiresIn": 7200
  }
}
```

### 4.3 登出

```javascript
// POST /api/auth/logout
// Header: Authorization: Bearer eyJhbGc...

// 响应
{
  "code": 200,
  "message": "登出成功"
}
```

### 4.4 发送验证码

```javascript
// POST /api/auth/sms/send
{
  "phone": "13800138000",
  "type": "LOGIN"  // REGISTER / LOGIN / RESET_PASSWORD
}

// 响应
{
  "code": 200,
  "message": "验证码已发送"
}
```

---

## 五、错误码处理

| 错误码 | 说明 | 前端处理 |
|--------|------|----------|
| 40001 | 用户名已存在 | 提示用户更换用户名 |
| 40002 | 手机号已注册 | 提示用户更换手机号或直接登录 |
| 40004 | 验证码错误 | 提示重新输入 |
| 40005 | 验证码已过期 | 提示重新发送 |
| 40101 | 账号或密码错误 | 提示检查账号密码 |
| 40102 | Token 无效或过期 | 清除 Token，跳转登录页 |
| 40301 | 账号已被封禁 | 提示联系客服 |
| 40401 | 用户不存在 | 提示检查账号 |

---

## 六、完整接口文档

接口详细定义以 OpenAPI 契约为准：

[docs/api/openapi.yaml](./api/openapi.yaml)

---

## 七、修订历史

| 版本 | 日期 | 修改内容 |
|------|------|----------|
| 1.0 | 2026-03-12 | 初始版本 |
