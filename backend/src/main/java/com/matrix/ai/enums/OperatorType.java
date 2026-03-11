package com.matrix.ai.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 运算符枚举类型
 */
@Getter
@AllArgsConstructor
public enum OperatorType {

    ADD("add", "加法", "+"),
    SUB("sub", "减法", "-"),
    MUL("mul", "乘法", "*"),
    DIV("div", "除法", "/");

    private final String code;
    private final String name;
    private final String symbol;

    /**
     * 根据 code 获取运算符
     */
    @JsonCreator
    public static OperatorType fromCode(String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(op -> op.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断是否为有效运算符
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }

    @JsonValue
    public String toCode() {
        return code;
    }
}
