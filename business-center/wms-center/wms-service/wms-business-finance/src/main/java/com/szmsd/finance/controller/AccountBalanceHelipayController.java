package com.szmsd.finance.controller;

import cn.hutool.http.HttpRequest;
import com.helipay.app.trx.facade.response.pay.APPScanPayResponseForm;
import com.helipay.component.facade.HeliRequest;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.service.HeliPayService;
import com.szmsd.finance.vo.helibao.PayRequestVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = {"账户余额-合利宝"})
@RestController
@RequestMapping("/helibao")
public class AccountBalanceHelipayController {

    @Autowired
    private HeliPayService payService;

    @ApiOperation(value = "/微信-支付宝扫码支付")
    @PostMapping("/pay")
    public R<APPScanPayResponseForm> pay(@RequestBody @Valid PayRequestVO payRequestVO){

        return payService.pay(payRequestVO);
    }

    @ApiOperation(value = "/支付回调")
    @GetMapping("/pay-callback")
    public String payallback(HttpServletRequest request){

        String content = request.getParameter("content");
        String sign = request.getParameter("sign");
        String merchantNo = request.getParameter("merchantNo");
        String productCode = request.getParameter("productCode");
        String orderNo = request.getParameter("orderNo");

        HeliRequest heliRequest = new HeliRequest();
        heliRequest.setContent(content);
        heliRequest.setSign(sign);
        heliRequest.setMerchantNo(merchantNo);
        heliRequest.setProductCode(productCode);
        heliRequest.setOrderNo(orderNo);

        return payService.payCallback(heliRequest);
    }

    @ApiOperation(value = "/支付回调")
    @PostMapping("/pay-callback")
    public String payCallback(HttpServletRequest request){

        String content = request.getParameter("content");
        String sign = request.getParameter("sign");
        String merchantNo = request.getParameter("merchantNo");
        String productCode = request.getParameter("productCode");
        String orderNo = request.getParameter("orderNo");

        HeliRequest heliRequest = new HeliRequest();
        heliRequest.setContent(content);
        heliRequest.setSign(sign);
        heliRequest.setMerchantNo(merchantNo);
        heliRequest.setProductCode(productCode);
        heliRequest.setOrderNo(orderNo);

        return payService.payCallback(heliRequest);
    }


}
