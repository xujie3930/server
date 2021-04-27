package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.chargerules.mapper.OperationMapper;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.vo.DelOutboundOperationDetailVO;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.http.enums.HttpRechargeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OperationServiceImpl extends ServiceImpl<OperationMapper, Operation> implements IOperationService {

    @Resource
    private IPayService payService;

    @Resource
    private OperationMapper operationMapper;

    @Resource
    private IChargeLogService chargeLogService;

    @Override
    public int save(OperationDTO dto) {
        Operation domain = new Operation();
        BeanUtils.copyProperties(dto, domain);
        return operationMapper.insert(domain);
    }

    @Override
    public int update(Operation dto) {
        return operationMapper.updateById(dto);
    }

    @Override
    public List<Operation> listPage(OperationDTO dto) {
        LambdaQueryWrapper<Operation> where = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(dto.getOperationType())) {
            where.eq(Operation::getOperationType, dto.getOperationType());
        }
        if (StringUtils.isNotEmpty(dto.getOrderType())) {
            where.eq(Operation::getOrderType, dto.getOrderType());
        }
        if (StringUtils.isNotEmpty(dto.getWarehouseCode())) {
            where.eq(Operation::getWarehouseCode, dto.getWarehouseCode());
        }
        return operationMapper.selectList(where);
    }

    @Override
    public Operation details(int id) {
        return operationMapper.selectById(id);
    }

    public Operation queryDetails(OperationDTO dto) {
        LambdaQueryWrapper<Operation> query = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(dto.getOrderType())) {
            query.eq(Operation::getOrderType, dto.getOrderType());
        }
        if (StringUtils.isNotBlank(dto.getOperationType())) {
            query.eq(Operation::getOperationType, dto.getOperationType());
        }
        if (dto.getWeight() != null) {
            query.lt(Operation::getMinimumWeight, dto.getWeight());
            query.ge(Operation::getMaximumWeight, dto.getWeight());
        }
        return operationMapper.selectOne(query);
    }

    @Transactional
    @Override
    public R delOutboundDeductions(DelOutboundOperationVO dto) {
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
    public R delOutboundFreeze(DelOutboundOperationVO dto) {
        List<DelOutboundOperationDetailVO> details = dto.getDetails();
        if (CollectionUtils.isEmpty(details)) {
            log.error("calculate() 出库单的详情信息为空");
            return R.failed("出库单的详情信息为空");
        }

        BigDecimal amount = BigDecimal.ZERO;
        if (dto.getOrderType().equals(DelOutboundOrderTypeEnum.COLLECTION.getCode())) {
            return chargeCollection(dto, details);
        }

        if (dto.getOrderType().equals(DelOutboundOrderTypeEnum.BATCH.getCode())) {
            return chargeBatch(dto, details);
        }

        return this.calculateFreeze(dto, dto.getOrderType(), details, amount);

    }

    private R calculateFreeze(DelOutboundOperationVO dto, String orderType, List<DelOutboundOperationDetailVO> details, BigDecimal amount) {
        Long qty;
        Long count = details.stream().mapToLong(DelOutboundOperationDetailVO::getQty).sum();
        for (DelOutboundOperationDetailVO vo : details) {
            Operation operation = getOperationDetails(dto, orderType, vo.getWeight(), "未找到" + dto.getOrderType() + "配置");
            qty = vo.getQty();
            amount = payService.calculate(operation.getFirstPrice(), operation.getNextPrice(), qty).add(amount);
            log.info("orderNo: {} orderType: {} amount: {}", dto.getOrderNo(), dto.getOrderType(), amount);
        }
        return this.freezeBalance(dto, count, amount);
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
            return this.calculateFreeze(dto, dto.getOrderType().concat("-manySku"), details, amount);
        }
        return this.calculateFreeze(dto, dto.getOrderType(), details, amount);
    }

    /**
     * 出库单批量出库处理费
     *
     * @param dto     dto
     * @param details details
     * @return result
     */
    private R<?> chargeBatch(DelOutboundOperationVO dto, List<DelOutboundOperationDetailVO> details) {

        //计算装箱费
        Integer packingCount = dto.getPackingCount();
        BigDecimal amount = BigDecimal.ZERO;
        if (packingCount > 0) {
            String packingType = dto.getOrderType().concat("-packing");
            Operation packingOperation = getOperationDetails(dto, packingType, null, "未找到" + packingType + "配置");
            BigDecimal calculate = payService.calculate(packingOperation.getFirstPrice(), packingOperation.getNextPrice(), packingCount.longValue());
            amount = amount.add(calculate);
        }

        //计算贴标费
        Integer shipmentLabelCount = dto.getShipmentLabelCount();
        if (shipmentLabelCount > 0) {
            String LabelType = dto.getOrderType().concat("-label");
            Operation LabelOperation = getOperationDetails(dto, LabelType, null, "未找到" + LabelType + "配置");
            BigDecimal calculate = payService.calculate(LabelOperation.getFirstPrice(), LabelOperation.getNextPrice(), shipmentLabelCount.longValue());
            amount = amount.add(calculate);
        }

        return this.calculateFreeze(dto, dto.getOrderType(), details, amount);
    }

    private Operation getOperationDetails(DelOutboundOperationVO dto, String orderType, Double weight, String message) {
        OperationDTO operationDTO = new OperationDTO(orderType, OrderTypeEnum.Shipment.name(), dto.getWarehouseCode(), weight);
        Operation operation = this.queryDetails(operationDTO);
        AssertUtil.notNull(operation, message);
        return operation;
    }

    private R freezeBalance(DelOutboundOperationVO dto, Long count, BigDecimal amount) {
        ChargeLog chargeLog = setChargeLog(dto, count);
        chargeLog.setHasFreeze(true);
        CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO(dto.getCustomCode(), HttpRechargeConstants.RechargeCurrencyCode.CNY.name(), dto.getOrderNo(), amount);
        return payService.freezeBalance(cusFreezeBalanceDTO, chargeLog);
    }

    private ChargeLog setChargeLog(DelOutboundOperationVO dto, Long qty) {
        ChargeLog chargeLog = new ChargeLog();
        chargeLog.setOrderNo(dto.getOrderNo());
        chargeLog.setOperationType(dto.getOrderType());
        DelOutboundOrderTypeEnum delOutboundOrderTypeEnum = DelOutboundOrderTypeEnum.get(dto.getOrderType());
        if (delOutboundOrderTypeEnum != null) chargeLog.setOperationType(delOutboundOrderTypeEnum.getName());
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
        CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO(chargeLog.getCustomCode(), HttpRechargeConstants.RechargeCurrencyCode.CNY.name(), chargeLog.getOrderNo(), chargeLog.getAmount());
        return payService.thawBalance(cusFreezeBalanceDTO, chargeLog);
    }


    private CustPayDTO setCustPayDto(ChargeLog chargeLog) {
        CustPayDTO custPayDTO = new CustPayDTO();
        List<AccountSerialBillDTO> serialBillInfoList = new ArrayList<>();
        AccountSerialBillDTO accountSerialBillDTO = new AccountSerialBillDTO();
        accountSerialBillDTO.setChargeCategory("操作费");
        accountSerialBillDTO.setChargeType(chargeLog.getOperationType());
        accountSerialBillDTO.setAmount(chargeLog.getAmount());
        accountSerialBillDTO.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        serialBillInfoList.add(accountSerialBillDTO);
        accountSerialBillDTO.setWarehouseCode(chargeLog.getWarehouseCode());
        custPayDTO.setCusCode(chargeLog.getCustomCode());
        custPayDTO.setPayType(BillEnum.PayType.PAYMENT);
        custPayDTO.setPayMethod(BillEnum.PayMethod.BUSINESS_OPERATE);
        custPayDTO.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        custPayDTO.setAmount(chargeLog.getAmount());
        custPayDTO.setNo(chargeLog.getOrderNo());
        custPayDTO.setSerialBillInfoList(serialBillInfoList);
        return custPayDTO;
    }


}
