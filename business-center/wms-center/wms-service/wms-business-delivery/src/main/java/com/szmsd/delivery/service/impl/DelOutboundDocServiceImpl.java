package com.szmsd.delivery.service.impl;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundBringVerifyDto;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.dto.DelOutboundOtherInServiceDto;
import com.szmsd.delivery.service.IDelOutboundDocService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundBringVerifyVO;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.Address;
import com.szmsd.http.dto.CountryInfo;
import com.szmsd.http.dto.PricedProductInServiceCriteria;
import com.szmsd.http.vo.PricedProduct;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 10:32
 */
@Service
public class DelOutboundDocServiceImpl implements IDelOutboundDocService {

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;
    @Autowired
    private BaseProductClientService baseProductClientService;

    private List<DelOutboundAddResponse> addResponseList(List<DelOutboundDto> dtoList) {
        List<DelOutboundAddResponse> result = new ArrayList<>();
        int index = 1;
        List<String> refNoList = new ArrayList<String>();
        for (DelOutboundDto dto : dtoList) {
            DelOutboundAddResponse delOutbound = null;
            if (StringUtils.isNotEmpty(dto.getRefNo()) && refNoList.contains(dto.getRefNo())) {
                delOutbound = new DelOutboundAddResponse();
                // 返回异常错误信息
                delOutbound.setStatus(false);
                delOutbound.setMessage("本次操作数据中Refno 必须唯一值" + dto.getRefNo());
            } else {
                refNoList.add(dto.getRefNo());
                try {
                    delOutbound = this.delOutboundService.insertDelOutbound(dto);
                } catch (Exception e) {
                    delOutbound = new DelOutboundAddResponse();
                    // 返回异常错误信息
                    delOutbound.setStatus(false);
                    delOutbound.setMessage(e.getMessage());
                }
            }
            delOutbound.setIndex(index);
            result.add(delOutbound);
            index++;
        }
        return result;
    }

    @Override
    public List<DelOutboundAddResponse> add(List<DelOutboundDto> list) {

        // 批量创建出库单
        // List<DelOutboundAddResponse> responses = this.delOutboundService.insertDelOutbounds(list);
        // 处理异常信息，出现异常让事务回滚
        List<DelOutboundAddResponse> responses = this.addResponseList(list);

        // 获取出库单ID
        List<Long> ids = responses.stream().map(DelOutboundAddResponse::getId).filter(Objects::nonNull).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(ids)) {
            // 批量提审出库单
            DelOutboundBringVerifyDto bringVerifyDto = new DelOutboundBringVerifyDto();
            bringVerifyDto.setIds(ids);
            List<DelOutboundBringVerifyVO> bringVerifyVOList = this.delOutboundBringVerifyService.bringVerify(bringVerifyDto);

            Map<String, DelOutboundBringVerifyVO> bringVerifyVOMap = new HashMap<>();
            for (DelOutboundBringVerifyVO bringVerifyVO : bringVerifyVOList) {
                bringVerifyVOMap.put(bringVerifyVO.getOrderNo(), bringVerifyVO);
            }

            // 查询出库单信息
            List<DelOutbound> delOutboundList = this.delOutboundService.listByIds(ids);
            Map<Long, DelOutbound> delOutboundMap = delOutboundList.stream().collect(Collectors.toMap(DelOutbound::getId, v -> v, (a, b) -> a));

            for (DelOutboundAddResponse response : responses) {
                if (!response.getStatus()) {
                    continue;
                }
                // 提审结果
                DelOutboundBringVerifyVO bringVerifyVO = bringVerifyVOMap.get(response.getOrderNo());
                if (null != bringVerifyVO) {
                    response.setStatus(bringVerifyVO.getSuccess());
                    response.setMessage(bringVerifyVO.getMessage());
                }
                // 挂号
                DelOutbound delOutbound = delOutboundMap.get(response.getId());
                if (null != delOutbound) {
                    response.setTrackingNo(delOutbound.getTrackingNo());
                }
            }
        }

