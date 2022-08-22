package com.szmsd.delivery.timer;

import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelOutboundThirdParty;
import com.szmsd.delivery.service.IDelOutboundBringVerifyAsyncService;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundThirdPartyService;
import com.szmsd.delivery.service.wrapper.IDelOutboundAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class DelOutboundThirdPartyAsyncTask {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IDelOutboundThirdPartyService delOutboundThirdPartyService;


    @Async(ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_THIRDPARTY)
    public void asyncThirdParty(DelOutboundThirdParty delOutboundThirdParty) {
        this.handle(s -> delOutboundThirdPartyService.thirdParty(delOutboundThirdParty.getOrderNo(), delOutboundThirdParty.getRemark()), delOutboundThirdParty.getId());
    }

    private void handle(Consumer<String> consumer, Long id) {
        try {
            consumer.accept(null);
            // 处理成功
            this.delOutboundThirdPartyService.success(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 处理失败
            this.delOutboundThirdPartyService.fail(id, e.getMessage());
        }
    }
}
