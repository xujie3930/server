package com.szmsd.delivery.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.domain.DelOutboundRetryLabel;
import com.szmsd.delivery.event.DelOutboundRetryLabelEvent;
import com.szmsd.delivery.event.EventUtil;
import com.szmsd.delivery.service.IDelOutboundRetryLabelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"出库单标签重试记录"})
@RestController
@RequestMapping("/del-outbound-retry-label")
public class DelOutboundRetryLabelController extends BaseController {

    @Autowired
    private IDelOutboundRetryLabelService delOutboundRetryLabelService;

    @PreAuthorize("@ss.hasPermi('DelOutboundRetryLabel:DelOutboundRetryLabel:list')")
    @GetMapping("/list")
    @ApiOperation(value = "出库单标签重试记录列表", notes = "出库单标签重试记录列表")
    public TableDataInfo list(DelOutboundRetryLabel delOutboundRetryLabel) {
        startPage();
        QueryWrapper<DelOutboundRetryLabel> queryWrapper = Wrappers.query();
        QueryWrapperUtil.filterDate(queryWrapper, "create_time", delOutboundRetryLabel.getCreateTimes());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "state", delOutboundRetryLabel.getState());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "order_no", delOutboundRetryLabel.getOrderNo());
        queryWrapper.orderByDesc("create_time");
        List<DelOutboundRetryLabel> list = delOutboundRetryLabelService.list(queryWrapper);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('DelOutboundRetryLabel:DelOutboundRetryLabel:handler')")
    @PostMapping("/handler")
    @ApiOperation(value = "出库单标签重试记录手动处理", notes = "出库单标签重试记录手动处理")
    public R<?> handler(@RequestBody DelOutboundRetryLabel delOutboundRetryLabel) {
        Long id = delOutboundRetryLabel.getId();
        Long[] ids = delOutboundRetryLabel.getIds();
        if (null == id && ArrayUtils.isEmpty(ids)) {
            throw new CommonException("500", "id或ids不能为空");
        }
        if (null != id) {
            EventUtil.publishEvent(new DelOutboundRetryLabelEvent(id, true));
        }
        if (ArrayUtils.isNotEmpty(ids)) {
            for (Long aLong : ids) {
                EventUtil.publishEvent(new DelOutboundRetryLabelEvent(aLong, true));
            }
        }
        return R.ok("已提交异步执行");
    }
}
