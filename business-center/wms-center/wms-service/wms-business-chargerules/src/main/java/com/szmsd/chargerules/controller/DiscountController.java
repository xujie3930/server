
package com.szmsd.chargerules.controller;

import cn.hutool.core.date.DateUtil;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.chargerules.config.DownloadTemplateUtil;
import com.szmsd.chargerules.export.*;
import com.szmsd.chargerules.service.IDiscountService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.ExcelUtils;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.dto.DiscountCustomImportDto;
import com.szmsd.delivery.dto.DiscountDetailImportDto;
import com.szmsd.http.dto.OperationRecordDto;
import com.szmsd.http.dto.custom.AssociatedCustomersDto;
import com.szmsd.http.dto.discount.*;
import com.szmsd.http.dto.grade.GradeMainDto;
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
import java.util.ArrayList;
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


@Api(tags = {"折扣方案"})
@RestController
@RequestMapping("/discount")
public class DiscountController extends BaseController{

    @Resource
    private IDiscountService DiscountService;
    @Autowired
    private BasSubClientService basSubClientService;
    @PostMapping("/page")
    @ApiOperation(value = "分页查询折扣方案")
    @PreAuthorize("@ss.hasPermi('Discount:Discount:page')")
    public TableDataInfo<DiscountMainDto> page(@RequestBody DiscountPageRequest pageDTO) {
        if(pageDTO.getPageNumber() == null){
            pageDTO.setPageNumber(pageDTO.getPageNum());
        }
        return DiscountService.page(pageDTO);
    }
    @PostMapping("/detailResult")
    @ApiOperation(value = "获取折扣方案明细信息")
    @PreAuthorize("@ss.hasPermi('Discount:Discount:detailResult')")
    public R<DiscountMainDto> detailResult(@RequestBody String id) {
        return DiscountService.detailResult(id);
    }


    @PostMapping("/operationRecord")
    @ApiOperation(value = "获取操作日记")
    @PreAuthorize("@ss.hasPermi('Discount:Discount:detailResoperationRecordult')")
    public R<OperationRecordDto> operationRecord(@RequestBody String id) {
        return DiscountService.operationRecord(id);
    }


