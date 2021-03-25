package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.dto.PricedSheetCodeCriteria;
import com.szmsd.http.dto.UpdatePricedSheetCommand;
import com.szmsd.http.service.IPricedSheetService;
import com.szmsd.http.vo.PricedSheet;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PricedSheetServiceImpl extends AbstractPricedProductHttpRequest implements IPricedSheetService {

    public PricedSheetServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public PricedSheet info(String sheetCode) {
        return JSON.parseObject(httpGet(httpConfig.getPricedSheet().getSheets(), null, sheetCode), PricedSheet.class);
    }

    @Override
    public ResponseVO create(CreatePricedSheetCommand createPricedSheetCommand) {
        return JSON.parseObject(httpPost(httpConfig.getPricedSheet().getSheets(), createPricedSheetCommand), ResponseVO.class);
    }

    @Override
    public ResponseVO update(UpdatePricedSheetCommand updatePricedSheetCommand) {
        return JSON.parseObject(httpPut(httpConfig.getPricedSheet().getSheets(), updatePricedSheetCommand, updatePricedSheetCommand.getCode()), ResponseVO.class);
    }

    @Override
    public ResponseVO importFile(String sheetCode, MultipartFile file) {
        return JSON.parseObject(httpPutMuFile(httpConfig.getPricedSheet().getSheets(), null, file, sheetCode, httpConfig.getPricedSheet().getImportFile()), ResponseVO.class);
    }

    @Override
    public FileStream exportFile(PricedSheetCodeCriteria pricedSheetCodeCriteria) {
        return httpPostFile(httpConfig.getPricedSheet().getExportFile(), pricedSheetCodeCriteria);
    }
}
