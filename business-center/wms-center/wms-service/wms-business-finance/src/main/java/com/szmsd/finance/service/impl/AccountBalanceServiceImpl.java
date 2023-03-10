package com.szmsd.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.szmsd.chargerules.api.feign.ChargeFeignService;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.BigDecimalUtil;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.dto.DelQueryServiceImport;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.domain.AccountBalanceExcle;
import com.szmsd.finance.domain.ThirdRechargeRecord;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.enums.CreditConstant;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.factory.abstractFactory.PayFactoryBuilder;
import com.szmsd.finance.handler.FreezeBalanceConsumer;
import com.szmsd.finance.handler.FreezeBalanceProducer;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import com.szmsd.finance.mapper.AccountBalanceMapper;
import com.szmsd.finance.mapper.ExchangeRateMapper;
import com.szmsd.finance.service.*;
import com.szmsd.finance.util.LogUtil;
import com.szmsd.finance.util.SnowflakeId;
import com.szmsd.finance.vo.CreditUseInfo;
import com.szmsd.finance.vo.PreOnlineIncomeVo;
import com.szmsd.finance.vo.UserCreditInfoVO;
import com.szmsd.http.api.feign.HttpRechargeFeignService;
import com.szmsd.http.dto.recharges.RechargesRequestAmountDTO;
import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.enums.HttpRechargeConstants;
import com.szmsd.http.vo.RechargesResponseVo;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.szmsd.finance.factory.abstractFactory.AbstractPayFactory.leaseTime;
import static com.szmsd.finance.factory.abstractFactory.AbstractPayFactory.time;

/**
 * @author liulei
 */
@Service
@Slf4j
public class AccountBalanceServiceImpl implements IAccountBalanceService {

    @Autowired
    PayFactoryBuilder payFactoryBuilder;

    @Autowired
    AccountBalanceMapper accountBalanceMapper;

    @Autowired
    AccountBalanceChangeMapper accountBalanceChangeMapper;

    @Autowired
    HttpRechargeFeignService httpRechargeFeignService;

    @Autowired
    IThirdRechargeRecordService thirdRechargeRecordService;

    @Autowired
    ISysDictDataService sysDictDataService;

    @Autowired
    ExchangeRateMapper exchangeRateMapper;
    @Resource
    private IAccountSerialBillService accountSerialBillService;
    @Resource
    private IDeductionRecordService iDeductionRecordService;
    @Resource
    private ThreadPoolTaskExecutor financeThreadTaskPool;
    @Resource
    private ChargeFeignService chargeFeignService;
    @Resource
    private DelOutboundFeignService delOutboundFeignService;
    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public R<PageInfo<AccountBalance>> listPage(AccountBalanceDTO dto,String len) {
        try {
            LambdaQueryWrapper<AccountBalance> queryWrapper = Wrappers.lambdaQuery();
            if (StringUtils.isNotEmpty(dto.getCusCode())) {
                queryWrapper.eq(AccountBalance::getCusCode, dto.getCusCode());
            }

            if(StringUtils.isNotEmpty(dto.getCurrencyCode())){
                queryWrapper.eq(AccountBalance::getCurrencyCode,dto.getCurrencyCode());
            }

            LoginUser loginUser = SecurityUtils.getLoginUser();
            List<String> sellerCodeList = null;
            List<String> sellerCodeList1 = null;
            if (null != loginUser && !loginUser.getUsername().equals("admin")) {
                String username = loginUser.getUsername();
                sellerCodeList = accountBalanceMapper.selectsellerCode(username);

                if (sellerCodeList.size() > 0) {
                    queryWrapper.in(AccountBalance::getCusCode, sellerCodeList);

                } else if (sellerCodeList.size() == 0) {
                    sellerCodeList1 = accountBalanceMapper.selectsellerCodeus(username);
                    if (sellerCodeList1.size() > 0) {
                        queryWrapper.in(AccountBalance::getCusCode, sellerCodeList1);
                    } else {
                        queryWrapper.in(AccountBalance::getCusCode, "");
                    }
                }


                if (null != loginUser && loginUser.getUsername().equals("admin")) {
                    sellerCodeList = accountBalanceMapper.selectsellerCodes();
                    if (sellerCodeList.size() > 0) {
                        queryWrapper.in(AccountBalance::getCusCode, sellerCodeList);

                    }
                }
            }
            //??????????????????
            PageHelper.startPage(dto.getPageNum(),dto.getPageSize());

            List<AccountBalance> accountBalances = accountBalanceMapper.listPage(queryWrapper);

//            accountBalances.forEach(x -> {
//                Map<String, CreditUseInfo> creditUseInfoMap = iDeductionRecordService.queryTimeCreditUse( x.getCusCode(), new ArrayList<>(), Arrays.asList(CreditConstant.CreditBillStatusEnum.DEFAULT, CreditConstant.CreditBillStatusEnum.CHECKED));
//                Map<String, CreditUseInfo> needRepayCreditUseInfoMap = iDeductionRecordService.queryTimeCreditUse( x.getCusCode(), new ArrayList<>(), Arrays.asList(CreditConstant.CreditBillStatusEnum.CHECKED));
//                String currencyCode = x.getCurrencyCode();
//                BigDecimal creditUseAmount = Optional.ofNullable(creditUseInfoMap.get(currencyCode)).map(CreditUseInfo::getCreditUseAmount).orElse(BigDecimal.ZERO);
//                x.setCreditUseAmount(creditUseAmount);
//                BigDecimal needRepayCreditUseAmount = Optional.ofNullable(needRepayCreditUseInfoMap.get(currencyCode)).map(CreditUseInfo::getCreditUseAmount).orElse(BigDecimal.ZERO);
//                x.setNeedRepayCreditUseAmount(needRepayCreditUseAmount);
//            });
//            accountBalances.forEach(AccountBalance::showCredit);

            accountBalances.forEach(x-> {
                if (len.equals("en")) {
                    x.setCurrencyName(x.getCurrencyCode());
                } else if (len.equals("zh")) {
                    x.setCurrencyName(x.getCurrencyName());
                }
                
                BigDecimal creditUseAmount = x.getCreditUseAmount();
                BigDecimal totalBalance = x.getTotalBalance();
                BigDecimal currentBalance = x.getCurrentBalance();

                if(creditUseAmount.compareTo(BigDecimal.ZERO) > 0){
                    BigDecimal newTotalBalance = totalBalance.subtract(creditUseAmount);
                    BigDecimal newCurrentBalance = currentBalance.subtract(creditUseAmount);
                    x.setTotalBalance(newTotalBalance);
                    x.setCurrentBalance(newCurrentBalance);
                }
            });

            //??????????????????
            PageInfo<AccountBalance> pageInfo=new PageInfo<>(accountBalances);
            return R.ok(pageInfo);
        }catch (Exception e){
            e.printStackTrace();
           return R.failed("Query failed");
        }


    }

