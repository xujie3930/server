package com.szmsd.putinstorage.component;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.http.api.feign.HtpInboundFeignService;
import com.szmsd.http.dto.CancelReceiptRequest;
import com.szmsd.http.dto.CreateReceiptRequest;
import com.szmsd.http.dto.ReceiptDetailInfo;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.enums.InboundReceiptEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * 远程请求
 */
@Component
@Slf4j
public class RemoteRequest {

    @Resource
    private HtpInboundFeignService htpInboundFeignService;

    /**
     * 创建入库单
     * @param inboundReceiptInfoVO
     */
    public void createInboundReceipt(InboundReceiptInfoVO inboundReceiptInfoVO) {

        String orderType = inboundReceiptInfoVO.getOrderType();
        InboundReceiptEnum.InboundReceiptEnumMethods orderTypeEnum = InboundReceiptEnum.InboundReceiptEnumMethods.getEnum(InboundReceiptEnum.OrderType.class, orderType);
        AssertUtil.notNull(orderType, "orderType[" + orderType + "]无效");

        CreateReceiptRequest createInboundReceipt = new CreateReceiptRequest();
        createInboundReceipt.setWarehouseCode(inboundReceiptInfoVO.getWarehouseCode());
        createInboundReceipt.setOrderType(orderTypeEnum.getValue());
        createInboundReceipt.setSellerCode(inboundReceiptInfoVO.getCusCode());
        createInboundReceipt.setTrackingNumber(inboundReceiptInfoVO.getDeliveryNo());
        createInboundReceipt.setRemark(inboundReceiptInfoVO.getRemark());
        createInboundReceipt.setRefOrderNo(inboundReceiptInfoVO.getWarehouseNo());
        createInboundReceipt.setDetails(inboundReceiptInfoVO.getInboundReceiptDetails().stream().map(detail -> {
            ReceiptDetailInfo receiptDetailInfo = new ReceiptDetailInfo();
            receiptDetailInfo.setSku(detail.getSku());
            receiptDetailInfo.setQty(detail.getDeclareQty());
            receiptDetailInfo.setOriginCode(detail.getOriginCode());
            return receiptDetailInfo;
        }).collect(Collectors.toList()));
        R<CreateReceiptResponse> createReceiptResponseR = htpInboundFeignService.create(createInboundReceipt);
        ResponseVO.resultAssert(createReceiptResponseR, "创建入库单");
    }

    /**
     * 取消入库单
     * @param orderNo
     * @param warehouseCode
     */
    public void cancelInboundReceipt(String orderNo, String warehouseCode) {
        CancelReceiptRequest cancelReceiptRequest = new CancelReceiptRequest();
        cancelReceiptRequest.setOrderNo(orderNo);
        cancelReceiptRequest.setWarehouseCode(warehouseCode);
        R<ResponseVO> cancel = htpInboundFeignService.cancel(cancelReceiptRequest);
        ResponseVO.resultAssert(cancel, "取消入库单");
    }

}
