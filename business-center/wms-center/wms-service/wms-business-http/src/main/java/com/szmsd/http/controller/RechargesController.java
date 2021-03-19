package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.service.IAccountService;
import com.szmsd.http.vo.RechargesResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liulei
 */
@Api(tags = {"基础信息"})
@RestController
@RequestMapping("/api/recharges/http")
public class RechargesController {

    @Autowired
    IAccountService accountService;

    @PostMapping("/onlineRecharge")
    @ApiOperation(value = "线上充值")
    @ApiImplicitParam(name = "dto", value = "RechargesResponseVo", dataType = "RechargesRequestDTO")
    public R<RechargesResponseVo> onlineRecharge(@RequestBody RechargesRequestDTO dto) {
        return R.ok(accountService.onlineRecharge(dto));
    }

//    @PostMapping("/rechargeResult")
//    @ApiOperation(value = "充值结果查询")
//    @ApiImplicitParam(name = "dto", value = "RechargesResponseVo", dataType = "RechargesRequestDTO")
//    public R<RechargesResponseVo> rechargeResult(@RequestBody RechargesRequestDTO dto) {
//        return R.ok(accountService.rechargeResult(dto));
//    }
}
