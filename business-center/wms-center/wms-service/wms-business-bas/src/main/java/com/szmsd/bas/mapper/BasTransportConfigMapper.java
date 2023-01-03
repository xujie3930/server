package com.szmsd.bas.mapper;


import com.szmsd.bas.domain.BasTransportConfig;
import com.szmsd.bas.dto.BasTransportConfigDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BasTransportConfigMapper {
    int deleteByPrimaryKey(@Param("code") String code);

    int insert(BasTransportConfig record);

    int insertSelective(BasTransportConfig record);

    List<BasTransportConfig> selectByPrimaryKey(BasTransportConfigDTO queryDTO);

    int updateByPrimaryKeySelective(BasTransportConfig record);

    int updateByPrimaryKey(BasTransportConfig record);

    List<BasTransportConfig>  selectListVO(BasTransportConfig basTransportConfig);
}