package com.szmsd.finance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.feign.EmailFeingService;
import com.szmsd.bas.dto.EmailDto;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountBalanceBillCurrencyVO;
import com.szmsd.finance.dto.AccountBalanceBillCusCurrencyVO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.AccountSyncErrorEmailDTO;
import com.szmsd.finance.entity.FssAccountBalanceLogNewEntity;
import com.szmsd.finance.mapper.AccountSerialBillMapper;
import com.szmsd.finance.mapper.FssAccountBalanceLogNewMapper;
import com.szmsd.finance.service.FssAccountBalanceLogNewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IAccountSerialBillService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 账户余额表日志新表 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2023-02-10
 */
@Service
public class FssAccountBalanceLogNewServiceImpl extends ServiceImpl<FssAccountBalanceLogNewMapper, FssAccountBalanceLogNewEntity> implements FssAccountBalanceLogNewService {

    @Autowired
    private IAccountBalanceService accountBalanceService;
    @Autowired
    private IAccountSerialBillService accountSerialBillService;
    @Autowired
    private EmailFeingService emailFeingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoSyncBalance() {
        Date date = new Date();
        List<AccountBalance> accountBalanceLastList = accountBalanceService.selectByDaily();
        if (CollectionUtils.isEmpty(accountBalanceLastList)) {
            return;
        }
        List<FssAccountBalanceLogNewEntity> todayList = BeanUtils.copyList(accountBalanceLastList, FssAccountBalanceLogNewEntity.class);
        todayList.stream().forEach(logEntity -> {
            logEntity.setGeneratorTime(date);
            logEntity.setId(null);
        });
        saveBatch(todayList);
        accountBalanceLastList.clear();
        //查找昨日的记录
        Date startTime = DateUtils.lastdayBegin(date);
        Date endTime = DateUtils.lastdayEnd(date);
        LambdaQueryWrapper<FssAccountBalanceLogNewEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.ge(FssAccountBalanceLogNewEntity::getUpdateTime, startTime);
        queryWrapper.le(FssAccountBalanceLogNewEntity::getUpdateTime, endTime);
        List<FssAccountBalanceLogNewEntity> yestodayList = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(yestodayList)) {
            return;
        }
        Map<String, FssAccountBalanceLogNewEntity> yestodayMap = yestodayList.stream().collect(Collectors.toMap(FssAccountBalanceLogNewEntity::getUniCode, Function.identity()));
        //昨日流水表的金额汇总
        AccountSerialBillDTO query = new AccountSerialBillDTO();
        query.setCreateTimeStart(DateUtils.dateToString(startTime));
        query.setCreateTimeEnd(DateUtils.dateToString(endTime));
        List<AccountBalanceBillCusCurrencyVO> billList = accountSerialBillService.findBillCusCurrencyData(query);
        //发送错误邮件列表
        List<AccountSyncErrorEmailDTO> emailList = new ArrayList<>();
        //和汇总金额做比对
        for (FssAccountBalanceLogNewEntity today : todayList) {
            FssAccountBalanceLogNewEntity yestoday = yestodayMap.get(today.getCusCode() + today.getCurrencyCode());
            //说明是新创建的账号，不用比较
            if (Objects.isNull(yestoday)){
                continue;
            }
            //查看今天和昨天金额差距
            BigDecimal yestodayAmount = yestoday.getTotalBalance().subtract(yestoday.getCreditUseAmount() == null? BigDecimal.ZERO : yestoday.getCreditUseAmount());
            BigDecimal todayAmount = today.getTotalBalance().subtract(today.getCreditUseAmount() == null? BigDecimal.ZERO : today.getCreditUseAmount());
            //和流水表做比对，且由于groupby后同一个cusCode和currencyCode下只会有一个金额
            Optional<AccountBalanceBillCusCurrencyVO> first = billList.stream().filter(vo -> vo.getCusCode().equals(today.getCusCode()) && vo.getCurrencyCode().equals(today.getCurrencyCode())).findFirst();
            //该账户该币种未必有流水记录
            if (!first.isPresent()) {
                continue;
            }
            AccountBalanceBillCusCurrencyVO changeAmount = first.get();
            if (todayAmount.subtract(yestodayAmount).compareTo(changeAmount.getAmount()) != 0){
                //客户代码，
                emailList.add(new AccountSyncErrorEmailDTO(today.getCusCode(), today.getCurrencyCode(), yestodayAmount, todayAmount, changeAmount.getAmount()));
            }
        }
        if(CollectionUtils.isNotEmpty(emailList)){
            EmailDto emailDto = new EmailDto();
            emailDto.setText(JSONObject.toJSONString(emailList));
                emailFeingService.sendEmailError(emailDto);
        }
    }
}
