package com.szmsd.delivery.controller;

import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.utils.ExcelUtils;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.exported.DelOutboundReassignExportContext;
import com.szmsd.delivery.exported.DelOutboundReassignExportQueryPage;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.delivery.vo.DelOutboundReassignExportListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 出库管理 - 重派
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"出库管理 - 重派"})
@ApiSort(100)
@RestController
@RequestMapping("/api/outbound-reassign")
public class DelOutboundReassignController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(DelOutboundReassignController.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private BasSubClientService basSubClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundReassign:list')")
    @PostMapping("/page")
    @ApiOperation(value = "出库管理 - 分页", position = 100)
    @AutoValue
    public TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryDto queryDto) {
        startPage(queryDto);
        queryDto.setReassignType(DelOutboundConstant.REASSIGN_TYPE_Y);
        return getDataTable(this.delOutboundService.selectDelOutboundList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:export')")
    @Log(title = "出库管理 - 退件重派导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "出库管理 - 退件重派导出", position = 200)
    public void export(HttpServletResponse response, @RequestBody DelOutboundListQueryDto queryDto) {
        try {
            String len = getLen();
            // 查询数据字典，063 出库方式，065 状态，100 动作节点（轨迹状态）
            Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("063,065,100");
            // 查询仓库信息，区域信息
            DelOutboundReassignExportContext exportContext = new DelOutboundReassignExportContext(this.basWarehouseClientService, this.basRegionFeignService, len);
            exportContext.setStateCacheAdapter(listMap.get("065"));
            exportContext.setOrderTypeCacheAdapter(listMap.get("063"));
            exportContext.setTrackingStatusCacheAdapter(listMap.get("100"));
            QueryDto queryDto1 = new QueryDto();
            queryDto1.setPageNum(1);
            queryDto1.setPageSize(500);
            queryDto.setReassignType(DelOutboundConstant.REASSIGN_TYPE_Y);
            QueryPage<DelOutboundReassignExportListVO> queryPage = new DelOutboundReassignExportQueryPage(queryDto, queryDto1, exportContext, this.delOutboundService);
            ExcelUtils.export(response,
                    null,
                    ExcelUtils.ExportExcel.build("en".equals(len) ? "Outbound_Return_Order" : "退件单重派",
                            len,
                            null,
                            new ExcelUtils.ExportSheet<DelOutboundReassignExportListVO>() {
                                @Override
                                public String sheetName() {

                                    if ("en".equals(len)) {
                                        return "Order Information";
                                    } else {
                                        return "退件单重派";
                                    }
                                }

                                @Override
                                public Class<DelOutboundReassignExportListVO> classType() {
                                    return DelOutboundReassignExportListVO.class;
                                }

                                @Override
                                public QueryPage<DelOutboundReassignExportListVO> query(ExcelUtils.ExportContext exportContext) {
                                    return queryPage;
                                }
                            }));
        } catch (Exception e) {
            log.error("导出异常:" + e.getMessage(), e);
        }
    }

}
