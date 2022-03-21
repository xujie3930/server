package com.szmsd.finance.service.impl;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.thread.NamedThreadFactory;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.finance.compont.ConfigData;
import com.szmsd.finance.compont.IRemoteApi;
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
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.domain.dto.QueryFinishListDTO;
import com.szmsd.inventory.domain.vo.QueryFinishListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.groups.Default;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
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
    private RedissonClient redissonClient;
    @Resource
    private IAccountBalanceService accountBalanceService;

    @Override
    @DataScope(value = "cus_code")
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
        return baseMapper.update(fssRefundRequest, Wrappers.<FssRefundRequest>lambdaUpdate()
                .in(FssRefundRequest::getAuditStatus, RefundStatusEnum.BRING_INTO_COURT.getStatus()
                        , RefundStatusEnum.INITIAL.getStatus()).eq(FssRefundRequest::getId, updateDTO.getId()));
    }

    @Override
    public int deleteRefundRequestByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) return 1;
        return baseMapper.delete(Wrappers.<FssRefundRequest>lambdaUpdate().
                in(FssRefundRequest::getId, ids)
                .in(FssRefundRequest::getAuditStatus, RefundStatusEnum.BRING_INTO_COURT.getStatus(), RefundStatusEnum.INITIAL.getStatus()));
    }

    @Override
