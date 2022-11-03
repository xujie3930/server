package com.szmsd.chargerules.mapper;


import com.szmsd.chargerules.domain.BasProductService;
import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.custom.BasCustomPricesgradeDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BasProductServiceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BasProductService record);

    int insertSelective(BasProductService record);

    BasProductService selectByPrimaryKey(@Param("productCode") String productCode);

    int updateByPrimaryKeySelective(BasProductService record);

    int updateByPrimaryKey(BasProductService record);

    List<BasProductService> selectBasProductService(@Param("list") List<String> list);

    List<BasCustomPricesgradeDto>  selectbasCustomPricesgradeList(BasCustomPricesgradeDto basCustomPricesgradeDto);

    String  selectProductCode(@Param("productCode") String productCode);
}