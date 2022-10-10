package com.szmsd.finance.handler;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.finance.dto.AccountBalanceBillResultDTO;
import com.szmsd.finance.mapper.AccountBalanceLogMapper;
import com.szmsd.finance.mapper.AccountSerialBillMapper;
import com.szmsd.finance.vo.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


public class BillSelectBalanceTask implements Serializable {

    private AccountSerialBillMapper accountSerialBillMapper;

    private AccountBalanceLogMapper accountBalanceLogMapper;

    private BasFeignService basFeignService;

    public BillSelectBalanceTask (AccountSerialBillMapper accountSerialBillMapper,AccountBalanceLogMapper accountBalanceLogMapper,BasFeignService basFeignService){
        this.accountSerialBillMapper = accountSerialBillMapper;
        this.accountBalanceLogMapper = accountBalanceLogMapper;
        this.basFeignService = basFeignService;
    }

    public List<BillBalanceVO> find(EleBillQueryVO queryVO){

        if(queryVO == null){
            throw new RuntimeException("参数异常");
        }

        String billStartTime = queryVO.getBillStartTime();

        if(StringUtils.isBlank(billStartTime) || StringUtils.isBlank(queryVO.getBillEndTime())){
            throw new RuntimeException("时间范围不允许为空");
        }

        //step 1.根据客户、币种分组统计出本期收入、本期支出数据
        List<AccountBalanceBillResultDTO> accountBalanceBillResults = accountSerialBillMapper.findAccountBillResultData(queryVO);

        if(CollectionUtils.isEmpty(accountBalanceBillResults)){
            return new ArrayList<>();
        }

        //获取基础币种
        List<BasCurrencyVO> basCurrencyVOS = this.findBasCurrency();
        if(CollectionUtils.isEmpty(basCurrencyVOS)){
            throw new RuntimeException("无法获取基础币种信息");
        }

        Date parseStartTime = DateUtils.parseDate(billStartTime);

        if(parseStartTime == null){
            throw new RuntimeException("开始时间日期格式异常");
        }

        Date startTime = DateUtils.getPastDate(parseStartTime,1);


        //查询客户上期余额
        List<String> cusCodeList = accountBalanceBillResults.stream().map(AccountBalanceBillResultDTO::getCusCode).collect(Collectors.toList());
        List<AccountBalanceBillResultDTO> cusBalancePeriod = accountBalanceLogMapper.cusPeriodAmount(cusCodeList,startTime);

        //客户上期余额
        Map<String,List<AccountBalanceBillResultDTO>> cusPeriodMap = cusBalancePeriod.stream().collect(Collectors.groupingBy(AccountBalanceBillResultDTO::getCusCode));

        Map<String,List<AccountBalanceBillResultDTO>> billResultMap = accountBalanceBillResults.stream().collect(Collectors.groupingBy(AccountBalanceBillResultDTO::getCusCode));

        Set<Map.Entry<String, List<AccountBalanceBillResultDTO>>> entries = billResultMap.entrySet();
        Iterator<Map.Entry<String, List<AccountBalanceBillResultDTO>>> iterator = entries.iterator();

        List<BillBalanceVO> billBalanceVOS = new ArrayList<>();

        while (iterator.hasNext()){

            BillBalanceVO billBalanceVO = new BillBalanceVO();
            //billBalanceVO.setBasCurrencys(basCurrencyVOS);

            Map.Entry<String, List<AccountBalanceBillResultDTO>> entry = iterator.next();
            String cusCode = entry.getKey();
            List<AccountBalanceBillResultDTO> resultDTOList = entry.getValue();

            List<AccountBalanceBillResultDTO> cusPeriodData = cusPeriodMap.get(cusCode);

            //step 2.根据币种分组
            List<BillChargeCategoryVO> chargeCategorys = this.generatorChargeCategory(cusCode,resultDTOList,basCurrencyVOS,cusPeriodData);

            billBalanceVO.setBillStartTime(queryVO.getBillStartTime());
            billBalanceVO.setBillEndTime(queryVO.getBillEndTime());

            billBalanceVO.setCusCode(cusCode);
            billBalanceVO.setChargeCategorys(chargeCategorys);

            billBalanceVOS.add(billBalanceVO);
        }

        for(BillBalanceVO billBalanceVO : billBalanceVOS){
            billBalanceVO.setBasCurrencys(basCurrencyVOS);
        }

        return billBalanceVOS;
    }

