package com.szmsd.bas.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.SyncReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.bas.constant.BaseConstant;
import com.szmsd.bas.constant.ProductConstant;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.service.IBaseProductService;
import com.szmsd.bas.vo.BaseProductVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.apache.commons.io.IOUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author l
 * @since 2021-03-04
 */


@Api(tags = {"SKU模块"})
@RestController
@RequestMapping("/base/product")
public class BaseProductController extends BaseController {

    @Resource
    private IBaseProductService baseProductService;

    @Autowired
    private IBasSellerService basSellerService;
    @Resource
    private RedissonClient redissonClient;

    private String genKey() {
        String lockKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        return className + methodName + "#" + lockKey;
    }

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    @AutoValue
    public TableDataInfo<BaseProduct> list(BaseProductQueryDto queryDto) {
        startPage();
        List<BaseProduct> list = baseProductService.selectBaseProductPage(queryDto);
        return getDataTable(list);
    }

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:queryList')")
    @PostMapping("/queryList")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    public TableDataInfo queryList(@RequestBody BaseProductQueryDto queryDto) {
        startPage(queryDto);
        List<BaseProduct> list = baseProductService.selectBaseProductPage(queryDto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @GetMapping("/listByCode")
    @ApiOperation(value = "通过code查询列表", notes = "通过code查询列表")
    public TableDataInfo listByCode(String code, String category, int current, int size) {
        QueryWrapper<BasSeller> basSellerQueryWrapper = new QueryWrapper<>();
        basSellerQueryWrapper.eq("user_name", SecurityUtils.getLoginUser().getUsername());
        BasSeller basSeller = basSellerService.getOne(basSellerQueryWrapper);
        TableDataInfo<BaseProductVO> list = baseProductService.selectBaseProductByCode(code, basSeller.getSellerCode(), category, current, size);
        return list;
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("import")
    @ApiOperation(value = "导入产品", notes = "导入产品")
    public R importData(MultipartFile file) throws Exception {

        /*ExcelUtil<BaseProductImportDto> util = new ExcelUtil<BaseProductImportDto>(BaseProductImportDto.class);
        List<BaseProductImportDto> userList = util.importExcel(file.getInputStream());*/

        List<BaseProductImportDto> userList = EasyExcel.read(file.getInputStream(), BaseProductImportDto.class, new SyncReadListener()).sheet().doReadSync();
        if (CollectionUtils.isEmpty(userList)) {
            throw new BaseException("导入内容为空");
        }
        baseProductService.importBaseProduct(userList);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @GetMapping("importTemplate")
    @ApiOperation(value = "导入模板下载", notes = "导入模板下载")
    public void importTemplate(HttpServletResponse response) throws Exception {
        commonExport(response, "sku_import_template");
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出模块列表", notes = "导出模块列表")
    public void export(HttpServletResponse response, BaseProductQueryDto queryDto) throws IOException {
        queryDto.setCategory(ProductConstant.SKU_NAME);
        List<BaseProductExportDto> list = baseProductService.exportProduceList(queryDto);
        ExcelUtil<BaseProductExportDto> util = new ExcelUtil<BaseProductExportDto>(BaseProductExportDto.class);
        util.exportExcel(response, list, "sku导出");
//        EasyExcel.write(response.getOutputStream()).withTemplate(new ClassPathResource(getFileName("sku_export_template")).getInputStream()).sheet().doWrite(list);
    }

    private String getFileName(String fileName) {
        return String.format("/template/%s_%s.xlsx", fileName, getLen().toLowerCase(Locale.ROOT));
    }

    private void commonExport(HttpServletResponse response, String fileName) {
        //"/template/退费申请模板.xls"
        ClassPathResource classPathResource = new ClassPathResource(getFileName(fileName));
        try (InputStream inputStream = classPathResource.getInputStream();
             ServletOutputStream outputStream = response.getOutputStream()) {

            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            String excelName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + excelName + ".xls");
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @PostMapping("/batchSKU")
    @ApiOperation(value = "通过code批量查询列表", notes = "通过code批量查询列表")
    public R<List<BaseProductMeasureDto>> batchSKU(@RequestBody BaseProductBatchQueryDto dto) {
        List<BaseProductMeasureDto> list = baseProductService.batchSKU(dto);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @PostMapping("/measuring")
    @ApiOperation(value = "仓库测量SKU", notes = "仓库测量SKU")
    public R measuringProduct(@RequestBody MeasuringProductRequest measuringProductRequest) {
        baseProductService.measuringProduct(measuringProductRequest);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @PostMapping("/listSku")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public R<List<BaseProduct>> listSKU(@RequestBody BaseProduct baseProduct) {
        List<BaseProduct> list = baseProductService.listSku(baseProduct);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @GetMapping("/listSkuBySeller")
    @ApiOperation(value = "查询当前用户列表", notes = "查询当前用户列表")
    public R<List<BaseProduct>> listSkuBySeller(BaseProductQueryDto queryDto) {
        List<BaseProduct> list = baseProductService.listSkuBySeller(queryDto);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @PostMapping("/getSku")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public R<BaseProduct> getSku(@RequestBody BaseProduct baseProduct) {
        return baseProductService.getSku(baseProduct);
    }

    /**
     * 获取模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息", notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(baseProductService.selectBaseProductById(id));
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增产品模块", notes = "新增产品模块")
    public R add(@RequestBody BaseProductDto baseProductDto) {
        RLock lock = redissonClient.getLock(genKey());
        try {
            if (lock.tryLock(BaseConstant.LOCK_TIME, BaseConstant.LOCK_TIME_UNIT)) {
                return toOk(baseProductService.insertBaseProduct(baseProductDto));
            } else {
                return R.failed(BaseConstant.genErrorMsg("新增SKU"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(BaseConstant.genErrorMsg("新增SKU"), e);
            return R.failed(BaseConstant.genErrorMsg("新增SKU"));
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("addBatch")
    @ApiOperation(value = "新增产品模块", notes = "新增产品模块")
    public R<List<BaseProduct>> addBatch(@RequestBody List<BaseProductDto> baseProductDtos) {
        RLock lock = redissonClient.getLock(genKey());
        try {
            if (lock.tryLock(BaseConstant.LOCK_TIME, BaseConstant.LOCK_TIME_UNIT)) {
                return R.ok(baseProductService.BatchInsertBaseProduct(baseProductDtos));
            } else {
                return R.failed(BaseConstant.genErrorMsg("新增SKU"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(BaseConstant.genErrorMsg("新增SKU"), e);
            return R.failed(BaseConstant.genErrorMsg("新增SKU"));
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块", notes = "修改模块")
    public R edit(@RequestBody BaseProductDto baseProductDto) throws IllegalAccessException {
        return toOk(baseProductService.updateBaseProduct(baseProductDto));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块", notes = "删除模块")
    public R remove(@RequestBody List<Long> ids) throws IllegalAccessException {
        return R.ok(baseProductService.deleteBaseProductByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:checkSkuValidToDelivery')")
    @PostMapping("checkSkuValidToDelivery")
    @ApiOperation(value = "查询sku是否有效", notes = "查询sku是否有效")
    public R<Boolean> checkSkuValidToDelivery(@RequestBody BaseProduct baseProduct) {
        return baseProductService.checkSkuValidToDelivery(baseProduct);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:listProductAttribute')")
    @PostMapping("/listProductAttribute")
    @ApiOperation(value = "根据sku返回产品属性")
    public R<List<String>> listProductAttribute(@RequestBody BaseProductConditionQueryDto conditionQueryDto) {
        return R.ok(this.baseProductService.listProductAttribute(conditionQueryDto));
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:queryProductList')")
    @PostMapping("/queryProductList")
    @ApiOperation(value = "根据仓库，SKU查询产品信息")
    public R<List<BaseProduct>> queryProductList(@RequestBody BaseProductConditionQueryDto conditionQueryDto) {
        return R.ok(this.baseProductService.queryProductList(conditionQueryDto));
    }
}
