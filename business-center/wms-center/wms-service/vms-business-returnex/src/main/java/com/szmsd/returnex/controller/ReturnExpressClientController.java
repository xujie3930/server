package com.szmsd.returnex.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.returnex.dto.ReturnExpressAddDTO;
import com.szmsd.returnex.dto.ReturnExpressListQueryDTO;
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
@Api(tags = {"退货服务-客户端"})
@RestController
@RequestMapping("/client/return/express")
public class ReturnExpressClientController extends BaseController {

    @Resource
    private IReturnExpressService returnExpressService;

    /**
     * 新建退件单
     * 用户新增退件单，本地化数据，并推送给WMS
     *
     * @param returnExpressAddDTO 新增
     * @return 返回结果
     */
    //@PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:add')")
    @PostMapping("/add")
    //@Log(title = "退货服务模块", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增退件单-创建退报单")
    public R add(@RequestBody ReturnExpressAddDTO returnExpressAddDTO) {
        return toOk(returnExpressService.insertReturnExpressDetail(returnExpressAddDTO));
    }

    /**
     * 退件单列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @PreAuthorize("@ss.hasPermi('*:*:*')")
    @PostMapping("/page")
    @ApiOperation(value = "退件单列表 - 分页")
    public TableDataInfo<ReturnExpressListVO> page(@RequestBody ReturnExpressListQueryDTO queryDto) {
        startPage();
        return getDataTable(returnExpressService.selectReturnOrderList(queryDto));
    }

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    //@PreAuthorize("@ss.hasPermi('ReturnExpressDetail:ReturnExpressDetail:list')")
    @PostMapping("/update")
    @ApiOperation(value = "更新退件单信息")
    public R update(@RequestBody ReturnExpressAddDTO expressUpdateDTO) {
        return toOk(returnExpressService.updateExpressInfo(expressUpdateDTO));
    }

}
