package com.szmsd.delivery.imported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 21:49
 */
public class DelOutboundImportContainer extends DelOutboundCacheImportContext {

    private final List<DelOutboundDetailImportDto2> detailList;
    private final Map<Integer, List<DelOutboundDetailImportDto2>> detailMapList;
    private final ImportValidationData importValidationData;
    private final String sellerCode;

    public DelOutboundImportContainer(List<DelOutboundImportDto> dataList,
                                      List<BasSubWrapperVO> orderTypeList,
                                      List<BasRegionSelectListVO> countryList,
                                      List<BasSubWrapperVO> deliveryMethodList,
                                      List<DelOutboundDetailImportDto2> detailList,
                                      ImportValidationData importValidationData,
                                      String sellerCode) {
        super(dataList, orderTypeList, countryList, deliveryMethodList);
        this.detailList = detailList;
        this.detailMapList = this.detailToMapList();
        this.importValidationData = importValidationData;
        this.sellerCode = sellerCode;
    }

    public List<DelOutboundDto> get() {
        List<DelOutboundImportDto> dataList = super.getDataList();
        List<DelOutboundDto> outboundDtoList = new ArrayList<>();
        for (DelOutboundImportDto dto : dataList) {
            DelOutboundDto outboundDto = new DelOutboundDto();
            outboundDto.setCustomCode(this.sellerCode);
            outboundDto.setWarehouseCode(dto.getWarehouseCode());
            outboundDto.setOrderType(super.orderTypeCache.get(dto.getOrderTypeName()));
            outboundDto.setSellerCode(this.sellerCode);
            outboundDto.setShipmentRule(dto.getShipmentRule());
            outboundDto.setRemark(dto.getRemark());
            outboundDto.setDeliveryMethod(super.deliveryMethodCache.get(dto.getDeliveryMethodName()));
            outboundDto.setDeliveryTime(dto.getDeliveryTime());
            outboundDto.setDeliveryAgent(dto.getDeliveryAgent());
            outboundDto.setDeliveryInfo(dto.getDeliveryInfo());
            outboundDto.setAddress(this.buildAddress(dto));
            outboundDto.setDetails(this.buildDetails(dto));
            outboundDto.setSourceType(DelOutboundConstant.SOURCE_TYPE_IMP);
            outboundDtoList.add(outboundDto);
        }
        return outboundDtoList;
    }

    public DelOutboundAddressDto buildAddress(DelOutboundImportDto dto) {
        DelOutboundAddressDto address = new DelOutboundAddressDto();
        address.setConsignee(dto.getConsignee());
        address.setCountryCode(super.countryCache.get(dto.getCountry()));
        address.setCountry(dto.getCountry());
        address.setStateOrProvince(dto.getStateOrProvince());
        address.setCity(dto.getCity());
        address.setStreet1(dto.getStreet1());
        address.setStreet2(dto.getStreet2());
        address.setPostCode(dto.getPostCode());
        address.setPhoneNo(dto.getPhoneNo());
        return address;
    }

    public List<DelOutboundDetailDto> buildDetails(DelOutboundImportDto dto) {
        List<DelOutboundDetailDto> details = new ArrayList<>();
        List<DelOutboundDetailImportDto2> list = this.detailMapList.get(dto.getSort());
        String warehouseCode = dto.getWarehouseCode();
        for (DelOutboundDetailImportDto2 dto2 : list) {
            DelOutboundDetailDto detail = new DelOutboundDetailDto();
            String sku = dto2.getSku();
            detail.setSku(sku);
            detail.setQty(Long.valueOf(dto2.getQty()));
            InventoryAvailableListVO vo = this.importValidationData.get(warehouseCode, sku);
            detail.setBindCode(vo.getBindCode());
            detail.setLength(vo.getLength());
            detail.setWidth(vo.getWidth());
            detail.setHeight(vo.getHeight());
            detail.setWeight(vo.getWeight());
            details.add(detail);
        }
        return details;
    }

    private Map<Integer, List<DelOutboundDetailImportDto2>> detailToMapList() {
        if (null == this.detailList) {
            return Collections.emptyMap();
        }
        return this.detailList.stream().collect(Collectors.groupingBy(DelOutboundDetailImportDto2::getSort));
    }
}
