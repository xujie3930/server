package com.szmsd.bas.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.SyncReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.constant.BaseConstant;
import com.szmsd.bas.constant.ProductConstant;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.service.IBaseProductService;
import com.szmsd.bas.vo.BasProductMultipleTicketDTO;
import com.szmsd.bas.vo.BaseProductVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author l
 * @since 2021-03-04
 */


@Api(tags = {"SKU??????"})
@RestController
@RequestMapping("/base/product")
public class BaseProductController extends BaseController {

    @Resource
    private IBaseProductService baseProductService;

    @Autowired
    private IBasSellerService basSellerService;
    @Resource
    private RedissonClient redissonClient;
    @Autowired
    private BasSubClientService basSubClientService;

    private String genKey() {
        String lockKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        return className + methodName + "#" + lockKey;
    }

    /**
     * ??????????????????
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @GetMapping("/list")
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @AutoValue
    public TableDataInfo<BaseProduct> list(BaseProductQueryDto queryDto) {
        startPage();
        List<BaseProduct> list = baseProductService.selectBaseProductPage(queryDto);
        return getDataTable(list);
    }

    /**
     * ??????????????????
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:queryList')")
    @PostMapping("/queryList")
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public TableDataInfo queryList(@RequestBody BaseProductQueryDto queryDto) {
        startPage(queryDto);
        List<BaseProduct> list = baseProductService.selectBaseProductPage(queryDto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @GetMapping("/listByCode")
    @ApiOperation(value = "??????code????????????", notes = "??????code????????????")
    public TableDataInfo listByCode(String code, String category, int current, int size) {
        QueryWrapper<BasSeller> basSellerQueryWrapper = new QueryWrapper<>();
        basSellerQueryWrapper.eq("user_name", SecurityUtils.getLoginUser().getUsername());
        BasSeller basSeller = basSellerService.getOne(basSellerQueryWrapper);
        TableDataInfo<BaseProductVO> list = baseProductService.selectBaseProductByCode(code, basSeller.getSellerCode(), category, current, size);
        return list;
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:add')")
    @Log(title = "??????", businessType = BusinessType.INSERT)
    @PostMapping("import")
    @ApiOperation(value = "????????????", notes = "????????????")
    public R importData(MultipartFile file) throws Exception {

        /*ExcelUtil<BaseProductImportDto> util = new ExcelUtil<BaseProductImportDto>(BaseProductImportDto.class);
        List<BaseProductImportDto> userList = util.importExcel(file.getInputStream());*/

        List<BaseProductImportDto> userList = EasyExcel.read(file.getInputStream(), BaseProductImportDto.class, new SyncReadListener()).sheet().doReadSync();
        if (CollectionUtils.isEmpty(userList)) {
            throw new BaseException("The import content is empty");
        }

        for (int x=0;x<userList.size();x++) {
            if (userList.get(x).getCode().getBytes().length != userList.get(x).getCode().length()) {
                throw new BaseException("In the "+(x+2)+" Sku of line exists in Chinese");

            }
        }

