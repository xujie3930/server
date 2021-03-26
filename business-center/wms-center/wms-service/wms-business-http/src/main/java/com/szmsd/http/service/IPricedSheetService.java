package com.szmsd.http.service;

import com.szmsd.common.core.utils.FileStream;
import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.dto.PricedSheetCodeCriteria;
import com.szmsd.http.dto.UpdatePricedSheetCommand;
import com.szmsd.http.vo.PricedSheet;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.web.multipart.MultipartFile;

public interface IPricedSheetService {

    PricedSheet info(String sheetCode);

    ResponseVO create(CreatePricedSheetCommand createPricedSheetCommand);

    ResponseVO update(UpdatePricedSheetCommand updatePricedSheetCommand);

    ResponseVO importFile(String sheetCode, MultipartFile file);

    FileStream exportFile(PricedSheetCodeCriteria pricedSheetCodeCriteria);
}
