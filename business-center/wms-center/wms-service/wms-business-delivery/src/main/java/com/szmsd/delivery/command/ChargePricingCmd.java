package com.szmsd.delivery.command;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.support.Context;
import com.szmsd.common.core.utils.BigDecimalUtil;
import com.szmsd.common.core.utils.MessageUtil;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.ChargeImport;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.dto.ChargePricingOrderMsgDto;
import com.szmsd.delivery.dto.ChargePricingResultDto;
import com.szmsd.delivery.enums.ChargeImportStateEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.mapper.ChargeImportMapper;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.DelOutboundWrapperContext;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.service.wrapper.PricingEnum;
import com.szmsd.delivery.util.Utils;
import com.szmsd.finance.api.feign.ConvertUnitFeignService;
import com.szmsd.finance.domain.FssConvertUnit;
import com.szmsd.http.dto.*;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ChargePricingCmd extends BasicCommand<ChargePricingResultDto> {

    private List<String> orderNos;

    public ChargePricingCmd(List<String> orderNos){

        this.orderNos = orderNos;
    }

    @Override
    protected void beforeDoExecute() {
        if(CollectionUtils.isEmpty(orderNos)){
            throw new RuntimeException("ChargePricingCmd ????????????");
        }
    }

    @Override
    protected ChargePricingResultDto doExecute() throws Exception {

        IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);
        IDelOutboundChargeService delOutboundChargeService = SpringUtils.getBean(IDelOutboundChargeService.class);
        IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);

        ChargeImportMapper chargeImportMapper = SpringUtils.getBean(ChargeImportMapper.class);

        //??????????????????
        List<DelOutbound> delOutbounds = iDelOutboundService.list(Wrappers.<DelOutbound>query().lambda().in(DelOutbound::getOrderNo,orderNos));
        StopWatch stopWatch = new StopWatch();

        ChargePricingResultDto chargePricingResultDto = new ChargePricingResultDto();

        List<ChargePricingOrderMsgDto> errorResultData = new ArrayList<>();
        List<ChargePricingOrderMsgDto> successResultData = new ArrayList<>();
        Map<String,ChargeWrapper> chargeWrapperMap = new HashMap<>();

        ConvertUnitFeignService convertUnitFeignService = Context.getBean(ConvertUnitFeignService.class);

        R<List<FssConvertUnit>> convertUnitRs = convertUnitFeignService.findAll();

        if(convertUnitRs == null || convertUnitRs.getCode() != Constants.SUCCESS){
            throw new RuntimeException("??????????????????????????????????????????");
        }

        List<FssConvertUnit> fssConvertUnitList = convertUnitRs.getData();

        if(org.apache.commons.collections4.CollectionUtils.isEmpty(fssConvertUnitList)){
            throw new CommonException("400", "ConvertUnit????????????");
        }

        Map<String,FssConvertUnit> fssConvertUnitMap = fssConvertUnitList.stream().collect(Collectors.toMap(FssConvertUnit::getCalcUnit, v->v));

        for(DelOutbound delOutbound : delOutbounds) {

            List<ChargeImport> chargeImportList = chargeImportMapper.selectList(Wrappers.<ChargeImport>query().lambda()
                    .eq(ChargeImport::getOrderNo,delOutbound.getOrderNo())
                    .eq(ChargeImport::getState,ChargeImportStateEnum.UPDATE_ORDER.getCode()));

            ChargePricingOrderMsgDto chargePricingOrderMsgDto = new ChargePricingOrderMsgDto();

            DelOutboundWrapperContext delOutboundWrapperContext = delOutboundBringVerifyService.initContext(delOutbound);

            if(CollectionUtils.isNotEmpty(chargeImportList)) {

                ChargeImport chargeImport = chargeImportList.get(0);

                FssConvertUnit fssConvertUnit = fssConvertUnitMap.get(chargeImport.getWeightUnit());

                DelOutbound outbound = delOutboundWrapperContext.getDelOutbound();
                outbound.setLength(1D);
                outbound.setWidth(1D);
                outbound.setHeight(1D);

                if(fssConvertUnit != null){
                    BigDecimal convertValue = fssConvertUnit.getConvertValue();
                    BigDecimal packcalcWeight = BigDecimalUtil.setScale(chargeImport.getCalcWeight().multiply(convertValue));
                    outbound.setWeight(packcalcWeight.doubleValue());
                }

                delOutboundWrapperContext.setDelOutbound(outbound);
            }

            stopWatch.start();
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = delOutboundBringVerifyService.pricing(delOutboundWrapperContext, PricingEnum.PACKAGE);
            stopWatch.stop();
            logger.info(">>>>>[ChargePricingCmd{}]-Pricing???????????????????????????{}, ??????:{}", delOutbound.getOrderNo(), stopWatch.getLastTaskTimeMillis(),
                    JSONObject.toJSONString(responseObject));
            if (null == responseObject) {

                logger.error("ChargePricingCmd PRC????????????:{},{}",delOutbound.getOrderNo(),"Failed to calculate the package fee");

                chargePricingOrderMsgDto.setErrorMsg("????????????????????????");
                chargePricingOrderMsgDto.setState(ChargeImportStateEnum.PRC_ING.getCode());
                chargePricingOrderMsgDto.setOrderNo(delOutbound.getOrderNo());
                errorResultData.add(chargePricingOrderMsgDto);
                continue;
            }

            if (!responseObject.isSuccess()) {

                String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), MessageUtil.to("????????????????????????", "Failed to calculate the package fee")+ "2");
                // ????????????
                chargePricingOrderMsgDto.setErrorMsg(exceptionMessage);
                chargePricingOrderMsgDto.setState(ChargeImportStateEnum.PRC_ING.getCode());
                chargePricingOrderMsgDto.setOrderNo(delOutbound.getOrderNo());
                errorResultData.add(chargePricingOrderMsgDto);
                continue;
            }

            ChargePricingOrderMsgDto pricingOrderMsgDto = new ChargePricingOrderMsgDto();

            // ???????????????
            ChargeWrapper chargeWrapper = responseObject.getObject();
            ShipmentChargeInfo data = chargeWrapper.getData();
            PricingPackageInfo packageInfo = data.getPackageInfo();


            delOutbound.setPrcInterfaceProductCode(data.getProductCode());
            delOutbound.setPrcTerminalCarrier(data.getTerminalCarrier());

            // ????????????
            delOutbound.setShipmentService(data.getLogisticsRouteId());
            // ?????????code
            delOutbound.setLogisticsProviderCode(data.getLogisticsProviderCode());
            // ???????????????????????????
            delOutbound.setProductShipmentRule(data.getShipmentRule());
            delOutbound.setPackingRule(data.getPackingRule());

            // ????????????
            delOutboundWrapperContext.setPrcProductCode(data.getProductCode());
            logger.info("???????????????????????????prcProductCode???{}", data.getProductCode());
            // ????????????
            Packing packing = packageInfo.getPacking();
            delOutbound.setLength(Utils.valueOf(packing.getLength()));
            delOutbound.setWidth(Utils.valueOf(packing.getWidth()));
            delOutbound.setHeight(Utils.valueOf(packing.getHeight()));
            delOutbound.setSupplierCalcType(data.getSupplierCalcType());
            delOutbound.setSupplierCalcId(data.getSupplierCalcId());

            if(StringUtils.isNotBlank(data.getAmazonLogisticsRouteId())){
                delOutbound.setAmazonLogisticsRouteId(data.getAmazonLogisticsRouteId());
            }
            // ???????????????
            Weight calcWeight = packageInfo.getCalcWeight();
            delOutbound.setCalcWeight(calcWeight.getValue());
            delOutbound.setCalcWeightUnit(calcWeight.getUnit());
            List<ChargeItem> charges = chargeWrapper.getCharges();
            // ??????????????????
            List<DelOutboundCharge> delOutboundCharges = new ArrayList<>();
            // ????????????
            BigDecimal totalAmount = BigDecimal.ZERO;
            String totalCurrencyCode = charges.get(0).getMoney().getCurrencyCode();
            for (ChargeItem charge : charges) {
                DelOutboundCharge delOutboundCharge = new DelOutboundCharge();
                ChargeCategory chargeCategory = charge.getChargeCategory();
                delOutboundCharge.setOrderNo(delOutbound.getOrderNo());
                delOutboundCharge.setBillingNo(chargeCategory.getBillingNo());
                delOutboundCharge.setChargeNameCn(chargeCategory.getChargeNameCN());
                delOutboundCharge.setChargeNameEn(chargeCategory.getChargeNameEN());
                delOutboundCharge.setParentBillingNo(chargeCategory.getParentBillingNo());
                Money money = charge.getMoney();
                BigDecimal amount = Utils.valueOf(money.getAmount());
                delOutboundCharge.setAmount(amount);
                delOutboundCharge.setCurrencyCode(money.getCurrencyCode());
                delOutboundCharge.setRemark(charge.getRemark());
                delOutboundCharges.add(delOutboundCharge);
                totalAmount = totalAmount.add(amount);
            }
            // ???????????????????????????

            stopWatch.start();
            delOutboundChargeService.saveCharges(delOutboundCharges);
            stopWatch.stop();
            logger.info(">>>>>[ChargePricingCmd{}]-Pricing????????????????????????????????????{}", delOutbound.getOrderNo(), stopWatch.getLastTaskTimeMillis());
            // ?????????
            delOutbound.setAmount(totalAmount);
            delOutbound.setCurrencyCode(totalCurrencyCode);

            //????????????????????????
            Map<String, BigDecimal> currencyMap = new HashMap<String, BigDecimal>();
            for (DelOutboundCharge charge: delOutboundCharges){

                String currencyCode = charge.getCurrencyCode();
                BigDecimal amount = BigDecimalUtil.setScale(charge.getAmount(),3);

                if(currencyMap.containsKey(currencyCode)){
                    BigDecimal chargeamount = currencyMap.get(currencyCode).add(amount);
                    currencyMap.put(currencyCode, chargeamount);
                }else{
                    currencyMap.put(currencyCode, amount);
                }
            }

            String currencyDescribe = ArrayUtil.join(currencyMap.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue))
                    .map(e -> e.getValue() + e.getKey()).collect(Collectors.toList()).toArray(), "???");

            delOutbound.setCurrencyDescribe(currencyDescribe);

            //??????PRC????????????
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setProductShipmentRule(data.getShipmentRule());
            updateDelOutbound.setPackingRule(delOutbound.getPackingRule());
            updateDelOutbound.setPrcInterfaceProductCode(delOutbound.getPrcInterfaceProductCode());
            updateDelOutbound.setPrcTerminalCarrier(delOutbound.getPrcTerminalCarrier());
            updateDelOutbound.setAmazonReferenceId(data.getAmazonLogisticsRouteId());
            updateDelOutbound.setCurrencyDescribe(delOutbound.getCurrencyDescribe());

            iDelOutboundService.updateById(updateDelOutbound);

            pricingOrderMsgDto.setOrderNo(delOutbound.getOrderNo());
            pricingOrderMsgDto.setState(ChargeImportStateEnum.PRC_ING.getCode());

            successResultData.add(pricingOrderMsgDto);

            chargeWrapperMap.put(delOutbound.getOrderNo(),chargeWrapper);

            DelOutboundOperationLogEnum.BRV_PRC_PRICING.listener(delOutbound);
        }

        chargePricingResultDto.setSuccessOrders(successResultData);
        chargePricingResultDto.setErrorOrders(errorResultData);
        chargePricingResultDto.setChargeWrapperMap(chargeWrapperMap);

        return chargePricingResultDto;
    }

    @Override
    protected void afterExecuted(ChargePricingResultDto result) throws Exception {

        ChargeImportMapper chargeImportMapper = SpringUtils.getBean(ChargeImportMapper.class);
        List<ChargePricingOrderMsgDto> allData = new ArrayList<>();

        allData.addAll(result.getSuccessOrders());
        allData.addAll(result.getErrorOrders());

        if(CollectionUtils.isNotEmpty(allData)){
            chargeImportMapper.batchUpd(allData);
        }

        super.afterExecuted(result);
    }
}
