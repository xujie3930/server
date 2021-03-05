package com.szmsd.bas.controller;

import com.szmsd.bas.api.feign.SerialNumberFeignService;
import com.szmsd.bas.domain.BasSerialNumber;
import com.szmsd.bas.dto.GenerateNumberDto;
import com.szmsd.bas.service.IBasSerialNumberService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 流水号信息 前端控制器
 * </p>
 *
 * @author gen
 * @since 2020-11-10
 */
@Api(tags = {"流水号信息"})
@RestController
@RequestMapping("/bas-serial-number")
public class BasSerialNumberController extends BaseController {

    @Resource
    private IBasSerialNumberService baseSerialNumberService;
    @Autowired
    private SerialNumberFeignService serialNumberFeignService;

    /**
     * 查询流水号信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('BaseSerialNumber:BaseSerialNumber:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询流水号信息模块列表", notes = "查询流水号信息模块列表")
    public TableDataInfo<?> list(BasSerialNumber baseSerialNumber) {
        startPage();
        List<BasSerialNumber> list = baseSerialNumberService.selectBaseSerialNumberList(baseSerialNumber);
        return getDataTable(list);
    }

    /**
     * 导出流水号信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('BaseSerialNumber:BaseSerialNumber:export')")
    @Log(title = "流水号信息模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出流水号信息模块列表", notes = "导出流水号信息模块列表")
    public void export(HttpServletResponse response, BasSerialNumber baseSerialNumber) throws IOException {
        List<BasSerialNumber> list = baseSerialNumberService.selectBaseSerialNumberList(baseSerialNumber);
        ExcelUtil<BasSerialNumber> util = new ExcelUtil<BasSerialNumber>(BasSerialNumber.class);
        util.exportExcel(response, list, "BaseSerialNumber");
    }

    /**
     * 获取流水号信息模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('BaseSerialNumber:BaseSerialNumber:query')")
    @GetMapping(value = "/getInfo/{id}")
    @ApiOperation(value = "获取流水号信息模块详细信息", notes = "获取流水号信息模块详细信息")
    public R<?> getInfo(@PathVariable("id") String id) {
        return R.ok(baseSerialNumberService.selectBaseSerialNumberById(id));
    }

    /**
     * 新增流水号信息模块
     */
    @PreAuthorize("@ss.hasPermi('BaseSerialNumber:BaseSerialNumber:add')")
    @Log(title = "流水号信息模块", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation(value = "新增流水号信息模块", notes = "新增流水号信息模块")
    public R<?> add(@RequestBody BasSerialNumber baseSerialNumber) {
        return toOk(baseSerialNumberService.insertBaseSerialNumber(baseSerialNumber));
    }

    /**
     * 修改流水号信息模块
     */
    @PreAuthorize("@ss.hasPermi('BaseSerialNumber:BaseSerialNumber:edit')")
    @Log(title = "流水号信息模块", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    @ApiOperation(value = " 修改流水号信息模块", notes = "修改流水号信息模块")
    public R<?> edit(@RequestBody BasSerialNumber baseSerialNumber) {
        return toOk(baseSerialNumberService.updateBaseSerialNumber(baseSerialNumber));
    }

    /**
     * 删除流水号信息模块
     */
    @PreAuthorize("@ss.hasPermi('BaseSerialNumber:BaseSerialNumber:remove')")
    @Log(title = "流水号信息模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove")
    @ApiOperation(value = "删除流水号信息模块", notes = "删除流水号信息模块")
    public R<?> remove(@RequestBody List<String> ids) {
        return toOk(baseSerialNumberService.deleteBaseSerialNumberByIds(ids));
    }

    @PostMapping(value = "/generateNumber")
    @ApiOperation(value = "生成流水号")
    @ApiImplicitParam(name = "dto", value = "业务编号", required = true, dataType = "GenerateNumberDto")
    public R<String> generateNumber(@RequestBody @Validated GenerateNumberDto dto) {
        return R.ok(this.baseSerialNumberService.generateNumber(dto.getCode()));
    }

    @PostMapping(value = "/generateNumbers")
    @ApiOperation(value = "生成流水号 - 批量")
    @ApiImplicitParam(name = "dto", value = "业务编号", required = true, dataType = "GenerateNumberDto")
    public R<List<String>> generateNumbers(@RequestBody @Validated GenerateNumberDto dto) {
        return R.ok(this.baseSerialNumberService.generateNumbers(dto.getCode(), dto.getNum()));
    }

    @PostMapping(value = "/generateNumberTest")
    @ApiOperation(value = "生成流水号 - 测试")
    @ApiImplicitParam(name = "dto", value = "业务编号", required = true, dataType = "GenerateNumberDto")
    public R<String> generateNumberTest(@RequestBody @Validated GenerateNumberDto dto) {
        return this.serialNumberFeignService.generateNumber(dto);
    }
}
