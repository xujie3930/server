package com.szmsd.http.enums;

/**
 * @author liulei
 */
public class HttpRechargeConstants {
    public enum RechargeErrorCode{
        Unkown,//未知系统异常
        InvalidAmount,//金额大小不合法
        InvalidCurrencyCode,//货币类型不支持
        RequestError,//请求参数不正确
        ;
        RechargeErrorCode(){}
    }
    public enum RechargeCurrencyCode{
        CNY,//人民币
        HKD,//港元
        USD,//美元
        JPY,//日元
        AUD//澳大利亚元
        ;
        RechargeCurrencyCode(){}
    }
    public enum RechargeMethodCode{
        AliPay,//支付宝
        WechatPay,//微信
        Paypal,//paypal
        Payonner//payonner
        ;
        RechargeMethodCode(){}
    }
    public enum RechargeStatusCode{
        Created,//已创建
        Pending,//待支付
        Successed,//充值成功
        Failed//充值失败
        ;
        RechargeStatusCode(){}
    }
    public enum OrderStateCode{
        BePay,//已创建
        Finish,//待支付
        Expired,//充值成功
        Paying,//充值失败
        Failed,//支付失败
        Cancel//取消支付
        ;
        OrderStateCode(){}
    }

}
