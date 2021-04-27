package com.szmsd.putinstorage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.InboundReceiptExportVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import com.szmsd.putinstorage.enums.InboundReceiptEnum;

import java.util.List;

/**
 * <p>
 * rec_wareh - 入库 服务类
 * </p>
 *
 * @author liangchao
 * @since 2021-03-03
 */
public interface IInboundReceiptService extends IService<InboundReceipt> {

    List<InboundReceiptVO> selectList(InboundReceiptQueryDTO queryDto);

    InboundReceiptVO selectByWarehouseNo(String warehouseNo);

    InboundReceiptInfoVO saveOrUpdate(CreateInboundReceiptDTO createInboundReceiptDTO);

    InboundReceipt saveOrUpdate(InboundReceiptDTO inboundReceiptDTO);

    void cancel(String warehouseNo);

    InboundReceiptInfoVO queryInfo(String warehouseNo);

    void receiving(ReceivingRequest receivingRequest);

    void completed(ReceivingCompletedRequest receivingCompletedRequest);

    void updateStatus(String warehouseNo, InboundReceiptEnum.InboundReceiptStatus status);

    void updateByWarehouseNo(InboundReceipt inboundReceipt);

    void review(InboundReceiptReviewDTO inboundReceiptReviewDTO);

    void delete(String warehouseNo);

    List<InboundReceiptExportVO> selectExport(InboundReceiptQueryDTO queryDTO);
}

