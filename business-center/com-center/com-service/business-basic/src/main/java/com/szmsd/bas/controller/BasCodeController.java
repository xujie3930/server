package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.service.BasCodeService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-28 17:48
 * @Description
 */
@Api(tags = {"自定义单号生成模块"})
@RestController
@RequestMapping("/basCode")
public class BasCodeController extends BaseController {

    @Resource
    private BasCodeService basCodeService;

    //@PreAuthorize("@ss.hasPermi('bas:sysCode:create')")
    @PostMapping("/create")
    @ApiOperation(httpMethod = "POST", value = "生成单号")
    public R createCode(@RequestBody BasCodeDto basCodeDto){
        try{
            return this.basCodeService.createCode(basCodeDto);
        }catch (Exception e){
            log.error("",e);
            return R.failed(e.getMessage());
        }
    }
}
