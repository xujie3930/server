package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpCustomPricesFeignFallback;
import com.szmsd.http.api.feign.fallback.HtpGradeFeignFallback;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.grade.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.HtpGradeFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpGradeFeignFallback.class)
public interface HtpGradeFeignService {


    @PostMapping("/api/grade/http/page")
    R<PageVO<GradeMainDto>> page(@RequestBody GradePageRequest pageDTO);

    @PostMapping("/api/grade/http/detailResult")
    R detailResult(@RequestBody String id);

    @PostMapping("/api/grade/http/create")
    R create(@RequestBody MergeGradeDto dto);


    @PostMapping("/api/grade/http/update")
    R update(@RequestBody MergeGradeDto dto);

    @PostMapping("/api/grade/http/detailImport")
    R detailImport(@RequestBody UpdateGradeDetailDto dto);

    @PostMapping("/api/grade/http/customUpdate")
    R customUpdate(@RequestBody UpdateGradeCustomDto dto);

}
