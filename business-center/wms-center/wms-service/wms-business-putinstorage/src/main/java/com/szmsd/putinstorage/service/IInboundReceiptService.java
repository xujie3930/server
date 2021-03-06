package com.szmsd.putinstorage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;

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

    void create(CreateInboundReceiptDTO createInboundReceiptDTO);

    InboundReceipt saveInboundReceipt(InboundReceiptDTO inboundReceiptDTO);

    void cancel(String warehouseNo);

    InboundReceiptInfoVO queryInfo(String warehouseNo);
}

