package com.szmsd.finance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.helipay.app.trx.facade.request.pay.OrderRequestForm;
import com.helipay.app.trx.facade.response.pay.APPScanPayResponseForm;
import com.helipay.component.facade.HeliRequest;
import com.helipay.demo.common.component.util.HandleDataUtils;
import com.helipay.demo.common.service.remote.RemoteService;
import com.helipay.demo.common.service.remote.RemoteServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.service.HeliPayService;
import com.szmsd.finance.vo.helibao.PayRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HeliPayServiceImpl implements HeliPayService {

    private String merchantNo;

    private String url;

    @Override
    public R<APPScanPayResponseForm> pay(PayRequestVO payRequestVO) {

        RemoteService remoteService = new RemoteServiceImpl();

        OrderRequestForm appScanPayRequestForm = new OrderRequestForm();
        //必填
        appScanPayRequestForm.setMerchantNo("Me10047065");
        appScanPayRequestForm.setOrderNo("WXPAYSCAN4765765476");
        appScanPayRequestForm.setProductCode(payRequestVO.getPayType().name());
        //订单金额
        appScanPayRequestForm.setOrderAmount(payRequestVO.getAmount());
        //商品名称
        appScanPayRequestForm.setGoodsName("测试商品");
        //选填
        //appScanPayRequestForm.setOrderIp("127.0.0.1");

        String url = "https://cbptrx.helipay.com/cbtrx/rest/domestic/pay/appScan";

        HeliRequest param = HandleDataUtils.encodeAndSign(appScanPayRequestForm, appScanPayRequestForm.getProductCode(), appScanPayRequestForm.getPlatMerchantNo(), appScanPayRequestForm.getMerchantNo());
        HeliRequest heliRequest = remoteService.postRemoteInvoke(url, JSONObject.toJSONString(param), HeliRequest.class);
        APPScanPayResponseForm appScanPayResponseForm = HandleDataUtils.decode(heliRequest, APPScanPayResponseForm.class, heliRequest.getProductCode(), heliRequest.getPlatMerchantNo(), heliRequest.getMerchantNo());
        log.info("#####解密后的内容为{}", appScanPayResponseForm);

        if(!appScanPayResponseForm.getErrorCode().equals("0000")){
            return R.failed(appScanPayResponseForm.getErrorMessage());
        }

        return R.ok(appScanPayResponseForm);
    }
}
