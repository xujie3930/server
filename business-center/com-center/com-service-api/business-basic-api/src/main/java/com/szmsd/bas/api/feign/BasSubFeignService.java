package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.factory.BasSubFeignServiceFallbackFactory;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 10:39
 */
@FeignClient(contextId = "BasSubFeignService", value = ServiceNameConstants.BUSINESS_BAS, fallbackFactory = BasSubFeignServiceFallbackFactory.class)
public interface BasSubFeignService {

//    @ApiOperation(value = "根据name，code查询子类别（下拉框）", notes = "根据name，code查询子类别列表（下拉框）")
//    @GetMapping("/bas-sub/getSubName")
//    R<?> list(String code, String name);

    @ApiOperation(value = "查询子类别列表api", notes = "查询子类别列表api")
    @GetMapping("/bas-sub/listApi")
    List<BasSub> listApi(@RequestParam("mainCode") String mainCode, @RequestParam("subValue") String subValue);
}
