package com.szmsd.returnex.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.returnex.dto.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.ReturnExpressAssignDTO;
import com.szmsd.returnex.dto.ReturnExpressListQueryDTO;
import com.szmsd.returnex.dto.ReturnProcessingReqDTO;
import com.szmsd.returnex.service.IReturnExpressService;
import com.szmsd.returnex.vo.ReturnExpressListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:list')")
    @PostMapping("/page")
    @ApiOperation(value = "退件单列表 - 分页")
    public TableDataInfo<ReturnExpressListVO> page(@Validated @RequestBody ReturnExpressListQueryDTO queryDto) {
        startPage();
        return getDataTable(returnExpressService.selectReturnOrderList(queryDto));
    }

    /**
     * 无名件管理列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:list')")
    @PostMapping("/noUserBind/page")
    @ApiOperation(value = "无名件管理列表 - 分页")
    public TableDataInfo<ReturnExpressListVO> pageForNoUserBind(@Validated @RequestBody ReturnExpressListQueryDTO queryDto) {
        startPage();
        return getDataTable(returnExpressService.pageForNoUserBind(queryDto));
    }

    /**
     * 无名件批量指派客户
     *
     * @param expressAssignDTO 指派条件
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:update')")
    @PostMapping("/noUserBind/assignUsers")
    @Log(title = "退货服务模块", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "无名件批量指派客户")
    public R assignUsersForNoUserBindBatch(@Validated @RequestBody ReturnExpressAssignDTO expressAssignDTO) {
        return toOk(returnExpressService.assignUsersForNoUserBindBatch(expressAssignDTO));
    }

    /**
     * 获取退件单信息详情
     * @param id
     * @return
     */
    @GetMapping("/getInfo/{id}")
    @ApiOperation(value = "获取退件单信息详情")
    public R getInfo(@PathVariable(value = "id") Long id) {
        return R.ok(returnExpressService.getInfo(id));
    }
}
