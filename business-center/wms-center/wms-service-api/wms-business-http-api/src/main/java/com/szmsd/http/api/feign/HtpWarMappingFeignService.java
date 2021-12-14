package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpShipmentOrderFeignFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @ClassName: HtpWarMappingFeignService
 * @Description: 仓库映射feign调用
 * @Author: 11
 * @Date: 2021-12-14 15:10
 */
@FeignClient(contextId = "FeignClient.HtpWarMappingFeignService", path = "/htpWarehouseMapping", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpShipmentOrderFeignFallback.class)
public interface HtpWarMappingFeignService {

    @GetMapping("/getMappingWarCode/{warehouseCode}")
    @ApiOperation(value = "查询映射的仓库编码", notes = "查询映射的仓库编码")
    R<String> getMappingWarCode(@PathVariable("warehouseCode") @Valid @NotBlank String warehouseCode);

}
