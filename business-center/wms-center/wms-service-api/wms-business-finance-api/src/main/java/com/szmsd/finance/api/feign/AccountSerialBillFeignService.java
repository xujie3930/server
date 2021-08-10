package com.szmsd.finance.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.api.feign.factory.AccountSerialBillFeignFallback;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.enums.BusinessFssInterface;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "FeignClient.AccountSerialBillFeignService", name = BusinessFssInterface.SERVICE_NAME, fallbackFactory = AccountSerialBillFeignFallback.class)
public interface AccountSerialBillFeignService {

    @ApiOperation(value = "流水账单 - 列表")
    @PostMapping("/serialBill/list")
    R<TableDataInfo<AccountSerialBill>> listPage(@RequestBody AccountSerialBillDTO dto);

}
