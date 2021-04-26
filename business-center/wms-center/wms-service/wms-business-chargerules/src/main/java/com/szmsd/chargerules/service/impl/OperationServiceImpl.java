package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.chargerules.mapper.OperationMapper;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
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

    public Operation queryDetails(OperationDTO dto) {
        LambdaQueryWrapper<Operation> query = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(dto.getOrderType())) {
            query.eq(Operation::getOrderType, dto.getOrderType());
        }
        if (StringUtils.isNotBlank(dto.getOperationType())) {
            query.eq(Operation::getOperationType, dto.getOperationType());
        }
        query.gt(Operation::getMinimumWeight, dto.getWeight());
        query.le(Operation::getMaximumWeight, dto.getWeight());

        return operationMapper.selectOne(query);
    }


    @Override
    public R delOutboundCharge(DelOutboundVO dto) {
        List<DelOutboundDetailVO> details = dto.getDetails();
        if (CollectionUtils.isEmpty(details)) {
            log.error("calculate() 出库单对应的详情信息未找到");
            return R.failed("出库单的详情信息为空");
        }

        // 查询收费规则
        OperationDTO operationDTO = new OperationDTO(dto.getOrderType(), OrderTypeEnum.Shipment.name(), dto.getWeight());
        Operation operation = queryDetails(operationDTO);
        if(operation == null) {
            log.error("orderType: {} weight: {}",dto.getOrderType(),dto.getWeight());
            return R.failed("未找到收费配置");
        }

        for (DelOutboundDetailVO detail : details) {
            int qty = detail.getQty().intValue();
            BigDecimal amount = payService.calculate(operation.getFirstPrice(), operation.getNextPrice(), qty);
            log.info("orderNo: {} orderType: {} amount: {}", dto.getOrderNo(), dto.getOrderType(), amount);
            ChargeLog chargeLog = new ChargeLog(dto.getOrderNo(), dto.getOrderType(), dto.getWarehouseCode(), qty);
            CustPayDTO custPayDTO = setCustPayDto(dto, amount, chargeLog);
            payService.pay(custPayDTO, chargeLog);
        }
        return R.ok();

    }


    private CustPayDTO setCustPayDto(DelOutboundVO dto, BigDecimal amount, ChargeLog chargeLog) {
        CustPayDTO custPayDTO = new CustPayDTO();
        List<AccountSerialBillDTO> serialBillInfoList = new ArrayList<>();
        AccountSerialBillDTO accountSerialBillDTO = new AccountSerialBillDTO();
        accountSerialBillDTO.setChargeCategory("操作费");
        accountSerialBillDTO.setChargeType(dto.getOrderType());
        accountSerialBillDTO.setRemark(dto.getRemark());
        accountSerialBillDTO.setAmount(amount);
        accountSerialBillDTO.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        serialBillInfoList.add(accountSerialBillDTO);
        accountSerialBillDTO.setWarehouseCode(dto.getWarehouseCode());
        custPayDTO.setCusCode(dto.getCustomCode());
        custPayDTO.setPayType(BillEnum.PayType.PAYMENT);
        custPayDTO.setPayMethod(BillEnum.PayMethod.BUSINESS_OPERATE);
        custPayDTO.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        custPayDTO.setAmount(amount);
        custPayDTO.setNo(chargeLog.getOrderNo());
        custPayDTO.setSerialBillInfoList(serialBillInfoList);
        return custPayDTO;
    }


}
