package com.szmsd.putinstorage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.putinstorage.domain.InboundReceiptDetail;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailQueryDTO;
import com.szmsd.putinstorage.domain.dto.InventoryStockByRangeDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.SkuInventoryStockRangeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InboundReceiptDetailMapper extends BaseMapper<InboundReceiptDetail> {

    List<InboundReceiptDetailVO> selectList(InboundReceiptDetailQueryDTO queryDto);

    void deleteByWarehouseNo(String warehouseNo);

    List<SkuInventoryStockRangeVo> querySkuStockByRange(@Param("cm") InventoryStockByRangeDTO inventoryStockByRangeDTO, @Param("userId") Long userId);

    int checkPackageTransfer(String deliveryNo);
}
