package com.matrix.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 计算器响应结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculatorResponse {

    /**
     * 第一个操作数
     */
    private Double num1;

    /**
     * 第二个操作数
     */
    private Double num2;

    /**
     * 运算符
     */
    private String operator;

    /**
     * 计算结果
     */
    private Double result;
}
