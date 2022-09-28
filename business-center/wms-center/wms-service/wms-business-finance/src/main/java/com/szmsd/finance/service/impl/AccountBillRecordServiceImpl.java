package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.domain.AccountBillRecord;
import com.szmsd.finance.handler.BillGeneratorBO;
import com.szmsd.finance.handler.BillGeneratorExcelTask;
import com.szmsd.finance.handler.BillSelectBalanceTask;
import com.szmsd.finance.mapper.AccountBalanceLogMapper;
import com.szmsd.finance.mapper.AccountBillRecordMapper;
import com.szmsd.finance.mapper.AccountSerialBillMapper;
import com.szmsd.finance.service.AccountBillRecordService;
import com.szmsd.finance.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service("AccountBillRecordServiceImpl")
@Slf4j
public class AccountBillRecordServiceImpl implements AccountBillRecordService {


    @Resource
    private ThreadPoolTaskScheduler exportThreadPoolTaskExecutor;

    @Resource
    private AccountSerialBillMapper accountSerialBillMapper;

    @Resource
    private AccountBillRecordMapper accountBillRecordMapper;

    @Resource
    private BasFeignService basFeignService;

    @Resource
    private BasSellerFeignService basSellerFeignService;

    @Resource
    private AccountBalanceLogMapper accountBalanceLogMapper;


    @Value("${filepath}")
    private String filePath;

    @Override
    public List<ElectronicBillVO> electronicPage(EleBillQueryVO queryVO) {

        return accountBillRecordMapper.electronicPage(queryVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Integer> generatorBill(BillGeneratorRequestVO billRequestVO) {

        String cusCode = billRequestVO.getCusCode();
        R<BasSellerInfoVO> basSellerInfoVOR = basSellerFeignService.getInfoBySellerCode(cusCode);

        if(basSellerInfoVOR == null){
            throw new RuntimeException("生成失败，无法获取客户基本信息");
        }

        if(!Constants.SUCCESS.equals(basSellerInfoVOR.getCode())){
            throw new RuntimeException(basSellerInfoVOR.getMsg());
        }

        BasSellerInfoVO basSellerInfoVO = basSellerInfoVOR.getData();

        if(StringUtils.isBlank(basSellerInfoVO.getSellerCode())){
            throw new RuntimeException("生成失败，无法获取客户基本信息");
        }

        //保存记录 状态 处理中
        String recordId = this.addAccountBillRecord(billRequestVO);

        //异步生成excel
        BillGeneratorBO billGeneratorBO = new BillGeneratorBO();
        billGeneratorBO.setAccountSerialBillMapper(accountSerialBillMapper);
        billGeneratorBO.setAccountBalanceLogMapper(accountBalanceLogMapper);
        billGeneratorBO.setBillRequestVO(billRequestVO);
        billGeneratorBO.setFilePath(filePath);
        billGeneratorBO.setRecordId(recordId);
        billGeneratorBO.setBasFeignService(basFeignService);
        billGeneratorBO.setBasSellerInfoVO(basSellerInfoVO);
        BillGeneratorExcelTask task = new BillGeneratorExcelTask(billGeneratorBO);

        ListenableFuture<AccountBillRecordTaskResultVO> taskResultVOFuture = exportThreadPoolTaskExecutor.submitListenable(task);

        //异步回调 data 成功处理 ,ex 失败处理
        taskResultVOFuture.addCallback(data -> {

            if(data == null) {
                return;
            }

            //根据业务id，更新状态已处理
            String resultId = data.getRecordId();

            LambdaUpdateWrapper<AccountBillRecord> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
            lambdaUpdateWrapper.set(AccountBillRecord::getFileName, data.getFileName());
            lambdaUpdateWrapper.set(AccountBillRecord::getFileUrl, data.getFileUrl());
            lambdaUpdateWrapper.set(AccountBillRecord::getBuildStatus,2);
            lambdaUpdateWrapper.eq(AccountBillRecord::getRecordId, resultId);
            accountBillRecordMapper.update(null, lambdaUpdateWrapper);

        },ex -> {
            log.error("生成失败,",ex);
        });

        return R.ok();
    }

    @Override
    public List<BillBalanceVO> balancePage(EleBillQueryVO queryVO) {

        BillSelectBalanceTask billSelectBalanceTask = new BillSelectBalanceTask(accountSerialBillMapper,accountBalanceLogMapper,basFeignService);

        return billSelectBalanceTask.find(queryVO);
    }

    private String addAccountBillRecord(BillGeneratorRequestVO billRequestVO){

        LoginUser loginUser = SecurityUtils.getLoginUser();

        AccountBillRecord accountBillRecord = new AccountBillRecord();
        Date pbillStartTime = DateUtils.parseDate(billRequestVO.getBillStartTime());
        Date pbillendTime = DateUtils.parseDate(billRequestVO.getBillEndTime());
        accountBillRecord.setBillStartTime(pbillStartTime);
        accountBillRecord.setBillEndTime(pbillendTime);
        accountBillRecord.setCusCode(billRequestVO.getCusCode());
        accountBillRecord.setDeleted(0);
        accountBillRecord.setVersion(0L);
        accountBillRecord.setCreateBy(loginUser.getSellerCode());
        accountBillRecord.setCreateByName(loginUser.getUsername());
        //accountBillRecord.setFileName(fileName);
        //accountBillRecord.setFileUrl(f);
        accountBillRecord.setBuildStatus(1);
        accountBillRecord.setCreateTime(new Date());
        String recordId = UUID.randomUUID().toString();
        accountBillRecord.setRecordId(recordId);
        accountBillRecordMapper.insert(accountBillRecord);

        return recordId;
    }
}
