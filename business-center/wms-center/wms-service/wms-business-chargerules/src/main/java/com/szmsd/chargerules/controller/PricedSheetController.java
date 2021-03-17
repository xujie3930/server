package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.service.IPricedSheetService;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"PricedSheet"})
@RestController
@RequestMapping("/sheets")
public class PricedSheetController extends BaseController {

    @Resource
    private IPricedSheetService iPricedSheetService;
}
