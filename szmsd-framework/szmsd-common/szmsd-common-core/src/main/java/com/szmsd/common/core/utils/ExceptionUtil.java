package com.szmsd.common.core.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * 错误信息处理类。
 *
 * @author szmsd
 */
public class ExceptionUtil {
    /**
     * 获取exception的详细错误信息。
     */
    public static String getExceptionMessage(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        String str = sw.toString();
        return str;
    }

    public static String getRootErrorMseeage(Exception e) {
        // 验证异常
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException manv = (MethodArgumentNotValidException) e;
            StringBuilder builder = new StringBuilder();
            List<FieldError> fieldErrors = manv.getBindingResult().getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                builder.append(fieldError.getField());
                builder.append(fieldError.getDefaultMessage());
                builder.append(",");
            }
            if (builder.lastIndexOf(",") == builder.length() - 1) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        }
        // 其它异常
        Throwable root = ExceptionUtils.getRootCause(e);
        root = (root == null ? e : root);
        if (root == null) {
            return "";
        }
        String msg = root.getMessage();
        if (msg == null) {
            return "null";
        }
        return StringUtils.defaultString(msg);
    }
}
