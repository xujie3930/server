package com.szmsd.putinstorage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;

import java.util.List;

public interface InboundReceiptMapper extends BaseMapper<InboundReceipt> {

    List<InboundReceiptVO> selectList(InboundReceiptQueryDTO queryDTO);
}
