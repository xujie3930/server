package com.szmsd.chargerules.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.dto.BaseProductBatchQueryDto;
import com.szmsd.bas.dto.BaseProductMeasureDto;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.dto.OperationQueryDTO;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.chargerules.service.IChaOperationService;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.chargerules.vo.ChaOperationDetailsVO;
import com.szmsd.chargerules.vo.ChaOperationVO;
import com.szmsd.chargerules.vo.OperationRuleVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.delivery.vo.DelOutboundOperationDetailVO;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OperationServiceImpl implements IOperationService {

    @Resource
    private IPayService payService;
    @Resource
    private IChaOperationService iChaOperationService;
    @Resource
    private IChargeLogService chargeLogService;


    @Transactional
    @Override
    public R<?> delOutboundDeductions(DelOutboundOperationVO dto) {
        ChargeLog chargeLog = this.selectLog(dto.getOrderNo());
        AssertUtil.notNull(chargeLog, "??????????????????????????????????????? orderNo: " + dto.getOrderNo());
        chargeLog.setPayMethod(BillEnum.PayMethod.BALANCE_DEDUCTIONS.name());
        CustPayDTO custPayDTO = setCustPayDto(chargeLog);
        return payService.pay(custPayDTO, chargeLog);
    }

    private ChargeLog selectLog(String orderNo) {
        ChargeLogDto chargeLogDto = new ChargeLogDto();
        chargeLogDto.setOrderNo(orderNo);
        chargeLogDto.setPayMethod(BillEnum.PayMethod.BALANCE_FREEZE.name());
        chargeLogDto.setOperationPayMethod(BillEnum.PayMethod.BUSINESS_OPERATE.getPaymentName());
        chargeLogDto.setSuccess(true);
        chargeLogDto.setHasFreeze(true); // ???????????????????????????????????????????????????????????????
        return chargeLogService.selectLog(chargeLogDto);
    }

    @Transactional
    @Override
    public R<?> delOutboundFreeze(DelOutboundOperationVO dto) {
        List<DelOutboundOperationDetailVO> details = dto.getDetails();
        if (CollectionUtils.isEmpty(details) && !"Freight".equals(dto.getOrderType())) {
            log.error("calculate() ??????????????????????????????");
            return R.failed("??????????????????????????????");
        }
        BigDecimal amount = BigDecimal.ZERO;
        if (dto.getOrderType().equals(DelOutboundOrderEnum.FREEZE_IN_STORAGE.getCode())) {
            log.info("???????????? - 1 frozenFeesForWarehousing---------------------------- {}", dto);
            return frozenFeesForWarehousing(dto, amount);
        }

        if (dto.getOrderType().equals(DelOutboundOrderEnum.COLLECTION.getCode())) {
            log.info("???????????? - 1 chargeCollection---------------------------- {}", dto);
            return chargeCollection(dto, details);
        }

        if (dto.getOrderType().equals(DelOutboundOrderEnum.PACKAGE_TRANSFER.getCode())) {
            log.info("???????????? - 1 packageTransfer---------------------------- {}", dto);
            return packageTransfer(dto, amount);
        }

        if (dto.getOrderType().equals(DelOutboundOrderEnum.BATCH.getCode())) {
            log.info("???????????? - 1 chargeBatch---------------------------- {}", dto);
            return chargeBatch(dto, details);
        }
        if (dto.getOrderType().equals(DelOutboundOrderEnum.PackageCollection.getCode())) {
            log.info("???????????? - 1 PackageCollection---------------------------- {}", dto);
            return packageCollection(dto);
        }
        return this.calculateFreeze(dto, details, amount);

    }

    /**
     * ???????????? ???????????? ?????????????????????????????????????????????
     *
     * @param dto
     * @return
     */
    private R<?> packageCollection(DelOutboundOperationVO dto) {
        BigDecimal amount = calculateCostForDetails(dto);
        Long count = dto.getDetails().stream().map(DelOutboundOperationDetailVO::getQty).filter(Objects::nonNull).reduce(Long::sum).orElse(0L);
        return this.freezeBalance(dto, count, amount, new OperationRuleVO().setCurrencyCode(dto.getCurrency()));
    }

    /**
     * ????????????
     * @param dto
     * @return
     */
    private BigDecimal calculateCostForDetails(DelOutboundOperationVO dto) {
        List<DelOutboundOperationDetailVO> details = dto.getDetails();
        BigDecimal amount = BigDecimal.ZERO;
        for (DelOutboundOperationDetailVO vo : details) {
            BigDecimal calculateWeight = Optional.ofNullable(vo.getWeight()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
            BigDecimal calculateNum = Optional.ofNullable(vo.getQty()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
            String orderType = dto.getOrderType();
            OperationRuleVO operation = getOperationDetails(dto, OrderTypeEnum.Shipment, calculateWeight.doubleValue(), "?????????" + OrderTypeEnum.Shipment + "???????????????????????????????????????");
            String currencyCode = operation.getCurrencyCode();
            dto.setCurrency(currencyCode);
            BigDecimal calculateAmount = calculateCostForDetails(operation, calculateWeight, calculateNum);
            amount = amount.add(calculateAmount);
        }
        return amount;
    }

    /**
     * ???????????????kg???????????????*????????????
     * ????????? ?????? ???????????????????????????
     *
     * @param operation       ????????????
     * @param calculateWeight ???????????? ??????/??????
     * @return ????????????
     */
    private BigDecimal calculateCostForDetails(OperationRuleVO operation, BigDecimal calculateWeight, BigDecimal calculateNum) {
        String unit = operation.getUnit();
        calculateWeight = Optional.ofNullable(calculateWeight).orElse(BigDecimal.ZERO);
        calculateNum = Optional.ofNullable(calculateNum).orElse(BigDecimal.ZERO);
        BigDecimal discountRate = Optional.ofNullable(operation.getDiscountRate()).orElse(BigDecimal.ONE);
        BigDecimal amount = BigDecimal.ZERO;
        if ("kg".equalsIgnoreCase(unit)) {
            // g->kg
            calculateWeight = calculateWeight.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
            BigDecimal weightAmount = operation.getFirstPrice().multiply(calculateWeight);
            weightAmount = weightAmount.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
            amount = amount.add(weightAmount);
        } else {
            BigDecimal firstPrice = operation.getFirstPrice();
            BigDecimal nextPrice = operation.getNextPrice();
            BigDecimal numAmount = calculateNum.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : firstPrice.add(nextPrice.multiply(calculateNum.subtract(BigDecimal.ONE)));
            numAmount = numAmount.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
            amount = amount.add(numAmount);
        }
        return amount;
    }

    /**
     * ??????????????? ?????????????????????????????????
     *
     * @param dto dto
     * @return result
     */
    private R<?> packageTransfer(DelOutboundOperationVO dto, BigDecimal amount) {
        List<DelOutboundOperationDetailVO> details = dto.getDetails();
        Long count = details.stream().mapToLong(DelOutboundOperationDetailVO::getQty).sum();
        OperationRuleVO operation = getOperationDetails(dto, Double.valueOf(count), "?????????" + dto.getOrderType() + "???????????????????????????????????????");
        for (DelOutboundOperationDetailVO vo : details) {
            amount = payService.calculate(operation.getFirstPrice(), operation.getNextPrice(), vo.getQty()).add(amount);
            log.info("orderNo: {} orderType: {} amount: {}", dto.getOrderNo(), dto.getOrderType(), amount);
        }
        amount = amount.multiply(operation.getDiscountRate()).setScale(2, RoundingMode.HALF_UP);
        return this.freezeBalance(dto, count, operation.getFirstPrice().multiply(operation.getDiscountRate()).setScale(2, RoundingMode.HALF_UP), operation);
    }

    @Resource
    private BaseProductFeignService baseProductFeignService;

    /**
     * ??????????????????
     * ?????????????????????????????????????????? ?????? ??????????????????????????? ???????????????kg???????????????*????????????
     *
     * @param dto
     * @param amount ????????????
     * @return
     */
    private R<?> frozenFeesForWarehousing(DelOutboundOperationVO dto, BigDecimal amount) {
        List<DelOutboundOperationDetailVO> details = dto.getDetails();
        Long count = details.stream().mapToLong(DelOutboundOperationDetailVO::getQty).sum();
        OperationRuleVO operation = new OperationRuleVO();
        List<String> skuList = details.stream().map(DelOutboundOperationDetailVO::getSku).collect(Collectors.toList());
        BaseProductBatchQueryDto baseProductBatchQueryDto = new BaseProductBatchQueryDto();
        baseProductBatchQueryDto.setSellerCode(dto.getCustomCode());
        baseProductBatchQueryDto.setCodes(skuList);
        log.info("???????????? - 3 frozenFeesForWarehousing - batchSKU ---------------------------- {}", dto);
        R<List<BaseProductMeasureDto>> listR = baseProductFeignService.batchSKU(baseProductBatchQueryDto);
        List<BaseProductMeasureDto> dataAndException = R.getDataAndException(listR);
        Map<String, Double> skuWeightMap = dataAndException.stream().collect(Collectors.toMap(BaseProductMeasureDto::getCode, BaseProductMeasureDto::getWeight));
        for (DelOutboundOperationDetailVO vo : details) {
            double weight = Optional.ofNullable(skuWeightMap.get(vo.getSku())).orElse(0.00);
            operation = getOperationDetails(dto, OrderTypeEnum.Receipt, weight, "?????????" + dto.getOrderType() + "???????????????????????????????????????");
            String unit = operation.getUnit();
            BigDecimal discountRate = operation.getDiscountRate();
            discountRate = Optional.ofNullable(discountRate).orElse(BigDecimal.ONE);
            if ("kg".equalsIgnoreCase(unit)) {
                BigDecimal multiply = operation.getFirstPrice().multiply(new BigDecimal((weight * vo.getQty() / 1000) + "").setScale(4, RoundingMode.HALF_UP));
                multiply = multiply.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
                amount = multiply.add(amount);
            } else {
                BigDecimal calculate = payService.calculate(operation.getFirstPrice(), operation.getNextPrice(), vo.getQty());
                calculate = calculate.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
                amount = calculate.add(amount);
            }
            log.info("orderNo: {} orderType: {} amount: {} getDiscountRate{}", dto.getOrderNo(), dto.getOrderType(), amount, discountRate);
        }
        return this.freezeBalance(dto, count, amount, operation);
    }

    /**
     * ?????????????????????????????? ??????????????????????????????
     * ?????????????????????????????????????????? ?????? ??????????????????????????? ???????????????kg???????????????*????????????
     *
     * @param dto     dto
     * @param details details
     * @param amount  amount
     * @return result
     */
    private R calculateFreeze(DelOutboundOperationVO dto, List<DelOutboundOperationDetailVO> details, BigDecimal amount) {
        log.info("???????????? - 3 calculateFreeze---------------------------- {}", dto);
        Long qty;
        Long count = details.stream().mapToLong(DelOutboundOperationDetailVO::getQty).sum();
        OperationRuleVO operation = new OperationRuleVO();
        List<String> skuList = details.stream().map(DelOutboundOperationDetailVO::getSku).collect(Collectors.toList());
        BaseProductBatchQueryDto baseProductBatchQueryDto = new BaseProductBatchQueryDto();
        baseProductBatchQueryDto.setSellerCode(dto.getCustomCode());
        baseProductBatchQueryDto.setCodes(skuList);
        log.info("???????????? - 3 calculateFreeze - ???sku ---------------------------- {}", dto);
        R<List<BaseProductMeasureDto>> listR = baseProductFeignService.batchSKU(baseProductBatchQueryDto);
        List<BaseProductMeasureDto> dataAndException = R.getDataAndException(listR);
        Map<String, Double> skuWeightMap = dataAndException.stream().collect(Collectors.toMap(BaseProductMeasureDto::getCode, BaseProductMeasureDto::getWeight));
        log.info("???????????? - 3 calculateFreeze - ????????? ---------------------------- {}", dto);
        for (DelOutboundOperationDetailVO vo : details) {
            double weight = Optional.ofNullable(skuWeightMap.get(vo.getSku())).orElse(0.00);
            operation = getOperationDetails(dto, weight, "?????????" + DelOutboundOrderEnum.getName(dto.getOrderType()) + "???????????????????????????????????????");
            String unit = operation.getUnit();
            BigDecimal discountRate = operation.getDiscountRate();
            discountRate = Optional.ofNullable(discountRate).orElse(BigDecimal.ONE);
            if ("kg".equalsIgnoreCase(unit)) {
                BigDecimal bigDecimal = operation.getFirstPrice().multiply(new BigDecimal(weight * vo.getQty() / 1000 + "").setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
                bigDecimal = bigDecimal.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
                amount = bigDecimal.add(amount);
            } else {
                BigDecimal calculate = payService.calculate(operation.getFirstPrice(), operation.getNextPrice(), vo.getQty());
                calculate = calculate.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
                amount = calculate.add(amount);
            }
            log.info("orderNo: {} orderType: {} amount: {}", dto.getOrderNo(), dto.getOrderType(), amount);
        }
        log.info("???????????? - 3 calculateFreeze - ???????????? ---------------------------- {}", dto);
        return this.freezeBalance(dto, count, amount, operation);
    }

    /**
     * ???????????????
     *
     * @param dto     dto
     * @param details details
     * @return result
     */
    private R<?> chargeCollection(DelOutboundOperationVO dto, List<DelOutboundOperationDetailVO> details) {
        BigDecimal amount = BigDecimal.ZERO;
        if (details.size() > 1) {
            dto.setOrderType(DelOutboundOrderEnum.COLLECTION_MANY_SKU.getCode());
            return this.calculateFreeze(dto, details, amount);
        }
        return this.calculateFreeze(dto, details, amount);
    }

    /**
     * ??????????????????????????????
     * ?????????????????????????????????????????????
     * ?????????????????????????????????????????????????????????
     * ?????????=???????????????+?????????+?????????
     *
     * @param dto     dto
     * @param details details
     * @return result
     */
    private R<?> chargeBatch(DelOutboundOperationVO dto, List<DelOutboundOperationDetailVO> details) {

        //???????????????
        Integer packingCount = dto.getPackingCount();
        BigDecimal amount = BigDecimal.ZERO;
        amount = getBatchAmount(dto, amount, packingCount, DelOutboundOrderEnum.BATCH_PACKING.getCode());

        //???????????????
        Integer shipmentLabelCount = dto.getShipmentLabelCount();
        amount = getBatchAmount(dto, amount, shipmentLabelCount, DelOutboundOrderEnum.BATCH_LABEL.getCode());

        return this.calculateFreeze(dto, details, amount);
    }

    private BigDecimal getBatchAmount(DelOutboundOperationVO dto, BigDecimal amount, Integer count, String type) {
        if (count != null && count > 0) {
            OperationRuleVO labelOperation = getOperationDetails(dto, Double.valueOf(count), "?????????" + DelOutboundOrderEnum.getName(type) + "???????????????????????????????????????");
            BigDecimal discountRate = labelOperation.getDiscountRate();
            discountRate = Optional.ofNullable(discountRate).orElse(BigDecimal.ONE);
            BigDecimal calculate = payService.calculate(labelOperation.getFirstPrice(), labelOperation.getNextPrice(), count.longValue());
            amount = amount.add(calculate.multiply(discountRate).setScale(2, RoundingMode.HALF_UP));
        }
        return amount;
    }

    private OperationRuleVO getOperationDetails(DelOutboundOperationVO dto, Double weight, String message) {
        return this.getOperationDetails(dto, OrderTypeEnum.Shipment, weight, message);
    }

    /**
     * ?????????????????????????????????
     *
     * @param dto
     * @param orderTypeEnum
     * @param weight
     * @param message
     * @return
     */
    private OperationRuleVO getOperationDetails(DelOutboundOperationVO dto, OrderTypeEnum orderTypeEnum, Double weight, String message) {
        weight = Optional.ofNullable(weight).orElse(Double.valueOf("1.00"));
        OperationQueryDTO operationQueryDTO = new OperationQueryDTO();
        LocalDateTime now = LocalDateTime.now();
        operationQueryDTO.setCusCodeList(dto.getCustomCode())
                .setWarehouseCode(dto.getWarehouseCode())
                .setOperationType(dto.getOrderType()).setOrderType(orderTypeEnum.name())
                .setEffectiveTime(now).setExpirationTime(now);
        ChaOperationVO chaOperationVO = iChaOperationService.queryOperationDetailByRule(operationQueryDTO);
        AssertUtil.notNull(chaOperationVO, message);
        // ???????????????????????????
        OperationRuleVO operation = new OperationRuleVO();
        BeanUtil.copyProperties(chaOperationVO, operation);
        List<ChaOperationDetailsVO> chaOperationDetailList = chaOperationVO.getChaOperationDetailList();
        BigDecimal weightD = BigDecimal.valueOf(weight);
        ChaOperationDetailsVO chaOperationDetailsVO = chaOperationDetailList.stream().filter(x -> {
            BigDecimal minimumWeight = x.getMinimumWeight();
            BigDecimal maximumWeight = x.getMaximumWeight();
            return weightD.compareTo(minimumWeight) >= 0 && weightD.compareTo(maximumWeight) < 0;
        }).findAny().orElse(null);
        AssertUtil.notNull(chaOperationDetailsVO, message);
        operation.setDiscountRate(chaOperationDetailsVO.getDiscountRate());
        operation.setMaximumWeight(chaOperationDetailsVO.getMaximumWeight().doubleValue());
        operation.setMinimumWeight(chaOperationDetailsVO.getMinimumWeight().doubleValue());
        operation.setFirstPrice(Optional.ofNullable(chaOperationDetailsVO.getFirstPrice()).orElse(BigDecimal.ZERO));
        operation.setNextPrice(Optional.ofNullable(chaOperationDetailsVO.getNextPrice()).orElse(BigDecimal.ZERO));
        operation.setUnit(chaOperationDetailsVO.getUnit());
        log.info("???????????????{}", JSONObject.toJSONString(operation));
        //  multiply(operation.getDiscountRate()).setScale(2, RoundingMode.HALF_UP);
        return operation;
    }

    private R freezeBalance(DelOutboundOperationVO dto, Long count, BigDecimal amount, OperationRuleVO operation) {
        log.info("???????????? - 4 freezeBalance ---------------------------- {}", dto);
        ChargeLog chargeLog = setChargeLog(dto, count);
        chargeLog.setHasFreeze(true);
        CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO(dto.getCustomCode(), operation.getCurrencyCode(), dto.getOrderNo(), dto.getOrderType(), amount);
        return payService.freezeBalance(cusFreezeBalanceDTO, chargeLog);
    }

    private ChargeLog setChargeLog(DelOutboundOperationVO dto, Long qty) {
        ChargeLog chargeLog = new ChargeLog();
        chargeLog.setOrderNo(dto.getOrderNo());
        chargeLog.setOperationType(dto.getOrderType());
        DelOutboundOrderEnum delOutboundOrderEnum = DelOutboundOrderEnum.get(dto.getOrderType());
        if (delOutboundOrderEnum != null){
            chargeLog.setOperationType(delOutboundOrderEnum.getName());
        }
        chargeLog.setPayMethod(BillEnum.PayMethod.BALANCE_FREEZE.name());
        chargeLog.setOperationPayMethod(BillEnum.PayMethod.BUSINESS_OPERATE.getPaymentName());
        chargeLog.setWarehouseCode(dto.getWarehouseCode());
        chargeLog.setQty(qty);
        return chargeLog;
    }

    @Transactional
    @Override
    public R delOutboundThaw(DelOutboundOperationVO dto) {
        ChargeLog chargeLog = this.selectLog(dto.getOrderNo());
        AssertUtil.notNull(chargeLog, "??????????????????????????????????????? orderNo: " + dto.getOrderNo());
        chargeLog.setPayMethod(BillEnum.PayMethod.BALANCE_THAW.name());
        CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO(chargeLog.getCustomCode(), chargeLog.getCurrencyCode(), chargeLog.getOrderNo(), chargeLog.getOperationType(), chargeLog.getAmount());
        return payService.thawBalance(cusFreezeBalanceDTO, chargeLog);
    }


    private CustPayDTO setCustPayDto(ChargeLog chargeLog) {
        CustPayDTO custPayDTO = new CustPayDTO();
        List<AccountSerialBillDTO> serialBillInfoList = new ArrayList<>();
        AccountSerialBillDTO accountSerialBillDTO = new AccountSerialBillDTO();
        accountSerialBillDTO.setChargeCategory(BillEnum.CostCategoryEnum.OPERATING_FEE.getName());
        accountSerialBillDTO.setChargeType(chargeLog.getOperationType());
        accountSerialBillDTO.setAmount(chargeLog.getAmount());
        accountSerialBillDTO.setCurrencyCode(chargeLog.getCurrencyCode());
        accountSerialBillDTO.setWarehouseCode(chargeLog.getWarehouseCode());
        serialBillInfoList.add(accountSerialBillDTO);
        custPayDTO.setCusCode(chargeLog.getCustomCode());
        custPayDTO.setPayType(BillEnum.PayType.PAYMENT);
        custPayDTO.setPayMethod(BillEnum.PayMethod.BUSINESS_OPERATE);
        custPayDTO.setCurrencyCode(chargeLog.getCurrencyCode());
        custPayDTO.setAmount(chargeLog.getAmount());
        custPayDTO.setNo(chargeLog.getOrderNo());
        custPayDTO.setSerialBillInfoList(serialBillInfoList);
        custPayDTO.setOrderType(chargeLog.getOperationType());
        return custPayDTO;
    }


}
