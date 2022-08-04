package com.szmsd.chargerules.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.chargerules.config.DownloadTemplateUtil;
import com.szmsd.chargerules.service.ICustomPricesService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.delivery.dto.CustomPricesDiscountImportDto;
import com.szmsd.delivery.dto.DiscountCustomImportDto;
import com.szmsd.delivery.dto.DiscountDetailImportDto;
import com.szmsd.delivery.dto.GradeDetailImportDto;
import com.szmsd.http.dto.OperationRecordDto;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.discount.*;
import com.szmsd.http.dto.grade.GradeDetailDto;
import com.szmsd.http.dto.grade.GradeMainDto;
import com.szmsd.http.dto.grade.UpdateGradeDetailDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
* <p>
    *  前端控制器
    * </p>
*
* @author admin
* @since 2022-06-22
*/


@Api(tags = {"客户等级及折扣"})
@RestController
@RequestMapping("/custom-prices")
public class CustomPricesController extends BaseController{

     @Resource
     private ICustomPricesService customPricesService;

    @Autowired
    private BasSubClientService basSubClientService;

    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:result')")
    @PostMapping("/result/{clientCode}")
    @ApiOperation(value = "获取折扣/等级方案数据")
    public R<CustomPricesPageDto> result(@PathVariable("clientCode") String clientCode) {
        return customPricesService.result(clientCode);
    }

    @PostMapping("/operationRecord")
    @ApiOperation(value = "获取操作日记")
    @PreAuthorize("@ss.hasPermi('Discount:Discount:detailResoperationRecordult')")
    public R<OperationRecordDto> operationRecord(@RequestBody String id) {
        return customPricesService.operationRecord(id);
    }

    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:updateDiscount')")
    @PostMapping("/updateDiscount")
    @ApiOperation(value = "修改客户折扣主信息")
    public R updateDiscount(@RequestBody UpdateCustomMainDto dto) {
        return customPricesService.updateDiscount(dto);
    }

    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:updateGrade')")
    @PostMapping("/updateGrade")
    @ApiOperation(value = "修改客户等级主信息")
    public R updateGrade(@RequestBody UpdateCustomMainDto dto) {
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
        return customPricesService.discountDetailResult(id);
    }


    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:downloadDiscountTemplate')")
    @ApiOperation(value = "折扣方案 - 下载模版")
    @PostMapping("/downloadDiscountTemplate")
    public void downloadDiscountTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "discountDetail");
    }

    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:downloadGradeTemplate')")
    @ApiOperation(value = "等级方案 - 下载模版")
    @PostMapping("/downloadGradeTemplate")
    public void downloadGradeTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "product");
    }


    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:importGradeDetailTemplate')")
    @PostMapping("/importGradeDetailTemplate")
    @ApiOperation(value = "等级方案导入", position = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R importGradeDetailTemplate(@RequestParam("clientCode") String clientCode, HttpServletRequest request) {
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
        List<GradeDetailDto> list = null;
        try {
            List<GradeDetailImportDto> dtoList = new ExcelUtil<>(GradeDetailImportDto.class).importExcel(file.getInputStream());
            list = BeanMapperUtil.mapList(dtoList, GradeDetailDto.class);
            for(int i = 0; i < list.size(); i++){
                list.get(i).setBeginTime(DateUtil.formatDateTime(dtoList.get(i).getBeginTimeDate()));
                list.get(i).setEndTime(DateUtil.formatDateTime(dtoList.get(i).getEndTimeDate()));
            }
            if (CollectionUtils.isEmpty(dtoList)) {
                return R.failed("导入数据不能为空");
            }
        } catch (Exception e) {
            return R.failed("文件解析异常");
        }
        CustomGradeMainDto dto = new CustomGradeMainDto();
        dto.setClientCode(clientCode);
        dto.setPricingGradeRules(list);
        return customPricesService.updateGradeDetail(dto);
    }


    @PreAuthorize("@ss.hasPermi('CustomPrices:CustomPrices:importDiscountDetailTemplate')")
    @PostMapping("/importDiscountDetailTemplate")
    @ApiOperation(value = "折扣方案导入", position = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R importDiscountDetailTemplate(@RequestParam("clientCode") String clientCode, HttpServletRequest request) {
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
        Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("046,047");
        List<BasSubWrapperVO> chargeType = listMap.get("046");
        List<BasSubWrapperVO> chargeRuleType = listMap.get("047");

        Map<String, String> chargeTypeMap =
                chargeType.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubName, BasSubWrapperVO::getSubValue));
        Map<String, String> chargeRuleTypeMap =
                chargeRuleType.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubName, BasSubWrapperVO::getSubValue));

        List<DiscountDetailDto> list = null;
        try {
            List<DiscountDetailImportDto> dtoList = new ExcelUtil<>(DiscountDetailImportDto.class).importExcel(file.getInputStream());
            list = BeanMapperUtil.mapList(dtoList, DiscountDetailDto.class);
            for(int i = 0; i < list.size(); i++){
                list.get(i).setBeginTime(DateUtil.formatDateTime(dtoList.get(i).getBeginTimeDate()));
                list.get(i).setEndTime(DateUtil.formatDateTime(dtoList.get(i).getEndTimeDate()));
            }
            for(int i = 0; i < dtoList.size(); i++){

                DiscountDetailDto data = list.get(i);
                DiscountDetailPackageLimitDto dto1 = new DiscountDetailPackageLimitDto();
                DiscountDetailFormulaDto dto2 = new DiscountDetailFormulaDto();
                data.setPackageLimit(dto1);
                data.setFormula(dto2);
                BeanUtils.copyProperties(dtoList.get(i), dto1);
                BeanUtils.copyProperties(dtoList.get(i), dto2);

                if(StringUtils.isNotEmpty(data.getChargeType())){
                    if(!chargeTypeMap.containsKey(data.getChargeType())){
                        return R.failed("费用类型不存在系统:"+data.getChargeType());
                    }else{
                        data.setChargeType(chargeTypeMap.get(data.getChargeType()));
                    }
                }
                if(StringUtils.isNotEmpty(dto2.getChargeRuleType())){
                    if(!chargeRuleTypeMap.containsKey(dto2.getChargeRuleType())){
                        return R.failed("计价类型不存在系统:"+dto2.getChargeRuleType());
                    }else{
                        dto2.setChargeRuleType(chargeRuleTypeMap.get(dto2.getChargeRuleType()));
                    }
                }

            }

            if (CollectionUtils.isEmpty(dtoList)) {
                return R.failed("导入数据不能为空");
            }
        } catch (Exception e) {
            return R.failed("文件解析异常");
        }
        CustomDiscountMainDto dto = new CustomDiscountMainDto();
        dto.setClientCode(clientCode);
        dto.setPricingDiscountRules(list);
        return customPricesService.updateDiscountDetail(dto);
    }


}
