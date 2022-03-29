package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.dto.*;
import com.szmsd.http.api.service.IHtpPickupPackageService;
import com.szmsd.http.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"PricedProduct"})
@RestController
@RequestMapping("/api/pickup/http")
public class PickupPackageController extends BaseController {

    @Resource
    private IHtpPickupPackageService iPickupPackageService;

    @GetMapping("/services")
    @ApiOperation(value = "获取可用的提货服务名称")
    public R<List<PickupPackageService>> services() {
        List<PickupPackageService> directServiceFeeData = iPickupPackageService.services();
        return R.ok(directServiceFeeData);
    }

    @PostMapping("/create")
    @ApiOperation(value = "创建提货服务")
    public R<ResponseVO> create(@RequestBody CreatePickupPackageCommand createPickupPackageCommand) {
        ResponseVO create = iPickupPackageService.create(createPickupPackageCommand);
        if (StringUtils.isEmpty(create.getErrors())) {
            create.setSuccess(true);
        } else {
            create.setSuccess(false);
        }
        return R.ok(create);
    }

}
