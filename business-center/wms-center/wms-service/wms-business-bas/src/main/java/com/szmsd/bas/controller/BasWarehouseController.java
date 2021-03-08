package com.szmsd.bas.controller;

import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"仓库"})
@RestController
@RequestMapping("/bas/warehouse")
public class BasWarehouseController extends BaseController {

}
