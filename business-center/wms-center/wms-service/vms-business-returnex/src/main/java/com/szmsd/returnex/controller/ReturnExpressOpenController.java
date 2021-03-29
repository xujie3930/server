package com.szmsd.returnex.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.returnex.dto.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.ReturnExpressAddDTO;
import com.szmsd.returnex.dto.ReturnExpressListQueryDTO;
import com.szmsd.returnex.dto.ReturnProcessingReqDTO;
import com.szmsd.returnex.service.IReturnExpressService;
import com.szmsd.returnex.vo.ReturnExpressListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("@ss.hasPermi('ReturnExpressDetail:*:*')")
@Api(tags = {"退货服务-OPEN VMS"})
@RestController
@RequestMapping("/api/return")
public class ReturnExpressOpenController extends BaseController {

    @Resource
    private IReturnExpressService returnExpressService;

    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @PostMapping("/processing")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "接收仓库退件处理", notes = "/api/return/processing #G2-接收仓库退件处理")
    public R updateProcessingInfoFromVms(@RequestBody ReturnProcessingReqDTO returnProcessingReqDTO) {
        return toOk(returnExpressService.updateProcessingInfoFromVms(returnProcessingReqDTO));
    }

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
    @ApiOperation(value = "接收仓库退件到货", notes = "/api/return/arrival #G1-接收仓库退件到货")
    public R saveArrivalInfoFormVms(@RequestBody ReturnArrivalReqDTO returnArrivalReqDTO) {
        return toOk(returnExpressService.saveArrivalInfoFormVms(returnArrivalReqDTO));
    }

}
