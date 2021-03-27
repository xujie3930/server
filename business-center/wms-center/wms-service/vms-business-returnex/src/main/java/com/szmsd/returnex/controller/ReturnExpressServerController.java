package com.szmsd.returnex.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.returnex.dto.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.ReturnExpressAssignDTO;
import com.szmsd.returnex.dto.ReturnExpressListQueryDTO;
import com.szmsd.returnex.dto.ReturnProcessingReqDTO;
import com.szmsd.returnex.service.IReturnExpressService;
import com.szmsd.returnex.vo.ReturnExpressListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName: ReturnExpressServerController
 * @Description: ReturnExpressController
 * @Author: 11
 * @Date: 2021/3/26 11:42
 */
@Api(tags = {"退货服务-管理端"})
@RestController
@RequestMapping("/server/return/express")
public class ReturnExpressServerController extends BaseController {

    @Resource
    private IReturnExpressService returnExpressService;

    /**
     * 退件单列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    //@PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/page")
    @ApiOperation(value = "退件单列表 - 分页")
    public TableDataInfo<ReturnExpressListVO> page(@RequestBody ReturnExpressListQueryDTO queryDto) {
        startPage();
        return getDataTable(returnExpressService.selectReturnOrderList(queryDto));
    }

    /**
     * 无名件管理列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    //@PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/noUserBind/page")
    @ApiOperation(value = "无名件管理列表 - 分页")
    public TableDataInfo<ReturnExpressListVO> pageForNoUserBind(@RequestBody ReturnExpressListQueryDTO queryDto) {
        startPage();
        return getDataTable(returnExpressService.pageForNoUserBind(queryDto));
    }

    /**
     * 无名件批量指派客户
     *
     * @param expressAssignDTO 指派条件
     * @return 返回结果
     */
    //@PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/noUserBind/assignUsers")
    @ApiOperation(value = "无名件批量指派客户")
    public R assignUsersForNoUserBindBatch(@RequestBody ReturnExpressAssignDTO expressAssignDTO) {
        return toOk(returnExpressService.assignUsersForNoUserBindBatch(expressAssignDTO));
    }

    /**
     * 接收VMS仓库到件信息
     * /api/return/arrival #G1-接收仓库退件到货
     *
     * @param returnArrivalReqDTO 接收VMS仓库到件信息
     * @return 操作结果
     */
    //@PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/arrival")
    @ApiOperation(value = "接收仓库退件到货", notes = "/api/return/arrival #G1-接收仓库退件到货")
    public R saveArrivalInfoFormVms(@RequestBody ReturnArrivalReqDTO returnArrivalReqDTO) {
        return toOk(returnExpressService.saveArrivalInfoFormVms(returnArrivalReqDTO));
    }

    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    //@PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/processing")
    @ApiOperation(value = "接收仓库退件处理", notes = "/api/return/processing #G2-接收仓库退件处理")
    public R updateProcessingInfoFromVms(@RequestBody ReturnProcessingReqDTO returnProcessingReqDTO) {
        return toOk(returnExpressService.updateProcessingInfoFromVms(returnProcessingReqDTO));
    }
}
