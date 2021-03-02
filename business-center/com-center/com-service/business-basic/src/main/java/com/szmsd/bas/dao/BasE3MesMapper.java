package com.szmsd.bas.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasE3Mes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * E3消息表 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2020-11-28
 */
@Mapper
public interface BasE3MesMapper extends BaseMapper<BasE3Mes> {

    int deleteBySourceId(@Param("sourceId") String sourceId);

    int batchDel(@Param("idList") List<String> idList);
}
