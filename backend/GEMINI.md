# 🤖 Backend Agent 指南 (GEMINI.md)

后端 Java Spring Boot 项目的开发规范与指南。

## 🛠️ 技术栈

| 组件 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.2.0 |
| SpringDoc | 2.3.0 |
| Lombok | 最新 |
| Maven | 3.9+ (Wrapper) |

## 📁 项目结构

```
backend/
├── pom.xml                        # Maven 配置
├── ARCHITECTURE.md                # 架构文档
└── src/main/java/com/matrix/ai/
    ├── MatrixApplication.java     # 启动类
    ├── common/                    # 通用组件
    │   ├── Result.java            # 统一响应
    │   └── GlobalExceptionHandler.java
    ├── config/                    # 配置类
    │   └── OpenApiConfig.java
    └── controller/                # REST 控制器
```

## 🔧 常用命令

```bash
# 编译 (Windows 环境请使用 .\mvnw.cmd)
./mvnw clean compile

# 运行 (Windows 环境请使用 .\mvnw.cmd)
./mvnw spring-boot:run

# 打包 (Windows 环境请使用 .\mvnw.cmd)
./mvnw clean package

# 导出 OpenAPI 文档 (Windows 环境请使用 .\mvnw.cmd)
./mvnw springdoc:export
```

## 📝 编码规范

- **注释保护**：不要删除注释，除非代码被删除或修改后注释意义改变。
- **统一响应**：所有接口返回 `Result<T>` 格式。
- **异常处理**：使用 `GlobalExceptionHandler` 统一处理。
- **API 文档**：新接口必须添加 Swagger 注解。

## 🔄 API 变更流程

1. 修改/新增 Controller 接口
2. 添加 Swagger 注解（`@Operation`, `@ApiResponse`）
3. 运行 `./mvnw springdoc:export` 更新 `/docs/api/openapi.yaml` (Windows 环境请使用 `.\mvnw.cmd`)
4. 通知前端/Android 端同步更新