package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.finance.domain.ChargeRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liulei
 */
public interface ChargeRelationMapper extends BaseMapper<ChargeRelation> {

    List<ChargeRelation> findChargeRelation(@Param("businessCategory") String businessCategory, @Param("orderType") String orderType);
}
