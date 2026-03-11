package com.matrix.ai.controller;

import com.matrix.ai.common.Result;
import com.matrix.ai.dto.CalculatorRequest;
import com.matrix.ai.vo.CalculatorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 计算器接口
 */
@Slf4j
@Tag(name = "Calculator", description = "计算器接口")
@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    /**
     * 执行加减乘除运算
     *
     * @param request 请求参数（num1, num2, operator）
     * @return 计算结果
     */
    @Operation(summary = "执行计算", description = "支持加法 (add)、减法 (sub)、乘法 (mul)、除法 (div) 运算")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "计算成功",
                    content = @Content(schema = @Schema(implementation = CalculatorResponse.class))),
            @ApiResponse(responseCode = "400", description = "参数错误",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PostMapping
    public Result<CalculatorResponse> calculate(
            @Valid @RequestBody
            @Parameter(description = "计算请求参数", required = true)
            CalculatorRequest request) {

        // 验证运算符合法性
        String operator = request.getOperator().toLowerCase();
        if (!isValidOperator(operator)) {
            return Result.error(400, "不支持的运算符：" + request.getOperator() + "，支持的运算符：add, sub, mul, div");
        }

        // 验证除数不能为 0
        if ("div".equals(operator) && request.getNum2() == 0) {
            return Result.error(400, "除数不能为 0");
        }

        Double result;
        switch (operator) {
            case "add":
                result = request.getNum1() + request.getNum2();
                break;
            case "sub":
                result = request.getNum1() - request.getNum2();
                break;
            case "mul":
                result = request.getNum1() * request.getNum2();
                break;
            case "div":
                result = request.getNum1() / request.getNum2();
                break;
            default:
                return Result.error(400, "不支持的运算符：" + operator);
        }

        log.info("计算：{} {} {} = {}", request.getNum1(), operator, request.getNum2(), result);

        return Result.success(CalculatorResponse.builder()
                .num1(request.getNum1())
                .num2(request.getNum2())
                .operator(operator)
                .result(result)
                .build());
    }

    /**
     * 验证运算符是否合法
     *
     * @param operator 运算符
     * @return 是否合法
     */
    private boolean isValidOperator(String operator) {
        return "add".equals(operator) || "sub".equals(operator)
                || "mul".equals(operator) || "div".equals(operator);
    }
}
