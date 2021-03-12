package com.szmsd.inventory.controller;

import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.inventory.service.IInventoryService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * <p>
 * inventory - 库存表 前端控制器
 * </p>
 *
 * @author liangchao
 * @since 2021-03-12
 */


@Api(tags = {"库存"})
@RestController
@RequestMapping("/inventory")
public class InventoryController extends BaseController {

    @Resource
    private IInventoryService inventoryService;

}
