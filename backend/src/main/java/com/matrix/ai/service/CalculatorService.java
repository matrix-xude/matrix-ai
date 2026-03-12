package com.matrix.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matrix.ai.dto.CalculatorRequest;
import com.matrix.ai.entity.CalculationHistory;
import com.matrix.ai.enums.OperatorType;
import com.matrix.ai.mapper.CalculationHistoryMapper;
import com.matrix.ai.vo.CalculatorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 计算器服务
 */
@Slf4j
@Service
public class CalculatorService {

    private final CalculationHistoryMapper calculationHistoryMapper;

    public CalculatorService(CalculationHistoryMapper calculationHistoryMapper) {
        this.calculationHistoryMapper = calculationHistoryMapper;
    }

    /**
     * 执行计算
     *
     * @param request 计算请求
     * @return 计算结果
     */
    public CalculatorResponse calculate(CalculatorRequest request) {
        OperatorType operator = request.getOperator();
        double num1 = request.getNum1();
        double num2 = request.getNum2();

        // 验证除数不能为 0
        if (operator == OperatorType.DIV && num2 == 0) {
            throw new ArithmeticException("除数不能为 0");
        }

        double result = switch (operator) {
            case ADD -> num1 + num2;
            case SUB -> num1 - num2;
            case MUL -> num1 * num2;
            case DIV -> num1 / num2;
        };

        log.info("计算：{} {} {} = {}", num1, operator.getSymbol(), num2, result);

        CalculatorResponse response = CalculatorResponse.builder()
                .num1(num1)
                .num2(num2)
                .operator(operator.getCode())
                .result(result)
                .build();

        // 保存历史记录
        saveHistory(response);

        return response;
    }

    /**
     * 保存计算历史记录
     */
    private void saveHistory(CalculatorResponse response) {
        CalculationHistory history = CalculationHistory.from(response);
        calculationHistoryMapper.insert(history);
        log.debug("保存历史记录：{}", history.getId());
    }

    /**
     * 获取所有历史记录（按时间倒序）
     */
    public List<com.matrix.ai.vo.CalculationHistory> getAllHistory() {
        LambdaQueryWrapper<CalculationHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CalculationHistory::getCreateTime);
        List<CalculationHistory> entities = calculationHistoryMapper.selectList(wrapper);
        return entities.stream().map(this::entityToVo).collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取历史记录
     */
    public Optional<com.matrix.ai.vo.CalculationHistory> getHistoryById(String id) {
        CalculationHistory history = calculationHistoryMapper.selectById(id);
        return Optional.ofNullable(history).map(this::entityToVo);
    }

    /**
     * Entity 转 VO
     */
    private com.matrix.ai.vo.CalculationHistory entityToVo(CalculationHistory entity) {
        return com.matrix.ai.vo.CalculationHistory.builder()
                .id(entity.getId())
                .num1(entity.getNum1())
                .num2(entity.getNum2())
                .operator(entity.getOperator())
                .result(entity.getResult())
                .createTime(entity.getCreateTime())
                .build();
    }

    /**
     * 删除单条历史记录
     *
     * @param id 记录 ID
     * @return 是否删除成功
     */
    public boolean deleteHistory(String id) {
        return calculationHistoryMapper.deleteById(id) > 0;
    }

    /**
     * 清空所有历史记录
     */
    public void clearAllHistory() {
        LambdaQueryWrapper<CalculationHistory> wrapper = new LambdaQueryWrapper<>();
        calculationHistoryMapper.delete(wrapper);
        log.info("清空所有历史记录");
    }

    /**
     * 获取历史记录数量
     */
    public int getHistoryCount() {
        return Math.toIntExact(calculationHistoryMapper.selectCount(null));
    }
}
