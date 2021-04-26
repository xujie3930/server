package com.szmsd.inventory.controller;

import com.szmsd.inventory.domain.dto.PurchaseAddDTO;
import com.szmsd.inventory.domain.dto.PurchaseInfoAddDTO;
import com.szmsd.inventory.domain.dto.PurchaseQueryDTO;
import com.szmsd.inventory.domain.vo.PurchaseInfoListVO;
import com.szmsd.inventory.domain.vo.PurchaseInfoVO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.inventory.service.IPurchaseService;
import com.szmsd.inventory.domain.Purchase;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;

import javax.annotation.Resource;

import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;


/**
 * <p>
 * 采购单 前端控制器
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */


@Api(tags = {"采购单"})
@RestController
@RequestMapping("/purchase")
public class PurchaseController extends BaseController {

    @Resource
    private IPurchaseService purchaseService;

    /**
     * 查询采购单模块列表
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询采购单模块列表", notes = "查询采购单模块列表")
    public TableDataInfo<PurchaseInfoListVO> list(PurchaseQueryDTO purchaseQueryDTO) {
        startPage();
        List<PurchaseInfoListVO> list = purchaseService.selectPurchaseList(purchaseQueryDTO);
        return getDataTable(list);
    }
    /**
     * 查询采购单模块列表
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:list')")
    @GetMapping("/client/list")
    @ApiOperation(value = "[客户端]查询采购单模块列表", notes = "查询采购单模块列表")
    public TableDataInfo<PurchaseInfoListVO> listClient(PurchaseQueryDTO purchaseQueryDTO) {
        startPage();
        List<PurchaseInfoListVO> list = purchaseService.selectPurchaseListClient(purchaseQueryDTO);
        return getDataTable(list);
    }

    /**
     * 获取采购单模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:query')")
    @GetMapping(value = "getInfo/{purchaseNo}")
    @ApiOperation(value = "获取采购单模块详细信息通过采购单号", notes = "获取采购单模块详细信息")
    public R getInfo(@PathVariable("purchaseNo") String purchaseNo) {
        return R.ok(purchaseService.selectPurchaseByPurchaseNo(purchaseNo));
    }

    /**
     * 新增采购单模块
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:add')")
    @Log(title = "采购单模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增采购单模块", notes = "新增采购单模块")
    public R addBatch(@RequestBody PurchaseInfoAddDTO purchase) {
        return toOk(purchaseService.insertPurchaseBatch(purchase));
    }

    /**
     * 删除采购单模块
     */
    @PreAuthorize("@ss.hasPermi('Purchase:Purchase:remove')")
    @Log(title = "采购单模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除采购单模块", notes = "删除采购单模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(purchaseService.deletePurchaseByIds(ids));
    }

}
