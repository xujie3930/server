package com.szmsd.chargerules.service;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.delivery.dto.DelOutboundChargeQueryDto;
import com.szmsd.delivery.vo.DelOutboundChargeListVO;

import java.util.List;

public interface IChargeLogService {

    /**
     * 保存日志
     * @param chargeLog chargeLog
     * @return result
     */
    int save(ChargeLog chargeLog);

    /**
     * 根据条件查询日志
     * @param chargeLogDto chargeLogDto
     * @return ChargeLog
     */
    ChargeLog selectLog(ChargeLogDto chargeLogDto);

    /**
     * 查询日志
     * @param chargeLogDto chargeLogDto
     * @return List
     */
    List<ChargeLog> selectPage(ChargeLogDto chargeLogDto);

    List<DelOutboundChargeListVO> selectChargeLogList(DelOutboundChargeQueryDto queryDto);
}
