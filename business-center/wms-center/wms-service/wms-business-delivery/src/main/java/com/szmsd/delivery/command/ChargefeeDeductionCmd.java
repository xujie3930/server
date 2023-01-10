package com.szmsd.delivery.command;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.dto.ChargePricingOrderMsgDto;
import com.szmsd.delivery.enums.ChargeImportStateEnum;
import com.szmsd.delivery.mapper.ChargeImportMapper;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.http.dto.ChargeItem;
import com.szmsd.http.dto.ChargeWrapper;
import com.szmsd.http.dto.Money;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChargefeeDeductionCmd extends BasicCommand<List<String>> {

    private List<String> orderNos;

    private Map<String, ChargeWrapper> chargeWrapperMap;

    public ChargefeeDeductionCmd(List<String> orderNos,Map<String, ChargeWrapper> chargeWrapperMap){
        this.orderNos = orderNos;
        this.chargeWrapperMap = chargeWrapperMap;
    }

    @Override
    protected void beforeDoExecute() {
        super.beforeDoExecute();
    }

    @Override
    protected List<String> doExecute() throws Exception {

        List<String> successOrderNos = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(orderNos) && chargeWrapperMap != null){

            IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
            //IDelOutboundChargeService iDelOutboundChargeService = SpringUtils.getBean(IDelOutboundChargeService.class);
            IDelOutboundAddressService iDelOutboundAddressService = SpringUtils.getBean(IDelOutboundAddressService.class);

            List<DelOutbound> delOutboundList = iDelOutboundService.list(Wrappers.<DelOutbound>query().lambda()
                    .in(DelOutbound::getOrderNo,orderNos)
            );

            for(DelOutbound delOutbound : delOutboundList){

                ChargeWrapper chargeList = chargeWrapperMap.get(delOutbound.getOrderNo());

                //List<DelOutboundCharge> chargeList = iDelOutboundChargeService.listCharges(delOutbound.getOrderNo());

                logger.info("ChargefeeDeductionCmd 记录：{}", JSON.toJSONString(chargeList));

                //汪总说：一个出库只会有一个地址
                List<DelOutboundAddress> addresses = iDelOutboundAddressService.list(Wrappers.<DelOutboundAddress>query().lambda().eq(DelOutboundAddress::getOrderNo,delOutbound.getOrderNo()));
                DelOutboundAddress address = new DelOutboundAddress();
                if(CollectionUtils.isNotEmpty(addresses)){
                    address = addresses.get(0);
                }

                List<ChargeItem> chargeItems = chargeList.getCharges();

                for (ChargeItem item: chargeItems) {

                    Money money = item.getMoney();
                    CustPayDTO custPayDTO = new CustPayDTO();
                    List<AccountSerialBillDTO> serialBillInfoList = new ArrayList<>(chargeItems.size());

                    // 扣减费用
                    custPayDTO.setCusCode(delOutbound.getSellerCode());
                    custPayDTO.setCurrencyCode(money.getCurrencyCode());
                    custPayDTO.setAmount(new BigDecimal(money.getAmount()));
                    custPayDTO.setNo(delOutbound.getOrderNo());
                    custPayDTO.setPayMethod(BillEnum.PayMethod.BALANCE_DEDUCTIONS);
                    // 查询费用明细
                    AccountSerialBillDTO serialBill = new AccountSerialBillDTO();
                    serialBill.setNo(delOutbound.getOrderNo());
                    serialBill.setTrackingNo(delOutbound.getTrackingNo());
                    serialBill.setCusCode(delOutbound.getSellerCode());
                    serialBill.setCurrencyCode(money.getCurrencyCode());
                    serialBill.setAmount(new BigDecimal(money.getAmount()));
                    serialBill.setWarehouseCode(delOutbound.getWarehouseCode());
                    serialBill.setChargeCategory(item.getChargeCategory().getChargeNameCN());
                    serialBill.setChargeType(item.getChargeCategory().getChargeNameCN());
                    serialBill.setOrderTime(delOutbound.getCreateTime());
                    serialBill.setPaymentTime(delOutbound.getShipmentsTime());
                    serialBill.setProductCode(delOutbound.getShipmentRule());
                    serialBill.setShipmentRule(delOutbound.getShipmentRule());
                    serialBill.setShipmentRuleName(delOutbound.getShipmentRuleName());
                    serialBill.setRemark(delOutbound.getRemark());
                    serialBill.setAmazonLogisticsRouteId(delOutbound.getAmazonLogisticsRouteId());
                    serialBill.setCountry(address.getCountry());
                    serialBill.setCountryCode(address.getCountryCode());
                    serialBill.setWeight(delOutbound.getWeight());
                    serialBill.setCalcWeight(delOutbound.getCalcWeight());

                    serialBillInfoList.add(serialBill);
                    custPayDTO.setSerialBillInfoList(serialBillInfoList);
                    custPayDTO.setOrderType("Freight");
                    logger.info("ChargefeeDeductionCmd feeDeductions 记录：{}", JSON.toJSONString(custPayDTO));
                    R<?> r = rechargesFeignService.feeDeductions(custPayDTO);
                    if (Constants.SUCCESS == r.getCode()) {
                        successOrderNos.add(delOutbound.getOrderNo());
                    }
                }
            }
        }

        if(CollectionUtils.isNotEmpty(successOrderNos)){

            return successOrderNos.stream().distinct().collect(Collectors.toList());
        }

        return null;
    }

    @Override
    protected void afterExecuted(List<String> result) throws Exception {

        if(CollectionUtils.isNotEmpty(result)){

            ChargeImportMapper chargeImportMapper = SpringUtils.getBean(ChargeImportMapper.class);

            List<ChargePricingOrderMsgDto> allData = new ArrayList<>();

            for(String s : result){
                ChargePricingOrderMsgDto chargePricingOrderMsgDto = new ChargePricingOrderMsgDto();
                chargePricingOrderMsgDto.setState(ChargeImportStateEnum.FEE_DEDUCTIONS.getCode());
                chargePricingOrderMsgDto.setOrderNo(s);
                allData.add(chargePricingOrderMsgDto);
            }

            if(CollectionUtils.isNotEmpty(allData)) {
                chargeImportMapper.batchUpd(allData);
            }
        }

        super.afterExecuted(result);
    }
}
