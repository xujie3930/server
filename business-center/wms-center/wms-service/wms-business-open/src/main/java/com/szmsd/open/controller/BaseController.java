package com.szmsd.open.controller;

import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.ApiException;
import com.szmsd.common.core.exception.com.LogisticsException;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.ExceptionUtil;
import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.open.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.security.auth.login.LoginException;
import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 14:42
 */
public class BaseController {

    protected static final Logger log = LoggerFactory.getLogger(BaseController.class);

    /**
     * 获取多语言标识
     */
    public static String getLen() {
        String len = ServletUtils.getHeaders("Langr");
        if (StringUtils.isEmpty(len)) {
            len = "zh";
        }
        return len;
    }

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 登录异常
     */
    @ExceptionHandler(LoginException.class)
    @ResponseBody
    public ResponseVO handleLoginException(LoginException e) {
        log.error("基础业务异常拦截 LoginException {}", e.getMessage(), e);
        return ResponseVO.failed(e.getMessage());
    }

    //基础业务异常拦截
    @ExceptionHandler({BaseException.class})
    @ResponseBody
    public ResponseVO handleBaseException(BaseException baseException) {
        log.info("基础业务异常拦截 BaseException {}", baseException.getDefaultMessage());
        return ResponseVO.failed(baseException.getDefaultMessage());
    }

    //自定义异常拦截
    @ExceptionHandler({ApiException.class})
    @ResponseBody
    public ResponseVO handleApiException(ApiException e) {
        log.info("自定义异常拦截 ApiException {}", e);
        return ResponseVO.failed(e.getMessage());
    }

    //全局系统异常拦截
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseVO handleException(Exception e) {
        log.info("系统异常拦截 Exception: {}", e);
        return ResponseVO.failed(ExceptionUtil.getRootErrorMseeage(e));
    }

    /**
     * @return com.szmsd.inner.common.handler.ResponseEntity
     * @Author Mars
     * @Description //TODO 请求前异常
     * @Date 2020/6/16
     * @Param [e]
     **/
    @ExceptionHandler({
            HttpMediaTypeNotAcceptableException.class,
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class
    })
    @ResponseBody
    public ResponseVO server500(Exception e) {
        log.info("请求前异常:", e);
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(ExceptionMessageEnum.REQUESTERROR, getLen());
        return ResponseVO.failed(logisticsException.getMessage() + ":" + ExceptionUtil.getRootErrorMseeage(e));
    }

    /**
     * @return com.szmsd.inner.common.handler.ResponseEntity
     * @Author Mars
     * @Description //TODO 运行时异常
     * @Date 2020/6/16
     * @Param [ex]
     **/
    @ResponseBody
    @ExceptionHandler({RuntimeException.class,
            NullPointerException.class,
            ClassCastException.class,
            NoSuchMethodException.class,
            IndexOutOfBoundsException.class,
            HttpMessageNotReadableException.class,
            TypeMismatchException.class

    })
    public ResponseVO runtimeExceptionHandler(Exception e) {
        log.info("运行时异常:", e);
        LogisticsException logisticsException = LogisticsExceptionUtil.getException(ExceptionMessageEnum.RUNERROR, getLen());
        return ResponseVO.failed(logisticsException.getMessage() + ":" + ExceptionUtil.getRootErrorMseeage(e));
    }
}
