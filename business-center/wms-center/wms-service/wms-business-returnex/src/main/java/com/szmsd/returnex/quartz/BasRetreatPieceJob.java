package com.szmsd.returnex.quartz;

import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.returnex.dto.ReturnExpressServiceAddDTO;
import com.szmsd.returnex.mapper.ReturnExpressMapper;
import com.szmsd.returnex.service.IReturnExpressService;
import com.szmsd.returnex.vo.ReturnBasRetreatPiece;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.List;

public class BasRetreatPieceJob extends QuartzJobBean {
   @Autowired
   private ReturnExpressMapper returnExpressMapper;
   @Autowired
   private IReturnExpressService iReturnExpressService;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<ReturnBasRetreatPiece> returnBasRetreatPieceList=returnExpressMapper.selectRetunBasRet();
        returnBasRetreatPieceList.forEach(x->{
            List<DelOutbound> delOutbounds=returnExpressMapper.selectRetunDleoutbound(x.getSn());
            delOutbounds.forEach(delOutbound->{
                ReturnExpressServiceAddDTO returnExpressAddDTO=new ReturnExpressServiceAddDTO();
                returnExpressAddDTO.setFromOrderNo(delOutbound.getOrderNo());
                returnExpressAddDTO.setScanCode(x.getSn());
                returnExpressAddDTO.setExpireTime(new Date());
                returnExpressAddDTO.setArrivalTime(x.getDateFinished());

                returnExpressAddDTO.setWarehouseCode(delOutbound.getWarehouseCode());
                returnExpressAddDTO.setSellerCode(delOutbound.getCustomCode());
                returnExpressAddDTO.setReturnSource("068003");
                iReturnExpressService.insertReturnExpressDetail(returnExpressAddDTO);


            });

            returnExpressMapper.updateReturnBasRetreat(x.getId());

        });
    }
}
