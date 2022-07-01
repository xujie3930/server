
package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.config.DownloadTemplateUtil;
import com.szmsd.chargerules.service.IDiscountService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.dto.DiscountCustomImportDto;
import com.szmsd.delivery.dto.DiscountDetailImportDto;
import com.szmsd.http.dto.custom.AssociatedCustomersDto;
import com.szmsd.http.dto.discount.*;
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


@Api(tags = {"折扣方案"})
@RestController
@RequestMapping("/discount")
public class DiscountController extends BaseController{

    @Resource
    private IDiscountService DiscountService;

    @PostMapping("/page")
    @ApiOperation(value = "分页查询折扣方案")
    @PreAuthorize("@ss.hasPermi('Discount:Discount:page')")
    public TableDataInfo<DiscountMainDto> page(@RequestBody DiscountPageRequest pageDTO) {
        return DiscountService.page(pageDTO);
    }

    @PostMapping("/detailResult")
    @ApiOperation(value = "获取折扣方案明细信息")
    @PreAuthorize("@ss.hasPermi('Discount:Discount:detailResult')")
    public R<DiscountMainDto> detailResult(@RequestBody String id) {
        return DiscountService.detailResult(id);
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
        List<DiscountDetailDto> list = null;
        try {
            List<DiscountDetailImportDto> dtoList = new ExcelUtil<>(DiscountDetailImportDto.class).importExcel(file.getInputStream());
            list = BeanMapperUtil.mapList(dtoList, DiscountDetailDto.class);

            for(int i = 0; i < dtoList.size(); i++){

                DiscountDetailDto data = list.get(i);
                DiscountDetailPackageLimitDto dto1 = new DiscountDetailPackageLimitDto();
                DiscountDetailFormulaDto dto2 = new DiscountDetailFormulaDto();
                data.setPackageLimit(dto1);
                data.setFormula(dto2);
                BeanUtils.copyProperties(dtoList.get(i), dto1);
                BeanUtils.copyProperties(dtoList.get(i), dto2);

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


}
