package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.dto.DelOutboundChargeQueryDto;
import com.szmsd.delivery.vo.DelOutboundChargeListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@Api(tags = {"扣费日志"})
@RestController
@RequestMapping("/log")
public class ChargeLogController extends BaseController {

    @Resource
    private IChargeLogService chargeLogService;

    @GetMapping("/list")
    @ApiOperation(value = "扣费日志 - 分页查询")
    public TableDataInfo<ChargeLog> list(ChargeLogDto chargeLogDto) {
        startPage();
        List<ChargeLog> chargeLog = chargeLogService.selectPage(chargeLogDto);
        return getDataTable(chargeLog);
    }

    @GetMapping("/operationCharge/page")
    @ApiOperation(value = "扣费日志 - 查询操作费用")
    public TableDataInfo<DelOutboundChargeListVO> getPage(DelOutboundChargeQueryDto queryDto) {
        startPage();
        List<DelOutboundChargeListVO> list = chargeLogService.selectChargeLogList(queryDto);
        return getDataTable(list);
    }

}
