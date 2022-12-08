package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.delivery.service.OfflineDeliveryService;
import com.szmsd.delivery.util.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = {"线下出库"})
@RestController
@RequestMapping("/offline-delivery")
public class OfflineDeliveryController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(DelOutboundController.class);

    @Autowired
    private OfflineDeliveryService offlineDeliveryService;


    @GetMapping("/exportTemplate")
    @ApiOperation(value = "线下出库 - 导入模板")
    public void exportTemplate(HttpServletResponse response) {
        String filePath = "/template/offline-delivery-template.xlsx";
        String fileName = "线下出库导入模板";
        ExcelUtil.downloadTemplate(response, filePath, fileName, "xlsx");
    }

    @PostMapping("/import")
    @ApiOperation(value = "线下出库 - 新增 - 导入", position = 900)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R importExcel(HttpServletRequest request){

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;

        MultipartFile file = multipartHttpServletRequest.getFile("file");

        return offlineDeliveryService.importExcel(file);
    }


}
