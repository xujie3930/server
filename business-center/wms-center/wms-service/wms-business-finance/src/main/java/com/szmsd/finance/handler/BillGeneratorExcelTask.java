package com.szmsd.finance.handler;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.mapper.AccountBalanceLogMapper;
import com.szmsd.finance.mapper.AccountSerialBillMapper;
import com.szmsd.finance.util.ExcelUtil;
import com.szmsd.finance.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BillGeneratorExcelTask implements Callable<AccountBillRecordTaskResultVO> {

    private BillGeneratorBO billGeneratorBO;

    private AccountSerialBillMapper accountSerialBillMapper;

    private AccountBalanceLogMapper accountBalanceLogMapper;

    private BasFeignService basFeignService;

    public BillGeneratorExcelTask(){

    }

    public BillGeneratorExcelTask(BillGeneratorBO billGeneratorBO){
        this.billGeneratorBO = billGeneratorBO;
    }

    @Override
    public AccountBillRecordTaskResultVO call() throws Exception {

        Long current = System.currentTimeMillis();

        BillGeneratorRequestVO billRequestVO = billGeneratorBO.getBillRequestVO();
        BasSellerInfoVO basSellerInfoVO = billGeneratorBO.getBasSellerInfoVO();
        accountSerialBillMapper = billGeneratorBO.getAccountSerialBillMapper();
        String filePath = billGeneratorBO.getFilePath();
        accountBalanceLogMapper = billGeneratorBO.getAccountBalanceLogMapper();
        basFeignService = billGeneratorBO.getBasFeignService();


        String fileName = "dm-oms-template-"+current+".xlsx";
        String modelPath = "classpath:template/dm-oms-template.xlsx";
        InputStream inputStream = null;

        try {
            inputStream = ResourceUtils.getURL(modelPath).openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ConcurrentHashMap<Integer, List<?>> titleDataMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, List<?>> sheetAndDataMap = new ConcurrentHashMap<>();

        ConcurrentHashMap<Integer, List<?>> otherAndDataMap = new ConcurrentHashMap<>();

        //sheet 0.客戶基本信息、资金账结余、业务账汇总
        List<BasSellerExcelInfoVO> cusTitleMap = this.generatorTitle(billRequestVO,basSellerInfoVO);

        EleBillQueryVO queryVO = new EleBillQueryVO();
        queryVO.setCusCode(billRequestVO.getCusCode());
        queryVO.setBillStartTime(billRequestVO.getBillStartTime());
        queryVO.setBillEndTime(billRequestVO.getBillEndTime());

        BillSelectBalanceTask billSelectBalanceTask = new BillSelectBalanceTask(accountSerialBillMapper,accountBalanceLogMapper,basFeignService);

        //资金账结余
        List<BillBalanceVO> billBalanceVOS = billSelectBalanceTask.find(queryVO);
        //封装导出表格数据
        List<BillBalanceExcelResultVO> billBalanceExcelResultVOS = this.generatorBillExcelResult(billBalanceVOS);

        //业务账汇总
        List<BillBusinessTotalVO> businessTotalVOS = this.selectBusinessTotal(queryVO);
        sheetAndDataMap.put(0,billBalanceExcelResultVOS);
        titleDataMap.put(0,cusTitleMap);
        otherAndDataMap.put(0,businessTotalVOS);

        //sheet 1.国内直发统计
        EleBillQueryVO directDeliveryQueryVO = new EleBillQueryVO();
        directDeliveryQueryVO.setCusCode(billRequestVO.getCusCode());
        directDeliveryQueryVO.setBillStartTime(billRequestVO.getBillStartTime());
        directDeliveryQueryVO.setBillEndTime(billRequestVO.getBillEndTime());
        List<String> orderTypeList = new ArrayList<>();
        orderTypeList.add("PackageTransfer");
        List<BillDirectDeliveryTotalVO> directDeliverys = this.selectDirectDelivery(directDeliveryQueryVO);
        sheetAndDataMap.put(1,directDeliverys);

        //sheet 2.仓储服务
        EleBillQueryVO storegeQueryVO = new EleBillQueryVO();
        storegeQueryVO.setCusCode(billRequestVO.getCusCode());
        storegeQueryVO.setBillStartTime(billRequestVO.getBillStartTime());
        storegeQueryVO.setBillEndTime(billRequestVO.getBillEndTime());
        List<String> storegeOrderTypeList = new ArrayList<>();
        storegeOrderTypeList.add("Batch");
        storegeOrderTypeList.add("Collection");
        storegeOrderTypeList.add("Destroy");
        storegeOrderTypeList.add("NewSku");
        storegeOrderTypeList.add("Normal");
        storegeOrderTypeList.add("SelfPick");
        storegeOrderTypeList.add("SplitSku");
        storegeQueryVO.setOrderTypeList(storegeOrderTypeList);
        List<BillDirectDeliveryTotalVO> storegeDeliverys = this.selectDirectDelivery(storegeQueryVO);
        sheetAndDataMap.put(2,storegeDeliverys);

        //sheet 3.仓租

        //sheet 4.大货服务
        EleBillQueryVO bigGoodsQueryVO = new EleBillQueryVO();
        bigGoodsQueryVO.setCusCode(billRequestVO.getCusCode());
        bigGoodsQueryVO.setBillStartTime(billRequestVO.getBillStartTime());
        bigGoodsQueryVO.setBillEndTime(billRequestVO.getBillEndTime());
        List<String> bigGoodsTypeList = new ArrayList<>();
        bigGoodsTypeList.add("BulkOrder");
        bigGoodsQueryVO.setOrderTypeList(bigGoodsTypeList);
        List<BillDirectDeliveryTotalVO> bigGoodsDeliverys = this.selectDirectDelivery(bigGoodsQueryVO);
        sheetAndDataMap.put(4,bigGoodsDeliverys);

        //sheet 5.充值&提现&转换&转账
        EleBillQueryVO billDetailQueryVO = new EleBillQueryVO();
        billDetailQueryVO.setCusCode(billRequestVO.getCusCode());
        billDetailQueryVO.setBillStartTime(billRequestVO.getBillStartTime());
        billDetailQueryVO.setBillEndTime(billRequestVO.getBillEndTime());
        billDetailQueryVO.setSheetNo(5);
        List<AccountSerialBill> billDetails = this.selectBillDetails(billDetailQueryVO);
        sheetAndDataMap.put(5,billDetails);

        //sheet 6.优惠&赔偿
        EleBillQueryVO discountDetailQueryVO = new EleBillQueryVO();
        discountDetailQueryVO.setCusCode(billRequestVO.getCusCode());
        discountDetailQueryVO.setBillStartTime(billRequestVO.getBillStartTime());
        discountDetailQueryVO.setBillEndTime(billRequestVO.getBillEndTime());
        discountDetailQueryVO.setSheetNo(6);
        List<AccountSerialBill> discountDetails = this.selectBillDetails(discountDetailQueryVO);
        sheetAndDataMap.put(6,discountDetails);

        try{

            AccountBillRecordTaskResultVO taskResultVO = new AccountBillRecordTaskResultVO();

            String f = filePath + fileName;
            File file = new File(f);
            ExcelUtil.exportFile(file,"bas",titleDataMap,"bill",sheetAndDataMap,"business",otherAndDataMap,inputStream);

            taskResultVO.setFileName(fileName);
            taskResultVO.setRecordId(billGeneratorBO.getRecordId());
            taskResultVO.setFileUrl(f);

            return taskResultVO;

        }catch (Exception e){
            e.printStackTrace();
            log.error("文件生成失败:",e);
        }

        return null;
    }

    private List<AccountSerialBill> selectBillDetails(EleBillQueryVO billDetailQueryVO) {

        return accountSerialBillMapper.selectBillDetails(billDetailQueryVO);
    }

    private List<BillDirectDeliveryTotalVO> selectDirectDelivery(EleBillQueryVO queryVO) {

        List<BillDirectDeliveryTotalVO> billDirectDeliveryTotalVOS = accountSerialBillMapper.selectDirectDelivery(queryVO);

        return billDirectDeliveryTotalVOS;
    }

    /**
     * 业务类型汇总
     * @param queryVO
     * @return
     */
    private List<BillBusinessTotalVO> selectBusinessTotal(EleBillQueryVO queryVO) {

        List<BillBusinessTotalVO> resultVOS = accountSerialBillMapper.selectBusinessTotal(queryVO);

        return resultVOS;
    }

    /**
     * 硬编码币种，导出
     * @param billBalanceVOS
     * @return
     */
    private List<BillBalanceExcelResultVO> generatorBillExcelResult(List<BillBalanceVO> billBalanceVOS) {

        if(CollectionUtils.isEmpty(billBalanceVOS)){
            return new ArrayList<>();
        }

        BillBalanceVO billBalanceVO = billBalanceVOS.get(0);
        List<BillChargeCategoryVO> billChargeCategoryVOS = billBalanceVO.getChargeCategorys();
        List<BillBalanceExcelResultVO> billBalanceExcelResultVOS = new ArrayList<>();

        for(BillChargeCategoryVO chargeCategoryVO : billChargeCategoryVOS){

            BillBalanceExcelResultVO billBalanceExcelResultVO = new BillBalanceExcelResultVO();
            billBalanceExcelResultVO.setChargeCategory(chargeCategoryVO.getChargeCategory());

            List<BillCurrencyAmountVO> billCurrencyAmountVOS = chargeCategoryVO.getBillCurrencyAmounts();

            for(BillCurrencyAmountVO amountVO :billCurrencyAmountVOS){

                String currencyCode = amountVO.getCurrencyCode();
                if(currencyCode.equals("USD")){
                    billBalanceExcelResultVO.setUsd(amountVO.getAmount());
                }
                if(currencyCode.equals("CNY")){
                    billBalanceExcelResultVO.setCny(amountVO.getAmount());
                }
                if(currencyCode.equals("GBP")){
                    billBalanceExcelResultVO.setGbp(amountVO.getAmount());
                }
                if(currencyCode.equals("AUD")){
                    billBalanceExcelResultVO.setAud(amountVO.getAmount());
                }
                if(currencyCode.equals("EUR")){
                    billBalanceExcelResultVO.setEur(amountVO.getAmount());
                }
                if(currencyCode.equals("CAD")){
                    billBalanceExcelResultVO.setCad(amountVO.getAmount());
                }
                if(currencyCode.equals("HKD")){
                    billBalanceExcelResultVO.setHkd(amountVO.getAmount());
                }
                if(currencyCode.equals("JPY")){
                    billBalanceExcelResultVO.setJpy(amountVO.getAmount());
                }
            }

            billBalanceExcelResultVOS.add(billBalanceExcelResultVO);
        }

        return billBalanceExcelResultVOS;
    }

    private List<BasSellerExcelInfoVO> generatorTitle(BillGeneratorRequestVO billRequestVO, BasSellerInfoVO basSellerInfoVO) {

        String billStartTime = billRequestVO.getBillStartTime();
        String billEndTime = billRequestVO.getBillEndTime();

        String billTime = billStartTime + "/" + billEndTime;


        List<BasSellerExcelInfoVO> basSellerInfoVOS = new ArrayList<>();

        BasSellerExcelInfoVO excelInfoVO = new BasSellerExcelInfoVO();

        excelInfoVO.setCusCode(basSellerInfoVO.getSellerCode());
        excelInfoVO.setCusName(basSellerInfoVO.getUserName());
        excelInfoVO.setGeneratorTime(DateUtils.getTime());
        excelInfoVO.setBillTime(billTime);
        excelInfoVO.setManagerNickName(basSellerInfoVO.getServiceManagerNickName());
        excelInfoVO.setStaffNickName(basSellerInfoVO.getServiceStaffNickName() == null ? "":basSellerInfoVO.getServiceStaffNickName());

        basSellerInfoVOS.add(excelInfoVO);

        return basSellerInfoVOS;
    }
}
