package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.chargerules.domain.BasSpecialOperation;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.domain.SpecialOperation;
import com.szmsd.chargerules.dto.BasSpecialOperationRequestDTO;
import com.szmsd.chargerules.enums.ErrorMessageEnum;
import com.szmsd.chargerules.enums.SpecialOperationStatusEnum;
import com.szmsd.chargerules.factory.OrderType;
import com.szmsd.chargerules.factory.OrderTypeFactory;
import com.szmsd.chargerules.mapper.BaseInfoMapper;
import com.szmsd.chargerules.service.IBaseInfoService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.chargerules.service.ISpecialOperationService;
import com.szmsd.chargerules.vo.BasSpecialOperationVo;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.SpecialOperationResultRequest;
import com.szmsd.http.enums.HttpRechargeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BaseInfoServiceImpl extends ServiceImpl<BaseInfoMapper, BasSpecialOperation> implements IBaseInfoService {

    @Resource
    private BaseInfoMapper baseInfoMapper;

    @Resource
    private HtpBasFeignService htpBasFeignService;

    @Resource
    private ISpecialOperationService specialOperationService;

    @Resource
    private OrderTypeFactory orderTypeFactory;

    @Resource
    private IPayService payService;

    @Override
    public void add(BasSpecialOperationRequestDTO basSpecialOperationRequestDTO) {
        BasSpecialOperation domain = BeanMapperUtil.map(basSpecialOperationRequestDTO, BasSpecialOperation.class);
        LambdaQueryWrapper<BasSpecialOperation> query = Wrappers.lambdaQuery();
        query.eq(BasSpecialOperation::getOperationOrderNo,basSpecialOperationRequestDTO.getOperationOrderNo());
        BasSpecialOperation basSpecialOperation = baseInfoMapper.selectOne(query);
        if(Objects.isNull(basSpecialOperation)) {
            baseInfoMapper.insert(domain);
            log.info("#A3 已创建特殊操作");
        } else {
            LambdaUpdateWrapper<BasSpecialOperation> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(BasSpecialOperation::getOperationOrderNo,basSpecialOperationRequestDTO.getOperationOrderNo());
            baseInfoMapper.update(domain,updateWrapper);
            log.info("#A3 已修改特殊操作");
        }
    }

    @Transactional
    @Override
    public R update(BasSpecialOperation basSpecialOperation) {

        this.checkStatus(basSpecialOperation);

        String customCode = this.getCustomCode(basSpecialOperation);

        //修改数据
        this.updateBasSpecialOperation(basSpecialOperation);

        //查询操作类型对应的收费配置
        this.charge(basSpecialOperation, customCode);

        return R.ok();
    }

    /**
     * 修改数据
     * @param basSpecialOperation basSpecialOperation
     */
    private void updateBasSpecialOperation(BasSpecialOperation basSpecialOperation) {
        LambdaUpdateWrapper<BasSpecialOperation> update = Wrappers.lambdaUpdate();
        update.set(BasSpecialOperation::getCoefficient, basSpecialOperation.getCoefficient())
                .set(BasSpecialOperation::getStatus, basSpecialOperation.getStatus())
                .set(BasSpecialOperation::getReason, basSpecialOperation.getReason())
                .set(BasSpecialOperation::getRemark, basSpecialOperation.getRemark())
                .set(BasSpecialOperation::getOmsRemark, basSpecialOperation.getOmsRemark())
                .eq(BasSpecialOperation::getId, basSpecialOperation.getId());
        this.update(update);
    }

    @Override
    public List<BasSpecialOperationVo> list(BasSpecialOperationRequestDTO dto) {
        QueryWrapper<BasSpecialOperation> where = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(dto.getOperationType())) {
            where.eq("operation_order_no", dto.getOperationType());
        }
        if (StringUtils.isNotEmpty(dto.getOperationType())) {
            where.eq("order_no", dto.getOperationType());
        }
        if (StringUtils.isNotEmpty(dto.getOperationType())) {
            where.eq("operation_type", dto.getOperationType());
        }
        return baseInfoMapper.selectPageList(where);
    }

    /**
     * 调用WMS接口回写数据
     * @param basSpecialOperation basSpecialOperation
     */
    private void sendResult(BasSpecialOperation basSpecialOperation) {
        SpecialOperationResultRequest request = new SpecialOperationResultRequest();
        BeanUtils.copyProperties(basSpecialOperation, request);
        R<com.szmsd.http.vo.ResponseVO> responseVOR = htpBasFeignService.specialOperationResult(request);
        if (responseVOR.getCode() != 200) {
            throw new CommonException("999", ErrorMessageEnum.UPDATE_OPERATION_TYPE_ERROR.getMessage());
        }
    }

    /**
     * 根据收费规则计算应扣费用并扣费
     * @param basSpecialOperation basSpecialOperation
     * @param customCode customCode
     */
    private void charge(BasSpecialOperation basSpecialOperation, String customCode) {
        SpecialOperation specialOperation = specialOperationService.selectOne(basSpecialOperation);
        if (specialOperation == null) {
            throw new CommonException("999", ErrorMessageEnum.OPERATION_TYPE_NOT_FOUND.getMessage());
        }
        if(SpecialOperationStatusEnum.PASS.getStatus().equals(basSpecialOperation.getStatus())) {
            BigDecimal baseAmount = payService.calculate(specialOperation.getFirstPrice(),
                    specialOperation.getNextPrice(), basSpecialOperation.getQty().longValue());
            BigDecimal amount = baseAmount.multiply(new BigDecimal(basSpecialOperation.getCoefficient()));

            //调用扣费接口扣费
            ChargeLog chargeLog = new ChargeLog(basSpecialOperation.getOrderNo(), basSpecialOperation.getOperationType(), basSpecialOperation.getWarehouseCode(),basSpecialOperation.getQty().longValue());
            CustPayDTO custPayDTO = setCustPayDto(customCode,amount,basSpecialOperation,specialOperation);
            R r = payService.pay(custPayDTO, chargeLog);
            if (r.getCode() != 200) {
                log.error("pay failed: {} {}", r.getData(), r.getMsg());
                throw new CommonException("999", ErrorMessageEnum.PAY_FAILED.getMessage());
            }

            // 调用WMS接口回写结果
            this.sendResult(basSpecialOperation);
        }
    }

    /**
     * 设置扣款实体类
     * @param customCode 客户id
     * @param amount 金额
     * @param basSpecialOperation basSpecialOperation
     * @return CustPayDTO
     */
    private CustPayDTO setCustPayDto(String customCode, BigDecimal amount, BasSpecialOperation basSpecialOperation,SpecialOperation specialOperation) {
        CustPayDTO custPayDTO = new CustPayDTO();
        List<AccountSerialBillDTO> serialBillInfoList = new ArrayList<>();
        AccountSerialBillDTO accountSerialBillDTO = new AccountSerialBillDTO();
        accountSerialBillDTO.setChargeCategory("特殊操作费");
        accountSerialBillDTO.setChargeType(basSpecialOperation.getOperationType());
        accountSerialBillDTO.setRemark(basSpecialOperation.getRemark());
        accountSerialBillDTO.setAmount(amount);
        accountSerialBillDTO.setCurrencyCode(specialOperation.getCurrencyCode());
        serialBillInfoList.add(accountSerialBillDTO);
        custPayDTO.setCusCode(customCode);
        custPayDTO.setPayType(BillEnum.PayType.PAYMENT);
        custPayDTO.setPayMethod(BillEnum.PayMethod.SPECIAL_OPERATE);
        custPayDTO.setCurrencyCode(specialOperation.getCurrencyCode());
        custPayDTO.setAmount(amount);
        custPayDTO.setNo(basSpecialOperation.getOrderNo());
        custPayDTO.setSerialBillInfoList(serialBillInfoList);
        return custPayDTO;
    }

    /**
     * 获取客户id
     * @param basSpecialOperation basSpecialOperation
     * @return customCode
     */
    private String getCustomCode(BasSpecialOperation basSpecialOperation) {
        OrderType factory = orderTypeFactory.getFactory(basSpecialOperation.getOrderType());
        String customCode = factory.findOrderById(basSpecialOperation.getOrderNo());
        if (StringUtils.isEmpty(customCode)) {
            throw new CommonException("999", ErrorMessageEnum.ORDER_IS_NOT_EXIST.getMessage());
        }
        return customCode;
    }

    /**
     * 校验Status
     * @param basSpecialOperation basSpecialOperation
     */
    private void checkStatus(BasSpecialOperation basSpecialOperation) {
        if (!SpecialOperationStatusEnum.checkStatus(basSpecialOperation.getStatus())) {
            throw new CommonException("999", ErrorMessageEnum.STATUS_RESULT.getMessage());
        }

        BasSpecialOperation check = baseInfoMapper.selectById(basSpecialOperation.getId());
        if (SpecialOperationStatusEnum.PASS.getStatus().equals(check.getStatus())) {
            throw new CommonException("999", ErrorMessageEnum.DUPLICATE_APPLY.getMessage());
        }

        //审批不通过->系数设为0 审批通过->系数必须大于0
        if (basSpecialOperation.getStatus().equals(SpecialOperationStatusEnum.REJECT.getStatus())) {
            basSpecialOperation.setCoefficient(0);
        } else {
            if (basSpecialOperation.getCoefficient() <= 0) {
                throw new CommonException("999", ErrorMessageEnum.COEFFICIENT_IS_ZERO.getMessage());
            }
        }
    }

    @Override
    public BasSpecialOperationVo details(int id) {
        BasSpecialOperationVo basSpecialOperationVo = baseInfoMapper.selectDetailsById(id);
        if (Objects.isNull(basSpecialOperationVo)) throw new CommonException("999", "未找到该操作类型对应的收费规则");
        return basSpecialOperationVo;
    }

}
