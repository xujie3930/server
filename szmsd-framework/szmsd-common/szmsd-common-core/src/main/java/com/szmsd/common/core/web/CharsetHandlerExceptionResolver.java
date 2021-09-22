package com.szmsd.common.core.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@ConditionalOnClass(HandlerExceptionResolver.class)
@Component
public class CharsetHandlerExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler, @NonNull Exception ex) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return null;
    }
}
