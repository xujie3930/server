package com.szmsd.open.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
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
    public int saveArrivalInfoFormVms(@RequestBody ReturnArrivalReqDTO returnArrivalReqDTO) {
        return returnExpressService.saveArrivalInfoFormVms(returnArrivalReqDTO);
    }

    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    @PostMapping("/processing")
    @ApiOperation(value = "接收仓库退件处理", notes = "/api/return/processing #G2-接收仓库退件处理")
    public R updateProcessingInfoFromVms(@RequestBody ReturnProcessingReqDTO returnProcessingReqDTO) {
        return toOk(returnExpressService.updateProcessingInfoFromVms(returnProcessingReqDTO));
    }
}
