package com.szmsd.bas.controller;
import com.szmsd.bas.dto.BaseProductDto;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.bas.service.IBaseProductService;
import com.szmsd.bas.domain.BaseProduct;
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
* @since 2021-03-04
*/


@Api(tags = {""})
@RestController
@RequestMapping("/base-product")
public class BaseProductController extends BaseController{

     @Resource
     private IBaseProductService baseProductService;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
      public TableDataInfo list(BaseProduct baseProduct)
     {
            startPage();
            List<BaseProduct> list = baseProductService.selectBaseProductList(baseProduct);
            return getDataTable(list);
      }

    /**
    * 导出模块列表
    */
     @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, BaseProduct baseProduct) throws IOException {
     List<BaseProduct> list = baseProductService.selectBaseProductList(baseProduct);
     ExcelUtil<BaseProduct> util = new ExcelUtil<BaseProduct>(BaseProduct.class);
        util.exportExcel(response,list, "BaseProduct");

     }

    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(baseProductService.selectBaseProductById(id));
    }

    /**
    * 新增模块
    */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增产品模块",notes = "新增产品模块")
    public R add(@RequestBody BaseProductDto baseProductDto)
    {
    return toOk(baseProductService.insertBaseProduct(baseProductDto));
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody BaseProduct baseProduct)
    {
    return toOk(baseProductService.updateBaseProduct(baseProduct));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块",notes = "删除模块")
    public R remove(@RequestBody List<String> ids)
    {
    return toOk(baseProductService.deleteBaseProductByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:checkSkuvalid')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @PostMapping("checkSkuvalid")
    @ApiOperation(value = "查询sku是否有效",notes = "查询sku是否有效")
    public R<Boolean> checkSkuvalid(@RequestBody BaseProduct baseProduct){
        return R.ok(baseProductService.checkSkuvalid(baseProduct));
    }
}
