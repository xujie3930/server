package com.szmsd.putinstorage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.putinstorage.domain.InboundReceiptRecord;
import com.szmsd.putinstorage.domain.dto.InboundReceiptRecordQueryDTO;
import com.szmsd.putinstorage.mapper.InboundReceiptRecordMapper;
import com.szmsd.putinstorage.service.IInboundReceiptRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InboundReceiptRecordServiceImpl extends ServiceImpl<InboundReceiptRecordMapper, InboundReceiptRecord> implements IInboundReceiptRecordService {

    @Override
    public List<InboundReceiptRecord> selectList(InboundReceiptRecordQueryDTO queryDTO) {
        LambdaQueryWrapper<InboundReceiptRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(StringUtils.isNotEmpty(queryDTO.getWarehouseNo()), InboundReceiptRecord::getWarehouseNo, queryDTO.getWarehouseNo());
        queryWrapper.eq(StringUtils.isNotEmpty(queryDTO.getType()), InboundReceiptRecord::getType, queryDTO.getType());
        queryWrapper.ne(InboundReceiptRecord::getType, "修改");
        queryWrapper.orderByDesc(InboundReceiptRecord::getCreateTime);
        return baseMapper.selectList(queryWrapper);
    }
}

