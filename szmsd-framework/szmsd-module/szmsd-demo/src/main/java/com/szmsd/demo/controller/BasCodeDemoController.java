package com.szmsd.demo.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.demo.domain.BasCodeDto;
import com.szmsd.demo.service.BasFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-07-02 16:49
 * @Description
 */
@RestController
@RequestMapping("/demo")
@Api(value = "/", description = "系统单号测试模块")
public class BasCodeDemoController {

    @Resource
    private BasFeignService basFeignService;

    /**
     * @param
     * @return
     */
    @GetMapping("/basCode")
    @ApiOperation(httpMethod = "GET", value = "查询列表数据")
    public R createBasCode() {
        BasCodeDto sysCodeDto=new BasCodeDto();
        sysCodeDto.setAppId("gfs");
        sysCodeDto.setCode("WAYBILL_CODE");
        //前缀
        sysCodeDto.setPrefix("IGFS");
        //生成数量
        sysCodeDto.setCount(5);
        //请求接口，生成运单号
        return this.basFeignService.create(sysCodeDto);
    }
}
