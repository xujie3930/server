package com.szmsd.putinstorage.domain.remote.request;

import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.remote.enums.InboundReceiptEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 创建入库单请求
 */
@Data
@Accessors(chain = true)
public class CreateReceiptRequest implements Serializable {

    /** 仓库代码 **/
    private String warehouseCode;

    /** 入库单类型 **/
    private String orderType;

    /** 卖家代码 **/
    private String sellerCode;

    /** 挂号 **/
    private String trackingNumber;

    /** 下单备注 **/
    private String remark;

    /** 参照单号（传OMS单号） **/
    private String refOrderNo;

    /** 收货单明细 **/
    private List<ReceiptDetailInfo> details;

    public CreateReceiptRequest() {

    }

    public CreateReceiptRequest(CreateInboundReceiptDTO createInboundReceiptDTO) {
        this.warehouseCode = createInboundReceiptDTO.getWarehouseCode();
        this.orderType = InboundReceiptEnum.OrderType.NORMAL.getKey();
        this.sellerCode = createInboundReceiptDTO.getCusCode();
        this.trackingNumber = createInboundReceiptDTO.getDeliveryNo();
        this.remark = createInboundReceiptDTO.getRemark();
        this.refOrderNo = createInboundReceiptDTO.getWarehouseNo();
        this.details = createInboundReceiptDTO.getInboundReceiptDetailDTOS().stream().map(detail -> {
            ReceiptDetailInfo receiptDetailInfo = new ReceiptDetailInfo();
            receiptDetailInfo.setSku(detail.getSku());
            receiptDetailInfo.setQty(detail.getDeclareQty());
            receiptDetailInfo.setOriginCode(detail.getOriginCode());
            return receiptDetailInfo;
        }).collect(Collectors.toList());
    }

}
