package com.szmsd.finance.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.finance.domain.FssConvertUnit;
import com.szmsd.finance.service.FssConvertUnitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"单位"})
@RestController
@RequestMapping("/convert-unit")
@Slf4j
public class FssConvertUnitController extends BaseController {

    @Autowired
    private FssConvertUnitService fssConvertUnitService;

    @ApiOperation(value = "查询所有单位")
    @GetMapping("/find-all")
    public R<List<FssConvertUnit>> findAll(){
        List<FssConvertUnit> fssConvertUnitList = fssConvertUnitService.list(Wrappers.<FssConvertUnit>query().lambda());
        return R.ok(fssConvertUnitList);
    }
}
