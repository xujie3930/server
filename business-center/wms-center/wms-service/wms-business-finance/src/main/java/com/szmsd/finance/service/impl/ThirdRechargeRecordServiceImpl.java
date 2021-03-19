package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.domain.ThirdRechargeRecord;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.RechargesCallbackRequestDTO;
import com.szmsd.finance.mapper.ThirdRechargeRecordMapper;
import com.szmsd.finance.service.IThirdRechargeRecordService;
import com.szmsd.http.enums.HttpRechargeConstants;
import com.szmsd.http.vo.RechargesResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liulei
 */
@Service
public class ThirdRechargeRecordServiceImpl implements IThirdRechargeRecordService {

    @Autowired
    ThirdRechargeRecordMapper thirdRechargeRecordMapper;

    @Override
    public void saveRecord(CustPayDTO dto, RechargesResponseVo vo) {
        ThirdRechargeRecord thirdRechargeRecord=new ThirdRechargeRecord();
        BeanUtils.copyProperties(dto,thirdRechargeRecord);
        if(vo!=null){
            //正常返回结果
            if(StringUtils.isNotEmpty(vo.getRechargeNo()) && HttpRechargeConstants.RechargeStatusCode.Pending.equals(vo.getStatus())){
                thirdRechargeRecord.setRechargeNo(vo.getRechargeNo());
                thirdRechargeRecord.setRechargeAmount(vo.getRechargeAmount().getAmount());
                thirdRechargeRecord.setRechargeCurrency(vo.getRechargeAmount().getCurrencyCode());
                thirdRechargeRecord.setTransactionAmount(vo.getTransactionFee().getAmount());
                thirdRechargeRecord.setTransactionCurrency(vo.getTransactionFee().getCurrencyCode());
                thirdRechargeRecord.setActualAmount(vo.getActualRechargeAmount().getAmount());
                thirdRechargeRecord.setActualCurrency(vo.getActualRechargeAmount().getCurrencyCode());
                thirdRechargeRecord.setSerialNo(vo.getSerialNo());
                thirdRechargeRecord.setRechargeStatus(vo.getStatus());
            }
            //返回异常
            if(StringUtils.isNotEmpty(vo.getCode())){
                thirdRechargeRecord.setErrorCode(vo.getCode());
            }
            if(StringUtils.isNotEmpty(vo.getMessage())){
                thirdRechargeRecord.setErrorMessage(vo.getMessage());
            }
        }
        thirdRechargeRecordMapper.insert(thirdRechargeRecord);
    }

    @Override
    public ThirdRechargeRecord updateRecordIfSuccess(RechargesCallbackRequestDTO requestDTO) {
        ThirdRechargeRecord domain=thirdRechargeRecordMapper.selectOne(new QueryWrapper<ThirdRechargeRecord>().lambda()
                .eq(ThirdRechargeRecord::getRechargeNo,requestDTO.getRechargeNo())
                .eq(ThirdRechargeRecord::getRechargeStatus,HttpRechargeConstants.RechargeStatusCode.Pending.name())
        );
        if(domain!=null){
            domain.setRechargeStatus(requestDTO.getStatus());
            thirdRechargeRecordMapper.updateById(domain);
            return domain;
        }
        return null;
    }
}
