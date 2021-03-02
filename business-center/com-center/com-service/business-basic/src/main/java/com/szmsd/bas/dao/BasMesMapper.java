package com.szmsd.bas.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasMes;
import com.szmsd.bas.domain.Mes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ziling
 * @since 2020-08-20
 */
@Mapper
public interface BasMesMapper extends BaseMapper<BasMes> {
    /**
     * 查询数据
     *
     * @param
     * @return
     */
    List<Mes> list(Mes basMes);

}
