package com.szmsd.http.api.controller;

import com.szmsd.http.api.service.ITransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 10:00
 */
// @Component
@Deprecated
public class TransactionHandlerRunner implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, ITransactionHandler<?, ?>> handlerMap = applicationContext.getBeansOfType(new DefaultTargetType<ITransactionHandler<?, ?>>() {
        }.getClassType());
        TransactionHandlerManager.getInstance().refresh(handlerMap);

        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {
            System.out.println("beanName: " + beanName);
        }
    }
}
