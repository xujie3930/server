package com.szmsd.finance.factory.abstractFactory;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.domain.FssDeductionRecord;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IAccountSerialBillService;
import com.szmsd.finance.service.IDeductionRecordService;
import com.szmsd.finance.service.ISysDictDataService;
import com.szmsd.finance.util.SnowflakeId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author liulei
 */
@Slf4j
public abstract class AbstractPayFactory {

    public static final TimeUnit unit = TimeUnit.MILLISECONDS;

    public static final long time = 1000L;

    @Autowired
    IAccountBalanceService accountBalanceService;

    @Resource
    AccountBalanceChangeMapper accountBalanceChangeMapper;

    @Autowired
    ISysDictDataService sysDictDataService;

    @Resource
    private IAccountBalanceService iAccountBalanceService;

    @Resource
    private IAccountSerialBillService accountSerialBillService;

    @Resource
    private IDeductionRecordService iDeductionRecordService;


    public abstract boolean updateBalance(CustPayDTO dto);

    public AccountBalanceChange recordOpLog(CustPayDTO dto, BigDecimal result) {
        AccountBalanceChange accountBalanceChange = new AccountBalanceChange();
        BeanUtils.copyProperties(dto, accountBalanceChange);
        if (StringUtils.isEmpty(accountBalanceChange.getCurrencyName())) {
            String currencyName = sysDictDataService.getCurrencyNameByCode(accountBalanceChange.getCurrencyCode());
            accountBalanceChange.setCurrencyName(currencyName);
            dto.setCurrencyName(currencyName);
        }

        accountBalanceChange.setSerialNum(SnowflakeId.getNextId12());
        setOpLogAmount(accountBalanceChange, dto.getAmount());
        accountBalanceChange.setCurrentBalance(result);
        accountBalanceChangeMapper.insert(accountBalanceChange);
        return accountBalanceChange;
    }

    /**
     * 详细使用记录
     *
     * @param custPayDTO
     * @param balanceDTO
     */
    public void recordDetailLog(CustPayDTO custPayDTO, BalanceDTO balanceDTO) {
        FssDeductionRecord fssDeductionRecord = new FssDeductionRecord();
        CreditInfoBO creditInfoBO = balanceDTO.getCreditInfoBO();
        fssDeductionRecord.setPayMethod(custPayDTO.getPayMethod().name())
                .setAmount(custPayDTO.getAmount()).setRepaymentAmount(BigDecimal.ZERO)
                .setRemainingRepaymentAmount(custPayDTO.getAmount())
                .setOrderNo(custPayDTO.getNo())
                .setCusCode(custPayDTO.getCusCode()).setCurrencyCode(custPayDTO.getCurrencyCode())
                .setActualDeduction(balanceDTO.getActualDeduction()).setCreditUseAmount(balanceDTO.getCreditUseAmount())
                .setCreditBeginTime(creditInfoBO.getCreditBeginTime()).setCreditEndTime(creditInfoBO.getCreditEndTime())
        ;
        iDeductionRecordService.save(fssDeductionRecord);
    }

    protected abstract void setOpLogAmount(AccountBalanceChange accountBalanceChange, BigDecimal amount);

    protected BigDecimal getCurrentBalance(String cusCode, String currencyCode) {
        return accountBalanceService.getCurrentBalance(cusCode, currencyCode);
    }

    protected void setCurrentBalance(String cusCode, String currencyCode, BigDecimal result) {
        accountBalanceService.setCurrentBalance(cusCode, currencyCode, result);
    }

    /**
     * 查询该用户对应币别的余额
     *
     * @param cusCode      客户编码
     * @param currencyCode 币别
     * @return 查询结果
     */
    protected BalanceDTO getBalance(String cusCode, String currencyCode) {
        return accountBalanceService.getBalance(cusCode, currencyCode);
    }

    protected void updateCreditStatus(CustPayDTO dto) {
        iAccountBalanceService.updateCreditStatus(dto);
    }

    /**
     * 需要扣减信用额
     *
     * @param cusCode
     * @param currencyCode
     * @param result
     * @param need
     */
    protected void setBalance(String cusCode, String currencyCode, BalanceDTO result, boolean needUpdateCredit) {
        accountBalanceService.setBalance(cusCode, currencyCode, result, true);
    }

    protected void setBalance(String cusCode, String currencyCode, BalanceDTO result) {
        accountBalanceService.setBalance(cusCode, currencyCode, result, false);
    }

    public abstract BalanceDTO calculateBalance(BalanceDTO oldBalance, BigDecimal changeAmount);

    protected void setHasFreeze(CustPayDTO dto) {
        AccountBalanceChangeDTO accountBalanceChange = new AccountBalanceChangeDTO();
        accountBalanceChange.setNo(dto.getNo());
        accountBalanceChange.setCurrencyCode(dto.getCurrencyCode());
        accountBalanceChange.setOrderType(dto.getOrderType());
        accountBalanceChange.setCusCode(dto.getCusCode());
        accountBalanceChange.setHasFreeze(false);
        accountBalanceChange.setPayMethod(BillEnum.PayMethod.BALANCE_FREEZE); //修改冻结的单
        accountBalanceService.updateAccountBalanceChange(accountBalanceChange);
    }

    public void setSerialBillLog(CustPayDTO dto) {
        log.info("setSerialBillLog - {}", JSONObject.toJSONString(dto));
        if (CollectionUtils.isEmpty(dto.getSerialBillInfoList())) {
            log.info("setSerialBillLog() list is empty :{} ", dto);
            AccountSerialBillDTO accountSerialBillDTO = BeanMapperUtil.map(dto, AccountSerialBillDTO.class);
            String paymentName = accountSerialBillDTO.getPayMethod().getPaymentName();
            accountSerialBillDTO.setBusinessCategory(paymentName);
            accountSerialBillDTO.setProductCategory(paymentName);
            String currencyName = accountSerialBillDTO.getCurrencyName();
            currencyName = currencyName == null ? "" : currencyName;
            accountSerialBillDTO.setChargeCategory(paymentName.concat(currencyName));
            accountSerialBillDTO.setChargeType(paymentName);
            accountSerialBillService.add(accountSerialBillDTO);
            return;
        }
        List<AccountSerialBillDTO> collect = dto.getSerialBillInfoList()
                .stream().map(value -> new AccountSerialBillDTO(dto, value)).collect(Collectors.toList());
        List<AccountSerialBillDTO> distanctList = collect.stream().filter(x -> StringUtils.isNotBlank(x.getChargeCategory()) && "物流基础费".equals(x.getChargeCategory()) && BillEnum.PayMethod.BALANCE_DEDUCTIONS.equals(x.getPayMethod())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(distanctList) && distanctList.size() > 1) {
            AccountSerialBillDTO accountSerialBillDTO = distanctList.get(0);
            log.info("删除物流操作费{}", JSONObject.toJSONString(accountSerialBillDTO));
            collect.remove(accountSerialBillDTO);
        }
        log.info("删除物流操作费后保存{}", JSONObject.toJSONString(collect));
        accountSerialBillService.saveBatch(collect);
    }

    protected void addForCreditBill(BigDecimal addMoney,String cusCode,String currencyCode) {
        if (addMoney.compareTo(BigDecimal.ZERO)>=0) return;
        iDeductionRecordService.addForCreditBill(addMoney,cusCode,currencyCode);
    }

}