    @Override
    public List<AccountBalanceExcle> accountBalanceExport(AccountBalanceDTO dto, String len) {
        try {
            LambdaQueryWrapper<AccountBalance> queryWrapper = Wrappers.lambdaQuery();
            if (StringUtils.isNotEmpty(dto.getCusCode())) {
                queryWrapper.eq(AccountBalance::getCusCode, dto.getCusCode());
            }

            LoginUser loginUser = SecurityUtils.getLoginUser();
            List<String> sellerCodeList=null;
            List<String> sellerCodeList1=null;
            if (null != loginUser && !loginUser.getUsername().equals("admin")) {
                String username = loginUser.getUsername();
                sellerCodeList=accountBalanceMapper.selectsellerCode(username);

                if (sellerCodeList.size()>0){
                    queryWrapper.in(AccountBalance::getCusCode, sellerCodeList);

                } else if (sellerCodeList.size()==0){
                    sellerCodeList1=accountBalanceMapper.selectsellerCodeus(username);
                    if (sellerCodeList1.size()>0){
                        queryWrapper.in(AccountBalance::getCusCode, sellerCodeList1);
                    }else {
                        queryWrapper.in(AccountBalance::getCusCode, "");
                    }
                }
                if (StringUtils.isNotEmpty(dto.getCurrencyCode())) {
                    queryWrapper.eq(AccountBalance::getCurrencyCode, dto.getCurrencyCode());
                }

            }
            if (null != loginUser && loginUser.getUsername().equals("admin")){
                sellerCodeList=accountBalanceMapper.selectsellerCodes();
                if (sellerCodeList.size()>0){
                    queryWrapper.in(AccountBalance::getCusCode, sellerCodeList);

                }
                if (StringUtils.isNotEmpty(dto.getCurrencyCode())) {
                    queryWrapper.eq(AccountBalance::getCurrencyCode, dto.getCurrencyCode());
                }

            }
            if (dto.getIds()!=null&&dto.getIds().size()>0){
                queryWrapper.in(AccountBalance::getId, dto.getIds());
            }
            //??????????????????
            PageHelper.startPage(dto.getPageNum(),dto.getPageSize());

            List<AccountBalance> accountBalances = accountBalanceMapper.listPage(queryWrapper);




            accountBalances.forEach(x -> {
                Map<String, CreditUseInfo> creditUseInfoMap = iDeductionRecordService.queryTimeCreditUse( x.getCusCode(), new ArrayList<>(), Arrays.asList(CreditConstant.CreditBillStatusEnum.DEFAULT, CreditConstant.CreditBillStatusEnum.CHECKED));
                Map<String, CreditUseInfo> needRepayCreditUseInfoMap = iDeductionRecordService.queryTimeCreditUse( x.getCusCode(), new ArrayList<>(), Arrays.asList(CreditConstant.CreditBillStatusEnum.CHECKED));
                String currencyCode = x.getCurrencyCode();
                BigDecimal creditUseAmount = Optional.ofNullable(creditUseInfoMap.get(currencyCode)).map(CreditUseInfo::getCreditUseAmount).orElse(BigDecimal.ZERO);
                x.setCreditUseAmount(creditUseAmount);
                BigDecimal needRepayCreditUseAmount = Optional.ofNullable(needRepayCreditUseInfoMap.get(currencyCode)).map(CreditUseInfo::getCreditUseAmount).orElse(BigDecimal.ZERO);
                x.setNeedRepayCreditUseAmount(needRepayCreditUseAmount);
            });
            accountBalances.forEach(AccountBalance::showCredit);

            accountBalances.forEach(x-> {
                if (len.equals("en")) {
                    if (x.getCreditType().equals("0")){
                        x.setCreditType("quota");
                    }else if (x.getCreditType().equals("1")){
                        x.setCreditType("type");
                    }
                    x.setCurrencyName(x.getCurrencyCode());
                } else if (len.equals("zh")) {
                    x.setCurrencyName(x.getCurrencyName());
                    if (x.getCreditType().equals("0")){
                        x.setCreditType("??????");
                    }else if (x.getCreditType().equals("1")){
                        x.setCreditType("??????");
                    }
                }

            });

            List<AccountBalanceExcle> list = BeanMapperUtil.mapList(accountBalances, AccountBalanceExcle.class);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            list.forEach(x->{
                if (x.getCreateTime()!=null){
                    x.setCreateTimes(sdf.format(x.getCreateTime()));


                }
//                if (x.getCreditEndTime()!=null){
//                    x.setCreditEndTimes(sdf.format(x.getCreditEndTime()));
//
//                }
//                if (x.getCreditBufferTime()!=null){
//                    x.setCreditBufferTimes(sdf.format(x.getCreditBufferTime()));
//
//                }
                x.setTotalBalance( x.getTotalBalance().stripTrailingZeros());
                x.setCurrentBalance( x.getCurrentBalance().stripTrailingZeros());
                x.setFreezeBalance( x.getFreezeBalance().stripTrailingZeros());


            });

            //??????????????????

            return list;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    @Override
    public List<AccountBalance> listPages(AccountBalanceDTO dto) {
        LambdaQueryWrapper<AccountBalance> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(dto.getCusCode())) {
            queryWrapper.eq(AccountBalance::getCusCode, dto.getCusCode());
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<String> sellerCodeList=null;
        if (null != loginUser && !loginUser.getUsername().equals("admin")) {
            String username = loginUser.getUsername();
            sellerCodeList=accountBalanceMapper.selectsellerCode(username);

            if (sellerCodeList.size()>0){
                queryWrapper.in(AccountBalance::getCusCode, sellerCodeList);

            }
            if (StringUtils.isNotEmpty(dto.getCurrencyCode())) {
                queryWrapper.eq(AccountBalance::getCurrencyCode, dto.getCurrencyCode());
            }

        }
        if (null != loginUser && loginUser.getUsername().equals("admin")){
            sellerCodeList=accountBalanceMapper.selectsellerCodes();

        }

        List<AccountBalance> accountBalances = accountBalanceMapper.listPage(queryWrapper);




        accountBalances.forEach(x -> {
            Map<String, CreditUseInfo> creditUseInfoMap = iDeductionRecordService.queryTimeCreditUse( x.getCusCode(), new ArrayList<>(), Arrays.asList(CreditConstant.CreditBillStatusEnum.DEFAULT, CreditConstant.CreditBillStatusEnum.CHECKED));
            Map<String, CreditUseInfo> needRepayCreditUseInfoMap = iDeductionRecordService.queryTimeCreditUse( x.getCusCode(), new ArrayList<>(), Arrays.asList(CreditConstant.CreditBillStatusEnum.CHECKED));
            String currencyCode = x.getCurrencyCode();
            BigDecimal creditUseAmount = Optional.ofNullable(creditUseInfoMap.get(currencyCode)).map(CreditUseInfo::getCreditUseAmount).orElse(BigDecimal.ZERO);
            x.setCreditUseAmount(creditUseAmount);
            BigDecimal needRepayCreditUseAmount = Optional.ofNullable(needRepayCreditUseInfoMap.get(currencyCode)).map(CreditUseInfo::getCreditUseAmount).orElse(BigDecimal.ZERO);
            x.setNeedRepayCreditUseAmount(needRepayCreditUseAmount);
        });
        accountBalances.forEach(AccountBalance::showCredit);
        return accountBalances;
    }

    @Override
    public List<AccountBalanceChange> recordListPage(AccountBalanceChangeDTO dto) {
        LambdaQueryWrapper<AccountBalanceChange> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(dto.getCusCode())) {
            queryWrapper.eq(AccountBalanceChange::getCusCode, dto.getCusCode());
        }
        if (dto.getPayMethod() != null) {
            if (dto.getPayMethod() == BillEnum.PayMethod.EXCHANGE_INCOME) {
                queryWrapper.and(wrapper -> wrapper.eq(AccountBalanceChange::getPayMethod, BillEnum.PayMethod.EXCHANGE_INCOME)
                        .or().eq(AccountBalanceChange::getPayMethod, BillEnum.PayMethod.EXCHANGE_PAYMENT));
            } else {
                queryWrapper.eq(AccountBalanceChange::getPayMethod, dto.getPayMethod());
            }
        }
        if (StringUtils.isNotEmpty(dto.getBeginTime())) {
            queryWrapper.ge(AccountBalanceChange::getCreateTime, dto.getBeginTime());
        }
        if (StringUtils.isNotEmpty(dto.getEndTime())) {
            queryWrapper.le(AccountBalanceChange::getCreateTime, dto.getEndTime());
        }
        if (StringUtils.isNotEmpty(dto.getNo())) {
            queryWrapper.eq(AccountBalanceChange::getNo, dto.getNo());
        }
        if (dto.getHasFreeze() != null) {
            queryWrapper.eq(AccountBalanceChange::getHasFreeze, dto.getHasFreeze());
        }
        if (StringUtils.isNotEmpty(dto.getOrderType())) {
            queryWrapper.eq(AccountBalanceChange::getOrderType, dto.getOrderType());
        }

        if(StringUtils.isNotEmpty(dto.getCurrencyCode())){
            queryWrapper.eq(AccountBalanceChange::getCurrencyCode,dto.getCurrencyCode());
        }

        queryWrapper.orderByDesc(AccountBalanceChange::getCreateTime);
        return accountBalanceChangeMapper.recordListPage(queryWrapper);
    }

    /**
     * ???????????????
     *
     * @param dto
     * @return
     */
    @Override
    public R preOnlineIncome(CustPayDTO dto) {
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("Customer Code/currency Cannot be blank and the amount must be greater than 0.01");
        }
        RechargesRequestDTO rechargesRequestDTO = new RechargesRequestDTO();
        //??????rechargesRequestDTO?????????
        fillRechargesRequestDTO(rechargesRequestDTO, dto);
        R<RechargesResponseVo> result = httpRechargeFeignService.onlineRecharge(rechargesRequestDTO);
        RechargesResponseVo vo = result.getData();
        //???????????????????????????????????????
        thirdRechargeRecordService.saveRecord(dto, vo);
        if (result.getCode() != 200 || vo == null || StringUtils.isNotEmpty(vo.getCode())) {
            if (vo != null && StringUtils.isNotEmpty(vo.getCode())) {
                return R.failed(vo.getMessage());
            }
            return R.failed();
        }
        String rechargeUrl = vo.getRechargeUrl();
        if (StringUtils.isEmpty(rechargeUrl)) {
            return R.failed();
        }
        return R.ok(new PreOnlineIncomeVo(rechargesRequestDTO.getSerialNo(), rechargeUrl));
    }

