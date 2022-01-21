package com.szmsd.delivery.controller;

import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.vo.DelOutboundListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 出库管理 - 重派
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"出库管理 - 重派"})
@ApiSort(100)
@RestController
@RequestMapping("/api/outbound-reassign")
public class DelOutboundReassignController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(DelOutboundReassignController.class);

    @Autowired
    private IDelOutboundService delOutboundService;

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundReassign:list')")
    @PostMapping("/page")
    @ApiOperation(value = "出库管理 - 分页", position = 100)
    @AutoValue
    public TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryDto queryDto) {
        startPage(queryDto);
        queryDto.setReassignType(DelOutboundConstant.REASSIGN_TYPE_Y);
        return getDataTable(this.delOutboundService.selectDelOutboundList(queryDto));
    }

}
