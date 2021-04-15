package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.service.IHtpConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"服务配置"})
@RestController
public class HtpConfigController {

    @Resource
    private IHtpConfigService htpConfigService;

    @PostMapping("/deploy")
    @ApiOperation(value = "部署")
    public R deploy() {
        htpConfigService.loadHtpConfig();
        return R.ok();
    }

}
