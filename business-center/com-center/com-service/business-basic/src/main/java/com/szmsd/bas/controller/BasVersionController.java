package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasVersion;
import com.szmsd.bas.service.IBasVersionService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-10-29
 */

@Api(tags = {"版本号表"})
@RestController
@RequestMapping("/bas-version")
public class BasVersionController extends BaseController {


    @Resource
    private IBasVersionService basVersionService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasVersion:BasVersion:list')")
    @GetMapping("/list")
    public R list(BasVersion basVersion) {
        List<BasVersion> list = basVersionService.selectBasVersionList(basVersion);
        return R.ok(list.get(0));
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('BasVersion:BasVersion:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasVersion basVersion) {
        return toOk(basVersionService.insertBasVersion(basVersion));
    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('BasVersion:BasVersion:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasVersion basVersion) {
        return toOk(basVersionService.updateBasVersion(basVersion));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('BasVersion:BasVersion:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basVersionService.deleteBasVersionByIds(ids));
    }

}
