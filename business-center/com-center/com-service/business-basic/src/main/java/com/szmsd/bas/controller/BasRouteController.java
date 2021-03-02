package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasRoute;
import com.szmsd.bas.service.IBasRouteService;
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
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 路由表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-11-11
 */

@Api(tags = {"路由表"})
@RestController
@RequestMapping("/bas-route")
public class BasRouteController extends BaseController {


    @Resource
    private IBasRouteService basRouteService;

    /**
     * 查询路由表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasRoute:BasRoute:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasRoute basRoute) {
        startPage();
        List<BasRoute> list = basRouteService.selectBasRouteList(basRoute);
        return getDataTable(list);
    }

    /**
     * 查询路由表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasRoute:BasRoute:list')")
    @PostMapping("/lists")
    public R<List<BasRoute>> lists(@RequestBody BasRoute basRoute) {
        List<BasRoute> list = basRouteService.selectBasRouteList(basRoute);
        return R.ok(list);
    }

    /**
     * 导出路由表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasRoute:BasRoute:export')")
    @Log(title = "路由表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasRoute basRoute) throws IOException {
        List<BasRoute> list = basRouteService.selectBasRouteList(basRoute);
        ExcelUtil<BasRoute> util = new ExcelUtil<BasRoute>(BasRoute.class);
        util.exportExcel(response, list, "BasRoute");
    }

    /**
     * 新增路由表模块
     */
    @PreAuthorize("@ss.hasPermi('BasRoute:BasRoute:add')")
    @Log(title = "路由表模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasRoute basRoute) {
        BasRoute basRoute2=new BasRoute();
        basRoute2.setEndStationCode(basRoute.getEndStationCode());
        basRoute2.setStartStationCode(basRoute.getStartStationCode());
        List<BasRoute> list2 = basRouteService.selectBasRouteList(basRoute2);
        if (list2.size()!=0){
            return R.failed("起始，结束站重复");
        }
        BasRoute basRoute1=new BasRoute();
        basRoute1.setRouteCode(basRoute.getRouteCode());
        List<BasRoute> list1 = basRouteService.selectBasRouteList(basRoute1);
        if (list1.size()!=0){
            return R.failed("路由编号重复");
        }
        basRoute.setCreateTime(new Date());
        return toOk(basRouteService.insertBasRoute(basRoute));
    }

    /**
     * 修改路由表模块
     */
    @PreAuthorize("@ss.hasPermi('BasRoute:BasRoute:edit')")
    @Log(title = "路由表模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasRoute basRoute) {
        BasRoute basRoute1=new BasRoute();
        basRoute1.setRouteCode(basRoute.getRouteCode());
        List<BasRoute> list1 = basRouteService.selectBasRouteList(basRoute1);
        if (list1.size()!=0){
            return R.failed("路由编号重复");
        }
        BasRoute basRoute2=new BasRoute();
        basRoute2.setStartStationCode(basRoute.getStartStationCode());
        basRoute2.setEndStationCode(basRoute.getEndStationCode());
        List<BasRoute> list2 = basRouteService.selectBasRouteList(basRoute2);
        if (list2.size()!=0){
            return R.failed("起始，结束站重复");
        }
        basRoute.setUpdateTime(new Date());
        return toOk(basRouteService.updateBasRoute(basRoute));
    }

    /**
     * 删除路由表模块
     */
    @PreAuthorize("@ss.hasPermi('BasRoute:BasRoute:remove')")
    @Log(title = "路由表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basRouteService.deleteBasRouteByIds(ids));
    }

}