//    @Transactional(rollbackFor = Exception.class)
    public int importByTemplate(MultipartFile file) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), new NamedThreadFactory("【RefundImport】==", false), new ThreadPoolExecutor.CallerRunsPolicy());
        try (InputStream inputStream = file.getInputStream()) {
            List<RefundRequestDTO> basPackingAddList = EasyExcel.read(inputStream, RefundRequestDTO.class, new SyncReadListener()).sheet().doReadSync();
            int count = 500;
            int size = basPackingAddList.size();
            int segments = size / count;
            segments = size % count == 0 ? segments : segments + 1;
            CountDownLatch countDownLatch = new CountDownLatch(segments);
            List<Future<String>> futures = new ArrayList<>();

            for (int i = 0; i < segments; i++) {
                List<RefundRequestDTO> refundRequestDTOS;
                if (i == segments - 1) {
                    refundRequestDTOS = basPackingAddList.subList(count * i, size);
                } else {
                    refundRequestDTOS = basPackingAddList.subList(count * i, count * (i + 1));
                }
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Future<String> submit = threadPoolExecutor.submit(() -> {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    LoginUser loginUser = SecurityUtils.getLoginUser();
                    String errorMsg = "";
                    try {
                        handleInsertData(refundRequestDTOS, true);
                        this.insertBatchRefundRequest(refundRequestDTOS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorMsg = e.getMessage();
                        log.error("=================导入失败=================：\n{} \n", JSONObject.toJSONString(refundRequestDTOS), e);
                    } finally {
                        countDownLatch.countDown();
                    }
                    return errorMsg;
                });
                futures.add(submit);
            }


            countDownLatch.await();
            StringBuilder stringBuilder = new StringBuilder();
            futures.forEach(errorMsg -> {
                String s = "";
                try {
                    s = errorMsg.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    log.error("执行等待异常：", e);
                    s = e.getMessage();
                }
                stringBuilder.append(s);
            });
            AssertUtil.isTrue(StringUtils.isBlank(stringBuilder.toString()), stringBuilder.toString());
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("执行等待异常22：", e);
        } finally {
            threadPoolExecutor.shutdown();
        }
        return 1;

    }

    @Resource
    private ConfigData configData;

    public void handleInsertData(List<RefundRequestDTO> basPackingAddList, Boolean isExport) {
        AssertUtil.isTrue(CollectionUtils.isNotEmpty(basPackingAddList), "数据异常,请重新新增!");
        if (isExport) {
            //检验规则
            AtomicInteger importNo = new AtomicInteger(1);
            List<String> errorMsg = new LinkedList<>();
            StopWatch stopWatch = new StopWatch("校验");
            stopWatch.start("参数必填校验");
            basPackingAddList.forEach(basSellAccountPeriodAddDTO -> {
                if (StringUtils.isNotBlank(basSellAccountPeriodAddDTO.getTreatmentProperties()) && "赔偿".equals(basSellAccountPeriodAddDTO.getTreatmentProperties())) {
                    FileVerifyUtil.validate(basSellAccountPeriodAddDTO, importNo, errorMsg, ICompensateCheck.class);
                } else {
                    FileVerifyUtil.validate(basSellAccountPeriodAddDTO, importNo, errorMsg, Default.class);
                }
            });
            stopWatch.stop();
            stopWatch.start("内容填充");
            AssertUtil.isTrue(CollectionUtils.isEmpty(errorMsg), String.join("\n", errorMsg));
            basPackingAddList.forEach(x -> {
                AssertUtil.isTrue(remoteApi.checkCusCode(x.getCusCode()), "用户" + x.getCusCode() + "不存在");
                // 校验单号是否属于他且
                // 处理性质	责任地区	所属仓库 业务类型	业务明细	费用类型	费用明细 属性
                ConfigData.MainSubCode mainSubCode = configData.getMainSubCode();

                x.setTreatmentPropertiesCode(remoteApi.getSubCode(mainSubCode.getTreatmentProperties(), x.getTreatmentProperties()));
                x.setWarehouseName(remoteApi.getWareHouseName(x.getWarehouseCode()));

//                x.setResponsibilityAreaCode(remoteApi.getSubCode(mainSubCode.getResponsibilityArea(), x.getResponsibilityArea()));

                x.setBusinessTypeCode(remoteApi.getSubCodeObjSubCode(mainSubCode.getBusinessType(), x.getBusinessTypeName()));
//                String subValue = businessTypeObj.getSubValue();
//                x.setBusinessDetailsCode(remoteApi.getSubCode(subValue, x.getBusinessDetails()));

                x.setFeeTypeCode(remoteApi.getSubCodeObjSubCode(mainSubCode.getTypesOfFee(), x.getFeeTypeName()));
//                String feeTypeSubValue = feeTypeSubCodeObj.getSubValue();
//                x.setFeeCategoryCode(remoteApi.getSubCode(feeTypeSubValue, x.getFeeCategoryName()));
                String attributesCode = remoteApi.getSubCode(mainSubCode.getProperty(), x.getAttributes());
                if (StringUtils.isNotBlank(attributesCode)) {
                    x.setAttributesCode(attributesCode);
                } else {
                    x.setAttributes(null);
                }

                // 供应商是否完成赔付
                x.setCompensationPaymentFlag((StringUtils.isNotBlank(x.getCompensationPaymentFlag()) && "已完成".equals(x.getCompensationPaymentFlag())) ? "1" : "0");
                x.setCompensationPaymentArrivedFlag((StringUtils.isNotBlank(x.getCompensationPaymentFlag()) && "是".equals(x.getCompensationPaymentFlag())) ? "1" : "0");

                x.setCurrencyName(remoteApi.getSubNameByValue(mainSubCode.getCurrency(), x.getCurrencyCode()));

                // 赔付币别
                if (StringUtils.isNotBlank(x.getCompensationPaymentCurrencyCode())) {
                    String compensationPaymentCurrency = remoteApi.getSubNameByValue(mainSubCode.getCurrency(), x.getCompensationPaymentCurrencyCode());
                    x.setCompensationPaymentCurrency(compensationPaymentCurrency);
                    if (StringUtils.isBlank(compensationPaymentCurrency)) x.setCompensationPaymentCurrency(null);
                }
            });
            stopWatch.stop();
            stopWatch.start("校验单号");
            StringBuilder errorMsgBuilder = new StringBuilder();
            //客户号-客户预处理号 校验预处理号是否是已完成的订单
            Map<String, List<String>> collect = basPackingAddList.stream().filter(x -> StringUtils.isNotBlank(x.getOrderNo())).collect(Collectors.groupingBy(RefundRequestDTO::getCusCode, Collectors.mapping(RefundRequestDTO::getOrderNo, Collectors.toList())));
            collect.forEach((cusCode, processNoList) -> {
                processNoList = processNoList.stream().distinct().collect(Collectors.toList());
                Map<Integer, List<String>> ck = processNoList.stream().collect(Collectors.groupingBy(x -> x.startsWith("CK") ? 1 : 0));


                ck.forEach((type, list) -> {
                    QueryFinishListDTO queryFinishListDTO = new QueryFinishListDTO();
                    queryFinishListDTO.setCusCode(cusCode);
                    queryFinishListDTO.setNoList(list);
                    queryFinishListDTO.setType(type);
                    queryFinishListDTO.setPageNum(1);
                    queryFinishListDTO.setPageSize(999);
                    log.info("校验单号：{}", JSONObject.toJSONString(queryFinishListDTO));
                    TableDataInfo<QueryFinishListVO> queryFinishListVOTableDataInfo = this.queryFinishList(queryFinishListDTO);
                    log.info("校验单号返回：{}", JSONObject.toJSONString(queryFinishListVOTableDataInfo));
                    AssertUtil.isTrue(queryFinishListVOTableDataInfo.getCode() == HttpStatus.SUCCESS, "校验单号失败");
                    if (queryFinishListVOTableDataInfo.getTotal() != list.size()) {
                        List<String> collect1 = queryFinishListVOTableDataInfo.getRows().stream().map(QueryFinishListVO::getNo).collect(Collectors.toList());
                        list.removeAll(collect1);
                        errorMsgBuilder.append("请检查用户【").append(cusCode).append("】的单号:").append(StringUtils.join(list, ",")).append("是否已完成/不属于该用户;").append("\n");
                    }
                });
            });
            stopWatch.stop();
            log.info(stopWatch.prettyPrint());
            if (StringUtils.isNotBlank(errorMsgBuilder.toString())) {
                throw new RuntimeException(errorMsgBuilder.toString());
            }
        }
    }

    public static final long time = 90L;
    public static final TimeUnit unit = TimeUnit.SECONDS;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approve(RefundReviewDTO refundReviewDTO) {
        String key = "cky-test-fss-approve";
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(time, unit)) {
                List<String> ids = refundReviewDTO.getIdList();
                RefundStatusEnum status = refundReviewDTO.getStatus();
                String reviewRemark = refundReviewDTO.getReviewRemark();
                LoginUser loginUser = SecurityUtils.getLoginUser();
                int update = baseMapper.update(null, Wrappers.<FssRefundRequest>lambdaUpdate()
                        .in(FssRefundRequest::getId, ids)
                        .eq(FssRefundRequest::getAuditStatus, RefundStatusEnum.BRING_INTO_COURT.getStatus())

                        .set(FssRefundRequest::getReviewRemark, reviewRemark)
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
            } else {
                log.error("退费业务处理超时,请稍候重试{}", JSONObject.toJSONString(refundReviewDTO));
                throw new RuntimeException("退费业务处理超时,请稍候重试");
            }
        } catch (InterruptedException  e) {
            log.error("退费业务处理超时,请稍候重试{}", JSONObject.toJSONString(refundReviewDTO));
            log.error("退费业务处理超时,请稍候重试", e);
            throw new RuntimeException("退费业务处理超时,请稍候重试");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("【退费】RefundPayFactory 解锁结束--");
            }
            log.info("【退费】RefundPayFactory --结束--");
        }
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
                        AssertUtil.isTrue(r.getCode() == HttpStatus.SUCCESS, r.getMsg() + "请检查该币别账户余额是否充足");
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
        custPayDTO.setNo(Optional.ofNullable(x.getOrderNo()).orElse(""));
        custPayDTO.setCurrencyCode(x.getCurrencyCode());
        custPayDTO.setCurrencyName(x.getCurrencyName());
        custPayDTO.setCusCode(x.getCusCode());
        custPayDTO.setCusId((long) x.getCusId());
        custPayDTO.setCusName(x.getCusName());
        custPayDTO.setWarehouseCode(x.getWarehouseCode());
        custPayDTO.setWarehouseName(x.getWarehouseName());
        List<AccountSerialBillDTO> accountSerialBillList = new ArrayList<>();
        AccountSerialBillDTO accountSerialBillDTO = new AccountSerialBillDTO();
        accountSerialBillDTO.setChargeCategory(x.getFeeCategoryName());
        accountSerialBillDTO.setChargeType(x.getFeeTypeName());
        accountSerialBillDTO.setPayMethod(BillEnum.PayMethod.REFUND);
        accountSerialBillDTO.setBusinessCategory(x.getTreatmentProperties());
        accountSerialBillDTO.setChargeCategory(BillEnum.CostCategoryEnum.REFUND.getName());
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

    @Resource
    private DelOutboundFeignService delOutboundFeignService;
    @Resource
    private InventoryFeignService inventoryFeignService;

    @Override
    public TableDataInfo<QueryFinishListVO> queryFinishList(QueryFinishListDTO queryFinishListDTO) {
        TableDataInfo<QueryFinishListVO> result;
        if (queryFinishListDTO.getType() == 1) {
            result = delOutboundFeignService.queryFinishList(queryFinishListDTO);
        } else {
            result = inventoryFeignService.queryFinishList(queryFinishListDTO);
        }
        return result;
    }
}

