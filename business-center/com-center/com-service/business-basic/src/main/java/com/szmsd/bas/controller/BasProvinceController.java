package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasProvince;
import com.szmsd.bas.driver.UpdateRedis;
import com.szmsd.bas.service.IBasProvinceService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.CodeToNameEnum;
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
 * 省份表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-08-03
 */

@Api(tags = {"省份表"})
@RestController
@RequestMapping("/bas-province")
public class BasProvinceController extends BaseController {


    @Resource
    private IBasProvinceService basProvinceService;

    /**
     * 查询省份表模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basprovince:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasProvince basProvince) {
        List<BasProvince> list = basProvinceService.selectBasProvinceList(basProvince);
        return getDataTable(list);
    }


    /**
     * 新增省份表模块
     */
    @PreAuthorize("@ss.hasPermi('BasProvince:BasProvince:add')")
    @Log(title = "省份表模块", businessType = BusinessType.INSERT)
    @UpdateRedis(type = CodeToNameEnum.BAS_PROVINCE)
    @PostMapping
    public R add(@RequestBody BasProvince basProvince)
    {
        return toOk(basProvinceService.insertBasProvince(basProvince));
    }

    /**
     * 修改省份表模块
     */
    @PreAuthorize("@ss.hasPermi('BasProvince:BasProvince:edit')")
    @Log(title = "省份表模块", businessType = BusinessType.UPDATE)
    @UpdateRedis(type = CodeToNameEnum.BAS_PROVINCE)
    @PutMapping
    public R edit(@RequestBody BasProvince basProvince)
    {
        return toOk(basProvinceService.updateBasProvince(basProvince));
    }

    /**
     * 删除省份表模块
     */
    @PreAuthorize("@ss.hasPermi('BasProvince:BasProvince:remove')")
    @Log(title = "省份表模块", businessType = BusinessType.DELETE)
    @UpdateRedis(type = CodeToNameEnum.BAS_PROVINCE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids)
    {
        return toOk(basProvinceService.deleteBasProvinceByIds(ids));
    }

}
