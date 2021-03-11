package com.szmsd.delivery.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.http.api.service.ITransactionHandler;
import com.szmsd.http.dto.CreateShipmentRequestDto;
import com.szmsd.http.dto.ShipmentAddressDto;
import com.szmsd.http.dto.ShipmentDetailInfoDto;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 19:42
 */
@Component
public class OutboundTransactionHandler implements ITransactionHandler<CreateShipmentRequestDto, CreateShipmentResponseVO> {

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;
    @Autowired
    private IDelOutboundDetailService delOutboundDetailService;

    @Override
    public CreateShipmentRequestDto get(String invoiceNo, String invoiceType) {
        LambdaQueryWrapper<DelOutbound> outboundLambdaQueryWrapper = Wrappers.lambdaQuery();
        outboundLambdaQueryWrapper.eq(DelOutbound::getOrderNo, invoiceNo);
        DelOutbound delOutbound = delOutboundService.getOne(outboundLambdaQueryWrapper);
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("999", "单据信息不存在");
        }
        CreateShipmentRequestDto requestDto = BeanMapperUtil.map(delOutbound, CreateShipmentRequestDto.class);

        LambdaQueryWrapper<DelOutboundAddress> outboundAddressLambdaQueryWrapper = Wrappers.lambdaQuery();
        outboundAddressLambdaQueryWrapper.eq(DelOutboundAddress::getOrderNo, delOutbound.getOrderNo());
        DelOutboundAddress delOutboundAddress = delOutboundAddressService.getOne(outboundAddressLambdaQueryWrapper);
        if (Objects.nonNull(delOutboundAddress)) {
            requestDto.setAddress(BeanMapperUtil.map(delOutboundAddress, ShipmentAddressDto.class));
        }

        LambdaQueryWrapper<DelOutboundDetail> outboundDetailLambdaQueryWrapper = Wrappers.lambdaQuery();
        outboundDetailLambdaQueryWrapper.eq(DelOutboundDetail::getOrderNo, delOutbound.getOrderNo());
        List<DelOutboundDetail> delOutboundDetailList = delOutboundDetailService.list(outboundDetailLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(delOutboundDetailList)) {
            requestDto.setDetails(BeanMapperUtil.mapList(delOutboundDetailList, ShipmentDetailInfoDto.class));
        }

        return requestDto;
    }

    @Override
    public void callback(CreateShipmentResponseVO responseVO, String invoiceNo, String invoiceType) {
        if (Objects.isNull(responseVO)) {
            return;
        }
        if (StringUtils.isNotEmpty(responseVO.getOrderNo())) {
            LambdaUpdateWrapper<DelOutbound> outboundLambdaUpdateWrapper = Wrappers.lambdaUpdate();
            outboundLambdaUpdateWrapper.set(DelOutbound::getRefOrderNo, responseVO.getOrderNo());
            outboundLambdaUpdateWrapper.eq(DelOutbound::getOrderNo, invoiceNo);
            this.delOutboundService.update(outboundLambdaUpdateWrapper);
        }
    }
}
