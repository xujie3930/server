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
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
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

    private static final String manySku = "-manySku";

    private static final String packing = "-packing";

    private static final String label = "-label";

    @Override
    public int save(OperationDTO dto) {
        Operation domain = new Operation();
        BeanUtils.copyProperties(dto, domain);
        checkDuplicate(domain);
        return operationMapper.insert(domain);
    }

    /**
     * 新增和修改时校验是否数据是否重复
     *
     * @param operation operation
     */
    private void checkDuplicate(Operation operation) {
        int count = operationMapper.findCount(operation);
        if (count > 0) {
            throw new CommonException("999", "仓库+操作类型+订单类型 重量区间不能重合");
        }
    }

    @Override
    public int update(Operation dto) {
        checkDuplicate(dto);
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
        where.orderByDesc(Operation::getCreateTime);
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
        if (StringUtils.isNotBlank(dto.getWarehouseCode())) {
            query.eq(Operation::getWarehouseCode, dto.getWarehouseCode());
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

        if (dto.getOrderType().equals(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode())) {
            return packageTransfer(dto);
        }

        if (dto.getOrderType().equals(DelOutboundOrderTypeEnum.BATCH.getCode())) {
            return chargeBatch(dto, details);
        }

        return this.calculateFreeze(dto, details, amount);

    }

    /**
     * 转运出库单 转运单没有重量，数量为1
     *
     * @param dto dto
     * @return result
     */
    private R<?> packageTransfer(DelOutboundOperationVO dto) {
        Operation operation = getOperationDetails(dto, null, "未找到" + dto.getOrderType() + "业务费用规则，请联系管理员");
        return this.freezeBalance(dto, 1L, operation.getFirstPrice(), operation);
    }

    /**
     * 遍历出库单的详情信息 根据收费规则计算费用
     *
     * @param dto     dto
     * @param details details
     * @param amount  amount
     * @return result
     */
    private R calculateFreeze(DelOutboundOperationVO dto, List<DelOutboundOperationDetailVO> details, BigDecimal amount) {
        Long qty;
        Long count = details.stream().mapToLong(DelOutboundOperationDetailVO::getQty).sum();
        Operation operation = new Operation();
        for (DelOutboundOperationDetailVO vo : details) {
            operation = getOperationDetails(dto, vo.getWeight(), "未找到" + dto.getOrderType() + "业务费用规则，请联系管理员");
            qty = vo.getQty();
            amount = payService.calculate(operation.getFirstPrice(), operation.getNextPrice(), qty).add(amount);
            log.info("orderNo: {} orderType: {} amount: {}", dto.getOrderNo(), dto.getOrderType(), amount);
        }
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
            dto.setOrderType(dto.getOrderType().concat(manySku));
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
        amount = getBatchAmount(dto, amount, packingCount, packing);

        //计算贴标费
        Integer shipmentLabelCount = dto.getShipmentLabelCount();
        amount = getBatchAmount(dto, amount, shipmentLabelCount, label);

        return this.calculateFreeze(dto, details, amount);
    }

    private BigDecimal getBatchAmount(DelOutboundOperationVO dto, BigDecimal amount, Integer count, String type) {
        if (count != null && count > 0) {
            String orderType = dto.getOrderType().concat(type);
            Operation labelOperation = getOperationDetails(dto, null, "未找到" + orderType + "业务费用规则，请联系管理员");
            BigDecimal calculate = payService.calculate(labelOperation.getFirstPrice(), labelOperation.getNextPrice(), count.longValue());
            amount = amount.add(calculate);
        }
        return amount;
    }

    private Operation getOperationDetails(DelOutboundOperationVO dto, Double weight, String message) {
        OperationDTO operationDTO = new OperationDTO(dto.getOrderType(), OrderTypeEnum.Shipment.name(), dto.getWarehouseCode(), weight);
        Operation operation = this.queryDetails(operationDTO);
        AssertUtil.notNull(operation, message);
        return operation;
    }

    private R freezeBalance(DelOutboundOperationVO dto, Long count, BigDecimal amount, Operation operation) {
        ChargeLog chargeLog = setChargeLog(dto, count);
        chargeLog.setHasFreeze(true);
        CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO(dto.getCustomCode(), operation.getCurrencyCode(), dto.getOrderNo(), dto.getOrderType(), amount);
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
        CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO(chargeLog.getCustomCode(), chargeLog.getCurrencyCode(), chargeLog.getOrderNo(), chargeLog.getOperationType(), chargeLog.getAmount());
        return payService.thawBalance(cusFreezeBalanceDTO, chargeLog);
    }


    private CustPayDTO setCustPayDto(ChargeLog chargeLog) {
        CustPayDTO custPayDTO = new CustPayDTO();
        List<AccountSerialBillDTO> serialBillInfoList = new ArrayList<>();
        AccountSerialBillDTO accountSerialBillDTO = new AccountSerialBillDTO();
        accountSerialBillDTO.setChargeCategory("操作费");
        accountSerialBillDTO.setChargeType(chargeLog.getOperationType());
        accountSerialBillDTO.setAmount(chargeLog.getAmount());
        accountSerialBillDTO.setCurrencyCode(chargeLog.getCurrencyCode());
        serialBillInfoList.add(accountSerialBillDTO);
        accountSerialBillDTO.setWarehouseCode(chargeLog.getWarehouseCode());
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
