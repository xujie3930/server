package com.szmsd.bas.api.service;

import com.szmsd.bas.dto.MeasuringProductRequest;
import com.szmsd.common.core.domain.R;

public interface BaseProductClientService {
    /**
     * 检查sku编码是否有效
     * @param code
     * @return
     */
    Boolean checkSkuValidToDelivery(String code);

    /**
     * 测量SKU
     * @param measuringProductRequest
     * @return
     */
    R measuringProduct(MeasuringProductRequest measuringProductRequest);
}
