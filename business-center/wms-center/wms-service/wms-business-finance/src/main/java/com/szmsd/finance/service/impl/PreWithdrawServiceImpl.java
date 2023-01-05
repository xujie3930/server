package com.szmsd.finance.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.domain.PreWithdraw;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.PreRechargeAuditDTO;
import com.szmsd.finance.dto.PreWithdrawDTO;
import com.szmsd.finance.mapper.PreWithdrawMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IPreWithdrawService;
import com.szmsd.finance.vo.PreWithdrawRejectVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author liulei
 */
@Service
public class PreWithdrawServiceImpl implements IPreWithdrawService {
    @Autowired
    IAccountBalanceService accountBalanceService;

    @Autowired
    PreWithdrawMapper preWithdrawMapper;

    @Override
    public List<PreWithdraw> listPage(PreWithdrawDTO dto) {
        LambdaQueryWrapper<PreWithdraw> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.isNotNull(PreWithdraw::getCusCode);
        if(dto.getCusId()!=null) {
            queryWrapper.eq(PreWithdraw::getCusCode, dto.getCusCode());
        }
        if(StringUtils.isNotEmpty(dto.getVerifyStatus())){
            queryWrapper.eq(PreWithdraw::getVerifyStatus,dto.getVerifyStatus());
        }
        if(StringUtils.isNotEmpty(dto.getCusCode())){
            queryWrapper.eq(PreWithdraw::getCusCode,dto.getCusCode());
        }
        if(StringUtils.isNotEmpty(dto.getCurrencyCode())){
            queryWrapper.eq(PreWithdraw::getCurrencyCode,dto.getCurrencyCode());
        }
        if(StringUtils.isNotEmpty(dto.getBeginTime())){
            queryWrapper.ge(PreWithdraw::getCreateTime,dto.getBeginTime());
        }
        if(StringUtils.isNotEmpty(dto.getEndTime())){
            queryWrapper.le(PreWithdraw::getCreateTime,dto.getEndTime());
        }
        queryWrapper.orderByAsc(PreWithdraw::getVerifyStatus).orderByDesc(PreWithdraw::getCreateTime);
        return preWithdrawMapper.listPage(queryWrapper);
    }

    @Override
    public R save(PreWithdrawDTO dto) {
        if(StringUtils.isEmpty(dto.getCusCode())){
            return R.failed("Customer code cannot be empty");
        }
        if(!accountBalanceService.withDrawBalanceCheck(dto.getCusCode(),dto.getCurrencyCode(),dto.getAmount())){
            return R.failed("Sorry, your credit is running low");
        }

        String serialNo = generatorSerialNo();
        dto.setSerialNo(serialNo);

        PreWithdraw domain= new PreWithdraw();
        BeanUtils.copyProperties(dto,domain);
        domain.setWithdrawTime(new Date());
        int insert = preWithdrawMapper.insert(domain);
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
        PreWithdraw preWithdraw = preWithdrawMapper.selectById(dto.getId());
        preWithdraw.setVerifyStatus(dto.getVerifyStatus());
        if("1".equals(dto.getVerifyStatus())){
            CustPayDTO custPayDTO=new CustPayDTO();
            custPayDTO.setAmount(preWithdraw.getAmount());
            custPayDTO.setCusCode(preWithdraw.getCusCode());
            custPayDTO.setCusName(preWithdraw.getCusName());
            custPayDTO.setCurrencyCode(preWithdraw.getCurrencyCode());
            custPayDTO.setCurrencyName(preWithdraw.getCurrencyName());
            custPayDTO.setNo(preWithdraw.getSerialNo());
            custPayDTO.setOrderTime(preWithdraw.getCreateTime());
            custPayDTO.setNature("提现");
            custPayDTO.setBusinessType("提现");
            custPayDTO.setChargeCategoryChange("提现");
            accountBalanceService.withdraw(custPayDTO);
        }
        preWithdraw.setVerifyRemark(dto.getVerifyRemark());
        preWithdraw.setVerifyDate(new Date());
        preWithdraw.setPaymentVoucher(dto.getPaymentVoucher());
        preWithdrawMapper.updateById(preWithdraw);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R reject(PreWithdrawRejectVO rejectVO) {

        List<String> ids =  rejectVO.getIds();

        if(CollectionUtils.isEmpty(ids)){
            return R.failed("主键ID不能为空");
        }

        List<PreWithdraw> preWithdrawList = preWithdrawMapper.selectList(Wrappers.<PreWithdraw>query().lambda().in(PreWithdraw::getId,ids));

        for(PreWithdraw preWithdraw : preWithdrawList) {

            String verifyStatus = preWithdraw.getVerifyStatus();

            if (!verifyStatus.equals("1")) {
                return R.failed("状态必须审核通过才允许提现驳回");
            }

            CustPayDTO custPayDTO = new CustPayDTO();
            custPayDTO.setAmount(preWithdraw.getAmount());
            custPayDTO.setCusCode(preWithdraw.getCusCode());
            custPayDTO.setCusName(preWithdraw.getCusName());
            custPayDTO.setCurrencyCode(preWithdraw.getCurrencyCode().trim());
            custPayDTO.setCurrencyName(preWithdraw.getCurrencyName().trim());
            custPayDTO.setOrderTime(new Date());
            custPayDTO.setNo(preWithdraw.getSerialNo());
            custPayDTO.setNote("提现驳回");
            R r = accountBalanceService.offlineIncome(custPayDTO);
            if (Constants.SUCCESS != r.getCode()) {
                return r;
            }

            PreWithdraw upd = new PreWithdraw();
            upd.setId(preWithdraw.getId());
            upd.setVerifyStatus("3");
            upd.setRejectRemark(rejectVO.getRejectRemark());
            upd.setRejectUserBy(SecurityUtils.getLoginUser().getSellerCode());
            upd.setRejectTime(new Date());
            preWithdrawMapper.updateById(upd);

        }

        return R.ok();
    }
}
