package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.chargerules.vo.OperationVo;
import com.szmsd.chargerules.vo.OrderTypeLabelVo;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"业务计费规则"})
@RestController
@RequestMapping("/operation")
public class OperationController extends BaseController {

    @Resource
    private IOperationService operationService;

    @PreAuthorize("@ss.hasPermi('Operation:Operation:add')")
    @ApiOperation(value = "业务计费逻辑 - 保存")
    @PostMapping("/save")
    public R save(@RequestBody OperationDTO dto){
        return toOk(operationService.save(dto));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:edit')")
    @ApiOperation(value = "业务计费逻辑 - 修改")
    @PutMapping("/update")
    public R update(@RequestBody Operation dto) {
        return toOk(operationService.update(dto));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:list')")
    @ApiOperation(value = "业务计费逻辑 - 分页查询")
    @GetMapping("/list")
    public TableDataInfo<OperationVo> listPage(OperationDTO dto){
        startPage();
        List<OperationVo> list = operationService.listPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:details')")
    @ApiOperation(value = "业务计费逻辑 - 详情")
    @GetMapping("/details/{id}")
    public R<OperationVo> details(@PathVariable int id) {
        return R.ok(operationService.details(id));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:getOrderTypeList')")
    @ApiOperation(value = "业务计费逻辑 - 查询订单类型")
    @GetMapping("/getOrderTypeList")
    public List<OrderTypeLabelVo> getOrderTypeList() {
        DelOutboundOrderTypeEnum[] values = DelOutboundOrderTypeEnum.values();
        return Arrays.stream(values).map(value -> new OrderTypeLabelVo(value.getCode(),value.getName())).collect(Collectors.toList());
    }

}
