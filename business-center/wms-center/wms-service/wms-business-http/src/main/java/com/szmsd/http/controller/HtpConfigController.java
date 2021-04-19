package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.domain.*;
import com.szmsd.http.service.IHtpConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"服务配置"})
@RestController
@RequestMapping("/htp/config")
public class HtpConfigController extends BaseController {

    @Resource
    private IHtpConfigService htpConfigService;

    @PostMapping("/deploy")
    @ApiOperation(value = "部署")
    public R deploy(@RequestBody HtpDeployLog htpDeployLog) {
        htpConfigService.loadHtpConfig(htpDeployLog.getRemark());
        return R.ok();
    }

    @GetMapping("/deploy/queryLastDeployLog")
    @ApiOperation(value = "查询最近一次部署记录")
    public R<HtpDeployLog> queryLastDeployLog() {
        HtpDeployLog htpDeployLog = htpConfigService.selectLastDeployLog();
        return R.ok(htpDeployLog);
    }

    @GetMapping("/htpUrlGroup/query")
    @ApiOperation(value = "查询地址组")
    public R<List<HtpUrlGroup>> queryHtpUrlGroup() {
        List<HtpUrlGroup> htpUrlGroup = htpConfigService.selectHtpUrlGroup();
        return R.ok(htpUrlGroup);
    }

    @GetMapping("/htpUrl/query")
    @ApiOperation(value = "查询地址")
    public R<List<HtpUrl>> queryHtpUrl(HtpUrl htpUrl) {
        List<HtpUrl> htpUrls = htpConfigService.selectHtpUrl(htpUrl);
        return R.ok(htpUrls);
    }

    @GetMapping("/htpUrl/queryInfo/{groupId}/{serviceId}")
    @ApiOperation(value = "查询地址详情")
    public R<HtpUrl> queryHtpUrlInfo(@PathVariable("groupId") String groupId, @PathVariable("serviceId") String serviceId) {
        List<HtpUrl> htpUrls = htpConfigService.selectHtpUrl(new HtpUrl().setGroupId(groupId).setServiceId(serviceId));
        if (CollectionUtils.isEmpty(htpUrls)) {
            return R.ok();
        } else {
            return R.ok(htpUrls.get(0));
        }
    }

    @GetMapping("/htpWarehouseGroup/query")
    @ApiOperation(value = "查询仓库组")
    public R<List<HtpWarehouseGroup>> queryHtpWarehouseGroup() {
        List<HtpWarehouseGroup> htpWarehouseGroup = htpConfigService.selectHtpWarehouseGroup();
        return R.ok(htpWarehouseGroup);
    }

    @GetMapping("/htpWarehouse/query")
    @ApiOperation(value = "查询仓库分组")
    public R<List<HtpWarehouse>> queryHtpWarehouse(HtpWarehouse htpWarehouse) {
        List<HtpWarehouse> htpWarehouses = htpConfigService.selectHtpWarehouse(htpWarehouse);
        return R.ok(htpWarehouses);
    }

    @PostMapping("/htpUrlGroup/saveOrUpdate")
    @ApiOperation(value = "地址组【保存/修改】")
    public R saveOrUpdateHtpUrlGroup(@RequestBody HtpUrlGroup htpUrlGroup) {
        htpConfigService.saveOrUpdateHtpUrlGroup(htpUrlGroup);
        return R.ok();
    }

    @PutMapping("/htpUrlGroup/setDefault/{groupId}")
    @ApiOperation(value = "地址组【设置默认】")
    public R setHtpUrlGroupDefault(@PathVariable("groupId") String groupId) {
        htpConfigService.setHtpUrlGroupDefault(groupId);
        return R.ok();
    }

    @PostMapping("/htpUrl/saveOrUpdate")
    @ApiOperation(value = "地址【保存/修改】")
    public R saveOrUpdateHtpUrl(@RequestBody HtpUrl htpUrl) {
        htpConfigService.saveOrUpdateHtpUrl(htpUrl);
        return R.ok();
    }

    @PostMapping("/htpWarehouseGroup/saveOrUpdate")
    @ApiOperation(value = "仓库组【保存/修改】")
    public R saveOrUpdateHtpWarehouseGroup(@RequestBody HtpWarehouseGroup htpWarehouseGroup) {
        htpConfigService.saveOrUpdateHtpWarehouseGroup(htpWarehouseGroup);
        return R.ok();
    }

    @PostMapping("/htpWarehouse/save")
    @ApiOperation(value = "仓库组仓库关联【保存】")
    public R saveWarehouse(@RequestBody HtpWarehouse htpWarehouse) {
        htpConfigService.saveWarehouse(htpWarehouse);
        return R.ok();
    }

    @DeleteMapping("/htpWarehouse/delete")
    @ApiOperation(value = "仓库组仓库关联【移除仓库】")
    public R deleteHtpWarehouse(@RequestBody HtpWarehouse htpWarehouse) {
        htpConfigService.deleteHtpWarehouse(htpWarehouse);
        return R.ok();
    }

    @PostMapping("/htpWarehouseUrlGroup/save")
    @ApiOperation(value = "仓库组关联地址组【保存】")
    public R saveHtpWarehouseUrlGroup(@RequestBody HtpWarehouseUrlGroup htpWarehouseUrlGroup) {
        htpConfigService.saveHtpWarehouseUrlGroup(htpWarehouseUrlGroup);
        return R.ok();
    }

    @GetMapping("/htpWarehouseUrlGroup/query/{warehouseGroupId}")
    @ApiOperation(value = "查询仓库组关联地址组")
    public R<HtpWarehouseUrlGroup> queryHtpWarehouseUrlGroup(@PathVariable("warehouseGroupId") String warehouseGroupId) {
        List<HtpWarehouseUrlGroup> htpWarehouseUrlGroups = htpConfigService.selectHtpWarehouseUrlGroup(warehouseGroupId, null);
        if (CollectionUtils.isEmpty(htpWarehouseUrlGroups)) {
            return R.ok();
        } else {
            return R.ok(htpWarehouseUrlGroups.get(0));
        }
    }

}
