package com.matrix.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matrix.ai.entity.CalculationHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 计算历史记录 Mapper 接口
 */
@Mapper
public interface CalculationHistoryMapper extends BaseMapper<CalculationHistory> {

    // 简单 CRUD 操作由 BaseMapper 提供，无需额外方法
    // 如需复杂查询，可在此添加自定义方法并在 XML 中实现
}
