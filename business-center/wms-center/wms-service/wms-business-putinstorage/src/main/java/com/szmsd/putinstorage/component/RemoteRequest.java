package com.szmsd.putinstorage.component;

import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.http.api.feign.HtpInboundFeignService;
import com.szmsd.http.dto.CancelReceiptRequest;
import com.szmsd.http.dto.CreateReceiptRequest;
import com.szmsd.http.dto.ReceiptDetailInfo;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.enums.InboundReceiptEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
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
     * @param createInboundReceiptDTO
     */
    public void createInboundReceipt(CreateInboundReceiptDTO createInboundReceiptDTO) {
        CreateReceiptRequest createInboundReceipt = new CreateReceiptRequest();
        createInboundReceipt.setWarehouseCode(createInboundReceiptDTO.getWarehouseCode());
        createInboundReceipt.setOrderType(InboundReceiptEnum.OrderType.NORMAL.getValue());
        createInboundReceipt.setSellerCode(createInboundReceiptDTO.getCusCode());
        createInboundReceipt.setTrackingNumber(createInboundReceiptDTO.getDeliveryNo());
        createInboundReceipt.setRemark(createInboundReceiptDTO.getRemark());
        createInboundReceipt.setRefOrderNo(createInboundReceiptDTO.getWarehouseNo());
        createInboundReceipt.setDetails(createInboundReceiptDTO.getInboundReceiptDetailDTOS().stream().map(detail -> {
            ReceiptDetailInfo receiptDetailInfo = new ReceiptDetailInfo();
            receiptDetailInfo.setSku(detail.getSku());
            receiptDetailInfo.setQty(detail.getDeclareQty());
            receiptDetailInfo.setOriginCode(detail.getOriginCode());
            return receiptDetailInfo;
        }).collect(Collectors.toList()));
        R<CreateReceiptResponse> createReceiptResponseR = htpInboundFeignService.create(createInboundReceipt);
        resultAssert(createReceiptResponseR, "创建入库单");
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
        resultAssert(cancel, "取消入库单");
    }

    public void resultAssert(R<? extends ResponseVO> result, String api) {
        AssertUtil.isTrue(result.getCode() == HttpStatus.SUCCESS, "RemoteRequest[" + api + "失败:" +  result.getMsg() + "]");
        ResponseVO data = result.getData();
        AssertUtil.isTrue(data != null && data.getSuccess() != null && data.getSuccess() == true, "RemoteRequest[" + api + "失败:" +  getDefaultStr(data.getMessage()).concat(getDefaultStr(data.getErrors())) + "]");
    }

    public String getDefaultStr(String str) {
        return Optional.ofNullable(str).orElse("");
    }

}
