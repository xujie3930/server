package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.mapper.AccountSerialBillMapper;
import com.szmsd.finance.service.IAccountSerialBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class AccountSerialBillServiceImpl extends ServiceImpl<AccountSerialBillMapper, AccountSerialBill> implements IAccountSerialBillService {

    @Resource
    private AccountSerialBillMapper accountSerialBillMapper;

    @Override
    public List<AccountSerialBill> listPage(AccountSerialBillDTO dto) {
        LambdaQueryWrapper<AccountSerialBill> query = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(dto.getNo())) {
            query.eq(AccountSerialBill::getNo, dto.getNo());
        }
        if (StringUtils.isNotBlank(dto.getChargeType())) {
            query.eq(AccountSerialBill::getChargeType, dto.getChargeType());
        }
        if (StringUtils.isNotBlank(dto.getCusCode())) {
            query.eq(AccountSerialBill::getCusCode, dto.getCusCode());
        }
        if (StringUtils.isNotBlank(dto.getWarehouseCode())) {
            query.eq(AccountSerialBill::getWarehouseCode, dto.getWarehouseCode());
        }
        if (StringUtils.isNotBlank(dto.getCurrencyCode())) {
            query.eq(AccountSerialBill::getCurrencyCode, dto.getCurrencyCode());
        }
        if (StringUtils.isNotBlank(dto.getBusinessCategory())) {
            query.eq(AccountSerialBill::getBusinessCategory, dto.getBusinessCategory());
        }
        if (StringUtils.isNotBlank(dto.getProductCategory())) {
            query.eq(AccountSerialBill::getProductCategory, dto.getProductCategory());
        }
        if (StringUtils.isNotBlank(dto.getProductCode())) {
            query.eq(AccountSerialBill::getProductCode, dto.getProductCode());
        }
        if (StringUtils.isNotBlank(dto.getChargeCategory())) {
            query.eq(AccountSerialBill::getChargeCategory, dto.getChargeCategory());
        }
        if (StringUtils.isNotBlank(dto.getCreateTimeStart())) {
            query.ge(AccountSerialBill::getCreateTime, dto.getCreateTimeStart());
        }
        if (StringUtils.isNotBlank(dto.getCreateTimeEnd())) {
            query.le(AccountSerialBill::getCreateTime, dto.getCreateTimeEnd());
        }
        query.orderByAsc(AccountSerialBill::getOrderTime);
        return accountSerialBillMapper.selectList(query);
    }

    @Override
    public int add(AccountSerialBillDTO dto) {
        AccountSerialBill accountSerialBill = BeanMapperUtil.map(dto, AccountSerialBill.class);
        return accountSerialBillMapper.insert(accountSerialBill);
    }

    @Override
    public boolean saveBatch(List<AccountSerialBillDTO> dto) {
        List<AccountSerialBill> accountSerialBill = BeanMapperUtil.mapList(dto, AccountSerialBill.class);
        boolean b = this.saveBatch(accountSerialBill);
        if (!b) log.error("saveBatch() insert failed. {}", accountSerialBill);
        return b;
    }

}
