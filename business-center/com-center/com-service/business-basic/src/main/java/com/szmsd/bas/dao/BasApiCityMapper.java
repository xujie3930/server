package com.szmsd.bas.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.api.domain.BasApiCity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 第三方接口 - 城市表 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-01-20
 */
@Mapper
public interface BasApiCityMapper extends BaseMapper<BasApiCity> {

    BasApiCity getBasApiCity(BasApiCity basApiCity);

}
