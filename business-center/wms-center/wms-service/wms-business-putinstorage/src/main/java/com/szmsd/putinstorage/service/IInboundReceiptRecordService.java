package com.szmsd.putinstorage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.putinstorage.domain.InboundReceiptRecord;
import com.szmsd.putinstorage.domain.dto.InboundReceiptRecordQueryDTO;

import java.util.List;

public interface IInboundReceiptRecordService extends IService<InboundReceiptRecord> {

    List<InboundReceiptRecord> selectList(InboundReceiptRecordQueryDTO queryDTO);

}

