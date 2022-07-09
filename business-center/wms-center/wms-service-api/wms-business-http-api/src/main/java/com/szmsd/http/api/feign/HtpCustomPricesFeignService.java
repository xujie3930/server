package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpCustomPricesFeignFallback;
import com.szmsd.http.dto.OperationRecordDto;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.discount.DiscountMainDto;
import com.szmsd.http.dto.grade.GradeMainDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.HtpCustomPricesFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpCustomPricesFeignFallback.class)
public interface HtpCustomPricesFeignService {

    @PostMapping("/api/discount/http/operationRecord")
    R<OperationRecordDto> operationRecord(@RequestBody String id);

    @PostMapping("/api/customPrices/http/result/{clientCode}")
    R<CustomPricesPageDto> page(@PathVariable("clientCode") String clientCode);

    @PostMapping("/api/customPrices/http/updateDiscount")
    R updateDiscount(@RequestBody UpdateCustomMainDto dto);

    @PostMapping("/api/customPrices/http/updateGrade")
    R updateGrade(@RequestBody UpdateCustomMainDto dto);

    @PostMapping("/api/customPrices/http/updateGradeDetail")
    R updateGradeDetail(@RequestBody CustomGradeMainDto dto);

    @PostMapping("/api/customPrices/http/updateDiscountDetail")
    R updateDiscountDetail(@RequestBody CustomDiscountMainDto dto);

}
