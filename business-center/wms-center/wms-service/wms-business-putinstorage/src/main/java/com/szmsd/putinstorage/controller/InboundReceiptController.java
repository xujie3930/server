package com.szmsd.putinstorage.controller;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.dto.BaseProductMeasureDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.pack.domain.PackageCollection;
import com.szmsd.putinstorage.annotation.InboundReceiptLog;
import com.szmsd.putinstorage.component.CheckTag;
import com.szmsd.putinstorage.component.RemoteComponent;
import com.szmsd.putinstorage.domain.InboundReceiptRecord;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.*;
import com.szmsd.putinstorage.enums.InboundReceiptRecordEnum;
import com.szmsd.putinstorage.service.IInboundReceiptRecordService;
import com.szmsd.putinstorage.service.IInboundReceiptService;
import com.szmsd.putinstorage.service.IInboundTrackingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * iInboundReceiptService
 * <p>
 * rec_wareh - ?????? ???????????????
 * </p>
 *
 * @author liangchao
 * @since 2021-03-03
 */



@Api(tags = {"??????"})
@RestController
@RequestMapping("/inbound")
public class InboundReceiptController extends BaseController {

    private final static long LOCK_TIME = 10L;

    @Resource
    private IInboundReceiptService iInboundReceiptService;

    @Resource
    private IInboundReceiptRecordService iInboundReceiptRecordService;
    @Resource
    private IInboundTrackingService iInboundTrackingService;
    @Resource
    private RemoteComponent remoteComponent;
    @Resource
    private RedissonClient redissonClient;

    private Logger logger = LoggerFactory.getLogger(InboundReceiptController.class);


