package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BasWarehouseCus;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.dto.BasWarehouseStatusChangeDTO;
import com.szmsd.bas.dto.WarehouseKvDTO;
import com.szmsd.bas.service.IBasWarehouseService;
import com.szmsd.bas.vo.BasWarehouseInfoVO;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.annotation.RedisCache;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.RedisLanguageFieldEnum;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"仓库"})
@RestController
@RequestMapping("/bas/warehouse")
public class BasWarehouseController extends BaseController {

    @Resource
    private IBasWarehouseService basWarehouseService;

    @PreAuthorize("@ss.hasPermi('bas:warehouse:page')")
    @GetMapping("/page")
    @ApiOperation(value = "查询", notes = "仓库列表 - 分页查询")
    public TableDataInfo<BasWarehouseVO> page(BasWarehouseQueryDTO queryDTO) {
        startPage();
        List<BasWarehouseVO> list = basWarehouseService.selectList(queryDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('bas:warehouse:create')")
    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "# 创建/更新仓库", notes = "创建/更新仓库")
    @RedisCache(redisLanguageFieldEnum = RedisLanguageFieldEnum.ADD_WAREHOUSE_REQUEST)
    public R saveOrUpdate(@RequestBody AddWarehouseRequest addWarehouseRequest) {
        basWarehouseService.saveOrUpdate(addWarehouseRequest);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('bas:warehouse:info')")
    @GetMapping("/info/{warehouseCode}")
    @ApiOperation(value = "详情", notes = "仓库列表 - 详情（包含黑白名单）")
    public R<BasWarehouseInfoVO> info(@PathVariable("warehouseCode") String warehouseCode) {
        BasWarehouseInfoVO basWarehouseInfoVO = basWarehouseService.queryInfo(warehouseCode);
        return R.ok(basWarehouseInfoVO);
    }

    @PreAuthorize("@ss.hasPermi('bas:warehouse:savewarehousecus')")
    @PutMapping("/saveWarehouseCus")
    @ApiOperation(value = "更新仓库客户黑白名单", notes = "更新仓库客户黑白名单")
    public R saveWarehouseCus(@RequestBody List<BasWarehouseCus> basWarehouseCusList) {
        basWarehouseService.saveWarehouseCus(basWarehouseCusList);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('bas:warehouse:statuschange')")
    @PostMapping("/statusChange")
    @ApiOperation(value = "修改状态", notes = "修改状态")
    public R statusChange(@RequestBody BasWarehouseStatusChangeDTO basWarehouseStatusChangeDTO) {
        basWarehouseService.statusChange(basWarehouseStatusChangeDTO);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('bas:warehouse:queryInboundWarehouse')")
    @GetMapping("/queryInboundWarehouse")
    @ApiOperation(value = "查询仓库下拉", notes = "入库单 - 创建 - 目的仓库")
    public R<List<WarehouseKvDTO>> queryCusInboundWarehouse() {
        List<WarehouseKvDTO> kvList = basWarehouseService.selectCusInboundWarehouse();
        return R.ok(kvList);
    }

    @PreAuthorize("@ss.hasPermi('bas:warehouse:queryByWarehouseCode')")
    @RequestMapping(value = "/queryByWarehouseCode", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @ApiOperation(value = "根据仓库编码查询仓库信息")
    public R<BasWarehouse> queryByWarehouseCode(@RequestParam("warehouseCode") String warehouseCode) {
        return R.ok(this.basWarehouseService.queryByWarehouseCode(warehouseCode));
    }

    @PreAuthorize("@ss.hasPermi('bas:warehouse:queryByWarehouseCodes')")
    @RequestMapping(value = "/queryByWarehouseCodes", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @ApiOperation(value = "根据仓库编码查询仓库信息")
    public R<List<BasWarehouse>> queryByWarehouseCodes(@RequestParam("warehouseCodes") List<String> warehouseCodes) {
        return R.ok(this.basWarehouseService.queryByWarehouseCodes(warehouseCodes));
    }
}
