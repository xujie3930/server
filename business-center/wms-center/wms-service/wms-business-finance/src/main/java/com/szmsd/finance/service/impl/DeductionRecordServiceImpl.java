package com.szmsd.finance.service.impl;

import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.FssDeductionRecord;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.enums.CreditConstant;
import com.szmsd.finance.mapper.DeductionRecordMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IDeductionRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.finance.vo.CreditUseInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 扣费信息记录表 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-10-14
 */
@Slf4j
@Service
public class DeductionRecordServiceImpl extends ServiceImpl<DeductionRecordMapper, FssDeductionRecord> implements IDeductionRecordService {

    @Resource
    private IAccountBalanceService iAccountBalanceService;

    /**
     * 查询扣费信息记录表模块
     *
     * @param id 扣费信息记录表模块ID
     * @return 扣费信息记录表模块
     */
    @Override
    public FssDeductionRecord selectDeductionRecordById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询扣费信息记录表模块列表
     *
     * @param fssDeductionRecord 扣费信息记录表模块
     * @return 扣费信息记录表模块
     */
    @Override
    public List<FssDeductionRecord> selectDeductionRecordList(FssDeductionRecord fssDeductionRecord) {
        QueryWrapper<FssDeductionRecord> where = new QueryWrapper<FssDeductionRecord>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增扣费信息记录表模块
     *
     * @param fssDeductionRecord 扣费信息记录表模块
     * @return 结果
     */
    @Override
    public int insertDeductionRecord(FssDeductionRecord fssDeductionRecord) {
        return baseMapper.insert(fssDeductionRecord);
    }

    /**
     * 修改扣费信息记录表模块
     *
     * @param fssDeductionRecord 扣费信息记录表模块
     * @return 结果
     */
    @Override
    public int updateDeductionRecord(FssDeductionRecord fssDeductionRecord) {
        return baseMapper.updateById(fssDeductionRecord);
    }

    /**
     * 批量删除扣费信息记录表模块
     *
     * @param ids 需要删除的扣费信息记录表模块ID
     * @return 结果
     */
    @Override
    public int deleteDeductionRecordByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除扣费信息记录表模块信息
     *
     * @param id 扣费信息记录表模块ID
     * @return 结果
     */
    @Override
    public int deleteDeductionRecordById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateDeductionRecordStatus(List<String> updateCusCodeList) {
        if (CollectionUtils.isEmpty(updateCusCodeList)) return;
        LocalDateTime now = LocalDate.now().minusDays(1).atTime(23, 59, 59);
        int update = baseMapper.update(new FssDeductionRecord(), Wrappers.<FssDeductionRecord>lambdaUpdate()
                .in(FssDeductionRecord::getCusCode, updateCusCodeList)
                .eq(FssDeductionRecord::getStatus, CreditConstant.CreditBillStatusEnum.DEFAULT.getValue())
                .le(FssDeductionRecord::getCreditEndTime, now)
                .set(FssDeductionRecord::getStatus, CreditConstant.CreditBillStatusEnum.CHECKED.getValue())
        );
        log.info("截断账单 - {}条", update);
    }

    /**
     * 正常周期更新数据
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateRecordStatusByCreditTimeInterval() {
        List<AccountBalance> accountBalanceList = iAccountBalanceService.queryThePreTermBill();
        List<String> cusCodeList = accountBalanceList.stream().map(AccountBalance::getCusCode).collect(Collectors.toList());
        log.info("需要正常更新的账单用户-{}", cusCodeList.size());
        updateDeductionRecordStatus(cusCodeList);
    }

    @Override
    public Map<String, CreditUseInfo> queryTimeCreditUse(String cusCode, List<String> currencyCodeList, List<CreditConstant.CreditBillStatusEnum> statusList) {
        List<Integer> statusValueList = statusList.stream().map(CreditConstant.CreditBillStatusEnum::getValue).collect(Collectors.toList());
        List<CreditUseInfo> creditUseInfos = baseMapper.queryTimeCreditUse(cusCode, statusValueList, currencyCodeList);
        return creditUseInfos.stream().collect(Collectors.toMap(CreditUseInfo::getCurrencyCode, x -> x));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addForCreditBill(BigDecimal addMoney, String cusCode, String currencyCode) {
        StopWatch stopWatch = new StopWatch("还款销账");
        stopWatch.start();
        log.info("addForCreditBill {} - {} - {}", addMoney, cusCode, currencyCode);
        List<FssDeductionRecord> fssDeductionRecords = new ArrayList<>();
        while (addMoney.compareTo(BigDecimal.ZERO) > 0 && (fssDeductionRecords = baseMapper.selectList(Wrappers.<FssDeductionRecord>lambdaQuery()
                .eq(FssDeductionRecord::getCurrencyCode, currencyCode)
                .eq(FssDeductionRecord::getCusCode, cusCode)
                .in(FssDeductionRecord::getPayMethod, BillEnum.PayMethod.BALANCE_DEDUCTIONS.name(), BillEnum.PayMethod.BUSINESS_OPERATE.name())
                .in(FssDeductionRecord::getStatus, CreditConstant.CreditBillStatusEnum.DEFAULT.getValue(), CreditConstant.CreditBillStatusEnum.CHECKED.getValue())
                .orderByAsc(FssDeductionRecord::getId).last("LIMIT 10").select(
                        FssDeductionRecord::getId,
                        FssDeductionRecord::getStatus,
                        FssDeductionRecord::getAmount,
                        FssDeductionRecord::getActualDeduction,
                        FssDeductionRecord::getRemainingRepaymentAmount,
                        FssDeductionRecord::getCreditUseAmount,
                        FssDeductionRecord::getRepaymentAmount))).size() > 0) {
            log.info("inner==addForCreditBill {} - {} - {}", addMoney, cusCode, currencyCode);

            for (FssDeductionRecord x : fssDeductionRecords) {
                if (addMoney.compareTo(x.getRemainingRepaymentAmount()) >= 0) {
                    x.setRemainingRepaymentAmount(BigDecimal.ZERO);
                    x.setRepaymentAmount(x.getCreditUseAmount());
                    x.setStatus(CreditConstant.CreditBillStatusEnum.REPAID.getValue());
                    addMoney = addMoney.subtract(x.getRemainingRepaymentAmount());
                } else {
                    x.setRepaymentAmount(x.getRepaymentAmount().add(addMoney));
                    x.setRemainingRepaymentAmount(x.getRemainingRepaymentAmount().subtract(addMoney));
                    addMoney = BigDecimal.ZERO;
                }
            }
            this.updateBatchById(fssDeductionRecords);
        }
        stopWatch.stop();
        log.info("addForCreditBill {} - {} - {}\n{}", addMoney, cusCode, currencyCode, stopWatch.prettyPrint());
        // 多余的钱充值道钱包里面
    }

    /**
     * 移动无效的账单数据
     * 每次1W 迁移并删除原表数据
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long moveInvalidCreditBill() {
        Long aLong = baseMapper.moveInvalidCreditBill();
        baseMapper.removeInvalidCreditBill();
        return aLong;
    }
}

