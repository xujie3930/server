package com.szmsd.putinstorage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.putinstorage.domain.InboundReceiptRecord;
import com.szmsd.putinstorage.mapper.InboundReceiptRecordMapper;
import com.szmsd.putinstorage.service.IInboundReceiptRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InboundReceiptRecordServiceImpl extends ServiceImpl<InboundReceiptRecordMapper, InboundReceiptRecord> implements IInboundReceiptRecordService {

}

