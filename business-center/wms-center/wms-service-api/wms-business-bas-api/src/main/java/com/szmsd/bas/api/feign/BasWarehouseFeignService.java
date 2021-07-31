package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasWarehouseFeignFallback;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "FeignClient.BasWarehouseFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasWarehouseFeignFallback.class)
public interface BasWarehouseFeignService {

    /**
     * 创建/更新仓库
     *
     * @param addWarehouseRequest
     * @return
     */
    @PostMapping("/bas/warehouse/saveOrUpdate")
    R saveOrUpdate(@RequestBody AddWarehouseRequest addWarehouseRequest);

    /**
     * 根据仓库编码查询仓库信息
     *
     * @param warehouseCode warehouseCode
     * @return BasWarehouse
     */
    @RequestMapping("/bas/warehouse/queryByWarehouseCode")
    R<BasWarehouse> queryByWarehouseCode(@RequestParam("warehouseCode") String warehouseCode);

    /**
     * 根据仓库编码查询仓库信息
     *
     * @param warehouseCodes warehouseCodes
     * @return BasWarehouse
     */
    @RequestMapping("/bas/warehouse/queryByWarehouseCodes")
    R<List<BasWarehouse>> queryByWarehouseCodes(@RequestParam("warehouseCodes") List<String> warehouseCodes);

    /**
     * 查询 仓库列表
     * @param queryDTO
     * @return
     */
    @PostMapping("/bas/warehouse/open/page")
    @ApiOperation(value = "查询", notes = "仓库列表 - 分页查询")
    TableDataInfo<BasWarehouseVO> pagePost(@RequestBody BasWarehouseQueryDTO queryDTO);
}
