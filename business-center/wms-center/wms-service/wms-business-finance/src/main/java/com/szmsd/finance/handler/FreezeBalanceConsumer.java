package com.szmsd.finance.handler;

import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.factory.abstractFactory.PayFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

@Slf4j
public class FreezeBalanceConsumer implements Callable<Boolean> {

    private BlockingQueue<CustPayDTO> queue;

    private PayFactoryBuilder payFactoryBuilder;

    public FreezeBalanceConsumer(PayFactoryBuilder payFactoryBuilder,BlockingQueue<CustPayDTO> dto){
        this.queue = dto;
        this.payFactoryBuilder = payFactoryBuilder;
    }

    @Override
    public Boolean call() throws Exception {

        try {

            log.info("FreezeBalanceConsumer 队列大小{}",queue.size());

            CustPayDTO dto =  queue.take();

            AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
            //log.info(LogUtil.format(cfbDTO, "费用冻结"));

            Boolean flag = abstractPayFactory.updateBalance(dto);

            return flag;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
