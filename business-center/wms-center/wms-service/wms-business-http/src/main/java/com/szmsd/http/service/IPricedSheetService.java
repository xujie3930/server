package com.szmsd.http.service;

import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.dto.UpdatePricedSheetCommand;
import com.szmsd.http.vo.PricedSheet;
import com.szmsd.http.vo.ResponseVO;

public interface IPricedSheetService {

    PricedSheet info(String sheetCode);

    ResponseVO create(CreatePricedSheetCommand createPricedSheetCommand);

    ResponseVO update(UpdatePricedSheetCommand updatePricedSheetCommand);
}
