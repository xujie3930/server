package com.szmsd.putinstorage.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import com.szmsd.putinstorage.service.IInboundReceiptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * rec_wareh - 入库 前端控制器
 * </p>
 *
 * @author liangchao
 * @since 2021-03-03
 */


@Api(tags = {"入库"})
@RestController
@RequestMapping("/inbound/receipt")
public class InboundReceiptController extends BaseController {

    @Resource
    private IInboundReceiptService inboundReceiptService;

    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @GetMapping("/page")
    @ApiOperation(value = "查询", notes = "入库管理 - 分页查询")
    public TableDataInfo<InboundReceiptVO> page(InboundReceiptQueryDTO queryDTO) {
        startPage();
        List<InboundReceiptVO> list = inboundReceiptService.selectList(queryDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @PostMapping("/create")
    @ApiOperation(value = "创建", notes = "入库管理 - 新增 - 提交")
    public R create(@RequestBody CreateInboundReceiptDTO createInboundReceiptDTO) {
        inboundReceiptService.create(createInboundReceiptDTO);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:create')")
    @DeleteMapping("/cancel/{warehouseNo}")
    @ApiOperation(value = "取消", notes = "入库管理 - 取消")
    public R cancel(@PathVariable("warehouseNo") String warehouseNo) {
        inboundReceiptService.cancel(warehouseNo);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:info')")
    @GetMapping("/info/{warehouseNo}")
    @ApiOperation(value = "详情", notes = "入库管理 - 详情（包含明细）")
    public R<InboundReceiptInfoVO> info(@PathVariable("warehouseNo") String warehouseNo) {
        InboundReceiptInfoVO inboundReceiptInfoVO = inboundReceiptService.queryInfo(warehouseNo);
        return R.ok(inboundReceiptInfoVO);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:importdetail')")
    @PostMapping("/importDetail")
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

}
