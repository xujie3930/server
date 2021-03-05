package com.szmsd.putinstorage.component;

import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.putinstorage.domain.remote.request.CancelReceiptRequest;
import com.szmsd.putinstorage.domain.remote.request.CreateReceiptRequest;
import com.szmsd.putinstorage.domain.remote.response.BaseOperationResponse;
import com.szmsd.putinstorage.domain.remote.response.CreateReceiptResponse;
import com.szmsd.putinstorage.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 远程请求
 */
@Component
@Slf4j
public class RemoteRequest {

    @Value("${inbound.receipt}")
    private String INBOUND_RECEIPT;

    /**
     * 创建入库单
     *
     * @param createReceiptRequest
     */
    public void createInboundReceipt(CreateReceiptRequest createReceiptRequest) {
        try {
            ResponseEntity<CreateReceiptResponse> responseEntity = RestTemplateUtils.post(INBOUND_RECEIPT, createReceiptRequest, CreateReceiptResponse.class);
            CreateReceiptResponse body = responseEntity.getBody();
            AssertUtil.isTrue(body.getSuccess(), body.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("创建入库单失败：RemoteRequest");
        }
    }

    /**
     * 取消入库单
     *
     * @param cancelReceiptRequest
     */
    public void cancelInboundReceipt(CancelReceiptRequest cancelReceiptRequest) {
        try {
            ResponseEntity<BaseOperationResponse> resultEntity = RestTemplateUtils.exchange(INBOUND_RECEIPT, HttpMethod.DELETE, null, BaseOperationResponse.class, cancelReceiptRequest);
            BaseOperationResponse body = resultEntity.getBody();
            AssertUtil.isTrue(body.getSuccess(), body.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("取消入库单失败：RemoteRequest");
        }
    }

}