        return responses;
    }

    @Override
    public List<PricedProduct> inService(DelOutboundOtherInServiceDto dto) {
        // 查询国家信息
        String countryCode = dto.getCountryCode();
        BasRegionSelectListVO country = null;
        if (StringUtils.isNotEmpty(countryCode)) {
            R<BasRegionSelectListVO> countryR = this.basRegionFeignService.queryByCountryCode(countryCode);
            country = R.getDataAndException(countryR);
            if (null == country) {
                throw new CommonException("400", "国家信息不存在");
            }
        }
        // 查询仓库信息
        String warehouseCode = dto.getWarehouseCode();
        BasWarehouse warehouse = null;
        if (StringUtils.isNotEmpty(warehouseCode)) {
            warehouse = this.basWarehouseClientService.queryByWarehouseCode(warehouseCode);
            if (null == warehouse) {
                throw new CommonException("400", "仓库信息不存在");
            }
        }
        // 传入参数：仓库，SKU
        PricedProductInServiceCriteria criteria = new PricedProductInServiceCriteria();
        criteria.setClientCode(dto.getClientCode());
        CountryInfo countryInfo = null;
        if (null != country) {
            criteria.setCountryName(country.getName());
            countryInfo = new CountryInfo(country.getAddressCode(), null, country.getEnName(), country.getName());
        }
        Address fromAddress = null;
        if (null != warehouse) {
            fromAddress = new Address(warehouse.getStreet1(),
                    warehouse.getStreet2(),
                    null,
                    warehouse.getPostcode(),
                    warehouse.getCity(),
                    warehouse.getProvince(),
                    countryInfo
            );
        }
        criteria.setFromAddress(fromAddress);
        if (CollectionUtils.isNotEmpty(dto.getSkus())) {
            // fix:SKU不跟仓库关联，SKU跟卖家编码关联。
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            conditionQueryDto.setSellerCode(dto.getClientCode());
            conditionQueryDto.setSkus(dto.getSkus());
            criteria.setShipmentTypes(this.baseProductClientService.listProductAttribute(conditionQueryDto));
        }
        if (CollectionUtils.isNotEmpty(dto.getProductAttributes())) {
            criteria.setShipmentTypes(dto.getProductAttributes());
        }
        return this._inService(criteria);
    }

    @Override
    public List<PricedProduct> inService2(DelOutboundOtherInServiceDto dto) {
        // 查询仓库信息
        String warehouseCode = dto.getWarehouseCode();
        BasWarehouse warehouse = null;
        if (StringUtils.isNotEmpty(warehouseCode)) {
            warehouse = this.basWarehouseClientService.queryByWarehouseCode(warehouseCode);
            if (null == warehouse) {
                throw new CommonException("400", "仓库信息不存在");
            }
        }
        // 传入参数：仓库，SKU
        PricedProductInServiceCriteria criteria = new PricedProductInServiceCriteria();
        criteria.setClientCode(dto.getClientCode());
        CountryInfo countryInfo = null;
        if (null != warehouse.getCountryCode()) {
            criteria.setCountryName(warehouse.getCountryName());
            countryInfo = new CountryInfo(warehouse.getCountryCode(), null, warehouse.getCountryName(), warehouse.getCountryChineseName());
        }
        Address fromAddress = null;
        if (null != warehouse) {
            fromAddress = new Address(warehouse.getStreet1(),
                    warehouse.getStreet2(),
                    null,
                    warehouse.getPostcode(),
                    warehouse.getCity(),
                    warehouse.getProvince(),
                    countryInfo
            );
        }
        criteria.setFromAddress(fromAddress);
        return this._inService(criteria);
    }

    private List<PricedProduct> _inService(PricedProductInServiceCriteria criteria) {
        return this.htpPricedProductClientService.inService(criteria);
    }

    @Override
    public boolean inServiceValid(DelOutboundOtherInServiceDto dto, String shipmentRule) {
        if (StringUtils.isEmpty(shipmentRule)) {
            return false;
        }
        List<PricedProduct> productList = this.inService(dto);
        if (CollectionUtils.isEmpty(productList)) {
            return false;
        }
        for (PricedProduct product : productList) {
            if (null != product && shipmentRule.equals(product.getCode())) {
                return true;
            }
        }
        return false;
    }
}
