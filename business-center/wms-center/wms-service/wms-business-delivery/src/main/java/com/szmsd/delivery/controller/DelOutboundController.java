package com.szmsd.delivery.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.Page;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.domain.dto.BasRegionSelectListQueryDto;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasFileFeignService;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BasFile;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.validator.ValidationSaveGroup;
import com.szmsd.common.core.validator.ValidationUpdateGroup;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundTarckError;
import com.szmsd.delivery.domain.DelOutboundTarckOn;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.exported.DelOutboundExportContext;
import com.szmsd.delivery.exported.DelOutboundExportItemQueryPage;
import com.szmsd.delivery.exported.DelOutboundExportQueryPage;
import com.szmsd.delivery.imported.*;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.task.EasyPoiExportTask;
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
 * ????????????
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"????????????"})
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
    private BasFileFeignService basFileFeignService;


    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/page")
    @ApiOperation(value = "???????????? - ??????", position = 100)
    @AutoValue
    public TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryDto queryDto) {
        startPage(queryDto);
        return getDataTable(this.delOutboundService.selectDelOutboundList(queryDto));
    }

    //@PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/pageDelTarck")
    @ApiOperation(value = "???????????????????????? - ??????", position = 100)
    @AutoValue
    public TableDataInfo<DelOutboundTarckOn> pageDelTarck(@RequestBody DelOutboundTarckOn delOutboundTarckOn) {
        startPage(delOutboundTarckOn);
        return getDataTable(this.delOutboundService.selectDelOutboundTarckList(delOutboundTarckOn));
    }

    /**
     * ?????? ??????????????????
     */
    @Log(title = "????????????????????????", businessType = BusinessType.EXPORT)
    @PostMapping("/exportDelTarck")
    @ApiOperation(value = "????????????????????????",notes = "????????????????????????")
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
            fileName  ="??????????????????"+System.currentTimeMillis();

        }else if (len.equals("en")){
            list2= BeanMapperUtil.mapList(list, DelOutboundTarckOnEh.class);
            list2.forEach(x->{
                x.setUpdateTimes(sdf.format(x.getUpdateTime()));
            });
            workbook=  ExcelExportUtil.exportExcel(params, DelOutboundTarckOnEh.class, list2);
            fileName  ="Tracking_No_Edit_Record"+System.currentTimeMillis();
        }







        Sheet sheet= workbook.getSheet("sheet0");

        //?????????????????????
        Row row2 =sheet.getRow(0);

        for (int i=0;i<4;i++){
            Cell deliveryTimeCell = row2.getCell(i);

            CellStyle styleMain = workbook.createCellStyle();

            styleMain.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            Font font = workbook.createFont();
            //true??????????????????????????????
            font.setBold(true);
            //??????????????????????????????????????????????????????????????????
            font.setColor(IndexedColors.WHITE.getIndex());
            //??????????????????????????????????????????
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
    @ApiOperation(value = "?????????TY", position = 100)
    public R ManualTrackingYee(@RequestBody List<String> list) {
        delOutboundService.manualTrackingYee(list);
        return R.ok();
    }


    @PreAuthorize("@ss.hasPermi('inventory:queryFinishList')")
    @PostMapping("/queryFinishList")
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    public TableDataInfo<QueryFinishListVO> queryFinishList(@RequestBody QueryFinishListDTO queryFinishListDTO) {
        startPage(queryFinishListDTO);
        List<QueryFinishListVO> list = delOutboundService.queryFinishList(queryFinishListDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "???????????? - ??????", position = 200)
    @AutoValue
    public R<DelOutboundVO> getInfo(@PathVariable("id") String id) {
        return R.ok(delOutboundService.selectDelOutboundById(id));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:getInfoByOrderNo')")
    @GetMapping(value = "getInfoByOrderNo/{orderNo}")
    @ApiOperation(value = "???????????? - ??????", position = 201)
    @AutoValue
    public R<DelOutboundVO> getInfoByOrderNo(@PathVariable("orderNo") String orderNo) {
        return R.ok(delOutboundService.selectDelOutboundByOrderNo(orderNo));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:getInfoForThirdParty')")
    @PostMapping(value = "getInfoForThirdParty")
    @ApiOperation(value = "???????????? - ?????????????????????????????????", position = 202)
    @AutoValue
    public R<DelOutboundThirdPartyVO> getInfoForThirdParty(@RequestBody DelOutboundVO vo) {
        return delOutboundService.getInfoForThirdParty(vo);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:queryDetailsLabelByNos')")
    @PostMapping(value = "/queryDetailsLabelByNos")
    @ApiOperation(value = "???????????? - ??????SKU??????", position = 203)
    public R<Map<String, String>> queryDetailsLabelByNos(@RequestBody List<String> nos) {
        return R.ok(delOutboundDetailService.queryDetailsLabelByNos(nos));
    }

    /**
     * ??????-??????????????? ??????
     *
     * @param idList
     * @return
     */
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "createPurchaseOrderListByIdList/{idList}")
    @ApiOperation(value = "??????-???????????????  ??????")
    public R<List<DelOutboundDetailVO>> createPurchaseOrderListByIdList(@PathVariable("idList") List<String> idList) {
        return R.ok(delOutboundService.createPurchaseOrderListByIdList(idList));
    }

    /**
     * ??????-????????????????????????????????? ????????????
     * ???????????????????????????????????????
     *
     * @param purchaseNo  ????????????
     * @param orderNoList ???????????????
     * @return
     */
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "purchase/setPurchaseNo/{purchaseNo}/{orderNoList}")
    @ApiOperation(value = "??????-??????????????????????????????????????????")
    public R setPurchaseNo(@PathVariable("purchaseNo") String purchaseNo, @PathVariable("orderNoList") List<String> orderNoList) {
        return R.ok(delOutboundService.setPurchaseNo(purchaseNo, orderNoList));
    }

    /**
     * ??????-???????????????
     *
     * @param idList
     * @return
     */
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "getTransshipmentProductData/{idList}")
    @ApiOperation(value = "??????-?????????????????????????????????")
    public R<List<DelOutboundDetailVO>> getTransshipmentProductData(@PathVariable("idList") List<String> idList) {
        return R.ok(delOutboundService.getTransshipmentProductData(idList));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "getInfoByOrderId/{orderId}")
    @ApiOperation(value = "???????????? - ??????", position = 201)
    public R<DelOutbound> getInfoByOrderId(@PathVariable("orderId") String orderId) {
        return R.ok(delOutboundService.selectDelOutboundByOrderId(orderId));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "/getStatusByOrderNo")
    @ApiOperation(value = "???????????? - ?????????????????????????????????", position = 202)
    public R<DelOutbound> getStatusByOrderNo(@RequestParam("orderNo") String orderNo) {
        return R.ok(delOutboundService.getByOrderNo(orderNo));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:add')")
    @Log(title = "???????????????", businessType = BusinessType.INSERT)
    @PostMapping("/shipment")
    @ApiOperation(value = "???????????? - ??????", position = 300)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundDto")
    public R<DelOutboundAddResponse> add(@RequestBody @Validated({ValidationSaveGroup.class}) DelOutboundDto dto) {
        DelOutboundAddResponse data = delOutboundService.insertDelOutbound(dto);
        return R.ok(data);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:addPackageCollection')")
    @Log(title = "???????????????", businessType = BusinessType.INSERT)
    @PostMapping("/shipment-package-collection")
    @ApiOperation(value = "???????????? - ???????????????????????????", position = 310)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundDto")
    public R<DelOutboundAddResponse> addPackageCollection(@RequestBody @Validated({ValidationSaveGroup.class}) DelOutboundDto dto) {
        DelOutboundAddResponse data = delOutboundService.insertDelOutbound(dto);
        if (data.getStatus()) {
            this.delOutboundCompletedService.add(data.getOrderNo(), DelOutboundOperationTypeEnum.BRING_VERIFY.getCode());
        }
        return R.ok(data);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:edit')")
    @Log(title = "???????????????", businessType = BusinessType.UPDATE)
    @PutMapping("/shipment")
    @ApiOperation(value = "???????????? - ??????", position = 400)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundDto")
    public R<Integer> edit(@RequestBody @Validated(ValidationUpdateGroup.class) DelOutboundDto dto) {
        return R.ok(delOutboundService.updateDelOutbound(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:updateWeightDelOutbound')")
    @Log(title = "???????????????", businessType = BusinessType.UPDATE)
    @PostMapping("/updateWeightDelOutbound")
    @ApiOperation(value = "???????????? - ??????", position = 400)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundDto")
    public R<Integer> updateWeightDelOutbound(@RequestBody @Validated(ValidationUpdateGroup.class) UpdateWeightDelOutboundDto dto) {

        return R.ok(delOutboundService.updateWeightDelOutbound(dto) ? 1 : 0);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:remove')")
    @Log(title = "???????????????", businessType = BusinessType.DELETE)
    @DeleteMapping("/shipment")
    @ApiOperation(value = "???????????? - ??????", position = 500)
    public R<Integer> remove(@RequestBody List<String> ids) {
        return R.ok(delOutboundService.deleteDelOutboundByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:bringVerify')")
    @PostMapping("/bringVerify")
    @ApiOperation(value = "???????????? - ??????", position = 600)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundBringVerifyDto")
    public R<List<DelOutboundBringVerifyVO>> bringVerify(@RequestBody @Validated DelOutboundBringVerifyDto dto) {
        return R.ok(delOutboundBringVerifyService.bringVerify(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:bringVerifyByOrderNo')")
    @PostMapping("/bringVerifyByOrderNo")
    @ApiOperation(value = "???????????? - ??????", position = 600)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundBringVerifyNoDto")
    public R<List<DelOutboundBringVerifyVO>> bringVerifyByOrderNo(@RequestBody @Validated DelOutboundBringVerifyNoDto dto) {
        return R.ok(delOutboundBringVerifyService.bringVerifyByOrderNo(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:canceled')")
    @PostMapping("/canceled")
    @ApiOperation(value = "???????????? - ??????", position = 700)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundCanceledDto")
    public R<Integer> canceled(@RequestBody @Validated DelOutboundCanceledDto dto) {
        return R.ok(this.delOutboundService.canceled(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:exportTemplate')")
    @GetMapping("/exportTemplate")
    @ApiOperation(value = "???????????? - ?????? - SKU????????????", position = 800)
    public void exportTemplate(HttpServletResponse response) {
        String filePath = "/template/Del_sku_import.xlsx";
        String fileName = "?????????SKU??????";
        this.downloadTemplate(response, filePath, fileName, "xlsx");
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:importdetail')")
    @PostMapping("/importDetail")
    @ApiOperation(value = "???????????? - ?????? - SKU??????", position = 900)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "warehouseCode", value = "????????????", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "sellerCode", value = "????????????", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "????????????", required = true, allowMultiple = true)
    })
    public R<ImportResultData<?>> importDetail(@RequestParam("warehouseCode") String warehouseCode, @RequestParam("sellerCode") String sellerCode, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "?????????????????????");
        AssertUtil.isTrue(StringUtils.isNotEmpty(warehouseCode), "????????????????????????");
        AssertUtil.isTrue(StringUtils.isNotEmpty(sellerCode), "????????????????????????");
        String originalFilename = file.getOriginalFilename();
        AssertUtil.notNull(originalFilename, "???????????????????????????");
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf + 1);
        boolean isXlsx = "xlsx".equals(suffix);
        AssertUtil.isTrue(isXlsx, "?????????xlsx??????");
        try {
            ExcelReaderSheetBuilder excelReaderSheetBuilder = EasyExcelFactory.read(file.getInputStream(), DelOutboundDetailImportDto.class, null).sheet(0);
            List<DelOutboundDetailImportDto> dtoList = excelReaderSheetBuilder.doReadSync();
            if (CollectionUtils.isEmpty(dtoList)) {
                return R.ok(ImportResultData.buildFailData(ImportMessage.build("????????????????????????")));
            }
            // SKU???????????????
            DelOutboundSkuImportContext importContext = new DelOutboundSkuImportContext(dtoList, warehouseCode, sellerCode);
            // ?????????SKU???????????????
            DelOutboundDetailImportValidationData importValidationData = new DelOutboundDetailImportValidationData(sellerCode, this.inventoryFeignClientService);
            // ???????????????????????????
            ImportResultData<DelOutboundDetailImportDto> importResult = new ImportValidationContainer<>(importContext, ImportValidation.build(new DelOutboundSkuImportValidation(importContext, importValidationData))).validData();
            // ??????SKU??????????????????
            if (!importResult.isStatus()) {
                return R.ok(importResult);
            }
            // ?????????????????????
            List<DelOutboundDetailVO> voList = new DelOutboundSkuImportContainer(warehouseCode, dtoList, importValidationData).get();
            // ?????????????????????
            return R.ok(ImportResultData.buildSuccessData(voList));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.failed("??????????????????");
        }
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:delOutboundImportTemplate')")
    @GetMapping("/delOutboundImportTemplate")
    @ApiOperation(value = "???????????? - ?????? - ?????????????????????", position = 1000)
    public void delOutboundImportTemplate(HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isNotEmpty(request.getParameter("len"))){
            if (request.getParameter("len").equals("zh")) {
                String filePath = "/template/DM.zip";
                String fileName = "DM??????????????????????????????????????????";
                ZipFileUtils.downloadZip(response, filePath, fileName);
            }else{
                String filePath = "/template/DM-en.zip";
                String fileName = "DM delivery (normal, self delivery, destruction) template";
                ZipFileUtils.downloadZip(response, filePath, fileName);
            }

        }else{
            String filePath = "/template/DM.zip";
            String fileName = "DM??????????????????????????????????????????";
            ZipFileUtils.downloadZip(response, filePath, fileName);
        }



    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:boxLabelImportTemplate')")
    @GetMapping("/boxLabelImportTemplate")
    @ApiOperation(value = "???????????? - ?????? - ?????????????????????????????????????????????", position = 1000)
    public void boxLabelImportTemplate(HttpServletRequest request, HttpServletResponse response) {
        String filePath = "/template/boxLabelImportTemplate.xls";
        String fileName = "BoxLabel";
        this.downloadTemplate(response, filePath, fileName);

    }

    /**
     * ????????????
     *
     * @param response response
     * @param filePath ?????????????????????${server.tomcat.basedir}??????????????????resources?????????
     * @param fileName ????????????
     */
    private void downloadTemplate(HttpServletResponse response, String filePath, String fileName) {
        this.downloadTemplate(response, filePath, fileName, "xls");
    }

    /**
     * ????????????
     *
     * @param response response
     * @param filePath ?????????????????????${server.tomcat.basedir}??????????????????resources?????????
     * @param fileName ????????????
     * @param ext      ?????????
     */
    private void downloadTemplate(HttpServletResponse response, String filePath, String fileName, String ext) {
        // ?????????????????????????????????
        // ??????????????????????????????????????????????????????
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
            //response???HttpServletResponse??????
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //Loading plan.xls??????????????????????????????????????????????????????????????????????????????
            String efn = URLEncoder.encode(fileName, "utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + efn + "." + ext);
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("400", "??????????????????" + e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("500", "????????????????????????" + e.getMessage());
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:importBoxLabel')")
    @Log(title = "??????", businessType = BusinessType.INSERT)
    @PostMapping("importBoxLabel")
    @ApiOperation(value = "????????????", notes = "????????????")
    public R importBoxLabel(MultipartFile file, @RequestParam("sellerCode") String sellerCode, @RequestParam("attachmentType")  String attachmentType) throws Exception {
        List<DelOutboundBoxLabelDto> userList = EasyExcel.read(file.getInputStream(), DelOutboundBoxLabelDto.class, new SyncReadListener()).sheet().doReadSync();
        if (CollectionUtils.isEmpty(userList)) {
            throw new BaseException("??????????????????");
        }
        delOutboundService.importBoxLabel(userList, sellerCode, attachmentType);
        return R.ok();
    }



    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:delOutboundImport')")
    @PostMapping("/delOutboundImport")
    @ApiOperation(value = "???????????? - ?????? - ???????????????", position = 1100)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "sellerCode", value = "????????????", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "????????????", required = true, allowMultiple = true)
    })
    public R<ImportResult> delOutboundImport(String len, @RequestParam("sellerCode") String sellerCode, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "?????????????????????");
        AssertUtil.isTrue(StringUtils.isNotEmpty(sellerCode), "????????????????????????");
        try {
            // copy?????????
            byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
            // ????????????????????????sheet????????????



            List<DelOutboundImportDto> dataList = null;
            List<DelOutboundDetailImportDto2> detailList = null;
            String orderTypeName = null;
            DefaultAnalysisEventListener<DelOutboundTypeImportDto> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundTypeImportDto.class, 0, 1);
            if (defaultAnalysisEventListener.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
            }
            List<DelOutboundTypeImportDto> tempDataList = defaultAnalysisEventListener.getList();
            if (CollectionUtils.isEmpty(tempDataList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("????????????????????????")));
            }else{
                orderTypeName = tempDataList.get(0).getOrderTypeName();
            }



            if("????????????".equals(orderTypeName) || "Pick Up".equals(orderTypeName)){
                DefaultAnalysisEventListener<DelOutboundPickUpImportDto> defaultAnalysisEventListener2 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundPickUpImportDto.class, 0, 1);
                if (defaultAnalysisEventListener2.isError()) {
                    return R.ok(ImportResult.buildFail(defaultAnalysisEventListener2.getMessageList()));
                }

                dataList = defaultAnalysisEventListener2.getList().stream().map(info -> {
                    DelOutboundImportDto teach = new DelOutboundImportDto();
                    BeanUtils.copyProperties(info, teach);
                    return teach;
                }).collect(Collectors.toList());


            }else if("????????????".equals(orderTypeName) || "Disposal Order".equals(orderTypeName)){
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


            // ????????????????????????sheet????????????
            DefaultAnalysisEventListener<DelOutboundDetailImportDto2> defaultAnalysisEventListener1 = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundDetailImportDto2.class, 1, 1);
            if (defaultAnalysisEventListener1.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener1.getMessageList()));
            }
            detailList = defaultAnalysisEventListener1.getList();


            // ????????????????????????
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("063,058");
            List<BasSubWrapperVO> orderTypeList = listMap.get("063");
            List<BasSubWrapperVO> deliveryMethodList = listMap.get("058");
            // ??????????????????
            R<List<BasRegionSelectListVO>> countryListR = this.basRegionFeignService.countryList(new BasRegionSelectListQueryDto());
            List<BasRegionSelectListVO> countryList = R.getDataAndException(countryListR);
            if("en".equals(len)){
                //??????????????????nameEn??????key
                for (BasRegionSelectListVO vo: countryList) {
                    vo.setName(vo.getEnName());
                }
            }

            // ????????????????????????
            DelOutboundImportContext importContext = new DelOutboundImportContext(dataList, orderTypeList, countryList, deliveryMethodList);
            // ??????????????????????????????
            DelOutboundOuterContext outerContext = new DelOutboundOuterContext();
            // ???????????????????????????
            ImportResult importResult = new ImportValidationContainer<>(importContext, ImportValidation.build(new DelOutboundImportValidation(outerContext, importContext))).valid();
            // ????????????????????????
            if (!importResult.isStatus()) {
                return R.ok(importResult);
            }
            // ?????????SKU???????????????
            DelOutboundDetailImportContext importContext1 = new DelOutboundDetailImportContext(detailList);
            // ?????????SKU???????????????
            DelOutboundDetailImportValidationData importValidationData = new DelOutboundDetailImportValidationData(sellerCode, inventoryFeignClientService);
            // ?????????SKU??????????????????
            ImportResult importResult1 = new ImportValidationContainer<>(importContext1, ImportValidation.build(new DelOutboundDetailImportValidation(outerContext, importContext1, importValidationData))).valid();
            // ??????SKU??????????????????
            if (!importResult1.isStatus()) {
                return R.ok(importResult1);
            }
            List<String> detailSkuList = new ArrayList<>();
            for(DelOutboundDetailImportDto2 detailImportDto2: detailList){
                if(StringUtils.isNotEmpty(detailImportDto2.getSku())){
                    detailSkuList.add(detailImportDto2.getSku());
                }
            }
            Map<String, BaseProduct> productMap = new HashMap<>();
            if(!detailSkuList.isEmpty()){
                BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
                conditionQueryDto.setSkus(detailSkuList);
                List<BaseProduct> productList = this.baseProductClientService.queryProductList(conditionQueryDto);
                if (CollectionUtils.isNotEmpty(productList)) {
                    productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, v -> v, (v, v2) -> v));
                }
            }

            // ?????????????????????
            List<DelOutboundDto> dtoList = new DelOutboundImportContainer(dataList, orderTypeList, countryList, deliveryMethodList, detailList, importValidationData, sellerCode, productMap).get();
            // ????????????
            // ????????????
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
            // ?????????????????????
            ImportResult importResult2;
            if (CollectionUtils.isNotEmpty(messageList)) {
                importResult2 = ImportResult.buildFail(messageList);
            } else {
                importResult2 = ImportResult.buildSuccess();
            }
            importResult2.setResultList(outboundAddResponseList);
            // ?????????????????????
            return R.ok(importResult2);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            // ?????????????????????
            return R.ok(ImportResult.buildFail(ImportMessage.build(e.getMessage())));
        }
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:handler')")
    @PostMapping("/handler")
    @ApiOperation(value = "???????????? - ??????", position = 1200)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundHandlerDto")
    public R<Integer> handler(@RequestBody @Validated DelOutboundHandlerDto dto) {
        return R.ok(this.delOutboundService.handler(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:handler')")
    @PostMapping("/querySpecialGoods")
    @ApiOperation(value = "???????????? - ??????", position = 1200)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundHandlerDto")
    public R<List<DelOutboundSpecialDto>> querySpecialGoods(@RequestBody List<String> orders) {

        return R.ok(this.delOutboundService.querySpecialGoods(orders));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:furtherHandler')")
    @PostMapping("/furtherHandler")
    @ApiOperation(value = "???????????? - ????????????", position = 1210)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundFurtherHandlerDto")
    public R<Integer> furtherHandler(@RequestBody @Validated DelOutboundFurtherHandlerDto dto) {
        return R.ok(this.delOutboundService.furtherHandler(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:label')")
    @PostMapping("/label")
    @ApiOperation(value = "???????????? - ????????????", position = 1300)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundLabelDto")
    public R label(HttpServletResponse response, @RequestBody @Validated DelOutboundLabelDto dto) {
        return this.delOutboundService.label(response, dto);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:label')")
    @PostMapping("/label-batch")
    @ApiOperation(value = "???????????? - ??????????????????", position = 1300)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundLabelDto")
    public R labelBatch(HttpServletResponse response, @RequestBody @Validated DelOutboundLabelDto dto) {
        return this.delOutboundService.labelBatch(response, dto);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:SelfPick')")
    @PostMapping("/labelSelfPick")
    @ApiOperation(value = "???????????? - ??????????????????", position = 1300)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundLabelDto")
    public void labelSelfPick(HttpServletResponse response, @RequestBody @Validated DelOutboundLabelDto dto) {
        this.delOutboundService.labelSelfPick(response, dto);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:labelBase64')")
    @PostMapping("/labelBase64")
    @ApiOperation(value = "???????????? - ?????????????????????????????????????????????DOC?????????", position = 1301)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundLabelDto")
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
    @ApiOperation(value = "???????????? - ????????????", position = 1400)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundUploadBoxLabelDto")
    public R<Integer> uploadBoxLabel(@RequestBody @Validated DelOutboundUploadBoxLabelDto dto) {
        return R.ok(this.delOutboundService.uploadBoxLabel(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/getDelOutboundDetailsList")
    @ApiOperation(value = "???????????? - ?????????????????????????????????", position = 1500)
    public R<List<DelOutboundDetailListVO>> getDelOutboundDetailsList(@RequestBody DelOutboundListQueryDto queryDto) {
        return R.ok(delOutboundService.getDelOutboundDetailsList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:export')")
    @Log(title = "???????????? - ??????", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "???????????? - ??????", position = 1600)
    @SneakyThrows
    public void export(HttpServletResponse response, @RequestBody DelOutboundListQueryDto queryDto) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String len = getLen();

            if (Objects.nonNull(SecurityUtils.getLoginUser())) {
                String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
                if (StringUtils.isEmpty(queryDto.getCustomCode())) {
                    queryDto.setCustomCode(cusCode);
                }
            }


            Integer   DelOutboundExportTotal=delOutboundService.selectDelOutboundCount(queryDto);
            if (DelOutboundExportTotal>2000) {
                // ????????????????????????
                Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("063,065,066,099,059");
                DelOutboundExportContext exportContext = new DelOutboundExportContext(this.basWarehouseClientService, this.basRegionFeignService, len);
                exportContext.setStateCacheAdapter(listMap.get("065"));
                exportContext.setOrderTypeCacheAdapter(listMap.get("063"));
                exportContext.setExceptionStateCacheAdapter(listMap.get("066"));
                exportContext.setTrackingStatusCache(listMap.get("099"));
                String filepath=this.filepath;
//            Integer DelOutboundExportTotal = basFileMapper.selectDelOutboundCount();
                Integer pageSize = 100000;
                if (len.equals("zh")) {
                    QueryDto queryDto1 = new QueryDto();

                    //QueryList<DelOutboundExportListVO>  queryList = new DelOutboundExportQueryPage(queryDto, exportContext, this.delOutboundService);
                    // ?????????????????????

                    // ????????????==??????excel????????????
                    int pageTotal = DelOutboundExportTotal % pageSize == 0 ? DelOutboundExportTotal / pageSize : DelOutboundExportTotal / pageSize + 1;
                    log.info("?????????????????????{}???, ???????????????????????????{}???", DelOutboundExportTotal, pageTotal);
                    CountDownLatch countDownLatch = new CountDownLatch(pageTotal);
                    long start = System.currentTimeMillis();

                    for (int i = 1; i <= pageTotal; i++) {

                        Date date =new Date();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");

//                ExParams exParams = new ExParams();
//                exParams.setFileName("????????????-"+i);
//                exParams.setSheetName("???????????????");
//                exParams.setDataNumsOfSheet(2<<15);
                        queryDto1.setPageNum(i);
                        queryDto1.setPageSize(pageSize);
                        QueryPage<DelOutboundExportListVO> queryPage = new DelOutboundExportQueryPage(queryDto, queryDto1, exportContext, this.delOutboundService);
                        QueryPage<DelOutboundExportItemListVO> itemQueryPage = new DelOutboundExportItemQueryPage(queryDto, queryDto1, this.delOutboundDetailService, this.baseProductClientService, listMap.get("059"), len);
                        String fileName = "???????????????-" +loginUser.getUsername()+"-"+ simpleDateFormat.format(date);
                        BasFile basFile = new BasFile();
                        basFile.setState("0");
                        basFile.setFileRoute(filepath);
                        basFile.setCreateBy(SecurityUtils.getUsername());
                        basFile.setFileName(fileName + ".xls");
                        basFile.setModularType(0);
                        basFile.setModularNameZh("??????????????????");
                        basFile.setModularNameEn("OutboundOrderInformation");
                       R<BasFile> r =basFileFeignService.addbasFile(basFile);
                       BasFile basFile1=r.getData();

                        EasyPoiExportTask<DelOutboundExportListVO, DelOutboundExportItemListVO> delOutboundExportExTask = new EasyPoiExportTask<DelOutboundExportListVO, DelOutboundExportItemListVO>()
                                .setExportParams(new ExportParams(fileName, "???????????????(" + ((i - 1) * pageSize) + "-" + (Math.min(i * pageSize, DelOutboundExportTotal)) + ")", ExcelType.XSSF))
                                .setData(queryPage.getPage())
                                .setClazz(DelOutboundExportListVO.class)
                                .setData2(itemQueryPage.getPage())
                                .setClazz2(DelOutboundExportItemListVO.class)
                                .setFilepath(filepath)
                                .setCountDownLatch(countDownLatch)
                                .setFileId(basFile1.getId());

                        basFile1.setState("1");
                        basFileFeignService.updatebasFile(basFile1);

                        //threadPoolTaskExecutor.execute(delOutboundExportExTask);
                        new Thread(delOutboundExportExTask, "export-" + i).start();
                    }
                    countDownLatch.await();
                    log.info("??????????????????????????????????????????{}ms", System.currentTimeMillis() - start);

                } else if (len.equals("en")) {
                    QueryDto queryDto1 = new QueryDto();
                    // ????????????==??????excel????????????
                    int pageTotal = DelOutboundExportTotal % pageSize == 0 ? DelOutboundExportTotal / pageSize : DelOutboundExportTotal / pageSize + 1;
                    log.info("?????????????????????{}???, ???????????????????????????{}???", DelOutboundExportTotal, pageTotal);
                    CountDownLatch countDownLatch = new CountDownLatch(pageTotal);
                    long start = System.currentTimeMillis();

                    for (int i = 1; i <= pageTotal; i++) {
                        Date date =new Date();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
//                ExParams exParams = new ExParams();
//                exParams.setFileName("????????????-"+i);
//                exParams.setSheetName("???????????????");
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
                        String fileName = "OutboundOrderInformation-" +  loginUser.getUsername()+"-"+ simpleDateFormat.format(date);

                        BasFile basFile = new BasFile();
                        basFile.setState("1");
                        basFile.setFileRoute(filepath);
                        basFile.setCreateBy(SecurityUtils.getUsername());
                        basFile.setFileName(fileName + ".xls");
                        basFile.setModularType(0);
                        basFile.setModularNameZh("??????????????????");
                        basFile.setModularNameEn("OutboundOrderInformation");
                        R<BasFile> r =basFileFeignService.addbasFile(basFile);
                        BasFile basFile1=r.getData();

                        EasyPoiExportTask<DelOutboundExportListEnVO, DelOutboundExportItemListEnVO> delOutboundExportExTask = new EasyPoiExportTask<DelOutboundExportListEnVO, DelOutboundExportItemListEnVO>()
                                .setExportParams(new ExportParams(fileName, "OutboundOrderInformation(" + ((i - 1) * pageSize) + "-" + (Math.min(i * pageSize, DelOutboundExportTotal)) + ")", ExcelType.XSSF))
                                .setData(page)
                                .setClazz(DelOutboundExportListEnVO.class)
                                .setData2(page1)
                                .setClazz2(DelOutboundExportItemListEnVO.class)
                                .setFilepath(filepath)
                                .setCountDownLatch(countDownLatch)
                                .setFileId(basFile1.getId());

                        basFile.setState("1");
                        basFileFeignService.updatebasFile(basFile1);


                        //threadPoolTaskExecutor.execute(delOutboundExportExTask);
                        new Thread(delOutboundExportExTask, "export-" + i).start();
                    }
                    countDownLatch.await();
                    log.info("??????????????????????????????????????????{}ms", System.currentTimeMillis() - start);

                }
            }else if (DelOutboundExportTotal<=2000){
                if (len.equals("zh")){
                    // ????????????????????????
                    Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("063,065,066,099,059");
                    DelOutboundExportContext exportContext = new DelOutboundExportContext(this.basWarehouseClientService, this.basRegionFeignService, len);
                    exportContext.setStateCacheAdapter(listMap.get("065"));
                    exportContext.setOrderTypeCacheAdapter(listMap.get("063"));
                    exportContext.setExceptionStateCacheAdapter(listMap.get("066"));
                    exportContext.setTrackingStatusCache(listMap.get("099"));

                    QueryDto queryDto1 = new QueryDto();
                    queryDto1.setPageNum(1);
                    queryDto1.setPageSize(2000);
                    QueryPage<DelOutboundExportListVO> queryPage = new DelOutboundExportQueryPage(queryDto, queryDto1, exportContext, this.delOutboundService);
                    QueryDto queryDto2 = new QueryDto();
                    queryDto2.setPageNum(1);
                    queryDto2.setPageSize(20000);
                    QueryPage<DelOutboundExportItemListVO> itemQueryPage = new DelOutboundExportItemQueryPage(queryDto, queryDto2, this.delOutboundDetailService, this.baseProductClientService, listMap.get("059"), len);

                    ExportParams params = new ExportParams();
                    // ??????sheet?????????
                    params.setSheetName("???????????????");
                    ExportParams exportParams2 = new ExportParams();
                    exportParams2.setSheetName("????????????");
                    // ??????sheet1?????????map
                    Map<String, Object>  DelOutboundExportMap = new HashMap<>(4);
                    // title????????????ExportParams??????
                    DelOutboundExportMap.put("title", params);
                    // ?????????????????????????????????
                    DelOutboundExportMap.put("entity", DelOutboundExportListVO.class);
                    // sheet?????????????????????
                    DelOutboundExportMap.put("data", queryPage.getPage());
                    // ??????sheet2?????????map
                    Map<String, Object>  DelOutboundExportItemListMap = new HashMap<>(4);
                    DelOutboundExportItemListMap.put("title", exportParams2);
                    DelOutboundExportItemListMap.put("entity", DelOutboundExportItemListVO.class);
                    DelOutboundExportItemListMap.put("data", itemQueryPage.getPage());
                    // ???sheet1???sheet2?????????map????????????
                    List<Map<String, Object>> sheetsList = new ArrayList<>();
                    sheetsList.add(DelOutboundExportMap);
                    sheetsList.add(DelOutboundExportItemListMap);

                    Workbook workbook = ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
                    Sheet sheet= workbook.getSheet("???????????????");

                    //?????????????????????
                    Row row2 =sheet.getRow(0);

                    for (int i=0;i<32;i++){
                        Cell deliveryTimeCell = row2.getCell(i);

                        CellStyle styleMain = workbook.createCellStyle();

                        styleMain.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());


                        Font font = workbook.createFont();
                        //true??????????????????????????????
                        font.setBold(true);
                        //??????????????????????????????????????????????????????????????????
                        font.setColor(IndexedColors.WHITE.getIndex());
                        //??????????????????????????????????????????
                        styleMain.setFont(font);

                        styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        styleMain.setAlignment(HorizontalAlignment.CENTER);
                        styleMain.setVerticalAlignment(VerticalAlignment.CENTER);

                        deliveryTimeCell.setCellStyle(styleMain);
                    }

                    Sheet sheet1= workbook.getSheet("????????????");
                    //?????????????????????
                    Row row3 =sheet1.getRow(0);

                    for (int i=0;i<7;i++){
                        Cell deliveryTimeCell = row3.getCell(i);

                        CellStyle styleMain = workbook.createCellStyle();

                        styleMain.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());


                        Font font = workbook.createFont();
                        //true??????????????????????????????
                        font.setBold(true);
                        //??????????????????????????????????????????????????????????????????
                        font.setColor(IndexedColors.WHITE.getIndex());
                        //??????????????????????????????????????????
                        styleMain.setFont(font);

                        styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        styleMain.setAlignment(HorizontalAlignment.CENTER);
                        styleMain.setVerticalAlignment(VerticalAlignment.CENTER);

                        deliveryTimeCell.setCellStyle(styleMain);
                    }

                    try {
                        String fileName="?????????"+System.currentTimeMillis();
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
                if (len.equals("en")){
                    // ????????????????????????
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
                    Page<DelOutboundExportListEnVO> page = new Page<>();
                    if (CollectionUtils.isNotEmpty(queryPage.getPage())) {
                        for (DelOutboundExportListVO dto : queryPage.getPage()) {
                            DelOutboundExportListEnVO vo = BeanMapperUtil.map(dto, DelOutboundExportListEnVO.class);
                            page.add(vo);
                        }
                    }
                    QueryDto queryDto2 = new QueryDto();
                    queryDto2.setPageNum(1);
                    queryDto2.setPageSize(500);
                    QueryPage<DelOutboundExportItemListVO> itemQueryPage = new DelOutboundExportItemQueryPage(queryDto, queryDto2, this.delOutboundDetailService, this.baseProductClientService, listMap.get("059"), len);
                    Page<DelOutboundExportItemListEnVO> page1 = new Page<>();
                    if (CollectionUtils.isNotEmpty(itemQueryPage.getPage())) {
                        for (DelOutboundExportItemListVO dto : itemQueryPage.getPage()) {
                            DelOutboundExportItemListEnVO vo = BeanMapperUtil.map(dto, DelOutboundExportItemListEnVO.class);
                            page1.add(vo);
                        }
                    }
                    ExportParams params = new ExportParams();
                    // ??????sheet?????????
                    params.setSheetName("???????????????");
                    ExportParams exportParams2 = new ExportParams();
                    exportParams2.setSheetName("????????????");
                    // ??????sheet1?????????map
                    Map<String, Object>  DelOutboundExportMap = new HashMap<>(4);
                    // title????????????ExportParams??????
                    DelOutboundExportMap.put("title", params);
                    // ?????????????????????????????????
                    DelOutboundExportMap.put("entity", DelOutboundExportListEnVO.class);
                    // sheet?????????????????????
                    DelOutboundExportMap.put("data", page);
                    // ??????sheet2?????????map
                    Map<String, Object>  DelOutboundExportItemListMap = new HashMap<>(4);
                    DelOutboundExportItemListMap.put("title", exportParams2);
                    DelOutboundExportItemListMap.put("entity", DelOutboundExportItemListEnVO.class);
                    DelOutboundExportItemListMap.put("data", page1);
                    // ???sheet1???sheet2?????????map????????????
                    List<Map<String, Object>> sheetsList = new ArrayList<>();
                    sheetsList.add(DelOutboundExportMap);
                    sheetsList.add(DelOutboundExportItemListMap);

                    Workbook workbook = ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
                    Sheet sheet= workbook.getSheet("???????????????");

                    //?????????????????????
                    Row row2 =sheet.getRow(0);

                    for (int i=0;i<32;i++){
                        Cell deliveryTimeCell = row2.getCell(i);

                        CellStyle styleMain = workbook.createCellStyle();

                        styleMain.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());


                        Font font = workbook.createFont();
                        //true??????????????????????????????
                        font.setBold(true);
                        //??????????????????????????????????????????????????????????????????
                        font.setColor(IndexedColors.WHITE.getIndex());
                        //??????????????????????????????????????????
                        styleMain.setFont(font);

                        styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        styleMain.setAlignment(HorizontalAlignment.CENTER);
                        styleMain.setVerticalAlignment(VerticalAlignment.CENTER);

                        deliveryTimeCell.setCellStyle(styleMain);
                    }

                    Sheet sheet1= workbook.getSheet("????????????");
                    //?????????????????????
                    Row row3 =sheet1.getRow(0);

                    for (int i=0;i<7;i++){
                        Cell deliveryTimeCell = row3.getCell(i);

                        CellStyle styleMain = workbook.createCellStyle();

                        styleMain.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());


                        Font font = workbook.createFont();
                        //true??????????????????????????????
                        font.setBold(true);
                        //??????????????????????????????????????????????????????????????????
                        font.setColor(IndexedColors.WHITE.getIndex());
                        //??????????????????????????????????????????
                        styleMain.setFont(font);

                        styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        styleMain.setAlignment(HorizontalAlignment.CENTER);
                        styleMain.setVerticalAlignment(VerticalAlignment.CENTER);

                        deliveryTimeCell.setCellStyle(styleMain);
                    }

                    try {
                        String fileName="Outbound Order Information"+System.currentTimeMillis();
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


//                ExcelUtils.export(response, null, ExcelUtils.ExportExcel.build("en".equals(len) ? "Outbound_order" : "?????????", len,  null, new ExcelUtils.ExportSheet<DelOutboundExportListVO>() {
//                            @Override
//                            public String sheetName() {
//
//                                if("en".equals(len)){
//                                    return "Outbound Order Information";
//                                }else{
//                                    return "???????????????";
//                                }
//                            }
//
//                            @Override
//                            public Class<DelOutboundExportListVO> classType() {
//                                return DelOutboundExportListVO.class;
//                            }
//
//                            @Override
//                            public QueryPage<DelOutboundExportListVO> query(ExcelUtils.ExportContext exportContext) {
//                                return queryPage;
//                            }
//                        },
//                        new ExcelUtils.ExportSheet<DelOutboundExportItemListVO>() {
//                            @Override
//                            public String sheetName() {
//                                if("en".equals(len)){
//                                    return "SKU list";
//                                }else{
//                                    return "????????????";
//                                }
//                            }
//
//                            @Override
//                            public Class<DelOutboundExportItemListVO> classType() {
//                                return DelOutboundExportItemListVO.class;
//                            }
//
//                            @Override
//                            public QueryPage<DelOutboundExportItemListVO> query(ExcelUtils.ExportContext exportContext) {
//                                return itemQueryPage;
//                            }
//                        }));
            }

//            QueryDto queryDto2 = new QueryDto();
//            queryDto2.setPageNum(1);
//            queryDto2.setPageSize(500);



//            ExcelUtils.export(response, null, ExcelUtils.ExportExcel.build("en".equals(len) ? "Outbound_order" : "?????????", len,  null, new ExcelUtils.ExportSheet<DelOutboundExportListVO>() {
//                        @Override
//                        public String sheetName() {
//
//                            if("en".equals(len)){
//                                return "Outbound Order Information";
//                            }else{
//                                return "???????????????";
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
//                                return "????????????";
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
            log.error("????????????:" + e.getMessage(), e);
        }
    }






    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:exportselect')")
    @Log(title = "???????????? - ????????????", businessType = BusinessType.OTHER)
    @PostMapping("/exportselect")
    @ApiOperation(value = "???????????? - ??????", position = 1600)
    @SneakyThrows
    public R exportselect(HttpServletResponse response, @RequestBody DelOutboundListQueryDto queryDto) {
        try {

            String len = getLen();

            if (Objects.nonNull(SecurityUtils.getLoginUser())) {
                String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
                if (StringUtils.isEmpty(queryDto.getCustomCode())) {
                    queryDto.setCustomCode(cusCode);
                }
            }


            Integer   DelOutboundExportTotal=delOutboundService.selectDelOutboundCount(queryDto);

         return R.ok(DelOutboundExportTotal);

        } catch (Exception e) {
            log.error("????????????:" + e.getMessage(), e);
           return R.failed("????????????");
        }
    }





    @AutoValue
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:delOutboundCharge')")
    @PostMapping("/delOutboundCharge/page")
    @ApiOperation(value = "???????????? - ???????????????????????????????????????", position = 1700)
    public R<TableDataInfo<QueryChargeVO>> getDelOutboundCharge(@RequestBody QueryChargeDto queryDto) {
        QueryDto page = new QueryDto();
        page.setPageNum(queryDto.getPageNum());
        page.setPageSize(queryDto.getPageSize());
        startPage(page);
        return R.ok(getDataTable(delOutboundService.getDelOutboundCharge(queryDto)));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:toPrint')")
    @PutMapping("/toPrint")
    @ApiOperation(value = "???????????? - ??????", position = 1800)
    @ApiImplicitParam(name = "dto", value = "??????", dataType = "DelOutboundToPrintDto")
    public R<Boolean> toPrint(@RequestBody DelOutboundToPrintDto dto) {
        return R.ok(delOutboundService.toPrint(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:batchUpdateTrackingNoTemplate')")
    @GetMapping("/batchUpdateTrackingNoTemplate")
    @ApiOperation(value = "???????????? - ?????? - ????????????????????????", position = 1900)
    public void batchUpdateTrackingNoTemplate(HttpServletResponse response) {
        String filePath = "/template/DM_UpdateTracking.xlsx";
        String fileName = "????????????";
        this.downloadTemplate(response, filePath, fileName, "xlsx");
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:batchUpdateTrackingNo')")
    @PostMapping("/batchUpdateTrackingNo")
    @ApiOperation(value = "???????????? - ?????? - ??????????????????", position = 1901)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "????????????", required = true, allowMultiple = true)
    })
    public R<List<Map<String, Object>>> batchUpdateTrackingNo(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "?????????????????????");
        try {
            DelOutboundBatchUpdateTrackingNoAnalysisEventListener analysisEventListener = new DelOutboundBatchUpdateTrackingNoAnalysisEventListener();
            ExcelReaderBuilder excelReaderBuilder = EasyExcelFactory.read(file.getInputStream(), DelOutboundBatchUpdateTrackingNoDto.class, analysisEventListener);
            ExcelReaderSheetBuilder excelReaderSheetBuilder = excelReaderBuilder.sheet(0);
            excelReaderSheetBuilder.build().setHeadRowNumber(1);
            excelReaderSheetBuilder.doRead();
            List<DelOutboundBatchUpdateTrackingNoDto> list = analysisEventListener.getList();
            List<Map<String, Object>> list1=this.delOutboundService.batchUpdateTrackingNo(list);
//            logger.info("???????????????{}",list1);
             //???list1????????????????????? ???????????????
            delOutboundService.emailBatchUpdateTrackingNo(list1,filepath);

            return R.ok(list1);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.failed(e.getMessage());
        }
    }



    /**
     * ??????????????????
     */
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:batchTrackingexport')")
    @Log(title = "??????????????????", businessType = BusinessType.EXPORT)
    @GetMapping("/batchTrackingexport")
    @ApiOperation(value = "??????????????????",notes = "??????????????????")
    public void batchTrackingexport(HttpServletResponse response) throws IOException {
        List<DelOutboundTarckError> list = delOutboundService.selectbatchTrackingexport();
        ExportParams params = new ExportParams();




        Workbook workbook = ExcelExportUtil.exportExcel(params, DelOutboundTarckError.class, list);


        Sheet sheet= workbook.getSheet("sheet0");

        //?????????????????????
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
            //true??????????????????????????????
            font.setBold(true);
            //??????????????????????????????????????????????????????????????????
            font.setColor(IndexedColors.WHITE.getIndex());
            //??????????????????????????????????????????
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
            String fileName="????????????"+System.currentTimeMillis();
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
    @Log(title = "???????????????", businessType = BusinessType.UPDATE)
    @PostMapping("/againTrackingNo")
    @ApiOperation(value = "???????????? - ???????????? - ??????????????????", position = 2000)
    @ApiImplicitParam(name = "dto", value = "??????", dataType = "DelOutboundAgainTrackingNoDto")
    public R<Integer> againTrackingNo(@RequestBody @Validated DelOutboundAgainTrackingNoDto dto) {
        return R.ok(this.delOutboundService.againTrackingNo(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:exceptionMessageList')")
    @PostMapping("/exceptionMessageList")
    @ApiOperation(value = "???????????? - ???????????? - ??????????????????", position = 2100)
    @ApiImplicitParam(name = "orderNos", value = "??????", dataType = "String")
    public R<List<DelOutboundListExceptionMessageVO>> exceptionMessageList(@RequestBody List<String> orderNos) {
        return R.ok(this.delOutboundService.exceptionMessageList(orderNos));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:exceptionMessageExportList')")
    @PostMapping("/exceptionMessageExportList")
    @ApiOperation(value = "???????????? - ???????????? - ??????????????????(??????)", position = 2101)
    @ApiImplicitParam(name = "orderNos", value = "??????", dataType = "String")
    public R<List<DelOutboundListExceptionMessageExportVO>> exceptionMessageExportList(@RequestBody List<String> orderNos) {
        return R.ok(this.delOutboundService.exceptionMessageExportList(orderNos));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:reassign')")
    @Log(title = "???????????????", businessType = BusinessType.INSERT)
    @PostMapping("/reassign")
    @ApiOperation(value = "???????????? - ??????", position = 2200)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundDto")
    public R<DelOutboundAddResponse> reassign(@RequestBody DelOutboundDto dto) {
        // ??????????????????
        DelOutboundAddResponse delOutboundAddResponse = delOutboundService.reassign(dto);
        if (null != delOutboundAddResponse
                && null != delOutboundAddResponse.getStatus()
                && delOutboundAddResponse.getStatus()) {
            // ??????????????????
            this.delOutboundCompletedService.add(delOutboundAddResponse.getOrderNo(), DelOutboundOperationTypeEnum.BRING_VERIFY.getCode());
        }
        return R.ok(delOutboundAddResponse);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:addShopify')")
    @Log(title = "???????????????", businessType = BusinessType.INSERT)
    @PostMapping("/addShopify")
    @ApiOperation(value = "???????????? - ??????Shopify?????????", position = 2300)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundDto")
    public R<DelOutboundAddResponse> addShopify(@RequestBody DelOutboundDto dto) {
        // ???????????????
        DelOutboundAddResponse data = delOutboundService.insertDelOutboundShopify(dto);
        // ??????????????????
        if (null != data
                && null != data.getStatus()
                && data.getStatus()) {
            // ??????????????????
            this.delOutboundCompletedService.add(data.getOrderNo(), DelOutboundOperationTypeEnum.BRING_VERIFY.getCode());
        }
        return R.ok(data);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:updateInStockList')")
    @Log(title = "???????????????", businessType = BusinessType.INSERT)
    @PostMapping("/updateInStockList")
    @ApiOperation(value = "???????????? - ??????????????????", position = 2400)
    @ApiImplicitParam(name = "idList", value = "?????????ID", dataType = "Long")
    public R<Boolean> updateInStockList(@RequestBody List<Long> idList) {
        LambdaUpdateWrapper<DelOutbound> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.set(DelOutbound::getInStock, true);
        lambdaUpdateWrapper.in(DelOutbound::getId, idList);
        this.delOutboundService.update(null, lambdaUpdateWrapper);
        return R.ok(true);
    }
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:receiveLabel')")
    @Log(title = "???????????????", businessType = BusinessType.UPDATE)
    @PostMapping("/receiveLabel")
    @ApiOperation(value = "???????????? - ????????????????????????????????????", position = 400)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundDto")
    public R<Integer> receiveLabel(@RequestBody @Validated(ValidationUpdateGroup.class) DelOutboundReceiveLabelDto dto) {
        return R.ok(delOutboundService.receiveLabel(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:receiveLabel')")
    @Log(title = "???????????????", businessType = BusinessType.UPDATE)
    @PostMapping("/box/status")
    @ApiOperation(value = "???????????? - ????????????????????????????????????", position = 400)
    @ApiImplicitParam(name = "dto", value = "?????????", dataType = "DelOutboundDto")
    public R<Integer> boxStatus(@RequestBody DelOutboundBoxStatusDto dto) {
        return R.ok(delOutboundService.boxStatus(dto));
    }

    @PostMapping("/findDelboundCharges")
    @ApiOperation(value = "????????????-???????????????????????????", position = 500)
    public R findDelboundCharges(@RequestBody List<String> orderNoList) {
        List<DelOutboundChargeData> delOutboundChargeData = delOutboundService.findDelboundCharges(orderNoList);
        return R.ok(delOutboundChargeData);
    }

    @PostMapping("/findDelboundAddress")
    @ApiOperation(value = "????????????-?????????????????????", position = 501)
    public R findDelboundAddress(@RequestBody List<String> orderNoList) {
        List<DelOutboundAddress> delOutboundAddresses = delOutboundService.findDelboundAddress(orderNoList);
        return R.ok(delOutboundAddresses);
    }

    @PostMapping("/updateDeloutboundTrackMsg")
    @ApiOperation(value = "????????????-?????????????????????", position = 502)
    public R updateDeloutboundTrackMsg(@RequestBody DelOutboundTrackRequestVO updateDeloutboundTrackMsg) {
        return delOutboundService.updateDeloutboundTrackMsg(updateDeloutboundTrackMsg);
    }

}
