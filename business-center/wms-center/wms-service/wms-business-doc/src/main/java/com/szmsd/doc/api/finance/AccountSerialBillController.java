package com.szmsd.doc.api.finance;

import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.api.finance.request.AccountSerialBillRequest;
import com.szmsd.finance.api.feign.AccountSerialBillFeignService;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = {"业务账单"})
@RestController
@RequestMapping("/account/serial/bill")
public class AccountSerialBillController {

    @Resource
    private AccountSerialBillFeignService accountSerialBillFeignService;

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/list")
    @ApiOperation(value = "流水账单 - 列表")
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "AccountSerialBillRequest")
    public TableDataInfo<AccountSerialBill> listPage(@RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                                     @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                                     @Validated @RequestBody AccountSerialBillRequest request) {
        AccountSerialBillDTO map = BeanMapperUtil.map(request, AccountSerialBillDTO.class);
        map.setPageNum(pageNum);
        map.setPageSize(pageSize);
        return this.accountSerialBillFeignService.listPage(map).getData();
    }

}
