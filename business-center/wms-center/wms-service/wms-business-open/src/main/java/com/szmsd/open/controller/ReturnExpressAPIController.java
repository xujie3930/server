package com.szmsd.open.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.open.vo.ResponseVO;
import com.szmsd.returnex.api.feign.client.IReturnExpressFeignClientService;
import com.szmsd.returnex.dto.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.ReturnProcessingReqDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName: ReturnExpressAPIController
 * @Description: 开放给WMS调用接口
 * @Author: 11
 * @Date: 2021/3/26 11:42
 */
@Api(tags = {"退货服务-远程调用"})
@RestController
@RequestMapping("/api/return")
public class ReturnExpressAPIController extends BaseController {

    @Resource
    private IReturnExpressFeignClientService returnExpressService;

    /**
     * 接收VMS仓库到件信息
     * /api/return/arrival #G1-接收仓库退件到货
     *
     * @param returnArrivalReqDTO 接收VMS仓库到件信息
     * @return 操作结果
     */
    @PostMapping("/arrival")
    @ApiOperation(value = "接收仓库退件到货", notes = "/api/return/arrival #G1-接收仓库退件到货")
    public ResponseVO saveArrivalInfoFormWms(@RequestBody ReturnArrivalReqDTO returnArrivalReqDTO) {
        returnExpressService.saveArrivalInfoFormWms(returnArrivalReqDTO);
        return ResponseVO.ok();
    }

    /**
     * 接收仓库拆包明细
     * /api/return/details #G2-接收仓库拆包明细
     *
     * @param returnProcessingReqDTO 拆包明细
     * @return 操作结果
     */
    @PostMapping("/details")
    @ApiOperation(value = "接收仓库拆包明细", notes = "/api/return/details #G2-接收仓库拆包明细")
    public ResponseVO saveProcessingInfoFromVms(@RequestBody ReturnProcessingReqDTO returnProcessingReqDTO) {
        returnExpressService.saveProcessingInfoFromVms(returnProcessingReqDTO);
        return ResponseVO.ok();
    }

    /**
     * 接收WMS仓库退件处理结果 结束流程
     * /api/return/processing #G2-接收仓库退件处理
     * 更换 ->  /api/return/done #G3-接收仓库退件处理完成
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    @PostMapping("/done")
    @ApiOperation(value = "接收仓库退件处理完成", notes = "/api/return/done #G3-接收仓库退件处理完成")
    public ResponseVO updateProcessingInfoFromWms(@RequestBody ReturnProcessingReqDTO returnProcessingReqDTO) {
        returnExpressService.updateProcessingInfoFromWms(returnProcessingReqDTO);
        return ResponseVO.ok();
    }


}
