package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasCountry;
import com.szmsd.bas.service.IBasCountryService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 国家表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-08-10
 */

@Api(tags = {"国家表"})
@RestController
@RequestMapping("/bas-country")
public class BasCountryController extends BaseController {


    @Resource
    private IBasCountryService basCountryService;

    /**
     * 查询国家表模块列表
     */
    @ApiOperation(value = "查询国家", notes = "查询国家('bas:bascountry:list')")
    @PreAuthorize("@ss.hasPermi('bas:bascountry:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasCountry basCountry) {
        List<BasCountry> list = basCountryService.selectBasCountryList(basCountry);
        return getDataTable(list);
    }

    @ApiOperation(value = "根据国家编码查询国家")
    @PreAuthorize("@ss.hasPermi('bas:bascountry:queryByCountryCode')")
    @GetMapping("/queryByCountryCode")
    public R<BasCountry> queryByCountryCode(String countryCode) {
        return R.ok(this.basCountryService.queryByCountryCode(countryCode));
    }
}
