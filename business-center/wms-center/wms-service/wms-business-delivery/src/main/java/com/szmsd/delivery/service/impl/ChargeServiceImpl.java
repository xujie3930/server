package com.szmsd.delivery.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.command.*;
import com.szmsd.delivery.domain.ChargeImport;
import com.szmsd.delivery.dto.ChargePricingOrderMsgDto;
import com.szmsd.delivery.dto.ChargePricingResultDto;
import com.szmsd.delivery.enums.ChargeImportStateEnum;
import com.szmsd.delivery.mapper.ChargeImportMapper;
import com.szmsd.delivery.service.ChargeService;
import com.szmsd.http.dto.ChargeWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChargeServiceImpl extends ServiceImpl<ChargeImportMapper, ChargeImport> implements ChargeService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public R importExcel(MultipartFile file) {

        List<ChargeImport> chargeImportList = new ChargeReadExcelCmd(file).execute();
        boolean save = super.saveBatch(chargeImportList);
        if(save){
            return R.ok();
        }
        return R.failed("导入失败");
    }

    @Override
    public R doSecondCharge() {

        //step 1. 获取导入数据
        List<ChargeImport> chargeImportList = this.selectChargeImport();

        if(CollectionUtils.isEmpty(chargeImportList)){
            return R.failed("无数据");
        }

        log.info("step 1.获取导入数据");

        //step 2. 更新出库单数据，尺寸、重量、报价表
        List<String> orderNos = new ChargeUpdateOutboundCmd(chargeImportList).execute();
        log.info("step 2.更新出库单数据，尺寸、重量:{}",JSON.toJSONString(orderNos));

        //step 3. PRC 重新计费
        ChargePricingResultDto chargePricingResultDto = new ChargePricingCmd(orderNos).execute();
        log.info("step 3.PRC 重新计费");

        List<ChargePricingOrderMsgDto> chargePricingOrderMsgDtoList = chargePricingResultDto.getSuccessOrders();
        List<String> orderNoPrcs = chargePricingOrderMsgDtoList.stream().map(ChargePricingOrderMsgDto::getOrderNo).collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(orderNoPrcs)) {

            //step 4. 旧数据退费
            new ChargeRefundCmd(orderNoPrcs).execute();
            log.info("step 4. 旧数据退费");
            Map<String, ChargeWrapper> chargeWrapperMap = chargePricingResultDto.getChargeWrapperMap();
            //step 5. 重新扣费
            List<String> completedOrderList = new ChargefeeDeductionCmd(orderNoPrcs,chargeWrapperMap).execute();
            log.info("step 3. 重新扣费{}", JSON.toJSONString(completedOrderList));

            //step 6.更新已经完成
            this.chargeComplete(completedOrderList);
            log.info("step 3. 更新已经完成");
        }

        return R.ok();
    }

    private void chargeComplete(List<String> completedOrderList) {

        if(CollectionUtils.isEmpty(completedOrderList)){
            return;
        }

        ChargeImportMapper chargeImportMapper = SpringUtils.getBean(ChargeImportMapper.class);

        List<ChargePricingOrderMsgDto> allData = new ArrayList<>();

        for(String s : completedOrderList){
            ChargePricingOrderMsgDto chargePricingOrderMsgDto = new ChargePricingOrderMsgDto();
            chargePricingOrderMsgDto.setState(ChargeImportStateEnum.COMPLETED.getCode());
            chargePricingOrderMsgDto.setOrderNo(s);
            allData.add(chargePricingOrderMsgDto);
        }

        if(CollectionUtils.isNotEmpty(allData)) {
            chargeImportMapper.batchUpd(allData);
        }
    }

    private List<ChargeImport> selectChargeImport() {

        List<ChargeImport> chargeImportList = baseMapper.selectList(Wrappers.<ChargeImport>query().lambda()
                .eq(ChargeImport::getState, ChargeImportStateEnum.INIT.getCode())
                .eq(ChargeImport::getDelFlag,0)
        );

        return chargeImportList;
    }
}
