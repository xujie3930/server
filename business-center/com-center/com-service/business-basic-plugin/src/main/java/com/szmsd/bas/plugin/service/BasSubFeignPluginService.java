package com.szmsd.bas.plugin.service;

import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 10:39
 */
@FeignClient(contextId = "BasSubFeignPluginService", value = ServiceNameConstants.BUSINESS_BAS, fallbackFactory = BasSubFeignPluginServiceFallbackFactory.class)
public interface BasSubFeignPluginService {

    @ApiOperation(value = "根据code查询子类别（下拉框）")
    @GetMapping("/bas-sub/getSub")
    R<Map<String, List<BasSubWrapperVO>>> getSub(String code);

}
