package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.finance.domain.ExchangeRate;
import com.szmsd.finance.dto.ExchangeRateDTO;
import com.szmsd.finance.service.IExchangeRateService;
import com.szmsd.finance.util.DownloadTemplateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author liulei
 */
@Api(tags = {"汇率管理"})
@RestController
@RequestMapping("/exchangeRate")
@Slf4j
public class ExchangeRateController extends FssBaseController {

    @Autowired
    IExchangeRateService exchangeRateService;

    @AutoValue
    @PreAuthorize("@ss.hasPermi('ExchangeRate:listPage')")
    @ApiOperation(value = "分页查询汇率信息")
    @GetMapping("/listPage")
    public TableDataInfo listPage(ExchangeRateDTO dto){
        startPage();
        String len=getLen();
        dto.setLen(len);
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
    @AutoValue
    public R selectRate(@RequestParam("currencyFromCode") String currencyFromCode,@RequestParam("currencyToCode") String currencyToCode){
        return exchangeRateService.selectRate(currencyFromCode,currencyToCode);
    }

    @ApiOperation(value = "汇率业务 - 下载模版")
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "exchangeRateTemplate");
    }

    @PostMapping("/uploadExchangeRate")
    @ApiOperation(value = "汇率导入")
    public R uploadExchangeRate(@RequestPart("file") MultipartFile file) throws IOException {

        return exchangeRateService.uploadExchangeRate(file);
    }

}
