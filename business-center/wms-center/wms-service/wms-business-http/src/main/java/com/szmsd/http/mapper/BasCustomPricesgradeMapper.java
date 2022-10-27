package com.szmsd.http.mapper;


import com.szmsd.http.domain.BasCustomPricesgrade;
import org.apache.ibatis.annotations.Param;

public interface BasCustomPricesgradeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BasCustomPricesgrade record);

    int insertSelective(BasCustomPricesgrade record);

    BasCustomPricesgrade selectByPrimaryKey(BasCustomPricesgrade basCustomPricesgrade);

    int updateByPrimaryKeySelective(BasCustomPricesgrade record);

    int updateByPrimaryKey(BasCustomPricesgrade record);

    BasCustomPricesgrade  selectByPrimaryKeys(@Param("templateId") String templateId,@Param("customprType") String customprType);
}