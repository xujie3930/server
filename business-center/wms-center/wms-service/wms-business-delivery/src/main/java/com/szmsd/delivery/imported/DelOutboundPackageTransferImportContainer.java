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
 * @date 2021-04-09 21:49
 */
public class DelOutboundPackageTransferImportContainer extends DelOutboundPackageTransferImportContext {

    private final List<DelOutboundPackageTransferDetailImportDto> detailList;
    private final Map<Integer, List<DelOutboundPackageTransferDetailImportDto>> detailMapList;
    private final String sellerCode;

    public DelOutboundPackageTransferImportContainer(List<DelOutboundPackageTransferImportDto> dataList,
                                                     List<BasRegionSelectListVO> countryList,
                                                     List<BasSubWrapperVO> packageConfirmList,
                                                     List<DelOutboundPackageTransferDetailImportDto> detailList,
                                                     String sellerCode) {
        super(dataList, countryList, packageConfirmList);
        this.detailList = detailList;
        this.detailMapList = this.detailToMapList();
        this.sellerCode = sellerCode;
    }

    public List<DelOutboundDto> get() {
        List<DelOutboundPackageTransferImportDto> dataList = super.getDataList();
        List<DelOutboundDto> outboundDtoList = new ArrayList<>();
        for (DelOutboundPackageTransferImportDto dto : dataList) {
            DelOutboundDto outboundDto = new DelOutboundDto();
            outboundDto.setCustomCode(this.sellerCode);
            outboundDto.setWarehouseCode(dto.getWarehouseCode());
            outboundDto.setOrderType(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode());
            outboundDto.setSellerCode(this.sellerCode);
            outboundDto.setShipmentRule(dto.getShipmentRule());
            outboundDto.setWeight(dto.getWeight());
            outboundDto.setLength(dto.getLength());
            outboundDto.setWidth(dto.getWidth());
            outboundDto.setHeight(dto.getHeight());
            outboundDto.setPackageConfirm(super.packageConfirmCache.get(dto.getPackageConfirmName()));
            outboundDto.setPackageWeightDeviation(dto.getPackageWeightDeviation());
            outboundDto.setAddress(this.buildAddress(dto));
            outboundDto.setDetails(this.buildDetails(dto));
            outboundDto.setSourceType(DelOutboundConstant.SOURCE_TYPE_IMP);
            outboundDto.setRefNo(dto.getRefNo());
            outboundDto.setRemark(dto.getRemark());
            outboundDto.setCodAmount(dto.getCodAmount());
            outboundDto.setIoss(dto.getIoss());
            outboundDtoList.add(outboundDto);
        }
        return outboundDtoList;
    }

    public DelOutboundAddressDto buildAddress(DelOutboundPackageTransferImportDto dto) {
        DelOutboundAddressDto address = new DelOutboundAddressDto();
        address.setConsignee(dto.getConsignee());
        address.setCountryCode(super.getCountryCodeCache(dto.getCountry(), this.countryCache, this.countryEnCache));
        address.setCountry(super.getCountryNameCache(dto.getCountry(), this.countryCodeCache, this.countryCache, this.countryEnCache));
        address.setStateOrProvince(dto.getStateOrProvince());
        address.setCity(dto.getCity());
        address.setStreet1(dto.getStreet1());
        address.setStreet2(dto.getStreet2());
        address.setPostCode(super.stringNumber(dto.getPostCode()));
        address.setPhoneNo(super.stringNumber(dto.getPhoneNo()));
        address.setEmail(dto.getEmail());

        return address;
    }

    public List<DelOutboundDetailDto> buildDetails(DelOutboundPackageTransferImportDto dto) {
        List<DelOutboundDetailDto> details = new ArrayList<>();
        List<DelOutboundPackageTransferDetailImportDto> list = this.detailMapList.get(dto.getSort());
        if (CollectionUtils.isEmpty(list)) {
            return details;
        }
        for (DelOutboundPackageTransferDetailImportDto dto2 : list) {
            DelOutboundDetailDto detail = new DelOutboundDetailDto();
            detail.setProductName(dto2.getProductName());
            detail.setProductNameChinese(dto2.getProductNameChinese());
            detail.setDeclaredValue(dto2.getDeclaredValue());
            detail.setQty(Long.valueOf(dto2.getQty()));
            detail.setProductAttribute(dto2.getProductAttribute());
            detail.setElectrifiedMode(dto2.getElectrifiedMode());
            detail.setBatteryPackaging(dto2.getBatteryPackaging());
            detail.setHsCode(dto2.getHsCode());
            details.add(detail);
        }
        return details;
    }

    private Map<Integer, List<DelOutboundPackageTransferDetailImportDto>> detailToMapList() {
        if (null == this.detailList) {
            return Collections.emptyMap();
        }
        return this.detailList.stream().collect(Collectors.groupingBy(DelOutboundPackageTransferDetailImportDto::getSort));
    }
}
