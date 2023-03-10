package com.szmsd.finance.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.BigDecimalUtil;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.domain.FssBank;
import com.szmsd.finance.domain.PreRecharge;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.PreRechargeAuditDTO;
import com.szmsd.finance.dto.PreRechargeAuditVO;
import com.szmsd.finance.dto.PreRechargeDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.enums.PreRechargeVerifyStatusEnum;
import com.szmsd.finance.mapper.FssBankMapper;
import com.szmsd.finance.mapper.PreRechargeMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IPreRechargeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author liulei
 */
@Service
public class PreRechargeServiceImpl implements IPreRechargeService {

    @Autowired
    private PreRechargeMapper preRechargeMapper;

    @Autowired
    private IAccountBalanceService accountBalanceService;

    @Autowired
    private FssBankMapper fssBankMapper;

    @Override
    public List<PreRecharge> listPage(PreRechargeDTO dto) {
        LambdaQueryWrapper<PreRecharge> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.isNotNull(PreRecharge::getCusCode);
        if(dto.getCusId()!=null) {
            queryWrapper.eq(PreRecharge::getCusId, dto.getCusId());
        }
        if(StringUtils.isNotEmpty(dto.getCusCode())){
            queryWrapper.eq(PreRecharge::getCusCode,dto.getCusCode());
        }
        if(StringUtils.isNotEmpty(dto.getVerifyStatus())){
            queryWrapper.eq(PreRecharge::getVerifyStatus,dto.getVerifyStatus());
        }
        if(StringUtils.isNotEmpty(dto.getRemittanceMethod())){
            queryWrapper.eq(PreRecharge::getRemittanceMethod,dto.getRemittanceMethod());
        }
        if(StringUtils.isNotEmpty(dto.getCurrencyCode())){
            queryWrapper.eq(PreRecharge::getCurrencyCode,dto.getCurrencyCode());
        }
        if(StringUtils.isNotEmpty(dto.getBeginTime())){
            queryWrapper.ge(PreRecharge::getRemittanceTime,dto.getBeginTime());
        }
        if(StringUtils.isNotEmpty(dto.getEndTime())){
            queryWrapper.le(PreRecharge::getRemittanceTime,dto.getEndTime());
        }
        queryWrapper.orderByAsc(PreRecharge::getVerifyStatus).orderByDesc(PreRecharge::getCreateTime);
        return preRechargeMapper.listPage(queryWrapper);
    }

    @Override
    public R save(PreRechargeDTO dto) {
        if(StringUtils.isEmpty(dto.getCusCode())){
            return R.failed("Customer code cannot be empty");
        }

        String bankId = dto.getBankId();

        if(StringUtils.isNotEmpty(bankId)) {

            FssBank fssBank = fssBankMapper.selectById(bankId);

            if(fssBank == null){
                return R.failed("????????????????????????");
            }
        }

        String serialNo = generatorSerialNo();
        dto.setSerialNo(serialNo);
        PreRecharge domain= new PreRecharge();
        BeanUtils.copyProperties(dto,domain);
        String trimCode = domain.getCurrencyCode().trim();
        domain.setCurrencyCode(trimCode);

        BigDecimal amount = BigDecimalUtil.setScale(domain.getAmount(),BigDecimalUtil.PRICE_SCALE);
        domain.setAmount(amount);

        BigDecimal proceAmount = BigDecimalUtil.setScale(domain.getProcedureAmount(),BigDecimalUtil.PRICE_SCALE);
        domain.setProcedureAmount(proceAmount);

        BigDecimal actur = BigDecimalUtil.setScale(domain.getActurlAmount(),BigDecimalUtil.PRICE_SCALE);
        domain.setActurlAmount(actur);

        int insert = preRechargeMapper.insert(domain);
        if(insert>0){
            return R.ok();
        }
        return R.failed("Save Exception");
    }

    private String generatorSerialNo(){

        String currentTime = DateUtils.dateTime();
        String rNum = RandomUtil.randomNumbers(8);
        return currentTime + rNum;
    }

