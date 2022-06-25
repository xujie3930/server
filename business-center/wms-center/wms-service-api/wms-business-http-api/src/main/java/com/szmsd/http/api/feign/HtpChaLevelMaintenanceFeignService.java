package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpChaLevelMaintenanceFeignFallback;
import com.szmsd.http.api.feign.fallback.HtpPricedSheetFeignFallback;
import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.dto.PricedSheetCodeCriteria;
import com.szmsd.http.dto.UpdatePricedSheetCommand;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenancePageRequest;
import com.szmsd.http.vo.PricedSheet;
import com.szmsd.http.vo.ResponseVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(contextId = "FeignClient.HtpChaLevelMaintenanceFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpChaLevelMaintenanceFeignFallback.class)
public interface HtpChaLevelMaintenanceFeignService {

    @PostMapping("/api/chaLevel/http/page")
    R<PageVO<ChaLevelMaintenanceDto>> page(@RequestBody ChaLevelMaintenancePageRequest pageDTO);

    @PostMapping("/api/chaLevel/http/create")
    R create(@RequestBody ChaLevelMaintenanceDto dto);

    @PostMapping("/api/chaLevel/http/update")
    R update(@RequestBody ChaLevelMaintenanceDto dto);

    @PostMapping("/api/chaLevel/http/delete/{id}")
    R delete(@PathVariable("id") String id);

    @PostMapping("/api/chaLevel/http/get/{id}")
    R get(@PathVariable("id") String id);

}
