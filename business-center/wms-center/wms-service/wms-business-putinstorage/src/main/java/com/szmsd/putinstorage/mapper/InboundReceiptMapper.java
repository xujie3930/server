package com.szmsd.putinstorage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundCountVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptExportVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InboundReceiptMapper extends BaseMapper<InboundReceipt> {
    List<InboundReceiptVO> selectListByCondiction(InboundReceiptQueryDTO queryDTO);

    InboundReceiptInfoVO selectInfo(@Param("id") Long id, @Param("warehouseNo") String warehouseNo);

    List<InboundReceiptExportVO> selectExport(InboundReceiptQueryDTO queryDTO);

    List<InboundCountVO> statistics(InboundReceiptQueryDTO queryDTO);
}