    private List<BillChargeCategoryVO> generatorChargeCategory(String cusCode,
                                                               List<AccountBalanceBillResultDTO> resultDTOList,
                                                               List<BasCurrencyVO> basCurrencyVOS,
                                                               List<AccountBalanceBillResultDTO> cusBalancePeriod) {

        List<BillChargeCategoryVO> billChargeCategoryVOS = new ArrayList<>();
        List<String> chargeCategoryNames = Arrays.asList("上期余额","本期收入","本期支出","本期余额","本期需支付");
        Map<String,BasCurrencyVO> basCurrencyVOMap = basCurrencyVOS.stream().collect(Collectors.toMap(BasCurrencyVO::getCurrencyCode,v->v));

        //币种
        Map<String,AccountBalanceBillResultDTO> chargeCategoryMap = resultDTOList.stream().collect(Collectors.toMap(AccountBalanceBillResultDTO::getCurrencyCode,v->v));

        //客户币种上期余额
        Map<String,AccountBalanceBillResultDTO> cusPeriodMap = null;

        if(CollectionUtils.isNotEmpty(cusBalancePeriod)) {
            cusPeriodMap = cusBalancePeriod.stream().collect(Collectors.toMap(AccountBalanceBillResultDTO::getCurrencyCode, v -> v));
        }

        for(String chargeCategory : chargeCategoryNames) {

            List<BillCurrencyAmountVO> billCurrencyAmounts = new ArrayList<>();

            for (AccountBalanceBillResultDTO entry : resultDTOList) {

                String currencyCode = entry.getCurrencyCode().trim();
                BasCurrencyVO basCurrencyVO = basCurrencyVOMap.get(currencyCode);

                if(basCurrencyVO == null){
                    throw new RuntimeException("无法获取币种符号异常,"+currencyCode);
                }

                AccountBalanceBillResultDTO resultDTO = chargeCategoryMap.get(currencyCode);

                if(resultDTO == null){
                    resultDTO = new AccountBalanceBillResultDTO();
                }

                //本期支出转负数
                BigDecimal currentExpenditureAmount = resultDTO.getCurrentExpenditureAmount();
                if(currentExpenditureAmount != null && currentExpenditureAmount.compareTo(BigDecimal.ZERO) > 0){
                    resultDTO.setCurrentExpenditureAmount(currentExpenditureAmount.negate());
                }

                //上期余额
                if(CollectionUtils.isNotEmpty(cusPeriodMap)){
                    AccountBalanceBillResultDTO periodData =  cusPeriodMap.get(currencyCode);
                    if(periodData != null) {
                        resultDTO.setPeriodAmount(periodData.getPeriodAmount());
                    }
                }

                //本期余额 = 上期余额+本期收入+本期支出
                BigDecimal currentAmount = BigDecimal.ZERO;
                if(resultDTO.getPeriodAmount() != null){
                    currentAmount = currentAmount.add(resultDTO.getPeriodAmount());
                }
                if(resultDTO.getCurrentIncomeAmount() != null){
                    currentAmount = currentAmount.add(resultDTO.getCurrentIncomeAmount());
                }
                if(resultDTO.getCurrentExpenditureAmount() != null){
                    currentAmount =  currentAmount.add(resultDTO.getCurrentExpenditureAmount());
                }
                resultDTO.setCurrentAmount(currentAmount);

                //本期需支付
                if(currentAmount.compareTo(BigDecimal.ZERO) == -1){
                    resultDTO.setCurrentNeedPay(currentAmount);
                }else{
                    resultDTO.setCurrentNeedPay(BigDecimal.ZERO);
                }

                BillCurrencyAmountVO billCurrencyAmountVO = new BillCurrencyAmountVO();
                billCurrencyAmountVO.setCurrencyCode(currencyCode);
                billCurrencyAmountVO.setCurrencyName(basCurrencyVO.getCurrencyName());

                if(chargeCategory.equals("上期余额")){
                    billCurrencyAmountVO.setAmount(resultDTO.getPeriodAmount());
                }else if(chargeCategory.equals("本期收入")){
                    billCurrencyAmountVO.setAmount(resultDTO.getCurrentIncomeAmount());
                }else if(chargeCategory.equals("本期支出")){
                    billCurrencyAmountVO.setAmount(resultDTO.getCurrentExpenditureAmount());
                }else if(chargeCategory.equals("本期余额")){
                    billCurrencyAmountVO.setAmount(resultDTO.getCurrentAmount());
                }else if(chargeCategory.equals("本期需支付")){
                    billCurrencyAmountVO.setAmount(resultDTO.getCurrentNeedPay());
                }

                billCurrencyAmounts.add(billCurrencyAmountVO);
            }

            BillChargeCategoryVO billChargeCategoryVO = new BillChargeCategoryVO();
            billChargeCategoryVO.setChargeCategory(chargeCategory);
            billChargeCategoryVO.setCusCode(cusCode);

            billChargeCategoryVO.setBillCurrencyAmounts(billCurrencyAmounts);
            billChargeCategoryVOS.add(billChargeCategoryVO);
        }

        return billChargeCategoryVOS;
    }

    private List<BasCurrencyVO> findBasCurrency(){

        List<BasCurrencyVO> basCurrencyVOS = new ArrayList<>();

        R rs = basFeignService.list("008","币别");

        if(rs != null && rs.getCode() == 200){

            LinkedHashMap jsonObject = (LinkedHashMap) rs.getData();

            if(jsonObject == null){
                return new ArrayList<>();
            }

            List<Map<String,String>> jsonArray = (List<Map<String,String>>)jsonObject.get("币别");

            for(int i = 0;i<jsonArray.size();i++){

                Map<String,String> object = jsonArray.get(i);
                BasCurrencyVO basCurrencyVO = new BasCurrencyVO();
                if(StringUtils.isNotBlank(object.get("subValue"))) {
                    basCurrencyVO.setCurrencyCode(object.get("subValue").trim());
                    basCurrencyVO.setCurrencyName(object.get("subName").trim());
                }

                basCurrencyVOS.add(basCurrencyVO);
            }
        }

        return basCurrencyVOS;
    }

}
