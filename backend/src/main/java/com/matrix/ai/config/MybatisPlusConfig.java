package com.matrix.ai.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 配置分页插件和乐观锁插件
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置 MyBatis-Plus 拦截器
     * - 分页插件：支持 MySQL、PostgreSQL 等多种数据库
     * - 乐观锁插件：支持基于 version 字段的乐观锁
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setMaxLimit(500L);
        // 溢出总页数后是否进行处理 (true: 返回第一页; false: 抛出异常)
        paginationInterceptor.setOverflow(false);

        // 乐观锁插件
        OptimisticLockerInnerInterceptor optimisticLockerInterceptor = new OptimisticLockerInnerInterceptor();

        interceptor.addInnerInterceptor(paginationInterceptor);
        interceptor.addInnerInterceptor(optimisticLockerInterceptor);

        return interceptor;
    }
}
