package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasRouteDetailed;
import com.szmsd.bas.service.IBasRouteDetailedService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 路由明细表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-11-11
 */

@Api(tags = {"路由明细表"})
@RestController
@RequestMapping("/bas-route-detailed")
public class BasRouteDetailedController extends BaseController {


    @Resource
    private IBasRouteDetailedService basRouteDetailedService;

    /**
     * 查询路由明细表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasRouteDetailed:BasRouteDetailed:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasRouteDetailed basRouteDetailed) {
        startPage();
        List<BasRouteDetailed> list = basRouteDetailedService.selectBasRouteDetailedList(basRouteDetailed);
        return getDataTable(list);
    }

    /**
     * 导出路由明细表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasRouteDetailed:BasRouteDetailed:export')")
    @Log(title = "路由明细表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasRouteDetailed basRouteDetailed) throws IOException {
        List<BasRouteDetailed> list = basRouteDetailedService.selectBasRouteDetailedList(basRouteDetailed);
        ExcelUtil<BasRouteDetailed> util = new ExcelUtil<BasRouteDetailed>(BasRouteDetailed.class);
        util.exportExcel(response, list, "BasRouteDetailed");
    }

    /**
     * 新增路由明细表模块
     */
    @PreAuthorize("@ss.hasPermi('BasRouteDetailed:BasRouteDetailed:add')")
    @Log(title = "路由明细表模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody List<BasRouteDetailed> basRouteDetailedList) {
        List siteList=new ArrayList();
        for (BasRouteDetailed basRouteDetailed:basRouteDetailedList) {
            String uid = UUID.randomUUID().toString().replace("-", "");
            BasRouteDetailed basRouteDetailed1 = new BasRouteDetailed();
            basRouteDetailed1.setRouteCode(basRouteDetailed.getRouteCode());
            basRouteDetailed1.setRouteName(basRouteDetailed.getRouteName());
            basRouteDetailed1.setRouteSiteCode(basRouteDetailed.getRouteSiteCode());
            basRouteDetailed1.setRouteSiteName(basRouteDetailed.getRouteSiteName());
            List<BasRouteDetailed> list = basRouteDetailedService.selectBasRouteDetailedList(basRouteDetailed1);
            if (list.size() != 0) {
                siteList.add(basRouteDetailed.getRouteSiteName());
                continue;
            }
            basRouteDetailed.setId(uid);
            basRouteDetailed.setCreateTime(new Date());
            basRouteDetailedService.save(basRouteDetailed);
        }
        if (siteList.size()==0){
            return R.ok();
        }
        return R.failed("网点重复");

    }

    /**
     * 修改路由明细表模块
     */
    @PreAuthorize("@ss.hasPermi('BasRouteDetailed:BasRouteDetailed:edit')")
    @Log(title = "路由明细表模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasRouteDetailed basRouteDetailed) {
        basRouteDetailed.setUpdateTime(new Date());
        return toOk(basRouteDetailedService.updateBasRouteDetailed(basRouteDetailed));
    }

    /**
     * 删除路由明细表模块
     */
    @PreAuthorize("@ss.hasPermi('BasRouteDetailed:BasRouteDetailed:remove')")
    @Log(title = "路由明细表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basRouteDetailedService.deleteBasRouteDetailedByIds(ids));
    }

}
