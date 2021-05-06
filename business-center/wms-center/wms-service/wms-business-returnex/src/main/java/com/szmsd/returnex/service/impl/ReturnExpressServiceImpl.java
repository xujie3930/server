package com.szmsd.returnex.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.common.datascope.service.AwaitUserService;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.dto.returnex.ReturnDetail;
import com.szmsd.http.dto.returnex.ReturnDetailWMS;
import com.szmsd.returnex.api.feign.client.IBasFeignClientService;
import com.szmsd.returnex.api.feign.client.IHttpFeignClientService;
import com.szmsd.returnex.config.BeanCopyUtil;
import com.szmsd.returnex.config.ConfigStatus;
import com.szmsd.returnex.constant.ReturnExpressConstant;
import com.szmsd.returnex.domain.ReturnExpressDetail;
import com.szmsd.returnex.dto.*;
import com.szmsd.returnex.dto.wms.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.wms.ReturnProcessingFinishReqDTO;
import com.szmsd.returnex.dto.wms.ReturnProcessingReqDTO;
import com.szmsd.returnex.mapper.ReturnExpressMapper;
import com.szmsd.returnex.service.IReturnExpressGoodService;
import com.szmsd.returnex.service.IReturnExpressService;
import com.szmsd.returnex.vo.ReturnExpressListVO;
import com.szmsd.returnex.vo.ReturnExpressVO;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName: ReturnExpressServiceImpl
 * @Description: 退货
 * @Author: 11
 * @Date: 2021/3/26 11:47
 */
@Slf4j
@Service
public class ReturnExpressServiceImpl extends ServiceImpl<ReturnExpressMapper, ReturnExpressDetail> implements IReturnExpressService {

    @Resource
    private ReturnExpressMapper returnExpressMapper;

    @Resource
    private IHttpFeignClientService httpFeignClient;

    @Resource
    private IReturnExpressGoodService returnExpressGoodService;

    @Resource
    private AwaitUserService awaitUserService;

    @Resource
    private BasFeignService basFeignService;

    @Resource
    private IBasFeignClientService iBasFeignClientService;

    @Resource
    private ConfigStatus configStatus;

    /**
     * 获取用户sellerCode
     *
     * @return
     */
    private String getSellCode() {
        // UserInfo info = awaitUserService.info();
        String loginSellerCode = iBasFeignClientService.getLoginSellerCode();
        //String loginSellerCode = "HTO3";
        return Optional.ofNullable(loginSellerCode).orElse("");
    }

    /**
     * 单号生成
     *
     * @return
     */
    public String genNo() {
        String code = ReturnExpressConstant.GENERATE_CODE;
        String appId = ReturnExpressConstant.GENERATE_APP_ID;
        log.info("调用自动生成单号：code={}", code);
        R<List<String>> r = basFeignService.create(new BasCodeDto().setAppId(appId).setCode(code));
        AssertUtil.notNull(r, "单号生成失败");
        AssertUtil.isTrue(r.getCode() == HttpStatus.SUCCESS, code + "单号生成失败：" + r.getMsg());
        String s = r.getData().get(0);
        log.info("调用自动生成单号：调用完成, {}-{}", code, s);
        return s;
    }

    /**
     * 新增退件单-生成预报单号
     *
     * @return 返回结果
     */
    @Override
    public String createExpectedNo() {
        return genNo();
    }

    @Override
    public List<ReturnExpressListVO> selectClientReturnOrderList(ReturnExpressListQueryDTO queryDto) {
        queryDto.setSellerCode(getSellCode());
        return selectReturnOrderList(queryDto);
    }

