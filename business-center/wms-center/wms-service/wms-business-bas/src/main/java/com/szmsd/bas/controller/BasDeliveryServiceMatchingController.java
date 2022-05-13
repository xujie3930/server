package com.szmsd.bas.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.bas.service.IBasDeliveryServiceMatchingService;
import com.szmsd.bas.domain.BasDeliveryServiceMatching;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import java.util.List;
import java.io.IOException;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;


/**
* <p>
    * 发货服务匹配 前端控制器
    * </p>
*
* @author Administrator
* @since 2022-05-12
*/


@Api(tags = {"发货服务匹配"})
@RestController
@RequestMapping("/bas-delivery-service-matching")
public class BasDeliveryServiceMatchingController extends BaseController{

     @Resource
     private IBasDeliveryServiceMatchingService basDeliveryServiceMatchingService;
     /**
       * 查询发货服务匹配模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasDeliveryServiceMatching:BasDeliveryServiceMatching:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询发货服务匹配模块列表",notes = "查询发货服务匹配模块列表")
      public TableDataInfo list(BasDeliveryServiceMatching basDeliveryServiceMatching)
     {
            startPage();
            List<BasDeliveryServiceMatching> list = basDeliveryServiceMatchingService.selectBasDeliveryServiceMatchingList(basDeliveryServiceMatching);
            return getDataTable(list);
      }

    /**
    * 导出发货服务匹配模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasDeliveryServiceMatching:BasDeliveryServiceMatching:export')")
     @Log(title = "发货服务匹配模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出发货服务匹配模块列表",notes = "导出发货服务匹配模块列表")
     public void export(HttpServletResponse response, BasDeliveryServiceMatching basDeliveryServiceMatching) throws IOException {
     List<BasDeliveryServiceMatching> list = basDeliveryServiceMatchingService.selectBasDeliveryServiceMatchingList(basDeliveryServiceMatching);
     ExcelUtil<BasDeliveryServiceMatching> util = new ExcelUtil<BasDeliveryServiceMatching>(BasDeliveryServiceMatching.class);
        util.exportExcel(response,list, "BasDeliveryServiceMatching");

     }

    /**
    * 获取发货服务匹配模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasDeliveryServiceMatching:BasDeliveryServiceMatching:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取发货服务匹配模块详细信息",notes = "获取发货服务匹配模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(basDeliveryServiceMatchingService.selectBasDeliveryServiceMatchingById(id));
    }

    /**
    * 新增发货服务匹配模块
    */
    @PreAuthorize("@ss.hasPermi('BasDeliveryServiceMatching:BasDeliveryServiceMatching:add')")
    @Log(title = "发货服务匹配模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增发货服务匹配模块",notes = "新增发货服务匹配模块")
    public R add(@RequestBody BasDeliveryServiceMatching basDeliveryServiceMatching)
    {
    return toOk(basDeliveryServiceMatchingService.insertBasDeliveryServiceMatching(basDeliveryServiceMatching));
    }

    /**
    * 修改发货服务匹配模块
    */
    @PreAuthorize("@ss.hasPermi('BasDeliveryServiceMatching:BasDeliveryServiceMatching:edit')")
    @Log(title = "发货服务匹配模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改发货服务匹配模块",notes = "修改发货服务匹配模块")
    public R edit(@RequestBody BasDeliveryServiceMatching basDeliveryServiceMatching)
    {
    return toOk(basDeliveryServiceMatchingService.updateBasDeliveryServiceMatching(basDeliveryServiceMatching));
    }

    /**
    * 删除发货服务匹配模块
    */
    @PreAuthorize("@ss.hasPermi('BasDeliveryServiceMatching:BasDeliveryServiceMatching:remove')")
    @Log(title = "发货服务匹配模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除发货服务匹配模块",notes = "删除发货服务匹配模块")
    public R remove(@RequestBody List<String> ids)
    {
    return toOk(basDeliveryServiceMatchingService.deleteBasDeliveryServiceMatchingByIds(ids));
    }

}
