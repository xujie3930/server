package com.szmsd.chargerules.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.cache.Ehcache;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.szmsd.chargerules.config.AnalysisListenerAbstract;
import com.szmsd.chargerules.config.DownloadTemplateUtil;
import com.szmsd.chargerules.config.IRemoteApi;
import com.szmsd.chargerules.config.LocalDateTimeConvert;
import com.szmsd.chargerules.domain.WarehouseOperationDetails;
import com.szmsd.chargerules.dto.ChaOperationDetailsDTO;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.dto.OperationQueryDTO;
import com.szmsd.chargerules.dto.WarehouseOperationDTO;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.chargerules.service.IWarehouseOperationService;
import com.szmsd.chargerules.vo.ChaOperationListVO;
import com.szmsd.chargerules.vo.WarehouseOperationVo;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Api(tags = {"仓储业务计费规则"})
@RestController
@RequestMapping("/warehouseOperation")
public class WarehouseOperationController extends BaseController {

    @Resource
    private IWarehouseOperationService warehouseOperationService;

    @Resource
    private IRemoteApi iRemoteApi;

    @PreAuthorize("@ss.hasPermi('WarehouseOperation:WarehouseOperation:add')")
    @ApiOperation(value = "仓储业务计费规则 - 保存")
    @PostMapping("/save")
    public R save(@RequestBody WarehouseOperationDTO dto) {
        return toOk(warehouseOperationService.save(dto));
    }

    @PreAuthorize("@ss.hasPermi('WarehouseOperation:WarehouseOperation:edit')")
    @ApiOperation(value = "仓储业务计费规则 - 修改")
    @PutMapping("/update")
    public R update(@RequestBody WarehouseOperationDTO dto) {
        return toOk(warehouseOperationService.update(dto));
    }

    @PreAuthorize("@ss.hasPermi('WarehouseOperation:WarehouseOperation:list')")
    @ApiOperation(value = "仓储业务计费规则 - 分页查询")
    @GetMapping("/list")
    @AutoValue
    public TableDataInfo<WarehouseOperationVo> listPage(WarehouseOperationDTO dto) {
        startPage();
        List<WarehouseOperationVo> list = warehouseOperationService.listPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('WarehouseOperation:WarehouseOperation:details')")
    @ApiOperation(value = "仓储业务计费规则 - 详情")
    @GetMapping("/details/{id}")
    @AutoValue
    public R<WarehouseOperationVo> details(@PathVariable int id) {
        return R.ok(warehouseOperationService.details(id));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundCharge')")
    @ApiOperation(value = "仓储业务计费规则 - 下载模版")
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "ChargeOperation");
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundCharge')")
    @ApiOperation(value = "仓储业务计费规则 - 下载")
    @PostMapping("/download")
    public void download(HttpServletResponse httpServletResponse,  OperationQueryDTO operationQueryDTO) {
        ExcelWriter excelWriter = null;
        try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            String fileName = "ChargeOperation" + System.currentTimeMillis();
            String efn = URLEncoder.encode(fileName, "utf-8");
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + efn + ".xlsx");
            excelWriter = EasyExcel.write(/*outputStream*/new File("C:\\Users\\11\\Downloads\\ChargeOperation001.xlsx")).build();
            WriteSheet build1 = EasyExcel.writerSheet(0, "基础信息").head(OperationDTO.class).registerConverter(new LocalDateTimeConvert()).build();
//            WriteSheet build2 = EasyExcel.writerSheet(1,"详细信息").head(ChaOperationDetailsDTO.class).build();
//            excelWriter.write(new ArrayList(),0)
            List<ChaOperationListVO> chaOperationListVOS = new ArrayList<>();
            List<OperationDTO> collect = chaOperationListVOS.stream().map(x -> {
                OperationDTO chaOperationVO = new OperationDTO();
                BeanUtil.copyProperties(x, chaOperationVO);
                return chaOperationVO;
            }).collect(Collectors.toList());
            excelWriter.write(collect, build1);
//            excelWriter.write(new ArrayList(), build2);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != excelWriter)
                excelWriter.finish();
        }
    }

    @SneakyThrows
    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundCharge')")
    @ApiOperation(value = "仓储业务计费规则 - 导入")
    @PostMapping("/upload")
    public R upload(@RequestPart("file") MultipartFile multipartFile) {
        ExcelReader excelReader = null;
        try (InputStream inputStream = multipartFile.getInputStream()) {
            excelReader = EasyExcel.read(inputStream).readCache(new Ehcache(5)).build();

            AnalysisListenerAbstract<WarehouseOperationDTO> listener0 = new AnalysisListenerAbstract<>();
            ReadSheet readSheet = EasyExcel.readSheet(0).head(WarehouseOperationDTO.class)
                    .registerConverter(new LocalDateTimeConvert()).registerReadListener(listener0).build();

            AnalysisListenerAbstract<WarehouseOperationDetails> listener1 = new AnalysisListenerAbstract<>();
            ReadSheet readSheet1 = EasyExcel.readSheet(1).head(WarehouseOperationDetails.class).registerReadListener(listener1).build();

            excelReader.read(readSheet, readSheet1);
            excelReader.finish();
            List<WarehouseOperationDTO> warehouseOperationDTOList = listener0.getResultList();
            List<WarehouseOperationDetails> chaOperationDetailsDTOList = listener1.getResultList();
            Map<Integer, List<WarehouseOperationDetails>> detailMap = chaOperationDetailsDTOList.stream().collect(Collectors.groupingBy(WarehouseOperationDetails::getWarehouseOperationId));
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            StringBuilder errorMsg = new StringBuilder();
            AtomicInteger index = new AtomicInteger(1);

            warehouseOperationDTOList.forEach(x -> {
                // 设置替换参数
                int indexThis = index.getAndIncrement();
                Set<ConstraintViolation<WarehouseOperationDTO>> validate = validator.validate(x, Default.class);
                String error = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
                if (StringUtils.isNotBlank(error)) {
                    errorMsg.append(String.format("请检查第%s条数据:%s\r", indexThis, error));
                    return;
                }
                try {
                    this.save(x);
                } catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    errorMsg.append(String.format("第%s条数据业务异常:%s\r", indexThis, message));
                }
            });
            AssertUtil.isTrue(StringUtils.isBlank(errorMsg.toString()), errorMsg.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != excelReader)
                excelReader.finish();
        }
        return R.ok();
    }
}
