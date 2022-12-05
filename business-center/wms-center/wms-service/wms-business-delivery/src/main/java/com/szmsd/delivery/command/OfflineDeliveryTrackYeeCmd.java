package com.szmsd.delivery.command;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineResultDto;
import com.szmsd.delivery.service.IDelOutboundService;

import java.util.ArrayList;
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

        IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);

        List<DelOutbound> delOutboundList = iDelOutboundService.list(Wrappers.<DelOutbound>query().lambda().in(DelOutbound::getTrackingNo,trackNos));

        if(CollectionUtils.isEmpty(delOutboundList)){
            return 0;
        }

        List<String> ids = new ArrayList<>();
        for(DelOutbound delOutbound : delOutboundList){
            Long id = delOutbound.getId();
            ids.add(id.toString());
        }

        try {
            iDelOutboundService.manualTrackingYee(ids);
        }catch (Exception e){
            return 0;
        }
        return 1;
    }
}
