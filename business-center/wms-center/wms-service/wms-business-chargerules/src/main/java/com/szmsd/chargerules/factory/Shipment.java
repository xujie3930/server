package com.szmsd.chargerules.factory;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.vo.DelOutboundDetailListVO;
import com.szmsd.finance.enums.BillEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 出库
 */
@Slf4j
@Component
public class Shipment extends OrderType {

    @Resource
    private IPayService payService;

    @Resource
    private IChargeLogService chargeLogService;

    @Resource
    private DelOutboundFeignService delOutboundFeignService;

    @Override
    public String findOrderById(String orderNo) {
        R<DelOutbound> info = delOutboundFeignService.details(orderNo);
        if(info.getCode() == 200 && info.getData() != null) {
            return info.getData().getCustomCode();
        }
        log.error("checkOrderExist error: {}",info.getData());
        return null;
    }

    @Override
    public void operationPay(Operation operation) {
        DelOutboundListQueryDto delOutbound = new DelOutboundListQueryDto();
        delOutbound.setOrderType(operation.getOperationType());
        R<List<DelOutboundDetailListVO>> rList = delOutboundFeignService.getDelOutboundDetailsList(delOutbound);
        if (rList.getCode() != 200 || CollectionUtils.isEmpty(rList.getData())) {
            log.error("getDelOutboundDetailsList() failed: {} {}", rList.getMsg(), rList.getData());
            return;
        }
        calculate(operation, rList);
    }

    private void calculate(Operation operation, R<List<DelOutboundDetailListVO>> rList) {
        for (DelOutboundDetailListVO datum : rList.getData()) {
            List<DelOutboundDetailDto> details = datum.getDetails();
            if (CollectionUtils.isEmpty(details)) {
                log.error("pay() {}","出库单对应的详情信息未找到");
            }
            int count = details.stream().mapToInt(detail -> detail.getQty().intValue()).sum();
            BigDecimal amount = payService.calculate(operation.getFirstPrice(), operation.getNextPrice(), count);
            log.info("orderNo: {} orderType: {} count: {} amount: {}", datum.getOrderNo(), datum.getOrderType(), count, amount);
            pay(datum, amount);
        }
    }

    private void pay(DelOutboundDetailListVO datum, BigDecimal amount) {
        ChargeLogDto chargeLogDto = new ChargeLogDto(datum.getOrderNo(),
                BillEnum.PayMethod.BUSINESS_OPERATE.getPaymentName(),datum.getOrderType(), true);
        ChargeLog exist = chargeLogService.selectLog(chargeLogDto);
        if (Objects.isNull(exist)) {
            log.info("该单已经扣过费, chargeLogDto: {}", chargeLogDto);
            return;
        }
        ChargeLog chargeLog = new ChargeLog(datum.getOrderNo(), datum.getOrderType());
        R resultPay = payService.pay(datum.getCustomCode(), amount, BillEnum.PayMethod.BUSINESS_OPERATE, chargeLog);
        if (resultPay.getCode() != 200) {
            log.error("executeOperation() pay failed.. msg: {},data: {}", resultPay.getMsg(), resultPay.getData());
        }
    }

}
