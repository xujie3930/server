package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.service.ChargeRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"费用类型"})
@RestController
@RequestMapping("/charge-relation")
@Slf4j
public class ChargeRelationController {

    @Autowired
    private ChargeRelationService chargeRelationService;

    @ApiOperation(value = "获取性质")
    @GetMapping("/nature")
    public R selectNature(){
        return chargeRelationService.selectNature();
    }

    @ApiOperation(value = "产品类型")
    @GetMapping("/category")
    public R selectCategory(@RequestParam("nature") String nature){
        return chargeRelationService.selectCategory(nature);
    }

    @ApiOperation(value = "费用类型")
    @GetMapping("/charge")
    public R selectCharge(@RequestParam("nature") String nature,@RequestParam("category") String category){
        return chargeRelationService.selectCharge(nature,category);
    }

}
