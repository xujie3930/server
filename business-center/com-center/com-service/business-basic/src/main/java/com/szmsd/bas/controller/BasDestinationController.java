package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasDestination;
import com.szmsd.bas.service.IBasDestinationService;
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
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-07-07
 */

@Api(tags = {"目的地维护模块"})
@RestController
@RequestMapping("/bas-destination")
public class BasDestinationController extends BaseController {


    @Resource
    private IBasDestinationService basDestinationService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basdestination:list')")
    @ApiOperation(value = "查询目的地列表", notes = "查询目的地列表('bas:basdestination:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasDestination basDestination) {
        startPage();
        List<BasDestination> list = basDestinationService.selectBasDestinationList(basDestination);
        return getDataTable(list);
    }

    /**
     * 查询模块列表fegin
     */
    @ApiOperation(value = "查询目的地列表", notes = "查询目的地列表")
    @PostMapping("/getdstination")
    public R<List<BasDestination>> getdstination(BasDestination basDestination) {
        List<BasDestination> list = basDestinationService.selectBasDestinationList(basDestination);
        return R.ok(list);
    }


    /**
     * 查询模块列表
     */
    @ApiOperation(value = "查询目的地列表APP", notes = "查询目的地列表APP")
    @GetMapping("/lists")
    public R lists(BasDestination basDestination) {
        List<BasDestination> list = basDestinationService.selectBasDestinationList(basDestination);
        List<Map<String, Object>> lists = new ArrayList<>();
        for (BasDestination basSub1 : list) {
            Map<String, Object> maps = new HashMap<>();
            maps.put("name", basSub1.getRegionName());
            maps.put("code", basSub1.getRegionCode());
            lists.add(maps);
        }
        return R.ok(lists);
    }


    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('BasKeyword:BasKeyword:add')")
    @ApiOperation(value = "新增目的地列表", notes = "新增目的地列表('BasKeyword:BasKeyword:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasDestination basDestination) {
        BasDestination basDestination1 = new BasDestination();
        basDestination1.setRegionCode(basDestination.getRegionCode());
        List<BasDestination> list = basDestinationService.selectBasDestinationList(basDestination);
        if (list.size() != 0) {
            return R.failed("目的地编号重复");
        }
        basDestination.setBusinesSiteCode(basDestination.getDisSiteCode());
        basDestination.setCreateTime(new Date());
        basDestinationService.insertBasDestination(basDestination);
        return R.ok();

    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('BasKeyword:BasKeyword:edit')")
    @ApiOperation(value = "修改目的地列表", notes = "修改目的地列表('BasKeyword:BasKeyword:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasDestination basDestination) {
        basDestination.setBusinesSiteCode(basDestination.getDisSiteCode());
        basDestination.setUpdateTime(new Date());
        basDestinationService.updateBasDestination(basDestination);
        return R.ok();
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basdestination:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除目的地树形", notes = "删除目的地树形('bas:basdestination:remove')")
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basDestinationService.deleteBasDestinationByIds(ids));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basdestination:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除目的地树形", notes = "删除目的地树形('bas:basdestination:removes')")
    @DeleteMapping("/delete/{businesSiteCode}")
    public R removes(@PathVariable("businesSiteCode") String businesSiteCode) {
        return toOk(basDestinationService.deleteBySiteCode(businesSiteCode));
    }
}
