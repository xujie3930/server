package com.szmsd.http.mapper;


import com.szmsd.http.domain.BasCustomPricesgrade;
import com.szmsd.http.dto.custom.UpdateCustomMainDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BasCustomPricesgradeMapper {
    int deleteByPrimaryKey(@Param("sellerCode") String sellerCode,@Param("customprType") String customprType);

    int insert(BasCustomPricesgrade record);

    int insertSelective(BasCustomPricesgrade record);

    List<BasCustomPricesgrade> selectByPrimaryKey(BasCustomPricesgrade basCustomPricesgrade);

    int updateByPrimaryKeySelective(UpdateCustomMainDto record);

    int updateByPrimaryKeySelectiveby(BasCustomPricesgrade record);



    int updateByPrimaryKey(BasCustomPricesgrade record);

    BasCustomPricesgrade  selectByPrimaryKeys(@Param("templateId") String templateId,@Param("customprType") String customprType);
}