package com.matrix.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matrix.ai.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 简单 CRUD 操作由 BaseMapper 提供，无需额外方法
    // 如需复杂查询，可在此添加自定义方法并在 XML 中实现
}
