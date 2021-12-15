package com.szmsd.bas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum EmailEnum {
    //"【DM FULFILLMENT】验证码：{0}。此验证码用于设置你的帐户邮箱，请在DM OMS注册页面中输入并完成验证。验证码有效时间：30分钟",
    VAR_CODE("Confirmation Code",
            "【DM FULFILLMENT】{0} is your DM Fulfillment confirmation code. Please enter and complete the confirmation on the registration page. The effective time of confirmation code is 30 minutes",
            (param) -> MessageFormat.format(EmailEnum.valueOf("VAR_CODE").getContent(), param), 30, TimeUnit.MINUTES),
    ;
    /** 标题 **/
    private String title;

    /** 内容 **/
    private String content;

    /** 自定义表达式 **/
    private Function<String, String> func;

    /** redis失效时间 **/
    private Integer timeout;

    /** redis存活单位 **/
    private TimeUnit timeUnit;

    public String get(String param) {
        return func.apply(param);
    }

}
