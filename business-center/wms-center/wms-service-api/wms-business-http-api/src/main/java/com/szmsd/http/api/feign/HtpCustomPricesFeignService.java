package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpCustomPricesFeignFallback;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.discount.DiscountMainDto;
import com.szmsd.http.dto.grade.GradeMainDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.HtpCustomPricesFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpCustomPricesFeignFallback.class)
public interface HtpCustomPricesFeignService {

    @PostMapping("/api/customPrices/http/result/{clientCode}")
    R<CustomPricesMainDto> result(@PathVariable("clientCode") String clientCode);

    @PostMapping("/api/customPrices/http/updateDiscount")
    R updateDiscount(@RequestBody UpdateCustomDiscountMainDto dto);

    @PostMapping("/api/customPrices/http/updateGrade")
    R updateGrade(@RequestBody UpdateCustomGradeMainDto dto);

    @PostMapping("/api/customPrices/http/updateGradeDetail")
    R updateGradeDetail(@RequestBody CustomGradeMainDto dto);

    @PostMapping("/api/customPrices/http/updateDiscountDetail")
    R updateDiscountDetail(@RequestBody CustomDiscountMainDto dto);

    @PostMapping("/api/customPrices/http/gradeDetailResult/{id}")
    R<GradeMainDto> gradeDetailResult(@PathVariable("id") String id);

    @PostMapping("/api/customPrices/http/discountDetailResult/{id}")
    R<DiscountMainDto> discountDetailResult(@PathVariable("id") String id);

}
