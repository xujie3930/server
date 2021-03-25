package com.szmsd.chargerules.service;

import com.szmsd.chargerules.dto.PricedSheetDTO;
import com.szmsd.chargerules.vo.PricedProductSheetVO;
import com.szmsd.chargerules.vo.PricedSheetInfoVO;

import java.util.List;

public interface IPricedSheetService {

    List<PricedProductSheetVO> sheets(String productCode);

    PricedSheetInfoVO info(String sheetCode);

    void create(PricedSheetDTO pricedSheetDTO);

    void update(PricedSheetDTO pricedSheetDTO);
}
