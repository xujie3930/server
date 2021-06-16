package com.szmsd.putinstorage.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import com.szmsd.bas.dto.BaseProductMeasureDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.putinstorage.annotation.InboundReceiptLog;
import com.szmsd.putinstorage.component.CheckTag;
import com.szmsd.putinstorage.component.RemoteComponent;
import com.szmsd.putinstorage.domain.InboundReceiptRecord;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.*;
import com.szmsd.putinstorage.enums.InboundReceiptRecordEnum;
import com.szmsd.putinstorage.service.IInboundReceiptRecordService;
import com.szmsd.putinstorage.service.IInboundReceiptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**iInboundReceiptService
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

    @Resource
    private IInboundReceiptService iInboundReceiptService;

    @Resource
    private IInboundReceiptRecordService iInboundReceiptRecordService;

    @Resource
    private RemoteComponent remoteComponent;

    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @GetMapping("/receipt/page")
    @ApiOperation(value = "查询", notes = "入库管理 - 分页查询")
    public TableDataInfo<InboundReceiptVO> page(InboundReceiptQueryDTO queryDTO) {
        startPage();
        List<InboundReceiptVO> list = iInboundReceiptService.selectList(queryDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @PostMapping("/receipt/saveOrUpdate")
    @ApiOperation(value = "创建/修改", notes = "入库管理 - 新增/创建")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.CREATE)
    public R<InboundReceiptInfoVO> saveOrUpdate(@RequestBody CreateInboundReceiptDTO createInboundReceiptDTO) {
        try {
            return R.ok(iInboundReceiptService.saveOrUpdate(createInboundReceiptDTO));
        } finally {
            CheckTag.remove();
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @DeleteMapping("/receipt/cancel/{warehouseNo}")
    @ApiOperation(value = "取消", notes = "入库管理 - 取消")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.CANCEL)
    public R cancel(@PathVariable("warehouseNo") String warehouseNo) {
        iInboundReceiptService.cancel(warehouseNo);
        return R.ok();
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
        List<String> rows = CollUtil.newArrayList("SKU", "申报数量", "原产品编码", "备注");
        super.excelExportTitle(response, rows, "入库单SKU导入");
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
            response.setHeader("Content-Disposition" , "attachment;filename=" + fileName + ".xls");
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
        List<InboundReceiptDetailVO> inboundReceiptDetailVOS;
        try {
            ExcelUtil<InboundReceiptDetailVO> excelUtil = new ExcelUtil<>(InboundReceiptDetailVO.class);
            inboundReceiptDetailVOS = excelUtil.importExcel(file.getInputStream());
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
        iInboundReceiptService.receiving(receivingRequest);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:receiving:completed')")
    @PostMapping("/receiving/completed")
    @ApiOperation(value = "#B3 接收完成入库", notes = "#B3 接收完成入库")
    @InboundReceiptLog(record = InboundReceiptRecordEnum.COMPLETED)
    public R completed(@RequestBody ReceivingCompletedRequest receivingCompletedRequest) {
        iInboundReceiptService.completed(receivingCompletedRequest);
        return R.ok();
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
    public void export(@RequestBody InboundReceiptQueryDTO queryDTO, HttpServletResponse response) {
        List<InboundReceiptExportVO> list = iInboundReceiptService.selectExport(queryDTO);
        ExcelUtil<InboundReceiptExportVO> util = new ExcelUtil<>(InboundReceiptExportVO.class);
        util.exportExcel(response, list, "入库单导出_" + DateUtils.dateTimeNow());
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
    public R<List<InboundReceiptRecord>> queryRecord(InboundReceiptRecordQueryDTO queryDTO) {
        return R.ok(iInboundReceiptRecordService.selectList(queryDTO));
    }

}
