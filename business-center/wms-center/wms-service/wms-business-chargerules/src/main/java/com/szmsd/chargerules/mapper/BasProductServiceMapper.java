package com.szmsd.chargerules.mapper;


import com.szmsd.chargerules.domain.BasProductService;
import com.szmsd.common.core.domain.R;
import org.apache.ibatis.annotations.Param;

public interface BasProductServiceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BasProductService record);

    int insertSelective(BasProductService record);

    BasProductService selectByPrimaryKey(@Param("productCode") String productCode);

    int updateByPrimaryKeySelective(BasProductService record);

    int updateByPrimaryKey(BasProductService record);

    BasProductService selectBasProductService(BasProductService basProductService);
}