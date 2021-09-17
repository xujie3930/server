package com.szmsd.doc.config;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.com.SystemException;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.UnexpectedTypeException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 公共异常处理
 *
 * @author zhangyuyuan
 * @date 2021-07-30 16:39
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * render
     *
     * @param httpStatus HttpStatus
     * @param exception  Throwable
     */
    protected R<Map<String, Object>> render(HttpStatus httpStatus, Throwable exception) {
        while (exception.getClass().equals(InvocationTargetException.class)) {
            exception = ((InvocationTargetException) exception).getTargetException();
        }
        int code = 0;
        String message = null;
        if (exception instanceof CommonException) {
            CommonException commonException = (CommonException) exception;
            code = Integer.parseInt(commonException.getCode());
            message = convertMessage(code, commonException.getMessage(), exception.getMessage(), commonException.getValues());
        } else if (exception instanceof HystrixFeignException) {
            HystrixFeignException hystrix = (HystrixFeignException) exception;
            code = hystrix.getCode();
            message = convertMessage(code, hystrix.getMessage(), exception.getMessage(), hystrix.getValues());
        } else if (exception instanceof RetryableException) {
            code = HttpStatus.REQUEST_TIMEOUT.value();
            message = HttpStatus.REQUEST_TIMEOUT.getReasonPhrase();
        } else if (exception instanceof BindException) {
            BindException bindException = (BindException) exception;
            List<ObjectError> list = bindException.getAllErrors();
            if (!CollectionUtils.isEmpty(list)) {
                for (ObjectError oe : list) {
                    message = oe.getDefaultMessage();
                    if (!StringUtils.isEmpty(message)) {
                        break;
                    }
                }
            }
        } else if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException manv = (MethodArgumentNotValidException) exception;
            StringBuilder builder = new StringBuilder();
            BindingResult bindingResult = manv.getBindingResult();
            List<ObjectError> allErrors = bindingResult.getAllErrors();
//            for (ObjectError objectError : allErrors) {
//                builder.append("[");
//                Object[] arguments = objectError.getArguments();
//                if (objectError instanceof FieldError) {
//                    if (null == arguments) {
//                        FieldError fieldError = (FieldError) objectError;
//                        builder.append(fieldError.getField());
//                    } else {
//                        for (Object argument : arguments) {
//                            if (argument instanceof DefaultMessageSourceResolvable) {
//                                DefaultMessageSourceResolvable defaultMessageSourceResolvable = (DefaultMessageSourceResolvable) argument;
//                                String[] codes = defaultMessageSourceResolvable.getCodes();
//                                if (null != codes) {
//                                    for (String s : codes) {
//                                        builder.append(s);
//                                        builder.append(",");
//                                    }
//                                    builder.deleteCharAt(builder.length() - 1);
//                                } else {
//                                    builder.append(defaultMessageSourceResolvable.getDefaultMessage());
//                                }
//                            } else {
//                                if (argument instanceof String[]) {
//                                    String[] codes = (String[]) argument;
//                                    for (String s : codes) {
//                                        builder.append(s);
//                                        builder.append(",");
//                                    }
//                                    builder.deleteCharAt(builder.length() - 1);
//                                } else {
//                                    builder.append(argument);
//                                }
//                            }
//                            builder.append(",");
//                        }
//                        builder.deleteCharAt(builder.length() - 1);
//                    }
//                } else {
//                    String[] codes = null;
//                    if (null != arguments) {
//                        for (Object argument : arguments) {
//                            if (argument instanceof String[]) {
//                                codes = (String[]) argument;
//                            }
//                        }
//                    }
//                    if (null != codes) {
//                        for (String s : codes) {
//                            builder.append(s);
//                            builder.append(",");
//                        }
//                        builder.deleteCharAt(builder.length() - 1);
//                    }
//                }
//                builder.append("]");
//                builder.append(objectError.getDefaultMessage());
//                builder.append(",");
//            }
//            if (builder.lastIndexOf(",") == builder.length() - 1) {
//                builder.deleteCharAt(builder.length() - 1);
//            }
//            code = HttpStatus.INTERNAL_SERVER_ERROR.value();
//            message = builder.toString();
            message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).reduce((a, b) -> a + "," + b).orElse("");
            code = HttpStatus.BAD_REQUEST.value();
        } else if (exception instanceof IllegalArgumentException) {
            code = HttpStatus.BAD_REQUEST.value();
            message = exception.getMessage();
            if (StringUtils.isEmpty(message)) {
                message = "不合法的参数异常";
            }
        } else if (exception instanceof UnexpectedTypeException) {
            code = HttpStatus.BAD_REQUEST.value();
            message = "不合法的参数异常";
        } else if (exception instanceof NoSuchMethodException) {
            code = HttpStatus.INTERNAL_SERVER_ERROR.value();
            message = "该方法不支持";
        } else if (exception instanceof ClassNotFoundException) {
            code = HttpStatus.INTERNAL_SERVER_ERROR.value();
            message = "找不到类";
        } else if (exception instanceof DataAccessException) {
            Throwable cause = exception.getCause();
            if (cause instanceof SQLException) {
                SQLException sqlEx = (SQLException) cause;
                code = HttpStatus.INTERNAL_SERVER_ERROR.value();
                String sqlErrMsg = sqlEx.getMessage();
                message = "数据异常:" + sqlErrMsg;
            } else {
                code = HttpStatus.INTERNAL_SERVER_ERROR.value();
                message = "数据异常:" + cause.getMessage();
            }
        }
        if (0 == code) {
            code = HttpStatus.BAD_REQUEST.value();
        }
        if (StringUtils.isEmpty(message)) {
            message = exception.getMessage();
            if (StringUtils.isEmpty(message)) {
                message = "系统内部错误";
            }
        }
        R<Map<String, Object>> result = new R<>();
        result.setCode(code);
        result.setMsg(message);

        // 记录异常日志
        logger.error(code + ":" + message, exception);

        return result;
    }

    /**
     * convertMessage
     *
     * @param code           int
     * @param message        String
     * @param defaultMessage String
     * @param args           Object[]
     * @return String
     */
    protected String convertMessage(int code, String message, String defaultMessage, Object[] args) {
        if (StringUtils.isEmpty(message)) {
            message = defaultMessage;
        } else {
            if (message.contains("{}") && args != null && args.length > 0) {
                for (Object arg : args) {
                    if (message.contains("{}")) {
                        message = message.replaceFirst("\\{}", String.valueOf(arg));
                    }
                }
            }
        }
        if (StringUtils.isEmpty(message) && !StringUtils.isEmpty(code)) {
            message = code + ":";
        }
        return message;
    }

    /**
     * Authentication Exception
     *
     * @param cause AuthenticationException
     * @return ResponseDto
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public R<Map<String, Object>> handleAuthenticationException(AuthenticationException cause) {
        return render(HttpStatus.UNAUTHORIZED, cause);
    }

    /**
     * Default Exception
     *
     * @param cause Throwable
     * @return ResponseDto
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public R<Map<String, Object>> handleException(Throwable cause) {
        return render(HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

    /**
     * handleHttpMessageNotReadableException
     *
     * @param cause HttpMessageNotReadableException
     * @return ResponseDto
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public R<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException cause) {
        return render(HttpStatus.BAD_REQUEST, cause);
    }

    /**
     * handleHttpRequestMethodNotSupportedException
     *
     * @param cause HttpRequestMethodNotSupportedException
     * @return ResponseDto
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public R<Map<String, Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException cause) {
        return render(HttpStatus.BAD_REQUEST, cause);
    }

    /**
     * handleHttpMediaTypeNotSupportedException
     *
     * @param cause Exception
     * @return ResponseDto
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public R<Map<String, Object>> handleHttpMediaTypeNotSupportedException(Exception cause) {
        return render(HttpStatus.UNSUPPORTED_MEDIA_TYPE, cause);
    }

    /**
     * handleCommonException
     *
     * @param cause Exception
     * @return ResponseDto
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CommonException.class)
    @ResponseBody
    public R<Map<String, Object>> handleCommonException(Exception cause) {
        return render(HttpStatus.BAD_REQUEST, cause);
    }

    /**
     * handleSystemException
     *
     * @param cause Exception
     * @return ResponseDto
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SystemException.class)
    @ResponseBody
    public R<Map<String, Object>> handleSystemException(Exception cause) {
        return render(HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
