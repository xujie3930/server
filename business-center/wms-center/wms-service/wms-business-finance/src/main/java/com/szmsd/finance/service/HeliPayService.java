package com.szmsd.finance.service;

import com.helipay.app.trx.facade.response.pay.APPScanPayResponseForm;
import com.helipay.component.facade.HeliRequest;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.vo.helibao.PayRequestVO;

public interface HeliPayService {

    /**
     * 支付
     * @param payRequestVO
     * @return
     */
    R<APPScanPayResponseForm> pay(PayRequestVO payRequestVO);

    /**
     * 支付回调
     * @param payCallbackParamsVO
     * @return
     */
    String payCallback(HeliRequest payCallbackParamsVO);
}
