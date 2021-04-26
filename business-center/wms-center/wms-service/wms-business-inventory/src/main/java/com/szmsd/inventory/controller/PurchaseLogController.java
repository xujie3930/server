package com.szmsd.inventory.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.inventory.domain.dto.PurchaseLogAddDTO;
import com.szmsd.inventory.domain.vo.PurchaseLogVO;
import com.szmsd.inventory.service.IPurchaseLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 采购单日志 前端控制器
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */


@Api(tags = {"采购单日志"})
@RestController
@RequestMapping("/purchase/log")
public class PurchaseLogController extends BaseController {

    @Resource
    private IPurchaseLogService purchaseLogService;

    /**
     * 查询采购单日志模块列表
     */
    @PreAuthorize("@ss.hasPermi('PurchaseLog:PurchaseLog:list')")
    @GetMapping("/getLogListByAssId/{id}")
    @ApiOperation(value = "查询采购单日志模块列表", notes = "查询采购单日志模块列表")
    public TableDataInfo<PurchaseLogVO> selectPurchaseLogList(@PathVariable("id") String id) {
        startPage();
        List<PurchaseLogVO> list = purchaseLogService.selectPurchaseLogList(id);
        return getDataTable(list);
    }

    /**
     * 新增采购单日志模块
     */
    @PreAuthorize("@ss.hasPermi('PurchaseLog:PurchaseLog:add')")
    @Log(title = "采购单日志模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增采购单日志模块", notes = "新增采购单日志模块")
    public R add(@RequestBody PurchaseLogAddDTO purchaseLog) {
        return toOk(purchaseLogService.insertPurchaseLog(purchaseLog));
    }

}
