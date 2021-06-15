package com.szmsd.putinstorage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.putinstorage.domain.InboundReceiptRecord;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class InboundReceiptRecordAsyncService {

    @Resource
    private IInboundReceiptService iInboundReceiptService;

    @Resource
    private IInboundReceiptRecordService iInboundReceiptRecordService;

    @Async
    public void saveRecord(InboundReceiptRecord inboundReceiptRecord) {
        if ("创建".equals(inboundReceiptRecord.getType())) {
            LambdaQueryWrapper<InboundReceiptRecord> queryWrapper = new QueryWrapper<InboundReceiptRecord>().lambda();
            queryWrapper.eq(InboundReceiptRecord::getType, inboundReceiptRecord.getType()).eq(InboundReceiptRecord::getWarehouseNo, inboundReceiptRecord.getWarehouseNo());
            InboundReceiptRecord one = iInboundReceiptRecordService.getOne(queryWrapper);
            if (one != null) {
                inboundReceiptRecord.setType("修改");
                String remark = inboundReceiptRecord.getRemark();
                if (StringUtils.isNotEmpty(remark)) {
                    inboundReceiptRecord.setRemark(remark.replaceAll("创建", "修改"));
                }
            }
        }
        String warehouseNo = inboundReceiptRecord.getWarehouseNo();
        InboundReceiptVO inboundReceiptVO = iInboundReceiptService.selectByWarehouseNo(warehouseNo);
        if (inboundReceiptVO == null) {
            log.info("入库单[{}]不存在： {}", warehouseNo, inboundReceiptRecord);
        } else {
            inboundReceiptRecord.setWarehouseCode(inboundReceiptVO.getWarehouseCode());
        }
        log.info("保存入库单日志: {}", inboundReceiptRecord);
        iInboundReceiptRecordService.save(inboundReceiptRecord);
    }

}

