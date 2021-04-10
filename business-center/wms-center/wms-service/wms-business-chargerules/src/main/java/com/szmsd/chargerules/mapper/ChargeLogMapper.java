package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.delivery.dto.DelOutboundChargeQueryDto;
import com.szmsd.delivery.vo.DelOutboundChargeListVO;

import java.util.List;

public interface ChargeLogMapper extends BaseMapper<ChargeLog> {

    List<DelOutboundChargeListVO> selectChargeLogList(DelOutboundChargeQueryDto queryDto);
}
