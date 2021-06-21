package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasSellerFeignFallback;
import com.szmsd.bas.api.factory.BaseProductFeignFallback;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.ServiceConditionDto;
import com.szmsd.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.BasSellerFeignFallback", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasSellerFeignFallback.class)
public interface BasSellerFeignService {
    @PostMapping(value = "/bas/seller/getSellerCode")
    R<String> getSellerCode(@RequestBody BasSeller basSeller);
    @PostMapping(value = "/bas/seller/getLoginSellerCode")
    R<String> getLoginSellerCode();

    /**
     * 查询客户验货要求
     * @param sellerCode
     * @return
     */
    @PostMapping(value = "/bas/seller/getInspection")
    R<String> getInspection(@RequestBody String sellerCode);

    @PostMapping("/bas/seller/queryByServiceCondition")
    R<List<String>> queryByServiceCondition(@RequestBody ServiceConditionDto conditionDto);

    @PostMapping("/bas/seller/queryAllSellerCode")
    R<List<String>> queryAllSellerCode();
}
