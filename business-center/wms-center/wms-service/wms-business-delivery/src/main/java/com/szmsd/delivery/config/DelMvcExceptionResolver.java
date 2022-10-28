package com.szmsd.delivery.config;


import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.szmsd.bas.api.feign.EmailFeingService;
import com.szmsd.bas.dto.EmailDto;
import com.szmsd.bas.dto.EmailObjectDto;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.task.EasyPoiExportTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class DelMvcExceptionResolver implements HandlerExceptionResolver {
    private Logger logger = LoggerFactory.getLogger(DelMvcExceptionResolver.class);
    @Resource
    protected ThreadPoolTaskExecutor financeThreadTaskPool;
    ExecutorService service= Executors.newFixedThreadPool(10);


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



