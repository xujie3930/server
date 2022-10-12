package com.szmsd.delivery.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.Page;
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
import com.szmsd.common.core.utils.*;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.validator.ValidationSaveGroup;
import com.szmsd.common.core.validator.ValidationUpdateGroup;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.domain.*;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.exported.DelOutboundExportContext;
import com.szmsd.delivery.exported.DelOutboundExportItemQueryPage;
import com.szmsd.delivery.exported.DelOutboundExportQueryPage;
import com.szmsd.delivery.imported.*;
import com.szmsd.delivery.mapper.BasFileMapper;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.task.EasyPoiExportTask;
import com.szmsd.delivery.task.PoiExportTask;
import com.szmsd.delivery.util.ExParams;
import com.szmsd.delivery.util.ZipFileUtils;
import com.szmsd.delivery.vo.*;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.QueryFinishListDTO;
import com.szmsd.inventory.domain.vo.QueryFinishListVO;
import io.swagger.annotations.*;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

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

    @Value("${filepaths}")
    private String filepath;
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

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private BasFileMapper basFileMapper;


    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/page")
    @ApiOperation(value = "出库管理 - 分页", position = 100)
    @AutoValue
    public TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryDto queryDto) {
        startPage(queryDto);
        return getDataTable(this.delOutboundService.selectDelOutboundList(queryDto));
    }

    //@PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/pageDelTarck")
    @ApiOperation(value = "出库挂号更新日志 - 分页", position = 100)
    @AutoValue
    public TableDataInfo<DelOutboundTarckOn> pageDelTarck(@RequestBody DelOutboundTarckOn delOutboundTarckOn) {
        startPage(delOutboundTarckOn);
        return getDataTable(this.delOutboundService.selectDelOutboundTarckList(delOutboundTarckOn));
    }

    /**
     * 导出 挂号修改记录
     */
    @Log(title = "挂号修改记录导出", businessType = BusinessType.EXPORT)
    @PostMapping("/exportDelTarck")
    @ApiOperation(value = "挂号修改记录导出",notes = "挂号修改记录导出")
    public void exportDelTarck(HttpServletResponse response,@RequestBody DelOutboundTarckOn delOutboundTarckOn) throws IOException {
        String len=getLen();
        List<DelOutboundTarckOn> list = delOutboundService.selectDelOutboundTarckList(delOutboundTarckOn);
        ExportParams params = new ExportParams();
        List<DelOutboundTarckOnZh> list1=new ArrayList<>();
        List<DelOutboundTarckOnEh> list2=new ArrayList<>();
        String fileName=null;
        Workbook workbook=null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (len.equals("zh")){
            list1= BeanMapperUtil.mapList(list, DelOutboundTarckOnZh.class);
            list1.forEach(x->{
                x.setUpdateTimes(sdf.format(x.getUpdateTime()));
            });
            workbook=  ExcelExportUtil.exportExcel(params, DelOutboundTarckOnZh.class, list1);
            fileName  ="挂号修改记录"+System.currentTimeMillis();

        }else if (len.equals("en")){
            list2= BeanMapperUtil.mapList(list, DelOutboundTarckOnEh.class);
            list2.forEach(x->{
                x.setUpdateTimes(sdf.format(x.getUpdateTime()));
            });
            workbook=  ExcelExportUtil.exportExcel(params, DelOutboundTarckOnEh.class, list2);
            fileName  ="Tracking_No_Edit_Record"+System.currentTimeMillis();
        }







        Sheet sheet= workbook.getSheet("sheet0");

        //获取第一行数据
        Row row2 =sheet.getRow(0);

        for (int i=0;i<4;i++){
            Cell deliveryTimeCell = row2.getCell(i);

            CellStyle styleMain = workbook.createCellStyle();

            styleMain.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            Font font = workbook.createFont();
            //true为加粗，默认为不加粗
            font.setBold(true);
            //设置字体颜色，颜色和上述的颜色对照表是一样的
            font.setColor(IndexedColors.WHITE.getIndex());
            //将字体样式设置到单元格样式中
            styleMain.setFont(font);

            styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleMain.setAlignment(HorizontalAlignment.CENTER);
            styleMain.setVerticalAlignment(VerticalAlignment.CENTER);
//        CellStyle style =  workbook.createCellStyle();
//        style.setFillPattern(HSSFColor.HSSFColorPredefined.valueOf(""));
//        style.setFillForegroundColor(IndexedColors.RED.getIndex());
            deliveryTimeCell.setCellStyle(styleMain);
        }


        try {

            URLEncoder.encode(fileName, "UTF-8");
            //response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");

            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");

            ServletOutputStream outStream = null;
            try {
                outStream = response.getOutputStream();
                workbook.write(outStream);
                outStream.flush();
            } finally {
                outStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }






    @PostMapping("/manualTrackingYee")
    @ApiOperation(value = "手动推TY", position = 100)
    public R ManualTrackingYee(@RequestBody List<String> list) {
        delOutboundService.manualTrackingYee(list);
        return R.ok();
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

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:bringVerifyByOrderNo')")
    @PostMapping("/bringVerifyByOrderNo")
    @ApiOperation(value = "出库管理 - 提审", position = 600)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundBringVerifyNoDto")
    public R<List<DelOutboundBringVerifyVO>> bringVerifyByOrderNo(@RequestBody @Validated DelOutboundBringVerifyNoDto dto) {
        return R.ok(delOutboundBringVerifyService.bringVerifyByOrderNo(dto));
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
        this.downloadTemplate(response, filePath, fileName, "xlsx");
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
                String filePath = "/template/DM.zip";
                String fileName = "DM出库（正常，自提，销毁）模板";
                ZipFileUtils.downloadZip(response, filePath, fileName);
            }else{
                String filePath = "/template/DM-en.zip";
                String fileName = "DM delivery (normal, self delivery, destruction) template";
                ZipFileUtils.downloadZip(response, filePath, fileName);
            }

        }else{
            String filePath = "/template/DM.zip";
            String fileName = "DM出库（正常，自提，销毁）模板";
            ZipFileUtils.downloadZip(response, filePath, fileName);
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
            String orderTypeName = null;
            DefaultAnalysisEventListener<DelOutboundTypeImportDto> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundTypeImportDto.class, 0, 1);
            if (defaultAnalysisEventListener.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
            }
            List<DelOutboundTypeImportDto> tempDataList = defaultAnalysisEventListener.getList();
            if (CollectionUtils.isEmpty(tempDataList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据不能为空")));
            }else{
                orderTypeName = tempDataList.get(0).getOrderTypeName();
            }



            if("自提出库".equals(orderTypeName) || "Pick Up".equals(orderTypeName)){
                DefaultAnalysisEventListener<DelOutboundPickUpImportDto> defaultAnalysisEventListener2 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundPickUpImportDto.class, 0, 1);
                if (defaultAnalysisEventListener2.isError()) {
                    return R.ok(ImportResult.buildFail(defaultAnalysisEventListener2.getMessageList()));
                }

                dataList = defaultAnalysisEventListener2.getList().stream().map(info -> {
                    DelOutboundImportDto teach = new DelOutboundImportDto();
                    BeanUtils.copyProperties(info, teach);
                    return teach;
                }).collect(Collectors.toList());


            }else if("销毁出库".equals(orderTypeName) || "Disposal Order".equals(orderTypeName)){
                DefaultAnalysisEventListener<DelOutboundDisposalImportDto> defaultAnalysisEventListener2 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundDisposalImportDto.class, 0, 1);
                if (defaultAnalysisEventListener2.isError()) {
                    return R.ok(ImportResult.buildFail(defaultAnalysisEventListener2.getMessageList()));
                }
                dataList = defaultAnalysisEventListener2.getList().stream().map(info -> {
                    DelOutboundImportDto teach = new DelOutboundImportDto();
                    BeanUtils.copyProperties(info, teach);
                    return teach;
                }).collect(Collectors.toList());
            }else{
                DefaultAnalysisEventListener<DelOutboundImportDto> defaultAnalysisEventListener2 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundImportDto.class, 0, 1);
                if (defaultAnalysisEventListener2.isError()) {
                    return R.ok(ImportResult.buildFail(defaultAnalysisEventListener2.getMessageList()));
                }
                dataList = defaultAnalysisEventListener2.getList();

            }


            // 初始化读取第二个sheet页的数据
            DefaultAnalysisEventListener<DelOutboundDetailImportDto2> defaultAnalysisEventListener1 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundDetailImportDto2.class, 1, 1);
            if (defaultAnalysisEventListener1.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener1.getMessageList()));
            }
            detailList = defaultAnalysisEventListener1.getList();


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
            // 批量新增
            List<DelOutboundAddResponse> outboundAddResponseList = this.delOutboundService.insertDelOutbounds(dtoList);
            List<ImportMessage> messageList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(outboundAddResponseList)) {
                int index = 1;
                for (DelOutboundAddResponse outboundAddResponse : outboundAddResponseList) {
                    if (!outboundAddResponse.getStatus()) {
                        messageList.add(new ImportMessage(index, 1, "", outboundAddResponse.getMessage()));
                    }
                    index++;
                }
            }
            // 返回成功的结果
            ImportResult importResult2;
            if (CollectionUtils.isNotEmpty(messageList)) {
                importResult2 = ImportResult.buildFail(messageList);
            } else {
                importResult2 = ImportResult.buildSuccess();
            }
            importResult2.setResultList(outboundAddResponseList);
            // 返回成功的结果
            return R.ok(importResult2);
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
    public R label(HttpServletResponse response, @RequestBody @Validated DelOutboundLabelDto dto) {
        return this.delOutboundService.label(response, dto);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:SelfPick')")
    @PostMapping("/labelSelfPick")
    @ApiOperation(value = "出库管理 - 获取自提标签", position = 1300)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundLabelDto")
    public void labelSelfPick(HttpServletResponse response, @RequestBody @Validated DelOutboundLabelDto dto) {
        this.delOutboundService.labelSelfPick(response, dto);
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
    @SneakyThrows
    public void export(HttpServletResponse response, @RequestBody DelOutboundListQueryDto queryDto) {
        try {

            String len = getLen();

            if (Objects.nonNull(SecurityUtils.getLoginUser())) {
                String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
                if (StringUtils.isEmpty(queryDto.getCustomCode())) {
                    queryDto.setCustomCode(cusCode);
                }
            }

            // 查询出库类型数据
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("063,065,066,099,059");
            DelOutboundExportContext exportContext = new DelOutboundExportContext(this.basWarehouseClientService, this.basRegionFeignService, len);
            exportContext.setStateCacheAdapter(listMap.get("065"));
            exportContext.setOrderTypeCacheAdapter(listMap.get("063"));
            exportContext.setExceptionStateCacheAdapter(listMap.get("066"));
            exportContext.setTrackingStatusCache(listMap.get("099"));
            String filepath=this.filepath;
            Integer   DelOutboundExportTotal=delOutboundService.selectDelOutboundCount(queryDto);
//            Integer DelOutboundExportTotal = basFileMapper.selectDelOutboundCount();
            Integer pageSize = 100000;
            if (len.equals("zh")) {
                QueryDto queryDto1 = new QueryDto();

                //QueryList<DelOutboundExportListVO>  queryList = new DelOutboundExportQueryPage(queryDto, exportContext, this.delOutboundService);
                // 单文件总数据量

                // 数据分页==导出excel文件数量
                int pageTotal = DelOutboundExportTotal % pageSize == 0 ? DelOutboundExportTotal / pageSize : DelOutboundExportTotal / pageSize + 1;
                log.info("导出数据总量：{}条, 预计导出文件数量：{}件", DelOutboundExportTotal, pageTotal);
                CountDownLatch countDownLatch = new CountDownLatch(pageTotal);
                long start = System.currentTimeMillis();

                for (int i = 1; i <= pageTotal; i++) {

//                ExParams exParams = new ExParams();
//                exParams.setFileName("包裹异常-"+i);
//                exParams.setSheetName("出库单详情");
//                exParams.setDataNumsOfSheet(2<<15);
                    queryDto1.setPageNum(i);
                    queryDto1.setPageSize(pageSize);
                    QueryPage<DelOutboundExportListVO> queryPage = new DelOutboundExportQueryPage(queryDto, queryDto1, exportContext, this.delOutboundService);
                    QueryPage<DelOutboundExportItemListVO> itemQueryPage = new DelOutboundExportItemQueryPage(queryDto, queryDto1, this.delOutboundDetailService, this.baseProductClientService, listMap.get("059"), len);
                   String fileName="出库单详情-" + System.currentTimeMillis();
                    BasFile basFile=new BasFile();
                    basFile.setState("0");
                    basFile.setFileRoute(filepath);
                    basFile.setCreateBy(SecurityUtils.getUsername());
                    basFile.setFileName(fileName+".xls");
                    basFile.setModularType(0);
                    basFile.setModularNameZh("包裹查询");
                    basFile.setModularNameEn("OutboundOrderInformation");
                    basFileMapper.insertSelective(basFile);

                    EasyPoiExportTask<DelOutboundExportListVO, DelOutboundExportItemListVO> delOutboundExportExTask = new EasyPoiExportTask<DelOutboundExportListVO, DelOutboundExportItemListVO>()
                            .setExportParams(new ExportParams(fileName, "出库单详情(" + ((i - 1) * pageSize) + "-" + (Math.min(i * pageSize, DelOutboundExportTotal)) + ")", ExcelType.XSSF))
                            .setData(queryPage.getPage())
                            .setClazz(DelOutboundExportListVO.class)
                            .setData2(itemQueryPage.getPage())
                            .setClazz2(DelOutboundExportItemListVO.class)
                            .setFilepath(filepath)
                            .setCountDownLatch(countDownLatch);

                    basFile.setState("1");
                    basFileMapper.updateByPrimaryKeySelective(basFile);

                    //threadPoolTaskExecutor.execute(delOutboundExportExTask);
                    new Thread(delOutboundExportExTask,"export-"+i).start();
                }
                countDownLatch.await();
                log.info("所有导出任务完成，总计耗时：{}ms", System.currentTimeMillis() - start);

            }else if (len.equals("en")){
                QueryDto queryDto1 = new QueryDto();
                // 数据分页==导出excel文件数量
                int pageTotal = DelOutboundExportTotal % pageSize == 0 ? DelOutboundExportTotal / pageSize : DelOutboundExportTotal / pageSize + 1;
                log.info("导出数据总量：{}条, 预计导出文件数量：{}件", DelOutboundExportTotal, pageTotal);
                CountDownLatch countDownLatch = new CountDownLatch(pageTotal);
                long start = System.currentTimeMillis();

                for (int i = 1; i <= pageTotal; i++) {
//                ExParams exParams = new ExParams();
//                exParams.setFileName("包裹异常-"+i);
//                exParams.setSheetName("出库单详情");
//                exParams.setDataNumsOfSheet(2<<15);
                    queryDto1.setPageNum(i);
                    queryDto1.setPageSize(pageSize);
                    QueryPage<DelOutboundExportListVO> queryPage = new DelOutboundExportQueryPage(queryDto, queryDto1, exportContext, this.delOutboundService);
                    Page<DelOutboundExportListEnVO> page = new Page<>();
                    if (CollectionUtils.isNotEmpty(queryPage.getPage())) {
                        for (DelOutboundExportListVO dto : queryPage.getPage()) {
                            DelOutboundExportListEnVO vo = BeanMapperUtil.map(dto, DelOutboundExportListEnVO.class);
                            page.add(vo);
                        }
                    }

                    QueryPage<DelOutboundExportItemListVO> itemQueryPage = new DelOutboundExportItemQueryPage(queryDto, queryDto1, this.delOutboundDetailService, this.baseProductClientService, listMap.get("059"), len);
                    Page<DelOutboundExportItemListEnVO> page1 = new Page<>();
                    if (CollectionUtils.isNotEmpty(itemQueryPage.getPage())) {
                        for (DelOutboundExportItemListVO dto : itemQueryPage.getPage()) {
                            DelOutboundExportItemListEnVO vo = BeanMapperUtil.map(dto, DelOutboundExportItemListEnVO.class);
                            page1.add(vo);
                        }
                    }
                    String fileName="OutboundOrderInformation-" + System.currentTimeMillis();

                    BasFile basFile=new BasFile();
                    basFile.setState("1");
                    basFile.setFileRoute(filepath);
                    basFile.setCreateBy(SecurityUtils.getUsername());
                    basFile.setFileName(fileName+".xls");
                    basFile.setModularType(0);
                    basFile.setModularNameZh("包裹查询");
                    basFile.setModularNameEn("OutboundOrderInformation");
                    basFileMapper.insertSelective(basFile);

                    EasyPoiExportTask<DelOutboundExportListEnVO, DelOutboundExportItemListEnVO> delOutboundExportExTask = new EasyPoiExportTask<DelOutboundExportListEnVO, DelOutboundExportItemListEnVO>()
                            .setExportParams(new ExportParams(fileName, "OutboundOrderInformation(" + ((i - 1) * pageSize) + "-" + (Math.min(i * pageSize, DelOutboundExportTotal)) + ")", ExcelType.XSSF))
                            .setData(page)
                            .setClazz(DelOutboundExportListEnVO.class)
                            .setData2(page1)
                            .setClazz2(DelOutboundExportItemListEnVO.class)
                            .setFilepath(filepath)
                            .setCountDownLatch(countDownLatch);

                    basFile.setState("1");
                    basFileMapper.updateByPrimaryKeySelective(basFile);


                    //threadPoolTaskExecutor.execute(delOutboundExportExTask);
                    new Thread(delOutboundExportExTask,"export-"+i).start();                }
                countDownLatch.await();
                log.info("所有导出任务完成，总计耗时：{}ms", System.currentTimeMillis() - start);
            }

//            QueryDto queryDto2 = new QueryDto();
//            queryDto2.setPageNum(1);
//            queryDto2.setPageSize(500);



//            ExcelUtils.export(response, null, ExcelUtils.ExportExcel.build("en".equals(len) ? "Outbound_order" : "出库单", len,  null, new ExcelUtils.ExportSheet<DelOutboundExportListVO>() {
//                        @Override
//                        public String sheetName() {
//
//                            if("en".equals(len)){
//                                return "Outbound Order Information";
//                            }else{
//                                return "出库单详情";
//                            }
//                        }
//
//                        @Override
//                        public Class<DelOutboundExportListVO> classType() {
//                            return DelOutboundExportListVO.class;
//                        }
//
//                        @Override
//                        public QueryPage<DelOutboundExportListVO> query(ExcelUtils.ExportContext exportContext) {
//                            return null;
//                        }
//                    },
//                    new ExcelUtils.ExportSheet<DelOutboundExportItemListVO>() {
//                        @Override
//                        public String sheetName() {
//                            if("en".equals(len)){
//                                return "SKU list";
//                            }else{
//                                return "包裹明细";
//                            }
//                        }
//
//                        @Override
//                        public Class<DelOutboundExportItemListVO> classType() {
//                            return DelOutboundExportItemListVO.class;
//                        }
//
//                        @Override
//                        public QueryPage<DelOutboundExportItemListVO> query(ExcelUtils.ExportContext exportContext) {
////                            return itemQueryPage;
//                            return null;
//                        }
//                    }));
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
            List<Map<String, Object>> list1=this.delOutboundService.batchUpdateTrackingNo(list);
             //传list1做邮件发送功能
            delOutboundService.emailBatchUpdateTrackingNo(list1);
            return R.ok(list1);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }



    /**
     * 导出挂号失败
     */
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:batchTrackingexport')")
    @Log(title = "挂号失败导出", businessType = BusinessType.EXPORT)
    @GetMapping("/batchTrackingexport")
    @ApiOperation(value = "导出挂号失败",notes = "导出挂号失败")
    public void batchTrackingexport(HttpServletResponse response) throws IOException {
        List<DelOutboundTarckError> list = delOutboundService.selectbatchTrackingexport();
        ExportParams params = new ExportParams();




        Workbook workbook = ExcelExportUtil.exportExcel(params, DelOutboundTarckError.class, list);


        Sheet sheet= workbook.getSheet("sheet0");

        //获取第一行数据
        Row row2 =sheet.getRow(0);

        for (int i=0;i<3;i++){
            Cell deliveryTimeCell = row2.getCell(i);

            CellStyle styleMain = workbook.createCellStyle();
             if (i==2){
                 styleMain.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
             }else {
                 styleMain.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());

             }
            Font font = workbook.createFont();
            //true为加粗，默认为不加粗
            font.setBold(true);
            //设置字体颜色，颜色和上述的颜色对照表是一样的
            font.setColor(IndexedColors.WHITE.getIndex());
            //将字体样式设置到单元格样式中
            styleMain.setFont(font);

            styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleMain.setAlignment(HorizontalAlignment.CENTER);
            styleMain.setVerticalAlignment(VerticalAlignment.CENTER);
//        CellStyle style =  workbook.createCellStyle();
//        style.setFillPattern(HSSFColor.HSSFColorPredefined.valueOf(""));
//        style.setFillForegroundColor(IndexedColors.RED.getIndex());
            deliveryTimeCell.setCellStyle(styleMain);
        }



        try {
            String fileName="失败挂号"+System.currentTimeMillis();
            URLEncoder.encode(fileName, "UTF-8");
            //response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");

            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");

            ServletOutputStream outStream = null;
            try {
                outStream = response.getOutputStream();
                workbook.write(outStream);
                outStream.flush();
            } finally {
                outStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:receiveLabel')")
    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/box/status")
    @ApiOperation(value = "出库管理 - 接收供应商系统传回的标签", position = 400)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<Integer> boxStatus(@RequestBody DelOutboundBoxStatusDto dto) {
        return R.ok(delOutboundService.boxStatus(dto));
    }

}
