package com.szmsd.delivery.config;


import com.szmsd.bas.api.feign.EmailFeingService;
import com.szmsd.bas.dto.EmailDto;
import com.szmsd.bas.dto.EmailObjectDto;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class DelMvcExceptionResolver implements HandlerExceptionResolver {
    private Logger logger = LoggerFactory.getLogger(DelMvcExceptionResolver.class);




    @Override
    public ModelAndView resolveException(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler, @NonNull Exception ex) {
        //通过判断错误类型来做邮件的处理
        System.out.println("通过判断错误类型来做邮件的处理 = ");
        EmailDto emailDto=new EmailDto();
        EmailFeingService bean = SpringUtils.getBean(EmailFeingService.class);
        //邮箱接收人
        emailDto.setTo("1402476569@qq.com");

           if (((CommonException) ex).getCode().equals("500")) {
               emailDto.setText(ex.getStackTrace()[0].getMethodName()+":"+((CommonException) ex).getMessage());
               R r = bean.sendEmailError(emailDto);
           }
        return null;
    }

//    @Override
//    public ModelAndView resolveException(HttpServletRequest request,
//                                         HttpServletResponse response, Object handler, Exception ex) {
//
//        //通过判断错误类型来做邮件的处理
//        System.out.println("通过判断错误类型来做邮件的处理 = ");
//
//        System.out.println(ex);
//
//
//        EmailDto emailDto=new EmailDto();
//
//        //邮箱接收人
//        emailDto.setTo("1402476569@qq.com");
//
//           if (((CommonException) ex).getCode().equals("500")) {
//               emailDto.setText(((CommonException) ex).getMessage());
//               R r = emailFeingService.sendEmailError(emailDto);
//           }
//
//
//        return  null;


}
