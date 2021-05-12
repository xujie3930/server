package com.szmsd.finance.factory.abstractFactory;

import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.AccountBalanceChangeDTO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IAccountSerialBillService;
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
    private IAccountSerialBillService accountSerialBillService;

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

    protected abstract void setOpLogAmount(AccountBalanceChange accountBalanceChange, BigDecimal amount);

    protected BigDecimal getCurrentBalance(String cusCode, String currencyCode) {
        return accountBalanceService.getCurrentBalance(cusCode, currencyCode);
    }

    protected void setCurrentBalance(String cusCode, String currencyCode, BigDecimal result) {
        accountBalanceService.setCurrentBalance(cusCode, currencyCode, result);
    }

    protected BalanceDTO getBalance(String cusCode, String currencyCode) {
        return accountBalanceService.getBalance(cusCode, currencyCode);
    }

    protected void setBalance(String cusCode, String currencyCode, BalanceDTO result) {
        accountBalanceService.setBalance(cusCode, currencyCode, result);
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
        accountSerialBillService.saveBatch(collect);
    }

}
