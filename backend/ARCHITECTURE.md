# Backend Architecture

后端项目架构说明文档。|

## 技术栈

| 组件 | 版本/说明 |
|------|----------|
| Java | 17 |
| Spring Boot | 3.2.0 |
| SpringDoc | 2.3.0 (OpenAPI 3.0) |
| Lombok | 简化样板代码 |
| Maven | 构建工具 |

## 项目结构

```
backend/
├── pom.xml                           # Maven 配置
├── ARCHITECTURE.md                   # 本文档
└── src/main/
    ├── java/com/matrix/ai/
    │   ├── MatrixApplication.java    # 应用入口
    │   ├── common/                   # 通用组件
    │   │   ├── Result.java           # 统一响应封装
    │   │   └── GlobalExceptionHandler.java
    │   ├── config/                   # 配置类
    │   │   └── OpenApiConfig.java    # OpenAPI 配置
    │   └── controller/               # REST 控制器
    │       └── HealthController.java # 健康检查
    └── resources/
        ├── application.yml           # 主配置文件
        └── application-dev.yml       # 开发环境配置
```

## 核心组件说明

### 1. 统一响应封装 (`Result<T>`)

所有 API 接口返回统一格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

使用方式：
- `Result.success()` - 无数据返回
- `Result.success(data)` - 带数据返回
- `Result.error(message)` - 错误返回

### 2. 全局异常处理 (`GlobalExceptionHandler`)

| 异常类型 | HTTP 状态码 | 说明 |
|----------|-----------|------|
| `MethodArgumentNotValidException` | 400 | 参数校验失败 |
| `BindException` | 400 | 参数绑定失败 |
| `RuntimeException` | 500 | 运行时异常 |
| `Exception` | 500 | 其他异常 |

### 3. API 文档

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **OpenAPI YAML**: 通过 Maven 插件导出到 `/docs/api/openapi.yaml`

## 开发指南

### 添加新接口

1. 在 `controller/` 包创建新的 `@RestController`
2. 使用 `@RequestMapping("/api/xxx")` 定义路径
3. 方法返回 `Result<T>` 类型
4. 添加 Swagger 注解（`@Operation`, `@ApiResponse`）

示例：
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }
}
```

### 添加新配置

在 `config/` 包创建 `@Configuration` 类。|

### 构建命令

```bash
# 编译
./mvnw clean compile

# 运行
./mvnw spring-boot:run

# 打包
./mvnw clean package

# 导出 OpenAPI 文档
./mvnw springdoc:export
```

## 后续扩展

- [ ] 数据库集成 (MySQL + MyBatis-Plus/JPA)
- [ ] 用户认证 (Spring Security + JWT)
- [ ] 服务层 (Service/Repository)
- [ ] 实体类 (Entity/DTO/VO)
- [ ] AI 服务对接
