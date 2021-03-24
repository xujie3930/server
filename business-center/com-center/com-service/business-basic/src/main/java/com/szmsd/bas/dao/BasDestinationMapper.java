package com.szmsd.bas.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.api.domain.BasDestination;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ziling
 * @since 2020-07-07
 */
@Mapper
public interface BasDestinationMapper extends BaseMapper<BasDestination> {


    public List<Map<String, Object>> selectTree(BasDestination basDestination);

    int deleteBySiteCode(String businesSiteCode);

}
