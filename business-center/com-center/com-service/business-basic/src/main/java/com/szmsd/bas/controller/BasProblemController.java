package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasProblem;
import com.szmsd.bas.service.IBasProblemService;
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
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 问题件记录表	 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-08-24
 */

@Api(tags = {"问题件记录表	"})
@RestController
@RequestMapping("/bas-problem")
public class BasProblemController extends BaseController {


    @Resource
    private IBasProblemService basProblemService;

    /**
     * 查询问题件记录表	模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basproblem:list')")
    @GetMapping("/list")
    @ApiOperation(value = "问题件类型查询", notes = "问题件类型查询('bas:basproblem:list')")
    public TableDataInfo list(BasProblem basProblem) {
        startPage();
        List<BasProblem> list = basProblemService.selectBasProblemList(basProblem);
        return getDataTable(list);
    }

    /**
     * 查询问题件记录表	模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basproblem:lists')")
    @GetMapping("/lists")
    @ApiOperation(value = "问题件类型查询APP", notes = "问题件类型查询APP('bas:basproblem:lists')")
    public R lists(BasProblem basProblem) {
        List<BasProblem> list = basProblemService.selectBasProblemList(basProblem);
        return R.ok(list);
    }

    /**
     * 新增问题件记录表模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basproblem:add')")
    @Log(title = "问题件记录表模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasProblem basProblem) {
        basProblem.setCreateTime(new Date());
        basProblemService.insertBasProblem(basProblem);
        return R.ok();
    }

    /**
     * 修改问题件记录表模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basproblem:edit')")
    @Log(title = "问题件记录表模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasProblem basProblem) {
        return toOk(basProblemService.updateBasProblem(basProblem));
    }

    /**
     * 删除问题件记录表	模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basproblem:remove')")
    @Log(title = "问题件记录表	模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basProblemService.deleteBasProblemByIds(ids));
    }

}
