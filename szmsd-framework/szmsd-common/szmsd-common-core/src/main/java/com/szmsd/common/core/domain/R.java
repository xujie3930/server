package com.szmsd.common.core.domain;

import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.LogisticsException;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;

import java.io.Serializable;

import static com.szmsd.common.core.web.controller.BaseController.getLen;

/**
 * 响应信息主体
 *
 * @author szmsd
 */
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    private T data;

    //区分语言默认返回的成功
    public static <T> R<T> ok() {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(ExceptionMessageEnum.SUCCESS, getLen());
        return restResult(null, Constants.SUCCESS, logisticsException.getMessage());
    }
    //区分语言 成功返回数据
    public static <T> R<T> ok(T data) {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(ExceptionMessageEnum.SUCCESS, getLen());
        return restResult(data, Constants.SUCCESS, logisticsException.getMessage());
    }

    //区分语言默认返回的失败
    public static <T> R<T> failed() {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(ExceptionMessageEnum.FAIL, getLen());
        return restResult(null, Constants.FAIL, logisticsException.getMessage());
    }
    //区分语言默认返回的失败
    public static <T> R<T> onFailed() {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(ExceptionMessageEnum.FAIL, null);
        return restResult(null, Constants.FAIL, logisticsException.getMessage());
    }


    //区分语言返回的失败 自定义错误枚举
    public static <T> R<T> failed(ExceptionMessageEnum messageEnum) {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(messageEnum, getLen());
        return restResult(null, Constants.FAIL, logisticsException.getMessage());
    }

    //区分语言返回的失败 自定义错误枚举+动态枚举参数, 在枚举 里用&通配符
    public static <T> R<T> failed(ExceptionMessageEnum messageEnum,Object... values) {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(messageEnum, getLen(),values);
        return restResult(null, Constants.FAIL, logisticsException.getMessage());
    }

    //区分语言 返回自定义code+自定义错误枚举
    public static <T> R<T> failed(Integer code, ExceptionMessageEnum messageEnum) {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(messageEnum, getLen());
        return restResult(null, code, logisticsException.getMessage());
    }

    //区分语言返回的失败 自定义code+错误枚举+动态枚举参数, 在枚举 里用&通配符
    public static <T> R<T> failed(Integer code,ExceptionMessageEnum messageEnum,Object... values) {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(messageEnum, getLen(),values);
        return restResult(null, code, logisticsException.getMessage());
    }



    //区分语言 失败返回数据+拼接失败单号等
    public static <T> R<T> failed(ExceptionMessageEnum messageEnum,String msg) {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(messageEnum, getLen());
        return restResult(null, Constants.FAIL, logisticsException.getMessage()+msg);
    }



    //不区分语言 失败返回数据
    public static <T> R<T> failed(String msg) {
//        LogisticsException logisticsException = LogisticsExceptionUtil.getException(ExceptionMessageEnum.FAIL, getLen());
        return restResult(null, Constants.FAIL, msg);
    }


//    public static <T> R<T> ok(T data, String msg)
//    {
//        return restResult(data, Constants.SUCCESS, "操作成功");
//    }




    public static <T> R<T> failed(int code, String msg) {
        return restResult(null, code, msg);
    }


    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
