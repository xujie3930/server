package com.szmsd.returnex.api.feign.serivice;

import com.szmsd.common.core.domain.R;
import com.szmsd.returnex.api.feign.serivice.facotry.ReturnExpressFeignFallback;
import com.szmsd.returnex.dto.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.ReturnProcessingReqDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName: IReturnExpressFeignService
 * @Description: 提供给HTTP服务外部通过feign调用的接口
 * @Author: 11
 * @Date: 2021/3/27 14:05
 */
@FeignClient(contextId = "feignClient.IReturnExpressFeignService",value = "wms-business-returnex", fallbackFactory = ReturnExpressFeignFallback.class)
public interface IReturnExpressFeignService {

    /**
     * 接收VMS仓库到件信息
     * /api/return/arrival #G1-接收仓库退件到货
     *
     * @param returnArrivalReqDTO 接收VMS仓库到件信息
     * @return 操作结果
     */
    @PostMapping("/api/return/arrival")
    @ApiOperation(value = "接收仓库退件到货", notes = "/api/return/arrival #G1-接收仓库退件到货")
    R<Integer> saveArrivalInfoFormVms(@RequestBody ReturnArrivalReqDTO returnArrivalReqDTO);

    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    @PostMapping("/api/return/processing")
    @ApiOperation(value = "接收仓库退件处理", notes = "/api/return/processing #G2-接收仓库退件处理")
    R<Integer> updateProcessingInfoFromVms(@RequestBody ReturnProcessingReqDTO returnProcessingReqDTO);
}
