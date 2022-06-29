package com.szmsd.delivery.controller;

import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.domain.dto.BasRegionSelectListQueryDto;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
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
import com.szmsd.delivery.imported.DefaultAnalysisEventListener;
import com.szmsd.delivery.imported.EasyExcelFactoryUtil;
import com.szmsd.delivery.imported.ImportMessage;
import com.szmsd.delivery.imported.ImportResult;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.delivery.vo.DelOutboundReassignExportListVO;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

    @PostMapping("/imported")
    @ApiOperation(value = "出库管理 - 退件重派导入", position = 300)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<ImportResult> imported(HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = multipartHttpServletRequest.getFile("file");
            AssertUtil.notNull(file, "上传文件不存在");
            byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
            DefaultAnalysisEventListener<DelOutboundReassignExportListVO> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), DelOutboundReassignExportListVO.class, 0, 1);
            if (defaultAnalysisEventListener.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
            }
            List<DelOutboundReassignExportListVO> dataList = defaultAnalysisEventListener.getList();
            if (CollectionUtils.isEmpty(dataList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据不能为空")));
            }
            Map<String, String> countryMap = new HashMap<>();
            R<List<BasRegionSelectListVO>> countryListR = this.basRegionFeignService.countryList(new BasRegionSelectListQueryDto());
            try {
                List<BasRegionSelectListVO> countryList = R.getDataAndException(countryListR);
                if (CollectionUtils.isNotEmpty(countryList)) {
                    for (BasRegionSelectListVO country : countryList) {
                        countryMap.put(country.getName(), country.getAddressCode());
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                countryMap.put("美国", "en");
                countryMap.put("安道尔", "an");
            }
            List<ImportMessage> messageList = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                DelOutboundReassignExportListVO vo = dataList.get(i);
                if (StringUtils.isEmpty(vo.getOrderNo())) {
                    messageList.add(new ImportMessage(i + 2, 3, null, "出库单号不能为空"));
                }
                if (StringUtils.isEmpty(vo.getShipmentRule())) {
                    messageList.add(new ImportMessage(i + 2, 8, null, "物流服务不能为空"));
                }
                if (StringUtils.isNotEmpty(vo.getStreet1()) && vo.getStreet1().length() > 500) {
                    messageList.add(new ImportMessage(i + 2, 10, null, "地址1不能超过500个字符"));
                }
                if (StringUtils.isNotEmpty(vo.getStreet2()) && vo.getStreet2().length() > 500) {
                    messageList.add(new ImportMessage(i + 2, 11, null, "地址2不能超过500个字符"));
                }
                if (StringUtils.isNotEmpty(vo.getStateOrProvince()) && vo.getStateOrProvince().length() > 50) {
                    messageList.add(new ImportMessage(i + 2, 12, null, "省份不能超过50个字符"));
                }
                if (StringUtils.isNotEmpty(vo.getCity()) && vo.getCity().length() > 50) {
                    messageList.add(new ImportMessage(i + 2, 13, null, "城市不能超过50个字符"));
                }
                if (StringUtils.isNotEmpty(vo.getPostCode()) && vo.getPostCode().length() > 50) {
                    messageList.add(new ImportMessage(i + 2, 14, null, "邮编不能超过50个字符"));
                }
                String country = vo.getCountry();
                if (StringUtils.isEmpty(country)) {
                    messageList.add(new ImportMessage(i + 2, 15, null, "国家不能为空"));
                } else {
                    String countryCode = countryMap.get(country);
                    if (StringUtils.isEmpty(countryCode)) {
                        messageList.add(new ImportMessage(i + 2, 15, country, "国家不存在"));
                    } else {
                        vo.setCountryCode(countryCode);
                    }
                }
                if (StringUtils.isNotEmpty(vo.getPhoneNo()) && vo.getPhoneNo().length() > 50) {
                    messageList.add(new ImportMessage(i + 2, 16, null, "电话不能超过50个字符"));
                }
                if (StringUtils.isNotEmpty(vo.getEmail()) && vo.getEmail().length() > 200) {
                    messageList.add(new ImportMessage(i + 2, 17, null, "邮箱不能超过200个字符"));
                }
                if (StringUtils.isNotEmpty(vo.getIoss()) && vo.getIoss().length() > 50) {
                    messageList.add(new ImportMessage(i + 2, 19, null, "增值税号不能超过50个字符"));
                }
                // 100
                if (messageList.size() > 100) {
                    break;
                }
            }
            if (CollectionUtils.isNotEmpty(messageList)) {
                return R.ok(ImportResult.buildFail(messageList));
            }
            int i = delOutboundService.updateReassignImportedData(dataList);
            return R.ok(ImportResult.buildSuccess(ImportMessage.build("导入成功，修改数据条数：" + i)));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 返回失败的结果
            return R.ok(ImportResult.buildFail(ImportMessage.build(e.getMessage())));
        }
    }
}