        baseProductService.importBaseProduct(userList);
        return R.ok();
    }


    /**
     * ??????????????????
     */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:export')")
    @Log(title = "??????", businessType = BusinessType.EXPORT)
    @GetMapping("/exportTemplate/multipleTicket")
    @ApiOperation(value = "????????????-????????????", notes = "??????????????????")
    public void exportTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<BasProductMultipleTicketDTO> util = new ExcelUtil<>(BasProductMultipleTicketDTO.class);
        util.exportExcel(response, new ArrayList<>(), "????????????");
    }
    /**
     * ??????????????????
     */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:export')")
    @Log(title = "??????", businessType = BusinessType.EXPORT)
    @PostMapping("/importTemplate/multipleTicket")
    @ApiOperation(value = "??????-????????????", notes = "??????????????????")
    public R<Void> importMultipleTicket(@RequestPart("file") MultipartFile file) {
        String s = baseProductService.importMultipleTicket(file);
        return StringUtils.isBlank(s)?R.ok():R.failed(s);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:add')")
    @Log(title = "??????", businessType = BusinessType.INSERT)
    @GetMapping("importTemplate")
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public void importTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        commonExport(response, "sku_import_template");
    }

    /**
     * ??????????????????
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:export')")
    @Log(title = "??????", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public void export(HttpServletResponse response, BaseProductQueryDto queryDto) throws IOException {

        String len = getLen();
        queryDto.setCategory(ProductConstant.SKU_NAME);
        List<BaseProductExportDto> list = baseProductService.exportProduceList(queryDto, len);
        if("en".equals(len)){
            // ??????????????????
            java.util.Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("059");
            java.util.Map<String, String> map059 = new HashMap();
            if(listMap.get("059") != null){
                map059 = listMap.get("059").stream()
                        .collect(Collectors.toMap(BasSubWrapperVO::getSubName, BasSubWrapperVO:: getSubNameEn, (v1, v2) -> v1));
            }

            List<BaseProductEnExportDto> enList = new ArrayList();
            for (BaseProductExportDto vo:list) {
                if(map059.containsKey(vo.getProductAttributeName())){
                    vo.setProductAttributeName(map059.get(vo.getProductAttributeName()));
                }

                BaseProductEnExportDto enDto = new BaseProductEnExportDto();
                BeanUtils.copyProperties(vo, enDto);
                enList.add(enDto);

            }
            ExcelUtil<BaseProductEnExportDto> util = new ExcelUtil<BaseProductEnExportDto>(BaseProductEnExportDto.class);
            util.exportExcel(response, enList, "sku_export");
        }else{
            ExcelUtil<BaseProductExportDto> util = new ExcelUtil<BaseProductExportDto>(BaseProductExportDto.class);
            util.exportExcel(response, list, "sku??????");
        }

//        EasyExcel.write(response.getOutputStream()).withTemplate(new ClassPathResource(getFileName("sku_export_template")).getInputStream()).sheet().doWrite(list);
    }

    private String getFileName(String fileName) {
        return String.format("/template/%s_%s.xlsx", fileName, getLen().toLowerCase(Locale.ROOT));
    }

    private void commonExport(HttpServletResponse response, String fileName) {
        //"/template/??????????????????.xls"
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
    @ApiOperation(value = "??????code??????????????????", notes = "??????code??????????????????")
    public R<List<BaseProductMeasureDto>> batchSKU(@RequestBody BaseProductBatchQueryDto dto) {
        List<BaseProductMeasureDto> list = baseProductService.batchSKU(dto);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @PostMapping("/measuring")
    @ApiOperation(value = "????????????SKU", notes = "????????????SKU")
    public R measuringProduct(@RequestBody MeasuringProductRequest measuringProductRequest) {
        baseProductService.measuringProduct(measuringProductRequest);
        return R.ok();
    }


    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:attribute')")
    @PostMapping("/attribute")
    @ApiOperation(value = "et??????sku??????", notes = "et??????sku??????")
    public R attribute(@RequestBody EtSkuAttributeRequest etSkuAttributeRequest) {
        baseProductService.attribute(etSkuAttributeRequest);
        return R.ok();
    }


    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @PostMapping("/listSku")
    @ApiOperation(value = "????????????", notes = "????????????")
    public R<List<BaseProduct>> listSKU(@RequestBody BaseProduct baseProduct) {
        List<BaseProduct> list = baseProductService.listSku(baseProduct);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @GetMapping("/listSkuBySeller")
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    public R<List<BaseProduct>> listSkuBySeller(BaseProductQueryDto queryDto) {
        List<BaseProduct> list = baseProductService.listSkuBySeller(queryDto);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:list')")
    @PostMapping("/getSku")
    @ApiOperation(value = "????????????", notes = "????????????")
    public R<BaseProduct> getSku(@RequestBody BaseProduct baseProduct) {
        return baseProductService.getSku(baseProduct);
    }

    /**
     * ????????????????????????
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(baseProductService.selectBaseProductById(id));
    }

    @GetMapping(value = "rePushBaseProduct/{sku}")
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    public R rePushBaseProduct(@PathVariable("sku") String sku) {
        baseProductService.rePushBaseProduct(sku);
        return R.ok();
    }

    /**
     * ????????????
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:add')")
    @Log(title = "??????", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public R add(@RequestBody BaseProductDto baseProductDto) {
        RLock lock = redissonClient.getLock(genKey());
        try {
            if (lock.tryLock(BaseConstant.LOCK_TIME, BaseConstant.LOCK_TIME_UNIT)) {
                return toOk(baseProductService.insertBaseProduct(baseProductDto));
            } else {
                return R.failed(BaseConstant.genErrorMsg("New SKU"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(BaseConstant.genErrorMsg("New SKU"), e);
            return R.failed(BaseConstant.genErrorMsg("New SKU"));
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:add')")
    @Log(title = "??????", businessType = BusinessType.INSERT)
    @PostMapping("addBatch")
    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    public R<List<BaseProduct>> addBatch(@RequestBody List<BaseProductDto> baseProductDtos) {
        RLock lock = redissonClient.getLock(genKey());
        try {
            if (lock.tryLock(BaseConstant.LOCK_TIME, BaseConstant.LOCK_TIME_UNIT)) {
                return R.ok(baseProductService.BatchInsertBaseProduct(baseProductDtos));
            } else {
                return R.failed(BaseConstant.genErrorMsg("New SKU"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(BaseConstant.genErrorMsg("New SKU"), e);
            return R.failed(BaseConstant.genErrorMsg("New SKU"));
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    /**
     * ????????????
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:edit')")
    @Log(title = "??????", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " ????????????", notes = "????????????")
    public R edit(@RequestBody BaseProductDto baseProductDto) throws IllegalAccessException {
        return toOk(baseProductService.updateBaseProduct(baseProductDto));
    }

    /**
     * ????????????
     */
    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:remove')")
    @Log(title = "??????", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "????????????", notes = "????????????")
    public R remove(@RequestBody List<Long> ids) throws IllegalAccessException {
        return R.ok(baseProductService.deleteBaseProductByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:checkSkuValidToDelivery')")
    @PostMapping("checkSkuValidToDelivery")
    @ApiOperation(value = "??????sku????????????", notes = "??????sku????????????")
    public R<Boolean> checkSkuValidToDelivery(@RequestBody BaseProduct baseProduct) {
        return baseProductService.checkSkuValidToDelivery(baseProduct);
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:listProductAttribute')")
    @PostMapping("/listProductAttribute")
    @ApiOperation(value = "??????sku??????????????????")
    public R<List<String>> listProductAttribute(@RequestBody BaseProductConditionQueryDto conditionQueryDto) {
        return R.ok(this.baseProductService.listProductAttribute(conditionQueryDto));
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:queryProductList')")
    @PostMapping("/queryProductList")
    @ApiOperation(value = "???????????????SKU??????????????????")
    public R<List<BaseProduct>> queryProductList(@RequestBody BaseProductConditionQueryDto conditionQueryDto) {
        return R.ok(this.baseProductService.queryProductList(conditionQueryDto));
    }
}
