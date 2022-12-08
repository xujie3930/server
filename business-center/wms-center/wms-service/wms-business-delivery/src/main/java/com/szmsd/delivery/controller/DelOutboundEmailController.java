package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.delivery.service.DelOutboundEmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"异常单电子邮件控制层"})
@RestController
@RequestMapping("/delOutboundEmailController")
public class DelOutboundEmailController  extends BaseController {

    private Logger logger = LoggerFactory.getLogger(DelOutboundDocController.class);

    @Autowired
    private DelOutboundEmailService delOutboundEmailService;
    /**
     * 查询OMS推送WMS错误日志
     */

    @PostMapping("selectOmsWmsLog")
    @ApiOperation(value = "查询oms推wms错误日志数据", notes = "查询oms推wms错误日志数据")
    public R selectOmsWmsLog() {
        return delOutboundEmailService.selectOmsWmsLog();
    }
}
