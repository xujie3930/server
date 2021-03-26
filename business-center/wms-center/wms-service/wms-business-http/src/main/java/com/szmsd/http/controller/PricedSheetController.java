package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.dto.PricedSheetCodeCriteria;
import com.szmsd.http.dto.UpdatePricedSheetCommand;
import com.szmsd.http.service.IPricedSheetService;
import com.szmsd.http.vo.PricedSheet;
import com.szmsd.http.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Api(tags = {"PricedSheet"})
@RestController
@RequestMapping("/api/sheets/http")
public class PricedSheetController extends BaseController {

    @Resource
    private IPricedSheetService iPricedSheetService;

    @GetMapping("/info/{sheetCode}")
    @ApiOperation(value = "根据报价表编号获取产品报价表信息")
    public R<PricedSheet> info(@PathVariable("sheetCode") String sheetCode) {
        PricedSheet info = iPricedSheetService.info(sheetCode);
        return R.ok(info);
    }

    @PostMapping("/create")
    @ApiOperation(value = "创建报价产品报价表详情信息")
    public R<ResponseVO> create(@RequestBody CreatePricedSheetCommand createPricedSheetCommand) {
        ResponseVO create = iPricedSheetService.create(createPricedSheetCommand);
        return R.ok(create);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改报价产品报价表详情信息")
    public R<ResponseVO> update(@RequestBody UpdatePricedSheetCommand updatePricedSheetCommand) {
        ResponseVO create = iPricedSheetService.update(updatePricedSheetCommand);
        return R.ok(create);
    }

    @PutMapping(value = "{sheetCode}/importFile", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "使用file文件导入产品报价表信息")
    public R<ResponseVO> importFile(@PathVariable("sheetCode") String sheetCode, @RequestParam MultipartFile file) {
        ResponseVO importFile = iPricedSheetService.importFile(sheetCode, file);
        return R.ok(importFile);
    }

    @PostMapping("/exportFile")
    @ApiOperation(value = "导出报价表信息")
    public R<FileStream> exportFile(@RequestBody PricedSheetCodeCriteria pricedSheetCodeCriteria) {
        FileStream fileStream = iPricedSheetService.exportFile(pricedSheetCodeCriteria);
        return R.ok(fileStream);
    }

}
