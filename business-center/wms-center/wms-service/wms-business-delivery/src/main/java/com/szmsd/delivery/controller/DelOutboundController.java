package com.szmsd.delivery.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.domain.dto.BasRegionSelectListQueryDto;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.ExcelUtils;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.validator.ValidationSaveGroup;
import com.szmsd.common.core.validator.ValidationUpdateGroup;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.exported.DelOutboundExportContext;
import com.szmsd.delivery.exported.DelOutboundExportItemQueryPage;
import com.szmsd.delivery.exported.DelOutboundExportQueryPage;
import com.szmsd.delivery.imported.DefaultAnalysisEventListener;
import com.szmsd.delivery.imported.DelOutboundBatchUpdateTrackingNoAnalysisEventListener;
import com.szmsd.delivery.imported.DelOutboundDetailImportContext;
import com.szmsd.delivery.imported.DelOutboundDetailImportValidation;
import com.szmsd.delivery.imported.DelOutboundDetailImportValidationData;
import com.szmsd.delivery.imported.DelOutboundImportContainer;
import com.szmsd.delivery.imported.DelOutboundImportContext;
import com.szmsd.delivery.imported.DelOutboundImportValidation;
import com.szmsd.delivery.imported.DelOutboundOuterContext;
import com.szmsd.delivery.imported.DelOutboundSkuImportContainer;
import com.szmsd.delivery.imported.DelOutboundSkuImportContext;
import com.szmsd.delivery.imported.DelOutboundSkuImportValidation;
import com.szmsd.delivery.imported.EasyExcelFactoryUtil;
import com.szmsd.delivery.imported.ImportMessage;
import com.szmsd.delivery.imported.ImportResult;
import com.szmsd.delivery.imported.ImportResultData;
import com.szmsd.delivery.imported.ImportValidation;
import com.szmsd.delivery.imported.ImportValidationContainer;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundBringVerifyVO;
import com.szmsd.delivery.vo.DelOutboundDetailListVO;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.delivery.vo.DelOutboundExportItemListVO;
import com.szmsd.delivery.vo.DelOutboundExportListVO;
import com.szmsd.delivery.vo.DelOutboundLabelResponse;
import com.szmsd.delivery.vo.DelOutboundListExceptionMessageExportVO;
import com.szmsd.delivery.vo.DelOutboundListExceptionMessageVO;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.delivery.vo.DelOutboundThirdPartyVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.QueryFinishListDTO;
import com.szmsd.inventory.domain.vo.QueryFinishListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 出库管理
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"出库管理"})
@ApiSort(100)
@RestController
@RequestMapping("/api/outbound")
public class DelOutboundController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(DelOutboundController.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    @Lazy
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private BasSubClientService basSubClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private InventoryFeignClientService inventoryFeignClientService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;
    @Autowired
    private IDelOutboundDetailService delOutboundDetailService;
    @Autowired
    private BaseProductClientService baseProductClientService;
    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/page")
    @ApiOperation(value = "出库管理 - 分页", position = 100)
    @AutoValue
    public TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryDto queryDto) {
        startPage(queryDto);
        return getDataTable(this.delOutboundService.selectDelOutboundList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('inventory:queryFinishList')")
    @PostMapping("/queryFinishList")
    @ApiOperation(value = "查询已完成的单号", notes = "查询已完成的单号")
    public TableDataInfo<QueryFinishListVO> queryFinishList(@RequestBody QueryFinishListDTO queryFinishListDTO) {
        startPage(queryFinishListDTO);
        List<QueryFinishListVO> list = delOutboundService.queryFinishList(queryFinishListDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "出库管理 - 详情", position = 200)
    @AutoValue
    public R<DelOutboundVO> getInfo(@PathVariable("id") String id) {
        return R.ok(delOutboundService.selectDelOutboundById(id));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:getInfoByOrderNo')")
    @GetMapping(value = "getInfoByOrderNo/{orderNo}")
    @ApiOperation(value = "出库管理 - 详情", position = 201)
    @AutoValue
    public R<DelOutboundVO> getInfoByOrderNo(@PathVariable("orderNo") String orderNo) {
        return R.ok(delOutboundService.selectDelOutboundByOrderNo(orderNo));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:getInfoForThirdParty')")
    @PostMapping(value = "getInfoForThirdParty")
    @ApiOperation(value = "出库管理 - 第三方订单查看专用接口", position = 202)
    @AutoValue
    public R<DelOutboundThirdPartyVO> getInfoForThirdParty(@RequestBody DelOutboundVO vo) {
        return R.ok(delOutboundService.getInfoForThirdParty(vo));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:queryDetailsLabelByNos')")
    @PostMapping(value = "/queryDetailsLabelByNos")
    @ApiOperation(value = "出库管理 - 标签SKU详情", position = 203)
    public R<Map<String, String>> queryDetailsLabelByNos(@RequestBody List<String> nos) {
        return R.ok(delOutboundDetailService.queryDetailsLabelByNos(nos));
    }

    /**
     * 出库-创建采购单 查询
     *
     * @param idList
     * @return
     */
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "createPurchaseOrderListByIdList/{idList}")
    @ApiOperation(value = "出库-创建采购单  查询")
    public R<List<DelOutboundDetailVO>> createPurchaseOrderListByIdList(@PathVariable("idList") List<String> idList) {
        return R.ok(delOutboundService.createPurchaseOrderListByIdList(idList));
    }

    /**
     * 出库-创建采购单后回写出库单 采购单号
     * 多个出库单，对应一个采购单
     *
     * @param purchaseNo  采购单号
     * @param orderNoList 出库单列表
     * @return
     */
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "purchase/setPurchaseNo/{purchaseNo}/{orderNoList}")
    @ApiOperation(value = "出库-实际创建采购单后回写采购单号")
    public R setPurchaseNo(@PathVariable("purchaseNo") String purchaseNo, @PathVariable("orderNoList") List<String> orderNoList) {
        return R.ok(delOutboundService.setPurchaseNo(purchaseNo, orderNoList));
    }

    /**
     * 出库-创建采购单
     *
     * @param idList
     * @return
     */
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "getTransshipmentProductData/{idList}")
    @ApiOperation(value = "转运-获取转运里面的商品数据")
    public R<List<DelOutboundDetailVO>> getTransshipmentProductData(@PathVariable("idList") List<String> idList) {
        return R.ok(delOutboundService.getTransshipmentProductData(idList));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "getInfoByOrderId/{orderId}")
    @ApiOperation(value = "出库管理 - 详情", position = 201)
    public R<DelOutbound> getInfoByOrderId(@PathVariable("orderId") String orderId) {
        return R.ok(delOutboundService.selectDelOutboundByOrderId(orderId));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "/getStatusByOrderNo")
    @ApiOperation(value = "出库管理 - 根据订单号查询订单信息", position = 202)
    public R<DelOutbound> getStatusByOrderNo(@RequestParam("orderNo") String orderNo) {
        return R.ok(delOutboundService.getByOrderNo(orderNo));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:add')")
    @Log(title = "出库单模块", businessType = BusinessType.INSERT)
    @PostMapping("/shipment")
    @ApiOperation(value = "出库管理 - 创建", position = 300)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<DelOutboundAddResponse> add(@RequestBody @Validated({ValidationSaveGroup.class}) DelOutboundDto dto) {
        DelOutboundAddResponse data = delOutboundService.insertDelOutbound(dto);
        return R.ok(data);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:addPackageCollection')")
    @Log(title = "出库单模块", businessType = BusinessType.INSERT)
    @PostMapping("/shipment-package-collection")
    @ApiOperation(value = "出库管理 - 创建揽收销毁出库单", position = 310)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<DelOutboundAddResponse> addPackageCollection(@RequestBody @Validated({ValidationSaveGroup.class}) DelOutboundDto dto) {
        DelOutboundAddResponse data = delOutboundService.insertDelOutbound(dto);
        if (data.getStatus()) {
            this.delOutboundCompletedService.add(data.getOrderNo(), DelOutboundOperationTypeEnum.BRING_VERIFY.getCode());
        }
        return R.ok(data);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:edit')")
    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PutMapping("/shipment")
    @ApiOperation(value = "出库管理 - 修改", position = 400)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<Integer> edit(@RequestBody @Validated(ValidationUpdateGroup.class) DelOutboundDto dto) {
        return R.ok(delOutboundService.updateDelOutbound(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:updateWeightDelOutbound')")
    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/updateWeightDelOutbound")
    @ApiOperation(value = "出库管理 - 修改", position = 400)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<Integer> updateWeightDelOutbound(@RequestBody @Validated(ValidationUpdateGroup.class) UpdateWeightDelOutboundDto dto) {
        LambdaQueryWrapper<DelOutbound> queryWrapper = new LambdaQueryWrapper<DelOutbound>();
        queryWrapper.eq(DelOutbound::getSellerCode, dto.getCustomCode());
        queryWrapper.eq(DelOutbound::getOrderNo, dto.getOrderNo());
        DelOutbound data = delOutboundService.getOne(queryWrapper);
        if(data == null){
            throw new CommonException("400", "该客户下订单不存在");
        }
        if (
                DelOutboundStateEnum.PROCESSING.getCode().equals(data.getState())
                || DelOutboundStateEnum.NOTIFY_WHSE_PROCESSING.getCode().equals(data.getState())
                || DelOutboundStateEnum.WHSE_PROCESSING.getCode().equals(data.getState())
                || DelOutboundStateEnum.WHSE_COMPLETED.getCode().equals(data.getState())
                || DelOutboundStateEnum.COMPLETED.getCode().equals(data.getState())
        ) {
            throw new CommonException("400", "单据不能修改");
        }
        BeanUtils.copyProperties(dto, data);
        return R.ok(delOutboundService.updateById(data) ? 1 : 0);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:remove')")
    @Log(title = "出库单模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/shipment")
    @ApiOperation(value = "出库管理 - 删除", position = 500)
    public R<Integer> remove(@RequestBody List<String> ids) {
        return R.ok(delOutboundService.deleteDelOutboundByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:bringVerify')")
    @PostMapping("/bringVerify")
    @ApiOperation(value = "出库管理 - 提审", position = 600)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundBringVerifyDto")
    public R<List<DelOutboundBringVerifyVO>> bringVerify(@RequestBody @Validated DelOutboundBringVerifyDto dto) {
        return R.ok(delOutboundBringVerifyService.bringVerify(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:canceled')")
    @PostMapping("/canceled")
    @ApiOperation(value = "出库管理 - 取消", position = 700)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundCanceledDto")
    public R<Integer> canceled(@RequestBody @Validated DelOutboundCanceledDto dto) {
        return R.ok(this.delOutboundService.canceled(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:exportTemplate')")
    @GetMapping("/exportTemplate")
    @ApiOperation(value = "出库管理 - 新增 - SKU导入模板", position = 800)
    public void exportTemplate(HttpServletResponse response) {
        String filePath = "/template/Del_sku_import.xlsx";
        String fileName = "出库单SKU导入";
        this.downloadTemplate(response, filePath, fileName);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:importdetail')")
    @PostMapping("/importDetail")
    @ApiOperation(value = "出库管理 - 新增 - SKU导入", position = 900)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "warehouseCode", value = "仓库编码", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "sellerCode", value = "客户编码", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<ImportResultData<?>> importDetail(@RequestParam("warehouseCode") String warehouseCode, @RequestParam("sellerCode") String sellerCode, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        AssertUtil.isTrue(StringUtils.isNotEmpty(warehouseCode), "仓库编码不能为空");
        AssertUtil.isTrue(StringUtils.isNotEmpty(sellerCode), "客户编码不能为空");
        String originalFilename = file.getOriginalFilename();
        AssertUtil.notNull(originalFilename, "导入文件名称不存在");
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf + 1);
        boolean isXlsx = "xlsx".equals(suffix);
        AssertUtil.isTrue(isXlsx, "请上传xlsx文件");
        try {
            ExcelReaderSheetBuilder excelReaderSheetBuilder = EasyExcelFactory.read(file.getInputStream(), DelOutboundDetailImportDto.class, null).sheet(0);
            List<DelOutboundDetailImportDto> dtoList = excelReaderSheetBuilder.doReadSync();
            if (CollectionUtils.isEmpty(dtoList)) {
                return R.ok(ImportResultData.buildFailData(ImportMessage.build("导入数据不能为空")));
            }
            // SKU导入上下文
            DelOutboundSkuImportContext importContext = new DelOutboundSkuImportContext(dtoList, warehouseCode, sellerCode);
            // 初始化SKU数据验证器
            DelOutboundDetailImportValidationData importValidationData = new DelOutboundDetailImportValidationData(sellerCode, this.inventoryFeignClientService);
            // 初始化导入验证容器
            ImportResultData<DelOutboundDetailImportDto> importResult = new ImportValidationContainer<>(importContext, ImportValidation.build(new DelOutboundSkuImportValidation(importContext, importValidationData))).validData();
            // 验证SKU导入验证结果
            if (!importResult.isStatus()) {
                return R.ok(importResult);
            }
            // 获取导入的数据
            List<DelOutboundDetailVO> voList = new DelOutboundSkuImportContainer(warehouseCode, dtoList, importValidationData).get();
            // 返回成功的结果
            return R.ok(ImportResultData.buildSuccessData(voList));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.failed("文件解析异常");
        }
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:delOutboundImportTemplate')")
    @GetMapping("/delOutboundImportTemplate")
    @ApiOperation(value = "出库管理 - 列表 - 出库单导入模板", position = 1000)
    public void delOutboundImportTemplate(HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isNotEmpty(request.getParameter("len"))){
            if (request.getParameter("len").equals("zh")) {
                String filePath = "/template/DM-cn.xls";
                String fileName = "DM出库（正常，自提，销毁）模板";
                this.downloadTemplate(response, filePath, fileName);
            }else{
                String filePath = "/template/DM-en.xls";
                String fileName = "DM delivery (normal, self delivery, destruction) template";
                this.downloadTemplate(response, filePath, fileName);
            }

        }else{
            String filePath = "/template/DM.xls";
            String fileName = "DM出库（正常，自提，销毁）模板";
            this.downloadTemplate(response, filePath, fileName);
        }



    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:boxLabelImportTemplate')")
    @GetMapping("/boxLabelImportTemplate")
    @ApiOperation(value = "出库管理 - 列表 - 一件代发、转运出库箱标导入模板", position = 1000)
    public void boxLabelImportTemplate(HttpServletRequest request, HttpServletResponse response) {
        String filePath = "/template/boxLabelImportTemplate.xls";
        String fileName = "BoxLabel";
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
        this.downloadTemplate(response, filePath, fileName, "xls");
    }

    /**
     * 下载模板
     *
     * @param response response
     * @param filePath 文件存放路径，${server.tomcat.basedir}配置的目录和resources目录下
     * @param fileName 文件名称
     * @param ext      扩展名
     */
    private void downloadTemplate(HttpServletResponse response, String filePath, String fileName, String ext) {
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
            String efn = URLEncoder.encode(fileName, "utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + efn + "." + ext);
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("400", "文件不存在，" + e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("500", "文件流处理失败，" + e.getMessage());
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:importBoxLabel')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("importBoxLabel")
    @ApiOperation(value = "导入箱标", notes = "导入箱标")
    public R importBoxLabel(MultipartFile file, @RequestParam("sellerCode") String sellerCode, @RequestParam("attachmentType")  String attachmentType) throws Exception {
        List<DelOutboundBoxLabelDto> userList = EasyExcel.read(file.getInputStream(), DelOutboundBoxLabelDto.class, new SyncReadListener()).sheet().doReadSync();
        if (CollectionUtils.isEmpty(userList)) {
            throw new BaseException("导入内容为空");
        }
        delOutboundService.importBoxLabel(userList, sellerCode, attachmentType);
        return R.ok();
    }



    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:delOutboundImport')")
    @PostMapping("/delOutboundImport")
    @ApiOperation(value = "出库管理 - 列表 - 出库单导入", position = 1100)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "sellerCode", value = "客户编码", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<ImportResult> delOutboundImport(String len, @RequestParam("sellerCode") String sellerCode, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        AssertUtil.isTrue(StringUtils.isNotEmpty(sellerCode), "客户编码不能为空");
        try {
            // copy文件流
            byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
            // 初始化读取第一个sheet页的数据


            List<DelOutboundImportDto> dataList = null;
            List<DelOutboundDetailImportDto2> detailList = null;
            if("en".equals(len)){
                DefaultAnalysisEventListener<DelOutboundEnImportDto> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundEnImportDto.class, 0, 1);
                if (defaultAnalysisEventListener.isError()) {
                    return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
                }
                List<DelOutboundEnImportDto> oldDataList = defaultAnalysisEventListener.getList();
                if (CollectionUtils.isEmpty(oldDataList)) {
                    return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据不能为空")));
                }
                dataList = new ArrayList();
                for(int i = 0 ; i < oldDataList.size(); i++){
                    DelOutboundEnImportDto enDto = oldDataList.get(i);
                    DelOutboundImportDto dto  =new DelOutboundImportDto();
                    BeanUtils.copyProperties(enDto, dto);
                    dataList.add(dto);
                    oldDataList.set(i, null);
                }

                // 初始化读取第二个sheet页的数据
                DefaultAnalysisEventListener<DelOutboundDetailEnImportDto2> defaultAnalysisEventListener1 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundDetailEnImportDto2.class, 1, 1);
                if (defaultAnalysisEventListener1.isError()) {
                    return R.ok(ImportResult.buildFail(defaultAnalysisEventListener1.getMessageList()));
                }
                List<DelOutboundDetailEnImportDto2> oldDetailList = defaultAnalysisEventListener1.getList();


                detailList = new ArrayList<>();
                for(int i = 0 ; i < oldDetailList.size(); i++){
                    DelOutboundDetailEnImportDto2 enDto = oldDetailList.get(i);
                    DelOutboundDetailImportDto2 dto  =new DelOutboundDetailImportDto2();
                    BeanUtils.copyProperties(enDto, dto);
                    detailList.add(dto);
                    oldDetailList.set(i, null);
                }

            }else{
                DefaultAnalysisEventListener<DelOutboundImportDto> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundImportDto.class, 0, 1);
                if (defaultAnalysisEventListener.isError()) {
                    return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
                }
                dataList = defaultAnalysisEventListener.getList();
                if (CollectionUtils.isEmpty(dataList)) {
                    return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据不能为空")));
                }

                // 初始化读取第二个sheet页的数据
                DefaultAnalysisEventListener<DelOutboundDetailImportDto2> defaultAnalysisEventListener1 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundDetailImportDto2.class, 1, 1);
                if (defaultAnalysisEventListener1.isError()) {
                    return R.ok(ImportResult.buildFail(defaultAnalysisEventListener1.getMessageList()));
                }
                detailList = defaultAnalysisEventListener1.getList();
            }


            // 查询出库类型数据
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("063,058");
            List<BasSubWrapperVO> orderTypeList = listMap.get("063");
            List<BasSubWrapperVO> deliveryMethodList = listMap.get("058");
            // 查询国家数据
            R<List<BasRegionSelectListVO>> countryListR = this.basRegionFeignService.countryList(new BasRegionSelectListQueryDto());
            List<BasRegionSelectListVO> countryList = R.getDataAndException(countryListR);
            if("en".equals(len)){
                //英文环境下，nameEn做为key
                for (BasRegionSelectListVO vo: countryList) {
                    vo.setName(vo.getEnName());
                }
            }

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

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:handler')")
    @PostMapping("/handler")
    @ApiOperation(value = "出库管理 - 处理", position = 1200)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundHandlerDto")
    public R<Integer> handler(@RequestBody @Validated DelOutboundHandlerDto dto) {
        return R.ok(this.delOutboundService.handler(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:furtherHandler')")
    @PostMapping("/furtherHandler")
    @ApiOperation(value = "出库管理 - 继续处理", position = 1210)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundFurtherHandlerDto")
    public R<Integer> furtherHandler(@RequestBody @Validated DelOutboundFurtherHandlerDto dto) {
        return R.ok(this.delOutboundService.furtherHandler(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:label')")
    @PostMapping("/label")
    @ApiOperation(value = "出库管理 - 获取标签", position = 1300)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundLabelDto")
    public void label(HttpServletResponse response, @RequestBody @Validated DelOutboundLabelDto dto) {
        this.delOutboundService.label(response, dto);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:labelBase64')")
    @PostMapping("/labelBase64")
    @ApiOperation(value = "出库管理 - 获取标签（根据订单号批量查询，DOC支持）", position = 1301)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundLabelDto")
    public R<List<DelOutboundLabelResponse>> labelBase64(@RequestBody @Validated DelOutboundLabelDto dto) {
        List<DelOutboundLabelResponse> data = this.delOutboundService.labelBase64(dto);
        if (CollectionUtils.isNotEmpty(data)) {
            List<Long> ids = new ArrayList<>();
            for (DelOutboundLabelResponse response : data) {
                if (null != response.getStatus() && response.getStatus()) {
                    ids.add(response.getId());
                }
            }
            if (CollectionUtils.isNotEmpty(ids)) {
                DelOutboundToPrintDto toPrintDto = new DelOutboundToPrintDto();
                toPrintDto.setBatch(true);
                toPrintDto.setIds(ids);
                this.delOutboundService.toPrint(toPrintDto);
            }
        }
        return R.ok(data);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:uploadBoxLabel')")
    @PostMapping("/uploadBoxLabel")
    @ApiOperation(value = "出库管理 - 上传箱标", position = 1400)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundUploadBoxLabelDto")
    public R<Integer> uploadBoxLabel(@RequestBody @Validated DelOutboundUploadBoxLabelDto dto) {
        return R.ok(this.delOutboundService.uploadBoxLabel(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/getDelOutboundDetailsList")
    @ApiOperation(value = "出库管理 - 按条件查询出库单及详情", position = 1500)
    public R<List<DelOutboundDetailListVO>> getDelOutboundDetailsList(@RequestBody DelOutboundListQueryDto queryDto) {
        return R.ok(delOutboundService.getDelOutboundDetailsList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:export')")
    @Log(title = "出库管理 - 导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "出库管理 - 导出", position = 1600)
    public void export(HttpServletResponse response, @RequestBody DelOutboundListQueryDto queryDto) {
        try {

            String len = getLen();

            // 查询出库类型数据
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("063,065,066,099,059");
            DelOutboundExportContext exportContext = new DelOutboundExportContext(this.basWarehouseClientService, this.basRegionFeignService, len);
            exportContext.setStateCacheAdapter(listMap.get("065"));
            exportContext.setOrderTypeCacheAdapter(listMap.get("063"));
            exportContext.setExceptionStateCacheAdapter(listMap.get("066"));
            exportContext.setTrackingStatusCache(listMap.get("099"));

            QueryDto queryDto1 = new QueryDto();
            queryDto1.setPageNum(1);
            queryDto1.setPageSize(500);
            QueryPage<DelOutboundExportListVO> queryPage = new DelOutboundExportQueryPage(queryDto, queryDto1, exportContext, this.delOutboundService);
            QueryDto queryDto2 = new QueryDto();
            queryDto2.setPageNum(1);
            queryDto2.setPageSize(500);
            QueryPage<DelOutboundExportItemListVO> itemQueryPage = new DelOutboundExportItemQueryPage(queryDto, queryDto2, this.delOutboundDetailService, this.baseProductClientService, listMap.get("059"));


            ExcelUtils.export(response, null, ExcelUtils.ExportExcel.build("en".equals(len) ? "Outbound_order" : "出库单", len,  null, new ExcelUtils.ExportSheet<DelOutboundExportListVO>() {
                        @Override
                        public String sheetName() {

                            if("en".equals(len)){
                                return "Outbound Order Information";
                            }else{
                                return "出库单详情";
                            }
                        }

                        @Override
                        public Class<DelOutboundExportListVO> classType() {
                            return DelOutboundExportListVO.class;
                        }

                        @Override
                        public QueryPage<DelOutboundExportListVO> query(ExcelUtils.ExportContext exportContext) {
                            return queryPage;
                        }
                    },
                    new ExcelUtils.ExportSheet<DelOutboundExportItemListVO>() {
                        @Override
                        public String sheetName() {
                            if("en".equals(len)){
                                return "SKU list";
                            }else{
                                return "包裹明细";
                            }
                        }

                        @Override
                        public Class<DelOutboundExportItemListVO> classType() {
                            return DelOutboundExportItemListVO.class;
                        }

                        @Override
                        public QueryPage<DelOutboundExportItemListVO> query(ExcelUtils.ExportContext exportContext) {
                            return itemQueryPage;
                        }
                    }));
        } catch (Exception e) {
            log.error("导出异常:" + e.getMessage(), e);
        }
    }

    @AutoValue
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:delOutboundCharge')")
    @PostMapping("/delOutboundCharge/page")
    @ApiOperation(value = "出库管理 - 按条件查询出库单及费用详情", position = 1700)
    public R<TableDataInfo<QueryChargeVO>> getDelOutboundCharge(@RequestBody QueryChargeDto queryDto) {
        QueryDto page = new QueryDto();
        page.setPageNum(queryDto.getPageNum());
        page.setPageSize(queryDto.getPageSize());
        startPage(page);
        return R.ok(getDataTable(delOutboundService.getDelOutboundCharge(queryDto)));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:toPrint')")
    @PutMapping("/toPrint")
    @ApiOperation(value = "出库管理 - 打印", position = 1800)
    @ApiImplicitParam(name = "dto", value = "参数", dataType = "DelOutboundToPrintDto")
    public R<Boolean> toPrint(@RequestBody DelOutboundToPrintDto dto) {
        return R.ok(delOutboundService.toPrint(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:batchUpdateTrackingNoTemplate')")
    @GetMapping("/batchUpdateTrackingNoTemplate")
    @ApiOperation(value = "出库管理 - 列表 - 批量更新挂号模板", position = 1900)
    public void batchUpdateTrackingNoTemplate(HttpServletResponse response) {
        String filePath = "/template/DM_UpdateTracking.xlsx";
        String fileName = "更新挂号";
        this.downloadTemplate(response, filePath, fileName, "xlsx");
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:batchUpdateTrackingNo')")
    @PostMapping("/batchUpdateTrackingNo")
    @ApiOperation(value = "出库管理 - 列表 - 批量更新挂号", position = 1901)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<List<Map<String, Object>>> batchUpdateTrackingNo(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        try {
            DelOutboundBatchUpdateTrackingNoAnalysisEventListener analysisEventListener = new DelOutboundBatchUpdateTrackingNoAnalysisEventListener();
            ExcelReaderBuilder excelReaderBuilder = EasyExcelFactory.read(file.getInputStream(), DelOutboundBatchUpdateTrackingNoDto.class, analysisEventListener);
            ExcelReaderSheetBuilder excelReaderSheetBuilder = excelReaderBuilder.sheet(0);
            excelReaderSheetBuilder.build().setHeadRowNumber(1);
            excelReaderSheetBuilder.doRead();
            List<DelOutboundBatchUpdateTrackingNoDto> list = analysisEventListener.getList();
            return R.ok(this.delOutboundService.batchUpdateTrackingNo(list));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:againTrackingNo')")
    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/againTrackingNo")
    @ApiOperation(value = "出库管理 - 异常列表 - 重新获取挂号", position = 2000)
    @ApiImplicitParam(name = "dto", value = "参数", dataType = "DelOutboundAgainTrackingNoDto")
    public R<Integer> againTrackingNo(@RequestBody @Validated DelOutboundAgainTrackingNoDto dto) {
        return R.ok(this.delOutboundService.againTrackingNo(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:exceptionMessageList')")
    @PostMapping("/exceptionMessageList")
    @ApiOperation(value = "出库管理 - 异常列表 - 获取异常描述", position = 2100)
    @ApiImplicitParam(name = "orderNos", value = "单号", dataType = "String")
    public R<List<DelOutboundListExceptionMessageVO>> exceptionMessageList(@RequestBody List<String> orderNos) {
        return R.ok(this.delOutboundService.exceptionMessageList(orderNos));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:exceptionMessageExportList')")
    @PostMapping("/exceptionMessageExportList")
    @ApiOperation(value = "出库管理 - 异常列表 - 获取异常描述(导出)", position = 2101)
    @ApiImplicitParam(name = "orderNos", value = "单号", dataType = "String")
    public R<List<DelOutboundListExceptionMessageExportVO>> exceptionMessageExportList(@RequestBody List<String> orderNos) {
        return R.ok(this.delOutboundService.exceptionMessageExportList(orderNos));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:reassign')")
    @Log(title = "出库单模块", businessType = BusinessType.INSERT)
    @PostMapping("/reassign")
    @ApiOperation(value = "出库管理 - 重派", position = 2200)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<DelOutboundAddResponse> reassign(@RequestBody DelOutboundDto dto) {
        // 处理重派逻辑
        DelOutboundAddResponse delOutboundAddResponse = delOutboundService.reassign(dto);
        if (null != delOutboundAddResponse
                && null != delOutboundAddResponse.getStatus()
                && delOutboundAddResponse.getStatus()) {
            // 添加提审记录
            this.delOutboundCompletedService.add(delOutboundAddResponse.getOrderNo(), DelOutboundOperationTypeEnum.BRING_VERIFY.getCode());
        }
        return R.ok(delOutboundAddResponse);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:addShopify')")
    @Log(title = "出库单模块", businessType = BusinessType.INSERT)
    @PostMapping("/addShopify")
    @ApiOperation(value = "出库管理 - 创建Shopify出库单", position = 2300)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<DelOutboundAddResponse> addShopify(@RequestBody DelOutboundDto dto) {
        // 新增出库单
        DelOutboundAddResponse data = delOutboundService.insertDelOutboundShopify(dto);
        // 添加提审操作
        if (null != data
                && null != data.getStatus()
                && data.getStatus()) {
            // 添加提审记录
            this.delOutboundCompletedService.add(data.getOrderNo(), DelOutboundOperationTypeEnum.BRING_VERIFY.getCode());
        }
        return R.ok(data);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:updateInStockList')")
    @Log(title = "出库单模块", businessType = BusinessType.INSERT)
    @PostMapping("/updateInStockList")
    @ApiOperation(value = "出库管理 - 修改入库状态", position = 2400)
    @ApiImplicitParam(name = "idList", value = "出库单ID", dataType = "Long")
    public R<Boolean> updateInStockList(@RequestBody List<Long> idList) {
        LambdaUpdateWrapper<DelOutbound> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.set(DelOutbound::getInStock, true);
        lambdaUpdateWrapper.in(DelOutbound::getId, idList);
        this.delOutboundService.update(null, lambdaUpdateWrapper);
        return R.ok(true);
    }
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:receiveLabel')")
    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/receiveLabel")
    @ApiOperation(value = "出库管理 - 接收供应商系统传回的标签", position = 400)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<Integer> receiveLabel(@RequestBody @Validated(ValidationUpdateGroup.class) DelOutboundReceiveLabelDto dto) {
        return R.ok(delOutboundService.receiveLabel(dto));
    }

}
