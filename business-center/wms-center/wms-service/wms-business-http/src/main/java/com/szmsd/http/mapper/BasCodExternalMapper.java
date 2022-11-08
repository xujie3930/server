package com.szmsd.http.mapper;


import com.szmsd.http.domain.BasCodExternal;
import com.szmsd.http.dto.BasCodExternalDto;

import java.util.List;

public interface BasCodExternalMapper {
    int deleteByPrimaryKey();

    int insert(BasCodExternal record);

    int insertSelective(BasCodExternal record);

    List<BasCodExternal> selectByPrimaryKey(BasCodExternalDto basCodExternalDto);

    int updateByPrimaryKeySelective(BasCodExternal record);

    int updateByPrimaryKey(BasCodExternal record);
}