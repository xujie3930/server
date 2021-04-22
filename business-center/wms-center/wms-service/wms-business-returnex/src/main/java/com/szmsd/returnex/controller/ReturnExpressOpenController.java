package com.szmsd.returnex.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import com.szmsd.returnex.api.feign.client.IHttpFeignClientService;
import com.szmsd.returnex.dto.wms.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.wms.ReturnProcessingFinishReqDTO;
import com.szmsd.returnex.dto.wms.ReturnProcessingReqDTO;
import com.szmsd.returnex.service.IReturnExpressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName: ReturnExpressClientController
 * @Description: ReturnExpressController
 * @Author: 11
 * @Date: 2021/3/26 11:42
 */
@Api(tags = {"退货服务-OPEN WMS"})
@RestController
@RequestMapping("/api/return")
public class ReturnExpressOpenController extends BaseController {

    @Resource
    private IReturnExpressService returnExpressService;

    /**
     * 接收VMS仓库到件信息
     * /api/return/arrival #G1-接收仓库退件到货
     *
     * @param returnArrivalReqDTO 接收VMS仓库到件信息
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @PostMapping("/arrival")
    @ApiOperation(value = "接收仓库退件-到货", notes = "/api/return/arrival #G1-接收仓库退件到货")
    public R saveArrivalInfoFormWms(@Validated @RequestBody ReturnArrivalReqDTO returnArrivalReqDTO) {
        return toOk(returnExpressService.saveArrivalInfoFormWms(returnArrivalReqDTO));
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
    public R saveProcessingInfoFromVms(@RequestBody ReturnProcessingReqDTO returnProcessingReqDTO) {
        return toOk(returnExpressService.saveProcessingInfoFromVms(returnProcessingReqDTO));
    }

    /**
     * 接收WMS仓库退件处理结果 结束流程
     * /api/return/processing #G2-接收仓库退件处理
     * 更换 ->  /api/return/done #G3-接收仓库退件处理完成
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @PostMapping("/processing")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "接收仓库-退件处理", notes = "/api/return/done #G3-接收仓库退件处理完成")
    public R updateProcessingInfoFromWms(@Validated @RequestBody ReturnProcessingFinishReqDTO returnProcessingReqDTO) {
        return toOk(returnExpressService.finishProcessingInfoFromWms(returnProcessingReqDTO));
    }


    @Resource
    private IHttpFeignClientService httpFeignClient;

    /**
     * 创建退件预报
     * /api/return/expected #F1-VMS 创建退件预报
     *
     * @param expectedReqDTO 创建
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:query')")
    @PostMapping("/expectedCreate")
    @ApiOperation(value = "WMS-创建退件单", notes = "/api/return/expected #F1-VMS 创建退件预报 WMS-创建退件单")
    public void expectedCreate(@RequestBody CreateExpectedReqDTO expectedReqDTO) {
        CreateExpectedRespVO createExpectedRespVO = httpFeignClient.expectedCreate(expectedReqDTO);
    }

    /**
     * 接收客户提供的处理方式
     * /api/return/processing #F2-VMS 接收客户提供的处理方式
     *
     * @param processingUpdateReqDTO 更新数据
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:query')")
    @PostMapping("/processingUpdate")
    @ApiOperation(value = "WMS-接收客户提供的处理方式", notes = "/api/return/processing #F2-WMS 接收客户提供的处理方式")
    public void processingUpdate(@RequestBody ProcessingUpdateReqDTO processingUpdateReqDTO) {
        ProcessingUpdateRespVO processingUpdateRespVO = httpFeignClient.processingUpdate(processingUpdateReqDTO);
    }

}
