package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasCuspriceCode;
import com.szmsd.bas.service.IBasCuspriceCodeService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 客户报价子表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-09-21
 */

@Api(tags = {"客户报价子表"})
@RestController
@RequestMapping("/bas-cusprice-code")
public class BasCuspriceCodeController extends BaseController {


    @Resource
    private IBasCuspriceCodeService basCuspriceCodeService;

    /**
     * 查询客户报价子表模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bascuspricecode:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasCuspriceCode basCuspriceCode) {
        startPage();
        List<BasCuspriceCode> list = basCuspriceCodeService.selectBasCuspriceCodeList(basCuspriceCode);
        return getDataTable(list);
    }

    /**
     * 新增客户报价子表模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascuspricecode:add')")
    @Log(title = "客户报价子表模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasCuspriceCode basCuspriceCode) {
        return toOk(basCuspriceCodeService.insertBasCuspriceCode(basCuspriceCode));
    }

    /**
     * 修改客户报价子表模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascuspricecode:edit')")
    @Log(title = "客户报价子表模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasCuspriceCode basCuspriceCode) {
        return toOk(basCuspriceCodeService.updateBasCuspriceCode(basCuspriceCode));
    }

    /**
     * 删除客户报价子表模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascuspricecode:remove')")
    @Log(title = "客户报价子表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basCuspriceCodeService.deleteBasCuspriceCodeByIds(ids));
    }

}
