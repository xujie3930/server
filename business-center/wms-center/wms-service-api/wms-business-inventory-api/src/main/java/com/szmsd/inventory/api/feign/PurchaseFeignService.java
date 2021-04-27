package com.szmsd.inventory.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.inventory.api.BusinessInventoryInterface;
import com.szmsd.inventory.api.factory.PurchaseFeignFallback;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName: PurchaseFeignService
 * @Description: 采购单feign
 * @Author: 11
 * @Date: 2021-04-27 11:49
 */
@FeignClient(contextId = "FeignClient.PurchaseFeignService", path = "/purchase", name = BusinessInventoryInterface.SERVICE_NAME, fallbackFactory = PurchaseFeignFallback.class)
public interface PurchaseFeignService {

    @DeleteMapping("/storage/cancel/byWarehouseNo/{warehouseNo}")
    @ApiImplicitParam(name = "warehouseNo", type = "String", value = "入库单主键id")
    @ApiOperation(value = "取消采购单入库", notes = "取消采购单入库 回调, 通过入库单id取消创建的采购单里面入库的请求数据")
    R<Integer> cancelByWarehouseNo(@PathVariable("warehouseNo") String warehouseNo);
}
