package com.szmsd.delivery.controller;


import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.domain.BasShipmentRules;
import com.szmsd.delivery.dto.BasShipmentRulesDto;
import com.szmsd.delivery.service.BasShipmenRulesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



@Api(tags = {"产品规则"})
@RestController
@RequestMapping("/basShipmenRulesController")
@Slf4j
public class BasShipmenRulesController extends BaseController {
    @Autowired
    private BasShipmenRulesService basShipmenRulesService;

    @PostMapping("/list")
    @ApiOperation(value = "查询产品规则列表",notes = "查询产品规则列表")
    public TableDataInfo<BasShipmentRules> list(@RequestBody BasShipmentRulesDto basShipmentRulesDto)
    {
        startPage(basShipmentRulesDto);
        List<BasShipmentRules> list = basShipmenRulesService.selectBasShipmentRules(basShipmentRulesDto);
        return getDataTable(list);
    }

    @PostMapping("/addBasShipmentRules")
    @ApiOperation(value = "添加产品规则",notes = "添加产品规则")
    public R addBasShipmentRules(@RequestBody BasShipmentRulesDto basShipmentRulesDto)
    {
        R r=basShipmenRulesService.addBasShipmentRules(basShipmentRulesDto);
        return r;
    }

    @PostMapping("/updeteBasShipmentRules")
    @ApiOperation(value = "修改产品规则",notes = "修改产品规则")
    public R updeteBasShipmentRules(@RequestBody BasShipmentRulesDto basShipmentRulesDto)
    {
        R r=basShipmenRulesService.updeteBasShipmentRules(basShipmentRulesDto);
        return r;
    }

    @PostMapping("/deleteShipmentRules")
    @ApiOperation(value = "删除产品规则",notes = "删除产品规则")
    public R deleteShipmentRules(@RequestBody BasShipmentRulesDto basShipmentRulesDto)
    {
        R r=basShipmenRulesService.deleteShipmentRules(basShipmentRulesDto);
        return r;
    }

}
