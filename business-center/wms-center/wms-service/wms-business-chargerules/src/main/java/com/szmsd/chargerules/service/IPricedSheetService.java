package com.szmsd.chargerules.service;

import com.szmsd.chargerules.dto.PricedSheetDTO;
import com.szmsd.chargerules.dto.ProductSheetGradeDTO;
import com.szmsd.chargerules.vo.PricedProductSheetVO;
import com.szmsd.chargerules.vo.PricedSheetExcelInfoVO;
import com.szmsd.chargerules.vo.PricedSheetInfoVO;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.http.dto.PricedSheetCodeCriteria;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPricedSheetService {

    List<PricedProductSheetVO> sheets(String productCode);

    PricedSheetInfoVO info(String sheetCode);

    void create(PricedSheetDTO pricedSheetDTO);

    void update(PricedSheetDTO pricedSheetDTO);

    FileStream exportFile(PricedSheetCodeCriteria pricedSheetCodeCriteria);

    void importFile(String sheetCode, MultipartFile file);

    void grade(ProductSheetGradeDTO productSheetGradeDTO);

    PricedSheetExcelInfoVO excelInfo(String sheetCode);
}