    @Override
    @Transactional
    public R rechargeCallback(RechargesCallbackRequestDTO requestDTO) {
        //?????????????????????????????????
        ThirdRechargeRecord thirdRechargeRecord = thirdRechargeRecordService.updateRecordIfSuccess(requestDTO);
        if (thirdRechargeRecord == null) {
            return R.failed("No corresponding recharge record found");
        }
        String rechargeStatus = HttpRechargeConstants.RechargeStatusCode.Successed.name();
        //??????????????????????????????
        if (StringUtils.equals(thirdRechargeRecord.getRechargeStatus(), rechargeStatus)) {
            CustPayDTO dto = new CustPayDTO();
            dto.setAmount(thirdRechargeRecord.getActualAmount());
            dto.setCurrencyCode(thirdRechargeRecord.getActualCurrency());
            dto.setCusCode(thirdRechargeRecord.getCusCode());
            dto.setRemark("The service charge is: ".concat(thirdRechargeRecord.getTransactionAmount().toString().concat(thirdRechargeRecord.getTransactionCurrency())));
            dto.setNo(thirdRechargeRecord.getRechargeNo());
            dto.setNature("??????");
            dto.setBusinessType("????????????");
            dto.setChargeCategoryChange("????????????");
            return onlineIncome(dto);
        }
        return R.ok();
    }

    @Override
    public R warehouseFeeDeductions(CustPayDTO dto) {

        if (BigDecimal.ZERO.compareTo(dto.getAmount()) == 0) return R.ok();
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("Customer code/currency cannot be blank and the amount must be greater than 0.01");
        }
        if (dto.getPayType() == null) {
            return R.failed("Payment type is empty");
        }

