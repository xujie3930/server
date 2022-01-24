package com.szmsd.chargerules.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        AssertUtil.notNull(chargeLog, "该单没有冻结金额，无法扣款 orderNo: " + dto.getOrderNo());
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
        chargeLogDto.setHasFreeze(true); // 解冻、扣款操作需要查询此单是否存在冻结的钱
        return chargeLogService.selectLog(chargeLogDto);
    }

    @Transactional
    @Override
    public R<?> delOutboundFreeze(DelOutboundOperationVO dto) {
        List<DelOutboundOperationDetailVO> details = dto.getDetails();
        if (CollectionUtils.isEmpty(details) && !"Freight".equals(dto.getOrderType())) {
            log.error("calculate() 出库单的详情信息为空");
            return R.failed("出库单的详情信息为空");
        }
        BigDecimal amount = BigDecimal.ZERO;
        if (dto.getOrderType().equals(DelOutboundOrderEnum.FREEZE_IN_STORAGE.getCode())) {
            log.info("【执行】 - 1 frozenFeesForWarehousing---------------------------- {}", dto);
            return frozenFeesForWarehousing(dto, amount);
        }

        if (dto.getOrderType().equals(DelOutboundOrderEnum.COLLECTION.getCode())) {
            log.info("【执行】 - 1 chargeCollection---------------------------- {}", dto);
            return chargeCollection(dto, details);
        }

        if (dto.getOrderType().equals(DelOutboundOrderEnum.PACKAGE_TRANSFER.getCode())) {
            log.info("【执行】 - 1 packageTransfer---------------------------- {}", dto);
            return packageTransfer(dto, amount);
        }

        if (dto.getOrderType().equals(DelOutboundOrderEnum.BATCH.getCode())) {
            log.info("【执行】 - 1 chargeBatch---------------------------- {}", dto);
            return chargeBatch(dto, details);
        }

        return this.calculateFreeze(dto, details, amount);

    }

    /**
     * 转运出库单 转运单没有重量，有数量
     *
     * @param dto dto
     * @return result
     */
    private R<?> packageTransfer(DelOutboundOperationVO dto, BigDecimal amount) {
        List<DelOutboundOperationDetailVO> details = dto.getDetails();
        Long count = details.stream().mapToLong(DelOutboundOperationDetailVO::getQty).sum();
        OperationRuleVO operation = getOperationDetails(dto, Double.valueOf(count), "未找到" + dto.getOrderType() + "业务费用规则，请联系管理员");
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
     * 入库冻结费用
     * 按照重量匹配规则：如果单位是 件数 则按首件及续件计算 如果单位是kg：则用重量*首件价格
     *
     * @param dto
     * @param amount 首件价格
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
        log.info("【执行】 - 3 frozenFeesForWarehousing - batchSKU ---------------------------- {}", dto);
        R<List<BaseProductMeasureDto>> listR = baseProductFeignService.batchSKU(baseProductBatchQueryDto);
        List<BaseProductMeasureDto> dataAndException = R.getDataAndException(listR);
        Map<String, Double> skuWeightMap = dataAndException.stream().collect(Collectors.toMap(BaseProductMeasureDto::getCode, BaseProductMeasureDto::getWeight));
        for (DelOutboundOperationDetailVO vo : details) {
            double weight = Optional.ofNullable(skuWeightMap.get(vo.getSku())).orElse(0.00);
            operation = getOperationDetails(dto, OrderTypeEnum.Receipt, weight, "未找到" + dto.getOrderType() + "业务费用规则，请联系管理员");
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
     * 遍历出库单的详情信息 根据收费规则计算费用
     * 按照重量匹配规则：如果单位是 件数 则按首件及续件计算 如果单位是kg：则用重量*首件价格
     *
     * @param dto     dto
     * @param details details
     * @param amount  amount
     * @return result
     */
    private R calculateFreeze(DelOutboundOperationVO dto, List<DelOutboundOperationDetailVO> details, BigDecimal amount) {
        log.info("【执行】 - 3 calculateFreeze---------------------------- {}", dto);
        Long qty;
        Long count = details.stream().mapToLong(DelOutboundOperationDetailVO::getQty).sum();
        OperationRuleVO operation = new OperationRuleVO();
        List<String> skuList = details.stream().map(DelOutboundOperationDetailVO::getSku).collect(Collectors.toList());
        BaseProductBatchQueryDto baseProductBatchQueryDto = new BaseProductBatchQueryDto();
        baseProductBatchQueryDto.setSellerCode(dto.getCustomCode());
        baseProductBatchQueryDto.setCodes(skuList);
        log.info("【执行】 - 3 calculateFreeze - 查sku ---------------------------- {}", dto);
        R<List<BaseProductMeasureDto>> listR = baseProductFeignService.batchSKU(baseProductBatchQueryDto);
        List<BaseProductMeasureDto> dataAndException = R.getDataAndException(listR);
        Map<String, Double> skuWeightMap = dataAndException.stream().collect(Collectors.toMap(BaseProductMeasureDto::getCode, BaseProductMeasureDto::getWeight));
        log.info("【执行】 - 3 calculateFreeze - 找规则 ---------------------------- {}", dto);
        for (DelOutboundOperationDetailVO vo : details) {
            double weight = Optional.ofNullable(skuWeightMap.get(vo.getSku())).orElse(0.00);
            operation = getOperationDetails(dto, weight, "未找到" + DelOutboundOrderEnum.getName(dto.getOrderType()) + "业务费用规则，请联系管理员");
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
        log.info("【执行】 - 3 calculateFreeze - 计算扣费 ---------------------------- {}", dto);
        return this.freezeBalance(dto, count, amount, operation);
    }

    /**
     * 集运处理费
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
     * 出库单批量出库处理费
     * 包含下架装箱费、贴标费、出库费
     * 下架装箱费、贴标费没有重量按照数量计价
     * 总费用=下架装箱费+贴标费+出库费
     *
     * @param dto     dto
     * @param details details
     * @return result
     */
    private R<?> chargeBatch(DelOutboundOperationVO dto, List<DelOutboundOperationDetailVO> details) {

        //计算装箱费
        Integer packingCount = dto.getPackingCount();
        BigDecimal amount = BigDecimal.ZERO;
        amount = getBatchAmount(dto, amount, packingCount, DelOutboundOrderEnum.BATCH_PACKING.getCode());

        //计算贴标费
        Integer shipmentLabelCount = dto.getShipmentLabelCount();
        amount = getBatchAmount(dto, amount, shipmentLabelCount, DelOutboundOrderEnum.BATCH_LABEL.getCode());

        return this.calculateFreeze(dto, details, amount);
    }

    private BigDecimal getBatchAmount(DelOutboundOperationVO dto, BigDecimal amount, Integer count, String type) {
        if (count != null && count > 0) {
            OperationRuleVO labelOperation = getOperationDetails(dto, Double.valueOf(count), "未找到" + DelOutboundOrderEnum.getName(type) + "业务费用规则，请联系管理员");
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
     * 判断是否有配置费用规则
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
        // 找出符合的重量区间
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
        log.info("使用规则：{}", JSONObject.toJSONString(operation));
        //  multiply(operation.getDiscountRate()).setScale(2, RoundingMode.HALF_UP);
        return operation;
    }

    private R freezeBalance(DelOutboundOperationVO dto, Long count, BigDecimal amount, OperationRuleVO operation) {
        log.info("【执行】 - 4 freezeBalance ---------------------------- {}", dto);
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
        if (delOutboundOrderEnum != null) chargeLog.setOperationType(delOutboundOrderEnum.getName());
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
        AssertUtil.notNull(chargeLog, "该单没有冻结金额，无法解冻 orderNo: " + dto.getOrderNo());
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
