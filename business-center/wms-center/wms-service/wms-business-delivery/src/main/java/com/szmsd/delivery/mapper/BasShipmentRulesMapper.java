package com.szmsd.delivery.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.delivery.domain.BasFile;
import com.szmsd.delivery.domain.BasShipmentRules;
import com.szmsd.delivery.dto.BasShipmentRulesDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface BasShipmentRulesMapper extends BaseMapper<BasShipmentRules> {
    int deleteByPrimaryKey(Integer id);

    int insert(BasShipmentRules record);

    int insertSelective(BasShipmentRules record);

    List<BasShipmentRules> selectByPrimaryKey(BasShipmentRulesDto basShipmentRulesDto);

    int updateByPrimaryKeySelective(BasShipmentRules record);

    int updateByPrimaryKey(BasShipmentRules record);

    List<BasShipmentRules> selectLists(BasShipmentRulesDto basShipmentRulesDto);

    String  selectserviceChannelName(@Param("productCode") String productCode);

    List<BasShipmentRules>  selectListus(BasShipmentRules basShipmentRules);

    void deleteByPrimaryKeyus(BasShipmentRules basShipmentRules);

    BasShipmentRules selectbasShipmentRules(BasShipmentRulesDto basShipmentRulesDto);

    BasShipmentRules selectbasShipmentRulesu(BasShipmentRulesDto basShipmentRulesDto);

}