        setCurrencyName(dto);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        log.info(LogUtil.format("???????????????", dto));
        Boolean flag = abstractPayFactory.updateBalance(dto);
        if (Objects.isNull(flag)) return R.ok();
        if (flag) {
            log.info(LogUtil.format(dto, "???????????????", "?????????????????????"));
            this.addOptLogAsync(dto);
        }
        return flag ? R.ok() : R.failed(Strings.nullToEmpty(dto.getCurrencyName()) + "Insufficient account balance");
    }

    /**
     * ???????????? ?????????????????? ??? ??????????????????-????????????
     *
     * @param dto
     * @return true : ?????????
     */
    public boolean checkForDuplicateCharges(CustPayDTO dto) {
        return accountSerialBillService.checkForDuplicateCharges(dto);
    }

    @Transactional
    @Override
    public R feeDeductions(CustPayDTO dto) {
        if (BigDecimal.ZERO.compareTo(dto.getAmount()) == 0){
            return R.ok();
        }
        // ??????
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("Customer code/currency cannot be blank and the amount must be greater than 0.01");
        }

        boolean b = checkForDuplicateCharges(dto);

        log.info("feeDeductions ???????????? ??????????????????:{},{}",dto.getNo(),b);

        if (b){
            return R.ok();
        }

        setCurrencyName(dto);
        dto.setPayMethod(BillEnum.PayMethod.BALANCE_DEDUCTIONS);
        dto.setPayType(BillEnum.PayType.PAYMENT);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        log.info(LogUtil.format(dto, "????????????"));
        Boolean flag = abstractPayFactory.updateBalance(dto);

        if (Objects.isNull(flag)){
            return R.ok();
        }

        log.info("feeDeductions ????????????:{},{}",dto.getNo(),flag);

        if (flag) {
            log.info(LogUtil.format(dto, "????????????", "?????????????????????"));
            this.addOptLogAsync(dto);
        }
        return flag ? R.ok() : R.failed(Strings.nullToEmpty(dto.getCurrencyName()) + "Insufficient account balance");
    }

    /**
     * ?????? ?????? ??????????????????????????? ???????????????
     *
     * @param dto
     */
    private void addOptLogAsync(CustPayDTO dto) {
        financeThreadTaskPool.execute(() -> {
            log.info(LogUtil.format(dto, "??????/????????????"));
            BillEnum.PayMethod payMethod = dto.getPayMethod();
            ChargeLog chargeLog = new ChargeLog();
            BeanUtils.copyProperties(dto, chargeLog);
            chargeLog
                    .setCustomCode(dto.getCusCode()).setPayMethod(payMethod.name())
                    .setOrderNo(dto.getNo()).setOperationPayMethod("????????????").setSuccess(true)
                    .setOperationType("").setCurrencyCode(dto.getCurrencyCode())
            ;

            chargeLog.setRemark("-----------------------------------------");
            log.info(LogUtil.format(chargeLog, "???????????????", payMethod.name()));
            if (null == chargeLog.getQty() || 0 >= chargeLog.getQty()) {
                //????????????????????????????????????????????????????????????
                if (StringUtils.isNotBlank(chargeLog.getOrderNo()) && chargeLog.getOrderNo().startsWith("CK")) {
                    R<DelOutboundVO> infoByOrderNo = delOutboundFeignService.getInfoByOrderNo(chargeLog.getOrderNo());
                    if (null != infoByOrderNo && null != infoByOrderNo.getData()) {
                        DelOutboundVO data = infoByOrderNo.getData();
                        //String trackingNo = data.getTrackingNo();
                        List<DelOutboundDetailVO> details = data.getDetails();
                        if (CollectionUtils.isNotEmpty(details)) {
                            Long qty = details.stream().map(DelOutboundDetailVO::getQty).reduce(Long::sum).orElse(0L);
                            chargeLog.setQty(qty);
                        }
                        chargeLog.setWarehouseCode(Optional.of(data).map(DelOutboundVO::getWarehouseCode).orElse(""));
                    }
                } else if (StringUtils.isNotBlank(chargeLog.getOrderNo()) && chargeLog.getOrderNo().startsWith("RK")) {
                    R<InboundReceiptInfoVO> infoByOrderNo = inboundReceiptFeignService.info(chargeLog.getOrderNo());
                    if (null != infoByOrderNo && null != infoByOrderNo.getData()) {
                        InboundReceiptInfoVO data = infoByOrderNo.getData();
                        //String trackingNo = data.getTrackingNo();
                        List<InboundReceiptDetailVO> details = data.getInboundReceiptDetails();
                        if (CollectionUtils.isNotEmpty(details)) {
                            int qty = 0;
                            if (payMethod == BillEnum.PayMethod.BALANCE_FREEZE || payMethod == BillEnum.PayMethod.BALANCE_THAW) {
                                qty = details.stream().map(InboundReceiptDetailVO::getDeclareQty).reduce(Integer::sum).orElse(0);
                            } else if (payMethod == BillEnum.PayMethod.BALANCE_DEDUCTIONS) {
                                qty = details.stream().map(InboundReceiptDetailVO::getPutQty).reduce(Integer::sum).orElse(0);
                            }
                            chargeLog.setQty((long) qty);
                        }
                        Optional<InboundReceiptInfoVO> resultDateOpt = Optional.of(data);
                        String warehouseNo = resultDateOpt.map(InboundReceiptInfoVO::getWarehouseNo).orElse("");
                        chargeLog.setWarehouseCode(warehouseNo);
                    }
                }
            }
            chargeFeignService.add(chargeLog);
        });
    }

    private BlockingQueue<CustPayDTO> blockingQueue = new LinkedBlockingDeque();



    @Transactional
    @Override
    public R freezeBalance(final CusFreezeBalanceDTO cfbDTO) {

        CustPayDTO dto = new CustPayDTO();
        BeanUtils.copyProperties(cfbDTO, dto);
        if (BigDecimal.ZERO.compareTo(dto.getAmount()) == 0){
            return R.ok();
        }
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("Customer code/currency cannot be blank and the amount must be greater than 0.01");
        }
        setCurrencyName(dto);
        dto.setPayType(BillEnum.PayType.FREEZE);
        dto.setPayMethod(BillEnum.PayMethod.BALANCE_FREEZE);

        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        log.info(LogUtil.format(cfbDTO, "????????????"));
        Boolean flag = abstractPayFactory.updateBalance(dto);
        if (Objects.isNull(flag)){
            return R.ok();
        }
        if (flag && "Freight".equals(dto.getOrderType()))
        // ?????? ?????? ??????????????????????????? ???????????????
        {
            log.info(LogUtil.format(cfbDTO, "????????????", "???????????????"));
            this.addOptLogAsync(dto);
        }
        return flag ? R.ok() : R.failed(Strings.nullToEmpty(dto.getCurrencyName()) + "The available balance of the account is insufficient for freezing");
    }

    @Transactional
    @Override
    public R thawBalance(CusFreezeBalanceDTO cfbDTO) {
        CustPayDTO dto = new CustPayDTO();
        BeanUtils.copyProperties(cfbDTO, dto);
        if (BigDecimal.ZERO.compareTo(dto.getAmount()) == 0){
            return R.ok();
        }

        log.info("?????????????????????{}",JSONObject.toJSONString(cfbDTO));

        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("Customer code/currency cannot be blank and the amount must be greater than 0.01");
        }
        dto.setPayType(BillEnum.PayType.FREEZE);
        dto.setPayMethod(BillEnum.PayMethod.BALANCE_THAW);
        setCurrencyName(dto);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        log.info(LogUtil.format(cfbDTO, "????????????"));

        log.info("??????????????????updateBalance???{}",JSONObject.toJSONString(abstractPayFactory));

        Boolean flag = abstractPayFactory.updateBalance(dto);
        if (Objects.isNull(flag)){
            return R.ok();
        }

        log.info("??????????????????updateBalance???{}",flag);

        if (flag)
        //?????? ?????? ??????????????????????????? ???????????????
        {
            log.info(LogUtil.format(cfbDTO, "????????????", "???????????????"));
            this.addOptLogAsync(dto);
        }
        return flag ? R.ok() : R.failed(Strings.nullToEmpty(dto.getNo()) + "The account frozen amount is not enough to unfreeze");
    }

    /**
     * ????????????????????????????????????
     *
     * @param cusCode      ????????????
     * @param currencyCode ??????
     * @return ????????????
     */
    @Override
    public BalanceDTO getBalance(String cusCode, String currencyCode) {
        log.info("????????????????????????{}-{}", cusCode, currencyCode);
        // ????????????????????????

//        Map<String, CreditUseInfo> creditUse = iDeductionRecordService.queryTimeCreditUse(cusCode, Arrays.asList(currencyCode), Arrays.asList(CreditConstant.CreditBillStatusEnum.DEFAULT, CreditConstant.CreditBillStatusEnum.CHECKED));
//        log.info("?????????????????????????????????{}", JSONObject.toJSONString(creditUse));
//        BigDecimal creditUseAmount =  Optional.ofNullable(creditUse.get(currencyCode)).map(CreditUseInfo::getCreditUseAmount).orElse(BigDecimal.ZERO);
//
//          List<CreditUseInfo> creditUseList = accountBalanceMapper.queryTimeCreditUse(cusCode,currencyCode);
//
//          Map<String,CreditUseInfo> creditUse = creditUseList.stream().collect(Collectors.toMap(CreditUseInfo::getCurrencyCode,v->v));
//
//          log.info("?????????????????????????????????{}", JSONObject.toJSONString(creditUse));
//          BigDecimal creditUseAmount =  Optional.ofNullable(creditUse.get(currencyCode)).map(CreditUseInfo::getCreditUseAmount).orElse(BigDecimal.ZERO);
//
//        CompletableFuture<BigDecimal> creditUseAmountFuture = CompletableFuture.supplyAsync(() -> {
//            Map<String, CreditUseInfo> creditUse = iDeductionRecordService.queryTimeCreditUse(cusCode, Arrays.asList(currencyCode), Arrays.asList(CreditConstant.CreditBillStatusEnum.DEFAULT, CreditConstant.CreditBillStatusEnum.CHECKED));
//            log.info("?????????????????????????????????{}", JSONObject.toJSONString(creditUse));
//            return Optional.ofNullable(creditUse.get(currencyCode)).map(CreditUseInfo::getCreditUseAmount).orElse(BigDecimal.ZERO);
//        }, financeThreadTaskPool);

        CompletableFuture<AccountBalance> accountBalanceCompletableFuture = CompletableFuture.supplyAsync(() -> {
            QueryWrapper<AccountBalance> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("cus_code", cusCode);
            queryWrapper.eq("currency_code", currencyCode);
            AccountBalance accountBalance = accountBalanceMapper.selectOne(queryWrapper);
            // ??????????????? ?????????????????????????????????????????????
            if (accountBalance == null) {
                log.info("getBalance() cusCode: {} currencyCode: {}", cusCode, currencyCode);
                String currencyName = getCurrencyName(currencyCode);
                accountBalance = new AccountBalance(cusCode, currencyCode, currencyName);
                //???????????????????????????????????????????????????????????????
                List<AccountBalance> accountBalances = accountBalanceMapper.selectList(Wrappers.<AccountBalance>lambdaQuery()
                        .eq(AccountBalance::getCreditType, CreditConstant.CreditTypeEnum.TIME_LIMIT.getValue())
                        .eq(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.ACTIVE.getValue())
                        .eq(AccountBalance::getCusCode, cusCode));
                if (CollectionUtils.isNotEmpty(accountBalances)) {
                    log.info("??????????????? {} ???????????????????????????{}", cusCode, JSON.toJSONString(accountBalances));
                    AccountBalance accountBalanceCredit = accountBalances.get(0);
                    BeanUtils.copyProperties(accountBalanceCredit, accountBalance);
                    accountBalance.setId(null);
                    accountBalance.setCurrencyCode(currencyCode).setCurrencyName(currencyName)
                            .setCreditUseAmount(BigDecimal.ZERO).setCreditType(CreditConstant.CreditTypeEnum.TIME_LIMIT.getValue().toString())
                            .setTotalBalance(BigDecimal.ZERO).setCurrentBalance(BigDecimal.ZERO).setFreezeBalance(BigDecimal.ZERO)
                            .setCreateTime(new Date());
                }
                // ????????????CreditType ????????????????????????????????????????????????
                if (StringUtils.isBlank(accountBalance.getCreditType())) {
                    accountBalance.setCreditType(CreditConstant.CreditTypeEnum.QUOTA.getValue().toString());
                }
                accountBalance.setVersion(0L);
                accountBalanceMapper.insert(accountBalance);
            }
            return accountBalance;
        }, financeThreadTaskPool);

//        QueryWrapper<AccountBalance> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("cus_code", cusCode);
//            queryWrapper.eq("currency_code", currencyCode);
//            AccountBalance accountBalance = accountBalanceMapper.selectOne(queryWrapper);
//            // ??????????????? ?????????????????????????????????????????????
//            if (accountBalance == null) {
//                log.info("getBalance() cusCode: {} currencyCode: {}", cusCode, currencyCode);
//                String currencyName = getCurrencyName(currencyCode);
//                accountBalance = new AccountBalance(cusCode, currencyCode, currencyName);
//                //???????????????????????????????????????????????????????????????
//                List<AccountBalance> accountBalances = accountBalanceMapper.selectList(Wrappers.<AccountBalance>lambdaQuery()
//                        .eq(AccountBalance::getCreditType, CreditConstant.CreditTypeEnum.TIME_LIMIT.getValue())
//                        .eq(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.ACTIVE.getValue())
//                        .eq(AccountBalance::getCusCode, cusCode));
//                if (CollectionUtils.isNotEmpty(accountBalances)) {
//                    log.info("??????????????? {} ???????????????????????????{}", cusCode, JSON.toJSONString(accountBalances));
//                    AccountBalance accountBalanceCredit = accountBalances.get(0);
//                    BeanUtils.copyProperties(accountBalanceCredit, accountBalance);
//                    accountBalance.setId(null);
//                    accountBalance.setCurrencyCode(currencyCode).setCurrencyName(currencyName)
//                            .setCreditUseAmount(BigDecimal.ZERO).setCreditType(CreditConstant.CreditTypeEnum.TIME_LIMIT.getValue().toString())
//                            .setTotalBalance(BigDecimal.ZERO).setCurrentBalance(BigDecimal.ZERO).setFreezeBalance(BigDecimal.ZERO)
//                            .setCreateTime(new Date());
//                }
//                // ????????????CreditType ????????????????????????????????????????????????
//                if (StringUtils.isBlank(accountBalance.getCreditType())) {
//                    accountBalance.setCreditType(CreditConstant.CreditTypeEnum.QUOTA.getValue().toString());
//                }
//                accountBalance.setVersion(0L);
//                accountBalanceMapper.insert(accountBalance);
//            }

        try {
            AccountBalance accountBalance = accountBalanceCompletableFuture.get();
            //creditUseAmount = creditUseAmountFuture.get();
            BalanceDTO balanceDTO = new BalanceDTO(accountBalance.getCurrentBalance(), accountBalance.getFreezeBalance(), accountBalance.getTotalBalance());
            CreditInfoBO creditInfoBO = balanceDTO.getCreditInfoBO();
            BeanUtils.copyProperties(accountBalance, creditInfoBO);
            balanceDTO.setCreditInfoBO(creditInfoBO);
            BigDecimal creditUseAmount = accountBalance.getCreditUseAmount();
            balanceDTO.getCreditInfoBO().setCreditUseAmount(creditUseAmount);
            balanceDTO.setVersion(accountBalance.getVersion());

            log.info("???????????????????????????????????????{}", creditUseAmount);

            return balanceDTO;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("???????????????????????????", e);
            throw new RuntimeException("????????????????????????: " + e.getMessage());
        }
    }

    @Override
    public void setBalance(String cusCode, String currencyCode, BalanceDTO result, boolean needUpdateCredit) {
        log.info("???????????????{}???{}???{}???{}", cusCode, currencyCode, JSONObject.toJSONString(result), needUpdateCredit);
        AccountBalanceUpdateDTO accountBalance = new AccountBalanceUpdateDTO();
        BigDecimal currentBalance = result.getCurrentBalance();

        if(currentBalance != null){
            BigDecimal currentBa = BigDecimalUtil.setScale(currentBalance,BigDecimalUtil.PRICE_SCALE);
            accountBalance.setCurrentBalance(currentBa);
        }
        BigDecimal freezebalance = result.getFreezeBalance();

        if(freezebalance != null){
            BigDecimal currentBa = BigDecimalUtil.setScale(freezebalance,BigDecimalUtil.PRICE_SCALE);
            accountBalance.setFreezeBalance(currentBa);
        }

        BigDecimal totalBalance = result.getTotalBalance();
        if(totalBalance != null){
            BigDecimal currentBa = BigDecimalUtil.setScale(totalBalance,BigDecimalUtil.PRICE_SCALE);
            accountBalance.setTotalBalance(currentBa);
        }

        accountBalance.setCusCode(cusCode);
        accountBalance.setCurrencyCode(currencyCode);

        accountBalance.setVersion(result.getVersion());
        if (needUpdateCredit && null != result.getCreditInfoBO()) {

            BigDecimal creditUseAmount = result.getCreditInfoBO().getCreditUseAmount();

            if(creditUseAmount != null){
                BigDecimal currentBa = BigDecimalUtil.setScale(creditUseAmount,BigDecimalUtil.PRICE_SCALE);
                accountBalance.setCreditUseAmount(currentBa);
            }

            accountBalance.setCreditStatus(result.getCreditInfoBO().getCreditStatus());
            accountBalance.setCreditBeginTime(result.getCreditInfoBO().getCreditBeginTime());
            accountBalance.setCreditEndTime(result.getCreditInfoBO().getCreditEndTime());
            accountBalance.setCreditBufferTime(result.getCreditInfoBO().getCreditBufferTime());
        }

        int updCount = accountBalanceMapper.setBalance(accountBalance);

        log.info("?????????????????????:{},?????????{}",updCount,result.getOrderNo());
    }

    @Override
    public boolean withDrawBalanceCheck(String cusCode, String currencyCode, BigDecimal amount) {
        BigDecimal currentBalance = getCurrentBalance(cusCode, currencyCode);
        return currentBalance.compareTo(amount) >= 0;
    }

    @Override
    public int updateAccountBalanceChange(AccountBalanceChangeDTO dto) {
        LambdaUpdateWrapper<AccountBalanceChange> update = Wrappers.lambdaUpdate();
        update.set(AccountBalanceChange::getHasFreeze, dto.getHasFreeze())
                .eq(AccountBalanceChange::getCusCode, dto.getCusCode())
                .eq(AccountBalanceChange::getNo, dto.getNo())
                .eq(AccountBalanceChange::getCurrencyCode, dto.getCurrencyCode())
                .eq(AccountBalanceChange::getPayMethod, dto.getPayMethod());
        if (StringUtils.isNotBlank(dto.getOrderType())) {
            update.eq(AccountBalanceChange::getOrderType, dto.getOrderType());
        }
        return accountBalanceChangeMapper.update(null, update);
    }

    /**
     * ????????????
     *
     * @param dto
     * @return
     */
    @Override
    public R onlineIncome(CustPayDTO dto) {
//        fillCustInfo(loginUser,dto);
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("Customer code/currency cannot be blank and the amount must be greater than 0.01");
        }
        setCurrencyName(dto);
        dto.setPayType(BillEnum.PayType.INCOME);
        dto.setPayMethod(BillEnum.PayMethod.ONLINE_INCOME);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        return flag ? R.ok() : R.failed();
    }

    /**
     * ??????
     *
     * @param dto
     * @return
     */
    @Override
    public R refund(CustPayDTO dto) {
//        fillCustInfo(loginUser,dto);
        /*if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("????????????/???????????????????????????????????????0.01");
        }*/
        final String key = "cky-fss-freeze-balance-all:" + dto.getCusCode()+ "_"+dto.getCurrencyCode();
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(time, leaseTime, TimeUnit.SECONDS)) {
                setCurrencyName(dto);
                dto.setPayType(BillEnum.PayType.REFUND);
                dto.setPayMethod(BillEnum.PayMethod.REFUND);
                AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
                Boolean flag = abstractPayFactory.updateBalance(dto);
                if (Objects.isNull(flag)) {
                    return R.ok();
                }

                return flag ? R.ok() : R.failed();
            }
        }catch (Exception e){
            log.error("?????????????????????{}",e);
            return R.failed(e.getMessage());
        }finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                log.info("??????????????????-??????redis??? {}",key);
                lock.unlock();
            }
        }

        return R.failed();
    }

    /**
     * ????????????
     *
     * @param dto
     * @return
     */
    @Override
    public R offlineIncome(CustPayDTO dto) {
//        fillCustInfo(loginUser,dto);
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("Customer code/currency cannot be blank and the amount must be greater than 0.01");
        }
        setCurrencyName(dto);
        dto.setPayType(BillEnum.PayType.INCOME);
        if(dto.getPayMethod() == null) {
            dto.setPayMethod(BillEnum.PayMethod.OFFLINE_INCOME);
        }
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        Boolean flag = abstractPayFactory.updateBalance(dto);
        if (Objects.isNull(flag)) return R.ok();
        return flag ? R.ok() : R.failed();
    }

    /**
     * ??????????????????
     *
     * @param dto
     * @return
     */
    @Override
    public R balanceExchange(CustPayDTO dto) {
        AssertUtil.notNull(dto.getRate(), "??????????????????");
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("Customer code/currency cannot be blank and the amount must be greater than 0.01");
        }
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode2(), dto.getAmount())) {
            return R.failed("Customer code/currency cannot be blank and the amount must be greater than 0.01");
        }
        dto.setPayType(BillEnum.PayType.EXCHANGE);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        Boolean flag = abstractPayFactory.updateBalance(dto);
        if (Objects.isNull(flag)) return R.ok();
        return flag ? R.ok() : R.failed(Strings.nullToEmpty(dto.getCurrencyName()) + "Insufficient account balance");
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param cusCode
     * @param currencyCode
     * @return
     */
    @Override
    public BigDecimal getCurrentBalance(String cusCode, String currencyCode) {
        QueryWrapper<AccountBalance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cus_code", cusCode);
        queryWrapper.eq("currency_code", currencyCode);
        AccountBalance accountBalance = accountBalanceMapper.selectOne(queryWrapper);
        if (accountBalance != null) {
            return accountBalance.getCurrentBalance();
        }
        accountBalance = new AccountBalance(cusCode, currencyCode, getCurrencyName(currencyCode));
        accountBalance.setVersion(0L);
        accountBalanceMapper.insert(accountBalance);
        return BigDecimal.ZERO;
    }

    /**
     * ??????????????????
     *
     * @param cusCode
     * @param currencyCode
     * @param result
     */
    @Override
    @Transactional
    public void setCurrentBalance(String cusCode, String currencyCode, BigDecimal result) {
        LambdaUpdateWrapper<AccountBalance> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.eq(AccountBalance::getCusCode, cusCode);
        lambdaUpdateWrapper.eq(AccountBalance::getCurrencyCode, currencyCode);
        lambdaUpdateWrapper.set(AccountBalance::getCurrentBalance, result);
        lambdaUpdateWrapper.set(AccountBalance::getCurrentBalance, result);
        accountBalanceMapper.update(null, lambdaUpdateWrapper);
    }

    /**
     * ??????
     *
     * @param dto
     * @return
     */
    @Override
    public R withdraw(CustPayDTO dto) {
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("Customer code/currency cannot be blank and the amount must be greater than 0.01");
        }
