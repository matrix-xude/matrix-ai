# 🤖 Monorepo 项目全局指引 (CLAUDE.md)

欢迎来到本项目仓库。这是一个包含后端（Java）、Android（Kotlin）和 Web（Vue）的多端综合项目。

## 📁 项目结构 (Project Topology)
- `/backend`: Java Spring Boot 后端项目 (Maven)
- `/android`: Kotlin Jetpack Compose 移动端项目 (Gradle)
- `/web`: Vue.js 前端项目 (Vite/NPM)
- `/docs/api`: 存放由后端生成的 `openapi.yaml` 契约文件
- `/scripts`: 跨端自动化脚本

## 🎯 Agent 行为准则
1. **边界意识**：子项目 Agent 仅限在各自目录下活动。严禁跨端修改源码。
2. **契约至上**：`/docs/api/openapi.yaml` 是各端通信的唯一事实来源。
3. **协作流程**：
   - 后端修改接口后，必须运行 `mvn springdoc:export` 更新契约。
   - 前端/Android Agent 检测到契约变化后，需同步更新其 Data Models 和 API 调用层。

## 🛠️ 全局常用指令
- **后端编译**: `cd backend && ./mvnw clean compile`
- **Android 构建**: `cd android && ./gradlew assembleDebug`
- **Web 启动**: `cd web && npm run dev`
- **同步 API**: `cd backend && ./mvnw verify` (确保触发 swagger 导出)

## 📝 代码与提交规范
- **Git Commit**: 遵循 `type(scope): description` 格式 (如 `feat(backend): add user login`)。
- **注释保护**：不要删除注释，除非代码被删除或修改后注释意义改变。
- **技术偏好**:
  - Backend: Java 17, Spring Boot 3
  - Android: Kotlin, Compose, MVVM
  - Web: Vue 3 (Script Setup), Tailwind CSS