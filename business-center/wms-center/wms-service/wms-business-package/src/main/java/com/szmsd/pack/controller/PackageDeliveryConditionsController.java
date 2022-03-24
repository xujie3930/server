package com.szmsd.pack.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.cache.Ehcache;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.szmsd.chargerules.domain.WarehouseOperationDetails;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.pack.config.AnalysisListenerAbstract;
import com.szmsd.pack.config.LocalDateTimeConvert;
import com.szmsd.pack.domain.PackageDeliveryConditions;
import com.szmsd.pack.dto.PackageDeliveryConditionsDTO;
import com.szmsd.pack.service.IPackageDeliveryConditionsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * <p>
 * 发货条件表 前端控制器
 * </p>
 *
 * @author admpon
 * @since 2022-03-23
 */


@Api(tags = {"发货条件表"})
@RestController
@RequestMapping("/package-delivery-conditions")
public class PackageDeliveryConditionsController extends BaseController {

    @Resource
    private IPackageDeliveryConditionsService packageDeliveryConditionsService;

    /**
     * 查询发货条件表模块列表
     */
    @PreAuthorize("@ss.hasPermi('PackageDeliveryConditions:PackageDeliveryConditions:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询发货条件表模块列表", notes = "查询发货条件表模块列表")
    public TableDataInfo list(PackageDeliveryConditions packageDeliveryConditions) {
        startPage();
        List<PackageDeliveryConditions> list = packageDeliveryConditionsService.selectPackageDeliveryConditionsList(packageDeliveryConditions);
        return getDataTable(list);
    }

    /**
     * 导出发货条件表模块列表
     */
    @PreAuthorize("@ss.hasPermi('PackageDeliveryConditions:PackageDeliveryConditions:export')")
    @Log(title = "发货条件表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出发货条件表模块列表", notes = "导出发货条件表模块列表")
    public void export(HttpServletResponse response, PackageDeliveryConditions packageDeliveryConditions) throws IOException {
        List<PackageDeliveryConditions> list = packageDeliveryConditionsService.selectPackageDeliveryConditionsList(packageDeliveryConditions);
        ExcelUtil<PackageDeliveryConditions> util = new ExcelUtil<PackageDeliveryConditions>(PackageDeliveryConditions.class);
        util.exportExcel(response, list, "PackageDeliveryConditions");

    }

    /**
     * 获取发货条件表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('PackageDeliveryConditions:PackageDeliveryConditions:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取发货条件表模块详细信息", notes = "获取发货条件表模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(packageDeliveryConditionsService.selectPackageDeliveryConditionsById(id));
    }

    /**
     * 新增发货条件表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageDeliveryConditions:PackageDeliveryConditions:add')")
    @Log(title = "发货条件表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增发货条件表模块", notes = "新增发货条件表模块")
    public R add(@RequestBody PackageDeliveryConditions packageDeliveryConditions) {
        return toOk(packageDeliveryConditionsService.insertPackageDeliveryConditions(packageDeliveryConditions));
    }


    @PreAuthorize("@ss.hasPermi('PackageDeliveryConditions:PackageDeliveryConditions:add')")
    @Log(title = "发货条件表模块", businessType = BusinessType.INSERT)
    @PostMapping("save")
    @ApiOperation(value = "新增发货条件表模块", notes = "新增发货条件表模块")
    public R save(@RequestBody PackageDeliveryConditionsDTO packageDeliveryConditions) {
        return toOk(packageDeliveryConditionsService.save(packageDeliveryConditions));
    }

    /**
     * 修改发货条件表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageDeliveryConditions:PackageDeliveryConditions:edit')")
    @Log(title = "发货条件表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改发货条件表模块", notes = "修改发货条件表模块")
    public R edit(@RequestBody PackageDeliveryConditions packageDeliveryConditions) {
        return toOk(packageDeliveryConditionsService.updatePackageDeliveryConditions(packageDeliveryConditions));
    }

    /**
     * 删除发货条件表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageDeliveryConditions:PackageDeliveryConditions:remove')")
    @Log(title = "发货条件表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除发货条件表模块", notes = "删除发货条件表模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(packageDeliveryConditionsService.deletePackageDeliveryConditionsByIds(ids));
    }

    @SneakyThrows
    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundCharge')")
    @ApiOperation(value = "仓储业务计费规则 - 导入")
    @PostMapping("/upload")
    public R upload(@RequestPart("file") MultipartFile multipartFile) {
        ExcelReader excelReader = null;
        try (InputStream inputStream = multipartFile.getInputStream()) {
            excelReader = EasyExcel.read(inputStream).readCache(new Ehcache(5)).build();

            AnalysisListenerAbstract<PackageDeliveryConditionsDTO> listener0 = new AnalysisListenerAbstract<>();
            ReadSheet readSheet = EasyExcel.readSheet(0).head(PackageDeliveryConditionsDTO.class)
                    .registerConverter(new LocalDateTimeConvert()).registerReadListener(listener0).build();

            AnalysisListenerAbstract<WarehouseOperationDetails> listener1 = new AnalysisListenerAbstract<>();
            ReadSheet readSheet1 = EasyExcel.readSheet(1).head(WarehouseOperationDetails.class).registerReadListener(listener1).build();

            excelReader.read(readSheet, readSheet1);
            excelReader.finish();
            List<PackageDeliveryConditionsDTO> warehouseOperationDTOList = listener0.getResultList();
            List<WarehouseOperationDetails> chaOperationDetailsDTOList = listener1.getResultList();
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            StringBuilder errorMsg = new StringBuilder();
            AtomicInteger index = new AtomicInteger(1);

            warehouseOperationDTOList.forEach(x -> {
                // 设置替换参数
                int indexThis = index.getAndIncrement();
                Set<ConstraintViolation<PackageDeliveryConditionsDTO>> validate = validator.validate(x, Default.class);
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
            throw new RuntimeException(e);
        } finally {
            if (null != excelReader)
                excelReader.finish();
        }
        return R.ok();
    }

}
