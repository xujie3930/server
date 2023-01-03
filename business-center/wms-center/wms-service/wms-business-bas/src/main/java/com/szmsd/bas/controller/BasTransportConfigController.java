package com.szmsd.bas.controller;


import com.szmsd.bas.service.BasTransportConfigService;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"运输方式表"})
@RestController
@RequestMapping("/bas/basTransportConfig")
public class BasTransportConfigController extends BaseController {
    @Autowired
    private BasTransportConfigService basTransportConfigService;
}
