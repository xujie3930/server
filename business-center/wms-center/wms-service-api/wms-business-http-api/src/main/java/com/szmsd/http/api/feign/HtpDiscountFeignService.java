package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpDiscountFeignFallback;
import com.szmsd.http.dto.discount.DiscountPageRequest;
import com.szmsd.http.dto.discount.MergeDiscountDto;
import com.szmsd.http.dto.discount.UpdateDiscountCustomDto;
import com.szmsd.http.dto.discount.UpdateDiscountDetailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.HtpDiscountFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpDiscountFeignFallback.class)
public interface HtpDiscountFeignService {


    @PostMapping("/api/discount/http/page")
    R<PageVO> page(@RequestBody DiscountPageRequest pageDTO);

    @PostMapping("/api/discount/http/detailResult")
    R detailResult(@RequestBody String id);

    @PostMapping("/api/discount/http/create")
    R create(@RequestBody MergeDiscountDto dto);


    @PostMapping("/api/discount/http/update")
    R update(@RequestBody MergeDiscountDto dto);

    @PostMapping("/api/discount/http/detailImport")
    R detailImport(@RequestBody UpdateDiscountDetailDto dto);

    @PostMapping("/api/discount/http/customUpdate")
    R customUpdate(@RequestBody UpdateDiscountCustomDto dto);

}
