package com.szmsd.delivery.command;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.UpdateWeightDelOutboundDto;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.event.DelOutUpdWeightEvent;
import com.szmsd.delivery.event.EventUtil;
import com.szmsd.delivery.service.IDelOutboundService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OutboundUpdWeightCmd extends BasicCommand<Boolean> {

    private UpdateWeightDelOutboundDto dto;

    public OutboundUpdWeightCmd(UpdateWeightDelOutboundDto dto){
        this.dto = dto;
    }

    @Override
    protected void beforeDoExecute() {

    }

    @Override
    protected Boolean doExecute() throws Exception {

        IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);

        String orderNo = dto.getOrderNo();

        LambdaQueryWrapper<DelOutbound> queryWrapper = new LambdaQueryWrapper<DelOutbound>();
        queryWrapper.eq(DelOutbound::getSellerCode, dto.getCustomCode());
        queryWrapper.eq(DelOutbound::getOrderNo, orderNo);
        DelOutbound data = iDelOutboundService.getOne(queryWrapper);
        if(data == null){
            throw new CommonException("400", "该客户下订单不存在");
        }

        if (
            DelOutboundStateEnum.PROCESSING.getCode().equals(data.getState())
                    || DelOutboundStateEnum.NOTIFY_WHSE_PROCESSING.getCode().equals(data.getState())
                    || DelOutboundStateEnum.WHSE_PROCESSING.getCode().equals(data.getState())
                    || DelOutboundStateEnum.WHSE_COMPLETED.getCode().equals(data.getState())
                    || DelOutboundStateEnum.COMPLETED.getCode().equals(data.getState())
        ) {
            throw new CommonException("400", "单据不能修改");
        }

        org.springframework.beans.BeanUtils.copyProperties(dto, data);
        boolean upd = iDelOutboundService.updateById(data);

        if(upd){

            log.info("开始DelOutUpdWeightEvent：{}",orderNo);
            DelOutUpdWeightEvent delOutUpdWeightEvent = new DelOutUpdWeightEvent(orderNo);
            EventUtil.publishEvent(delOutUpdWeightEvent);
        }

        return upd;
    }
}
