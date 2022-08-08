package com.szmsd.putinstorage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.putinstorage.domain.InboundReceiptRecord;
import org.apache.ibatis.annotations.Param;

public interface InboundReceiptRecordMapper extends BaseMapper<InboundReceiptRecord> {
    String seleBaseProductSku(@Param("skus") String skus);

    String  selectinboundReBatchNum(@Param("warehouseNo") String warehouseNo);
}
