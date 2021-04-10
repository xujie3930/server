package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;

import java.util.List;

public interface ChargeLogMapper extends BaseMapper<ChargeLog> {

    List<QueryChargeVO> selectChargeLogList(QueryChargeDto queryDto);
}
