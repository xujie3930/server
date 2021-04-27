package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.mapper.OperationMapper;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.http.enums.HttpRechargeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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

    @Override
    public R delOutboundDeductions(DelOutboundVO dto) {
        ChargeLog chargeLog = this.selectLog(dto.getOrderNo());
        if (chargeLog == null) {
            return R.failed("该单没有冻结余额 orderNo: " + dto.getOrderNo());
        }
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
        return chargeLogService.selectLog(chargeLogDto);
    }

    @Override
    public R delOutboundFreeze(DelOutboundVO dto) {
        List<DelOutboundDetailVO> details = dto.getDetails();
        if (CollectionUtils.isEmpty(details)) {
            log.error("calculate() 出库单对应的详情信息未找到");
            return R.failed("出库单的详情信息为空");
        }

        OperationDTO operationDTO = new OperationDTO();
        List<Operation> operations = listPage(operationDTO);

        for (DelOutboundDetailVO vo : details) {
            Operation operation = operations.stream().filter(value ->
                    value.getWarehouseCode().equals(dto.getWarehouseCode()) && value.getOperationType().equals(dto.getOrderType())
                            && vo.getWeight() > value.getMinimumWeight() && vo.getWeight() <= value.getMaximumWeight()).findAny().orElse(null);
            if (operation == null) {
                log.error("calculate() 未找到业务操作的收费配置");
                return R.failed("未找到业务操作的收费配置");
            }
            Long qty = vo.getQty();
            BigDecimal amount = payService.calculate(operation.getFirstPrice(), operation.getNextPrice(), qty);
            log.info("orderNo: {} orderType: {} amount: {}", dto.getOrderNo(), dto.getOrderType(), amount);
            ChargeLog chargeLog = setChargeLog(dto, qty);
            CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO(dto.getCustomCode(), HttpRechargeConstants.RechargeCurrencyCode.CNY.name(), dto.getOrderNo(), amount);
            payService.freezeBalance(cusFreezeBalanceDTO, chargeLog);
        }
        return R.ok();

    }

    private ChargeLog setChargeLog(DelOutboundVO dto, Long qty) {
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

    @Override
    public R delOutboundThaw(DelOutboundVO dto) {
        ChargeLog chargeLog = this.selectLog(dto.getOrderNo());
        if (chargeLog == null) {
            return R.failed("该单没有冻结余额 orderNo: " + dto.getOrderNo());
        }
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
