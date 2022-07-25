package com.szmsd.report.service.impl;

import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.redis.service.RedisService;
import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.vo.DelOutboundReportListVO;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundCountVO;
import com.szmsd.report.component.RemoteComponent;
import com.szmsd.report.service.HomeService;
import com.szmsd.returnex.domain.vo.CusWalletVO;
import com.szmsd.returnex.domain.vo.DocumentVO;
import com.szmsd.system.api.domain.SysUser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    /** 我的钱包redisKey {cusCode} **/
    private String HOME_CUS_WALLET_KEY = "HOME_CUS_WALLET_KEY:{0}";

    /** 订单数据redisKey {cusCode} {startTime} {endTime} **/
    private String HOME_DOCUMENTS = "HOME_DOCUMENTS:{0}:{1}:{2}";

    /** 未处理问题件redisKey **/
    private String HOME_PROBLEM = "HOME_PROBLEM:{0}";

    /** 订单报表redisKey **/
    private String HOME_REPORT = "HOME_REPORT:{0}:{1}:{2}";

    @Resource
    private RedisService redisService;

    @Resource
    private RemoteComponent remoteComponent;

    /**
     * 客户钱包
     * @param cusCode
     * @return
     */
    @Override
    public List<CusWalletVO> selectCusWallet(String cusCode) {

        // 断言 是否是当前登录客户
        assertCusCode(cusCode);

        String redisKey = MessageFormat.format(HOME_CUS_WALLET_KEY, cusCode);
        List<CusWalletVO> cacheObject = redisService.getCacheObject(redisKey);
        if (CollectionUtils.isNotEmpty(cacheObject)) {
            return cacheObject;
        }

        List<AccountBalance> accountList = remoteComponent.accountList(cusCode);
        List<CusWalletVO> collect = accountList.stream().map(wallet -> {
            CusWalletVO cusWalletVO = new CusWalletVO();
            cusWalletVO.setCurrency(wallet.getCurrencyName());
            cusWalletVO.setTotalBalance(wallet.getTotalBalance() + "");
            cusWalletVO.setFreezeBalance(wallet.getFreezeBalance() + "");
            cusWalletVO.setCurrentBalance(wallet.getCurrentBalance() + "");
            return cusWalletVO;
        }).collect(Collectors.toList());

        putRedis(redisKey, collect);

        return collect;
    }

    /**
     * 客户订单数据
     * 当天提审量（当天出库订单的提审包裹数量）
     * 当天到仓量（当天入库单的数量）
     * 当天装运包裹到仓量（当天转运/集运包裹的到仓数量）
     * 当天出库量（当天出库订单的数量）
     * @param cusCode
     */
    @Override
    public List<DocumentVO> selectDocuments(String cusCode) {

        // 断言 是否是当前登录客户
        assertCusCode(cusCode);

        // 当天到仓量
        String thatDay = DateUtils.dateTimeNow("yyyy-MM-dd");
        String startTime = thatDay.concat(" 00:00:00");
        String endTime = thatDay.concat(" 23:59:59");
        String redisKey =  MessageFormat.format(HOME_DOCUMENTS, cusCode, startTime, endTime);

        List<DocumentVO> cacheObject = redisService.getCacheObject(redisKey);
        if (CollectionUtils.isNotEmpty(cacheObject)) {
            return cacheObject;
        }

        List<DocumentVO> documents = new ArrayList<>();
        // 当天提审量 （即当天出库单审核失败、待发货订单数量）
        DelOutboundReportQueryDto outQueryDTO = new DelOutboundReportQueryDto().setSellerCode(cusCode).setBeginDate(DateUtils.parseDate(startTime)).setEndDate(DateUtils.parseDate(endTime));
        List<DelOutboundReportListVO> data1 = remoteComponent.outboundReportCount(outQueryDTO);
        Integer count1 = data1.stream().map(DelOutboundReportListVO::getCount).reduce(Integer::sum).orElse(0);
        documents.add(new DocumentVO().setCusCode(cusCode).setDocumentType(LocalLanguageEnum.HOME_DOCUMENT_TYPE_1.getKey()).setCount(count1.toString()));

        // 当天到仓量 （当天收货的数量：汇总数量）
        InboundReceiptQueryDTO queryDTO = new InboundReceiptQueryDTO();
        queryDTO.setCusCode(cusCode);
        queryDTO.setStartTime(startTime);
        queryDTO.setEndTime(endTime);
        queryDTO.setTimeType(InboundReceiptQueryDTO.TimeType.CR);
        List<InboundCountVO> data2 = remoteComponent.inboundCount(queryDTO);
        Integer count2 = data2.stream().map(InboundCountVO::getCount2).reduce(Integer::sum).orElse(0);
        documents.add(new DocumentVO().setCusCode(cusCode).setDocumentType(LocalLanguageEnum.HOME_DOCUMENT_TYPE_2.getKey()).setCount(count2.toString()));

        // 当天装运包裹到仓量 （即当天转运/集运入库单 处理中/已完成 的到仓数量）
        List<InboundCountVO> data3 = remoteComponent.inboundCount(queryDTO.setOrderType("Collection").setStatusList(Arrays.asList(LocalLanguageEnum.INBOUND_RECEIPT_STATUS_4.getKey(), LocalLanguageEnum.INBOUND_RECEIPT_STATUS_5.getKey())));
        Integer count3 = data3.stream().map(InboundCountVO::getCount).reduce(Integer::sum).orElse(0);
        documents.add(new DocumentVO().setCusCode(cusCode).setDocumentType(LocalLanguageEnum.HOME_DOCUMENT_TYPE_3.getKey()).setCount(count3.toString()));

        // 当天出库量 （当天出库订单“已完成”的数量）
        List<DelOutboundReportListVO> data4 = remoteComponent.outboundData(outQueryDTO);
        Integer count4 = data4.stream().map(DelOutboundReportListVO::getCount).reduce(Integer::sum).orElse(0);
        documents.add(new DocumentVO().setCusCode(cusCode).setDocumentType(LocalLanguageEnum.HOME_DOCUMENT_TYPE_4.getKey()).setCount(count4.toString()));

        putRedis(redisKey, documents);

        return documents;
    }

    /**
     * 问题件
     * @param cusCode
     * @return
     */
    @Override
    public List<DocumentVO> selectProblem(String cusCode) {

        // 断言 是否是当前登录客户
        assertCusCode(cusCode);

        String redisKey =  MessageFormat.format(HOME_PROBLEM, cusCode);

        List<DocumentVO> cacheObject = redisService.getCacheObject(redisKey);
        if (cacheObject != null) {
            return cacheObject;
        }

        Integer count = Optional.ofNullable(remoteComponent.problemCount(cusCode)).orElse(0);
        List<DocumentVO> documents = Collections.singletonList(new DocumentVO().setCusCode(cusCode).setDocumentType(LocalLanguageEnum.HOME_DOCUMENT_TYPE_5.getKey()).setCount(count + ""));
        putRedis(redisKey, documents);
        return documents;
    }

    /**
     * 近7天订单报表
     * @param cusCode
     * @return
     */
    @Override
    public List<List<String>> queryOrder7Report(String cusCode) {

        // 断言 是否是当前登录客户
        assertCusCode(cusCode);

        String startTime = DateUtils.getPastDate(6);
        String endTime = DateUtils.dateTimeNow("yyyy-MM-dd").concat(" 00:00:00");
        String redisKey =  MessageFormat.format(HOME_REPORT, cusCode, startTime, endTime);

        List<List<String>> cacheObject = redisService.getCacheObject(redisKey);
        if (cacheObject != null) {
            return cacheObject;
        }
        List<List<String>> result = new ArrayList<>();
        result.add(Arrays.asList("date", LocalLanguageEnum.HOME_BAR_CHART_TYPE_1.getValueLen(), LocalLanguageEnum.HOME_BAR_CHART_TYPE_2.getValueLen(), LocalLanguageEnum.HOME_BAR_CHART_TYPE_3.getValueLen(), LocalLanguageEnum.HOME_BAR_CHART_TYPE_4.getValueLen()));

        // 已创建订单(创建出库单的数量)：Order Created
        DelOutboundReportQueryDto outQueryDto = new DelOutboundReportQueryDto().setSellerCode(cusCode).setBeginDate(DateUtils.parseDate(startTime)).setEndDate(DateUtils.parseDate(endTime));
        List<DelOutboundReportListVO> created = remoteComponent.queryCreateData(outQueryDto);

        // 已提审订单（审核失败、待发货订单）：Order Submited
        List<DelOutboundReportListVO> submited = remoteComponent.outboundReportCount(outQueryDto);

        // 已入库订单（状态：已完成）：Order Completed
        InboundReceiptQueryDTO inQueryDTO = new InboundReceiptQueryDTO();
        inQueryDTO.setCusCode(cusCode);
        inQueryDTO.setStartTime(startTime);
        inQueryDTO.setEndTime(endTime);
        inQueryDTO.setTimeType(InboundReceiptQueryDTO.TimeType.CR);
        List<InboundCountVO> completed = remoteComponent.inboundCount(inQueryDTO.setStatus(LocalLanguageEnum.INBOUND_RECEIPT_STATUS_5.getKey()));

        // 已出库订单（状态为已完成）：Order Shipped
        List<DelOutboundReportListVO> shipped = remoteComponent.outboundData(outQueryDto);

        for (int i = 7; i > 0; i--) {
            String time = DateUtils.getPastDate(i);
            Integer createdCount = created.stream().filter(item -> time.equals(item.getDate())).map(DelOutboundReportListVO::getCount).reduce(Integer::sum).orElse(0);
            Integer submitedCount = submited.stream().filter(item -> time.equals(item.getDate())).map(DelOutboundReportListVO::getCount).reduce(Integer::sum).orElse(0);
            Integer completedCount = completed.stream().filter(item -> time.equals(item.getGroupBy())).map(InboundCountVO::getCount).reduce(Integer::sum).orElse(0);
            Integer shippedCount = shipped.stream().filter(item -> time.equals(item.getDate())).map(DelOutboundReportListVO::getCount).reduce(Integer::sum).orElse(0);
            List<String> source = Arrays.asList(time.replace(DateUtils.dateTimeNow("yyyy-"), ""), createdCount.toString(), submitedCount.toString(), completedCount.toString(), shippedCount.toString());
            result.add(source);
        }

        putRedis(redisKey, result);

        return result;
    }

    /**
     * 5秒redis
     * @param key
     * @param value
     */
    private void putRedis(String key, Object value) {
        redisService.setCacheObject(key, value, 5, TimeUnit.SECONDS);
    }

    public void assertCusCode(String cusCode) {
        SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
        boolean equals = StringUtils.equals(loginUserInfo.getSellerCode(), cusCode);
        AssertUtil.isTrue(equals, "无效客户统计");
    }
}
