package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.domain.SpecialOperation;
import com.szmsd.chargerules.dto.SpecialOperationDTO;
import com.szmsd.chargerules.service.ISpecialOperationService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"特殊操作计费规则"})
@RestController
@RequestMapping("/specialOperation")
public class SpecialOperationController extends BaseController {

    @Resource
    private ISpecialOperationService specialOperationService;

    @PreAuthorize("@ss.hasPermi('SpecialOperation:SpecialOperation:add')")
    @ApiOperation(value = "特殊操作计费规则 - 保存")
    @PostMapping("/save")
    public R save(@RequestBody SpecialOperationDTO dto) {
        int save = 0;
        try {
            save = specialOperationService.save(dto);
        } catch (DuplicateKeyException e) {
            log.error(e.getMessage(), e);
            return R.failed("操作类型+仓库不能重复");
        }
        return toOk(save);
    }

    @PreAuthorize("@ss.hasPermi('SpecialOperation:SpecialOperation:edit')")
    @ApiOperation(value = "特殊操作计费规则 - 修改")
    @PutMapping("/update")
    public R update(@RequestBody SpecialOperation dto) {
        return toOk(specialOperationService.update(dto));
    }

    @PreAuthorize("@ss.hasPermi('SpecialOperation:SpecialOperation:list')")
    @ApiOperation(value = "特殊操作计费规则 - 分页查询")
    @GetMapping("/list")
    public TableDataInfo<SpecialOperation> listPage(SpecialOperationDTO dto) {
        startPage();
        List<SpecialOperation> list = specialOperationService.listPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('SpecialOperation:SpecialOperation:details')")
    @ApiOperation(value = "特殊操作计费规则 - 详情")
    @GetMapping("/details/{id}")
    public R<SpecialOperation> details(@PathVariable int id) {
        return R.ok(specialOperationService.details(id));
    }

}
