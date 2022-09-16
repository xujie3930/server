package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.domain.DelOutboundCompleted;
import com.szmsd.delivery.domain.DelOutboundThirdParty;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundThirdPartyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * WMS推送 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-04-06
 */


@Api(tags = {"WMS推送"})
@RestController
@RequestMapping("/del-outbound-thirdParty")
public class DelOutboundThirdPartyController extends BaseController {

    @Resource
    private IDelOutboundThirdPartyService delOutboundThirdPartyService;

    /**
     * 查询出库单完成记录模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundThirdParty:DelOutboundThirdParty:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询WMS轮询的订单列表", notes = "查询WMS轮询的订单列表")
    public TableDataInfo list(DelOutboundThirdParty delOutboundThirdParty) {
        startPage();
        List<DelOutboundThirdParty> list = delOutboundThirdPartyService.selectDelOutboundThirdPartyList(delOutboundThirdParty);
        return getDataTable(list);
    }


    /**
     * 新增出库单完成记录模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundThirdParty:DelOutboundThirdParty:pushAgain')")
    @Log(title = "出库单完成记录模块", businessType = BusinessType.INSERT)
    @PostMapping("pushAgain")
    @ApiOperation(value = "重推数据", notes = "重推数据")
    public R pushAgain(@RequestBody List<Long> ids) {

        if(ids == null || ids.isEmpty()){
            return R.failed("参数不能为空");
        }
        List<DelOutboundThirdParty> list = delOutboundThirdPartyService.listByIds(ids);
        List<DelOutboundThirdParty> newList = new ArrayList<>();
        for(DelOutboundThirdParty vo: list){
            if(DelOutboundCompletedStateEnum.FAIL.getCode().equals(vo.getState())){
                vo.setNextHandleTime(new Date());
                vo.setHandleSize(0);
                newList.add(vo);
            }
        }
        if(newList.size() > 0){
            delOutboundThirdPartyService.updateBatchById(newList);
            return R.ok();
        } else{
            return R.failed("没有符合条件的数据进行重推");
        }
    }


}
