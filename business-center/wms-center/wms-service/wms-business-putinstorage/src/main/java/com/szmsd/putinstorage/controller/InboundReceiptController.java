package com.szmsd.putinstorage.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.dto.ReceivingRequest;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import com.szmsd.putinstorage.service.IInboundReceiptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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

    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @GetMapping("/receipt/page")
    @ApiOperation(value = "查询", notes = "入库管理 - 分页查询")
    public TableDataInfo<InboundReceiptVO> page(InboundReceiptQueryDTO queryDTO) {
        startPage();
        List<InboundReceiptVO> list = iInboundReceiptService.selectList(queryDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @PostMapping("/receipt/create")
    @ApiOperation(value = "创建", notes = "入库管理 - 新增 - 提交")
    public R create(@RequestBody CreateInboundReceiptDTO createInboundReceiptDTO) {
        iInboundReceiptService.create(createInboundReceiptDTO);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @DeleteMapping("/receipt/cancel/{warehouseNo}")
    @ApiOperation(value = "取消", notes = "入库管理 - 取消")
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

    @PreAuthorize("@ss.hasPermi('inbound:receipt:importdetail')")
    @PostMapping("/receipt/importDetail")
    @ApiOperation(value = "导入明细", notes = "入库管理 - 新增 - 导入")
    public R<List<InboundReceiptDetailVO>> importDetail(MultipartFile excel) {
        AssertUtil.isTrue(ObjectUtils.allNotNull(excel), "上传文件不存在");
        String originalFilename = excel.getOriginalFilename();
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf + 1);
        boolean isXls = "xls".equals(suffix);
        boolean isXlsx = "xlsx".equals(suffix);
        AssertUtil.isTrue(isXls || isXlsx, "请上传xls或xlsx文件");
        try {
            ExcelUtil<InboundReceiptDetailVO> excelUtil = new ExcelUtil<>(InboundReceiptDetailVO.class);
            List<InboundReceiptDetailVO> inboundReceiptDetailVOS = excelUtil.importExcel(excel.getInputStream());
            Map<String, Long> collect = inboundReceiptDetailVOS.stream().map(InboundReceiptDetailVO::getSku).collect(Collectors.groupingBy(p -> p, Collectors.counting()));
            collect.entrySet().forEach(item -> AssertUtil.isTrue(!(item.getValue() > 1L), "Excel存在重复SKU"));
            return R.ok(inboundReceiptDetailVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("文件解析异常");
        }
    }

    @PreAuthorize("@ss.hasPermi('inbound:receiving')")
    @PostMapping("/receiving")
    @ApiOperation(value = "#B1 接收入库上架", notes = "#B1 接收入库上架")
    public R receiving(@RequestBody ReceivingRequest receivingRequest) {
        iInboundReceiptService.receiving(receivingRequest);
        return R.ok();
    }

}
