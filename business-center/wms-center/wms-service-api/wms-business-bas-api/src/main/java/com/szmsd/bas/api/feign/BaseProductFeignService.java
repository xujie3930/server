package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BaseProductFeignFallback;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.dto.BaseProductMeasureDto;
import com.szmsd.bas.dto.MeasuringProductRequest;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.BaseProductFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BaseProductFeignFallback.class)
public interface BaseProductFeignService {

    @PostMapping(value = "/base/product/checkSkuValidToDelivery")
    R<Boolean> checkSkuValidToDelivery(@RequestBody BaseProduct baseProduct);

    /**
     * 查询sku列表
     *
     * @param baseProduct
     * @return
     */
    @PostMapping(value = "/base/product/listSku")
    R<List<BaseProduct>> listSku(@RequestBody BaseProduct baseProduct);

    /**
     * 查询单条sku
     *
     * @param baseProduct
     * @return
     */
    @PostMapping(value = "/base/product/getSku")
    R<BaseProduct> getSku(@RequestBody BaseProduct baseProduct);

    /**
     * 批量查询SKU数值信息
     *
     * @param codes
     * @return
     */
    @PostMapping(value = "/base/product/batchSKU")
    R<List<BaseProductMeasureDto>> batchSKU(@RequestBody List<String> codes);

    @PostMapping(value = "/base/product/measuring")
    R measuringProduct(@RequestBody MeasuringProductRequest measuringProductRequest);

    /**
     * 根据sku返回产品属性
     *
     * @param conditionQueryDto conditionQueryDto
     * @return String
     */
    @PostMapping("/base/product/listProductAttribute")
    R<List<String>> listProductAttribute(@RequestBody BaseProductConditionQueryDto conditionQueryDto);

    /**
     * 根据仓库，SKU查询产品信息
     *
     * @param conditionQueryDto conditionQueryDto
     * @return BaseProduct
     */
    @PostMapping("/base/product/queryProductList")
    R<List<BaseProduct>> queryProductList(@RequestBody BaseProductConditionQueryDto conditionQueryDto);
}
