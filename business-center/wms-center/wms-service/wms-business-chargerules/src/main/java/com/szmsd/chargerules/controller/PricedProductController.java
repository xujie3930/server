package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.dto.CreateProductDTO;
import com.szmsd.chargerules.dto.FreightCalculationDTO;
import com.szmsd.chargerules.dto.PricedProductQueryDTO;
import com.szmsd.chargerules.dto.UpdateProductDTO;
import com.szmsd.chargerules.service.IPricedProductService;
import com.szmsd.chargerules.vo.FreightCalculationVO;
import com.szmsd.chargerules.vo.PricedProductInfoVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.vo.KeyValuePair;
import com.szmsd.http.vo.PricedProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = {"PricedProduct"})
@RestController
@RequestMapping("/products")
public class PricedProductController extends BaseController {

    @Resource
    private IPricedProductService iPricedProductService;

    @PreAuthorize("@ss.hasPermi('products:pricedproducts')")
    @PostMapping("/freightCalculation")
    @ApiOperation(value = "运费测算 - 根据包裹基本信息获取可下单报价产品")
    public R<List<FreightCalculationVO>> freightCalculation(@RequestBody FreightCalculationDTO freightCalculationDTO) {
        List<FreightCalculationVO> directServiceFeeData = iPricedProductService.pricedProducts(freightCalculationDTO);
        return R.ok(directServiceFeeData);
    }

    @PreAuthorize("@ss.hasPermi('products:page')")
    @GetMapping("/page")
    @ApiOperation(value = "分页查询产品列表，返回指定页面的数据，以及统计总记录数")
    public TableDataInfo<PricedProduct> page(PricedProductQueryDTO pricedProductQueryDTO) {
        TableDataInfo<PricedProduct> pricedProductList = iPricedProductService.selectPage(pricedProductQueryDTO);
        return pricedProductList;
    }

    @PreAuthorize("@ss.hasPermi('products:keyvaluepairs')")
    @GetMapping("/keyValuePairs")
    @ApiOperation(value = "查询产品下拉列表，返回list数据")
    public R<List<KeyValuePair>> keyValuePairs() {
        List<KeyValuePair> directServiceFeeData = iPricedProductService.keyValuePairs();
        return R.ok(directServiceFeeData);
    }

    @PreAuthorize("@ss.hasPermi('products:create')")
    @PostMapping("/create")
    @ApiOperation(value = "创建报价产品信息")
    public R create(@RequestBody CreateProductDTO createProductDTO) {
        iPricedProductService.create(createProductDTO);
        return R.ok();
    }

    @GetMapping("/info/{productCode}")
    @ApiOperation(value = "根据产品代码获取计价产品信息")
    public R<PricedProductInfoVO> info(@PathVariable("productCode") String productCode) {
        PricedProductInfoVO pricedProductInfoVO = iPricedProductService.getInfo(productCode);
        return R.ok(pricedProductInfoVO);
    }

    @PreAuthorize("@ss.hasPermi('products:update')")
    @PutMapping("/update")
    @ApiOperation(value = "修改报价产品信息")
    public R update(@RequestBody UpdateProductDTO updateProductDTO) {
        iPricedProductService.update(updateProductDTO);
        return R.ok();
    }

    @PostMapping("/exportFile")
    @ApiOperation(value = "导出产品信息列表")
    public void exportFile(HttpServletResponse response, @RequestBody List<String> codes) {
        FileStream fileStream = iPricedProductService.exportFile(codes);
        super.fileStreamWrite(response, fileStream);
    }

}
