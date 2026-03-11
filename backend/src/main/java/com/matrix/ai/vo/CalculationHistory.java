package com.matrix.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 计算历史记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationHistory {

    /**
     * 记录 ID
     */
    private String id;

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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建新的历史记录
     */
    public static CalculationHistory from(CalculatorResponse response) {
        return CalculationHistory.builder()
                .id(UUID.randomUUID().toString())
                .num1(response.getNum1())
                .num2(response.getNum2())
                .operator(response.getOperator())
                .result(response.getResult())
                .createTime(LocalDateTime.now())
                .build();
    }
}
