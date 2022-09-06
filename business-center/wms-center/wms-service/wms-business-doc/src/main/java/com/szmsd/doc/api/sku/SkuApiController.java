package com.szmsd.doc.api.sku;

import com.szmsd.bas.api.service.BasePackingClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.domain.SysLanres;
import com.szmsd.bas.dto.BasePackingDto;
import com.szmsd.bas.dto.BaseProductDto;
import com.szmsd.bas.dto.BaseProductQueryDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.api.RUtils;
import com.szmsd.doc.api.sku.request.*;
import com.szmsd.doc.api.sku.resp.BasePackingResp;
import com.szmsd.doc.api.sku.resp.BaseProductResp;
import com.szmsd.doc.component.IRemoterApi;
import com.szmsd.doc.config.DocSubConfigData;
import com.szmsd.doc.utils.AuthenticationUtil;
import com.szmsd.doc.utils.GoogleBarCodeUtils;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author francis
 * @date 2021-07-31
 */
@Api(tags = {"SKU管理"})
@ApiSort(100)
@RestController
@RequestMapping("/api/sku/")
public class SkuApiController {

    @Autowired
    private BaseProductClientService baseProductClientService;
    @Resource
    private BasePackingClientService basePackingClientService;
    @Resource
    private DocSubConfigData docSubConfigData;
    @Resource
    private BasePackingClientService basePackingFeignService;
    @Resource
    private IRemoterApi remoterApi;

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("list")
    @ApiOperation(value = "查询SKU列表", notes = "查询SKU信息，支持分页呈现，用于入库，或者新SKU出库、集运出库")
    public TableDataInfo<BaseProductResp> list(@Validated @RequestBody BaseProductQueryRequest baseProductQueryRequest) {
        baseProductQueryRequest.setSellerCode(AuthenticationUtil.getSellerCode());
        TableDataInfo<BaseProduct> list = baseProductClientService.list(BeanMapperUtil.map(baseProductQueryRequest, BaseProductQueryDto.class));
        TableDataInfo<BaseProductResp> baseProductResp = new TableDataInfo<>();
        BeanUtils.copyProperties(list, baseProductResp);
        List<BaseProduct> rows = list.getRows();
        List<BaseProductResp> collect = rows.stream().map(x -> {
            BaseProductResp baseProductResp1 = new BaseProductResp();
            BeanUtils.copyProperties(x, baseProductResp1);
            return baseProductResp1;
        }).collect(Collectors.toList());
        baseProductResp.setRows(collect);
        return baseProductResp;
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("save")
    @ApiOperation(value = "新增", notes = "创建SKU，创建成功，同步推送WMS")
    public R save(@RequestBody @Validated ProductRequest productRequest) {
        productRequest.setSellerCode(AuthenticationUtil.getSellerCode());
        productRequest.uploadFile(remoterApi).validData(remoterApi).calculateTheVolume().checkPack(basePackingFeignService).setTheCode(remoterApi, docSubConfigData);
        BaseProductDto product = BeanMapperUtil.map(productRequest, BaseProductDto.class);
        RUtils.getDataAndException(baseProductClientService.add(product));
        return R.ok();
    }
    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/collection/save")
    @ApiOperation(value = "集运新增SKU", notes = "创建SKU，创建成功，同步推送WMS")
    public R collectionSave(@RequestBody @Validated ProductCollectionReq productRequest) {
        productRequest.setSellerCode(AuthenticationUtil.getSellerCode());
        productRequest/*.uploadFile(remoterApi)*/.validData(remoterApi)/*.calculateTheVolume()*/.checkPack(basePackingFeignService).setTheCode(remoterApi, docSubConfigData);
        BaseProductDto product = BeanMapperUtil.map(productRequest, BaseProductDto.class);
        RUtils.getDataAndException(baseProductClientService.add(product));
        return R.ok();
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/package/save")
    @ApiOperation(value = "新增-包材", notes = "新增-包材")
    public R savePackage(@RequestBody @Validated BasePackingAddReq basePackingAddReq) {
        basePackingAddReq.setSellerCode(AuthenticationUtil.getSellerCode());
        basePackingAddReq.calculateTheVolume();
        BaseProductDto product = BeanMapperUtil.map(basePackingAddReq, BaseProductDto.class);
        RUtils.getDataAndException(baseProductClientService.add(product));
        return R.ok();
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("getBarCode")
    @ApiOperation(value = "SKU标签生成", notes = "生成sku编号，生成标签条形码，返回的为条形码图片的Base64")
    public R getBarCode(@RequestBody @Validated BarCodeReq barCodeReq) {
        String skuCode = barCodeReq.getSkuCode();
//        Boolean valid = baseProductClientService.checkSkuValidToDelivery(skuCode);
        boolean b = remoterApi.checkSkuBelong(skuCode);
        if (!b) throw new CommonException("400", String.format("请检查SKU:%s是否存在", skuCode));
//        AssertUtil.isTrue(b, String.format("请检查SKU:%s是否存在", skuCode));
        return R.ok(GoogleBarCodeUtils.generateBarCodeBase64(skuCode));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/listPacking/byWarehouseCode")
    @ApiOperation(value = "查询物流包装列表", notes = "查询物流包装列表-查询仓库下")
    public R<List<BasePackingResp>> listParent(@Validated @RequestBody BasePackingQueryReq warehouseCode) {
        BasePackingDto basePackingDto = new BasePackingDto();
        BeanUtils.copyProperties(warehouseCode,basePackingDto);
        List<BasePackingDto> basePackingDtos = basePackingClientService.listParent(basePackingDto);
        List<BasePackingResp> collect = basePackingDtos.stream().map(x -> {
            BasePackingResp basePackingResp = new BasePackingResp();
            BeanUtils.copyProperties(x, basePackingResp);
            return basePackingResp;
        }).collect(Collectors.toList());
        return R.ok(collect);
    }

    /**
     * 查询多语言配置表模块列表
     */
    @GetMapping("/lists")
    @ApiIgnore
    public R lists(SysLanres sysLanres) {
        return basePackingClientService.selectSysLanresList(sysLanres);
    }

}