//        fillCustInfo(loginUser,dto);
        dto.setPayType(BillEnum.PayType.PAYMENT_NO_FREEZE);
        dto.setPayMethod(BillEnum.PayMethod.WITHDRAW_PAYMENT);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        return flag ? R.ok() : R.failed(Strings.nullToEmpty(dto.getCurrencyName()) + "Insufficient account balance");
    }

    private String getCurrencyName(String currencyCode) {
        return sysDictDataService.getCurrencyNameByCode(currencyCode);
    }

    private void setCurrencyName(CustPayDTO dto) {
        if (StringUtils.isEmpty(dto.getCurrencyName())) {
            dto.setCurrencyName(getCurrencyName(dto.getCurrencyCode()));
        }
    }

    /**
     * ???????????????????????????
     *
     * @param rechargesRequestDTO
     * @param dto
     */
    private void fillRechargesRequestDTO(RechargesRequestDTO rechargesRequestDTO, CustPayDTO dto) {
        rechargesRequestDTO.setSerialNo(SnowflakeId.getNextId12());
        rechargesRequestDTO.setBankCode(dto.getBankCode());
        rechargesRequestDTO.setRemark(dto.getRemark());
        rechargesRequestDTO.setMethod(dto.getMethod());
        //set amount
        RechargesRequestAmountDTO amount = new RechargesRequestAmountDTO();
        amount.setAmount(dto.getAmount());
        amount.setCurrencyCode(dto.getCurrencyCode());
        rechargesRequestDTO.setAmount(amount);
    }

    public boolean checkPayInfo(String cusCode, String currencyCode, BigDecimal amount) {
        boolean b1 = StringUtils.isEmpty(cusCode);
        boolean b2 = StringUtils.isEmpty(currencyCode);
        boolean b3 = amount == null;
        return b1 || b2 || b3 || amount.setScale(2, BigDecimal.ROUND_FLOOR).compareTo(BigDecimal.ZERO) < 1;
    }

    @Override
    public void updateCreditStatus(CustPayDTO dto) {
        int update = accountBalanceMapper.update(new AccountBalance(), Wrappers.<AccountBalance>lambdaUpdate()
                .eq(AccountBalance::getCurrencyCode, dto.getCurrencyCode())
                .eq(AccountBalance::getCusCode, dto.getCusCode())
                .eq(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.ACTIVE.getValue())
                .set(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.ARREARAGE_DEACTIVATION.getValue())
        );
        AssertUtil.isTrue(update <= 1, "??????????????????????????????");
        log.info("??????{}????????????????????? {}", update, JSONObject.toJSONString(dto));
    }

    private boolean checkAmountIsZero(BigDecimal bigDecimal) {
        return bigDecimal != null && bigDecimal.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * ????????????(A+B)???
     * A ??????????????????????????????????????????????????????????????? ????????????A????????????
     * B?????????????????????
     *
     * @param userCreditDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserCredit(UserCreditDTO userCreditDTO) {
        log.info("?????????????????????????????? {}", userCreditDTO);
        String cusCode = userCreditDTO.getCusCode();
        List<UserCreditDetailDTO> userCreditDetailList = userCreditDTO.getUserCreditDetailList();
        List<String> updateCurrencyCodeList = userCreditDetailList.stream().map(UserCreditDetailDTO::getCurrencyCode).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        CreditConstant.CreditTypeEnum newCreditTypeEnum = userCreditDetailList.stream().map(UserCreditDetailDTO::getCreditType).filter(Objects::nonNull).findAny().orElse(CreditConstant.CreditTypeEnum.DEFAULT);
        List<AccountBalance> accountBalancesOld = accountBalanceMapper.selectList(Wrappers.<AccountBalance>lambdaUpdate().eq(AccountBalance::getCusCode, cusCode));

        Integer creditTimeInterval = userCreditDTO.getUserCreditDetailList().stream().map(UserCreditDetailDTO::getCreditTimeInterval).filter(Objects::nonNull).findAny().orElse(0);
        Integer creditBufferTimeInterval = userCreditDTO.getUserCreditDetailList().stream().map(UserCreditDetailDTO::getCreditBufferTimeInterval).filter(Objects::nonNull).findAny().orElse(0);
        ChronoUnit creditTimeUnit = userCreditDetailList.stream().map(UserCreditDetailDTO::getCreditTimeUnit).findAny().orElse(ChronoUnit.DAYS);
        ChronoUnit creditBufferTimeUnit = userCreditDetailList.stream().map(UserCreditDetailDTO::getCreditBufferTimeUnit).findAny().orElse(ChronoUnit.DAYS);


        if (CollectionUtils.isEmpty(userCreditDetailList)) {
            // ????????????????????????
            LambdaUpdateWrapper<AccountBalance> accountOldWrapper = Wrappers.<AccountBalance>lambdaUpdate()
                    .eq(AccountBalance::getCusCode, cusCode)
                    .notIn(CollectionUtils.isNotEmpty(updateCurrencyCodeList), AccountBalance::getCurrencyCode, updateCurrencyCodeList).or().nested(x -> x.eq(AccountBalance::getCusCode, cusCode).ne(AccountBalance::getCreditType, newCreditTypeEnum));
            List<AccountBalance> accountBalances = accountBalanceMapper.selectList(accountOldWrapper);

            Map<String, BigDecimal> needToRepayMap = accountBalances.stream().filter(x -> checkAmountIsZero(x.getCreditUseAmount())).collect(Collectors.toMap(AccountBalance::getCurrencyCode, x -> Optional.ofNullable(x.getCreditUseAmount()).orElse(BigDecimal.ZERO)));
            StringBuilder errorMsg = new StringBuilder();
            needToRepayMap.forEach((x, y) -> {
                if (!checkAmountIsZero(y)) {
                    errorMsg.append(String.format("%s ???%s\n", x, y));
                }
            });
            AssertUtil.isTrue(StringUtils.isBlank(errorMsg.toString()), "????????????????????????????????????\n" + errorMsg);
        }

        if (CollectionUtils.isNotEmpty(userCreditDetailList)) {
            if (newCreditTypeEnum == CreditConstant.CreditTypeEnum.TIME_LIMIT) {
                //????????????????????? ????????????????????????????????????
                Integer currencyListCount = accountBalanceMapper.selectCount(Wrappers.<AccountBalance>lambdaUpdate()
                        .eq(AccountBalance::getCusCode, cusCode));
                if (currencyListCount == 0) {
                    UserCreditDetailDTO userCreditDetailDTO = new UserCreditDetailDTO();
                    BeanUtils.copyProperties(userCreditDTO, userCreditDetailDTO);
                    userCreditDetailDTO.setCurrencyName("?????????");
                    userCreditDetailDTO.setCurrencyCode("CNY");
                    insertNewCreditAccount(userCreditDTO.getCusCode(), Collections.singletonList(userCreditDetailDTO));
                }
            }
        } else {
            // ?????? / ?????? ????????????????????????
            int update = accountBalanceMapper.update(new AccountBalance(),
                    Wrappers.<AccountBalance>lambdaUpdate()
                            .eq(AccountBalance::getCusCode, cusCode)
                            .set(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.DISABLED.getValue())
            );
            log.info("????????????????????????{}- {}???", userCreditDTO, update);
        }


        CreditConstant.CreditTypeEnum creditTypeEnum = accountBalancesOld.stream()
                .filter(x -> null != x.getCreditType())
                .findAny()
                .map(AccountBalance::getCreditType)
                .map(CreditConstant.CreditTypeEnum::getThisByTypeCode)
                .orElse(CreditConstant.CreditTypeEnum.DEFAULT);
        LocalDate now = LocalDate.now();
        LocalDateTime newStart = now.atTime(0, 0, 0);
        LocalDateTime newEnd = newStart.plus(creditTimeInterval, creditTimeUnit).minusSeconds(1);
        LocalDateTime newBufferEnd = newEnd.plus(creditBufferTimeInterval, creditBufferTimeUnit);
        Map<String, AccountBalance> oldAccountInfo = accountBalancesOld.stream().collect(Collectors.toMap(AccountBalance::getCurrencyCode, x -> x));

        switch (creditTypeEnum) {
            case QUOTA:
                switch (newCreditTypeEnum) {
                    case QUOTA:
                        List<String> oldCodeList = new ArrayList<>(oldAccountInfo.keySet());
                        List<UserCreditDetailDTO> updateList = userCreditDetailList.stream().filter(x -> oldCodeList.contains(x.getCurrencyCode())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(updateList)) {
                            log.info("?????????????????????,?????????{} , {}", cusCode, JSONObject.toJSONString(updateList));
                            this.updateCreditBatch(updateList, cusCode);
                        }
                        List<UserCreditDetailDTO> insertList = userCreditDetailList.stream().filter(x -> !oldCodeList.contains(x.getCurrencyCode())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(insertList)) {
                            insertNewCreditAccount(cusCode, insertList);
                        }
                        return;
                    case TIME_LIMIT:
                        // ?????????????????????????????????????????????
                        List<UserCreditDetailDTO> updateCreditList = accountBalancesOld.stream().map(x -> {
                            UserCreditDetailDTO userCreditDetailDTO = new UserCreditDetailDTO();
                            userCreditDetailDTO.setCurrencyCode(x.getCurrencyCode()).setCreditType(CreditConstant.CreditTypeEnum.TIME_LIMIT);
                            userCreditDetailDTO
                                    .setCreditTimeInterval(creditTimeInterval).setCreditTimeUnit(creditTimeUnit).setCreditBeginTime(newStart).setCreditEndTime(newEnd)
                                    .setCreditBufferTimeInterval(creditBufferTimeInterval).setCreditBufferTime(newBufferEnd).setCreditBufferTimeUnit(creditBufferTimeUnit);
                            return userCreditDetailDTO;
                        }).collect(Collectors.toList());
                        this.updateCreditBatch(updateCreditList, cusCode);
                    case DEFAULT:
                    default:
                        return;
                }
            case TIME_LIMIT:
                // ??????????????????A?????? ????????? ???????????? A?????? ??????????????? ????????????????????? ??????????????????
                switch (newCreditTypeEnum) {
                    case QUOTA:
                        // ??????????????????????????? ??????????????????????????????????????????????????????0 ????????????????????????
                        accountBalanceMapper.update(new AccountBalance(), Wrappers.<AccountBalance>lambdaUpdate()
                                        .eq(AccountBalance::getCusCode,cusCode)
                                .set(AccountBalance::getCreditTimeFlag, true)
                                .set(AccountBalance::getCreditType, CreditConstant.CreditTypeEnum.QUOTA.getValue()));
                        this.updateCreditBatch(userCreditDetailList, cusCode);
                        return;
                    case TIME_LIMIT:
                        // ????????????????????????
                        List<UserCreditDetailDTO> updateCreditList = accountBalancesOld.stream().map(x -> {
                            UserCreditDetailDTO userCreditDetailDTO = new UserCreditDetailDTO();
                            userCreditDetailDTO
                                    .setCurrencyCode(x.getCurrencyCode()).setCreditType(CreditConstant.CreditTypeEnum.TIME_LIMIT)
                                    .setCreditTimeInterval(creditTimeInterval).setCreditBufferTimeInterval(creditBufferTimeInterval)
                                    .setCreditTimeUnit(creditTimeUnit).setCreditBufferTimeUnit(creditBufferTimeUnit);
                            // ??????????????????A?????? ????????? ???????????? A?????? ??????????????? ????????????????????? ??????????????????
                            Integer oldCreditTimeInterval = Optional.ofNullable(x.getCreditTimeInterval()).orElse(0);
                            // A?????? ????????????????????? ?????????????????? ?????????
                            if (creditTimeInterval.compareTo(oldCreditTimeInterval) >= 0) {
                                LocalDateTime creditBeginTime = x.getCreditBeginTime();
                                LocalDateTime creditEndTime = creditBeginTime.plus(creditTimeInterval, creditTimeUnit).minusSeconds(1);
                                LocalDateTime creditBufferTime = creditEndTime.plus(creditBufferTimeInterval, creditBufferTimeUnit);
                                userCreditDetailDTO.setCreditBeginTime(creditBeginTime).setCreditEndTime(creditEndTime).setCreditBufferTime(creditBufferTime);
                            } else {
                                // A?????? ?????????????????? ?????????????????????????????? ????????????
                                userCreditDetailDTO.setCreditBeginTime(newStart).setCreditEndTime(newEnd).setCreditBufferTime(newBufferEnd).setCreditTimeFlag(true);
                            }
                            return userCreditDetailDTO;
                        }).collect(Collectors.toList());
                        this.updateCreditBatch(updateCreditList, cusCode);
                        return;
                    case DEFAULT:
                    default:
                        return;
                }
            case DEFAULT:
                // ??????
                switch (newCreditTypeEnum) {
                    case QUOTA:
                        // ???????????????????????????
                        List<String> oldCodeList = new ArrayList<>(oldAccountInfo.keySet());
                        List<UserCreditDetailDTO> updateList = userCreditDetailList.stream().filter(x -> oldCodeList.contains(x.getCurrencyCode())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(updateList)) {
                            log.info("?????????????????????,?????????{} , {}", cusCode, JSONObject.toJSONString(updateList));
                            this.updateCreditBatch(updateList, cusCode);
                        } else {
                            insertNewCreditAccount(cusCode, userCreditDetailList);
                        }
                        return;
                    case TIME_LIMIT:
                        List<UserCreditDetailDTO> updateCreditList = accountBalancesOld.stream().map(x -> {
                            UserCreditDetailDTO userCreditDetailDTO = new UserCreditDetailDTO();
                            userCreditDetailDTO.setCurrencyCode(x.getCurrencyCode()).setCreditType(CreditConstant.CreditTypeEnum.TIME_LIMIT);
                            userCreditDetailDTO
                                    .setCreditTimeInterval(creditTimeInterval).setCreditTimeUnit(creditTimeUnit).setCreditBeginTime(newStart).setCreditEndTime(newEnd)
                                    .setCreditBufferTimeInterval(creditBufferTimeInterval).setCreditBufferTime(newBufferEnd).setCreditBufferTimeUnit(creditBufferTimeUnit);
                            return userCreditDetailDTO;
                        }).collect(Collectors.toList());
                        // ????????????????????????
                        this.updateCreditBatch(updateCreditList, cusCode);
                        return;
                    case DEFAULT:
                    default:
                        return;
                }
            default:
                return;
        }

    }

    private void insertNewCreditAccount(String cusCode, List<UserCreditDetailDTO> insertList) {
        List<AccountBalance> insertAccountList = insertList.stream().map(x -> {
            AccountBalance accountBalance = new AccountBalance();
            BeanUtils.copyProperties(x, accountBalance);
            accountBalance.setCreditStatus(CreditConstant.CreditStatusEnum.ACTIVE.getValue()).setCreditType(CreditConstant.CreditTypeEnum.QUOTA.getValue() + "");
            accountBalance.setCurrentBalance(BigDecimal.ZERO).setFreezeBalance(BigDecimal.ZERO).setTotalBalance(BigDecimal.ZERO);
            accountBalance.setCusCode(cusCode);
            return accountBalance;
        }).collect(Collectors.toList());
        log.info("?????????????????????,?????????{} , {}", cusCode, JSONObject.toJSONString(insertAccountList));
        insertAccountList.forEach(accountBalanceMapper::insert);
    }

    private void updateCreditBatch(List<UserCreditDetailDTO> updateList, String cusCode) {
        List<String> currencyCode = updateList.stream().map(UserCreditDetailDTO::getCurrencyCode).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        int i = accountBalanceMapper.updateCreditBatch(updateList, cusCode, currencyCode);
    }

    @Override
    public List<UserCreditInfoVO> queryUserCredit(String cusCode) {
        List<AccountBalance> accountBalances = accountBalanceMapper.selectList(Wrappers.<AccountBalance>lambdaQuery()
                .eq(AccountBalance::getCusCode, cusCode)
                .eq(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.ACTIVE.getValue())
                .isNotNull(AccountBalance::getCreditType)
        );
        List<UserCreditInfoVO> collect = accountBalances.stream().map(x -> {
            UserCreditInfoVO userCreditInfoVO = new UserCreditInfoVO();
            BeanUtils.copyProperties(x, userCreditInfoVO);
            userCreditInfoVO.setCreditType(CreditConstant.CreditTypeEnum.getThisByTypeCode(x.getCreditType()).name());
            return userCreditInfoVO;
        }).collect(Collectors.toList());
        boolean present = collect.stream().anyMatch(x -> null != x.getCreditType() && (CreditConstant.CreditTypeEnum.TIME_LIMIT.name() + "").equals(x.getCreditType()));
        if (CollectionUtils.isNotEmpty(collect)) {
            if (present) {
                UserCreditInfoVO userCreditInfoVO = collect.get(0);
                userCreditInfoVO.setCreditLine(null);
                userCreditInfoVO.setCurrencyCode(null);
                userCreditInfoVO.setCurrencyName(null);
                collect = Collections.singletonList(userCreditInfoVO);
            } else {
                collect.forEach(x -> x.setCreditTimeInterval(null));
            }
        }
        return collect;
    }

    @Override
    public List<AccountBalance> queryAndUpdateUserCreditTimeFlag() {
        List<AccountBalance> accountBalanceList = accountBalanceMapper.selectList(Wrappers.<AccountBalance>lambdaQuery()
                .eq(AccountBalance::getCreditTimeFlag, true)
                .groupBy(AccountBalance::getCusCode).select(AccountBalance::getCusCode));
        int update = accountBalanceMapper.update(new AccountBalance(), Wrappers.<AccountBalance>lambdaUpdate()
                .set(AccountBalance::getCreditTimeFlag, false)
                .eq(AccountBalance::getCreditTimeFlag, true));
        return accountBalanceList;
    }

    @Override
    public List<AccountBalance> queryThePreTermBill() {
        return accountBalanceMapper.queryThePreTermBill();
    }

    @Override
    public int reloadCreditTime(List<String> cusCodeList, String currencyCode) {
        log.info("reloadCreditTiem {} -{}", cusCodeList, currencyCode);
        LocalDate now = LocalDate.now();
        int update = accountBalanceMapper.update(new AccountBalance(),
                Wrappers.<AccountBalance>lambdaUpdate().in(AccountBalance::getCusCode, cusCodeList)
                        .eq(AccountBalance::getCurrencyCode, currencyCode)
                        .set(AccountBalance::getCreditBeginTime, now)

                        .setSql("credit_begin_time = DATE_FORMAT( NOW(), '%Y-%m-%d 00:00:00' ) ")
                        .setSql("credit_end_time = DATE_ADD( DATE_FORMAT( NOW(), '%Y-%m-%d 23:59:59' ), INTERVAL credit_time_interval - 1 DAY ) ")
                        .setSql("credit_buffer_time = DATE_ADD( DATE_FORMAT( NOW(), '%Y-%m-%d 23:59:59' ), INTERVAL credit_time_interval + credit_buffer_time_interval - 1 DAY ) ")
        );
        log.info("reloadCreditTiem {}???", update);
        return update;
    }

    @Override
    public List<AccountBalance> queryTheCanUpdateCreditUserList() {
        return accountBalanceMapper.queryTheCanUpdateCreditUserList(LocalDate.now().atTime(0, 0, 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserCreditTime() {
        // ??????????????????????????????????????????????????????????????????????????????????????????
        List<AccountBalance> accountBalanceList = this.queryTheCanUpdateCreditUserList();
        log.info("???????????????????????????-{}", accountBalanceList.size());
        Map<String, List<String>> collect = accountBalanceList.stream().collect(Collectors.groupingBy(AccountBalance::getCurrencyCode, Collectors.mapping(AccountBalance::getCusCode, Collectors.toList())));
        collect.forEach((currency, cusCodeList) -> {
            this.reloadCreditTime(cusCodeList, currency);
        });

    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountBalance> selectByDaily() {
        Date date = new Date();
        LambdaQueryWrapper<AccountBalance> queryWrapper = Wrappers.lambdaQuery();
        return accountBalanceMapper.listPage(queryWrapper);
    }
}