    @Override
    public R audit(PreRechargeAuditDTO dto) {
        PreRecharge preRecharge = preRechargeMapper.selectById(dto.getId());
        preRecharge.setVerifyStatus(dto.getVerifyStatus());
        if("1".equals(dto.getVerifyStatus())){
            CustPayDTO custPayDTO=new CustPayDTO();
            custPayDTO.setAmount(preRecharge.getAmount());
            custPayDTO.setCusCode(preRecharge.getCusCode());
            custPayDTO.setCusName(preRecharge.getCusName());
            custPayDTO.setCurrencyCode(preRecharge.getCurrencyCode().trim());
            custPayDTO.setCurrencyName(preRecharge.getCurrencyName().trim());
            custPayDTO.setOrderTime(preRecharge.getRemittanceTime());
            custPayDTO.setNo(preRecharge.getSerialNo());
            custPayDTO.setNature("??????");
            custPayDTO.setBusinessType("????????????");
            custPayDTO.setChargeCategoryChange("????????????");
            R r = accountBalanceService.offlineIncome(custPayDTO);
            if (Constants.SUCCESS != r.getCode()) {
                return r;
            }
        }
        preRecharge.setVerifyRemark(dto.getVerifyRemark());
        preRecharge.setVerifyDate(new Date());
        preRechargeMapper.updateById(preRecharge);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R pay(PreRechargeDTO dto) {

        if(StringUtils.isEmpty(dto.getCusCode())){
            return R.failed("Customer code cannot be empty");
        }

        PreRecharge domain= new PreRecharge();
        BeanUtils.copyProperties(dto,domain);

        BigDecimal amount = BigDecimalUtil.setScale(domain.getAmount(),BigDecimalUtil.PRICE_SCALE);
        domain.setAmount(amount);

        BigDecimal proceAmount = BigDecimalUtil.setScale(domain.getProcedureAmount(),BigDecimalUtil.PRICE_SCALE);
        domain.setProcedureAmount(proceAmount);

        BigDecimal actur = BigDecimalUtil.setScale(domain.getActurlAmount(),BigDecimalUtil.PRICE_SCALE);
        domain.setActurlAmount(actur);

        int insert = preRechargeMapper.insert(domain);

        if(insert > 0) {
            CustPayDTO custPayDTO = new CustPayDTO();
            custPayDTO.setAmount(dto.getAmount());
            custPayDTO.setCusCode(dto.getCusCode());
            custPayDTO.setCusName(dto.getCusName());
            custPayDTO.setCurrencyCode(dto.getCurrencyCode());
            custPayDTO.setCurrencyName(dto.getCurrencyName());
            custPayDTO.setOrderTime(dto.getRemittanceTime());
            custPayDTO.setNo(dto.getSerialNo());
            custPayDTO.setNature("??????");
            custPayDTO.setBusinessType("????????????");
            custPayDTO.setChargeCategoryChange("????????????");
            custPayDTO.setPayMethod(BillEnum.PayMethod.ONLINE_INCOME);
            custPayDTO.setPayType(BillEnum.PayType.INCOME);
            String note = this.generatorNote(domain);
            custPayDTO.setNote(note);
            R r = accountBalanceService.offlineIncome(custPayDTO);
            if (Constants.SUCCESS != r.getCode()) {
                return r;
            }
        }

        return R.failed("??????");
    }

    private String generatorNote(PreRecharge domain) {

        StringBuilder stringBuilder = new StringBuilder();

        if(domain.getRemittanceMethod().equals("3")){
            stringBuilder.append("????????????:????????????");
            stringBuilder.append("????????????:????????????-??????");

        }else if(domain.getRemittanceMethod().equals("4")){
            stringBuilder.append("????????????:???????????????");
            stringBuilder.append("????????????:???????????????-??????");
        }

        stringBuilder.append("????????????:"+domain.getAmount());
        stringBuilder.append("?????????:"+domain.getProcedureAmount());
        stringBuilder.append("??????????????????:"+domain.getActurlAmount());
        stringBuilder.append("?????????:"+domain.getSerialNo());

        return stringBuilder.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R reject(PreRechargeAuditVO auditVO) {

        PreRechargeVerifyStatusEnum verifyStatus = auditVO.getVerifyStatus();

        if(!verifyStatus.name().equals(PreRechargeVerifyStatusEnum.REJECT.name())){
            return R.failed("????????????????????????");
        }

        Long id = auditVO.getId();
        PreRecharge preRecharge = preRechargeMapper.selectById(id);

        if(preRecharge == null){
            return R.failed("????????????????????????");
        }

        String remittanceMethod = preRecharge.getRemittanceMethod();

        if(remittanceMethod.equals("3") || remittanceMethod.equals("4")){
            return R.failed("???????????????????????????????????????");
        }

        try {

            CustPayDTO custPayDTO = new CustPayDTO();
            custPayDTO.setAmount(preRecharge.getAmount());
            custPayDTO.setCurrencyCode(preRecharge.getCurrencyCode());
            custPayDTO.setNo(preRecharge.getSerialNo());
            custPayDTO.setCusCode(preRecharge.getCusCode());
            custPayDTO.setNature("??????");
            custPayDTO.setChargeCategoryChange("????????????");
            custPayDTO.setBusinessType("????????????");

            R rs = accountBalanceService.feeDeductions(custPayDTO);

            if (rs.getCode() != 200) {
                return R.failed(rs.getMsg());
            }

            PreRecharge preRechargeUpd = new PreRecharge();
            preRechargeUpd.setId(id);
            preRechargeUpd.setVerifyStatus(verifyStatus.getValue().toString());
            preRechargeUpd.setRejectRemark(auditVO.getRejectRemark());
            preRechargeUpd.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
            preRechargeUpd.setUpdateByName(SecurityUtils.getLoginUser().getUsername());
            preRechargeUpd.setUpdateTime(new Date());
            preRechargeUpd.setRejectDate(new Date());

            preRechargeMapper.updateById(preRechargeUpd);

            return R.ok();

        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
