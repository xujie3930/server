package com.szmsd.finance.service;

import com.helipay.app.trx.facade.response.pay.APPScanPayResponseForm;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.vo.helibao.PayRequestVO;

public interface HeliPayService {

    /**
     * 支付
     * @param payRequestVO
     * @return
     */
    R<APPScanPayResponseForm> pay(PayRequestVO payRequestVO);
}
