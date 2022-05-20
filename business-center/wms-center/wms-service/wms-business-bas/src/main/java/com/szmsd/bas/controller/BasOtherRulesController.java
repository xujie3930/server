package com.szmsd.bas.controller;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.bas.service.IBasOtherRulesService;
import com.szmsd.bas.domain.BasOtherRules;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import java.util.List;
import java.io.IOException;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;


/**
* <p>
    * 其他规则匹配 前端控制器
    * </p>
*
* @author Administrator
* @since 2022-05-16
*/


@Api(tags = {"其他规则匹配"})
@RestController
@RequestMapping("/bas-other-rules")
public class BasOtherRulesController extends BaseController{

     @Resource
     private IBasOtherRulesService basOtherRulesService;

    /**
    * 获取其他规则匹配模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasOtherRules:BasOtherRules:query')")
    @GetMapping(value = "getInfo/{sellerCode}")
    @ApiOperation(value = "获取其他规则匹配模块详细信息",notes = "获取其他规则匹配模块详细信息")
    public R<BasOtherRules> getInfo(@PathVariable("sellerCode") String sellerCode)
    {

        if(StringUtils.isEmpty(sellerCode)){
            throw new CommonException("400", "sellerCode 不能空");
        }

        BasOtherRules vo = basOtherRulesService.selectBasOtherRulesById(sellerCode);
        if(vo == null){
            vo = new BasOtherRules();
        }
    return R.ok(vo);
    }

    /**
    * 新增或者修改其他规则匹配模块
    */
    @PreAuthorize("@ss.hasPermi('BasOtherRules:BasOtherRules:edit')")
    @Log(title = "其他规则匹配模块", businessType = BusinessType.UPDATE)
    @PostMapping("addOrUpdate")
    @ApiOperation(value = "新增其他规则匹配模块",notes = "新增其他规则匹配模块")
    public synchronized R addOrUpdate(@RequestBody @Valid BasOtherRules basOtherRules)
    {
        return toOk(basOtherRulesService.addOrUpdate(basOtherRules));
    }

}
