package com.szmsd.delivery.service;

import com.szmsd.delivery.config.AsyncThreadObject;
import com.szmsd.delivery.domain.DelOutbound;

public interface IDelOutboundBringVerifyAsyncService {

    /**
     * 提审异步处理
     *
     * @param orderNo orderNo
     */
    void bringVerifyAsync(String orderNo);

    /**
     * 提审异步处理
     *
     * @param delOutbound       delOutbound
     * @param asyncThreadObject asyncThreadObject
     */
    void bringVerifyAsync(DelOutbound delOutbound, AsyncThreadObject asyncThreadObject);
}
