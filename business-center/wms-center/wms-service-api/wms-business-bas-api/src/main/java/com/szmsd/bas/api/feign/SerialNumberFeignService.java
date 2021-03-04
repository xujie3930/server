package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.SerialNumberFeignFallback;
import com.szmsd.bas.dto.GenerateNumberDto;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2020-11-10 010 9:59
 */
@FeignClient(contextId = "FeignClient.SerialNumberFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = SerialNumberFeignFallback.class)
public interface SerialNumberFeignService {

    /**
     * 生成流水号
     *
     * @param dto dto
     * @return ResultJson
     */
    @PostMapping(value = "/bas-serial-number/generateNumber")
    R<String> generateNumber(@RequestBody GenerateNumberDto dto);

    /**
     * 生成流水号
     *
     * @param dto dto
     * @return ResultJson
     */
    @PostMapping(value = "/bas-serial-number/generateNumbers")
    R<List<String>> generateNumbers(@RequestBody GenerateNumberDto dto);
}
