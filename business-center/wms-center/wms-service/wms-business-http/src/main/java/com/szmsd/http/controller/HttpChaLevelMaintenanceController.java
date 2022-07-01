package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenancePageRequest;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.service.IHttpChaLevelMaintenanceService;
import com.szmsd.http.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"等级维护"})
@RestController
@RequestMapping("/api/chaLevel/http")
public class HttpChaLevelMaintenanceController extends BaseController {

    @Resource
    private IHttpChaLevelMaintenanceService chaLevelMaintenanceService;

    @PostMapping("/page")
    @ApiOperation(value = "分页查询等级维护")
    public R<PageVO> page(@RequestBody ChaLevelMaintenancePageRequest pageDTO) {
        return chaLevelMaintenanceService.page(pageDTO);
    }


    @PostMapping("/create")
    @ApiOperation(value = "创建等级维护")
    public R create(@RequestBody ChaLevelMaintenanceDto chaLevelMaintenanceCreate) {
        return chaLevelMaintenanceService.create(chaLevelMaintenanceCreate);
    }
    @PostMapping("/update")
    @ApiOperation(value = "修改等级维护")
    public R update(@RequestBody ChaLevelMaintenanceDto chaLevelMaintenanceUpdate) {
        return chaLevelMaintenanceService.update(chaLevelMaintenanceUpdate);
    }
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除等级维护")
    public R delete(@PathVariable("id") String id) {
        return chaLevelMaintenanceService.delete(id);
    }

    @PostMapping("/get/{id}")
    @ApiOperation(value = "获取等级维护")
    public R<ChaLevelMaintenanceDto> get(@PathVariable("id") String id) {
        return chaLevelMaintenanceService.get(id);
    }

}
