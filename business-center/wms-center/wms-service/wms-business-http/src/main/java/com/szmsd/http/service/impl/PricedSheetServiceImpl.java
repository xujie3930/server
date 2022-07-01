package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.dto.PricedSheetCodeCriteria;
import com.szmsd.http.dto.UpdatePricedGradeDto;
import com.szmsd.http.dto.UpdatePricedSheetCommand;
import com.szmsd.http.service.IPricedSheetService;
import com.szmsd.http.service.http.SaaSPricedProductRequest;
import com.szmsd.http.vo.PricedSheet;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PricedSheetServiceImpl extends SaaSPricedProductRequest implements IPricedSheetService {

    public PricedSheetServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public PricedSheet info(String sheetCode) {
        return JSON.parseObject(httpGet("", "pricedSheet.sheets", null, sheetCode), PricedSheet.class);
    }

    @Override
    public ResponseVO create(CreatePricedSheetCommand createPricedSheetCommand) {
        return JSON.parseObject(httpPost("", "pricedSheet.create", createPricedSheetCommand), ResponseVO.class);
    }

    @Override
    public ResponseVO update(UpdatePricedSheetCommand updatePricedSheetCommand) {
        return JSON.parseObject(httpPut("", "pricedSheet.update", updatePricedSheetCommand, updatePricedSheetCommand.getCode()), ResponseVO.class);
    }

    @Override
    public ResponseVO updateGrade(UpdatePricedGradeDto dto) {
        return JSON.parseObject(httpPut("", "pricedSheet.updateGrade", dto, dto.getProductCode(), dto.getSheetCode()), ResponseVO.class);
    }

    @Override
    public ResponseVO importFile(String sheetCode, MultipartFile file) {
        return JSON.parseObject(httpPutMuFile("", "pricedSheet.importFile", null, file, sheetCode), ResponseVO.class);
    }

    @Override
    public FileStream exportFile(PricedSheetCodeCriteria pricedSheetCodeCriteria) {
        return httpPostFile("", "pricedSheet.exportFile", pricedSheetCodeCriteria);
    }
}
