package com.szmsd.bas.api.service;

public interface BaseProductClientService {
    /**
     * 检查sku编码是否有效
     * @param code
     * @return
     */
    Boolean checkSkuvalidToDelivery(String code);
}
