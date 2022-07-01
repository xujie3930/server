package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpPricedSheetFeignFallback;
import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.dto.PricedSheetCodeCriteria;
import com.szmsd.http.dto.UpdatePricedGradeDto;
import com.szmsd.http.dto.UpdatePricedSheetCommand;
import com.szmsd.http.vo.PricedSheet;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(contextId = "FeignClient.HtpPricedSheetFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpPricedSheetFeignFallback.class)
public interface HtpPricedSheetFeignService {

    @GetMapping("/api/sheets/http/info/{sheetCode}")
    R<PricedSheet> info(@PathVariable("sheetCode") String sheetCode);

    @PostMapping("/api/sheets/http/create")
    R<ResponseVO> create(@RequestBody CreatePricedSheetCommand createPricedSheetCommand);

    @PostMapping("/api/sheets/http/update")
    R<ResponseVO> update(@RequestBody UpdatePricedSheetCommand updatePricedSheetCommand);


    @PostMapping("/api/sheets/http/updateGrade")
    R<ResponseVO> updateGrade(@RequestBody UpdatePricedGradeDto dto);

    @PutMapping(value = "/api/sheets/http/{sheetCode}/importFile", headers = "content-type=multipart/form-data")
    R<ResponseVO> importFile(@PathVariable("sheetCode") String sheetCode, @RequestPart("file") MultipartFile file);

    @PostMapping("/api/sheets/http/exportFile")
    R<FileStream> exportFile(@RequestBody PricedSheetCodeCriteria pricedSheetCodeCriteria);
}
