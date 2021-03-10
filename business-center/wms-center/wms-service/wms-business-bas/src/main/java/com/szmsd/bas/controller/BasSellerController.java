package com.szmsd.bas.controller;
import com.szmsd.bas.dto.BasSellerDto;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/bas-seller")
public class BasSellerController extends BaseController{

     @Resource
     private IBasSellerService basSellerService;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
      public TableDataInfo list(BasSeller basSeller)
     {
            startPage();
            List<BasSeller> list = basSellerService.selectBasSellerList(basSeller);
            return getDataTable(list);
      }

    /**
    * 导出模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, BasSeller basSeller) throws IOException {
     List<BasSeller> list = basSellerService.selectBasSellerList(basSeller);
     ExcelUtil<BasSeller> util = new ExcelUtil<BasSeller>(BasSeller.class);
        util.exportExcel(response,list, "BasSeller");

     }

    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(basSellerService.selectBasSellerById(id));
    }

    /**
    * 新增模块
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "注册模块",notes = "注册模块")
    public R<Boolean> add(HttpServletRequest request,@RequestBody BasSellerDto dto)
    {

    return basSellerService.insertBasSeller(request,dto);
    }

    @ApiOperation("获取验证码")
    @PostMapping("getCheckCode")
    public R<String> getCheckCode(HttpServletRequest request) {
        return R.ok(this.basSellerService.getCheckCode(request));
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody BasSeller basSeller)
    {
    return toOk(basSellerService.updateBasSeller(basSeller));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块",notes = "删除模块")
    public R remove(@RequestBody List<String> ids)
    {
    return toOk(basSellerService.deleteBasSellerByIds(ids));
    }

}
