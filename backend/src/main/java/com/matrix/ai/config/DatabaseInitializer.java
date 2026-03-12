package com.matrix.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 数据库初始化器
 * <p>
 * 在应用启动时自动检测并创建数据库，避免手动创建的繁琐步骤。
 * 仅在开发环境启用。
 */
@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.auto-create-database:true}")
    private boolean autoCreateDatabase;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!autoCreateDatabase) {
            logger.info("Auto database creation is disabled, skipping...");
            return;
        }

        // 1. 从 URL 提取数据库名称
        String databaseName = extractDatabaseName(jdbcUrl);
        if (databaseName == null || databaseName.isEmpty()) {
            logger.warn("Cannot extract database name from JDBC URL, skipping database creation");
            return;
        }

        // 2. 建立不带数据库名的连接（连接到 MySQL 服务器）
        String serverUrl = jdbcUrl.replaceFirst("/" + databaseName, "");

        logger.info("Checking if database '{}' exists...", databaseName);

        try (Connection connection = DriverManager.getConnection(serverUrl, username, password)) {
            // 3. 检查数据库是否存在
            boolean databaseExists = checkDatabaseExists(connection, databaseName);

            if (!databaseExists) {
                // 4. 创建数据库
                logger.info("Database '{}' does not exist, creating...", databaseName);
                createDatabase(connection, databaseName);
                logger.info("Database '{}' created successfully.", databaseName);
            } else {
                logger.info("Database '{}' already exists, skipping creation.", databaseName);
            }
        } catch (Exception e) {
            logger.error("Failed to check/create database '{}': {}", databaseName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 从 JDBC URL 中提取数据库名称
     * <p>
     * 例如：jdbc:mysql://localhost:3306/matrix_ai?... -> matrix_ai
     *
     * @param jdbcUrl JDBC 连接 URL
     * @return 数据库名称
     */
    private String extractDatabaseName(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
            return null;
        }

        // 匹配 jdbc:mysql://host:port/databaseName?...
        // 先去掉协议头
        String urlWithoutProtocol = jdbcUrl;
        if (jdbcUrl.startsWith("jdbc:mysql://")) {
            urlWithoutProtocol = jdbcUrl.substring("jdbc:mysql://".length());
        }

        // 找到第一个斜杠后的部分（即 host:port/ 之后的部分）
        int firstSlashIndex = urlWithoutProtocol.indexOf('/');
        if (firstSlashIndex == -1 || firstSlashIndex >= urlWithoutProtocol.length() - 1) {
            return null;
        }

        // 从第一个斜杠后开始，找到问号（参数开始）或冒号（如果有端口后的额外内容）
        String afterHostPort = urlWithoutProtocol.substring(firstSlashIndex + 1);

        // 找到问号位置（参数开始）
        int questionMarkIndex = afterHostPort.indexOf('?');
        String dbNamePart = (questionMarkIndex != -1)
                ? afterHostPort.substring(0, questionMarkIndex)
                : afterHostPort;

        // 移除可能的路径分隔符
        return dbNamePart.trim();
    }

    /**
     * 检查数据库是否存在
     *
     * @param connection   数据库连接
     * @param databaseName 数据库名称
     * @return 数据库是否存在
     * @throws Exception SQL 异常
     */
    private boolean checkDatabaseExists(Connection connection, String databaseName) throws Exception {
        String sql = "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, databaseName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * 创建数据库
     * <p>
     * 使用 utf8mb4 字符集和 utf8mb4_general_ci 排序规则
     *
     * @param connection   数据库连接
     * @param databaseName 数据库名称
     * @throws Exception SQL 异常
     */
    private void createDatabase(Connection connection, String databaseName) throws Exception {
        String sql = "CREATE DATABASE IF NOT EXISTS `" + databaseName +
                     "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }
}
