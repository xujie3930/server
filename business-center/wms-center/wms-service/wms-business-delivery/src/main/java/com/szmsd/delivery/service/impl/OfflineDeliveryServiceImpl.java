package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.szmsd.bas.api.domain.dto.BasRegionSelectListQueryDto;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.BigDecimalUtil;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.command.OfflineCreateCostCmd;
import com.szmsd.delivery.command.OfflineDeliveryCreateOrderCmd;
import com.szmsd.delivery.command.OfflineDeliveryReadExcelCmd;
import com.szmsd.delivery.command.OfflineDeliveryTrackYeeCmd;
import com.szmsd.delivery.convert.OfflineDeliveryConvert;
import com.szmsd.delivery.domain.OfflineCostImport;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.OfflineDeliveryStateEnum;
import com.szmsd.delivery.mapper.OfflineCostImportMapper;
import com.szmsd.delivery.mapper.OfflineDeliveryImportMapper;
import com.szmsd.delivery.service.OfflineDeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OfflineDeliveryServiceImpl  implements OfflineDeliveryService {

    @Autowired
    private OfflineDeliveryImportMapper offlineDeliveryImportMapper;

    @Autowired
    private OfflineCostImportMapper offlineCostImportMapper;

    @Autowired
    private BasRegionFeignService basRegionFeignService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R importExcel(MultipartFile file) {

        //step 1. ??????excel
        OfflineReadDto offlineReadDto = new OfflineDeliveryReadExcelCmd(file).execute();

        List<OfflineDeliveryExcelDto> deliveryExcelDtoList = offlineReadDto.getOfflineDeliveryExcelList();
        List<OfflineCostExcelDto> offlineCostExcelDtos = offlineReadDto.getOfflineCostExcelDtoList();
        if(CollectionUtils.isEmpty(deliveryExcelDtoList) || CollectionUtils.isEmpty(offlineCostExcelDtos)){
            return R.failed("????????????excel??????");
        }
        //step 2. ??????
        List<OfflineDeliveryImport> offlineDeliveryImports = OfflineDeliveryConvert.INSTANCE.toOfflineDeliveryImportList(deliveryExcelDtoList);
        List<OfflineCostImport> offlineCostImports = OfflineDeliveryConvert.INSTANCE.toOfflineCostImportList(offlineCostExcelDtos);

        String loginUser = SecurityUtils.getUsername();
        Date date = new Date();

        BasRegionSelectListQueryDto queryDto = new BasRegionSelectListQueryDto();
        queryDto.setType(1);
        R<List<BasRegionSelectListVO>> regionRs = basRegionFeignService.countryList(queryDto);
        if(regionRs == null || regionRs.getCode() != 200){
            throw new RuntimeException("??????????????????basRegion ?????????????????????????????????");
        }

        List<BasRegionSelectListVO> basRegionSelectListVOList = regionRs.getData();
        if(CollectionUtils.isEmpty(basRegionSelectListVOList)) {
            throw new RuntimeException("??????basRegion ???????????????????????????????????????");
        }

        Map<String, BasRegionSelectListVO> basRegionSelectListVOMap = basRegionSelectListVOList.stream().collect(Collectors.toMap(BasRegionSelectListVO::getAddressCode, v -> v));
        for(OfflineDeliveryImport excelDto : offlineDeliveryImports){
            BasRegionSelectListVO basRegionSelectListVO = basRegionSelectListVOMap.get(excelDto.getCountryCode());
            if(basRegionSelectListVO == null){
                throw new RuntimeException("?????????:"+excelDto.getTrackingNo()+",???????????????????????????????????????????????????????????????");
            }
            excelDto.setCountry(basRegionSelectListVO.getName());
        }

        //?????????????????????????????????????????????????????????
        Map<String,List<OfflineCostImport>> costExcelMap = offlineCostImports.stream().filter(item-> StringUtils.isNotBlank(item.getTrackingNo())).collect(Collectors.groupingBy(OfflineCostImport::getTrackingNo));
        for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){
            List<OfflineCostImport> trackingList = costExcelMap.get(deliveryImport.getTrackingNo());
            BigDecimal amount = BigDecimal.ZERO;
            for(OfflineCostImport excelDto1 : trackingList){
                amount = amount.add(excelDto1.getAmount());
            }
            BigDecimal amountDec = BigDecimalUtil.setScale(amount,BigDecimalUtil.PRICE_SCALE);
            deliveryImport.setAmount(amountDec);

            deliveryImport.setVersion(1L);
            deliveryImport.setCreateBy(loginUser);
            deliveryImport.setCreateTime(date);
            deliveryImport.setCreateByName(loginUser);
            deliveryImport.setDealStatus(OfflineDeliveryStateEnum.INIT.getCode());


            BigDecimal weight = BigDecimalUtil.setScale(deliveryImport.getWeight(),BigDecimalUtil.WEIGHT_SCALE);
            deliveryImport.setWeight(weight);

            BigDecimal calcWeight = BigDecimalUtil.setScale(deliveryImport.getCalcWeight(),BigDecimalUtil.WEIGHT_SCALE);
            deliveryImport.setCalcWeight(calcWeight);

            String specifications = deliveryImport.getSpecifications();
            if(StringUtils.isNotEmpty(specifications)){
                String s[] = specifications.split("\\*");

                if(s.length < 3){
                    throw new RuntimeException(deliveryImport.getTrackingNo()+"???????????????????????????");
                }

                deliveryImport.setLength(toDecimal(s[0]));
                deliveryImport.setWidth(toDecimal(s[1]));
                deliveryImport.setHeight(toDecimal(s[2]));
            }
        }

        for(OfflineCostImport costImport : offlineCostImports){
            costImport.setCreateBy(loginUser);
            costImport.setCreateTime(date);
            costImport.setCreateByName(loginUser);
            BigDecimal amount = BigDecimalUtil.setScale(costImport.getAmount(),BigDecimalUtil.PRICE_SCALE);
            costImport.setAmount(amount);
        }

        //step 3. ??????????????????
        int batchSaveDelivery = offlineDeliveryImportMapper.saveBatch(offlineDeliveryImports);
        int batchSaveCost = offlineCostImportMapper.saveBatch(offlineCostImports);
        if(batchSaveDelivery > 0 && batchSaveCost > 0){
            return R.ok();
        }

        return R.failed("????????????");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R dealOfflineDelivery() {

        //step 1. ??????INIT ?????????????????????
        OfflineResultDto offlineResultDto = this.selectOfflineData();
        if(offlineResultDto == null){
            return R.failed("?????????");
        }

        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();

        List<List<OfflineDeliveryImport>> partition = Lists.partition(offlineDeliveryImports,10);

        for(List<OfflineDeliveryImport> ps : partition) {

            OfflineResultDto offlineResultDtops = new OfflineResultDto();

            List<String> trackNoList = ps.stream().map(OfflineDeliveryImport::getTrackingNo).collect(Collectors.toList());
            List<OfflineCostImport> offlineCostImports = offlineCostImportMapper.selectList(Wrappers.<OfflineCostImport>query().lambda().in(OfflineCostImport::getTrackingNo, trackNoList));

            offlineResultDtops.setOfflineCostImportList(offlineCostImports);
            offlineResultDtops.setOfflineDeliveryImports(ps);

            //step 2. ????????????????????????offline ???????????????CREATE_ORDER
            OfflineResultDto createOrder = new OfflineDeliveryCreateOrderCmd(offlineResultDtops).execute();
            if (createOrder == null) {
                return R.failed("??????????????????");
            }

            log.info("????????????????????????offline ???????????????CREATE_ORDER???{}");

            //step 3. ????????????????????????????????????????????????
            int createCost = new OfflineCreateCostCmd(createOrder).execute();
            if (createCost != 1) {
                return R.failed("????????????????????????");
            }

            log.info("???????????????????????????{}");

            //step 4. ??????TY
            int trackYee = new OfflineDeliveryTrackYeeCmd(createOrder).execute();
            if (trackYee != 1) {
                return R.failed("??????TY??????");
            }

            log.info("??????TY???{}", trackYee);

            //step 5.??????
            this.complete(createOrder);

            log.info("dealOfflineDelivery ?????? ???{}");
        }

        return R.ok();
    }

    private void complete(OfflineResultDto createOrder) {

        OfflineDeliveryImportMapper importMapper = SpringUtils.getBean(OfflineDeliveryImportMapper.class);
        List<OfflineDeliveryImport> offlineDeliveryImports = createOrder.getOfflineDeliveryImports();

        List<OfflineImportDto> updateData = new ArrayList<>();
        for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){

            OfflineImportDto offlineImportDto = new OfflineImportDto();
            offlineImportDto.setTrackingNo(deliveryImport.getTrackingNo());
            offlineImportDto.setId(deliveryImport.getId());
            offlineImportDto.setDealStatus(OfflineDeliveryStateEnum.PUSH_TY.getCode());
            updateData.add(offlineImportDto);
        }

        if(CollectionUtils.isNotEmpty(updateData)) {
            importMapper.updateDealState(updateData);
//            for(OfflineImportDto importDto : updateData){
//
//                OfflineDeliveryImport offlineDeliveryImport = new OfflineDeliveryImport();
//                offlineDeliveryImport.setId(importDto.getId());
//                offlineDeliveryImport.setTrackingNo(importDto.getTrackingNo());
//                offlineDeliveryImport.setDealStatus(importDto.getDealStatus());
//
//                offlineDeliveryImportMapper.updateById(offlineDeliveryImport);
//            }
        }
    }


    private OfflineResultDto selectOfflineData(){

        List<OfflineDeliveryImport> offlineDeliveryImports = offlineDeliveryImportMapper.selectList(Wrappers.<OfflineDeliveryImport>query().lambda()
                .eq(OfflineDeliveryImport::getDealStatus,OfflineDeliveryStateEnum.INIT.getCode())
        );

        if(CollectionUtils.isEmpty(offlineDeliveryImports)){
            return null;
        }

        List<String> trackNoList = offlineDeliveryImports.stream().map(OfflineDeliveryImport::getTrackingNo).collect(Collectors.toList());

        List<List<String>> trackNoPartions = Lists.partition(trackNoList,200);
        List<OfflineCostImport> offlineCostImportList = new ArrayList<>();

//        for(List<String> orderNos : trackNoPartions) {
//
//            List<OfflineCostImport> offlineCostImports = offlineCostImportMapper.selectList(Wrappers.<OfflineCostImport>query().lambda().in(OfflineCostImport::getTrackingNo, orderNos));
//            offlineCostImportList.addAll(offlineCostImports);
//        }

        OfflineResultDto offlineResultDto = new OfflineResultDto();
        offlineResultDto.setOfflineDeliveryImports(offlineDeliveryImports);
        offlineResultDto.setOfflineCostImportList(offlineCostImportList);

        return offlineResultDto;
    }


    private BigDecimal toDecimal(String s){
        if(StringUtils.isEmpty(s)){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(s);
    }

}
