package com.szmsd.common.core.domain;

import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.com.LogisticsException;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import com.szmsd.common.core.exception.com.SystemException;
import org.springframework.http.HttpStatus;

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
    public static <T> R<T> failed(ExceptionMessageEnum messageEnum, Object... values) {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(messageEnum, getLen(), values);
        return restResult(null, Constants.FAIL, logisticsException.getMessage());
    }

    //区分语言 返回自定义code+自定义错误枚举
    public static <T> R<T> failed(Integer code, ExceptionMessageEnum messageEnum) {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(messageEnum, getLen());
        return restResult(null, code, logisticsException.getMessage());
    }

    //区分语言返回的失败 自定义code+错误枚举+动态枚举参数, 在枚举 里用&通配符
    public static <T> R<T> failed(Integer code, ExceptionMessageEnum messageEnum, Object... values) {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(messageEnum, getLen(), values);
        return restResult(null, code, logisticsException.getMessage());
    }


    //区分语言 失败返回数据+拼接失败单号等
    public static <T> R<T> failed(ExceptionMessageEnum messageEnum, String msg) {
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(messageEnum, getLen());
        return restResult(null, Constants.FAIL, logisticsException.getMessage() + msg);
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

    public static <T> R<T> convertResultJson(Throwable throwable) {
        R<T> r = new R<>();
        if (null == throwable) {
            return r;
        }
        if (throwable instanceof CommonException) {
            CommonException commonException = (CommonException) throwable;
            // code使用异常返回的code
            // r.setCode(Constants.FAIL);
            if (null == commonException.getCode()) {
                r.setCode(HttpStatus.BAD_REQUEST.value());
            } else {
                r.setCode(Integer.parseInt(commonException.getCode()));
            }
            r.setMsg(commonException.getMessage());
        } else if (throwable instanceof SystemException) {
            SystemException systemException = (SystemException) throwable;
            if (null == systemException.getCode()) {
                r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            } else {
                r.setCode(Integer.parseInt(systemException.getCode()));
            }
            r.setMsg(systemException.getMessage());
        } else {
            r.setCode(Constants.FAIL);
            r.setMsg(throwable.getMessage());
        }
        return r;
    }

    public static <T> T getDataAndException(R<T> r) {
        if (null != r) {
            if (Constants.SUCCESS == r.getCode()) {
                return r.getData();
            } else if (HttpStatus.BAD_REQUEST.value() == r.getCode()) {
                throw new CommonException("" + r.getCode(), r.getMsg());
            } else {
                // 抛出接口返回的异常信息
                throw new SystemException("" + r.getCode(), r.getMsg());
            }
        }
        return null;
    }


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
