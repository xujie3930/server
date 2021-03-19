package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.domain.ExchangeRate;
import com.szmsd.finance.dto.ExchangeRateDTO;
import com.szmsd.finance.service.IExchangeRateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liulei
 */
@Api(tags = {"汇率管理"})
@RestController
@RequestMapping("/exchangeRate")
public class ExchangeRateController extends FssBaseController {

    @Autowired
    IExchangeRateService exchangeRateService;

    @PreAuthorize("@ss.hasPermi('ExchangeRate:listPage')")
    @ApiOperation(value = "分页查询汇率信息")
    @GetMapping("/listPage")
    public TableDataInfo listPage(ExchangeRateDTO dto){
        startPage();
        List<ExchangeRate> list =exchangeRateService.listPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:save')")
    @ApiOperation(value = "保存汇率")
    @PostMapping("/save")
    public R save(@RequestBody ExchangeRateDTO dto){
        return exchangeRateService.save(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:update')")
    @ApiOperation(value = "更新汇率")
    @PutMapping("/update")
    public R update(@RequestBody ExchangeRateDTO dto){
        return exchangeRateService.update(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:delete')")
    @ApiOperation(value = "删除汇率")
    @DeleteMapping("/delete")
    public R delete(@RequestParam("id") Long id){
        return exchangeRateService.delete(id);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:selectRate')")
    @ApiOperation(value = "汇率转换查询")
    @GetMapping("/selectRate")
    public R selectRate(@RequestParam("currencyFromId") Long currencyFromId,@RequestParam("currencyToId")Long currencyToId){
        return exchangeRateService.selectRate(currencyFromId,currencyToId);
    }

}
