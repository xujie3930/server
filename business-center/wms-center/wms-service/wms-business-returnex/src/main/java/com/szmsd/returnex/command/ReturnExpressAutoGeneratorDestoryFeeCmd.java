package com.szmsd.returnex.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.DelOutboundChargeData;
import com.szmsd.delivery.dto.ReturnExpressFeeDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.vo.DelOutboundAddressVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.*;
import com.szmsd.returnex.domain.ReturnExpressDetail;
import com.szmsd.returnex.mapper.ReturnExpressMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ReturnExpressAutoGeneratorDestoryFeeCmd extends BasicCommand<List<ReturnExpressFeeDto>> {

    private List<ReturnExpressDetail> expressDetails;

    private static final String FEE_TYPE = "销毁费";

    public ReturnExpressAutoGeneratorDestoryFeeCmd(List<ReturnExpressDetail> expressDetails){
        this.expressDetails = expressDetails;
    }

    @Override
    protected void beforeDoExecute() {

        if(CollectionUtils.isEmpty(expressDetails)){
            throw new RuntimeException("参数异常");
        }
    }

    @Override
    protected List<ReturnExpressFeeDto> doExecute() throws Exception {

        StopWatch stopWatch = new StopWatch();

        DelOutboundClientService delOutboundClientService = SpringUtils.getBean(DelOutboundClientService.class);
        IHtpPricedProductClientService iHtpPricedProductClientService = SpringUtils.getBean(IHtpPricedProductClientService.class);
        RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
        DelOutboundFeignService delOutboundFeignService1 = SpringUtils.getBean(DelOutboundFeignService.class);

        List<String> orderNoList = expressDetails.stream().map(ReturnExpressDetail::getFromOrderNo).collect(Collectors.toList());

        Map<String,List<ReturnExpressDetail>> returnExpressDetailMap = expressDetails.stream().collect(Collectors.groupingBy(ReturnExpressDetail::getFromOrderNo));

        //step 2.根据退件费单据查询最新的PRC费用、查询出库单基本信息
        stopWatch.start();
        List<DelOutboundChargeData> chargeRs = delOutboundClientService.findDelboundCharges(orderNoList);
        stopWatch.stop();
        logger.info(">>>>>findDelboundCharges：耗时{}, 内容:{}", stopWatch.getLastTaskTimeMillis(),
                JSONObject.toJSONString(chargeRs));

        if(CollectionUtils.isEmpty(chargeRs)){
            throw new RuntimeException("无法获取退件费单据查询最新的PRC费用、查询出库单基本信息");
        }

        List<ReturnExpressFeeDto> feeDtos = new ArrayList<>();

        for(DelOutboundChargeData data : chargeRs) {

            String orderNo = data.getOrderNo();
            List<ReturnExpressDetail> returnExpressDetails = returnExpressDetailMap.get(orderNo);
            if(CollectionUtils.isEmpty(returnExpressDetails)){
                continue;
            }
            ReturnExpressDetail returnExpressDetail = returnExpressDetails.get(0);

            R<DelOutboundVO> delOutboundVOR = delOutboundFeignService1.getInfoByOrderNo(data.getOrderNo());

            if(delOutboundVOR == null || delOutboundVOR.getCode() != Constants.SUCCESS){

                String errorMsg = "单号："+orderNo+",getInfoByOrderNo 异常";
                this.errorHandler(returnExpressDetail.getId(),errorMsg);
                continue;
            }

            //step 3.计算包裹的特定附加费用

            stopWatch.start();
            TagSurchargeRequest tagSurchargeRequest = this.generatorTagSurcharge(data);
            log.info("计算包裹的特定附加费用参数:{}", JSON.toJSONString(tagSurchargeRequest));
            ChargeWrapper chargeWrapper = iHtpPricedProductClientService.tagSurcharge(tagSurchargeRequest);
            stopWatch.stop();
            logger.info(">>>>>计算包裹的特定附加费用tagSurcharge：耗时{}, 内容:{}", stopWatch.getLastTaskTimeMillis(),
                    JSONObject.toJSONString(chargeWrapper));

            if(chargeWrapper == null){
                String errorMsg = "单号："+orderNo+",计算包裹的特定附加费用htp请求异常";
                this.errorHandler(returnExpressDetail.getId(),errorMsg);
                continue;
            }

            if(CollectionUtils.isEmpty(chargeWrapper.getCharges())){
                String errorMsg = "单号："+orderNo+",计算包裹的特定附加费用无数据";
                this.errorHandler(returnExpressDetail.getId(),errorMsg);
                continue;
            }

            DelOutboundAddressVO delOutboundAddressVO = delOutboundVOR.getData().getAddress();

            //step 4.调用财务扣费接口
            List<CustPayDTO> dtos = this.generatorCustDto(data,delOutboundAddressVO,chargeWrapper);

            String fee = "";
            for(CustPayDTO dto : dtos) {

                logger.info("ReturnExpressAutoGeneratorDestoryFeeCmd feeDeductions参数:{}",JSON.toJSONString(dto));
                R feeRs = rechargesFeignService.feeDeductions(dto);
                logger.info("ReturnExpressAutoGeneratorDestoryFeeCmd feeDeductions返回:{}",JSON.toJSONString(feeRs));

                if(feeRs == null){
                    String errorMsg = "单号："+orderNo+",扣费接口 异常";
                    this.errorHandler(returnExpressDetail.getId(),errorMsg);
                    continue;
                }

                if(feeRs.getCode() != Constants.SUCCESS){

                    logger.error("ReturnExpressAutoGeneratorDestoryFeeCmd 单号：{},扣费异常:{}",orderNo,feeRs.getMsg());

                    String errorMsg = "单号："+orderNo+",扣费接口 异常";
                    this.errorHandler(returnExpressDetail.getId(),errorMsg);
                    continue;
                }

                fee += dto.getAmount() + " "+ dto.getCurrencyCode()+",";
            }

            ReturnExpressFeeDto feeDto = new ReturnExpressFeeDto();
            feeDto.setId(returnExpressDetail.getId());
            feeDto.setFromOrderNo(orderNo);
            feeDto.setFee(fee);
            feeDtos.add(feeDto);
        }

        return feeDtos;
    }

    @Override
    protected void afterExecuted(List<ReturnExpressFeeDto> result) throws Exception {

        //step 5.回写销毁费数据
        ReturnExpressMapper expressMapper = SpringUtils.getBean(ReturnExpressMapper.class);

        if(CollectionUtils.isNotEmpty(result)){

            log.info("回写销毁费数据:{}",JSON.toJSONString(result));

            for(ReturnExpressFeeDto feeDto : result) {
                LambdaUpdateWrapper<ReturnExpressDetail> update = Wrappers.lambdaUpdate();
                update.set(ReturnExpressDetail::getDestoryFee, feeDto.getFee())
                        .set(ReturnExpressDetail::getDestoryFeeStatus,1)
                        .set(ReturnExpressDetail::getDestoryFeeTime,new Date())
                        .eq(ReturnExpressDetail::getId, feeDto.getId());
                expressMapper.update(null, update);
            }
        }
        super.afterExecuted(result);
    }

    @Override
    protected void rollback(String errorMsg) {

//        List<Integer> integers = expressDetails.stream().map(ReturnExpressDetail::getId).collect(Collectors.toList());
//
//        ReturnExpressMapper expressMapper = SpringUtils.getBean(ReturnExpressMapper.class);
//
//        for(Integer id : integers){
//            LambdaUpdateWrapper<ReturnExpressDetail> update = Wrappers.lambdaUpdate();
//            update.set(ReturnExpressDetail::getErrorMsg, errorMsg)
//                    .eq(ReturnExpressDetail::getId, id);
//            expressMapper.update(null,update);
//        }

        logger.error(errorMsg);

        super.rollback(errorMsg);
    }

    private void errorHandler(Integer id,String errorMsg){

        logger.error("errorHandler:{},{}",id,errorMsg);

        ReturnExpressMapper expressMapper = SpringUtils.getBean(ReturnExpressMapper.class);

        LambdaUpdateWrapper<ReturnExpressDetail> update = Wrappers.lambdaUpdate();
        update.set(ReturnExpressDetail::getErrorMsg, errorMsg)
                .eq(ReturnExpressDetail::getId, id);
        expressMapper.update(null, update);
    }

    private List<CustPayDTO> generatorCustDto(DelOutboundChargeData delOutbound,DelOutboundAddressVO address, ChargeWrapper chargeWrapper) {

        List<CustPayDTO> custPayDTOList  = new ArrayList<>();

        List<ChargeItem> chargeItems = chargeWrapper.getCharges();

        for(ChargeItem chargeItem : chargeItems){

            Money money = chargeItem.getMoney();
            Double amount = money.getAmount();

            CustPayDTO custPayDTO = new CustPayDTO();

            custPayDTO.setCusCode(delOutbound.getSellerCode());
            custPayDTO.setCurrencyCode(money.getCurrencyCode());
            custPayDTO.setAmount(toBigDecimal(amount));
            custPayDTO.setNo(delOutbound.getOrderNo());
            custPayDTO.setPayMethod(BillEnum.PayMethod.BALANCE_DEDUCTIONS);
            // 查询费用明细
            List<AccountSerialBillDTO> serialBillInfoList = new ArrayList<>(chargeItems.size());
            AccountSerialBillDTO serialBill = new AccountSerialBillDTO();
            serialBill.setNo(delOutbound.getOrderNo());
            serialBill.setTrackingNo(delOutbound.getTrackingNo());
            serialBill.setCusCode(delOutbound.getSellerCode());
            serialBill.setCurrencyCode(money.getCurrencyCode());
            serialBill.setAmount(toBigDecimal(money.getAmount()));
            serialBill.setWarehouseCode(delOutbound.getWarehouseCode());
            serialBill.setChargeCategory(chargeItem.getChargeCategory().getChargeNameCN());
            serialBill.setChargeType(chargeItem.getChargeCategory().getChargeNameCN());
            serialBill.setOrderTime(delOutbound.getCreateTime());
            serialBill.setPaymentTime(delOutbound.getShipmentsTime());
            serialBill.setProductCode(delOutbound.getShipmentRule());
            serialBill.setShipmentRule(delOutbound.getShipmentRule());
            serialBill.setShipmentRuleName(delOutbound.getShipmentRuleName());
            serialBill.setRemark(delOutbound.getRemark());
            serialBill.setAmazonLogisticsRouteId(delOutbound.getAmazonLogisticsRouteId());
            serialBill.setCountry(address.getCountry());
            serialBill.setCountryCode(address.getCountryCode());
            serialBill.setGrade(delOutbound.getGrade());
            serialBill.setWeight(delOutbound.getWeight());
            serialBill.setCalcWeight(delOutbound.getCalcWeight());
            serialBill.setRefNo(delOutbound.getRefNo());
            serialBill.setShipmentService(delOutbound.getShipmentService());
            serialBill.setNature("物流消费");

            if(delOutbound.getOrderType().equals(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode())){
                serialBill.setBusinessType("国内直发");
            }else{
                serialBill.setBusinessType("仓储服务");
            }
            serialBill.setChargeCategory(FEE_TYPE);
            serialBill.setChargeType(FEE_TYPE);
            serialBill.setChargeCategoryChange(FEE_TYPE);

            serialBillInfoList.add(serialBill);
            custPayDTO.setSerialBillInfoList(serialBillInfoList);
            custPayDTO.setOrderType("Freight");
            custPayDTOList.add(custPayDTO);
        }

        return custPayDTOList;
    }

    private TagSurchargeRequest generatorTagSurcharge(DelOutboundChargeData data) {

        TagSurchargeRequest tagSurchargeRequest = new TagSurchargeRequest();

        tagSurchargeRequest.setTags(Arrays.asList(FEE_TYPE));
        tagSurchargeRequest.setGrade(data.getGrade());
        tagSurchargeRequest.setFinalPricingProductCode(data.getPrcInterfaceProductCode());
        tagSurchargeRequest.setProductCode(data.getPrcInterfaceProductCode());
        Packing packing = new Packing();
        packing.setLength(toBigDecimal(data.getLength()));
        packing.setWidth(toBigDecimal(data.getWidth()));
        packing.setHeight(toBigDecimal(data.getHeight()));
        packing.setLengthUnit("cm");
        tagSurchargeRequest.setPacking(packing);
        Weight weight = new Weight();
        weight.setValue(data.getCalcWeight());
        weight.setUnit(data.getCalcWeightUnit());
        tagSurchargeRequest.setCalcWeight(weight);
        Weight acweight = new Weight();
        acweight.setUnit("g");
        acweight.setValue(toBigDecimal(data.getWeight()));
        tagSurchargeRequest.setActualWeight(acweight);
        tagSurchargeRequest.setZoneName(data.getZoneName());
        tagSurchargeRequest.setClientCode(data.getCustomCode());

        return tagSurchargeRequest;
    }

    private BigDecimal toBigDecimal(Double d){
        if(d == null){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(d);
    }
}
