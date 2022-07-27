package com.szmsd.delivery.imported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 21:49
 */
public class DelOutboundCollectionImportContainer extends DelOutboundCollectionImportContext {

    private final List<DelOutboundDetailImportDto2> detailList;
    private final Map<Integer, List<DelOutboundDetailImportDto2>> detailMapList;
    private final ImportValidationData importValidationData;
    private final String sellerCode;

    public DelOutboundCollectionImportContainer(List<DelOutboundCollectionImportDto> dataList,
                                                List<BasRegionSelectListVO> countryList,
                                                List<DelOutboundDetailImportDto2> detailList,
                                                ImportValidationData importValidationData,
                                                String sellerCode) {
        super(dataList, countryList);
        this.detailList = detailList;
        this.detailMapList = this.detailToMapList();
        this.importValidationData = importValidationData;
        this.sellerCode = sellerCode;
    }

    public List<DelOutboundDto> get() {
        List<DelOutboundCollectionImportDto> dataList = super.getDataList();
        List<DelOutboundDto> outboundDtoList = new ArrayList<>();
        for (DelOutboundCollectionImportDto dto : dataList) {
            DelOutboundDto outboundDto = new DelOutboundDto();
            outboundDto.setCustomCode(this.sellerCode);
            outboundDto.setWarehouseCode(dto.getWarehouseCode());
            outboundDto.setOrderType(DelOutboundOrderTypeEnum.COLLECTION.getCode());
            outboundDto.setSellerCode(this.sellerCode);
            outboundDto.setShipmentRule(dto.getShipmentRule());
            outboundDto.setWeight(dto.getWeight());
            outboundDto.setLength(dto.getLength());
            outboundDto.setWidth(dto.getWidth());
            outboundDto.setHeight(dto.getHeight());
            outboundDto.setAddress(this.buildAddress(dto));
            outboundDto.setDetails(this.buildDetails(dto));
            outboundDto.setSourceType(DelOutboundConstant.SOURCE_TYPE_IMP);
            outboundDtoList.add(outboundDto);
        }
        return outboundDtoList;
    }

    public DelOutboundAddressDto buildAddress(DelOutboundCollectionImportDto dto) {
        DelOutboundAddressDto address = new DelOutboundAddressDto();
        address.setConsignee(dto.getConsignee());
        address.setCountryCode(super.getCountryCodeCache(dto.getCountry(), this.countryCodeCache, this.countryCache));
        address.setCountry(super.getCountryNameCache(dto.getCountry(), this.countryCodeCache, this.countryCache));
        address.setStateOrProvince(dto.getStateOrProvince());
        address.setCity(dto.getCity());
        address.setStreet1(dto.getStreet1());
        address.setStreet2(dto.getStreet2());
        address.setPostCode(super.stringNumber(dto.getPostCode()));
        address.setPhoneNo(super.stringNumber(dto.getPhoneNo()));
        address.setEmail(dto.getEmail());
        return address;
    }

    public List<DelOutboundDetailDto> buildDetails(DelOutboundCollectionImportDto dto) {
        List<DelOutboundDetailDto> details = new ArrayList<>();
        List<DelOutboundDetailImportDto2> list = this.detailMapList.get(dto.getSort());
        if (CollectionUtils.isEmpty(list)) {
            return details;
        }
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
