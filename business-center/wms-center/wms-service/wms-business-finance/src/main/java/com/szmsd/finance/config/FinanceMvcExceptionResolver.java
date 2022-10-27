package com.szmsd.finance.config;


import com.szmsd.bas.api.feign.EmailFeingService;
import com.szmsd.bas.dto.EmailDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class FinanceMvcExceptionResolver implements HandlerExceptionResolver {
    private Logger logger = LoggerFactory.getLogger(FinanceMvcExceptionResolver.class);




    @Override
    public ModelAndView resolveException(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler, @NonNull Exception ex) {
        //通过判断错误类型来做邮件的处理
        System.out.println("通过判断错误类型来做邮件的处理 = ");
        EmailDto emailDto=new EmailDto();
        EmailFeingService bean = SpringUtils.getBean(EmailFeingService.class);


           if (((CommonException) ex).getCode().equals("500")) {
               emailDto.setText(ex.getStackTrace()[0].getMethodName()+":"+((CommonException) ex).getMessage());
               R r = bean.sendEmailError(emailDto);
           }
        return null;
    }


}
