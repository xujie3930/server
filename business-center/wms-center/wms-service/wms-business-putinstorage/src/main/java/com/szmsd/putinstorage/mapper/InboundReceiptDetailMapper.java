package com.szmsd.putinstorage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.putinstorage.domain.InboundReceiptDetail;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;

import java.util.List;

public interface InboundReceiptDetailMapper extends BaseMapper<InboundReceiptDetail> {

    List<InboundReceiptDetailVO> selectList(InboundReceiptDetailQueryDTO queryDto);

}
