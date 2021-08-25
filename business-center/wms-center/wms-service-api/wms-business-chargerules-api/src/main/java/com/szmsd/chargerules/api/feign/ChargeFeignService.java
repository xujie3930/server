package com.szmsd.chargerules.api.feign;

import com.szmsd.chargerules.api.SpecialOperationInterface;
import com.szmsd.chargerules.api.feign.factory.ChargeFeignFallback;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.ChargeFeignService", name = SpecialOperationInterface.SERVICE_NAME, fallbackFactory = ChargeFeignFallback.class)
public interface ChargeFeignService {

    @PostMapping(value = "/log/operationCharge/page")
    R<TableDataInfo<QueryChargeVO>> selectPage(@RequestBody QueryChargeDto queryDto);

    @PostMapping("/log/add")
    @ApiOperation(value = "新增扣费")
    R add(@RequestBody ChargeLog chargeLog);
}