    @PostMapping("/create")
    @ApiOperation(value = "创建折扣方案")
    @PreAuthorize("@ss.hasPermi('Discount:Discount:create')")
    public R create(@RequestBody MergeDiscountDto dto) {
        return DiscountService.create(dto);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改折扣方案")
    @PreAuthorize("@ss.hasPermi('Discount:Discount:update')")
    public R update(@RequestBody MergeDiscountDto dto) {
        return DiscountService.update(dto);
    }

    @PostMapping("/detailImport")
    @ApiOperation(value = "修改折扣方案关联产品")
    @PreAuthorize("@ss.hasPermi('Discount:Discount:detailImport')")
    public R detailImport(@RequestBody UpdateDiscountDetailDto dto) {
        return DiscountService.detailImport(dto);
    }

    @PostMapping("/customUpdate")
    @ApiOperation(value = "修改折扣方案关联客户")
    @PreAuthorize("@ss.hasPermi('Discount:Discount:customUpdate')")
    public R customUpdate(@RequestBody UpdateDiscountCustomDto dto) {
        return DiscountService.customUpdate(dto);
    }


    @PreAuthorize("@ss.hasPermi('Discount:Discount:downloadDetailTemplate')")
    @ApiOperation(value = "关联产品 - 下载模版")
    @PostMapping("/downloadDetailTemplate")
    public void downloadDiscountTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "discountDetail");
    }

    @PreAuthorize("@ss.hasPermi('Discount:Discount:downloadDiscountTemplate')")
    @ApiOperation(value = "关联客户 - 下载模版")
    @PostMapping("/downloadDiscountTemplate")
    public void downloadCustomTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "custom");
    }

    @PreAuthorize("@ss.hasPermi('Discount:Discount:importDetailTemplate')")
    @PostMapping("/importDetailTemplate")
    @ApiOperation(value = "关联产品 - 导入", position = 200)
    @ApiImplicitParams({
    @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
   public R importDetailTemplate(@RequestParam("templateId") String templateId, HttpServletRequest request) {
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
//                        return R.failed("费用类型不存在系统:"+data.getChargeType());
                    }else{
                        data.setChargeType(chargeTypeMap.get(data.getChargeType()));
                    }
                }
                if(StringUtils.isNotEmpty(dto2.getChargeRuleType())){
                    if(!chargeRuleTypeMap.containsKey(dto2.getChargeRuleType())){
//                        return R.failed("计价类型不存在系统:"+dto2.getChargeRuleType());
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
        UpdateDiscountDetailDto dto = new UpdateDiscountDetailDto();
        dto.setTemplateId(templateId);
        dto.setPricingDiscountRules(list);
        return DiscountService.detailImport(dto);
    }

    @PreAuthorize("@ss.hasPermi('Discount:Discount:importCustomTemplate')")
    @PostMapping("/importCustomTemplate")
    @ApiOperation(value = "关联客户 - 导入", position = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R importCustomTemplate(@RequestParam("templateId") String templateId, HttpServletRequest request) {
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
        List<AssociatedCustomersDto> list = null;
        try {
            List<DiscountCustomImportDto> dtoList = new ExcelUtil<>(DiscountCustomImportDto.class).importExcel(file.getInputStream());
            list = BeanMapperUtil.mapList(dtoList, AssociatedCustomersDto.class);
            for(int i = 0; i < list.size(); i++){
                list.get(i).setBeginTime(DateUtil.formatDateTime(dtoList.get(i).getBeginTimeDate()));
                list.get(i).setEndTime(DateUtil.formatDateTime(dtoList.get(i).getEndTimeDate()));
                if("是".equals(dtoList.get(i).getIsValidStr())|| "TRUE".equals(dtoList.get(i).getIsValidStr())){
                    list.get(i).setIsValid(true);
                }else{
                    list.get(i).setIsValid(false);
                }
            }
            if (CollectionUtils.isEmpty(dtoList)) {
                return R.failed("导入数据不能为空");
            }
        } catch (Exception e) {
            return R.failed("文件解析异常");
        }
        UpdateDiscountCustomDto dto = new UpdateDiscountCustomDto();
        dto.setTemplateId(templateId);
        dto.setAssociatedCustomers(list);
        return DiscountService.customUpdate(dto);
    }


    @PreAuthorize("@ss.hasPermi('grade:grade:exportDetail')")
    @PostMapping("/exportDetail")
    @ApiOperation(value = "折扣方案 - 导出产品")
    public void exportDetail(HttpServletResponse response, @RequestBody DiscountMainDto dto) {
        try {
            List<DiscountDetailExportListVO> gradeDetailDtoList = BeanMapperUtil.mapList(dto.getPricingDiscountRules()
                    , DiscountDetailExportListVO.class);
            for(int j = 0; j < gradeDetailDtoList.size(); j++) {
                gradeDetailDtoList.get(j).setName(dto.getName());
                //封装明细
                BeanUtils.copyProperties(dto.getPricingDiscountRules().get(j).getPackageLimit(), gradeDetailDtoList.get(j));
                BeanUtils.copyProperties(dto.getPricingDiscountRules().get(j).getFormula(), gradeDetailDtoList.get(j));
            };
            QueryPage gradeDetailExportQueryPage = new CommonExportQueryPage(gradeDetailDtoList);
            ExcelUtils.export(response, null, ExcelUtils.ExportExcel.build("DiscountDetailInfo", "zh",
                    null, new ExcelUtils.ExportSheet<DiscountDetailExportListVO>() {
                        @Override
                        public String sheetName() {
                            return "关联产品";
                        }
                        @Override
                        public Class<DiscountDetailExportListVO> classType() {
                            return DiscountDetailExportListVO.class;
                        }
                        @Override
                        public QueryPage<DiscountDetailExportListVO> query(ExcelUtils.ExportContext exportContext) {
                            return gradeDetailExportQueryPage;
                        }
                    }));
        } catch (Exception e) {
            log.error("导出异常:" + e.getMessage(), e);
        }
    }
    @PreAuthorize("@ss.hasPermi('grade:grade:exportCustome')")
    @PostMapping("/exportCustome")
    @ApiOperation(value = "折扣方案 - 导出客户")
    public void exportCustome(HttpServletResponse response,  @RequestBody GradeMainDto dto) {
        try {
            List<CustomExportListVO> newGradeCustomExportListVOList
                    = BeanMapperUtil.mapList(dto.getAssociatedCustomers(), CustomExportListVO.class);
            for(int j = 0; j < newGradeCustomExportListVOList.size(); j++){
                newGradeCustomExportListVOList.get(j).setName(dto.getName());
                Boolean isValid = dto.getAssociatedCustomers().get(j).getIsValid();
                if(isValid != null && isValid){
                    newGradeCustomExportListVOList.get(j).setIsValidStr("是");
                }else{
                    newGradeCustomExportListVOList.get(j).setIsValidStr("否");
                }
            }
            QueryPage gradeCustomExportQueryPage = new CommonExportQueryPage(newGradeCustomExportListVOList);


            ExcelUtils.export(response, null, ExcelUtils.ExportExcel.build("DiscountCustomeInfo", "zh",
                    null, new ExcelUtils.ExportSheet<CustomExportListVO>() {
                        @Override
                        public String sheetName() {
                            return "关联客户";
                        }
                        @Override
                        public Class<CustomExportListVO> classType() {
                            return CustomExportListVO.class;
                        }
                        @Override
                        public QueryPage<CustomExportListVO> query(ExcelUtils.ExportContext exportContext) {
                            return gradeCustomExportQueryPage;
                        }
                    }));
        } catch (Exception e) {
            log.error("导出异常:" + e.getMessage(), e);
        }
    }

    @PreAuthorize("@ss.hasPermi('grade:grade:export')")
    @PostMapping("/export")
    @ApiOperation(value = "折扣方案 - 导出")
    public void export(HttpServletResponse response, @RequestBody DiscountPageRequest pageDTO) {
        try {
            pageDTO.setPageNum(1);
            if(pageDTO.getPageSize() == null){
                pageDTO.setPageSize(99999);

            }
            TableDataInfo<DiscountMainDto> mainList =  DiscountService.page(pageDTO);

            List<CommonExportListVO> newList = BeanMapperUtil.mapList(mainList.getRows(), CommonExportListVO.class);

            List<DiscountDetailExportListVO> gradeDetailDtoList = new ArrayList<>();
            List<CustomExportListVO> gradeCustomExportListVOList = new ArrayList<>();

            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("046,047");
            List<BasSubWrapperVO> chargeType = listMap.get("046");
            List<BasSubWrapperVO> chargeRuleType = listMap.get("047");
            Map<String, String> chargeTypeMap =
                    chargeType.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubValue, BasSubWrapperVO::getSubName));
            Map<String, String> chargeRuleTypeMap =
                    chargeRuleType.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubValue, BasSubWrapperVO::getSubName));
            
            
            for(int i = 0; i < newList.size(); i++){

                DiscountMainDto dto = mainList.getRows().get(i);
                newList.get(i).setCreation(dto.getCreation().getOperator().getName());
                newList.get(i).setTime(mainList.getRows().get(i).getLastModifyOperation().getTime());

                //封装明细
                List<DiscountDetailExportListVO> newDetailExportListVOList
                        = BeanMapperUtil.mapList(dto.getPricingDiscountRules(), DiscountDetailExportListVO.class);
                for(int j = 0; j < newDetailExportListVOList.size(); j++){
                    newDetailExportListVOList.get(j).setName(dto.getName());
                    BeanUtils.copyProperties(dto.getPricingDiscountRules().get(j).getPackageLimit(), newDetailExportListVOList.get(j));
                    BeanUtils.copyProperties(dto.getPricingDiscountRules().get(j).getFormula(), newDetailExportListVOList.get(j));

                    DiscountDetailExportListVO data = newDetailExportListVOList.get(j);
                    if(StringUtils.isNotEmpty(data.getChargeType())){
                        if(chargeTypeMap.containsKey(data.getChargeType())){
                            data.setChargeType(chargeTypeMap.get(data.getChargeType()));
                        }
                    }
                    if(StringUtils.isNotEmpty(data.getChargeRuleType())){
                        if(chargeRuleTypeMap.containsKey(data.getChargeRuleType())){
                            data.setChargeRuleType(chargeRuleTypeMap.get(data.getChargeRuleType()));
                        }
                    }


                }

                List<CustomExportListVO> newGradeCustomExportListVOList
                        = BeanMapperUtil.mapList(dto.getAssociatedCustomers(), CustomExportListVO.class);



                for(int j = 0; j < newGradeCustomExportListVOList.size(); j++){
                    newGradeCustomExportListVOList.get(j).setName(dto.getName());
                    Boolean isValid = dto.getAssociatedCustomers().get(j).getIsValid();
                    if(isValid != null && isValid){
                        newGradeCustomExportListVOList.get(j).setIsValidStr("是");
                    }else{
                        newGradeCustomExportListVOList.get(j).setIsValidStr("否");
                    }
                }


                gradeDetailDtoList.addAll(newDetailExportListVOList);
                gradeCustomExportListVOList.addAll(newGradeCustomExportListVOList);
            }
            QueryPage gradeExportQueryPage = new CommonExportQueryPage(newList);
            QueryPage gradeDetailExportQueryPage = new CommonExportQueryPage(gradeDetailDtoList);
            QueryPage gradeCustomExportQueryPage = new CommonExportQueryPage(gradeCustomExportListVOList);



            ExcelUtils.export(response, null, ExcelUtils.ExportExcel.build("DiscountInfo", "zh",
                    null, new ExcelUtils.ExportSheet<CommonExportListVO>() {
                        @Override
                        public String sheetName() {
                            return "折扣方案信息";
                        }
                        @Override
                        public Class<CommonExportListVO> classType() {
                            return CommonExportListVO.class;
                        }
                        @Override
                        public QueryPage<CommonExportListVO> query(ExcelUtils.ExportContext exportContext) {
                            return gradeExportQueryPage;
                        }
                    }, new ExcelUtils.ExportSheet<DiscountDetailExportListVO>() {
                        @Override
                        public String sheetName() {
                            return "关联产品";
                        }
                        @Override
                        public Class<DiscountDetailExportListVO> classType() {
                            return DiscountDetailExportListVO.class;
                        }
                        @Override
                        public QueryPage<DiscountDetailExportListVO> query(ExcelUtils.ExportContext exportContext) {
                            return gradeDetailExportQueryPage;
                        }
                    }, new ExcelUtils.ExportSheet<CustomExportListVO>() {
                        @Override
                        public String sheetName() {
                            return "关联客户";
                        }
                        @Override
                        public Class<CustomExportListVO> classType() {
                            return CustomExportListVO.class;
                        }
                        @Override
                        public QueryPage<CustomExportListVO> query(ExcelUtils.ExportContext exportContext) {
                            return gradeCustomExportQueryPage;
                        }
                    }));
        } catch (Exception e) {
            log.error("导出异常:" + e.getMessage(), e);
        }
    }

}
