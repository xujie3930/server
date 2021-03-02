package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasFormula;
import com.szmsd.bas.service.IBasFormulaService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 公式表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-07-08
 */

@Api(tags = {"公式表"})
@RestController
@RequestMapping("/bas-formula")
public class BasFormulaController extends BaseController {


    @Resource
    private IBasFormulaService basFormulaService;

    /**
     * 查询公式表模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basformula:list')")
    @ApiOperation(value = "查询公式列表", notes = "查询公式列表('bas:basformula:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasFormula basFormula) {
        List<BasFormula> list = basFormulaService.selectBasFormulaList(basFormula);
        return getDataTable(list);
    }

    /**
     * 新增修改模块
     */
    @PreAuthorize("@ss.hasPermi('BasKeyword:BasKeyword:add')")
    @ApiOperation(value = "新增or修改公式列表", notes = "新增or修改公式列表('BasKeyword:BasKeyword:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody List<BasFormula> basFormulaList) {
        basFormulaList.stream().forEach(item -> {
            if (item.getId() == null) {
                String uid = UUID.randomUUID().toString().substring(0, 8);
                item.setCalculateFormula(item.getCalculateFormula().toLowerCase());
                item.setWeight(item.getWeight().toLowerCase());
                item.setId(uid);
                basFormulaService.insertBasFormula(item);
            } else if (item.getId() != null) {
                item.setCalculateFormula(item.getCalculateFormula().toLowerCase());
                item.setWeight(item.getWeight().toLowerCase());
                basFormulaService.updateBasFormula(item);
            }
        });
        return R.ok();
    }

    /**
     * 删除公式表模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basformula:remove')")
    @ApiOperation(value = "删除公式列表", notes = "删除公式列表('bas:basformula:remove')")
    @Log(title = "公式表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basFormulaService.deleteBasFormulaByIds(ids));
    }

}
