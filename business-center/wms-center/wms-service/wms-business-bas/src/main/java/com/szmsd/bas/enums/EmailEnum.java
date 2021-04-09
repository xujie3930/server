package com.szmsd.bas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum EmailEnum {

    VAR_CODE("验证码",
            "【DM FULFILLMENT】验证码：{0}。此验证码用于设置你的帐户邮箱，请在DM OMS注册页面中输入并完成验证。验证码有效时间：30分钟",
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
