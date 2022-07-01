
package com.szmsd.chargerules.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.szmsd.bas.dto.BaseProductImportDto;
import com.szmsd.chargerules.config.DownloadTemplateUtil;
import com.szmsd.chargerules.service.ICustomPricesService;
import com.szmsd.chargerules.service.IGradeService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.dto.CustomPricesDiscountImportDto;
import com.szmsd.delivery.dto.DelOutboundCollectionDetailImportDto;
import com.szmsd.delivery.dto.GradeCustomImportDto;
import com.szmsd.delivery.dto.GradeDetailImportDto;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.grade.*;
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


@Api(tags = {"等级方案"})
@RestController
@RequestMapping("/grade")
public class GradeController extends BaseController{

    @Resource
    private IGradeService gradeService;

    @PostMapping("/page")
    @ApiOperation(value = "分页查询等级方案")
    @PreAuthorize("@ss.hasPermi('grade:grade:page')")
    public TableDataInfo<ChaLevelMaintenanceDto> page(@RequestBody GradePageRequest pageDTO) {
        return gradeService.page(pageDTO);
    }

    @PostMapping("/detailResult")
    @ApiOperation(value = "获取等级方案明细信息")
    @PreAuthorize("@ss.hasPermi('grade:grade:detailResult')")
    public R detailResult(@RequestBody String id) {
        return gradeService.detailResult(id);
    }


    @PostMapping("/create")
    @ApiOperation(value = "创建等级方案")
    @PreAuthorize("@ss.hasPermi('grade:grade:create')")
    public R create(@RequestBody MergeGradeDto dto) {
        return gradeService.create(dto);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改等级方案")
    @PreAuthorize("@ss.hasPermi('grade:grade:update')")
    public R update(@RequestBody MergeGradeDto dto) {
        return gradeService.update(dto);
    }

    @PostMapping("/detailImport")
    @ApiOperation(value = "修改等级方案关联产品")
    @PreAuthorize("@ss.hasPermi('grade:grade:detailImport')")
    public R detailImport(@RequestBody UpdateGradeDetailDto dto) {
        return gradeService.detailImport(dto);
    }

    @PostMapping("/customUpdate")
    @ApiOperation(value = "修改等级方案关联客户")
    @PreAuthorize("@ss.hasPermi('grade:grade:customUpdate')")
    public R customUpdate(@RequestBody UpdateGradeCustomDto dto) {
        return gradeService.customUpdate(dto);
    }


    @PreAuthorize("@ss.hasPermi('grade:grade:downloadDetailTemplate')")
    @ApiOperation(value = "关联产品 - 下载模版")
    @PostMapping("/downloadDetailTemplate")
    public void downloadDiscountTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "product");
    }

    @PreAuthorize("@ss.hasPermi('grade:grade:downloadGradeTemplate')")
    @ApiOperation(value = "关联客户 - 下载模版")
    @PostMapping("/downloadGradeTemplate")
    public void downloadCustomTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "custom");
    }

    @PreAuthorize("@ss.hasPermi('grade:grade:importDetailTemplate')")
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
        List<GradeDto> list = null;
        try {
            List<GradeDetailImportDto> dtoList = EasyExcelFactory.read(file.getInputStream(), GradeDetailImportDto.class, null).sheet(0).doReadSync();
            list = BeanMapperUtil.mapList(dtoList, GradeDto.class);
            if (CollectionUtils.isEmpty(dtoList)) {
                return R.failed("导入数据不能为空");
            }
        } catch (Exception e) {
            return R.failed("文件解析异常");
        }
        UpdateGradeDetailDto dto = new UpdateGradeDetailDto();
        dto.setTemplateId(templateId);
        dto.setPricingGradeRules(list);
        return gradeService.detailImport(dto);
    }

    @PreAuthorize("@ss.hasPermi('grade:grade:importCustomTemplate')")
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
            ExcelReaderSheetBuilder excelReaderSheetBuilder = EasyExcelFactory.read(file.getInputStream(),
                    GradeCustomImportDto.class, null).sheet(0);
            List<GradeDetailImportDto> dtoList = excelReaderSheetBuilder.doReadSync();
            list = BeanMapperUtil.mapList(dtoList, AssociatedCustomersDto.class);
            if (CollectionUtils.isEmpty(dtoList)) {
                return R.failed("导入数据不能为空");
            }
        } catch (Exception e) {
            return R.failed("文件解析异常");
        }
        UpdateGradeCustomDto dto = new UpdateGradeCustomDto();
        dto.setTemplateId(templateId);
        dto.setAssociatedCustomers(list);
        return gradeService.customUpdate(dto);
    }


}
