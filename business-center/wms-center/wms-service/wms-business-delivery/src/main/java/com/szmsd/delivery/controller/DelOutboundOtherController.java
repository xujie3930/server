package com.szmsd.delivery.controller;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.constant.ShipmentType;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.delivery.dto.DelOutboundOtherInServiceDto;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.Address;
import com.szmsd.http.dto.CountryInfo;
import com.szmsd.http.dto.PricedProductInServiceCriteria;
import com.szmsd.http.vo.PricedProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 出库管理
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"出库管理 - 其它服务"})
@ApiSort(200)
@RestController
@RequestMapping("/api/outbound/other")
public class DelOutboundOtherController extends BaseController {

    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;
    @Autowired
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;
    @Autowired
    private BaseProductClientService baseProductClientService;

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundOther:inService')")
    @PostMapping("/inService")
    @ApiOperation(value = "出库管理 - 其它服务 - 物流服务", position = 100)
    @ApiImplicitParam(name = "dto", value = "参数", dataType = "DelOutboundOtherInServiceDto")
    public R<List<PricedProduct>> inService(@RequestBody @Validated DelOutboundOtherInServiceDto dto) {
        // 查询国家信息
        R<BasRegionSelectListVO> countryR = this.basRegionFeignService.queryByCountryCode(dto.getCountryCode());
        BasRegionSelectListVO country = R.getDataAndException(countryR);
        if (null == country) {
            throw new CommonException("999", "国家信息不存在");
        }
        // 查询仓库信息
        BasWarehouse warehouse = this.basWarehouseClientService.queryByWarehouseCode(dto.getWarehouseCode());
        if (null == warehouse) {
            throw new CommonException("999", "仓库信息不存在");
        }
        // 传入参数：仓库，SKU
        PricedProductInServiceCriteria criteria = new PricedProductInServiceCriteria();
        criteria.setClientCode(dto.getClientCode());
        criteria.setCountryName(country.getName());
        criteria.setFromAddress(new Address(warehouse.getStreet1(),
                warehouse.getStreet2(),
                null,
                warehouse.getPostcode(),
                warehouse.getCity(),
                warehouse.getProvince(),
                new CountryInfo(country.getAddressCode(), null, country.getEnName(), country.getName())
        ));
        if (CollectionUtils.isNotEmpty(dto.getSkus())) {
            criteria.setIsElectriferous(ShipmentType.BATTERY.equals(this.baseProductClientService.buildShipmentType(dto.getWarehouseCode(), dto.getSkus())));
        }
        if (CollectionUtils.isNotEmpty(dto.getProductAttributes())) {
            criteria.setIsElectriferous(ShipmentType.BATTERY.equals(ShipmentType.highest(dto.getProductAttributes())));
        }
        return R.ok(this.htpPricedProductClientService.inService(criteria));
    }
}
