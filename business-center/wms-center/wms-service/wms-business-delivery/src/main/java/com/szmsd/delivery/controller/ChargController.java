package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.delivery.service.ChargeService;
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

@Api(tags = {"二次计费"})
@RestController
@RequestMapping("/charg")
public class ChargController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(ChargController.class);

    @Autowired
    private ChargeService chargeService;


    @GetMapping("/exportTemplate")
    @ApiOperation(value = "二次计费 - 导入模板")
    public void exportTemplate(HttpServletResponse response) {
        String filePath = "/template/charg-template.xlsx";
        String fileName = "二次计费导入模板";
        ExcelUtil.downloadTemplate(response, filePath, fileName, "xlsx");
    }


    @PostMapping("/import")
    @ApiOperation(value = "二次计费 - 导入", position = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R importDetail(HttpServletRequest request){

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");

        return chargeService.importExcel(file);
    }


}
