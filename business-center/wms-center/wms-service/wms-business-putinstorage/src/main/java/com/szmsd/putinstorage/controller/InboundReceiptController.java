package com.szmsd.putinstorage.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.szmsd.bas.dto.BaseProductMeasureDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * iInboundReceiptService
 * <p>
 * rec_wareh - 入库 前端控制器
 * </p>
 *
 * @author liangchao
 * @since 2021-03-03
 */


@Api(tags = {"入库"})
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

    //    @AutoValue
    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @GetMapping("/receipt/page")
    @ApiOperation(value = "查询", notes = "入库管理 - 分页查询")
    public TableDataInfo<InboundReceiptVO> page(InboundReceiptQueryDTO queryDTO) {
        startPage();
        List<InboundReceiptVO> list = iInboundReceiptService.selectList(queryDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @PostMapping("/open/receipt/page")
    @ApiOperation(value = "查询", notes = "入库管理 - 分页查询")
    public TableDataInfo<InboundReceiptVO> postPage(@RequestBody InboundReceiptQueryDTO queryDTO) {
        startPage(queryDTO);
        List<InboundReceiptVO> list = iInboundReceiptService.selectList(queryDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @GetMapping("/receipt/list")
    @ApiOperation(value = "查询", notes = "入库管理")
    public R<List<InboundReceiptVO>> list(@RequestBody InboundReceiptQueryDTO queryDTO) {
        List<InboundReceiptVO> list = iInboundReceiptService.selectList(queryDTO);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @PostMapping("/receipt/saveOrUpdate")
    @ApiOperation(value = "创建/修改", notes = "入库管理 - 新增/创建")
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
                return R.failed("请求超时，请稍候重试!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("创建修改失败：", e);
            return R.failed("创建/修改入库单失败!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @PostMapping("/updateTrackingNo")
    @ApiOperation(value = "修改快递单号信息", notes = "修改快递单号信息 已完成/已取消的订单不能处理")
    public R<Integer> updateTrackingNo(@Validated @RequestBody UpdateTrackingNoRequest updateTrackingNoRequest) {
        return R.ok(iInboundReceiptService.updateTrackingNo(updateTrackingNoRequest));
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @PostMapping("/receipt/saveOrUpdate/batch")
    @ApiOperation(value = "创建/修改-批量", notes = "批量 入库管理 - 新增/创建")
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
                return R.failed("请求超时，请稍候重试!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("批量创建/修改入库单失败!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @DeleteMapping("/receipt/cancel/{warehouseNo}")
    @ApiOperation(value = "取消", notes = "入库管理 - 取消")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.CANCEL)
    public R cancel(@PathVariable("warehouseNo") String warehouseNo) {
        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#cancel" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                iInboundReceiptService.cancel(warehouseNo);
                return R.ok();
            } else {
                return R.failed("请求超时，请稍候重试!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("取消入库单失败!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:info')")
    @GetMapping("/receipt/info/{warehouseNo}")
    @ApiOperation(value = "详情", notes = "入库管理 - 详情（包含明细）")
    public R<InboundReceiptInfoVO> info(@PathVariable("warehouseNo") String warehouseNo) {
        InboundReceiptInfoVO inboundReceiptInfoVO = iInboundReceiptService.queryInfo(warehouseNo);
        return R.ok(inboundReceiptInfoVO);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:exporttemplate')")
    @GetMapping("/receipt/exportTemplate")
    @ApiOperation(value = "导出sku模板", notes = "入库管理 - 新增 - 下载模板")
    public void exportTemplate(HttpServletResponse response) {
        String len = getLen().toLowerCase(Locale.ROOT);
        List<String> rows;
        String fileName;
        if ("en".equals(len)) {
            fileName = "inbound_order_sku_import";
            rows = CollUtil.newArrayList("inbound order no", "SKU", "QTY", "Original product code", "Note");
        } else {
            fileName = "入库单SKU导入";
            rows = CollUtil.newArrayList("入库单号", "SKU", "申报数量", "原产品编码", "备注");
        }
        super.excelExportTitle(response, rows, fileName);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:exportsku')")
    @PostMapping("/receipt/exportSku")
    @ApiOperation(value = "导出sku", notes = "入库管理 - 详情 - 导出")
    public void exportSku(@RequestBody List<InboundReceiptDetailVO> details, HttpServletResponse response) {
        try (Workbook excel = new XSSFWorkbook();
             OutputStream out = response.getOutputStream()) {
            // 导出SKU
            iInboundReceiptService.exportSku(excel, details);
            //response为HttpServletResponse对象
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            String fileName = URLEncoder.encode("SKU_入库单_" + details.get(0).getWarehouseNo(), "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
            excel.write(out);
            //此处记得关闭输出Servlet流
            IoUtil.close(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:importsku')")
    @PostMapping("/receipt/{cusCode}/importSku")
    @ApiOperation(value = "导入sku", notes = "入库管理 - 新增 - 导入")
    public R<List<InboundReceiptDetailVO>> importSku(MultipartFile file, @PathVariable("cusCode") String cusCode) {
        AssertUtil.isTrue(ObjectUtils.allNotNull(file), "上传文件不存在");
        String originalFilename = file.getOriginalFilename();
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf + 1);
        boolean isXls = "xls".equals(suffix);
        boolean isXlsx = "xlsx".equals(suffix);
        AssertUtil.isTrue(isXls || isXlsx, "请上传xls或xlsx文件");
        List<String> error = new ArrayList<>();
        List<InboundReceiptDetailVO> inboundReceiptDetailVOS = new ArrayList<>();
        try {
            inboundReceiptDetailVOS = EasyExcel.read(file.getInputStream(), InboundReceiptDetailVO.class, new SyncReadListener()).sheet().doReadSync();
//            ExcelUtil<InboundReceiptDetailVO> excelUtil = new ExcelUtil<>(InboundReceiptDetailVO.class);
//            inboundReceiptDetailVOS = excelUtil.importExcel(file.getInputStream());
            Map<String, Long> collect = inboundReceiptDetailVOS.stream().map(InboundReceiptDetailVO::getSku).collect(Collectors.groupingBy(p -> p, Collectors.counting()));
            collect.forEach((key, value) -> AssertUtil.isTrue(!(value > 1L), "Excel存在重复SKU[" + key + "]"));

            List<BaseProductMeasureDto> skuList = remoteComponent.querySku(new ArrayList<>(collect.keySet()), cusCode);
            for (int i = 0; i < inboundReceiptDetailVOS.size(); i++) {
                InboundReceiptDetailVO vo = inboundReceiptDetailVOS.get(i);
                String item = vo.getSku();
                List<BaseProductMeasureDto> collect1 = skuList.stream().filter(data -> item.equals(data.getCode())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(collect1)) {
                    error.add("Excel第" + (i + 1) + "行,sku[" + item + "]不存在");
                } else {
                    vo.setSkuName(collect1.get(0).getProductName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("文件解析异常");
        }
        AssertUtil.isTrue(error.size() == 0, String.join("; ", error));
        return R.ok(inboundReceiptDetailVOS);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receiving')")
    @PostMapping("/receiving")
    @ApiOperation(value = "#B1 接收入库上架", notes = "#B1 接收入库上架")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.PUT)
    public R receiving(@RequestBody ReceivingRequest receivingRequest) {
        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#receiving" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                iInboundReceiptService.receiving(receivingRequest);
                return R.ok();
            } else {
                return R.failed("请求超时，请稍候重试!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("接收入库上架失败!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receiving:completed')")
    @PostMapping("/receiving/completed")
    @ApiOperation(value = "#B3 接收完成入库", notes = "#B3 接收完成入库")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.COMPLETED)
    public R completed(@RequestBody ReceivingCompletedRequest receivingCompletedRequest) {
        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#completed" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                iInboundReceiptService.completed(receivingCompletedRequest);
                return R.ok();
            } else {
                return R.failed("请求超时，请稍候重试!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("接收完成入库异常!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:arraigned')")
    @PutMapping("/arraigned")
    @ApiOperation(value = "提审", notes = "客户端提审")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.ARRAIGNED)
    public R arraigned(@RequestBody List<String> warehouseNos) {
        iInboundReceiptService.arraigned(warehouseNos);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:review')")
    @PostMapping("/review")
    @ApiOperation(value = "审核", notes = "入库单审核")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.REVIEW)
    public R review(@RequestBody InboundReceiptReviewDTO inboundReceiptReviewDTO) {
        iInboundReceiptService.review(inboundReceiptReviewDTO);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:delete')")
    @DeleteMapping("/delete/{warehouseNo}")
    @ApiOperation(value = "删除", notes = "物理删除")
    public R delete(@PathVariable("warehouseNo") String warehouseNo) {
        iInboundReceiptService.delete(warehouseNo);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:export')")
    @PostMapping("/export")
    @ApiOperation(value = "导出入库单", notes = "入库管理 - 导出")
    public void export(@RequestBody InboundReceiptQueryDTO queryDTO, HttpServletResponse httpServletResponse) {
        List<InboundReceiptExportVO> list = iInboundReceiptService.selectExport(queryDTO);
       /* ExcelUtil<InboundReceiptExportVO> util = new ExcelUtil<>(InboundReceiptExportVO.class);
        util.exportExcel(response, list, "入库单导出_" + DateUtils.dateTimeNow());*/
        List<String> orderNoList = list.stream().map(InboundReceiptExportVO::getWarehouseNo).collect(Collectors.toList());
        List<InboundTrackingExportVO> inboundTrackingVOS = iInboundTrackingService.selectInboundTrackingList(orderNoList);
        Map<String, List<InboundTrackingExportVO>> trackingNumberMap = inboundTrackingVOS.stream().collect(Collectors.groupingBy(InboundTrackingExportVO::getOrderNo));
        List<InboundTrackingExportVO> finalInboundTrackingVOS = inboundTrackingVOS;
        list.forEach(x -> {
            List<String> thisTrackingNumList = StringToolkit.getCodeByArray(x.getDeliveryNo());
            if (CollectionUtils.isEmpty(thisTrackingNumList)) thisTrackingNumList = new ArrayList<>();
            List<InboundTrackingExportVO> inboundTrackingVOS1 = trackingNumberMap.get(x.getWarehouseNo());
            if (CollectionUtils.isEmpty(inboundTrackingVOS1)) inboundTrackingVOS1 = new ArrayList<>();
            List<String> collect = inboundTrackingVOS1.stream().map(InboundTrackingExportVO::getTrackingNumber).collect(Collectors.toList());
            // 去除已完成的单 把未完成的拼接上去
            thisTrackingNumList.removeAll(collect);
            List<InboundTrackingExportVO> notArrivedList = thisTrackingNumList.stream().map(tr -> {
                InboundTrackingExportVO inboundTrackingVO = new InboundTrackingExportVO();
                inboundTrackingVO.setOrderNo(x.getWarehouseNo());
                inboundTrackingVO.setTrackingNumber(tr);
                inboundTrackingVO.setReceiptStatus("未到货");
                return inboundTrackingVO;
            }).collect(Collectors.toList());
            finalInboundTrackingVOS.addAll(notArrivedList);
        });
        inboundTrackingVOS = inboundTrackingVOS.parallelStream().sorted(Comparator.comparing(InboundTrackingExportVO::getOrderNo)).collect(Collectors.toList());
        ExcelWriter excelWriter = null;
        try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            String fileName = "入库单导出_" + System.currentTimeMillis();
            String efn = URLEncoder.encode(fileName, "utf-8");
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + efn + ".xlsx");
            excelWriter = EasyExcel.write(outputStream).build();
            String sheetName1 = "入库单信息";
            String sheetName2 = "入库到货情况";
            if ("en".equals(getLen().toLowerCase(Locale.ROOT))) {
                sheetName1 = "Inbound Order Information";
                sheetName2 = "Inbound Arrival Situation";
            }
            WriteSheet build1 = EasyExcel.writerSheet(0, sheetName1).head(inboundReceiptExportHead()).build();
            excelWriter.write(list, build1);

            WriteSheet build2 = EasyExcel.writerSheet(1, sheetName2).head(inboundTrackingExportHead()).build();
            excelWriter.write(inboundTrackingVOS, build2);

            excelWriter.finish();
            outputStream.flush();
        } catch (IOException e) {
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
        INBOUND_RECEIPT_EXPORT_HEAD_ZH = Arrays.asList("入库单号", "采购单号", "送货方式", "快递单号/揽收单号", "状态", "目的仓库", "入库方式", "SKU", "初始数量", "到仓数量", "原产品编码", "下单时间", "到仓时间", "审核备注", "客户备注", "销售VAT");
        INBOUND_RECEIPT_EXPORT_HEAD_EN = Arrays.asList("Inbound Orders", "Purchase Orders", "Delivery Method", "Tracking Nu of pickup", "Status", "Destinatuon Warehouse", "Inbound Method", "SKU", "Order Qty", "Put on sale Qty", "Original product Number", "Order Create Time", "Arriving time", "System Note", "Note", "VAT Nu");

        INBOUND_TRACKING_EXPORT_HEAD_EN = Arrays.asList("Inbound Orders", "Tracking Nu of pickup", "Receipt Status", "Operating Time");
        INBOUND_TRACKING_EXPORT_HEAD_ZH = Arrays.asList("入库单号", "快递单号/揽收单号", "收货状态", "操作时间");
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
    @ApiOperation(value = "统计", notes = "入库单统计")
    public R<List<InboundCountVO>> statistics(InboundReceiptQueryDTO queryDTO) {
        List<InboundCountVO> statistics = iInboundReceiptService.statistics(queryDTO);
        return R.ok(statistics);
    }

    @GetMapping("/receipt/queryRecord")
    @ApiOperation(value = "日志", notes = "入库单日志")
    public TableDataInfo<InboundReceiptRecord> queryRecord(InboundReceiptRecordQueryDTO queryDTO) {
        startPage();
        return getDataTable(iInboundReceiptRecordService.selectList(queryDTO));
    }

    @PostMapping("/receiving/tracking")
    @ApiOperation(value = "#B5 物流到货接收确认", notes = "#B5 物流到货接收确认")
    R tracking(@Validated @RequestBody ReceivingTrackingRequest receivingCompletedRequest) {
        String localKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InboundReceiptController#tracking" + localKey);
        try {
            if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                iInboundReceiptService.tracking(receivingCompletedRequest);
                return R.ok();
            } else {
                return R.failed("物流到货接收确认等待超时,请稍候重试!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("物流到货接收确认处理异常!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:querySkuStockByRange')")
    @PostMapping("/querySkuStockByRange")
    @ApiOperation(value = "查询sku的入库状况", notes = "查询sku的入库状况-指定范围内")
    public R<List<SkuInventoryStockRangeVo>> querySkuStockByRange(@RequestBody @Validated InventoryStockByRangeDTO inventoryStockByRangeDTO) {
        inventoryStockByRangeDTO.valid();
        return R.ok(iInboundReceiptService.querySkuStockByRange(inventoryStockByRangeDTO));
    }
    @PreAuthorize("@ss.hasPermi('inventory:querySkuStockByRange')")
    @PostMapping("/collectAndInbound")
    @ApiOperation(value = "揽收入库", notes = "查询sku的入库状况-指定范围内")
    public R<InboundReceiptInfoVO> collectAndInbound(@RequestBody @Validated PackageCollection packageCollection) {
        return R.ok(iInboundReceiptService.collectAndInbound(packageCollection));
    }


}
