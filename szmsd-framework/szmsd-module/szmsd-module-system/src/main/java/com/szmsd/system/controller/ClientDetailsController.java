package com.szmsd.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.system.domain.SysOauthClientDetails;
import com.szmsd.system.service.ISysOauthClientDetailsService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/details")
@Api(tags = "回调参数修改")
public class ClientDetailsController {

    @Autowired
    private ISysOauthClientDetailsService sysOauthClientDetailsService;

    @GetMapping
    public R<String> get() {
        LambdaQueryWrapper<SysOauthClientDetails> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SysOauthClientDetails::getClientId, "doc");
        SysOauthClientDetails sysOauthClientDetails = sysOauthClientDetailsService.getOne(lambdaQueryWrapper);
        if (null != sysOauthClientDetails) {
            return R.ok(sysOauthClientDetails.getWebServerRedirectUri());
        }
        return R.ok();
    }

    @PutMapping
    public R<Integer> update(@RequestBody SysOauthClientDetails sysOauthClientDetails) {
        LambdaUpdateWrapper<SysOauthClientDetails> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.set(SysOauthClientDetails::getWebServerRedirectUri, sysOauthClientDetails.getWebServerRedirectUri());
        lambdaUpdateWrapper.eq(SysOauthClientDetails::getClientId, "doc");
        return R.ok(sysOauthClientDetailsService.getBaseMapper().update(null, lambdaUpdateWrapper));
    }
}
