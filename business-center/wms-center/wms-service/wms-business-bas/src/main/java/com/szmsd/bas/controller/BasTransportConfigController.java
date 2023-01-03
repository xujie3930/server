package com.szmsd.bas.controller;


import com.szmsd.bas.domain.BasTransportConfig;
import com.szmsd.bas.dto.BasTransportConfigDTO;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.service.BasTransportConfigService;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"运输方式表"})
@RestController
@RequestMapping("/bas/basTransportConfig")
public class BasTransportConfigController extends BaseController {
    @Autowired
    private BasTransportConfigService basTransportConfigService;
//    @PreAuthorize("@ss.hasPermi('bas:transportConfig:page')")
    @PostMapping("/page")
    @ApiOperation(value = "查询", notes = "仓库列表 - 分页查询")
    public TableDataInfo<BasTransportConfig> page(@RequestBody BasTransportConfigDTO queryDTO) {
        startPage();
        List<BasTransportConfig> list = basTransportConfigService.selectList(queryDTO);
        return getDataTable(list);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改", notes = "修改")
    public R update(@RequestBody BasTransportConfig basTransportConfig) {
        R r = basTransportConfigService.updateBasTransportConfig(basTransportConfig);
        return r;
    }

}
