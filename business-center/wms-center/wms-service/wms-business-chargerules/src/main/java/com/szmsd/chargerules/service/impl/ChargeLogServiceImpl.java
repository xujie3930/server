package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.mapper.ChargeLogMapper;
import com.szmsd.chargerules.service.IChargeLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChargeLogServiceImpl implements IChargeLogService {

    @Resource
    private ChargeLogMapper chargeLogMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public int save(ChargeLog chargeLog) {
        return chargeLogMapper.insert(chargeLog);
    }

    @Override
    public ChargeLog selectLog(ChargeLogDto chargeLogDto) {
        LambdaQueryWrapper<ChargeLog> query = Wrappers.lambdaQuery();
        query.eq(ChargeLog::getOrderNo, chargeLogDto.getOrderNo());
        query.eq(ChargeLog::getPayMethod, chargeLogDto.getPayMethod());
        query.eq(ChargeLog::getOperationType, chargeLogDto.getOperationType());
        query.eq(ChargeLog::getSuccess, chargeLogDto.getSuccess());
        return chargeLogMapper.selectOne(query);
    }

    @Override
    public List<ChargeLog> selectPage(ChargeLogDto chargeLogDto) {
        LambdaQueryWrapper<ChargeLog> query = Wrappers.lambdaQuery();
        query.eq(ChargeLog::getOrderNo,chargeLogDto.getOrderNo());
        return chargeLogMapper.selectList(query);
    }
}
