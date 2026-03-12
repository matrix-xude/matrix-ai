package com.matrix.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 计算历史记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("calculation_history")
public class CalculationHistory {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 第一个操作数
     */
    @TableField("num1")
    private Double num1;

    /**
     * 第二个操作数
     */
    @TableField("num2")
    private Double num2;

    /**
     * 运算符
     */
    @TableField("operator")
    private String operator;

    /**
     * 计算结果
     */
    @TableField("result")
    private Double result;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 乐观锁版本号
     */
    @Version
    @TableField("version")
    private Integer version;

    /**
     * 创建新的历史记录
     */
    public static CalculationHistory from(com.matrix.ai.vo.CalculatorResponse response) {
        return CalculationHistory.builder()
                .id(java.util.UUID.randomUUID().toString())
                .num1(response.getNum1())
                .num2(response.getNum2())
                .operator(response.getOperator())
                .result(response.getResult())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .version(0)
                .build();
    }
}
