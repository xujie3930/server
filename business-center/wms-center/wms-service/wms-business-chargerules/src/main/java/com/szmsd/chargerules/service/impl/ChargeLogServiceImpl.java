package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.mapper.ChargeLogMapper;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ChargeLogServiceImpl implements IChargeLogService {

    @Resource
    private ChargeLogMapper chargeLogMapper;

    @Override
    public int save(ChargeLog chargeLog) {
        return chargeLogMapper.insert(chargeLog);
    }

    @Override
    public ChargeLog selectLog(ChargeLogDto chargeLogDto) {
        LambdaQueryWrapper<ChargeLog> query = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(chargeLogDto.getOrderNo())) {
            query.eq(ChargeLog::getOrderNo, chargeLogDto.getOrderNo());
        }
        if (StringUtils.isNotBlank(chargeLogDto.getPayMethod())) {
            query.eq(ChargeLog::getPayMethod, chargeLogDto.getPayMethod());
        }
        if (StringUtils.isNotBlank(chargeLogDto.getOperationType())) {
            query.eq(ChargeLog::getOperationType, chargeLogDto.getOperationType());
        }
        if (StringUtils.isNotNull(chargeLogDto.getSuccess())) {
            query.eq(ChargeLog::getSuccess, chargeLogDto.getSuccess());
        }
        return chargeLogMapper.selectOne(query);
    }
}
