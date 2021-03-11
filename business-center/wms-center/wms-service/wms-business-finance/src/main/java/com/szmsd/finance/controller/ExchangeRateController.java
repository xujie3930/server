package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.domain.FssExchangeRate;
import com.szmsd.finance.dto.FssExchangeRateDTO;
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
@Api(tags = {"出库管理"})
@RestController
@RequestMapping("/exchangeRate")
public class ExchangeRateController extends BaseController {

    @Autowired
    IExchangeRateService exchangeRateService;

    @PreAuthorize("@ss.hasPermi('ExchangeRate:listPage')")
    @ApiOperation(value = "分页查询汇率信息")
    @GetMapping("/listPage")
    public TableDataInfo listPage(FssExchangeRateDTO dto){
        startPage();
        List<FssExchangeRate> list =exchangeRateService.listPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:save')")
    @ApiOperation(value = "保存费率")
    @PutMapping("/save")
    public R save(FssExchangeRateDTO dto){
        return exchangeRateService.save(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:update')")
    @ApiOperation(value = "分页查询汇率信息")
    @PostMapping("/update")
    public R update(FssExchangeRateDTO dto){
        return exchangeRateService.update(dto);
    }

}
