package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.msd.chargerules.domain.BasSpecialOperation;
import com.msd.chargerules.domain.SpecialOperation;
import com.msd.chargerules.dto.BasSpecialOperationDTO;
import com.szmsd.chargerules.enums.ErrorMessageEnum;
import com.szmsd.chargerules.enums.SpecialOperationStatusEnum;
import com.szmsd.chargerules.factory.OrderType;
import com.szmsd.chargerules.factory.OrderTypeFactory;
import com.szmsd.chargerules.mapper.BaseInfoMapper;
import com.szmsd.chargerules.service.IBaseInfoService;
import com.szmsd.chargerules.service.ISpecialOperationService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.SpecialOperationResultRequest;
import com.szmsd.open.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

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

    @Override
    public ResponseVO add(BasSpecialOperationDTO basSpecialOperationDTO) {
        BasSpecialOperation domain = new BasSpecialOperation();
        BeanUtils.copyProperties(basSpecialOperationDTO, domain);

        int insert = baseInfoMapper.insert(domain);
        return insert > 0 ? ResponseVO.ok() : ResponseVO.failed(null);
    }

    @Override
    public List<BasSpecialOperation> list(BasSpecialOperationDTO dto) {
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
        return baseInfoMapper.selectList(where);
    }

    @Transactional
    @Override
    public R update(BasSpecialOperation basSpecialOperation) {

        if (!SpecialOperationStatusEnum.PASS.checkStatus(basSpecialOperation.getStatus())) {
            return R.failed(ErrorMessageEnum.STATUS_RESULT.getMessage());
        }
        //审批不通过->系数设为0 审批通过->系数必须大于0
        if (basSpecialOperation.getStatus().equals(SpecialOperationStatusEnum.REJECT.getStatus())) {
            basSpecialOperation.setCoefficient(0);
        } else {
            if (basSpecialOperation.getCoefficient() == 0) {
                return R.failed(ErrorMessageEnum.COEFFICIENT_IS_ZERO.getMessage());
            }
        }

        //校验单号是否存在
        OrderType factory = orderTypeFactory.getFactory(basSpecialOperation.getOrderType());
        boolean exist = factory.checkOrderExist(basSpecialOperation.getOrderNo());
        if (!exist) {
            return R.failed(ErrorMessageEnum.ORDER_IS_NOT_EXIST.getMessage());
        }

        //修改数据
        baseInfoMapper.updateById(basSpecialOperation);

        SpecialOperation specialOperation = specialOperationService.selectOne(basSpecialOperation);
        if (specialOperation == null) {
            return R.failed(ErrorMessageEnum.OPERATION_TYPE_NOT_FOUND.getMessage());
        }
        BigDecimal calculate = calculate(specialOperation.getFirstPrice(),
                specialOperation.getNextPrice(), basSpecialOperation.getQty());
        //TODO 调用扣费接口

        SpecialOperationResultRequest request = new SpecialOperationResultRequest();
        BeanUtils.copyProperties(basSpecialOperation, request);
        R<com.szmsd.http.vo.ResponseVO> responseVOR = htpBasFeignService.specialOperationResult(request);
        if (responseVOR.getCode() != 200) {
            return R.failed(ErrorMessageEnum.UPDATE_OPERATION_TYPE_ERROR.getMessage());
        }
        return R.ok();
    }

    public BigDecimal calculate(BigDecimal firstPrice, BigDecimal nextPrice, Integer qty) {
        return qty == 1 ? firstPrice : new BigDecimal(qty - 1).multiply(nextPrice).add(firstPrice);
    }

}
