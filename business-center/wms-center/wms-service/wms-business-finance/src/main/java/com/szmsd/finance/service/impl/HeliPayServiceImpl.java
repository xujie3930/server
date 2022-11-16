package com.szmsd.finance.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.helipay.app.trx.facade.request.pay.OrderRequestForm;
import com.helipay.app.trx.facade.response.pay.APPScanPayResponseForm;
import com.helipay.component.facade.BaseDTO;
import com.helipay.component.facade.HeliRequest;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.finance.domain.AccountPay;
import com.szmsd.finance.dto.PreRechargeDTO;
import com.szmsd.finance.enums.HeliOrderStatusEnum;
import com.szmsd.finance.enums.PayEnum;
import com.szmsd.finance.enums.PayScoketEnum;
import com.szmsd.finance.mapper.AccountPayMapper;
import com.szmsd.finance.service.HeliPayService;
import com.szmsd.finance.service.IPreRechargeService;
import com.szmsd.finance.service.RemoteService;
import com.szmsd.finance.util.AES;
import com.szmsd.finance.util.HelipayAPIEncrypt;
import com.szmsd.finance.vo.helibao.PayCallback;
import com.szmsd.finance.vo.helibao.PayRequestVO;
import com.szmsd.finance.ws.PayWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HeliPayServiceImpl implements HeliPayService {

    @Value("${helibao.merchantNo}")
    private String merchantNo;

    @Value("${helibao.paysign}")
    private String paysign;

    @Value("${helibao.payaes}")
    private String payaes;

    @Value("${helibao.alipaysign}")
    private String alipaysign;

    @Value("${helibao.alipayaes}")
    private String alipayaes;

    @Value("${helibao.requestUrl}")
    private String requestUrl;

    @Value("${helibao.serverCallbackUrl}")
    private String serverCallbackUrl;

    private static final String goodsName = "账户充值";

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AccountPayMapper accountPayMapper;

    @Resource
    private IPreRechargeService iPreRechargeService;

    @Resource
    private RemoteService remoteService;

    //手续费 千分之三
    private final static BigDecimal RATE = new BigDecimal("0.003");

    @Override
    public R<APPScanPayResponseForm> pay(PayRequestVO payRequestVO) {

        OrderRequestForm appScanPayRequestForm = new OrderRequestForm();

        BigDecimal amount = payRequestVO.getAmount();
        BigDecimal procedureAmount = payRequestVO.getProcedureAmount();
        BigDecimal proceAmount = amount.multiply(RATE).setScale(2,BigDecimal.ROUND_UP);

        if(procedureAmount.compareTo(proceAmount) != 0){
            return R.failed("手续费计算错误");
        }

        BigDecimal acturlAmount = payRequestVO.getActurlAmount();

        //必填
        appScanPayRequestForm.setMerchantNo(merchantNo);

        String orderNo = this.generatorOrderNo();
        appScanPayRequestForm.setOrderNo(orderNo);
        PayEnum payType = payRequestVO.getPayType();

        appScanPayRequestForm.setProductCode(payType.name());
        //订单金额
        appScanPayRequestForm.setOrderAmount(acturlAmount);

        String cusCode = payRequestVO.getCusCode();

        String gname = goodsName +"-"+cusCode+"-"+RandomUtil.randomNumbers(6);

        //商品名称
        appScanPayRequestForm.setGoodsName(gname);
        appScanPayRequestForm.setServerCallbackUrl(serverCallbackUrl);
        //选填
        //appScanPayRequestForm.setOrderIp("127.0.0.1");

        //String url = "https://cbptrx.helipay.com/cbtrx/rest/domestic/pay/appScan";

        HeliRequest param = this.encodeAndSign(appScanPayRequestForm,payType);
        HeliRequest heliRequest = remoteService.postRemoteInvoke(requestUrl, JSONObject.toJSONString(param), HeliRequest.class);
        APPScanPayResponseForm appScanPayResponseForm = this.decode(heliRequest,payType);
        log.info("#####解密后的内容为{}", appScanPayResponseForm);

        if(!appScanPayResponseForm.getErrorCode().equals("0000")){
            return R.failed(appScanPayResponseForm.getErrorMessage());
        }

        AccountPay accountPay = this.generatorAccountPay(appScanPayResponseForm,cusCode,gname,payRequestVO.getAmount());
        accountPay.setProcedureAmount(proceAmount);
        accountPay.setActurlAmount(acturlAmount);
        accountPayMapper.insert(accountPay);

        return R.ok(appScanPayResponseForm);
    }

    private AccountPay generatorAccountPay(APPScanPayResponseForm appScanPayResponseForm, String cusCode, String gname, BigDecimal amount) {

        AccountPay accountPay = new AccountPay();

        Date current = appScanPayResponseForm.getCurrent();
        accountPay.setGeneratorTime(current);
        accountPay.setErrorCode(appScanPayResponseForm.getErrorCode());
        accountPay.setErrorMessage(appScanPayResponseForm.getErrorMessage());
        accountPay.setQrCode(appScanPayResponseForm.getQrCode());
        accountPay.setMerchantNo(appScanPayResponseForm.getMerchantNo());
        accountPay.setOrderNo(appScanPayResponseForm.getOrderNo());
        accountPay.setProductCode(appScanPayResponseForm.getProductCode());
        accountPay.setSerialNumber(appScanPayResponseForm.getSerialNumber());
        accountPay.setOrderStatus(HeliOrderStatusEnum.INIT.name());
        accountPay.setCusCode(cusCode);
        accountPay.setGoodsName(gname);
        accountPay.setAmount(amount);
        accountPay.setCallbackNumber(0L);

        return accountPay;
    }

    /**
     * 加密
     * @param orgRequest
     * @return
     */
    private HeliRequest encodeAndSign(BaseDTO orgRequest,PayEnum payType){

        log.info("未加密原始请求信息:{}", orgRequest);
        Map map = (Map) JSON.toJSON(orgRequest);
        log.info("转换为map集合后的信息：{}", map);

        String sign = "";
        String aesValue = "";

        if(payType.name().equals("WXPAYSCAN")) {
             sign = HelipayAPIEncrypt.sign(map, paysign);
             aesValue = AES.encryptToBase64(map.toString(), payaes);
        }else if(payType.name().equals("ALIPAYSCAN")){
             sign = HelipayAPIEncrypt.sign(map, alipaysign);
             aesValue = AES.encryptToBase64(map.toString(), alipayaes);
        }

        HeliRequest heliRequest = new HeliRequest();
        heliRequest.setMerchantNo(orgRequest.getMerchantNo());
        heliRequest.setPlatMerchantNo(orgRequest.getPlatMerchantNo());
        heliRequest.setOrderNo(orgRequest.getOrderNo());
        heliRequest.setProductCode(orgRequest.getProductCode());
        heliRequest.setContent(aesValue);
        heliRequest.setSign(sign);
        log.info("加密后请求信息为：{}", JSONObject.toJSONString(heliRequest));
        return heliRequest;
    }

    /**
     * 解密
     * @param heliRequest
     * @return
     */
    private APPScanPayResponseForm decode(HeliRequest heliRequest,PayEnum payType) {
        if (StringUtils.isBlank(heliRequest.getSign())) {
            throw new RuntimeException(heliRequest.getContent());
        } else {

            String sign = "";
            String jsonMap = "";

            if(payType.name().equals("WXPAYSCAN")) {
                jsonMap = AES.decryptFromBase64(heliRequest.getContent(), payaes);
                Map resultMap = JSON.parseObject(jsonMap);
                sign = HelipayAPIEncrypt.sign(resultMap, paysign);

            }else if(payType.name().equals("ALIPAYSCAN")){
                jsonMap = AES.decryptFromBase64(heliRequest.getContent(), alipayaes);
                Map resultMap = JSON.parseObject(jsonMap);
                sign = HelipayAPIEncrypt.sign(resultMap, alipaysign);
            }

            if (!StringUtils.equalsIgnoreCase(sign, heliRequest.getSign())) {
                String msg = String.format("服务端签名:%s,请求签名：%s.验证请求失败,参数可能被劫持篡改,请注意保证接口密钥安全.", sign, heliRequest.getSign());
                log.error("验签失败!!! {}", msg);
                throw new RuntimeException("验签失败");
            } else {
                log.info("\n-------------------解密完成返回-----------------\n");
                return JSON.parseObject(jsonMap, APPScanPayResponseForm.class);
            }
        }
    }

    private PayCallback callbackDecode(HeliRequest heliRequest) {
        if (StringUtils.isBlank(heliRequest.getSign())) {
            throw new RuntimeException(heliRequest.getContent());
        } else {

            String productCode  = heliRequest.getProductCode();

            String jsonMap = "";

            if(productCode.equals(PayEnum.WXPAYSCAN.name())) {
                jsonMap = AES.decryptFromBase64(heliRequest.getContent(), payaes);
            }else if (productCode.equals(PayEnum.ALIPAYSCAN.name())){
                jsonMap = AES.decryptFromBase64(heliRequest.getContent(), alipayaes);
            }
            //Map resultMap = JSON.parseObject(jsonMap);
//            String sign = HelipayAPIEncrypt.sign(resultMap, paysign);
//            if (!StringUtils.equalsIgnoreCase(sign, heliRequest.getSign())) {
//                String msg = String.format("服务端签名:%s,请求签名：%s.验证请求失败,参数可能被劫持篡改,请注意保证接口密钥安全.", sign, heliRequest.getSign());
//                log.error("验签失败!!! {}", msg);
//                throw new RuntimeException("验签失败");
//            } else {
//                log.info("\n-------------------解密完成返回-----------------\n");
//
//            }
            return JSON.parseObject(jsonMap, PayCallback.class);
        }
    }

    /**
     * 生成订单号
     * @return
     */
    private String generatorOrderNo() {

        String current = DateUtils.dateTime();

        String rn = RandomUtil.randomNumbers(8);

        return current + rn;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String payCallback(HeliRequest heliRequest) {

        log.info("helipay payCallback() start...");
        RLock lock = redissonClient.getLock("helipayCallback");

        try {
            if (lock.tryLock(3, TimeUnit.SECONDS)) {
                return this.heliPayCallback(heliRequest);
            }
        }catch (Exception e){
            log.error("helipay payCallback() error: ", e);
            return e.getMessage();
        }finally {
            if (lock.isLocked()){
                lock.unlock();
            }
        }

        log.info("helipay payCallback() end...");

        return null;
    }

    private String heliPayCallback(HeliRequest heliRequest) {

        if(heliRequest == null){
            return "Parameter exception";
        }

        String content = heliRequest.getContent();
        String sign = heliRequest.getSign();

        if(StringUtils.isEmpty(content) || StringUtils.isEmpty(sign)){

            return "content sign ERROR";
        }

        PayCallback payCallback = this.callbackDecode(heliRequest);

        if(payCallback == null){
            log.error("APPScanPayResponseForm error ");
            return "ERROR";
        }

        String orderNo = payCallback.getOrderNo();

        log.info("heliPayCallback payCallback:{}",JSON.toJSONString(payCallback));

        AccountPay accountPay = accountPayMapper.selectOne(Wrappers.<AccountPay>query().lambda()
                .eq(AccountPay::getOrderNo,orderNo)
        );

        if(accountPay == null){
            log.error("accountPay is null,{} ",orderNo);
            return "ERROR";
        }

        if(accountPay.getOrderStatus().equals("SUCCESS")){
            log.error("accountPay 单据已经成功不允许重复提交,{} ",orderNo);
            return "REPEAT";
        }

        log.info("selectOne :{}",JSON.toJSONString(accountPay));

        String orderStatus = payCallback.getOrderStatus();
        
        //accountPay.setAmount(payCallback.getOrderAmount());
        accountPay.setBindId(payCallback.getBindId());
        accountPay.setCreateDate(payCallback.getCreateDate());
        accountPay.setChanlType(payCallback.getChanlType());
        accountPay.setGoodsName(payCallback.getGoodsName());
        accountPay.setProductCode(payCallback.getProductCode());
        accountPay.setConsumeOrderId(payCallback.getConsumeOrderId());
        accountPay.setOrderStatus(orderStatus);
        accountPay.setRemark(payCallback.getRemark());
        accountPay.setFinishDate(payCallback.getFinishDate());
        accountPay.setSerialNumber(payCallback.getSerialNumber());

        accountPay.setActurlAmount(payCallback.getOrderAmount());

        Long cNumber = accountPay.getCallbackNumber();
        accountPay.setCallbackNumber(cNumber+1);

        try {

            int updAccountPay = accountPayMapper.updateById(accountPay);

            log.info("回写更新记录 updAccountPay:{}",updAccountPay);

            if(updAccountPay > 0 && orderStatus.equals(HeliOrderStatusEnum.SUCCESS.name())){

                PayWebSocketServer.sendMessage(orderNo, PayScoketEnum.PAY_SUCCESS.name());

                PreRechargeDTO preRechargeDTO = this.generatorPreRecharge(accountPay);

                log.info("iPreRechargeService pay:{}",JSON.toJSONString(preRechargeDTO));

                iPreRechargeService.pay(preRechargeDTO);
            }

        }catch (Exception e){

            log.error("回写数据失败:",e.getMessage());
            return "ERROR";
        }

        return "SUCCESS";
    }

    private PreRechargeDTO generatorPreRecharge(AccountPay accountPay) {

        PreRechargeDTO preRechargeDTO = new PreRechargeDTO();

        preRechargeDTO.setSerialNo(accountPay.getSerialNumber());
        preRechargeDTO.setCurrencyCode("CNY");
        preRechargeDTO.setCurrencyName("人民币");
        preRechargeDTO.setRemark(accountPay.getRemark());
        preRechargeDTO.setCusName(accountPay.getCusCode());
        preRechargeDTO.setCusCode(accountPay.getCusCode());
        String productCode = accountPay.getProductCode();

        if(productCode.equals(PayEnum.WXPAYSCAN.name())){
            preRechargeDTO.setRemittanceMethod("3");
        }else if(productCode.equals(PayEnum.ALIPAYSCAN.name())){
            preRechargeDTO.setRemittanceMethod("4");
        }
        preRechargeDTO.setVerifyStatus("1");
        preRechargeDTO.setRemittanceTime(accountPay.getFinishDate());
        preRechargeDTO.setVerifyDate(accountPay.getFinishDate());
        preRechargeDTO.setAmount(accountPay.getAmount());
        preRechargeDTO.setProcedureAmount(accountPay.getProcedureAmount());
        preRechargeDTO.setActurlAmount(accountPay.getActurlAmount());

        return preRechargeDTO;
    }
}
