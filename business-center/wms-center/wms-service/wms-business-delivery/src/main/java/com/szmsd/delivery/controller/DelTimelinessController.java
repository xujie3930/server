package com.szmsd.delivery.controller;

import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.delivery.domain.DelTimeliness;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.service.IDelTimelinessService;
import com.szmsd.delivery.vo.DelOutboundListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.github.pagehelper.page.PageMethod.startPage;

/**
 * 时效性控制层
 *
 * @author jun
 * @since 2022-08-16
 */
@Api(tags = {"出库管理"})
@RestController
@RequestMapping("/delTimelinessController")
public class DelTimelinessController extends BaseController {
    @Autowired
    private IDelTimelinessService delTimelinessService;

    @PostMapping("/page")
    @ApiOperation(value = "出库管理 - 分页", position = 100)
    @AutoValue
    public TableDataInfo<DelTimeliness> page(@RequestBody DelTimeliness delTimeliness) {
        startPage(delTimeliness);
        return getDataTable(this.delTimelinessService.selectDelTimeliness(delTimeliness));
    }

}
