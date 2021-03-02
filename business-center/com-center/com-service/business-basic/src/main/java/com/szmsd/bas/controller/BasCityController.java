package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasCity;
import com.szmsd.bas.driver.UpdateRedis;
import com.szmsd.bas.service.IBasCityService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.CodeToNameEnum;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 城市表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-08-03
 */

@Api(tags = {"城市表"})
@RestController
@RequestMapping("/bas-city")
public class BasCityController extends BaseController {


    @Resource
    private IBasCityService basCityService;

    /**
     * 查询城市表模块列表
     */
    @ApiOperation(value = "查询城市", notes = "查询城市('bas:bascity:list')")
    @PreAuthorize("@ss.hasPermi('bas:bascity:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasCity basCity) {
        List<BasCity> list = basCityService.selectBasCityList(basCity);
        return getDataTable(list);
    }

    /**
     * 查询城市表模块列表
     */
    @ApiOperation(value = "查询城市分页", notes = "查询城市分页('bas:bascity:lists')")
    @PreAuthorize("@ss.hasPermi('bas:bascity:lists')")
    @GetMapping("/lists")
    public TableDataInfo lists(BasCity basCity) {
        startPage();
        List<BasCity> list = basCityService.selectBasCityList(basCity);
        return getDataTable(list);
    }

    /**
     * 新增城市表模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascity:list')")
    @Log(title = "城市表模块", businessType = BusinessType.INSERT)
    @UpdateRedis(type = CodeToNameEnum.BAS_CITY)
    @PostMapping
    public R add(@RequestBody BasCity basCity)
    {
        return toOk(basCityService.insertBasCity(basCity));
    }

    /**
     * 修改城市表模块
     */
    @PreAuthorize("@ss.hasPermi('BasCity:BasCity:edit')")
    @Log(title = "城市表模块", businessType = BusinessType.UPDATE)
    @UpdateRedis(type = CodeToNameEnum.BAS_CITY)
    @PutMapping
    public R edit(@RequestBody BasCity basCity)
    {
        return toOk(basCityService.updateBasCity(basCity));
    }

    /**
     * 删除城市表模块
     */
    @PreAuthorize("@ss.hasPermi('BasCity:BasCity:remove')")
    @Log(title = "城市表模块", businessType = BusinessType.DELETE)
    @UpdateRedis(type = CodeToNameEnum.BAS_CITY)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids)
    {
        return toOk(basCityService.deleteBasCityByIds(ids));
    }

}