    /**
     * 接收仓库拆包明细
     * /api/return/details #G2-接收仓库拆包明细
     *
     * @param returnProcessingReqDTO 拆包明细
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveProcessingInfoFromVms(ReturnProcessingReqDTO returnProcessingReqDTO) {
        log.info("接收仓库拆包明细 {}", returnProcessingReqDTO);
        ReturnExpressDetail detail = returnExpressMapper.selectOne(Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWmsWaitReceive())
                .eq(ReturnExpressDetail::getProcessType, configStatus.getUnpackingInspection())
                .eq(ReturnExpressDetail::getReturnNo, returnProcessingReqDTO.getReturnNo()));
        AssertUtil.notNull(detail, "数据不存在!");

        //处理中的会接收到拆包明细 拆包方式才调这个接口
        ReturnExpressDetail returnExpressDetail = new ReturnExpressDetail();
        int update = returnExpressMapper.update(returnExpressDetail, Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWmsWaitReceive())
                .eq(ReturnExpressDetail::getProcessType, configStatus.getUnpackingInspection())
                .eq(ReturnExpressDetail::getReturnNo, returnProcessingReqDTO.getReturnNo())

                .set(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWaitCustomerDeal())
                .set(ReturnExpressDetail::getDealStatusStr, configStatus.getDealStatus().getWaitCustomerDealStr())
                .last("LIMIT 1")
        );
        AssertUtil.isTrue(1 == update, "请检查是否已经处理过该数据");
        log.info("更新拆包数据 {}", returnExpressDetail);

        List<ReturnDetail> details = returnProcessingReqDTO.getDetails();
        if (CollectionUtils.isEmpty(details)) {
            log.info("更新拆包数据 无商品明细数据");
            return update;
        }
        List<ReturnExpressGoodAddDTO> returnExpressGoodAddDTOS = BeanCopyUtil.copyListProperties(details, ReturnExpressGoodAddDTO::new);
        returnExpressGoodService.addOrUpdateGoodInfoBatch(returnExpressGoodAddDTOS, detail.getId());
        return update;
    }

    /**
     * 退件单列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @Override
    public List<ReturnExpressListVO> selectReturnOrderList(ReturnExpressListQueryDTO queryDto) {
        return returnExpressMapper.selectPageList(queryDto);
    }

    /**
     * 无名件管理列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @Override
    public List<ReturnExpressListVO> pageForNoUserBind(ReturnExpressListQueryDTO queryDto) {
        queryDto.setNoUserQuery(true);
        return selectReturnOrderList(queryDto);
    }

    /**
     * 无名件批量指派客户
     *
     * @param expressAssignDTO 指派条件
     * @return 返回结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int assignUsersForNoUserBindBatch(ReturnExpressAssignDTO expressAssignDTO) {
        int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .isNull(ReturnExpressDetail::getSellerCode)
                .in(ReturnExpressDetail::getId, expressAssignDTO.getIds())
                .set(ReturnExpressDetail::getSellerCode, expressAssignDTO.getSellerCode())
                .set(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWaitCustomerDeal())
                .set(ReturnExpressDetail::getDealStatusStr, configStatus.getDealStatus().getWaitCustomerDealStr())
        );
        return update;
    }

    /**
     * 新建退件单
     *
     * @param returnExpressAddDTO 新增
     * @return 返回结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReturnExpressDetail(ReturnExpressAddDTO returnExpressAddDTO) {
        returnExpressAddDTO.setSellerCode(getSellCode());
        checkSubmit(returnExpressAddDTO);

        if (StringUtils.isBlank(returnExpressAddDTO.getExpectedNo())) {
            String expectedNo = createExpectedNo();
            returnExpressAddDTO.setExpectedNo(expectedNo);
        }
        handleExpectedCreate(returnExpressAddDTO);
        // 本地保存
        return saveReturnExpressDetail(returnExpressAddDTO.convertThis(ReturnExpressDetail.class));
    }

    /**
     * 调用wms 创建退件单
     *
     * @param returnExpressAddDTO
     */
    private void handleExpectedCreate(ReturnExpressAddDTO returnExpressAddDTO) {
        // 创建退报单 推给VMS仓库
        CreateExpectedReqDTO createExpectedReqDTO = returnExpressAddDTO.convertThis(CreateExpectedReqDTO.class);
        createExpectedReqDTO.setRefOrderNo(returnExpressAddDTO.getFromOrderNo());
        //需要转换 处理方式
        createExpectedReqDTO.setProcessType(configStatus.getPrCode(returnExpressAddDTO.getProcessType()));
        httpFeignClient.expectedCreate(createExpectedReqDTO);
    }

