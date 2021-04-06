package com.szmsd.pack.api.feign.serve;

import com.szmsd.common.core.domain.R;
import com.szmsd.pack.api.feign.serve.factory.BasFeignServiceFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName: IReturnExpressFeignClientService
 * @Description: 通过HTTP服务发起 http请求调用外部VMS接口
 * @Author: 11
 * @Date: 2021/3/27 14:21
 */
@FeignClient(contextId = "FeignClient.IBasFeignService", value = "wms-business-bas", fallbackFactory = BasFeignServiceFallback.class)
public interface IBasFeignService {

    /**
     * 查询sellerCode
     *
     * @param
     * @return
     */
    @PostMapping("/getLoginSellerCode")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    R<String> getLoginSellerCode();
}
