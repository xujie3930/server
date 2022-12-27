package com.szmsd.delivery.command;

import com.google.common.collect.Lists;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.dto.ChargePricingOrderMsgDto;
import com.szmsd.delivery.enums.ChargeImportStateEnum;
import com.szmsd.delivery.mapper.ChargeImportMapper;
import com.szmsd.finance.api.feign.AccountSerialBillFeignService;
import com.szmsd.finance.api.feign.RefundRequestFeignService;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.enums.PrcStateEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ChargeRefundCmd extends BasicCommand<List<AccountSerialBill>> {

    private List<String> orderNos;

    public ChargeRefundCmd(List<String> orderNos){
        this.orderNos = orderNos;
    }

    @Override
    protected void beforeDoExecute() {

    }

    @Override
    protected List<AccountSerialBill> doExecute() throws Exception {

        List<AccountSerialBill> accountSerialBillResultData = new ArrayList<>();

        AccountSerialBillFeignService accountSerialBillFeignService = SpringUtils.getBean(AccountSerialBillFeignService.class);
        RefundRequestFeignService refundRequestFeignService = SpringUtils.getBean(RefundRequestFeignService.class);
        //每次查询1000条
        List<List<String>> orderParts = Lists.partition(orderNos,1000);

        for(List<String> orderNoList : orderParts){
            AccountOrderQueryDTO accountOrderQueryDTO = new AccountOrderQueryDTO();
            accountOrderQueryDTO.setOrderNoList(orderNoList);
            accountOrderQueryDTO.setPrcState(PrcStateEnum.PRC.getCode());
            R<List<AccountSerialBill>> resultRs = accountSerialBillFeignService.selectAccountPrcSerialBill(accountOrderQueryDTO);

            if(resultRs.getCode() == 200 && CollectionUtils.isNotEmpty(resultRs.getData())){
                List<AccountSerialBill> accountSerialBills = resultRs.getData();
                RefundRequestListAutoDTO refundReviewDTO = this.generatorRefundAutoData(accountSerialBills);
                //退费
                R autoRefundRs = refundRequestFeignService.autoRefund(refundReviewDTO);

                if(autoRefundRs.getCode() == 200){
                    accountSerialBillResultData.addAll(accountSerialBills);
                }
            }
        }

        return accountSerialBillResultData;
    }

    @Override
    protected void afterExecuted(List<AccountSerialBill> result) throws Exception {

        ChargeImportMapper chargeImportMapper = SpringUtils.getBean(ChargeImportMapper.class);

        List<ChargePricingOrderMsgDto> allData = new ArrayList<>();

        for(AccountSerialBill s : result){
            ChargePricingOrderMsgDto chargePricingOrderMsgDto = new ChargePricingOrderMsgDto();
            chargePricingOrderMsgDto.setState(ChargeImportStateEnum.REFUND_COST.getCode());
            chargePricingOrderMsgDto.setOrderNo(s.getNo());
            allData.add(chargePricingOrderMsgDto);
        }

        if(CollectionUtils.isNotEmpty(allData)) {
            chargeImportMapper.batchUpd(allData);
        }

        super.afterExecuted(result);
    }

    private RefundRequestListAutoDTO generatorRefundAutoData(List<AccountSerialBill> accountSerialBills) {

        RefundRequestListAutoDTO autoDTO = new RefundRequestListAutoDTO();
        List<RefundRequestAutoDTO> refundRequestList = new ArrayList<>();


        for(AccountSerialBill accountSerialBill : accountSerialBills){

            RefundRequestAutoDTO refundRequestAutoDTO = new RefundRequestAutoDTO();
            refundRequestAutoDTO.setOrderNo(accountSerialBill.getNo());
            refundRequestAutoDTO.setAmount(accountSerialBill.getAmount());
            refundRequestAutoDTO.setCusCode(accountSerialBill.getCusCode());
            refundRequestAutoDTO.setCurrencyCode(accountSerialBill.getCurrencyCode());
            refundRequestAutoDTO.setCurrencyName(accountSerialBill.getCurrencyName());
            refundRequestAutoDTO.setRemark(accountSerialBill.getRemark());
            refundRequestAutoDTO.setTreatmentProperties("退费");
            refundRequestAutoDTO.setTreatmentPropertiesCode("025001");
            refundRequestAutoDTO.setWarehouseCode(accountSerialBill.getWarehouseCode());
            refundRequestAutoDTO.setWarehouseName(accountSerialBill.getWarehouseName());
            refundRequestAutoDTO.setBusinessTypeCode("转运");
            refundRequestAutoDTO.setBusinessTypeCode("012001");
            refundRequestAutoDTO.setBusinessDetailsCode(accountSerialBill.getSerialNumber());
            refundRequestAutoDTO.setFeeTypeCode(accountSerialBill.getChargeType());
            refundRequestAutoDTO.setFeeTypeName(accountSerialBill.getChargeType());
            refundRequestAutoDTO.setFeeCategoryCode(accountSerialBill.getChargeCategory());
            refundRequestAutoDTO.setFeeCategoryName(accountSerialBill.getChargeCategory());
            refundRequestAutoDTO.setPrcState(PrcStateEnum.SECOND.getCode());
            refundRequestAutoDTO.setAccountSerialBillId(accountSerialBill.getId());

            refundRequestList.add(refundRequestAutoDTO);
        }

        autoDTO.setRefundRequestList(refundRequestList);
        return autoDTO;
    }
}
