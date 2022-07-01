package com.szmsd.chargerules.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.szmsd.chargerules.config.DownloadTemplateUtil;
import com.szmsd.chargerules.service.ICustomPricesService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.delivery.dto.CustomPricesDiscountImportDto;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.grade.GradeMainDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
* <p>
    *  前端控制器
    * </p>
*
* @author admin
* @since 2022-06-22
*/


@Api(tags = {"等级维护"})
@RestController
@RequestMapping("/custom-prices")
public class CustomPricesController extends BaseController{

     @Resource
     private ICustomPricesService customPricesService;

    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:result')")
    @PostMapping("/result/{clientCode}")
    @ApiOperation(value = "获取折扣/等级方案数据")
    public R<CustomPricesMainDto> result(@PathVariable("clientCode") String clientCode) {
        return customPricesService.result(clientCode);
    }

    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:updateDiscount')")
    @PostMapping("/updateDiscount")
    @ApiOperation(value = "修改客户折扣主信息")
    public R updateDiscount(@RequestBody UpdateCustomDiscountMainDto dto) {
        return customPricesService.updateDiscount(dto);
    }

    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:updateGrade')")
    @PostMapping("/updateGrade")
    @ApiOperation(value = "修改客户等级主信息")
    public R updateGrade(@RequestBody UpdateCustomGradeMainDto dto) {
        return customPricesService.updateGrade(dto);
    }


    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:updateGradeDetail')")
    @PostMapping("/updateGradeDetail")
    @ApiOperation(value = "修改客户等级明细信息")
    public R updateGradeDetail(@RequestBody CustomGradeMainDto dto) {
        return customPricesService.updateGradeDetail(dto);
    }


    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:updateDiscountDetail')")
    @PostMapping("/updateDiscountDetail")
    @ApiOperation(value = "修改客户折扣明细信息")
    public R updateDiscountDetail(@RequestBody CustomDiscountMainDto dto) {
        return customPricesService.updateDiscountDetail(dto);
    }


    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:gradeDetailResult')")
    @PostMapping("/gradeDetailResult/{id}")
    @ApiOperation(value = "获取等级方案明细信息")
    public R<GradeMainDto> gradeDetailResult(@PathVariable("id") String id) {
        return customPricesService.gradeDetailResult(id);
    }

    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:discountDetailResult')")
    @PostMapping("/discountDetailResult/{id}")
    @ApiOperation(value = "获取折扣方案明细信息")
    public R<DiscountMainDto> discountDetailResult(@PathVariable("id") String id) {
        return customPricesService.gradeDetailResult(id);
    }



    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:downloadDiscountTemplate')")
    @ApiOperation(value = "折扣方案 - 下载模版")
    @PostMapping("/downloadDiscountTemplate")
    public void downloadDiscountTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "客户折扣方案模板");
    }

    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:downloadGradeTemplate')")
    @ApiOperation(value = "等级方案 - 下载模版")
    @PostMapping("/downloadGradeTemplate")
    public void downloadGradeTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "客户等级方案模板");
    }

   /* @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:importGradeTemplate')")
    @PostMapping("/importGradeTemplate")
    @ApiOperation(value = "等级方案 - 导入", position = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R importGradeTemplate(@RequestParam("clientCode") String clientCode, HttpServletRequest request) {*/
   @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:importDiscountTemplate')")
   @PostMapping("/importDiscountTemplate")
   @ApiOperation(value = "折扣方案 - 导入", position = 200)
   @ApiImplicitParams({
           @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
   })
   public R importDiscountTemplate(@RequestParam("clientCode") String clientCode, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        String originalFilename = file.getOriginalFilename();
        AssertUtil.notNull(originalFilename, "导入文件名称不存在");
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf + 1);
        if (!"xls".equals(suffix) && !"xlsx".equals(suffix)) {
            throw new CommonException("999", "只能上传xls,xlsx文件");
        }
        try {
            ExcelReaderSheetBuilder excelReaderSheetBuilder = EasyExcelFactory.read(file.getInputStream(),
                    CustomPricesDiscountImportDto.class, null).sheet(0);
            List<CustomPricesDiscountImportDto> dtoList = excelReaderSheetBuilder.doReadSync();
            if (CollectionUtils.isEmpty(dtoList)) {
                return R.failed("导入数据不能为空");
            }
        } catch (Exception e) {
            return R.failed("文件解析异常");
        }


       CustomDiscountMainDto dto = new CustomDiscountMainDto();

//       customPricesService.updateDiscountDetail();


        return R.ok();
    }
    


}
