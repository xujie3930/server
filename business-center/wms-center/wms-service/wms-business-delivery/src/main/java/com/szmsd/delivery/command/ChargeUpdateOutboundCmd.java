package com.szmsd.delivery.command;

import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.delivery.domain.ChargeImport;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ChargeUpdateOutboundCmd extends BasicCommand<Void> {

    private List<ChargeImport> chargeImportList;

    public ChargeUpdateOutboundCmd(List<ChargeImport> chargeImportList){
        this.chargeImportList = chargeImportList;
    }

    @Override
    protected void beforeDoExecute() {

    }

    @Override
    protected Void doExecute() throws Exception {
        return null;
    }
}
