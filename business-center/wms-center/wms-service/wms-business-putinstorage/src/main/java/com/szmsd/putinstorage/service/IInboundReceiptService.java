package com.szmsd.putinstorage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.common.core.domain.R;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.*;
import com.szmsd.putinstorage.enums.InboundReceiptEnum;
import org.apache.poi.ss.usermodel.Workbook;

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

    void exportSku(Workbook excel, List<InboundReceiptDetailVO> details);

    List<InboundCountVO> statistics(InboundReceiptQueryDTO queryDTO);

    void arraigned(List<String> warehouseNos);

    void tracking(ReceivingTrackingRequest receivingCompletedRequest);

    List<SkuInventoryStockRangeVo> querySkuStockByRange(InventoryStockByRangeDTO inventoryStockByRangeDTO);

    int updateTrackingNo(UpdateTrackingNoRequest updateTrackingNoRequest);
}

