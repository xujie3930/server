package com.szmsd.delivery.convert;

import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundChargeData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DelOutboundChargeConvert {

    DelOutboundChargeConvert INSTANCE = Mappers.getMapper(DelOutboundChargeConvert.class);

    DelOutboundChargeData toDelOutboundCharge(DelOutbound delOutbound);

    List<DelOutboundChargeData> toDelOutboundChargeList(List<DelOutbound> delOutbounds);

}
