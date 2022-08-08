package com.szmsd.finance.controller;

import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.domain.AccountSerialBillEn;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.service.IAccountSerialBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = {"流水账单"})
@RestController
@RequestMapping("/serialBill")
public class AccountSerialBillController extends BaseController {

    @Resource
    private IAccountSerialBillService accountSerialBillService;
    @Autowired
    private BasSubClientService basSubClientService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;

    @AutoValue
    @PreAuthorize("@ss.hasPermi('AccountSerialBill:listPage')")
    @ApiOperation(value = "流水账单 - 列表")
    @GetMapping("/listPage")
    public TableDataInfo<AccountSerialBill> listPage(AccountSerialBillDTO dto) {
        startPage();
        return getDataTable(accountSerialBillService.listPage(dto));
    }

    @AutoValue
    @PreAuthorize("@ss.hasPermi('AccountSerialBill:list')")
    @ApiOperation(value = "第三方 - 流水账单 - 列表")
    @PostMapping("/list")
    public R<TableDataInfo<AccountSerialBill>> list(@RequestBody AccountSerialBillDTO dto) {
        QueryDto page = new QueryDto();
        page.setPageNum(dto.getPageNum());
        page.setPageSize(dto.getPageSize());
        startPage(page);
        return R.ok(getDataTable(accountSerialBillService.listPage(dto)));
    }

    @PreAuthorize("@ss.hasPermi('AccountSerialBill:export')")
    @ApiOperation(value = "流水账单 - 列表导出")
    @PostMapping ("/export")
    public void export(HttpServletResponse response, @RequestBody AccountSerialBillDTO dto) {
        List<AccountSerialBill> list = accountSerialBillService.listPage(dto);




        String len = getLen();
        if("en".equals(len)) {

            // 查询出库类型数据
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("040,045");


            Map<String, String> map040 = null;
            Map<String, String> map045 = null;


            if(listMap.get("040") != null){
                map040 = listMap.get("040").stream()
                        .collect(Collectors.toMap(BasSubWrapperVO::getSubName, BasSubWrapperVO:: getSubNameEn, (v1, v2) -> v1));
            }

            if(listMap.get("045") != null){
                map045 = listMap.get("045").stream()
                        .collect(Collectors.toMap(BasSubWrapperVO::getSubName, BasSubWrapperVO:: getSubNameEn, (v1, v2) -> v1));
            }



            List<String> warehouseCodes = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                AccountSerialBill vo = list.get(i);
                if(!warehouseCodes.contains(vo.getWarehouseCode())){
                    warehouseCodes.add(vo.getWarehouseCode());
                }

                if(map040 != null){
                    if(map040.containsKey(vo.getBusinessCategory())){
                        vo.setBusinessCategory(map040.get(vo.getBusinessCategory()));
                    }
                }

                if(map045 != null){
                    if(map045.containsKey(vo.getChargeType())){
                        vo.setChargeType(map045.get(vo.getChargeType()));
                    }

                    if(map045.containsKey(vo.getChargeCategory())){
                        vo.setChargeCategory(map045.get(vo.getChargeCategory()));
                    }
                }
            }
            if(warehouseCodes.size() > 0){
                List<BasWarehouse> warehouseList = basWarehouseClientService.queryByWarehouseCodes(warehouseCodes);
                Map<String, BasWarehouse> collectMap = warehouseList.stream()
                        .collect(Collectors.toMap(BasWarehouse::getWarehouseCode, Function.identity(), (v1, v2) -> v1));
                for (int i = 0; i < list.size(); i++) {
                    AccountSerialBill vo = list.get(i);
                    if(collectMap.containsKey(vo.getWarehouseCode())){
                        vo.setWarehouseCode(collectMap.get(vo.getWarehouseCode()).getWarehouseNameEn());
                    }
                }

            }


            List<AccountSerialBillEn> enList = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                AccountSerialBill vo = list.get(i);
                AccountSerialBillEn enDto = new AccountSerialBillEn();
                BeanUtils.copyProperties(vo, enDto);
                enList.add(enDto);
                list.set(i, null);
            }
            ExcelUtil<AccountSerialBillEn> util = new ExcelUtil<AccountSerialBillEn>(AccountSerialBillEn.class);
            util.exportExcel(response, enList, "bill" + DateUtils.dateTimeNow());
        }else{

            ExcelUtil<AccountSerialBill> util = new ExcelUtil<>(AccountSerialBill.class);
            util.exportExcel(response,list,"业务明细");

        }

    }

}
