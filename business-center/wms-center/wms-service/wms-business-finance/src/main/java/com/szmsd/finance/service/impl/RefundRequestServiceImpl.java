package com.szmsd.finance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.compont.ConfigData;
import com.szmsd.finance.compont.IRemoteApi;
import com.szmsd.finance.config.ExportValid;
import com.szmsd.finance.config.FileVerifyUtil;
import com.szmsd.finance.domain.FssRefundRequest;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.enums.RefundProcessEnum;
import com.szmsd.finance.enums.RefundStatusEnum;
import com.szmsd.finance.mapper.RefundRequestMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IRefundRequestService;
import com.szmsd.finance.vo.RefundRequestListVO;
import com.szmsd.finance.vo.RefundRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 退费记录表 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-08-13
 */
@Slf4j
@Service
public class RefundRequestServiceImpl extends ServiceImpl<RefundRequestMapper, FssRefundRequest> implements IRefundRequestService {
    @Resource
    private IRemoteApi remoteApi;

    @Resource
    private IAccountBalanceService accountBalanceService;

    @Override
    public List<RefundRequestListVO> selectRequestList(RefundRequestQueryDTO queryDTO) {
        return baseMapper.selectRequestList(queryDTO);
    }

    @Override
    public RefundRequestVO selectDetailInfoById(String id) {
        FssRefundRequest fssRefundRequest = baseMapper.selectById(id);
        AssertUtil.notNull(fssRefundRequest, "数据不存在!");
        RefundRequestVO refundRequestVO = new RefundRequestVO();
        BeanUtils.copyProperties(fssRefundRequest, refundRequestVO);
        return refundRequestVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRefundRequest(RefundRequestListDTO addDTO) {
        List<RefundRequestDTO> refundRequestList = addDTO.getRefundRequestList();
        return this.insertBatchRefundRequest(refundRequestList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBatchRefundRequest(List<RefundRequestDTO> addList) {
        List<String> strings = remoteApi.genNo(addList.size());
        AtomicInteger noLine = new AtomicInteger(0);
        List<FssRefundRequest> collect = addList.stream().map(x -> {
            FssRefundRequest fssRefundRequest = new FssRefundRequest();
            BeanUtils.copyProperties(x, fssRefundRequest);
            fssRefundRequest.setAuditStatus(RefundStatusEnum.BRING_INTO_COURT.getStatus())
                    .setProcessNo(strings.get(noLine.getAndIncrement()));
            return fssRefundRequest;
        }).collect(Collectors.toList());
        return this.saveBatch(collect) ? addList.size() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRefundRequest(RefundRequestDTO updateDTO) {
        AssertUtil.notNull(updateDTO.getId(), "id is require");
        FssRefundRequest fssRefundRequest = new FssRefundRequest();
        BeanUtils.copyProperties(updateDTO, fssRefundRequest);
        fssRefundRequest.setAuditStatus(RefundStatusEnum.BRING_INTO_COURT.getStatus());
        return baseMapper.update(fssRefundRequest,Wrappers.<FssRefundRequest>lambdaUpdate()
                .in(FssRefundRequest::getAuditStatus,RefundStatusEnum.BRING_INTO_COURT.getStatus()
                        ,RefundStatusEnum.INITIAL.getStatus()).eq(FssRefundRequest::getId,updateDTO.getId()));
    }

    @Override
    public int deleteRefundRequestByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) return 1;
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importByTemplate(MultipartFile file) {
        List<RefundRequestDTO> basPackingAddList = FileVerifyUtil.importExcel(file, RefundRequestDTO.class);
        handleInsertData(basPackingAddList, true);
        return this.insertBatchRefundRequest(basPackingAddList);
    }

    @Resource
    private ConfigData configData;

    public void handleInsertData(List<RefundRequestDTO> basPackingAddList, Boolean isExport) {
        AssertUtil.isTrue(CollectionUtils.isNotEmpty(basPackingAddList), "数据异常,请重新新增!");
        if (isExport) {
            //检验规则
            AtomicInteger importNo = new AtomicInteger(1);
            basPackingAddList.forEach(basSellAccountPeriodAddDTO -> FileVerifyUtil.validate(basSellAccountPeriodAddDTO, importNo, ExportValid.class));
            basPackingAddList.forEach(x -> {
                // 处理性质	责任地区	所属仓库 业务类型	业务明细	费用类型	费用明细 属性
                ConfigData.MainSubCode mainSubCode = configData.getMainSubCode();

                x.setTreatmentPropertiesCode(remoteApi.getSubCode(mainSubCode.getTreatmentProperties(), x.getTreatmentProperties()));

                x.setResponsibilityAreaCode(remoteApi.getSubCode(mainSubCode.getResponsibilityArea(), x.getResponsibilityArea()));

                BasSub businessTypeObj = remoteApi.getSubCodeObj(mainSubCode.getBusinessType(), x.getBusinessTypeName());
                x.setBusinessTypeCode(businessTypeObj.getSubCode());
                String subValue = businessTypeObj.getSubValue();
                x.setBusinessDetailsCode(remoteApi.getSubCode(subValue, x.getBusinessDetails()));

                BasSub feeTypeSubCodeObj = remoteApi.getSubCodeObj(mainSubCode.getTypesOfFee(), x.getFeeTypeName());
                x.setFeeTypeCode(feeTypeSubCodeObj.getSubCode());
                String feeTypeSubValue = feeTypeSubCodeObj.getSubValue();
                x.setFeeCategoryCode(remoteApi.getSubCode(feeTypeSubValue, x.getFeeCategoryName()));

                x.setAttributesCode(remoteApi.getSubCode(mainSubCode.getProperty(), x.getAttributes()));
                // 供应商是否完成赔付
                String compensationPaymentFlag = Optional.ofNullable(x.getCompensationPaymentFlag()).map(z -> {
                    if ("是".equals(z)) return "1";
                    return "0";
                }).orElse("0");
                x.setCompensationPaymentFlag(compensationPaymentFlag);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approve(RefundStatusEnum status, List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) return 0;
        LoginUser loginUser = SecurityUtils.getLoginUser();
        int update = baseMapper.update(null, Wrappers.<FssRefundRequest>lambdaUpdate()
                .in(FssRefundRequest::getId, ids)
                .eq(FssRefundRequest::getAuditStatus, RefundStatusEnum.BRING_INTO_COURT.getStatus())

                .set(FssRefundRequest::getAuditStatus, status.getStatus())
                .set(FssRefundRequest::getAuditTime, LocalDateTime.now())
                .set(FssRefundRequest::getReviewerId, loginUser.getUserId())
                .set(FssRefundRequest::getReviewerCode, loginUser.getSellerCode())
                .set(FssRefundRequest::getReviewerName, loginUser.getUsername())
        );
        AssertUtil.isTrue(update == ids.size(), "审核异常!");
        //审核完成触发扣减
        this.afterApprove(status, ids);
        return update;
    }

    /**
     * 系统需根据处理性质以及状态对客户账户余额进行变动。处理性质为“补收”“增值消费”，
     * 在订单完成之后调减客户余额；处理性质为“退费”、“赔偿”、“充值”、“优惠”，
     * 当订单完成之后对客户余额进行调增
     * <p>
     * 发生余额变动均需在业务账中体现
     *
     * @param status 审核状态
     * @param idList 审核id集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void afterApprove(RefundStatusEnum status, List<String> idList) {
        if (RefundStatusEnum.COMPLETE != status) return;
        log.info("审核通过-进行相应的越扣减 {}", idList);
        List<FssRefundRequest> fssRefundRequests = baseMapper.selectList(Wrappers.<FssRefundRequest>lambdaQuery().in(FssRefundRequest::getId, idList));
        Map<RefundProcessEnum, List<FssRefundRequest>> collect = fssRefundRequests.stream().collect(Collectors.groupingBy(x -> {
            ConfigData.MainSubCode mainSubCode = configData.getMainSubCode();
            BasSub subCodeObj = remoteApi.getSubCodeObj(mainSubCode.getTreatmentProperties(), x.getTreatmentProperties());
            String subValue = subCodeObj.getSubValue();
            return RefundProcessEnum.getProcessStrategy(subValue);
        }));
        log.info("审核处理{}", JSONObject.toJSONString(collect));
        // TODO 订单记录流水、【余额对应调增/调减】、【产生业务账记录】
        collect.forEach((processEnum, list) -> {
            switch (processEnum) {
                case ADD:
                    log.info("ADD--{}", list);
                    list.forEach(x -> {
                        CustPayDTO custPayDTO = getCustPayDTO(x);
                        custPayDTO.setRemark(String.format("退费单%s,余额调增", x.getProcessNo()));
                        R r = accountBalanceService.refund(custPayDTO);
                        AssertUtil.isTrue(r.getCode() == HttpStatus.SUCCESS, r.getMsg());
                        log.info("ADD--{}--{}", list, JSONObject.toJSONString(r));
                    });
                    return;
                case SUBTRACT:
                    log.info("SUBTRACT--{}", list);
                    list.forEach(x -> {
                        CustPayDTO custPayDTO = getCustPayDTO(x);
                        custPayDTO.setAmount(x.getAmount().multiply(new BigDecimal("-1")));
                        custPayDTO.setRemark(String.format("退费单%s,余额调减", x.getProcessNo()));
                        R r = accountBalanceService.refund(custPayDTO);
                        AssertUtil.isTrue(r.getCode() == HttpStatus.SUCCESS, r.getMsg()+"请检查改币别账户余额是否充足");
                        log.info("SUBTRACT--{}--{}", list, JSONObject.toJSONString(r));
                    });
                    return;
                default:
                    log.info("不处理--{}", list);
                    return;
            }
        });
    }

    private CustPayDTO getCustPayDTO(FssRefundRequest x) {
        CustPayDTO custPayDTO = new CustPayDTO();
        custPayDTO.setAmount(x.getAmount());
        custPayDTO.setNo(x.getProcessNo());
        custPayDTO.setCurrencyCode(x.getCurrencyCode());
        custPayDTO.setCurrencyName(x.getCurrencyName());
        custPayDTO.setCusCode(x.getCusCode());
        custPayDTO.setCusId((long) x.getCusId());
        custPayDTO.setCusName(x.getCusName());
        List<AccountSerialBillDTO> accountSerialBillList = new ArrayList<>();
        AccountSerialBillDTO accountSerialBillDTO = new AccountSerialBillDTO();
        accountSerialBillDTO.setChargeCategory(x.getFeeCategoryName());
        accountSerialBillDTO.setChargeType(x.getFeeTypeName());
        accountSerialBillDTO.setPayMethod(BillEnum.PayMethod.REFUND);
        accountSerialBillDTO.setBusinessCategory(x.getTreatmentProperties());
        accountSerialBillDTO.setChargeCategory(BillEnum.PayMethod.REFUND.getPaymentName());
        accountSerialBillDTO.setAmount(x.getAmount());
        accountSerialBillDTO.setCusCode(x.getCusCode());
        accountSerialBillDTO.setCusName(x.getCusName());
        accountSerialBillDTO.setCurrencyCode(x.getCurrencyCode());
        accountSerialBillDTO.setCurrencyName(x.getCurrencyName());
        accountSerialBillList.add(accountSerialBillDTO);
        custPayDTO.setSerialBillInfoList(accountSerialBillList);
        return custPayDTO;
    }

    @Override
    public int confirmOperation(ConfirmOperationDTO confirmOperationDTO) {
        //TODO 订单记录流水、【余额对应调增/调减】、【产生业务账记录】

        return 0;
    }
}

