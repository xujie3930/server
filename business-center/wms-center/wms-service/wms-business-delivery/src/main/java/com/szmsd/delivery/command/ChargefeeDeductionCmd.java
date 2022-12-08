package com.szmsd.delivery.command;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.http.dto.ChargeWrapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChargefeeDeductionCmd extends BasicCommand<Void> {

    private Map<String, ChargeWrapper> chargeWrapperMap;

    public ChargefeeDeductionCmd(Map<String, ChargeWrapper> chargeWrapperMap){
        this.chargeWrapperMap = chargeWrapperMap;
    }


    @Override
    protected void beforeDoExecute() {
        super.beforeDoExecute();
    }

    @Override
    protected Void doExecute() throws Exception {

        if(!chargeWrapperMap.isEmpty()){

            IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);

            Set<String> orderNos = chargeWrapperMap.keySet();

            List<DelOutbound> delOutboundList = iDelOutboundService.list(Wrappers.<DelOutbound>query().lambda()
                    .in(DelOutbound::getOrderNo,orderNos)
            );

            for(DelOutbound delOutbound : delOutboundList){


            }

            // 扣减费用
            CustPayDTO custPayDTO = new CustPayDTO();
//            custPayDTO.setCusCode(delOutbound.getSellerCode());
//            custPayDTO.setCurrencyCode(currencyCode);
//            custPayDTO.setAmount(bigDecimal);
//            custPayDTO.setNo(delOutbound.getOrderNo());
//            custPayDTO.setPayMethod(BillEnum.PayMethod.BALANCE_DEDUCTIONS);
//            // 查询费用明细
//            if (CollectionUtils.isNotEmpty(groupByCharge.get(currencyCode))) {
//                List<AccountSerialBillDTO> serialBillInfoList = new ArrayList<>(chargeList.size());
//                for (DelOutboundCharge charge : groupByCharge.get(currencyCode)) {
//                    AccountSerialBillDTO serialBill = new AccountSerialBillDTO();
//                    serialBill.setNo(orderNo);
//                    serialBill.setTrackingNo(delOutbound.getTrackingNo());
//                    serialBill.setCusCode(delOutbound.getSellerCode());
//                    serialBill.setCurrencyCode(charge.getCurrencyCode());
//                    serialBill.setAmount(charge.getAmount());
//                    serialBill.setWarehouseCode(delOutbound.getWarehouseCode());
//                    serialBill.setChargeCategory(charge.getChargeNameCn());
//                    serialBill.setChargeType(charge.getChargeNameCn());
//                    serialBill.setOrderTime(delOutbound.getCreateTime());
//                    serialBill.setPaymentTime(delOutbound.getShipmentsTime());
//                    serialBill.setProductCode(delOutbound.getShipmentRule());
//                    serialBill.setShipmentRule(delOutbound.getShipmentRule());
//                    serialBill.setShipmentRuleName(delOutbound.getShipmentRuleName());
//                    serialBill.setRemark(delOutbound.getRemark());
//                    serialBill.setAmazonLogisticsRouteId(delOutbound.getAmazonLogisticsRouteId());
//                    serialBill.setCountry(address.getCountry());
//                    serialBill.setCountryCode(address.getCountryCode());
//
//                    serialBillInfoList.add(serialBill);
//                }
//                custPayDTO.setSerialBillInfoList(serialBillInfoList);
//            }
            custPayDTO.setOrderType("Freight");

            R<?> r = rechargesFeignService.feeDeductions(custPayDTO);

        }

        return null;
    }
}
