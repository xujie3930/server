package com.szmsd.chargerules.factory;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.vo.DelOutboundDetailListVO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.http.enums.HttpRechargeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
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
        log.error("findOrderById error: {}",info.getData());
        return null;
    }

    @Override
    public void operationPay(Operation operation) {
//        String orderType = DelOutboundOrderTypeEnum.getCode(operation.getOperationType());
//        if(StringUtils.isNull(orderType)) {
//            log.error("operationPay() failed: 出库单未找到对应的操作类型, operationType: {}", operation.getOperationType());
//            return;
//        }
        DelOutboundListQueryDto delOutbound = new DelOutboundListQueryDto();
        delOutbound.setOrderType(operation.getOperationType());
        delOutbound.setWarehouseCode(operation.getWarehouseCode());
        delOutbound.setUpdateTime(DateUtils.getPastDate(1));
        delOutbound.setState("COMPLETED");
        R<List<DelOutboundDetailListVO>> rList = delOutboundFeignService.getDelOutboundDetailsList(delOutbound);
        if (rList.getCode() != 200 || CollectionUtils.isEmpty(rList.getData())) {
            log.error("operationPay() failed: {} {}", rList.getMsg(), rList.getData());
            return;
        }
        calculate(operation, rList);
    }

    /**
     * 计算费用
     * @param operation operation
     * @param rList list
     */
    private void calculate(Operation operation, R<List<DelOutboundDetailListVO>> rList) {
        for (DelOutboundDetailListVO datum : rList.getData()) {
            List<DelOutboundDetailDto> details = datum.getDetails();
            if (CollectionUtils.isEmpty(details)) {
                log.error("calculate() {}","出库单对应的详情信息未找到");
            }
            BigDecimal amount = BigDecimal.ZERO;
            int count = 0;
//            if(operation.isManySku() && details.size() > 1) { // 配置为多SKU并且出库单SKU为多个
                count = details.stream().mapToInt(detail -> detail.getQty().intValue()).sum();
                amount = payService.manySkuCalculate(operation.getFirstPrice(), operation.getNextPrice(), details);
//            }
//            if(!operation.isManySku() && details.size() == 1) { // 配置为单SKU并且出库单SKU为单个
//                count = details.get(0).getQty().intValue();
//                amount = payService.calculate(operation.getFirstPrice(), operation.getNextPrice(), count);
//            }
            DelOutboundOrderTypeEnum delOutboundOrderTypeEnum = DelOutboundOrderTypeEnum.get(datum.getOrderType());
            if(delOutboundOrderTypeEnum != null) datum.setOrderType(delOutboundOrderTypeEnum.getName());
            log.info("orderNo: {} orderType: {} amount: {}", datum.getOrderNo(), datum.getOrderType(),amount);
            if(amount.compareTo(BigDecimal.ZERO) > 0) {
                pay(datum, amount, count);
            }
        }
    }

    /**
     * 支付费用记录
     * @param datum datum
     * @param amount amount
     */
    private void pay(DelOutboundDetailListVO datum, BigDecimal amount,Integer count) {
        ChargeLogDto chargeLogDto = new ChargeLogDto(datum.getOrderNo(),
                BillEnum.PayMethod.BUSINESS_OPERATE.getPaymentName(),datum.getOrderType(), datum.getWarehouseCode(),true);
        ChargeLog exist = chargeLogService.selectLog(chargeLogDto);
        if (Objects.nonNull(exist)) {
            log.info("该单已经扣过费, chargeLogDto: {}", chargeLogDto);
            return;
        }
        ChargeLog chargeLog = new ChargeLog(datum.getOrderNo(), datum.getOrderType(), datum.getWarehouseCode(),count.longValue());
        CustPayDTO custPayDTO = setCustPayDto(datum, amount, chargeLog);
        R resultPay = payService.pay(custPayDTO, chargeLog);
        if (resultPay.getCode() != 200)
            log.error("pay() pay failed.. msg: {},data: {}", resultPay.getMsg(), resultPay.getData());

    }

    private CustPayDTO setCustPayDto(DelOutboundDetailListVO datum, BigDecimal amount, ChargeLog chargeLog) {
        CustPayDTO custPayDTO = new CustPayDTO();
        List<AccountSerialBillDTO> serialBillInfoList = new ArrayList<>();
        AccountSerialBillDTO accountSerialBillDTO = new AccountSerialBillDTO();
        accountSerialBillDTO.setChargeCategory("操作费");
        accountSerialBillDTO.setChargeType(datum.getOrderType());
        accountSerialBillDTO.setRemark(datum.getRemark());
        accountSerialBillDTO.setAmount(amount);
        accountSerialBillDTO.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        serialBillInfoList.add(accountSerialBillDTO);
        accountSerialBillDTO.setWarehouseCode(datum.getWarehouseCode());
        custPayDTO.setCusCode(datum.getCustomCode());
        custPayDTO.setPayType(BillEnum.PayType.PAYMENT);
        custPayDTO.setPayMethod(BillEnum.PayMethod.BUSINESS_OPERATE);
        custPayDTO.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        custPayDTO.setAmount(amount);
        custPayDTO.setNo(chargeLog.getOrderNo());
        custPayDTO.setSerialBillInfoList(serialBillInfoList);
        return custPayDTO;
    }

}
