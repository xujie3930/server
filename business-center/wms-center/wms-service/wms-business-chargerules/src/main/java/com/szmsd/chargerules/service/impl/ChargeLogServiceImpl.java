package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.mapper.ChargeLogMapper;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
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
        if (StringUtils.isNotBlank(chargeLogDto.getOrderNo())) {
            query.eq(ChargeLog::getOrderNo, chargeLogDto.getOrderNo());
        }
        if (StringUtils.isNotBlank(chargeLogDto.getOperationPayMethod())) {
            query.eq(ChargeLog::getOperationPayMethod, chargeLogDto.getOperationPayMethod());
        }
        if (StringUtils.isNotBlank(chargeLogDto.getPayMethod())) {
            query.eq(ChargeLog::getPayMethod, chargeLogDto.getPayMethod());
        }
        if (StringUtils.isNotBlank(chargeLogDto.getOperationType())) {
            query.eq(ChargeLog::getOperationType, chargeLogDto.getOperationType());
        }
        if (StringUtils.isNotBlank(chargeLogDto.getWarehouseCode())) {
            query.eq(ChargeLog::getWarehouseCode, chargeLogDto.getWarehouseCode());
        }
        if (chargeLogDto.getSuccess() != null) {
            query.eq(ChargeLog::getSuccess, chargeLogDto.getSuccess());
        }
        if (chargeLogDto.getHasFreeze() != null) {
            query.eq(ChargeLog::getHasFreeze, chargeLogDto.getHasFreeze());
        }
        query.orderByDesc(ChargeLog::getId).last("LIMIT 1");
        return chargeLogMapper.selectOne(query);
    }

    @Override
    public List<ChargeLog> selectPage(ChargeLogDto chargeLogDto) {
        LambdaQueryWrapper<ChargeLog> query = Wrappers.lambdaQuery();
        query.eq(ChargeLog::getOrderNo, chargeLogDto.getOrderNo());
        return chargeLogMapper.selectList(query);
    }

    @Override
    public List<QueryChargeVO> selectChargeLogList(QueryChargeDto queryDto) {
        return chargeLogMapper.selectChargeLogList(queryDto);
    }

    @Override
    public int update(Long id) {
        LambdaUpdateWrapper<ChargeLog> update = Wrappers.lambdaUpdate();
        update.set(ChargeLog::getHasFreeze, false).eq(ChargeLog::getId, id);
        return chargeLogMapper.update(null, update);
    }
}
