package com.szmsd.delivery.command;

import com.szmsd.common.core.command.BasicCommand;

import java.util.List;

public class ChargePricingCmd extends BasicCommand<Void> {

    private List<String> orderNos;

    public ChargePricingCmd(List<String> orderNos){

        this.orderNos = orderNos;
    }

    @Override
    protected Void doExecute() throws Exception {


        return null;
    }
}
