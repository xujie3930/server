package com.szmsd.putinstorage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.putinstorage.domain.InboundReceiptDetail;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailQueryDTO;
import com.szmsd.putinstorage.domain.dto.InventoryStockByRangeDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.SkuInventoryStockRangeVo;

import java.util.List;

/**
 * <p>
 * rec_wareh_detail - 入库明细 服务类
 * </p>
 *
 * @author liangchao
 * @since 2021-03-03
 */
public interface IInboundReceiptDetailService extends IService<InboundReceiptDetail> {

    List<InboundReceiptDetailVO> selectList(InboundReceiptDetailQueryDTO queryDto);

    List<InboundReceiptDetailVO> selectList(InboundReceiptDetailQueryDTO queryDto, boolean isContainFile);

    InboundReceiptDetailVO selectObject(String warehouseNo, String sku);

    void saveOrUpdate(List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS, List<String> receiptDetailIds);

    void saveOrUpdate(List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS);

    InboundReceiptDetail saveOrUpdate(InboundReceiptDetailDTO inboundReceiptDetailDTO);

    void receiving(String warehouseNo, String sku, Integer qty);

    void deleteByWarehouseNo(String warehouseNo);

    void deleteAndFileByWarehouseNo(String warehouseNo);

    List<SkuInventoryStockRangeVo> querySkuStockByRange(InventoryStockByRangeDTO inventoryStockByRangeDTO);
}

