package com.szmsd.bas.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasCuspriceCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 客户报价子表 Mapper 接口
 * </p>
 *
 * @author ziling
 * @since 2020-09-21
 */
@Mapper
public interface BasCuspriceCodeMapper extends BaseMapper<BasCuspriceCode> {

    int deleteByCusId(String cuspriceId);
}