    //    @AutoValue
    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @GetMapping("/receipt/page")
    @ApiOperation(value = "??????", notes = "???????????? - ????????????")
    public TableDataInfo<InboundReceiptVO> page(InboundReceiptQueryDTO queryDTO) {
        startPage();
        List<InboundReceiptVO> list = iInboundReceiptService.selectList(queryDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @PostMapping("/open/receipt/page")
    @ApiOperation(value = "??????", notes = "???????????? - ????????????")
    public TableDataInfo<InboundReceiptVO> postPage(@RequestBody InboundReceiptQueryDTO queryDTO) {
        startPage(queryDTO);
        List<InboundReceiptVO> list = iInboundReceiptService.selectList(queryDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @GetMapping("/receipt/list")
    @ApiOperation(value = "??????", notes = "????????????")
    public R<List<InboundReceiptVO>> list(@RequestBody InboundReceiptQueryDTO queryDTO) {
        List<InboundReceiptVO> list = iInboundReceiptService.selectList(queryDTO);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @PostMapping("/receipt/saveOrUpdate")
    @ApiOperation(value = "??????/??????", notes = "???????????? - ??????/??????")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.CREATE)
    public R<InboundReceiptInfoVO> saveOrUpdate(@RequestBody CreateInboundReceiptDTO createInboundReceiptDTO) {
        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#saveOrUpdate" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                try {
                    return R.ok(iInboundReceiptService.saveOrUpdate(createInboundReceiptDTO));
                } finally {
                    CheckTag.remove();
                }
            } else {
                return R.failed("??????????????????????????????!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("?????????????????????", e);
            return R.failed("??????/?????????????????????!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @PostMapping("/updateTrackingNo")
    @ApiOperation(value = "????????????????????????", notes = "???????????????????????? ?????????/??????????????????????????????")
    public R<Integer> updateTrackingNo(@Validated @RequestBody UpdateTrackingNoRequest updateTrackingNoRequest) {
        return R.ok(iInboundReceiptService.updateTrackingNo(updateTrackingNoRequest));
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @PostMapping("/receipt/saveOrUpdate/batch")
    @ApiOperation(value = "??????/??????-??????", notes = "?????? ???????????? - ??????/??????")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.CREATE)
    public R<List<InboundReceiptInfoVO>> saveOrUpdateBatch(@RequestBody List<CreateInboundReceiptDTO> createInboundReceiptDTOList) {
        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#saveOrUpdateBatch" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                try {
                    List<InboundReceiptInfoVO> resultList = new ArrayList<>();
                    createInboundReceiptDTOList.forEach(createInboundReceiptDTO -> {
                        InboundReceiptInfoVO inboundReceiptInfoVO = iInboundReceiptService.saveOrUpdate(createInboundReceiptDTO);
                        resultList.add(inboundReceiptInfoVO);
                    });
                    return R.ok(resultList);
                } finally {
                    CheckTag.remove();
                }
            } else {
                return R.failed("??????????????????????????????!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("????????????/?????????????????????!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @DeleteMapping("/receipt/cancel/{warehouseNo}")
    @ApiOperation(value = "??????", notes = "???????????? - ??????")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.CANCEL)
    public R cancel(@PathVariable("warehouseNo") String warehouseNo) {
        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#cancel" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                iInboundReceiptService.cancel(warehouseNo);
                return R.ok();
            } else {
                return R.failed("??????????????????????????????!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("?????????????????????!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:info')")
    @GetMapping("/receipt/info/{warehouseNo}")
    @ApiOperation(value = "??????", notes = "???????????? - ????????????????????????")
    public R<InboundReceiptInfoVO> info(@PathVariable("warehouseNo") String warehouseNo) {
        InboundReceiptInfoVO inboundReceiptInfoVO = iInboundReceiptService.queryInfo(warehouseNo);
        return R.ok(inboundReceiptInfoVO);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:exporttemplate')")
    @GetMapping("/receipt/exportTemplate")
    @ApiOperation(value = "??????sku??????", notes = "???????????? - ?????? - ????????????")
    public void exportTemplate(HttpServletResponse response) {
//        String len = getLen().toLowerCase(Locale.ROOT);
//        List<String> rows;
//        String fileName;
//        if ("en".equals(len)) {
//            fileName = "inbound_order_sku_import";
//            rows = CollUtil.newArrayList("inbound order no", "SKU", "Quantity", "Original SKU code", "Remarks");
//        } else {
//            fileName = "?????????SKU??????";
//            rows = CollUtil.newArrayList("????????????", "SKU??????", "????????????", "???SKU??????", "??????");
//        }
//        super.excelExportTitle(response, rows, fileName);

        String len=getLen().toLowerCase(Locale.ROOT);
        String filePath=null;
        String fileName=null;
        if (len.equals("zh")){
            filePath = "/template/?????????SKU????????????.xlsx";
            fileName = "?????????SKU????????????";
        }else if (len.equals("en")){
            filePath = "/template/SKU_Import_of_Inbound_Order.xlsx";
            fileName = "SKU_Import_of_Inbound_Order";
        }

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
                org.springframework.core.io.Resource resource = new ClassPathResource(filePath);
                inputStream = resource.getInputStream();
                response.setHeader("File-Source", "resource");
            }
            outputStream = response.getOutputStream();
            //response???HttpServletResponse??????
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            String efn = URLEncoder.encode(fileName, "utf-8");
            //Loading plan.xls??????????????????????????????????????????????????????????????????????????????
            response.setHeader("Content-Disposition", "attachment;filename=" + efn + ".xls");
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "??????????????????" + e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "????????????????????????" + e.getMessage());
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:exportsku')")
    @PostMapping("/receipt/exportSku")
    @ApiOperation(value = "??????sku", notes = "???????????? - ?????? - ??????")
    public void exportSku(@RequestBody List<InboundReceiptDetailVO> details, HttpServletResponse response) {
        try (Workbook excel = new XSSFWorkbook();
             OutputStream out = response.getOutputStream()) {
            // ??????SKU
            iInboundReceiptService.exportSku(excel, details);
            //response???HttpServletResponse??????
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //Loading plan.xls??????????????????????????????????????????????????????????????????????????????
            String fileName = URLEncoder.encode("SKU_?????????_" + details.get(0).getWarehouseNo(), "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
            excel.write(out);
            //????????????????????????Servlet???
            IoUtil.close(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:importsku')")
    @PostMapping("/receipt/{cusCode}/importSku")
    @ApiOperation(value = "??????sku", notes = "???????????? - ?????? - ??????")
    public R<List<InboundReceiptDetailVO>> importSku(MultipartFile file, @PathVariable("cusCode") String cusCode) {
        AssertUtil.isTrue(ObjectUtils.allNotNull(file), "?????????????????????");
        String originalFilename = file.getOriginalFilename();
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf + 1);
        boolean isXls = "xls".equals(suffix);
        boolean isXlsx = "xlsx".equals(suffix);
        AssertUtil.isTrue(isXls || isXlsx, "?????????xls???xlsx??????");
        List<String> error = new ArrayList<>();
        List<InboundReceiptDetailVO> inboundReceiptDetailVOSex = new ArrayList<>();
        List<InboundReceiptDetailVO> inboundReceiptDetailVOS = new ArrayList();
        try {
            inboundReceiptDetailVOSex = EasyExcel.read(file.getInputStream(), InboundReceiptDetailVO.class, new SyncReadListener()).sheet().doReadSync();



            //list????????????????????????????????????????????????
            inboundReceiptDetailVOS = inboundReceiptDetailVOSex.stream().collect(Collectors.toMap(InboundReceiptDetailVO::getSku, a -> a, (o1,o2)-> {

                        if (o1.getDeclareQty()!=null&&o2.getDeclareQty()!=null) {
                            o1.setDeclareQty(o1.getDeclareQty() + o2.getDeclareQty());
                        }

                        return o1;

                    })).values().stream().collect(Collectors.toList());



//            ExcelUtil<InboundReceiptDetailVO> excelUtil = new ExcelUtil<>(InboundReceiptDetailVO.class);
//            inboundReceiptDetailVOS = excelUtil.importExcel(file.getInputStream());
            Map<String, Long> collect = inboundReceiptDetailVOS.stream().map(InboundReceiptDetailVO::getSku).collect(Collectors.groupingBy(p -> p, Collectors.counting()));
            collect.forEach((key, value) -> AssertUtil.isTrue(!(value > 1L), "Excel????????????SKU[" + key + "]"));

            List<BaseProductMeasureDto> skuList = remoteComponent.querySku(new ArrayList<>(collect.keySet()), cusCode);
            for (int i = 0; i < inboundReceiptDetailVOS.size(); i++) {
                InboundReceiptDetailVO vo = inboundReceiptDetailVOS.get(i);
                String item = vo.getSku();
                List<BaseProductMeasureDto> collect1 = skuList.stream().filter(data -> item.equals(data.getCode())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect1)) {
                    error.add("Excel???" + (i + 1) + "???,sku[" + item + "]?????????");
                } else {
                    vo.setSkuName(collect1.get(0).getProductName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("??????????????????");
        }
        AssertUtil.isTrue(error.size() == 0, String.join("; ", error));
        return R.ok(inboundReceiptDetailVOS);
    }

    TimedCache<String, Long> skuRep = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis() * 3);

    @PreAuthorize("@ss.hasPermi('inbound:receiving')")
    @PostMapping("/receiving")
    @ApiOperation(value = "#B1 ??????????????????", notes = "#B1 ??????????????????")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.PUT)
    public R receiving(@RequestBody ReceivingRequest receivingRequest) {
        String repeatRequestKey = JSONObject.toJSONString(receivingRequest);
        Long excuteTime = skuRep.get(repeatRequestKey);
        if (null == excuteTime) {
            skuRep.put(repeatRequestKey, System.currentTimeMillis());
        } else {
            log.info("#B1 ?????????????????? ???????????????{}==={}", receivingRequest, excuteTime);
            return R.ok();
        }

        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#receiving" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                iInboundReceiptService.receiving(receivingRequest);
                return R.ok();
            } else {
                return R.failed("??????????????????????????????!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("????????????????????????!");
        } catch (Exception e) {
            skuRep.remove(repeatRequestKey);
            log.error("????????????????????????:", e);
            throw new RuntimeException(e.getMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    //@PreAuthorize("@ss.hasPermi('inbound:receipt')")
    @PostMapping("/receipt")
    @ApiOperation(value = "#B6 ????????????????????????", notes = "#B6 ????????????????????????")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.PUT)
    public R receipt(@RequestBody ReceiptRequest receiptRequest) {
        String repeatRequestKey = JSONObject.toJSONString(receiptRequest);
        Long excuteTime = skuRep.get(repeatRequestKey);
        if (null == excuteTime) {
            skuRep.put(repeatRequestKey, System.currentTimeMillis());
        } else {
            log.info("#B1 ???????????????????????? ???????????????{}==={}", receiptRequest, excuteTime);
            return R.ok();
        }

        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#receipt" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                iInboundReceiptService.receipt(receiptRequest);
                return R.ok();
            } else {
                return R.failed("??????????????????????????????!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("??????????????????????????????!");
        } catch (Exception e) {
            skuRep.remove(repeatRequestKey);
            log.error("??????????????????????????????:", e);
            throw new RuntimeException(e.getMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @GetMapping("/updateInboundReceipt/{warehouseNo}")
    @ApiOperation(value = "??????500????????????", notes = "??????500????????????")
    public R updateInboundReceipt(@PathVariable("warehouseNo") String warehouseNo) {
        iInboundReceiptService.updateInboundReceipt(warehouseNo);
        return R.ok();
    }



    @PreAuthorize("@ss.hasPermi('inbound:receiving:completed')")
    @PostMapping("/receiving/completed")
    @ApiOperation(value = "#B3 ??????????????????", notes = "#B3 ??????????????????")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.COMPLETED)
    public R completed(@RequestBody ReceivingCompletedRequest receivingCompletedRequest) {
        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#completed" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                iInboundReceiptService.completed(receivingCompletedRequest);
                return R.ok();
            } else {
                return R.failed("??????????????????????????????!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("????????????????????????!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:arraigned')")
    @PutMapping("/arraigned")
    @ApiOperation(value = "??????", notes = "???????????????")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.ARRAIGNED)
    public R arraigned(@RequestBody List<String> warehouseNos) {
        iInboundReceiptService.arraigned(warehouseNos);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:review')")
    @PostMapping("/review")
    @ApiOperation(value = "??????", notes = "???????????????")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.REVIEW)
    public R review(@RequestBody InboundReceiptReviewDTO inboundReceiptReviewDTO) {
        iInboundReceiptService.review(inboundReceiptReviewDTO);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:delete')")
    @DeleteMapping("/delete/{warehouseNo}")
    @ApiOperation(value = "??????", notes = "????????????")
    public R delete(@PathVariable("warehouseNo") String warehouseNo) {
        iInboundReceiptService.delete(warehouseNo);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:export')")
    @PostMapping("/export")
    @ApiOperation(value = "???????????????", notes = "???????????? - ??????")
    public void export(@RequestBody InboundReceiptQueryDTO queryDTO, HttpServletResponse httpServletResponse) {

        List<InboundReceiptExportVO> list = new ArrayList<>();
        queryDTO.setExportIdIndex(0L);
        ExcelWriter excelWriter = null;
        try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            String fileName = "???????????????_" + System.currentTimeMillis();
            String sheetName1 = "???????????????";
            String sheetName2 = "??????????????????";
            if ("en".equals(getLen().toLowerCase(Locale.ROOT))) {
                sheetName1 = "Inbound Order Information";
                sheetName2 = "Inbound Arrival Situation";
                fileName = "Inbound_Order" + System.currentTimeMillis();
            }


            String efn = URLEncoder.encode(fileName, "utf-8");
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + efn + ".xlsx");
            excelWriter = EasyExcel.write(outputStream).build();
            WriteSheet build1 = EasyExcel.writerSheet(0, sheetName1).head(inboundReceiptExportHead()).build();
            WriteSheet build2 = EasyExcel.writerSheet(1, sheetName2).head(inboundTrackingExportHead()).build();
            long start = System.currentTimeMillis();
            while ((list = iInboundReceiptService.selectExport(queryDTO)).size() != 0) {
                log.info("????????????1---" + (System.currentTimeMillis() - start));
                Long exportIdIndex = queryDTO.getExportIdIndex();
                queryDTO.setExportIdIndex(exportIdIndex + (500L));
                List<InboundReceiptExportVO> finalList = list;
                CompletableFuture<List<InboundTrackingExportVO>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
                    List<String> orderNoList = finalList.stream().map(InboundReceiptExportVO::getWarehouseNo).collect(Collectors.toList());
                    List<InboundTrackingExportVO> inboundTrackingVOS = iInboundTrackingService.selectInboundTrackingList(orderNoList);
                    Map<String, List<InboundTrackingExportVO>> trackingNumberMap = inboundTrackingVOS.stream().collect(Collectors.groupingBy(InboundTrackingExportVO::getOrderNo));
                    finalList.forEach(x -> {
                        List<String> thisTrackingNumList = StringToolkit.getCodeByArray(x.getDeliveryNo());
                        if (CollectionUtils.isEmpty(thisTrackingNumList)) thisTrackingNumList = new ArrayList<>();
                        List<InboundTrackingExportVO> inboundTrackingVOS1 = trackingNumberMap.get(x.getWarehouseNo());
                        if (CollectionUtils.isEmpty(inboundTrackingVOS1)) inboundTrackingVOS1 = new ArrayList<>();
                        List<String> collect = inboundTrackingVOS1.stream().map(InboundTrackingExportVO::getTrackingNumber).collect(Collectors.toList());
                        // ????????????????????? ???????????????????????????
                        thisTrackingNumList.removeAll(collect);
                        List<InboundTrackingExportVO> notArrivedList = thisTrackingNumList.stream().map(tr -> {
                            InboundTrackingExportVO inboundTrackingVO = new InboundTrackingExportVO();
                            inboundTrackingVO.setOrderNo(x.getWarehouseNo());
                            inboundTrackingVO.setTrackingNumber(tr);
                            inboundTrackingVO.setReceiptStatus("?????????");
                            return inboundTrackingVO;
                        }).collect(Collectors.toList());
                        inboundTrackingVOS.addAll(notArrivedList);


                    });
                    return inboundTrackingVOS;
                });
                // ???sheet 1
                excelWriter.write(list, build1);
                log.info("????????????2---" + (System.currentTimeMillis() - start));
                List<InboundTrackingExportVO> inboundTrackingExportVOS = listCompletableFuture.get();
                log.info("????????????3---" + (System.currentTimeMillis() - start));
                // ???sheet 2
                excelWriter.write(inboundTrackingExportVOS, build2);
                log.info("????????????4---" + (System.currentTimeMillis() - start));
            }
            log.info("????????????5---" + (System.currentTimeMillis() - start));
            excelWriter.finish();
            outputStream.flush();
            log.info("????????????5---" + (System.currentTimeMillis() - start));
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (null != excelWriter)
                excelWriter.finish();
        }
    }

    private static final List<String> INBOUND_RECEIPT_EXPORT_HEAD_ZH;
    private static final List<String> INBOUND_RECEIPT_EXPORT_HEAD_EN;

    private static final List<String> INBOUND_TRACKING_EXPORT_HEAD_ZH;
    private static final List<String> INBOUND_TRACKING_EXPORT_HEAD_EN;

    static {
        INBOUND_RECEIPT_EXPORT_HEAD_ZH = Arrays.asList("????????????", "????????????", "????????????", "????????????/????????????", "??????", "????????????", "????????????", "SKU", "????????????", "????????????", "???????????????", "????????????", "????????????", "????????????", "????????????", "??????VAT");
        INBOUND_RECEIPT_EXPORT_HEAD_EN = Arrays.asList("Inbound Orders", "Purchase Orders", "Delivery Method", "Tracking Nu of pickup", "Status",
                "Destinatuon Warehouse", "Inbound Method", "SKU", "Initial Qty", "Received Qty", "Original Product Code", "Order Time",
                "Arriving time", "Under Review", "Customer Remarks", "Sales manager VAT");

        INBOUND_TRACKING_EXPORT_HEAD_EN = Arrays.asList("Inbound Orders", "Tracking Number/\n" +
                "Collecting Number", "Status", "Operating Time");
        INBOUND_TRACKING_EXPORT_HEAD_ZH = Arrays.asList("????????????", "????????????/????????????", "????????????", "????????????");
    }

    private List<List<String>> inboundReceiptExportHead() {
        String len = getLen().toLowerCase(Locale.ROOT);
        if ("en".equals(len)) {
            return head(INBOUND_RECEIPT_EXPORT_HEAD_EN);
        } else {
            return head(INBOUND_RECEIPT_EXPORT_HEAD_ZH);
        }
    }

    private List<List<String>> inboundTrackingExportHead() {
        String len = getLen().toLowerCase(Locale.ROOT);
        if ("en".equals(len)) {
            return head(INBOUND_TRACKING_EXPORT_HEAD_EN);
        } else {
            return head(INBOUND_TRACKING_EXPORT_HEAD_ZH);
        }
    }

    private static List<List<String>> head(List<String> excelTitleNameList) {
        List<List<String>> headList = new ArrayList<>(excelTitleNameList.size());
        excelTitleNameList.forEach(x -> {
            List<String> head = new ArrayList<>();
            head.add(x);
            headList.add(head);
        });
        return headList;
    }

    @PreAuthorize("@ss.hasPermi('inbound:statistics')")
    @GetMapping("/statistics")
    @ApiOperation(value = "??????", notes = "???????????????")
    public R<List<InboundCountVO>> statistics(InboundReceiptQueryDTO queryDTO) {
        List<InboundCountVO> statistics = iInboundReceiptService.statistics(queryDTO);
        return R.ok(statistics);
    }

    @GetMapping("/receipt/queryRecord")
    @ApiOperation(value = "??????", notes = "???????????????")
    public TableDataInfo<InboundReceiptRecord> queryRecord(InboundReceiptRecordQueryDTO queryDTO) {
        startPage();
        String le=getLen();
        return getDataTable(iInboundReceiptRecordService.selectList(queryDTO,le));
    }

    @PostMapping("/receiving/tracking")
    @ApiOperation(value = "#B5 ????????????????????????", notes = "#B5 ????????????????????????")
    R tracking(@Validated @RequestBody ReceivingTrackingRequest receivingCompletedRequest) {
        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#tracking" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                iInboundReceiptService.tracking(receivingCompletedRequest);
                return R.ok();
            } else {
                return R.failed("????????????????????????????????????,???????????????!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("????????????????????????????????????!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:querySkuStockByRange')")
    @PostMapping("/querySkuStockByRange")
    @ApiOperation(value = "??????sku???????????????", notes = "??????sku???????????????-???????????????")
    public R<List<SkuInventoryStockRangeVo>> querySkuStockByRange(@RequestBody @Validated InventoryStockByRangeDTO inventoryStockByRangeDTO) {
        inventoryStockByRangeDTO.valid();
        return R.ok(iInboundReceiptService.querySkuStockByRange(inventoryStockByRangeDTO));
    }

    @PreAuthorize("@ss.hasPermi('inventory:querySkuStockByRange')")
    @PostMapping("/collectAndInbound")
    @ApiOperation(value = "????????????", notes = "??????sku???????????????-???????????????")
    public R<InboundReceiptInfoVO> collectAndInbound(@RequestBody @Validated PackageCollection packageCollection) {
        return R.ok(iInboundReceiptService.collectAndInbound(packageCollection));
    }


}
