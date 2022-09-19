package com.szmsd.finance.handler;

import com.szmsd.finance.dto.CustPayDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@Slf4j
public class FreezeBalanceProducer implements Runnable{

    private CustPayDTO custPayDTO;

    private BlockingQueue<CustPayDTO> queue;

    public FreezeBalanceProducer(CustPayDTO custPayDTO,BlockingQueue<CustPayDTO> dto){
        this.custPayDTO = custPayDTO;
        this.queue = dto;
    }


    @Override
    public void run() {

        log.info("FreezeBalanceProducer 开始 put");

        try {
            queue.put(custPayDTO);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
