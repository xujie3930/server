package com.szmsd.bas.api.service;

import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.dto.MeasuringProductRequest;
import com.szmsd.common.core.domain.R;

import java.util.List;

public interface BaseProductClientService {
    /**
     * 检查sku编码是否有效
     *
     * @param code
     * @return
     */
    Boolean checkSkuValidToDelivery(String code);

    /**
     * 测量SKU
     *
     * @param measuringProductRequest
     * @return
     */
    R measuringProduct(MeasuringProductRequest measuringProductRequest);

    /**
     * 根据sku返回产品属性
     *
     * @param conditionQueryDto conditionQueryDto
     * @return String
     */
    List<String> listProductAttribute(BaseProductConditionQueryDto conditionQueryDto);

    /**
     * 返回SKU属性
     *
     * @param sellerCode sellerCode
     * @param skus       sku
     * @return String
     */
    String buildShipmentType(String sellerCode, List<String> skus);

    /**
     * 根据仓库，SKU查询产品信息
     *
     * @param conditionQueryDto conditionQueryDto
     * @return BaseProduct
     */
    List<BaseProduct> queryProductList(BaseProductConditionQueryDto conditionQueryDto);
}
