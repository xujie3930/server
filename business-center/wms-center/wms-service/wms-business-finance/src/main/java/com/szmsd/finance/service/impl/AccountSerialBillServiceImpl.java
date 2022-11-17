package com.szmsd.finance.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.domain.AccountSerialBillTotalVO;
import com.szmsd.finance.domain.ChargeRelation;
import com.szmsd.finance.dto.AccountBalanceBillCurrencyVO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.AccountSerialBillNatureDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.mapper.AccountSerialBillMapper;
import com.szmsd.finance.mapper.ChargeRelationMapper;
import com.szmsd.finance.service.IAccountSerialBillService;
import com.szmsd.finance.service.ISysDictDataService;
import com.szmsd.finance.vo.AccountSerialBillExcelVO;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountSerialBillServiceImpl extends ServiceImpl<AccountSerialBillMapper, AccountSerialBill> implements IAccountSerialBillService {

    @Resource
    private AccountSerialBillMapper accountSerialBillMapper;

    @Resource
    private ISysDictDataService sysDictDataService;

    @Resource
    private ChargeRelationMapper chargeRelationMapper;

    @Override
//    @DataScope("cus_code")
    public List<AccountSerialBill> listPage(AccountSerialBillDTO dto) {
//        QueryWrapper<Object> query1 = Wrappers.query();
//        query1.eq("a.cretea",);
//        LambdaQueryWrapper<AccountSerialBill> query = Wrappers.lambdaQuery();
//        query.in(CollectionUtils.isNotEmpty(dto.getNoList()), AccountSerialBill::getNo, dto.getNoList());
//        query.in(CollectionUtils.isNotEmpty(dto.getCusCodeList()), AccountSerialBill::getCusCode, dto.getCusCodeList());
//        query.in(CollectionUtils.isNotEmpty(dto.getProductCodeList()), AccountSerialBill::getProductCode, dto.getProductCodeList());
//
//
//        if (StringUtils.isNotBlank(dto.getChargeType())) {
//            query.eq(AccountSerialBill::getChargeType, dto.getChargeType());
//        }
//        if (StringUtils.isNotBlank(dto.getWarehouseCode())) {
//            query.eq(AccountSerialBill::getWarehouseCode, dto.getWarehouseCode());
//        }
//        if (StringUtils.isNotBlank(dto.getCurrencyCode())) {
//            query.eq(AccountSerialBill::getCurrencyCode, dto.getCurrencyCode());
//        }
//        if (StringUtils.isNotBlank(dto.getBusinessCategory())) {
//            query.eq(AccountSerialBill::getBusinessCategory, dto.getBusinessCategory());
//        }
//        if (StringUtils.isNotBlank(dto.getProductCategory())) {
//            query.eq(AccountSerialBill::getProductCategory, dto.getProductCategory());
//        }
//        if (StringUtils.isNotBlank(dto.getChargeCategory())) {
//            query.eq(AccountSerialBill::getChargeCategory, dto.getChargeCategory());
//        }
//        if (StringUtils.isNotBlank(dto.getCreateTimeStart())) {
//            query.ge(AccountSerialBill::getCreateTime, dto.getCreateTimeStart());
//        }
//        if (StringUtils.isNotBlank(dto.getCreateTimeEnd())) {
//            query.le(AccountSerialBill::getCreateTime, dto.getCreateTimeEnd());
//        }
//        if (StringUtils.isNotBlank(dto.getIds())) {
//            query.in(AccountSerialBill::getId, (Object[]) dto.getIds().split(","));
//        }
        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
            if (com.szmsd.common.core.utils.StringUtils.isEmpty(dto.getCusCode())) {
                dto.setCusCode(cusCode);
            }
        }

        if(StringUtils.isNotBlank(dto.getCreateTimeStart())) {
            String billStartTime = dto.getCreateTimeStart() + " 00:00:00";
            dto.setCreateTimeStart(billStartTime);
        }

        if(StringUtils.isNotBlank(dto.getCreateTimeEnd())) {
            String billEndTime = dto.getCreateTimeEnd() + " 23:59:59";
            dto.setCreateTimeEnd(billEndTime);
        }

        if(StringUtils.isNotBlank(dto.getPaymentTimeStart())) {
            String billStartTime = dto.getPaymentTimeStart() + " 00:00:00";
            dto.setPaymentTimeStart(billStartTime);
        }
        if(StringUtils.isNotBlank(dto.getPaymentTimeEnd())) {
            String billEndTime = dto.getPaymentTimeEnd() + " 23:59:59";
            dto.setPaymentTimeEnd(billEndTime);
        }

        List<AccountSerialBill> accountSerialBills = accountSerialBillMapper.selectPageList(dto);
        // 修改下单时间等信息
        showProcess(accountSerialBills);
        return accountSerialBills;
    }

    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;
    @Resource
    private DelOutboundFeignService delOutboundFeignService;

    @Data
    @AllArgsConstructor
    private static class ListProcess {
        private Date orderTime;
        private String no;
        private String warehouseCode;

    }

    /**
     * 账单展示仓库 下单时间
     *
     * @param accountSerialBills
     */
    public void showProcess(List<AccountSerialBill> accountSerialBills) {
        ArrayList<AccountSerialBill> resultList = new ArrayList<>();
        List<AccountSerialBill> noList = accountSerialBills.stream().filter(x -> StringUtils.isNotBlank(x.getNo())).collect(Collectors.toList());
        // 查询下单时间 结算时间，列表时间展示
        CompletableFuture<List<ListProcess>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<AccountSerialBill> rk = noList.parallelStream().filter(x -> x.getNo().startsWith("RK")).distinct().collect(Collectors.toList());
            String rkNo = rk.stream().map(AccountSerialBill::getNo).distinct().collect(Collectors.joining(","));

            List<ListProcess> result = new ArrayList<>();
            if (StringUtils.isNotBlank(rkNo)) {
                InboundReceiptQueryDTO inboundReceiptQueryDTO = new InboundReceiptQueryDTO();
                inboundReceiptQueryDTO.setWarehouseNo(rkNo);
                TableDataInfo<InboundReceiptVO> inboundReceiptVOTableDataInfo = inboundReceiptFeignService.postPage(inboundReceiptQueryDTO);
                if (inboundReceiptVOTableDataInfo.getCode() == HttpStatus.SUCCESS) {
                    List<InboundReceiptVO> ckPage = inboundReceiptVOTableDataInfo.getRows();
                    Map<String, InboundReceiptVO> collect = ckPage.stream().collect(Collectors.toMap(InboundReceiptVO::getWarehouseNo, x -> x, (x1, x2) -> x1));
                    result = rk.stream().map(x -> {
                        InboundReceiptVO inboundReceiptVO = collect.get(x.getNo());
                        if (Objects.nonNull(inboundReceiptVO)) {
                            Date createTime = inboundReceiptVO.getCreateTime();
                            String warehouseCode = inboundReceiptVO.getWarehouseCode();
                            return new ListProcess(createTime, x.getNo(), warehouseCode);
                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
                }
            }
            return result;
        });


        CompletableFuture<List<ListProcess>> listCompletableFuture1 = CompletableFuture.supplyAsync(() -> {
            List<AccountSerialBill> ck = noList.parallelStream().filter(x -> x.getNo().startsWith("CK")).distinct().collect(Collectors.toList());
            String ckNo = ck.stream().map(AccountSerialBill::getNo).distinct().collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(ckNo)) {
                DelOutboundListQueryDto delOutboundListQueryDto = new DelOutboundListQueryDto();
                delOutboundListQueryDto.setOrderNo(ckNo);
                TableDataInfo<DelOutboundListVO> page = delOutboundFeignService.page(delOutboundListQueryDto);
                if (page.getCode() == HttpStatus.SUCCESS) {
                    List<DelOutboundListVO> rows = page.getRows();
                    Map<String, DelOutboundListVO> collect = rows.stream().collect(Collectors.toMap(DelOutboundListVO::getOrderNo, x -> x, (x1, x2) -> x1));
                    return ck.stream().map(x -> {
                        DelOutboundListVO delOutboundListVO = collect.get(x.getNo());
                        if (Objects.nonNull(delOutboundListVO)) {
                            Date createTime = delOutboundListVO.getCreateTime();
                            String warehouseCode = delOutboundListVO.getWarehouseCode();
                            x.setOrderTime(createTime);
                            x.setWarehouseCode(warehouseCode);
                            return new ListProcess(createTime, x.getNo(), warehouseCode);
                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
                }
            }
            return new ArrayList<>();
        });

        Void join = CompletableFuture.allOf(listCompletableFuture1, listCompletableFuture).join();

        List<ListProcess> listProcesses = listCompletableFuture.join();
        List<ListProcess> listProcesses1 = listCompletableFuture1.join();
        ArrayList<ListProcess> queryResult = new ArrayList<>();
        queryResult.addAll(listProcesses);
        queryResult.addAll(listProcesses1);
        Map<String, ListProcess> queryResultMap = queryResult.stream().collect(Collectors.toMap(ListProcess::getNo, x -> x, (x1, x2) -> x1));
        List<String> positiveNumber = Arrays.asList("线下充值", "退费", "优惠","赔偿");// 正数

        List<String> negativeNumber = Arrays.asList("补收", "增值消费"); //为负数
        accountSerialBills.forEach(x -> {
            if (StringUtils.isNotBlank(x.getNo())) {
                Optional.ofNullable(queryResultMap.get(x.getNo())).ifPresent(queryResultNo -> {
                    x.setWarehouseCode(queryResultNo.getWarehouseCode());
                    x.setOrderTime(queryResultNo.getOrderTime());
                });
            }
            if (null == x.getAmount()) x.setAmount(BigDecimal.ZERO);
            // 负数
            String businessCategory = x.getBusinessCategory();
            //费用类型，费用类型为汇率转换的也不能去转换负数
            String chargeType=x.getChargeType();
            if (StringUtils.isNotBlank(businessCategory) && positiveNumber.contains(businessCategory)) {
                x.setAmount(x.getAmount().abs());
            } else if (!chargeType.equals("汇率转换充值")){
                Optional.ofNullable(x.getAmount()).ifPresent(amount -> x.setAmount(amount.abs().negate()));
            }

        });

    }

    @Override
    public int add(AccountSerialBillDTO dto) {
        AccountSerialBill accountSerialBill = BeanMapperUtil.map(dto, AccountSerialBill.class);
        if (StringUtils.isBlank(accountSerialBill.getWarehouseName())) {
            accountSerialBill.setWarehouseName(sysDictDataService.getWarehouseNameByCode(accountSerialBill.getWarehouseCode()));
        }
        if (StringUtils.isBlank(accountSerialBill.getCurrencyName())) {
            accountSerialBill.setCurrencyName(sysDictDataService.getCurrencyNameByCode(dto.getCurrencyCode()));
        }
        accountSerialBill.setBusinessCategory(accountSerialBill.getChargeCategory());//性质列内容，同费用类别
        //单号不为空的时候
        if (StringUtils.isNotBlank(dto.getNo())){
           DelOutbound delOutbound=accountSerialBillMapper.selectDelOutbound(dto.getNo());
           if (delOutbound!=null){
               if (delOutbound.getId()!=null){
                   accountSerialBill.setRefNo(delOutbound.getRefNo());
                   accountSerialBill.setShipmentService(delOutbound.getShipmentService());
                   accountSerialBill.setWeight(delOutbound.getWeight());
                   accountSerialBill.setCalcWeight(delOutbound.getCalcWeight());
                   accountSerialBill.setSpecifications(delOutbound.getSpecifications());
               }
           }
        }

        String serialNumber = this.createSerialNumber();
        accountSerialBill.setSerialNumber(serialNumber);

        return accountSerialBillMapper.insert(accountSerialBill);
    }

    private String createSerialNumber(){

        String s = DateUtils.dateTime();
        String randomNums = RandomUtil.randomNumbers(8);

        return s + randomNums;
    }

    @Override
    public boolean saveBatch(List<AccountSerialBillDTO> dto) {
        List<AccountSerialBill> accountSerialBill = BeanMapperUtil.mapList(dto, AccountSerialBill.class);
        List<AccountSerialBill> collect = accountSerialBill.stream().map(value -> {
            if (StringUtils.isBlank(value.getWarehouseName())) {
                value.setWarehouseName(sysDictDataService.getWarehouseNameByCode(value.getWarehouseCode()));
            }
            if (StringUtils.isBlank(value.getCurrencyName())) {
                value.setCurrencyName(sysDictDataService.getCurrencyNameByCode(value.getCurrencyCode()));
            }
            value.setBusinessCategory(value.getChargeCategory());//性质列内容，同费用类别
            //单号不为空的时候
            if (StringUtils.isNotBlank(value.getNo())){
                DelOutbound delOutbound=accountSerialBillMapper.selectDelOutbound(value.getNo());
                if (delOutbound!=null) {
                    if (delOutbound.getId() != null) {
                        value.setRefNo(delOutbound.getRefNo());
                        value.setShipmentService(delOutbound.getShipmentService());
                        value.setWeight(delOutbound.getWeight());
                        value.setCalcWeight(delOutbound.getCalcWeight());
                        value.setSpecifications(delOutbound.getSpecifications());
                    }
                }
            }

            if(StringUtils.isEmpty(value.getAmazonLogisticsRouteId()) || value.getAmazonLogisticsRouteId().equals("null")){
                value.setAmazonLogisticsRouteId(null);
            }

            String serialNumber = this.createSerialNumber();
            value.setSerialNumber(serialNumber);

            return value;
        }).collect(Collectors.toList());
        boolean b = this.saveBatch(collect);
        if (!b){
            log.error("saveBatch() insert failed. {}", accountSerialBill);
        }
        return b;
    }

    /**
     * 幂等校验 校验重复扣费 ： 单号—发生额-业务类型
     *
     * @param dto
     * @return true : 已存在
     */
    @Override
    public boolean checkForDuplicateCharges(CustPayDTO dto) {
        List<AccountSerialBillDTO> serialBillInfoList = dto.getSerialBillInfoList();
        if (CollectionUtils.isEmpty(serialBillInfoList)){
            return false;
        }
        AccountSerialBillDTO accountSerialBillDTO = serialBillInfoList.get(0);

        Integer count = baseMapper.selectCount(Wrappers.<AccountSerialBill>lambdaQuery()
                .eq(AccountSerialBill::getNo, dto.getNo())
                .eq(AccountSerialBill::getAmount, accountSerialBillDTO.getAmount())
                .eq(AccountSerialBill::getChargeCategory, accountSerialBillDTO.getChargeCategory())
        );

        boolean flag = !count.equals(0);

        return flag;
    }

    @Override
    public void executeSerialBillNature() {

        Integer count = accountSerialBillMapper.selectBillOutbountCount();

        if(count == 0){
            return;
        }

        Integer pageSize = 5000;

        Integer totalPage = (count + pageSize -1) / pageSize;

        for(int i = 0;i<totalPage;i++){

            List<AccountSerialBillNatureDTO> accountSerialBillDTOList = accountSerialBillMapper.selectBillOutbount(i,pageSize);

            for(AccountSerialBillNatureDTO billNatureDTO : accountSerialBillDTOList){

                String chargeCategory = billNatureDTO.getChargeCategory();
                String businessCategory = billNatureDTO.getBusinessCategory();
                String orderType = billNatureDTO.getOrderType();
                Long id = billNatureDTO.getId();

                if(StringUtils.isBlank(businessCategory) || StringUtils.isBlank(orderType)){
                    continue;
                }

                List<ChargeRelation> chargeRelationList = chargeRelationMapper.findChargeRelation(businessCategory,orderType);

                if(CollectionUtils.isNotEmpty(chargeRelationList)){

                    ChargeRelation chargeRelation = chargeRelationList.get(0);

                    AccountSerialBill accountSerialBill = new AccountSerialBill();
                    accountSerialBill.setId(id);
                    accountSerialBill.setNature(chargeRelation.getNature());
                    accountSerialBill.setBusinessType(chargeRelation.getBusinessType());
                    accountSerialBill.setChargeCategoryChange(chargeRelation.getChargeCategoryChange());

                    accountSerialBillMapper.updateById(accountSerialBill);
                }
            }

        }

    }

    @Override
    public List<AccountBalanceBillCurrencyVO> findBillCurrencyData(AccountSerialBillDTO dto) {

        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
            if (com.szmsd.common.core.utils.StringUtils.isEmpty(dto.getCusCode())) {
                dto.setCusCode(cusCode);
            }
        }

        if(StringUtils.isNotBlank(dto.getCreateTimeStart())) {
            String billStartTime = dto.getCreateTimeStart() + " 00:00:00";
            dto.setCreateTimeStart(billStartTime);
        }

        if(StringUtils.isNotBlank(dto.getCreateTimeEnd())) {
            String billEndTime = dto.getCreateTimeEnd() + " 23:59:59";
            dto.setCreateTimeEnd(billEndTime);
        }

        if(StringUtils.isNotBlank(dto.getPaymentTimeStart())) {
            String billStartTime = dto.getPaymentTimeStart() + " 00:00:00";
            dto.setPaymentTimeStart(billStartTime);
        }
        if(StringUtils.isNotBlank(dto.getPaymentTimeEnd())) {
            String billEndTime = dto.getPaymentTimeEnd() + " 23:59:59";
            dto.setPaymentTimeEnd(billEndTime);
        }

        return accountSerialBillMapper.findBillCurrencyData(dto);
    }

    @Override
    public void exportBillTotal(HttpServletResponse response, AccountSerialBillDTO dto) {

        if(StringUtils.isNotBlank(dto.getCreateTimeStart())) {
            String billStartTime = dto.getCreateTimeStart() + " 00:00:00";
            dto.setCreateTimeStart(billStartTime);
        }

        if(StringUtils.isNotBlank(dto.getCreateTimeEnd())) {
            String billEndTime = dto.getCreateTimeEnd() + " 23:59:59";
            dto.setCreateTimeEnd(billEndTime);
        }

        if(StringUtils.isNotBlank(dto.getPaymentTimeStart())) {
            String billStartTime = dto.getPaymentTimeStart() + " 00:00:00";
            dto.setPaymentTimeStart(billStartTime);
        }
        if(StringUtils.isNotBlank(dto.getPaymentTimeEnd())) {
            String billEndTime = dto.getPaymentTimeEnd() + " 23:59:59";
            dto.setPaymentTimeEnd(billEndTime);
        }

        List<AccountSerialBillTotalVO> accountSerialBillTotalVOS = accountSerialBillMapper.selectBillTotal(dto);

        ExcelUtil<AccountSerialBillTotalVO> util = new ExcelUtil<>(AccountSerialBillTotalVO.class);
        util.exportExcel(response,accountSerialBillTotalVOS,"业务明细汇总");

    }

    @Override
    public List<AccountSerialBillExcelVO> exportData(AccountSerialBillDTO dto) {

        if(StringUtils.isNotBlank(dto.getCreateTimeStart())) {
            String billStartTime = dto.getCreateTimeStart() + " 00:00:00";
            dto.setCreateTimeStart(billStartTime);
        }

        if(StringUtils.isNotBlank(dto.getCreateTimeEnd())) {
            String billEndTime = dto.getCreateTimeEnd() + " 23:59:59";
            dto.setCreateTimeEnd(billEndTime);
        }

        if(StringUtils.isNotBlank(dto.getPaymentTimeStart())) {
            String billStartTime = dto.getPaymentTimeStart() + " 00:00:00";
            dto.setPaymentTimeStart(billStartTime);
        }
        if(StringUtils.isNotBlank(dto.getPaymentTimeEnd())) {
            String billEndTime = dto.getPaymentTimeEnd() + " 23:59:59";
            dto.setPaymentTimeEnd(billEndTime);
        }

        List<AccountSerialBillExcelVO> accountSerialBillTotalVOS = accountSerialBillMapper.exportData(dto);

        return accountSerialBillTotalVOS;
    }


}
