package com.matrix.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Matrix AI Backend Application
 */
@SpringBootApplication
@MapperScan("com.matrix.ai.mapper")
public class MatrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatrixApplication.class, args);
    }
}
