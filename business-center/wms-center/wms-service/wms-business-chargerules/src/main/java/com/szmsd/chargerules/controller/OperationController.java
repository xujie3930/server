package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.api.feign.SpecialOperationFeignService;
import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.chargerules.vo.OrderTypeLabelVo;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.vo.DelOutboundVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
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
    public R save(@RequestBody OperationDTO dto) {
        int save = 0;
        try {
            save = operationService.save(dto);
        } catch (DuplicateKeyException e) {
            log.error(e.getMessage(), e);
            return R.failed("操作类型+仓库+是否多SKU不能重复");
        }
        return toOk(save);
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
    public TableDataInfo<Operation> listPage(OperationDTO dto) throws InvocationTargetException, IllegalAccessException {
        startPage();
        List<Operation> list = operationService.listPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:details')")
    @ApiOperation(value = "业务计费逻辑 - 详情")
    @GetMapping("/details/{id}")
    public R<Operation> details(@PathVariable int id) {
        return R.ok(operationService.details(id));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:getOrderTypeList')")
    @ApiOperation(value = "业务计费逻辑 - 查询订单类型")
    @GetMapping("/getOrderTypeList")
    public R<List<OrderTypeLabelVo>> getOrderTypeList() {
        return R.ok(Arrays.stream(DelOutboundOrderTypeEnum.values()).map(value ->
                new OrderTypeLabelVo(value.getCode(), value.getName())).collect(Collectors.toList()));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundCharge')")
    @ApiOperation(value = "业务计费 - 出库扣款")
    @PostMapping("/delOutboundCharge")
    public R delOutboundCharge(@RequestBody DelOutboundVO delOutboundVO) {
        return operationService.delOutboundCharge(delOutboundVO);
    }

}
