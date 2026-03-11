package com.matrix.ai.controller;

import com.matrix.ai.common.Result;
import com.matrix.ai.dto.CalculatorRequest;
import com.matrix.ai.service.CalculatorService;
import com.matrix.ai.vo.CalculationHistory;
import com.matrix.ai.vo.CalculatorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 计算器接口
 */
@Tag(name = "Calculator", description = "计算器接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final CalculatorService calculatorService;

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

        return Result.success(calculatorService.calculate(request));
    }

    /**
     * 获取所有计算历史记录
     *
     * @return 历史记录列表（按时间倒序）
     */
    @Operation(summary = "获取历史记录", description = "返回所有计算历史记录，按创建时间倒序排列")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = CalculationHistory.class)))
    @GetMapping("/history")
    public Result<List<CalculationHistory>> getHistory() {
        return Result.success(calculatorService.getAllHistory());
    }

    /**
     * 根据 ID 获取历史记录
     *
     * @param id 记录 ID
     * @return 历史记录详情
     */
    @Operation(summary = "获取历史记录详情", description = "根据 ID 获取单条计算历史记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(schema = @Schema(implementation = CalculationHistory.class))),
            @ApiResponse(responseCode = "404", description = "记录不存在",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("/history/{id}")
    public Result<CalculationHistory> getHistoryById(
            @PathVariable @Parameter(description = "历史记录 ID", required = true) String id) {

        return calculatorService.getHistoryById(id)
                .map(Result::success)
                .orElse(Result.error(404, "历史记录不存在：" + id));
    }

    /**
     * 删除单条历史记录
     *
     * @param id 记录 ID
     * @return 删除结果
     */
    @Operation(summary = "删除历史记录", description = "根据 ID 删除单条计算历史记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "记录不存在",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @DeleteMapping("/history/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> deleteHistory(
            @PathVariable @Parameter(description = "历史记录 ID", required = true) String id) {

        boolean deleted = calculatorService.deleteHistory(id);
        return deleted ? Result.success("删除成功", null) : Result.error(404, "历史记录不存在：" + id);
    }

    /**
     * 清空所有历史记录
     *
     * @return 清空结果
     */
    @Operation(summary = "清空历史记录", description = "删除所有计算历史记录")
    @ApiResponse(responseCode = "200", description = "清空成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    @DeleteMapping("/history")
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> clearHistory() {
        calculatorService.clearAllHistory();
        return Result.success("已清空所有历史记录", null);
    }

    /**
     * 获取历史记录数量
     *
     * @return 历史记录数量
     */
    @Operation(summary = "获取历史记录数量", description = "返回当前存储的历史记录总数")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = Result.class)))
    @GetMapping("/history/count")
    public Result<Integer> getHistoryCount() {
        return Result.success(calculatorService.getHistoryCount());
    }
}
