package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasWeightSectionDto;
import com.szmsd.bas.api.domain.BasWeightSectionQueryDto;
import com.szmsd.bas.api.domain.BasWeightSectionVo;
import com.szmsd.bas.service.IBasWeightSectionService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 重量区间设置 前端控制器
 * </p>
 *
 * @author 2
 * @since 2021-01-11
 */


@Api(tags = {"重量区间设置"})
@RestController
@RequestMapping("/bas-weight-section")
public class BasWeightSectionController extends BaseController {

    @Resource
    private IBasWeightSectionService basWeightSectionService;

    /**
     * 查询重量区间设置模块列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询重量区间设置模块列表", notes = "查询重量区间设置模块列表")
    public R<List<BasWeightSectionVo>> list(BasWeightSectionQueryDto queryDto) {
        return R.ok(basWeightSectionService.selectBasWeightSectionList(queryDto));
    }

    /**
     * 新增重量区间设置模块
     */
    @PostMapping("save")
    @ApiOperation(value = "新增重量区间设置模块", notes = "新增重量区间设置模块")
    public R<Integer> add(@RequestBody BasWeightSectionDto dto) {
        return toOk(basWeightSectionService.insertBasWeightSection(dto));
    }
}
