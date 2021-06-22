package com.szmsd.inventory.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.domain.InventoryWarning;
import com.szmsd.inventory.domain.dto.InventoryWarningQueryDTO;
import com.szmsd.inventory.domain.dto.InventoryWarningSendEmailDTO;
import com.szmsd.inventory.service.IInventoryWarningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"库存对账"})
@RestController
@RequestMapping("/inventory/warning")
public class InventoryWarningController extends BaseController {

    @Resource
    private IInventoryWarningService iInventoryWarningService;

    @GetMapping("/page")
    @ApiOperation(value = "分页", notes = "库存管理 - 分页查询")
    public TableDataInfo<InventoryWarning> page(InventoryWarningQueryDTO queryDTO) {
        startPage();
        List<InventoryWarning> list = iInventoryWarningService.selectList(queryDTO);
        return getDataTable(list);
    }

    @PostMapping("/sendEmail")
    @ApiOperation(value = "发送邮件", notes = "发送邮件")
    public R sendEmail(@RequestBody InventoryWarningSendEmailDTO sendEmailDTO) {
        iInventoryWarningService.sendEmail(sendEmailDTO);
        return R.ok();
    }

    @GetMapping("/queryBatch")
    @ApiOperation(value = "获取批次号", notes = "获取批次号")
    public R<List<String>> queryBatch() {
        return R.ok(iInventoryWarningService.selectBatch());
    }

}
