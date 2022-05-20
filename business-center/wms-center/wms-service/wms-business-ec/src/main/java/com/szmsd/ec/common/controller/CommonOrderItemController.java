package com.szmsd.ec.common.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.ec.common.service.ICommonOrderItemService;
import com.szmsd.ec.domain.CommonOrderItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * <p>
 * 电商平台公共订单明细表 前端控制器
 * </p>
 *
 * @author zengfanlang
 * @since 2021-12-17
 */
@Api(tags = {"电商平台公共订单明细表"})
@RestController
@RequestMapping("/ec-common-order-item")
public class CommonOrderItemController extends BaseController {

    @Resource
    private ICommonOrderItemService ecCommonOrderItemService;

    /**
     * 查询电商平台公共订单明细表模块列表
     */
    @PreAuthorize("@ss.hasPermi('EcCommonOrderItem:EcCommonOrderItem:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询电商平台公共订单明细表模块列表", notes = "查询电商平台公共订单明细表模块列表")
    public TableDataInfo list(CommonOrderItem commonOrderItem) {
        startPage();
        List<CommonOrderItem> list = ecCommonOrderItemService.selectEcCommonOrderItemList(commonOrderItem);
        return getDataTable(list);
    }

    /**
     * 导出电商平台公共订单明细表模块列表
     */
    @PreAuthorize("@ss.hasPermi('EcCommonOrderItem:EcCommonOrderItem:export')")
    @Log(title = "电商平台公共订单明细表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出电商平台公共订单明细表模块列表", notes = "导出电商平台公共订单明细表模块列表")
    public void export(HttpServletResponse response, CommonOrderItem commonOrderItem) throws IOException {
        List<CommonOrderItem> list = ecCommonOrderItemService.selectEcCommonOrderItemList(commonOrderItem);
        ExcelUtil<CommonOrderItem> util = new ExcelUtil<CommonOrderItem>(CommonOrderItem.class);
        util.exportExcel(response, list, "EcCommonOrderItem");

    }

    /**
     * 获取电商平台公共订单明细表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('EcCommonOrderItem:EcCommonOrderItem:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取电商平台公共订单明细表模块详细信息", notes = "获取电商平台公共订单明细表模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(ecCommonOrderItemService.selectEcCommonOrderItemById(id));
    }

    /**
     * 新增电商平台公共订单明细表模块
     */
    @PreAuthorize("@ss.hasPermi('EcCommonOrderItem:EcCommonOrderItem:add')")
    @Log(title = "电商平台公共订单明细表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增电商平台公共订单明细表模块", notes = "新增电商平台公共订单明细表模块")
    public R add(@RequestBody CommonOrderItem commonOrderItem) {
        return toOk(ecCommonOrderItemService.insertEcCommonOrderItem(commonOrderItem));
    }

    /**
     * 修改电商平台公共订单明细表模块
     */
    @PreAuthorize("@ss.hasPermi('EcCommonOrderItem:EcCommonOrderItem:edit')")
    @Log(title = "电商平台公共订单明细表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改电商平台公共订单明细表模块", notes = "修改电商平台公共订单明细表模块")
    public R edit(@RequestBody CommonOrderItem commonOrderItem) {
        return toOk(ecCommonOrderItemService.updateEcCommonOrderItem(commonOrderItem));
    }

    /**
     * 删除电商平台公共订单明细表模块
     */
    @PreAuthorize("@ss.hasPermi('EcCommonOrderItem:EcCommonOrderItem:remove')")
    @Log(title = "电商平台公共订单明细表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除电商平台公共订单明细表模块", notes = "删除电商平台公共订单明细表模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(ecCommonOrderItemService.deleteEcCommonOrderItemByIds(ids));
    }

}
