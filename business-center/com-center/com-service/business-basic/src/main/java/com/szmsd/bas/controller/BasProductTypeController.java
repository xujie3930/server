package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasProductType;
import com.szmsd.bas.driver.UpdateRedis;
import com.szmsd.bas.service.IBasProductTypeService;
import com.szmsd.common.core.annotation.CodeToName;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.CodeToNameEnum;
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
 * @since 2020-06-13
 */

@Api(tags = {"产品类型模块"})
@RestController
@RequestMapping("/bas-product-type")
public class BasProductTypeController extends BaseController {


    @Resource
    private IBasProductTypeService basProductTypeService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basproducttype:list')")
    @ApiOperation(value = "查询产品类型列表", notes = "查询产品类型列表")
    @PostMapping("/lists")
    public R<List<BasProductType>> lists(@RequestBody BasProductType basProductType) {
        List<BasProductType> list = basProductTypeService.selectBasProductTypeList(basProductType);
        return R.ok(list);
    }


    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basproducttype:list')")
    @ApiOperation(value = "查询产品类型列表", notes = "查询产品类型列表")
    @GetMapping("/list")
    @CodeToName
    public TableDataInfo list(BasProductType basProductType) {
        startPage();
        List<BasProductType> list = basProductTypeService.selectBasProductTypeList(basProductType);
        return getDataTable(list);
    }


    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basproducttype:export')")
    @ApiOperation(value = "导出产品类型列表", notes = "导出产品类型列表")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasProductType basProductType) throws IOException {
        List<BasProductType> list = basProductTypeService.selectBasProductTypeList(basProductType);
        ExcelUtil<BasProductType> util = new ExcelUtil<BasProductType>(BasProductType.class);
        util.exportExcel(response, list, "BasProductType");
    }

    /**
     * 获取模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('bas:basproducttype:query')")
    @ApiOperation(value = "查询产品类型列表", notes = "查询产品类型列表")
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(basProductTypeService.selectBasProductTypeById(id));
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basproducttype:add')")
    @ApiOperation(value = "新增产品类型列表", notes = "新增产品类型列表")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @UpdateRedis(type = CodeToNameEnum.BAS_PRODUCT)
    @PostMapping
    public R add(@RequestBody BasProductType basProductType) {
        BasProductType basProductType2 =new BasProductType();
        basProductType2.setPrefixNumber(basProductType.getPrefixNumber());
        List<BasProductType> list = basProductTypeService.selectBasProductTypeList(basProductType);
        if (list.size()!=0){
            return R.failed("单号前缀重复");
        }
        BasProductType basProductTypes =new BasProductType();
        basProductTypes.setProductTypeCode(basProductType.getProductTypeCode());
        List<BasProductType> lists = basProductTypeService.selectBasProductTypeList(basProductTypes);
        if (lists.size()!=0){
            return R.failed("产品类型编号重复");
        }
//        String uid = UUID.randomUUID().toString().substring(0, 10);
//        basProductType.setProductTypeCode(uid);
        basProductType.setCreateTime(new Date());
        basProductTypeService.insertBasProductType(basProductType);
        return R.ok();
    }

    /**
     * 修改产品类型列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basproducttype:edit')")
    @Log(title = "修改产品类型列表", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改产品类型列表", notes = "修改产品类型列表")
    @UpdateRedis(type = CodeToNameEnum.BAS_PRODUCT)
    @PutMapping
    public R edit(@RequestBody BasProductType basProductType) {
        basProductType.setUpdateTime(new Date());
        return toOk(basProductTypeService.updateBasProductType(basProductType));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basproducttype:remove')")
    @ApiOperation(value = "删除产品类型列表", notes = "删除产品类型列表")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @UpdateRedis(type = CodeToNameEnum.BAS_PRODUCT)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basProductTypeService.deleteBasProductTypeByIds(ids));
    }

    /**
     * 获取模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('bas:basproducttype:query')")
    @GetMapping(value = "/list/{ids}")
    public R getids(@PathVariable("id") List<String> id) {
        return R.ok(basProductTypeService.selectGenTableColumnListByTableIds(id));
    }

}
