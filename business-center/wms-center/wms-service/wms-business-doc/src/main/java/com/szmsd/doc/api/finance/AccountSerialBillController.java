package com.szmsd.doc.api.finance;

import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.api.finance.request.AccountSerialBillRequest;
import com.szmsd.doc.utils.AuthenticationUtil;
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

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/list")
    @ApiOperation(value = "流水账单 - 列表",notes = "展示用户费用的清单流水，包括充值记录、出入库费用实扣记录等。")
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "AccountSerialBillRequest")
    public TableDataInfo<AccountSerialBill> listPage(@Validated @RequestBody AccountSerialBillRequest request) {
        request.setCusCode(AuthenticationUtil.getSellerCode());
        AccountSerialBillDTO map = BeanMapperUtil.map(request, AccountSerialBillDTO.class);
        return this.accountSerialBillFeignService.listPage(map).getData();
    }

}
