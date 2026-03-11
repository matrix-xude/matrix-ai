package com.matrix.ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / SpringDoc 配置
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Matrix AI API")
                        .version("0.0.1")
                        .description("Matrix AI 三端互通项目后端 API 文档")
                        .contact(new Contact()
                                .name("Matrix AI Team")
                                .email("support@matrix.ai")));
    }
}
