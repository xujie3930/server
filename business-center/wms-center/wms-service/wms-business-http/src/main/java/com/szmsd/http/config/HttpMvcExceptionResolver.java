package com.szmsd.http.config;


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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HttpMvcExceptionResolver implements HandlerExceptionResolver {
    private Logger logger = LoggerFactory.getLogger(HttpMvcExceptionResolver.class);

    ExecutorService service= Executors.newFixedThreadPool(4);


    @Override
    public ModelAndView resolveException(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler, @NonNull Exception ex) {
        //通过判断错误类型来做邮件的处理
        System.out.println("通过判断错误类型来做邮件的处理 = ");
        EmailDto emailDto=new EmailDto();


           if (((CommonException) ex).getCode().equals("500")) {
               emailDto.setText(ex.getStackTrace()[0].getMethodName()+":"+((CommonException) ex).getMessage());
               EmailFeingService bean = SpringUtils.getBean(EmailFeingService.class);
               Email email=new Email().setEmailDto(emailDto).setEmailFeingService(bean);

               service.execute(email);
               //2.关闭连接
               //service.shutdown();
           }
        return null;
    }

}
class  Email implements Runnable {
    private EmailDto emailDto;
    private EmailFeingService emailFeingService;
    @Override
    public void run() {

        emailFeingService.sendEmailError(emailDto);

    }


    public Email setEmailDto(EmailDto emailDto) {
        this.emailDto = emailDto;
        return this;
    }
    public Email setEmailFeingService(EmailFeingService emailFeingService) {
        this.emailFeingService = emailFeingService;
        return this;
    }


}
