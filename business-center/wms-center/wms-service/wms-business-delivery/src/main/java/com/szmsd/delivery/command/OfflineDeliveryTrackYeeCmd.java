package com.szmsd.delivery.command;

import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineResultDto;

import java.util.List;
import java.util.stream.Collectors;

public class OfflineDeliveryTrackYeeCmd extends BasicCommand<Integer> {

    private OfflineResultDto offlineResultDto;

    public OfflineDeliveryTrackYeeCmd(OfflineResultDto offlineResultDto){
        this.offlineResultDto = offlineResultDto;
    }

    @Override
    protected Integer doExecute() throws Exception {

        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();
        List<String> trackNos = offlineDeliveryImports.stream().map(OfflineDeliveryImport::getTrackingNo).collect(Collectors.toList());

        

        return null;
    }
}
