package com.szmsd.delivery.imported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangyuyuan
 */
public class DelOutboundBatchImportContainer extends DelOutboundBatchImportContext {

    private final List<DelOutboundBatchDetailImportDto> detailList;
    private final Map<Integer, List<DelOutboundBatchDetailImportDto>> detailMapList;
    private final String sellerCode;

    public DelOutboundBatchImportContainer(List<DelOutboundBatchImportDto> dataList,
                                           List<BasRegionSelectListVO> countryList,
                                           List<BasSubWrapperVO> shipmentChannelList,
                                           List<DelOutboundBatchDetailImportDto> detailList,
                                           String sellerCode) {
        super(dataList, countryList, shipmentChannelList);
        this.detailList = detailList;
        this.detailMapList = this.detailToMapList();
        this.sellerCode = sellerCode;
    }

    public List<DelOutboundDto> get() {
        List<DelOutboundBatchImportDto> dataList = super.getDataList();
        List<DelOutboundDto> outboundDtoList = new ArrayList<>();
        for (DelOutboundBatchImportDto dto : dataList) {
            DelOutboundDto outboundDto = new DelOutboundDto();
            outboundDto.setCustomCode(this.sellerCode);
            outboundDto.setWarehouseCode(dto.getWarehouseCode());
            outboundDto.setOrderType(DelOutboundOrderTypeEnum.BATCH.getCode());
            outboundDto.setSellerCode(this.sellerCode);
            outboundDto.setIsLabelBox(super.confirmCache.get(dto.getLabelBox()));
            outboundDto.setIsPackingByRequired(super.confirmCache.get(dto.getPackingByRequired()));
            outboundDto.setBoxNumber(dto.getBoxNumber());
            outboundDto.setIsDefaultWarehouse(super.confirmCache.get(dto.getDefaultWarehouse()));
            outboundDto.setShipmentChannel(super.shipmentChannelCache.get(dto.getShipmentChannel()));
            outboundDto.setAddress(this.buildAddress(dto));
            outboundDto.setDetails(this.buildDetails(dto));
            outboundDto.setDeliveryAgent(dto.getDeliveryAgent());
            outboundDto.setDeliveryInfo(dto.getDeliveryInfo());
            outboundDto.setRemark(dto.getRemark());
            outboundDto.setSourceType(DelOutboundConstant.SOURCE_TYPE_IMP);
            outboundDto.setEmail(dto.getEmail());

            outboundDtoList.add(outboundDto);
        }
        return outboundDtoList;
    }

    public DelOutboundAddressDto buildAddress(DelOutboundBatchImportDto dto) {
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

    public List<DelOutboundDetailDto> buildDetails(DelOutboundBatchImportDto dto) {
        List<DelOutboundDetailDto> details = new ArrayList<>();
        List<DelOutboundBatchDetailImportDto> list = this.detailMapList.get(dto.getSort());
        if (CollectionUtils.isEmpty(list)) {
            return details;
        }
        for (DelOutboundBatchDetailImportDto dto2 : list) {
            DelOutboundDetailDto detail = new DelOutboundDetailDto();
            detail.setSku(dto2.getSku());
            detail.setQty(dto2.getQty());
            detail.setNewLabelCode(dto2.getNewLabelCode());
            details.add(detail);
        }
        return details;
    }

    private Map<Integer, List<DelOutboundBatchDetailImportDto>> detailToMapList() {
        if (null == this.detailList) {
            return Collections.emptyMap();
        }
        return this.detailList.stream().collect(Collectors.groupingBy(DelOutboundBatchDetailImportDto::getSort));
    }
}
