package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpPricedSheetFeignFallback;
import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.dto.UpdatePricedSheetCommand;
import com.szmsd.http.vo.PricedSheet;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.HtpPricedSheetFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpPricedSheetFeignFallback.class)
public interface HtpPricedSheetFeignService {

    @GetMapping("/api/sheets/http/info/{sheetCode}")
    R<PricedSheet> info(@PathVariable("sheetCode") String sheetCode);

    @PostMapping("/api/sheets/http/create")
    R<ResponseVO> create(@RequestBody CreatePricedSheetCommand createPricedSheetCommand);

    @PostMapping("/api/sheets/http/update")
    R<ResponseVO> update(@RequestBody UpdatePricedSheetCommand updatePricedSheetCommand);
}
