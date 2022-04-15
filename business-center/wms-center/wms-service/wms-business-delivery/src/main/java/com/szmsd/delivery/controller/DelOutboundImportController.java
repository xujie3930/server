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
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.imported.*;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
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
import java.net.URLEncoder;
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

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundImport:collectionImportTemplate')")
    @GetMapping("/collectionImportTemplate")
    @ApiOperation(value = "出库管理 - 导入 - 集运出库导入模板", position = 300)
    public void collectionImportTemplate(HttpServletResponse response) {
        String filePath = "/template/DM_collection.xls";
        String fileName = "集运出库模板";
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
            String efn = URLEncoder.encode(fileName, "utf-8");
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + efn + ".xls");
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

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundImport:collectionImport')")
    @PostMapping("/collectionImport")
    @ApiOperation(value = "出库管理 - 导入 - 集运出库导入", position = 400)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "sellerCode", value = "客户编码", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<ImportResult> collectionImport(@RequestParam("sellerCode") String sellerCode, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        AssertUtil.isTrue(StringUtils.isNotEmpty(sellerCode), "客户编码不能为空");
        try {
            // copy文件流
            byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
            // 初始化读取第一个sheet页的数据
            DefaultAnalysisEventListener<DelOutboundCollectionImportDto> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundCollectionImportDto.class, 0, 1);
            if (defaultAnalysisEventListener.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
            }
            List<DelOutboundCollectionImportDto> dataList = defaultAnalysisEventListener.getList();
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
            // 查询国家数据
            R<List<BasRegionSelectListVO>> countryListR = this.basRegionFeignService.countryList(new BasRegionSelectListQueryDto());
            List<BasRegionSelectListVO> countryList = R.getDataAndException(countryListR);
            // 初始化导入上下文
            DelOutboundCollectionImportContext importContext = new DelOutboundCollectionImportContext(dataList, countryList);
            // 初始化外联导入上下文
            DelOutboundOuterContext outerContext = new DelOutboundOuterContext();
            // 初始化导入验证容器
            ImportResult importResult = new ImportValidationContainer<>(importContext, ImportValidation.build(new DelOutboundCollectionImportValidation(outerContext, importContext))).valid();
            // 验证导入验证结果
            if (!importResult.isStatus()) {
                return R.ok(importResult);
            }
            // 初始化SKU导入上下文
            DelOutboundDetailImportContext importContext1 = new DelOutboundDetailImportContext(detailList);
            // 初始化SKU数据验证器
            DelOutboundDetailImportValidationData importValidationData = new DelOutboundDetailImportValidationData(sellerCode, inventoryFeignClientService);
            // 初始化SKU导入验证容器
            ImportResult importResult1 = new ImportValidationContainer<>(importContext1, ImportValidation.build(new DelOutboundCollectionDetailImportValidation(outerContext, importContext1))).valid();
            // 验证SKU导入验证结果
            if (!importResult1.isStatus()) {
                return R.ok(importResult1);
            }
            // 获取导入的数据
            List<DelOutboundDto> dtoList = new DelOutboundCollectionImportContainer(dataList, countryList, detailList, importValidationData, sellerCode).get();
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

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundImport:packageTransferImportTemplate')")
    @GetMapping("/packageTransferImportTemplate")
    @ApiOperation(value = "出库管理 - 导入 - 转运出库导入模板", position = 500)
    public void packageTransferImportTemplate(HttpServletResponse response) {
        String filePath = "/template/DM_packageTransfer.xls";
        String fileName = "转运出库模板";
        this.downloadTemplate(response, filePath, fileName);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundImport:packageTransferImport')")
    @PostMapping("/packageTransferImport")
    @ApiOperation(value = "出库管理 - 导入 - 转运出库导入", position = 501)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "sellerCode", value = "客户编码", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<ImportResult> packageTransferImport(@RequestParam("sellerCode") String sellerCode, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        AssertUtil.isTrue(StringUtils.isNotEmpty(sellerCode), "客户编码不能为空");
        try {
            // copy文件流
            byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
            // 初始化读取第一个sheet页的数据
            DefaultAnalysisEventListener<DelOutboundPackageTransferImportDto> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundPackageTransferImportDto.class, 0, 1);
            if (defaultAnalysisEventListener.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
            }
            List<DelOutboundPackageTransferImportDto> dataList = defaultAnalysisEventListener.getList();
            if (CollectionUtils.isEmpty(dataList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据不能为空")));
            }
            // 初始化读取第二个sheet页的数据
            DefaultAnalysisEventListener<DelOutboundPackageTransferDetailImportDto> defaultAnalysisEventListener1 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundPackageTransferDetailImportDto.class, 1, 1);
            if (defaultAnalysisEventListener1.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener1.getMessageList()));
            }
            List<DelOutboundPackageTransferDetailImportDto> detailList = defaultAnalysisEventListener1.getList();
            if (CollectionUtils.isEmpty(detailList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据明细不能为空")));
            }
            // 产品属性，带电信息，电池包装
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("059,060,061,076");
            List<BasSubWrapperVO> productAttributeList = listMap.get("059");
            List<BasSubWrapperVO> electrifiedModeList = listMap.get("060");
            List<BasSubWrapperVO> batteryPackagingList = listMap.get("061");
            List<BasSubWrapperVO> packageConfirmList = listMap.get("076");
            // 查询国家数据
            R<List<BasRegionSelectListVO>> countryListR = this.basRegionFeignService.countryList(new BasRegionSelectListQueryDto());
            List<BasRegionSelectListVO> countryList = R.getDataAndException(countryListR);
            // 初始化导入上下文
            DelOutboundPackageTransferImportContext importContext = new DelOutboundPackageTransferImportContext(dataList, countryList, packageConfirmList);
            // 初始化外联导入上下文
            DelOutboundOuterContext outerContext = new DelOutboundOuterContext();
            // 初始化导入验证容器
            ImportResult importResult = new ImportValidationContainer<>(importContext, ImportValidation.build(new DelOutboundPackageTransferImportValidation(outerContext, importContext))).valid();
            // 验证导入验证结果
            if (!importResult.isStatus()) {
                return R.ok(importResult);
            }
            // 初始化SKU导入上下文
            DelOutboundPackageTransferDetailImportContext importContext1 = new DelOutboundPackageTransferDetailImportContext(detailList, productAttributeList, electrifiedModeList, batteryPackagingList);
            // 初始化SKU导入验证容器
            ImportResult importResult1 = new ImportValidationContainer<>(importContext1, ImportValidation.build(new DelOutboundPackageTransferDetailImportValidation(outerContext, importContext1))).valid();
            // 验证SKU导入验证结果
            if (!importResult1.isStatus()) {
                return R.ok(importResult1);
            }
            // 获取导入的数据
            List<DelOutboundDto> dtoList = new DelOutboundPackageTransferImportContainer(dataList, countryList, packageConfirmList, detailList, sellerCode).get();
            // 批量新增
            List<DelOutboundAddResponse> outboundAddResponseList = this.delOutboundService.insertDelOutbounds(dtoList);
            // 返回成功的结果
            ImportResult buildSuccess = ImportResult.buildSuccess();
            buildSuccess.setResultList(outboundAddResponseList);
            return R.ok(buildSuccess);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            // 返回失败的结果
            return R.ok(ImportResult.buildFail(ImportMessage.build(e.getMessage())));
        }
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundImport:batchImportTemplate')")
    @GetMapping("/batchImportTemplate")
    @ApiOperation(value = "出库管理 - 导入 - 批量出库导入模板", position = 600)
    public void batchImportTemplate(HttpServletResponse response) {
        String filePath = "/template/DM_batch.xlsx";
        String fileName = "批量出库模板";
        this.downloadTemplate(response, filePath, fileName);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundImport:batchImport')")
    @PostMapping("/batchImport")
    @ApiOperation(value = "出库管理 - 导入 - 批量出库导入", position = 601)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "sellerCode", value = "客户编码", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<ImportResult> batchImport(@RequestParam("sellerCode") String sellerCode, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        AssertUtil.isTrue(StringUtils.isNotEmpty(sellerCode), "客户编码不能为空");
        try {
            // copy文件流
            byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
            // 初始化读取第一个sheet页的数据
            DefaultAnalysisEventListener<DelOutboundBatchImportDto> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundBatchImportDto.class, 0, 1);
            if (defaultAnalysisEventListener.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
            }
            List<DelOutboundBatchImportDto> dataList = defaultAnalysisEventListener.getList();
            if (CollectionUtils.isEmpty(dataList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据不能为空")));
            }
            // 初始化读取第二个sheet页的数据
            DefaultAnalysisEventListener<DelOutboundBatchDetailImportDto> defaultAnalysisEventListener1 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundBatchDetailImportDto.class, 1, 1);
            if (defaultAnalysisEventListener1.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener1.getMessageList()));
            }
            List<DelOutboundBatchDetailImportDto> detailList = defaultAnalysisEventListener1.getList();
            if (CollectionUtils.isEmpty(detailList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据明细不能为空")));
            }
            // 出货渠道
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("079");
            List<BasSubWrapperVO> shipmentChannelList = listMap.get("079");
            // 查询国家数据
            R<List<BasRegionSelectListVO>> countryListR = this.basRegionFeignService.countryList(new BasRegionSelectListQueryDto());
            List<BasRegionSelectListVO> countryList = R.getDataAndException(countryListR);
            // 初始化导入上下文
            DelOutboundBatchImportContext importContext = new DelOutboundBatchImportContext(dataList, countryList, shipmentChannelList);
            // 初始化外联导入上下文
            DelOutboundOuterContext outerContext = new DelOutboundOuterContext();
            // 初始化导入验证容器
            ImportResult importResult = new ImportValidationContainer<>(importContext, ImportValidation.build(new DelOutboundBatchImportValidation(outerContext, importContext))).valid();
            // 验证导入验证结果
            if (!importResult.isStatus()) {
                return R.ok(importResult);
            }
            // 初始化SKU导入上下文
            DelOutboundBatchDetailImportContext importContext1 = new DelOutboundBatchDetailImportContext(detailList);
            // 初始化SKU导入验证容器
            ImportResult importResult1 = new ImportValidationContainer<>(importContext1, ImportValidation.build(new DelOutboundBatchDetailImportValidation(outerContext, importContext1))).valid();
            // 验证SKU导入验证结果
            if (!importResult1.isStatus()) {
                return R.ok(importResult1);
            }
            // 获取导入的数据
            List<DelOutboundDto> dtoList = new DelOutboundBatchImportContainer(dataList, countryList, shipmentChannelList, detailList, sellerCode).get();
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