    private void checkSubmit(ReturnExpressAddDTO returnExpressAddDTO) {
        // 校验重复条件
        ReturnExpressDetail returnExpressDetail = returnExpressMapper.selectOne(Wrappers.<ReturnExpressDetail>lambdaQuery()
                .eq(ReturnExpressDetail::getExpectedNo, returnExpressAddDTO.getExpectedNo())
                .select(ReturnExpressDetail::getId));
        Optional.ofNullable(returnExpressDetail).ifPresent(x -> {
            throw new BaseException("请勿重复提交");
        });
    }

    /**
     * 本地保存用户发起的预报单数据
     *
     * @param returnExpressDetail
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int saveReturnExpressDetail(ReturnExpressDetail returnExpressDetail) {
        returnExpressDetail.setDealStatus(configStatus.getDealStatus().getWmsWaitReceive());
        returnExpressDetail.setDealStatusStr(configStatus.getDealStatus().getWmsWaitReceiveStr());
        return returnExpressMapper.insert(returnExpressDetail);
    }

    /**
     * 接收WMS仓库到件信息
     * /api/return/arrival #G1-接收仓库退件到货
     *
     * @param returnArrivalReqDTO 接收VMS仓库到件信息
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveArrivalInfoFormWms(ReturnArrivalReqDTO returnArrivalReqDTO) {

        if (returnArrivalReqDTO.getExpectedNo() != null) {
            //到货 拆包检查的 需要接收商品详情
            ReturnExpressDetail returnExpressDetailCheck = returnExpressMapper.selectOne(Wrappers.<ReturnExpressDetail>lambdaUpdate()
                    .eq(ReturnExpressDetail::getExpectedNo, returnArrivalReqDTO.getExpectedNo())
                    .eq(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWmsWaitReceive()).last("LIMIT 1"));
            AssertUtil.notNull(returnExpressDetailCheck, "数据不存在!");
            String dealStatus = configStatus.getDealStatus().getWaitCustomerDeal();
            String dealStatusStr = configStatus.getDealStatus().getWaitCustomerDealStr();
            // 拆包/销毁 整包 需要等待接收其他接口 拆包 G2 需要用户处理，销毁 整包 G3直接结束流程
            boolean isOpenAndCheck = returnExpressDetailCheck.getProcessType().equals(configStatus.getUnpackingInspection());
            boolean isDestroy = returnExpressDetailCheck.getProcessType().equals(configStatus.getDestroy()) || returnExpressDetailCheck.getProcessType().equals(configStatus.getWholePackageOnShelves());
            if (isOpenAndCheck) {
                dealStatus = configStatus.getDealStatus().getWmsWaitReceive();
                dealStatusStr = configStatus.getDealStatus().getWmsWaitReceiveStr();
            } else if (isDestroy) {
                dealStatus = configStatus.getDealStatus().getWmsReceivedDealWay();
                dealStatusStr = configStatus.getDealStatus().getWmsReceivedDealWayStr();
            }

            int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                    .eq(ReturnExpressDetail::getExpectedNo, returnArrivalReqDTO.getExpectedNo())
                    .eq(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWmsWaitReceive())
                    .set(ReturnExpressDetail::getReturnNo, returnArrivalReqDTO.getReturnNo())
                    .set(ReturnExpressDetail::getFromOrderNo, returnArrivalReqDTO.getFromOrderNo())
                    .set(StringUtil.isNotBlank(returnArrivalReqDTO.getExpectedNo()), ReturnExpressDetail::getExpectedNo, returnArrivalReqDTO.getExpectedNo())
                    .set(ReturnExpressDetail::getScanCode, returnArrivalReqDTO.getScanCode())
                    .set(ReturnExpressDetail::getSellerCode, returnArrivalReqDTO.getSellerCode())
                    .set(StringUtil.isNotBlank(returnArrivalReqDTO.getRemark()), BaseEntity::getRemark, returnArrivalReqDTO.getRemark())
                    .set(ReturnExpressDetail::getArrivalTime, LocalDateTime.now())
                    .set(isDestroy,ReturnExpressDetail::getFinishTime,LocalDateTime.now())

                    .set(ReturnExpressDetail::getDealStatus, dealStatus)
                    .set(ReturnExpressDetail::getDealStatusStr, dealStatusStr)
            );
            return update;
        } else {
            // 新增无主件 状态待指派
            ReturnExpressDetail returnExpressDetail = returnArrivalReqDTO.convertThis(ReturnExpressDetail.class);
            returnExpressDetail.setReturnSource(configStatus.getReturnSource().getWmsReturn());
            returnExpressDetail.setReturnSourceStr(configStatus.getReturnSource().getWmsReturnStr());
            returnExpressDetail.setDealStatus(configStatus.getDealStatus().getWaitAssigned());
            returnExpressDetail.setDealStatusStr(configStatus.getDealStatus().getWaitAssignedStr());
            int insert = returnExpressMapper.insert(returnExpressDetail);
            // 其他处理
            return insert;
        }
    }

    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收WMS仓库退件处理结果
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int finishProcessingInfoFromWms(ReturnProcessingFinishReqDTO returnProcessingReqDTO) {
        log.info("接收WMS仓库退件处理结果 {}", returnProcessingReqDTO);
        String dealStatus = configStatus.getDealStatus().getWmsFinish();
        String dealStatusStr = configStatus.getDealStatus().getWmsFinishStr();

        int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getReturnNo, returnProcessingReqDTO.getReturnNo())
                .eq(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWmsReceivedDealWay())

                .set(ReturnExpressDetail::getDealStatus, dealStatus)
                .set(ReturnExpressDetail::getDealStatusStr, dealStatusStr)
                .set(ReturnExpressDetail::getFinishTime, LocalDateTime.now())
                .last("LIMIT 1")
        );
        log.info("接收WMS仓库退件处理结果 {} - 更新条数 {}", returnProcessingReqDTO, update);
        AssertUtil.isTrue(update == 1, "更新异常");
        return update;
    }

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateExpressInfo(ReturnExpressAddDTO expressUpdateDTO) {
        //如果之前是销毁，已经结束了，到这都是要么销毁，要么拆包上架
        log.info("更新退单信息 req:{}", expressUpdateDTO);
        expressUpdateDTO.setSellerCode(getSellCode());
        AssertUtil.isTrue(expressUpdateDTO.getId() != null && expressUpdateDTO.getId() > 0, "更新异常！");
        //如果是拆包，不可以整包上架 前段控制;
        ReturnExpressDetail returnExpressDetailCheck = returnExpressMapper.selectById(expressUpdateDTO.getId());
        AssertUtil.notNull(returnExpressDetailCheck, "数据不存在!");
        boolean isOpenAndCheck = configStatus.getUnpackingInspection().equals(returnExpressDetailCheck.getProcessType());
        String dealStatus = configStatus.getDealStatus().getWmsReceivedDealWay();
        String dealStatusStr = configStatus.getDealStatus().getWmsReceivedDealWayStr();

        if (isOpenAndCheck)
            AssertUtil.isTrue(!configStatus.getWholePackageOnShelves().equals(expressUpdateDTO.getProcessType()), "拆包上架后不在支持整包上架");
        if (configStatus.getReturnSource().getWmsReturn().equals(returnExpressDetailCheck.getReturnSource()) && StringUtils.isBlank(returnExpressDetailCheck.getProcessType())) {
            //如果是WMS通知退件 则需要重新走流程 状态重置为wms待处理
            if (isOpenAndCheck) {
                dealStatus = configStatus.getDealStatus().getWmsWaitReceive();
                dealStatusStr = configStatus.getDealStatus().getWmsWaitReceiveStr();
            }
        }

        int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getId, expressUpdateDTO.getId())
                .eq(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWaitCustomerDeal())

                .set(ReturnExpressDetail::getDealStatus, dealStatus)
                .set(ReturnExpressDetail::getDealStatusStr, dealStatusStr)
                .set(expressUpdateDTO.getProcessType() != null, ReturnExpressDetail::getProcessType, expressUpdateDTO.getProcessType())
                .set(expressUpdateDTO.getProcessTypeStr() != null, ReturnExpressDetail::getProcessTypeStr, expressUpdateDTO.getProcessTypeStr())
                .set(StringUtil.isNotBlank(expressUpdateDTO.getFromOrderNo()), ReturnExpressDetail::getFromOrderNo, expressUpdateDTO.getFromOrderNo())
                .last("LIMIT 1")
        );
        AssertUtil.isTrue(update == 1, "更新异常,请勿重复提交!");
        List<ReturnExpressGoodAddDTO> details = expressUpdateDTO.getDetails();
        returnExpressGoodService.addOrUpdateGoodInfoBatch(details, expressUpdateDTO.getId());

        //处理结果推送WMS
        pushSkuDetailsToWMS(expressUpdateDTO, details);
        return update;
    }

    /**
     * 推送对sku的操作给WMS
     *
     * @param expressUpdateDTO
     * @param details
     */
    private void pushSkuDetailsToWMS(ReturnExpressAddDTO expressUpdateDTO, List<ReturnExpressGoodAddDTO> details) {
        ProcessingUpdateReqDTO processingUpdateReqDTO = new ProcessingUpdateReqDTO();
        processingUpdateReqDTO
                .setSku(expressUpdateDTO.getSku())
                .setProcessRemark(expressUpdateDTO.getProcessRemark())
                .setWarehouseCode(expressUpdateDTO.getWarehouseCode())
                .setOrderNo(expressUpdateDTO.getReturnNo())
                .setProcessType(configStatus.getPrCode(expressUpdateDTO.getProcessType()));
        List<ReturnDetailWMS> detailArrayList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(details)) {
            details.forEach(x -> {
                ReturnDetailWMS returnDetail = new ReturnDetailWMS();
                returnDetail
                        .setSku(x.getSku())
                        .setPutawaySku(x.getPutawaySku())
                        .setPutawayQty(x.getPutawayQty())
                        .setProcessRemark(x.getProcessRemark());
                detailArrayList.add(returnDetail);
            });
            processingUpdateReqDTO.setDetails(detailArrayList);
        }
        log.info("推送商品处理信息 {}" ,processingUpdateReqDTO);
        httpFeignClient.processingUpdate(processingUpdateReqDTO);
    }


    @Override
    public int expiredUnprocessedForecastOrder() {
        log.info("--------------更新过期未处理的预报单 开始--------------");
        int update = returnExpressMapper.update(null, Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getOverdue, 0)
                .lt(BaseEntity::getUpdateTime, LocalDate.now().minusDays(configStatus.getExpirationDays()))
                .set(ReturnExpressDetail::getOverdue, 1)
        );
        log.info("--------------更新过期未处理的预报单 结束--------------");
        return update;
    }

    @Override
    public ReturnExpressVO getInfo(Long id) {
        ReturnExpressDetail returnExpressDetail = returnExpressMapper.selectById(id);
        Optional.ofNullable(returnExpressDetail).orElseThrow(() -> new BaseException("数据不存在！"));
        ReturnExpressVO returnExpressVO = returnExpressDetail.convertThis(ReturnExpressVO.class);
        returnExpressVO.setGoodList(returnExpressGoodService.queryGoodListByExId(returnExpressVO.getId()));
        return returnExpressVO;
    }
}
