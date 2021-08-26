package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Api(tags = {"扣费日志"})
@RestController
@RequestMapping("/log")
public class ChargeLogController extends BaseController {

    @Resource
    private IChargeLogService chargeLogService;

    @PostMapping("/add")
    @ApiOperation(value = "新增扣费")
    public R add(@RequestBody ChargeLog chargeLog) {
        chargeLogService.save(chargeLog);
        return R.ok();
    }

    @GetMapping("/list")
    @ApiOperation(value = "扣费日志 - 分页查询")
    public TableDataInfo<ChargeLog> list(ChargeLogDto chargeLogDto) {
        startPage();
        List<ChargeLog> chargeLog = chargeLogService.selectPage(chargeLogDto);
        return getDataTable(chargeLog);
    }

    @PostMapping("/operationCharge/page")
    @ApiOperation(value = "扣费日志 - 查询操作费用")
    public R<TableDataInfo<QueryChargeVO>> getPage(@RequestBody QueryChargeDto queryDto) {
        QueryDto page = new QueryDto();
        page.setPageNum(queryDto.getPageNum());
        page.setPageSize(queryDto.getPageSize());
        startPage(page);
        List<QueryChargeVO> list = chargeLogService.selectChargeLogList(queryDto);
        return R.ok(getDataTable(list));
    }

}
