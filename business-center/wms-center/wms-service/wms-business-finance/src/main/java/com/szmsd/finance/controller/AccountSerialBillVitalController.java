package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.service.IAccountSerialBillService;
import com.szmsd.finance.vo.BillBalanceVO;
import com.szmsd.finance.vo.EleBillQueryVO;
import com.szmsd.finance.vo.ElectronicBillVO;
import com.szmsd.finance.vo.GeneratorBillRequestVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"流水账单统计"})
@RestController
@RequestMapping("/bill")
public class AccountSerialBillVitalController extends BaseController {

    @Resource
    private IAccountSerialBillService accountSerialBillService;

    //@AutoValue
    //@PreAuthorize("@ss.hasPermi('AccountSerialBill:listPage')")
    @ApiOperation(value = "电子账单列表")
    @GetMapping("/page")
    public TableDataInfo<ElectronicBillVO> electronicPage(EleBillQueryVO queryVO) {
        startPage();
        return getDataTable(accountSerialBillService.electronicPage(queryVO));
    }

    //@PreAuthorize("@ss.hasPermi('PreRecharge:save')")
    @ApiOperation(value = "账单生成")
    @PostMapping("/generator-bill")
    public R<Integer> generatorBill(@RequestBody GeneratorBillRequestVO billRequestVO){
        return accountSerialBillService.generatorBill(billRequestVO);
    }

    @ApiOperation(value = "资金结余列表")
    @GetMapping("/balance-page")
    public TableDataInfo<BillBalanceVO> balancePage(EleBillQueryVO queryVO) {
        startPage();
        return getDataTable(accountSerialBillService.balancePage(queryVO));
    }

    @ApiOperation(value = "导出")
    @PostMapping ("/export")
    public void export(HttpServletResponse response, @RequestBody GeneratorBillRequestVO requestVO) {


    }

}
