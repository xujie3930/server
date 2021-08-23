package com.szmsd.delivery.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.domain.dto.BasRegionSelectListQueryDto;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.delivery.dto.DelOutboundCollectionDetailImportDto;
import com.szmsd.delivery.dto.DelOutboundDetailImportDto2;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.dto.DelOutboundImportDto;
import com.szmsd.delivery.imported.*;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 出库管理
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"出库管理 - 导入"})
@ApiSort(900)
@RestController
@RequestMapping("/api/outbound/import")
public class DelOutboundImportController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(DelOutboundImportController.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private BasSubClientService basSubClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private InventoryFeignClientService inventoryFeignClientService;

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundImport:collectionExportTemplate')")
    @GetMapping("/collectionExportTemplate")
    @ApiOperation(value = "出库管理 - 导入 - 集运出库 - SKU导入模板", position = 100)
    public void collectionExportTemplate(HttpServletResponse response) {
        String filePath = "/template/Del_collection_sku_import.xls";
        String fileName = "集运出库单SKU导入";
        this.downloadTemplate(response, filePath, fileName);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundImport:collectionImportDetail')")
    @PostMapping("/collectionImportDetail")
    @ApiOperation(value = "出库管理 - 导入 - 集运出库 - SKU导入", position = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<ImportResultData<?>> collectionImportDetail(HttpServletRequest request) {
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
            ExcelReaderSheetBuilder excelReaderSheetBuilder = EasyExcelFactory.read(file.getInputStream(), DelOutboundCollectionDetailImportDto.class, null).sheet(0);
            List<DelOutboundCollectionDetailImportDto> dtoList = excelReaderSheetBuilder.doReadSync();
            if (CollectionUtils.isEmpty(dtoList)) {
                return R.ok(ImportResultData.buildFailData(ImportMessage.build("导入数据不能为空")));
            }
            // 产品属性，带电信息，电池包装
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("059,060,061");
            List<BasSubWrapperVO> productAttributeList = listMap.get("059");
            List<BasSubWrapperVO> electrifiedModeList = listMap.get("060");
            List<BasSubWrapperVO> batteryPackagingList = listMap.get("061");
            // SKU导入上下文
            DelOutboundCollectionSkuImportContext importContext = new DelOutboundCollectionSkuImportContext(dtoList, productAttributeList, electrifiedModeList, batteryPackagingList);
            // 初始化导入验证容器
            ImportResultData<DelOutboundCollectionDetailImportDto> importResult = new ImportValidationContainer<>(importContext, ImportValidation.build(new DelOutboundCollectionSkuImportValidation(importContext))).validData();
            // 验证SKU导入验证结果
            if (!importResult.isStatus()) {
                return R.ok(importResult);
            }
            // 返回成功的结果
            return R.ok(ImportResultData.buildSuccessData(dtoList));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.failed("文件解析异常");
        }
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:delOutboundImportTemplate')")
    @GetMapping("/delOutboundImportTemplate")
    @ApiOperation(value = "出库管理 - 列表 - 出库单导入模板", position = 1000)
    public void delOutboundImportTemplate(HttpServletResponse response) {
        String filePath = "/template/DM.xls";
        String fileName = "DM出库（正常，自提，销毁）模板";
        this.downloadTemplate(response, filePath, fileName);
    }

    /**
     * 下载模板
     *
     * @param response response
     * @param filePath 文件存放路径，${server.tomcat.basedir}配置的目录和resources目录下
     * @param fileName 文件名称
     */
    private void downloadTemplate(HttpServletResponse response, String filePath, String fileName) {
        // 先去模板目录中获取模板
        // 模板目录中没有模板再从项目中获取模板
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery");
        File file = new File(basedir + "/" + filePath);
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            if (file.exists()) {
                inputStream = new FileInputStream(file);
                response.setHeader("File-Source", "local");
            } else {
                Resource resource = new ClassPathResource(filePath);
                inputStream = resource.getInputStream();
                response.setHeader("File-Source", "resource");
            }
            outputStream = response.getOutputStream();
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + ".xls");
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "文件不存在，" + e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "文件流处理失败，" + e.getMessage());
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:delOutboundImport')")
    @PostMapping("/delOutboundImport")
    @ApiOperation(value = "出库管理 - 列表 - 出库单导入", position = 1100)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "sellerCode", value = "客户编码", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<ImportResult> delOutboundImport(@RequestParam("sellerCode") String sellerCode, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        AssertUtil.isTrue(StringUtils.isNotEmpty(sellerCode), "客户编码不能为空");
        try {
            // copy文件流
            byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
            // 初始化读取第一个sheet页的数据
            DefaultAnalysisEventListener<DelOutboundImportDto> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundImportDto.class, 0, 1);
            if (defaultAnalysisEventListener.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
            }
            List<DelOutboundImportDto> dataList = defaultAnalysisEventListener.getList();
            if (CollectionUtils.isEmpty(dataList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据不能为空")));
            }
            // 初始化读取第二个sheet页的数据
            DefaultAnalysisEventListener<DelOutboundDetailImportDto2> defaultAnalysisEventListener1 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundDetailImportDto2.class, 1, 1);
            if (defaultAnalysisEventListener1.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener1.getMessageList()));
            }
            List<DelOutboundDetailImportDto2> detailList = defaultAnalysisEventListener1.getList();
            if (CollectionUtils.isEmpty(detailList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据明细不能为空")));
            }
            // 查询出库类型数据
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("063,058");
            List<BasSubWrapperVO> orderTypeList = listMap.get("063");
            List<BasSubWrapperVO> deliveryMethodList = listMap.get("058");
            // 查询国家数据
            R<List<BasRegionSelectListVO>> countryListR = this.basRegionFeignService.countryList(new BasRegionSelectListQueryDto());
            List<BasRegionSelectListVO> countryList = R.getDataAndException(countryListR);
            // 初始化导入上下文
            DelOutboundImportContext importContext = new DelOutboundImportContext(dataList, orderTypeList, countryList, deliveryMethodList);
            // 初始化外联导入上下文
            DelOutboundOuterContext outerContext = new DelOutboundOuterContext();
            // 初始化导入验证容器
            ImportResult importResult = new ImportValidationContainer<>(importContext, ImportValidation.build(new DelOutboundImportValidation(outerContext, importContext))).valid();
            // 验证导入验证结果
            if (!importResult.isStatus()) {
                return R.ok(importResult);
            }
            // 初始化SKU导入上下文
            DelOutboundDetailImportContext importContext1 = new DelOutboundDetailImportContext(detailList);
            // 初始化SKU数据验证器
            DelOutboundDetailImportValidationData importValidationData = new DelOutboundDetailImportValidationData(sellerCode, inventoryFeignClientService);
            // 初始化SKU导入验证容器
            ImportResult importResult1 = new ImportValidationContainer<>(importContext1, ImportValidation.build(new DelOutboundDetailImportValidation(outerContext, importContext1, importValidationData))).valid();
            // 验证SKU导入验证结果
            if (!importResult1.isStatus()) {
                return R.ok(importResult1);
            }
            // 获取导入的数据
            List<DelOutboundDto> dtoList = new DelOutboundImportContainer(dataList, orderTypeList, countryList, deliveryMethodList, detailList, importValidationData, sellerCode).get();
            // 批量新增
            this.delOutboundService.insertDelOutbounds(dtoList);
            // 返回成功的结果
            return R.ok(ImportResult.buildSuccess());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            // 返回失败的结果
            return R.ok(ImportResult.buildFail(ImportMessage.build(e.getMessage())));
        }
    }

}
