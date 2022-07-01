package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.discount.DiscountMainDto;
import com.szmsd.http.dto.grade.GradeMainDto;
import com.szmsd.http.service.IHttpCustomPricesService;
import com.szmsd.http.service.IHttpDiscountService;
import com.szmsd.http.service.IHttpGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = {"客户等级/折扣报价维护"})
@RestController
@RequestMapping("/api/customPrices/http")
public class HttpCustomPricesController extends BaseController {

    @Resource
    private IHttpCustomPricesService httpCustomPricesService;
    @Resource
    private IHttpGradeService httpGradeService;
    @Resource
    private IHttpDiscountService httpDiscountService;


    @PostMapping("/result/{clientCode}")
    @ApiOperation(value = "获取折扣/等级方案数据")
    public R<CustomPricesMainDto> result(@PathVariable("clientCode") String clientCode) {
        return httpCustomPricesService.result(clientCode);
    }

    @PostMapping("/updateDiscount")
    @ApiOperation(value = "修改客户折扣主信息")
    public R updateDiscount(@RequestBody UpdateCustomDiscountMainDto dto) {
        return httpCustomPricesService.updateDiscount(dto);
    }

    @PostMapping("/updateGrade")
    @ApiOperation(value = "修改客户等级主信息")
    public R updateGrade(@RequestBody UpdateCustomGradeMainDto dto) {
        return httpCustomPricesService.updateGrade(dto);
    }


    @PostMapping("/updateGradeDetail")
    @ApiOperation(value = "修改客户等级明细信息")
    public R updateGradeDetail(@RequestBody CustomGradeMainDto dto) {
        return httpCustomPricesService.updateGradeDetail(dto);
    }


    @PostMapping("/updateDiscountDetail")
    @ApiOperation(value = "修改客户折扣明细信息")
    public R updateDiscountDetail(@RequestBody CustomDiscountMainDto dto) {
        return httpCustomPricesService.updateDiscountDetail(dto);
    }


    @PostMapping("/gradeDetailResult/{id}")
    @ApiOperation(value = "获取等级方案明细信息")
    public R<GradeMainDto> gradeDetailResult(@PathVariable("id") String id) {
        return httpGradeService.detailResult(id);
    }

    @PostMapping("/discountDetailResult/{id}")
    @ApiOperation(value = "获取折扣方案明细信息")
    public R<DiscountMainDto> discountDetailResult(@PathVariable("id") String id) {
        return httpDiscountService.detailResult(id);
    }
}
