package com.szmsd.bas.controller;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.api.domain.BasMain;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.service.IBasMainService;
import com.szmsd.bas.service.IBasSubService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-06-18
 */

@Api(tags = {"主类别模块"})
@RestController
@RequestMapping("/bas-main")
public class BasMainController extends BaseController {


    @Resource
    private IBasMainService basMainService;

    @Resource
    private IBasSubService basSubService;
    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basmain:list')")
    @ApiOperation(value = "查询主类别列表", notes = "查询主类别列表")
    @GetMapping("/list")
    public TableDataInfo list(BasMain basMain) {
        startPage();
        List<BasMain> list = basMainService.selectBasMainList(basMain);
        return getDataTable(list);
    }

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basmain:list')")
    @ApiOperation(value = "查询主类别列表", notes = "查询主类别列表")
    @GetMapping("/lists")
    public TableDataInfo lists(BasMain basMain) {
        List<BasMain> list = basMainService.selectBasMainList(basMain);
        return getDataTable(list);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basmain:export')")
    @ApiOperation(value = "导出主类别列表", notes = "导出主类别列表")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasMain basMain) throws IOException {
        List<BasMain> list = basMainService.selectBasMainList(basMain);
        ExcelUtil<BasMain> util = new ExcelUtil<BasMain>(BasMain.class);
        util.exportExcel(response, list, "BasMain");
    }

    /**
     * 获取模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('bas:basmain:query')")
    @ApiOperation(value = "查询主类别列表", notes = "查询主类别列表")
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(basMainService.selectBasMainById(id));
    }

    /**
     * 新增修改模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basmain:add')")
    @ApiOperation(value = "新增主类别列表", notes = "新增主类别列表")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasMain basMain) {
        if (basMain.getId() == null) {
            BasMain basMain1 = new BasMain();
            basMain1.setMainName(basMain.getMainName());
            List<BasMain> list = basMainService.selectBasMainList(basMain1);
            if (list.size() != 0) {
                return R.failed("主类别名称重复");
            }
            basMain.setCreateTime(new Date());
            basMainService.insertBasMain(basMain);
            Long t = basMain.getId();
            String s = String.format("%03d", t);
            basMain.setMainCode(s);
            basMainService.updateBasMain(basMain);
        }
        return R.ok();
    }


    /**
     * 修改主类别列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basmain:edit')")
    @Log(title = "修改主类别列表", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改主类别列表", notes = "修改主类别列表")
    @PutMapping
    public R edit(@RequestBody BasMain basMain) {
        BasSub basSub=new BasSub();
        basSub.setMainCode(basMain.getMainCode());
        List<BasSub> list = basSubService.selectBasSubList(basSub);
        if (list.size()!=0){
            for (BasSub basSub1 :list){
                basSub1.setMainName(basMain.getMainName());
                basSubService.updateBasSub(basSub1);
            }
        }
        basMain.setUpdateTime(new Date());
        return toOk(basMainService.updateBasMain(basMain));
    }

    @PreAuthorize("@ss.hasPermi('BasSub:BasSub:list')")
    @ApiOperation(value = "根据name，code查询主类别（下拉框）", notes = "根据name，code查询主类别列表（下拉框）")
    @GetMapping("/getSubName")
    public R list(String code, String name) {
        BasMain basMain = new BasMain();
        JSONObject data = new JSONObject();
        if (code.contains(",")) {
            String[] split = code.split(",");
            String[] names = name.split(",");
            for (int i = 0; i < split.length; i++) {
                basMain.setMainCode(split[i]);
                List<BasMain> list = basMainService.selectBasMainList(basMain);
                data.put(names[i], list);
            }
        } else {
            basMain.setMainCode(code);
            List<BasMain> list = basMainService.selectBasMainList(basMain);
            data.put(name, list);
        }
        return R.ok(data);
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basmain:remove')")
    @ApiOperation(value = "删除主类别列表", notes = "删除主类别列表")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basMainService.deleteBasMainByIds(ids));
    }

}
