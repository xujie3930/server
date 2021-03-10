package com.szmsd.bas.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.bas.service.IBasSellerInfoService;
import com.szmsd.bas.domain.BasSellerInfo;
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
    *  前端控制器
    * </p>
*
* @author l
* @since 2021-03-09
*/


@Api(tags = {""})
@RestController
@RequestMapping("/bas-seller-info")
public class BasSellerInfoController extends BaseController{

     @Resource
     private IBasSellerInfoService basSellerInfoService;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasSellerInfo:BasSellerInfo:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
      public TableDataInfo list(BasSellerInfo basSellerInfo)
     {
            startPage();
            List<BasSellerInfo> list = basSellerInfoService.selectBasSellerInfoList(basSellerInfo);
            return getDataTable(list);
      }

    /**
    * 导出模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasSellerInfo:BasSellerInfo:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, BasSellerInfo basSellerInfo) throws IOException {
     List<BasSellerInfo> list = basSellerInfoService.selectBasSellerInfoList(basSellerInfo);
     ExcelUtil<BasSellerInfo> util = new ExcelUtil<BasSellerInfo>(BasSellerInfo.class);
        util.exportExcel(response,list, "BasSellerInfo");

     }

    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasSellerInfo:BasSellerInfo:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(basSellerInfoService.selectBasSellerInfoById(id));
    }

    /**
    * 新增模块
    */
    @PreAuthorize("@ss.hasPermi('BasSellerInfo:BasSellerInfo:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块",notes = "新增模块")
    public R add(@RequestBody BasSellerInfo basSellerInfo)
    {
    return toOk(basSellerInfoService.insertBasSellerInfo(basSellerInfo));
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('BasSellerInfo:BasSellerInfo:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody BasSellerInfo basSellerInfo)
    {
    return toOk(basSellerInfoService.updateBasSellerInfo(basSellerInfo));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('BasSellerInfo:BasSellerInfo:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块",notes = "删除模块")
    public R remove(@RequestBody List<String> ids)
    {
    return toOk(basSellerInfoService.deleteBasSellerInfoByIds(ids));
    }

}
