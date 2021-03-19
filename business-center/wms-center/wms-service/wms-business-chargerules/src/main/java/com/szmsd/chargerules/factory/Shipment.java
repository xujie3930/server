package com.szmsd.chargerules.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.domain.DelOutbound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 出库
 */
@Slf4j
@Component
public class Shipment extends OrderType {

    @Resource
    private DelOutboundFeignService delOutboundFeignService;

    @Override
    public boolean checkOrderExist(String orderNo) {
        R<DelOutbound> details = delOutboundFeignService.details(orderNo);
        if(details.getCode() != 200) {
            log.error("checkOrderExist error: {}",details.getData());
            return false;
        }
        return details.getData() != null;
    }

}
