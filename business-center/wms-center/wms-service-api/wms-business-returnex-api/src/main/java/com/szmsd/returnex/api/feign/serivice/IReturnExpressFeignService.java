package com.szmsd.returnex.api.feign.serivice;

import com.szmsd.common.core.domain.R;
import com.szmsd.returnex.api.feign.serivice.facotry.ReturnExpressFeignFallback;
import com.szmsd.returnex.dto.wms.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.wms.ReturnProcessingReqDTO;
import com.szmsd.returnex.vo.ReturnExpressVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName: IReturnExpressFeignService
 * @Description: 提供给HTTP服务外部通过feign调用的接口
 * @Author: 11
 * @Date: 2021/3/27 14:05
 */
@FeignClient(contextId = "FeignClient.IReturnExpressFeignService",value = "wms-business-returnex", fallbackFactory = ReturnExpressFeignFallback.class)
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
    @ApiOperation(value = "接收仓库退件处理", notes = "/api/return/processing #G3-接收仓库退件处理")
    R<Integer> updateProcessingInfoFromVms(@RequestBody ReturnProcessingReqDTO returnProcessingReqDTO);

    /**
     * 接收仓库拆包明细
     * /api/return/details #G2-接收仓库拆包明细
     *
     * @param returnProcessingReqDTO 拆包明细
     * @return 操作结果
     */
    @PostMapping("/api/return/details")
    @ApiOperation(value = "接收仓库拆包明细", notes = "/api/return/details #G2-接收仓库拆包明细")
    R<Integer> saveProcessingInfoFromVms(ReturnProcessingReqDTO returnProcessingReqDTO);

    @PatchMapping("/server/return/express/updateTrackNo/byOutBoundNo/{outBoundNo}/{trackNo}")
    @ApiOperation(value = "更新运单跟踪号-根据出库单号", notes = "退件后重派创建新出库单,回调接口")
    R<ReturnExpressVO> updateTrackNoByOutBoundNo(@PathVariable(value = "outBoundNo") String outBoundNo, @PathVariable(value = "trackNo") String trackNo);
}
