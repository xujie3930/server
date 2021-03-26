package com.szmsd.bas.plugin;

import com.szmsd.bas.plugin.service.BasSubFeignPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;

/**
 * @author zhangyuyuan
 * @date 2020-12-16 016 16:04
 */
public class AutoValueHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    private final Logger logger = LoggerFactory.getLogger(AutoValueHandlerMethodReturnValueHandler.class);

    private final HandlerMethodReturnValueHandler delegate;
    private final BasSubFeignPluginService basSubFeignPluginService;

    public AutoValueHandlerMethodReturnValueHandler(HandlerMethodReturnValueHandler delegate, BasSubFeignPluginService basSubFeignService) {
        this.delegate = delegate;
        this.basSubFeignPluginService = basSubFeignService;
    }

    @Override
    public boolean supportsReturnType(@NonNull MethodParameter methodParameter) {
        return delegate.supportsReturnType(methodParameter);
    }

    @Override
    public void handleReturnValue(Object o, @NonNull MethodParameter methodParameter, @NonNull ModelAndViewContainer modelAndViewContainer, @NonNull NativeWebRequest nativeWebRequest) throws Exception {
        delegate.handleReturnValue(convertReturnValue(o, methodParameter), methodParameter, modelAndViewContainer, nativeWebRequest);
    }

    private Object convertReturnValue(Object source, MethodParameter returnType) {
        // 逻辑判断
        Method returnTypeMethod = returnType.getMethod();
        if (null != source && null != returnTypeMethod && returnTypeMethod.isAnnotationPresent(AutoValue.class)) {
            // handlerAutoValue(source);
            // return source;
            return new HandlerContext<>(basSubFeignPluginService, source).handlerValue();
        }
        return source;
    }
}
