package com.szmsd.delivery.event.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.event.DelOutUpdWeightEvent;
import com.szmsd.delivery.service.IDelOutboundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DelOutUpdWeightListener {

    @EventListener
    public void onApplicationEvent(DelOutUpdWeightEvent event) {

        String orderNo = (String)event.getSource();
        if(StringUtils.isEmpty(orderNo)){
            return;
        }
        log.info("DelOutUpdWeightEvent onApplicationEvent：{}",orderNo);

        IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);

        DelOutbound outbound = iDelOutboundService.getOne(Wrappers.<DelOutbound>query().lambda().eq(DelOutbound::getOrderNo,orderNo));

        if(outbound.getState().equals(DelOutboundStateEnum.DELIVERED.getCode())) {
            iDelOutboundService.nuclearWeight(outbound);
        }

    }
}
