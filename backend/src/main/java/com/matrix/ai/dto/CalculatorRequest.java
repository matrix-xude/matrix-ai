package com.matrix.ai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 计算器请求参数
 */
@Data
public class CalculatorRequest {

    /**
     * 第一个操作数
     */
    @NotNull(message = "第一个操作数不能为空")
    private Double num1;

    /**
     * 第二个操作数
     */
    @NotNull(message = "第二个操作数不能为空")
    private Double num2;

    /**
     * 运算符：add, sub, mul, div
     */
    @NotNull(message = "运算符不能为空")
    private String operator;
}
