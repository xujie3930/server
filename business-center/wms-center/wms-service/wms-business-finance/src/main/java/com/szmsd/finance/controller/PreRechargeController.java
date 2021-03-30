package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.domain.PreRecharge;
import com.szmsd.finance.dto.PreRechargeAuditDTO;
import com.szmsd.finance.dto.PreRechargeDTO;
import com.szmsd.finance.service.IPreRechargeService;
import com.szmsd.finance.service.ISysDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liulei
 */
@Api(tags = {"汇款充值"})
@RestController
@RequestMapping("/preRecharge")
public class PreRechargeController extends BaseController {
    @Autowired
    IPreRechargeService preRechargeService;

    @PreAuthorize("@ss.hasPermi('PreRecharge:listPage')")
    @ApiOperation(value = "分页查询汇款充值信息")
    @GetMapping("/listPage")
    public TableDataInfo listPage(PreRechargeDTO dto) {
        startPage();
        List<PreRecharge> list = preRechargeService.listPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('PreRecharge:save')")
    @ApiOperation(value = "汇款充值")
    @PostMapping("/save")
    public R save(@RequestBody PreRechargeDTO dto){
        return preRechargeService.save(dto);
    }

    @PreAuthorize("@ss.hasPermi('PreRecharge:save')")
    @ApiOperation(value = "审核汇款")
    @PostMapping("/audit")
    public R audit(@RequestBody PreRechargeAuditDTO dto){
        return preRechargeService.audit(dto);
    }

    @Autowired
    ISysDictDataService sysDictDataService;

    @ApiOperation(value="xxx")
    @PostMapping("/test")
    public String test(String s){
        return sysDictDataService.getCurrencyNameByCode(s);
    }
}
