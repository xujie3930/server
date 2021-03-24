package com.szmsd.bas.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.api.domain.BasAppMes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * App消息表 Mapper 接口
 * </p>
 *
 * @author ziling
 * @since 2020-10-14
 */
@Mapper
public interface BasAppMesMapper extends BaseMapper<BasAppMes> {

    int deleteBySourceId(@Param("sourceId") String sourceId);
}
