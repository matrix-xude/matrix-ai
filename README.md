# Matrix-AI

一个由 后端、Android、web构成的项目，大部分由AI Agent 编辑



## 目录结构

/matrix-ai (根目录)
  ├── CLAUDE.md                # 🤖 Team Leader 的全景导航图 (定义项目拓扑、API 更新协议)
  ├── .gitignore               # 🌍 全局忽略文件 (IDE 缓存、系统临时文件)
  │
  ├── /docs                    # 📖 共享文档中心
  │   └── /api                 # 📄 API 契约存放地 (由后端 Agent 生成的 openapi.yaml)
  │
  ├── /backend                 # ☕ 后端项目 (Java / Spring Boot)
  │   ├── CLAUDE.md            # 🤖 Backend Agent 的生存手册 (Maven 指令、编码规范)
  │   ├── .gitignore           # 📦 后端专用忽略 (target/, .mvn/)
  │   ├── pom.xml              # Maven 配置 (需包含 SpringDoc 导出插件)
  │   └── src/                 # 后端源码
  │
  ├── /android                 # 📱 Android 项目 (Kotlin / Compose)
  │   ├── CLAUDE.md            # 🤖 Android Agent 的生存手册 (Gradle 指令、UI 规范)
  │   ├── .gitignore           # 📦 Android 专用忽略 (.gradle/, build/)
  │   ├── build.gradle.kts     # Gradle 配置
  │   └── app/                 # Android 源码
  │
  ├── /web                     # 🌐 Web 项目 (JavaScript / Vue)
  │   ├── CLAUDE.md            # 🤖 Web Agent 的生存手册 (NPM 指令、Vite 配置)
  │   ├── .gitignore           # 📦 Web 专用忽略 (node_modules/, dist/)
  │   ├── package.json         # 前端配置
  │   └── src/                 # Web 源码
  │
  └── /scripts                 # 🛠️ 自动化脚本 (例如：触发所有子项目构建的脚本)