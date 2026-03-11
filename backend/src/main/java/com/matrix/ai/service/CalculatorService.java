package com.matrix.ai.service;

import com.matrix.ai.dto.CalculatorRequest;
import com.matrix.ai.enums.OperatorType;
import com.matrix.ai.vo.CalculationHistory;
import com.matrix.ai.vo.CalculatorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 计算器服务
 */
@Slf4j
@Service
public class CalculatorService {

    /**
     * 内存存储历史记录（线程安全）
     */
    private final Map<String, CalculationHistory> historyStore = new ConcurrentHashMap<>();

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
        historyStore.put(history.getId(), history);
        log.debug("保存历史记录：{}", history.getId());
    }

    /**
     * 获取所有历史记录（按时间倒序）
     */
    public List<CalculationHistory> getAllHistory() {
        List<CalculationHistory> list = new ArrayList<>(historyStore.values());
        // 按创建时间倒序
        list.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        return list;
    }

    /**
     * 根据 ID 获取历史记录
     */
    public Optional<CalculationHistory> getHistoryById(String id) {
        return Optional.ofNullable(historyStore.get(id));
    }

    /**
     * 删除单条历史记录
     *
     * @param id 记录 ID
     * @return 是否删除成功
     */
    public boolean deleteHistory(String id) {
        return historyStore.remove(id) != null;
    }

    /**
     * 清空所有历史记录
     */
    public void clearAllHistory() {
        historyStore.clear();
        log.info("清空所有历史记录");
    }

    /**
     * 获取历史记录数量
     */
    public int getHistoryCount() {
        return historyStore.size();
    }
}
