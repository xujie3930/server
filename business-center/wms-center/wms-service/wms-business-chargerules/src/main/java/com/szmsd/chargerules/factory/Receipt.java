package com.szmsd.chargerules.factory;

import com.szmsd.chargerules.vo.OperationVo;
import com.szmsd.common.core.domain.R;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 入库
 */
@Slf4j
@Component
public class Receipt extends OrderType {

    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;

    @Override
    public String findOrderById(String orderNo) {
        R<InboundReceiptInfoVO> info = inboundReceiptFeignService.info(orderNo);
        if (info.getCode() == 200 && info.getData() != null) {
            return info.getData().getCusCode();
        }
        log.error("checkOrderExist error: {}", info.getData());
        return null;
    }

    @Override
    public void operationPay(OperationVo operation) {

    }

}
