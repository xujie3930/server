package com.szmsd.chargerules.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.domain.BasProductService;
import com.szmsd.http.dto.custom.BasCustomPricesgradeDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BasProductServiceMapper extends BaseMapper<BasProductService> {
    int deleteByPrimaryKey(Integer id);

    int insert(BasProductService record);

    int insertSelective(BasProductService record);

    BasProductService selectByPrimaryKey(@Param("productCode") String productCode);

    int updateByPrimaryKeySelective(BasProductService record);

    int updateByPrimaryKey(BasProductService record);

    List<BasProductService> selectBasProductService(@Param("list") List<String> list);

    List<BasCustomPricesgradeDto>  selectbasCustomPricesgradeList(BasCustomPricesgradeDto basCustomPricesgradeDto);

    Map selectProductCode(@Param("productCode") String productCode);
}