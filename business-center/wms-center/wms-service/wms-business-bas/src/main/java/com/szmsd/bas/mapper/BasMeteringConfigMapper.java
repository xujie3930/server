package com.szmsd.bas.mapper;


import com.szmsd.bas.domain.BasMeteringConfig;
import com.szmsd.bas.domain.BasMeteringConfigData;
import com.szmsd.bas.dto.BasMeteringConfigDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BasMeteringConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insertus(BasMeteringConfig record);

    int insertSelective(BasMeteringConfig record);

    BasMeteringConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasMeteringConfig record);

    int updateByPrimaryKey(BasMeteringConfig record);

    List<BasMeteringConfig> selectList(BasMeteringConfigDto basMeteringConfigDto);

    BasMeteringConfig  selectPrimary(BasMeteringConfig basMeteringConfig);

    BasMeteringConfig  selectUptePrimary(BasMeteringConfig basMeteringConfig);

    BasMeteringConfig  selectById(Integer id);

    List<BasMeteringConfigData>  selectjblj(BasMeteringConfigDto basMeteringConfigDto);
}