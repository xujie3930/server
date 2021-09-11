package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasePackingFeignFallback;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BasePackingConditionQueryDto;
import com.szmsd.bas.dto.BasePackingDto;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.dto.CreatePackingRequest;
import com.szmsd.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.BasePackingFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasePackingFeignFallback.class)
public interface BasePackingFeignService {

    /**
     * 根据仓库，SKU查询产品信息
     *
     * @param conditionQueryDto conditionQueryDto
     * @return BaseProduct
     */
    @PostMapping("/base/packing/queryPackingList")
    R<List<BasePacking>> queryPackingList(@RequestBody BaseProductConditionQueryDto conditionQueryDto);

    /**
     * 根据编码查询
     *
     * @param conditionQueryDto conditionQueryDto
     * @return BasePacking
     */
    @PostMapping("/base/packing/queryByCode")
    R<BasePacking> queryByCode(@RequestBody BasePackingConditionQueryDto conditionQueryDto);

    @PostMapping("/base/packing/createPackings")
    R createPackings(@RequestBody CreatePackingRequest createPackingRequest);

    /**
     * 查询包装列表
     * @return
     */
    @GetMapping("/base/packing/listPacking")
    @ApiOperation(value = "查询包装列表", notes = "查询包装列表")
    R<List<BasePackingDto>> listParent();
}
