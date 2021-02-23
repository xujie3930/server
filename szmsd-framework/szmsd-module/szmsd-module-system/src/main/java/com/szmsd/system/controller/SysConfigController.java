package com.szmsd.system.controller;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.domain.SysConfig;
import com.szmsd.system.service.ISysConfigService;

/**
 * 参数配置 信息操作处理
 * 
 * @author lzw
 */
@RestController
@RequestMapping("/config")
@Api(tags = "参数配置")
public class SysConfigController extends BaseController
{
    @Resource
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @ApiOperation(httpMethod = "GET", value = "获取参数配置列表")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config)
    {

        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }
    
    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @ApiOperation(httpMethod = "POST", value = "导出数据")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysConfig config) throws IOException
    {
        List<SysConfig> list = configService.selectConfigList(config);
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        util.exportExcel(response, list, "参数数据");
    }

    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@ss.hasRole('admin')")
    @GetMapping(value = "/{configId}")
    @ApiOperation(httpMethod = "GET", value = "根据参数编号获取详细信息")
    public R getInfo(@PathVariable Long configId)
    {
        return R.ok(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    @ApiOperation(httpMethod = "GET", value = "根据参数键名查询参数值")
    public R getConfigKey(@PathVariable @ApiParam(value ="参数键值" ) String configKey)
    {
        return R.ok(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(httpMethod = "POST", value = "新增参数配置")
    public R add(@Validated @RequestBody SysConfig config)
    {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config)))
        {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM002 , config.getConfigName());
        }
        config.setCreateByName(SecurityUtils.getUsername());
        return toOk(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(httpMethod = "PUT", value = "修改参数配置")
    public R edit(@Validated @RequestBody SysConfig config)
    {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config)))
        {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM002 , config.getConfigName());
        }
        config.setUpdateByName(SecurityUtils.getUsername());
        return toOk(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    @ApiOperation(httpMethod = "DELETE", value = "删除参数配置")
    public R remove(@PathVariable @ApiParam(value ="参数id数组" ) Long[] configIds)
    {
        return toOk(configService.deleteConfigByIds(configIds));
    }
}
