package com.szmsd.delivery.command;

import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.dto.OfflineResultDto;
import com.szmsd.finance.api.feign.RefundRequestFeignService;
import com.szmsd.finance.dto.RefundRequestListDTO;
import com.szmsd.finance.dto.RefundReviewDTO;

public class OfflineCreateCostCmd extends BasicCommand<Void> {

    private OfflineResultDto offlineResultDto;

    public OfflineCreateCostCmd(OfflineResultDto offlineResultDto){
        this.offlineResultDto = offlineResultDto;
    }

    @Override
    protected void beforeDoExecute() {

    }

    @Override
    protected Void doExecute() throws Exception {


        RefundRequestFeignService refundRequestFeignService = SpringUtils.getBean(RefundRequestFeignService.class);

        RefundRequestListDTO addDTO = new RefundRequestListDTO();

        R addRequest = refundRequestFeignService.add(addDTO);

        if(addRequest.getCode() != 200){

            RefundReviewDTO refundReviewDTO = new RefundReviewDTO();

            R approve = refundRequestFeignService.approve(refundReviewDTO);
        }

        return null;
    }
}
