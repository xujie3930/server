package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelOutbound;
import org.slf4j.Logger;

public interface IDelOutboundBringVerifyAsyncService {

    /**
     * 提审异步处理
     *
     * @param delOutbound delOutbound
     */
    void bringVerifyAsync(DelOutbound delOutbound);
}
