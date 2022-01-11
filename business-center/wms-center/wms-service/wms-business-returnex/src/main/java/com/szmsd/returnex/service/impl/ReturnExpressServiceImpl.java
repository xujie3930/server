package com.szmsd.returnex.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.dto.returnex.ReturnDetail;
import com.szmsd.http.dto.returnex.ReturnDetailWMS;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.domain.dto.InventoryAdjustmentDTO;
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
import com.szmsd.returnex.vo.ReturnExpressGoodVO;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
    private BasFeignService basFeignService;

    @Resource
    private BaseProductFeignService baseProductFeignService;

    @Resource
    private ConfigStatus configStatus;

    @Resource
    private InventoryFeignService inventoryFeignService;

    @Resource
    private DelOutboundFeignService delOutboundFeignService;

    /**
     * 获取用户sellerCode
     *
     * @return
     */
    private String getSellCode() {
        return Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
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
        log.info("接收WMS仓库拆包明细 {}", returnProcessingReqDTO);
        ReturnExpressDetail detail = returnExpressMapper.selectOne(Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWmsWaitReceive())
                .eq(ReturnExpressDetail::getProcessType, configStatus.getUnpackingInspection())
                .eq(ReturnExpressDetail::getReturnNo, returnProcessingReqDTO.getReturnNo()));
        AssertUtil.notNull(detail, "数据不存在!");

        //wms处理中的会接收到拆包明细 拆包方式才调这个接口
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
    public <T extends ReturnExpressAddDTO> int insertReturnExpressDetail(T returnExpressAddDTO) {
        returnExpressAddDTO.setSellerCode(getSellCode());
        checkSubmit(returnExpressAddDTO);
        if (StringUtils.isBlank(returnExpressAddDTO.getExpectedNo())) {
            String expectedNo = createExpectedNo();
            returnExpressAddDTO.setExpectedNo(expectedNo);
        }
        handleExpectedCreate(returnExpressAddDTO);
        return saveReturnExpressDetail(returnExpressAddDTO.convertThis(ReturnExpressDetail.class));
    }

    /**
     * 重派则创建新的出库单 生成新的出库单，跑PRC，供应商系统，获取挂号，物流标签，费用直接扣除，不处理库存，不传WMS
     *
     * @param returnExpressAddDTO
     */
    public void makeNewOutboundOrder(ReturnExpressServiceAddDTO returnExpressAddDTO) {
        //TODO
        boolean reassign = returnExpressAddDTO.getReturnType().equals(configStatus.getReassign());
        if (reassign) {
            log.info("【重新派件】：{}", returnExpressAddDTO);
            String returnNo = returnExpressAddDTO.getReturnNo();
        }
    }

    /**
     * 调用wms 创建退件单
     *
     * @param returnExpressAddDTO
     */
    private <T extends ReturnExpressAddDTO> void handleExpectedCreate(T returnExpressAddDTO) {
        //判断如果是待提审状态的订单则不能提交 外部渠道退件，不用校验
        if (!"070003".equals(returnExpressAddDTO.getReturnType())) {
            DelOutboundListQueryDto delOutboundListQueryDto = new DelOutboundListQueryDto();
            delOutboundListQueryDto.setOrderNo(returnExpressAddDTO.getFromOrderNo());
            TableDataInfo<DelOutboundListVO> page = delOutboundFeignService.page(delOutboundListQueryDto);
            if (page != null && page.getCode() == 200) {
                List<DelOutboundListVO> rows = page.getRows();
                if (CollectionUtils.isNotEmpty(rows)) {
                    DelOutboundListVO delOutboundListVO = rows.get(0);
                    boolean equals = delOutboundListVO.getState().equals(DelOutboundStateEnum.COMPLETED.getCode());
                    AssertUtil.isTrue(equals, "该原出库单号未完成/不存在!");
                } else {
                    throw new BaseException("该原出库单号不存在!");
                }
            } else {
                throw new BaseException("获取原出库单信息失败,请重试!");
            }
        }
        String returnSource = returnExpressAddDTO.getReturnSource();
        if (!configStatus.getReturnSource().getOmsReturn().equals(returnSource)) {
            // 创建退报单 推给WMS仓库
            CreateExpectedReqDTO createExpectedReqDTO = returnExpressAddDTO.convertThis(CreateExpectedReqDTO.class);
            createExpectedReqDTO.setRefOrderNo(returnExpressAddDTO.getFromOrderNo());
            //需要转换 处理方式
            createExpectedReqDTO.setProcessType(configStatus.getPrCode(returnExpressAddDTO.getProcessType()));
            httpFeignClient.expectedCreate(createExpectedReqDTO);
        }
    }

    private void checkSubmit(ReturnExpressAddDTO returnExpressAddDTO) {
        //整包上架必须有sku
        Optional.of(returnExpressAddDTO).map(ReturnExpressAddDTO::getProcessType).ifPresent(x -> {
            boolean equals = configStatus.getWholePackageOnShelves().equals(x);
            if (equals) {
                AssertUtil.isTrue(StringUtils.isNotBlank(returnExpressAddDTO.getSku()), "整包上架，sku必填");
            }
        });

        // OMS 创建只能销毁 069001，重派 069005
        String returnSource = returnExpressAddDTO.getReturnSource();
        String processType = returnExpressAddDTO.getProcessType();
        String omsReturn = configStatus.getReturnSource().getOmsReturn();
        if (returnSource.equals(omsReturn)) {
            List<String> allProcessType = Arrays.asList(configStatus.getReassign(), configStatus.getDestroy());
            AssertUtil.isTrue(allProcessType.contains(processType), "OMS退件预报只支持,销毁/重派");
        } else {
            List<String> allProcessType = Collections.singletonList(configStatus.getReassign());
            AssertUtil.isTrue(!allProcessType.contains(processType), "退件预报暂不支持重派");
        }

        // 校验重复条件
        String fromOrderNo = returnExpressAddDTO.getFromOrderNo();
        Integer integer = returnExpressMapper.selectCount(Wrappers.<ReturnExpressDetail>lambdaQuery()
                //唯一 必填
                .eq(ReturnExpressDetail::getScanCode, returnExpressAddDTO.getScanCode())
                .or().eq(StringUtils.isNotBlank(fromOrderNo), ReturnExpressDetail::getFromOrderNo, fromOrderNo)
                .select(ReturnExpressDetail::getId));
        AssertUtil.isTrue(integer == 0, "退件可扫描编码/原出库单号不能重复");
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
        log.info("接收wms 仓库到件信息{}", returnArrivalReqDTO);
        if (StringUtils.isNotBlank(returnArrivalReqDTO.getExpectedNo())) {
            //到货 拆包检查的 需要接收商品详情
            ReturnExpressDetail returnExpressDetailCheck = returnExpressMapper.selectOne(Wrappers.<ReturnExpressDetail>lambdaUpdate()
                    .eq(ReturnExpressDetail::getExpectedNo, returnArrivalReqDTO.getExpectedNo())
                    .eq(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWmsWaitReceive()).last("LIMIT 1"));
            AssertUtil.notNull(returnExpressDetailCheck, "数据不存在!");
            String dealStatus = configStatus.getDealStatus().getWaitCustomerDeal();
            String dealStatusStr = configStatus.getDealStatus().getWaitCustomerDealStr();
            // 拆包/销毁 整包 需要等待接收其他接口 拆包 G2 需要用户处理，销毁 整包 G3直接结束流程
            String processType = returnExpressDetailCheck.getProcessType();
            boolean isOpenAndCheck = processType.equals(configStatus.getUnpackingInspection());
            boolean isDestroy = processType.equals(configStatus.getDestroy()) || processType.equals(configStatus.getWholePackageOnShelves());
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
                    .set(isDestroy, ReturnExpressDetail::getFinishTime, LocalDateTime.now())
                    .set(ReturnExpressDetail::getArrivalTime, LocalDateTime.now())
                    .set(ReturnExpressDetail::getDealStatus, dealStatus)
                    .set(ReturnExpressDetail::getDealStatusStr, dealStatusStr)
            );
            return update;
        } else {
            // 新增无主件 状态待指派
            ReturnExpressDetail returnExpressDetail = returnArrivalReqDTO.convertThis(ReturnExpressDetail.class);
            returnExpressDetail.setReturnSource(configStatus.getReturnSource().getWmsReturn());
            returnExpressDetail.setReturnSourceStr(configStatus.getReturnSource().getWmsReturnStr());
            returnExpressDetail.setArrivalTime(LocalDateTime.now());
            if (StringUtils.isNotBlank(returnExpressDetail.getSellerCode())) {
                returnExpressDetail.setDealStatus(configStatus.getDealStatus().getWaitCustomerDeal());
                returnExpressDetail.setDealStatusStr(configStatus.getDealStatus().getWaitCustomerDealStr());
            } else {
                returnExpressDetail.setDealStatus(configStatus.getDealStatus().getWaitAssigned());
                returnExpressDetail.setDealStatusStr(configStatus.getDealStatus().getWaitAssignedStr());
            }
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
        log.info("接收WMS仓库最终退件处理结果 {}", returnProcessingReqDTO);
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

        //回调后 如果是上架则需要更新库存
        addSkuInventory(returnProcessingReqDTO, dealStatus);
        return update;
    }

    /**
     * 增加sku库存数量
     *
     * @param returnProcessingReqDTO
     * @param dealStatus
     */
    private void addSkuInventory(ReturnProcessingFinishReqDTO returnProcessingReqDTO, String dealStatus) {
        ReturnExpressDetail returnExpressDetail = returnExpressMapper.selectOne(Wrappers.<ReturnExpressDetail>lambdaQuery()
                .eq(ReturnExpressDetail::getDealStatus, dealStatus)
                .eq(ReturnExpressDetail::getReturnNo, returnProcessingReqDTO.getReturnNo())
                .last("LIMIT 1")
        );

        String warehouseCode = returnExpressDetail.getWarehouseCode();
        String sellerCode = returnExpressDetail.getSellerCode();
        //拆包上架
        if (returnExpressDetail.getProcessType().equals(configStatus.getPutawayByDetail())) {
            log.info("拆包后按明细上架--上架的商品需要更新库存管理的sku库存数量");
            Integer id = returnExpressDetail.getId();
            List<ReturnExpressGoodVO> goodVOList = returnExpressGoodService.queryGoodListByExId(id);
            goodVOList = merage(goodVOList);
            // 把details里面的sku 更新到对应的库存管理的数量里面
            List<String> collect = goodVOList.stream().map(ReturnExpressGoodVO::getPutawaySku).collect(Collectors.toList());
            String sku = String.join(",", collect);
            //2021-07-06取消 查询，没有的sku会在之前的校验，sku没有库存信息会 无法更新
//            TableDataInfo<InventorySkuVO> page = inventoryFeignService.page(warehouseCode, sku, sellerCode, collect.size());
//            log.info("warehouseCode:{},sku:{},sellerCode:{},查询到的商品sku信息: {}", warehouseCode, sku, sellerCode, JSONObject.toJSONString(page.getRows()));
//            AssertUtil.isTrue(page.getCode() == 200, "获取库存信息失败!");
//            List<InventorySkuVO> rows = page.getRows();
            Map<String, Integer> needAddSkuNum = goodVOList.stream().collect(Collectors.toMap(ReturnExpressGoodVO::getPutawaySku, ReturnExpressGoodVO::getPutawayQty));
            //根据sku 更新库存
            collect.forEach(x -> {
                InventoryAdjustmentDTO inventoryAdjustmentDTO = new InventoryAdjustmentDTO();
                //一直为增加
                inventoryAdjustmentDTO.setSku(x);
                inventoryAdjustmentDTO.setAdjustment("5");
                Integer skuAddNum = needAddSkuNum.get(x);
                inventoryAdjustmentDTO.setWarehouseCode(warehouseCode);
                inventoryAdjustmentDTO.setSellerCode(sellerCode);
                inventoryAdjustmentDTO.setQuantity(skuAddNum);
                inventoryAdjustmentDTO.setFormReturn(true);
                inventoryAdjustmentDTO.setReceiptNo(returnProcessingReqDTO.getReturnNo());
                log.info("拆包上架更新库存数据{}", JSONObject.toJSONString(inventoryAdjustmentDTO));
                inventoryFeignService.adjustment(inventoryAdjustmentDTO);
            });
        }
        if (returnExpressDetail.getProcessType().equals(configStatus.getWholePackageOnShelves())) {
            log.info("整包上架--上架的商品需要更新库存管理的sku库存数量 + 1 ");
            String sku = returnExpressDetail.getSku();
//            TableDataInfo<InventorySkuVO> page = inventoryFeignService.page(warehouseCode, sku, sellerCode, 1);
//            AssertUtil.isTrue(page.getCode() == 200, "获取库存信息失败!");
//            List<InventorySkuVO> rows = page.getRows();
//            //整包上架直接+1
//            rows.forEach(x -> {
            InventoryAdjustmentDTO inventoryAdjustmentDTO = new InventoryAdjustmentDTO();
//                BeanUtils.copyProperties(x, inventoryAdjustmentDTO);
            //一直为增加
            inventoryAdjustmentDTO.setAdjustment("5");
            inventoryAdjustmentDTO.setQuantity(1);
            inventoryAdjustmentDTO.setWarehouseCode(warehouseCode);
            inventoryAdjustmentDTO.setSku(sku);
            inventoryAdjustmentDTO.setSellerCode(sellerCode);
            inventoryAdjustmentDTO.setFormReturn(true);
            inventoryAdjustmentDTO.setReceiptNo(returnProcessingReqDTO.getReturnNo());
            log.info("整包上架更新库存数据{}", JSONObject.toJSONString(inventoryAdjustmentDTO));
            inventoryFeignService.adjustment(inventoryAdjustmentDTO);
//            });
        }
    }

    private List<ReturnExpressGoodVO> merage(List<ReturnExpressGoodVO> details) {
        return new ArrayList<>(details.stream()
                .collect(Collectors.toMap(ReturnExpressGoodVO::getPutawaySku, a -> a, (o1, o2) -> {
                    o1.setPutawayQty(o1.getPutawayQty() + o2.getPutawayQty());
                    return o1;
                })).values());
    }

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T extends ReturnExpressAddDTO> int updateExpressInfo(T expressUpdateDTO) {
        //如果之前是销毁，已经结束了，到这都是要么销毁，要么拆包上架 还有WMS通知退件等待
        checkBeforeUpdate(expressUpdateDTO);
        String dealStatus;
        String dealStatusStr;
        if (expressUpdateDTO.getProcessType().equals(configStatus.getUnpackingInspection())) {
            dealStatus = configStatus.getDealStatus().getWmsWaitReceive();
            dealStatusStr = configStatus.getDealStatus().getWmsWaitReceiveStr();
        } else {
            // 处理完后状态
            dealStatus = configStatus.getDealStatus().getWmsReceivedDealWay();
            dealStatusStr = configStatus.getDealStatus().getWmsReceivedDealWayStr();
        }

        int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getId, expressUpdateDTO.getId())
                .eq(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWaitCustomerDeal())

                .set(ReturnExpressDetail::getSku, expressUpdateDTO.getSku())
                .set(ReturnExpressDetail::getDealStatus, dealStatus)
                .set(ReturnExpressDetail::getDealStatusStr, dealStatusStr)
                .set(expressUpdateDTO.getProcessType() != null, ReturnExpressDetail::getProcessType, expressUpdateDTO.getProcessType())
                .set(expressUpdateDTO.getProcessTypeStr() != null, ReturnExpressDetail::getProcessTypeStr, expressUpdateDTO.getProcessTypeStr())
                .set(StringUtil.isNotBlank(expressUpdateDTO.getFromOrderNo()), ReturnExpressDetail::getFromOrderNo, expressUpdateDTO.getFromOrderNo())
                .last("LIMIT 1")
        );
        AssertUtil.isTrue(update == 1, "更新异常,请勿重复提交!");
        List<ReturnExpressGoodAddDTO> details = expressUpdateDTO.getGoodList();
        returnExpressGoodService.addOrUpdateGoodInfoBatch(details, expressUpdateDTO.getId());
        //上架处理校验是否属于该用户的sku
        checkSku(expressUpdateDTO);
        //处理结果推送WMS
        pushSkuDetailsToWMS(expressUpdateDTO, details);

        return update;
    }

    /**
     * 校验该sku是否属于该用户
     *
     * @param expressUpdateDTO
     */
    private void checkSku(ReturnExpressAddDTO expressUpdateDTO) {
        List<ReturnExpressGoodAddDTO> details = expressUpdateDTO.getGoodList();
        if (CollectionUtils.isEmpty(details)) {
            log.info("无商品数据，不校验商品sku");
        }
        List<String> skuIdList = details.stream().map(ReturnExpressGoodAddDTO::getPutawaySku).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        log.info("需要上架的：{}", skuIdList);
        if (CollectionUtils.isEmpty(skuIdList)) return;
        BaseProductConditionQueryDto baseProductConditionQueryDto = new BaseProductConditionQueryDto();
        String sellCode = getSellCode();
        baseProductConditionQueryDto.setSellerCode(sellCode);
        baseProductConditionQueryDto.setSkus(skuIdList);
        log.info("查询sku信息 {}", JSONObject.toJSONString(baseProductConditionQueryDto));
        R<List<BaseProduct>> listR = baseProductFeignService.queryProductList(baseProductConditionQueryDto);
        AssertUtil.isTrue(HttpStatus.SUCCESS == listR.getCode(), "校验sku异常：" + listR.getMsg());
        List<BaseProduct> data = listR.getData();
        List<String> returnIdList = data.stream().map(BaseProduct::getCode).collect(Collectors.toList());
        log.info("查询到的sku信息：{}", returnIdList);
        skuIdList.removeAll(returnIdList);
        if (CollectionUtils.isNotEmpty(skuIdList)) {
            log.info("未查询到的数据：{}", skuIdList);
            throw new BaseException("未查询到该SKU: " + String.join(" ", skuIdList) + "数据");
        }

    }


    /**
     * 更新前校验
     *
     * @param expressUpdateDTO
     */
    private void checkBeforeUpdate(ReturnExpressAddDTO expressUpdateDTO) {
        log.info("更新退单信息 req:{}", expressUpdateDTO);
        expressUpdateDTO.setSellerCode(getSellCode());
        AssertUtil.isTrue(expressUpdateDTO.getId() != null && expressUpdateDTO.getId() > 0, "更新异常！");
        //如果是拆包，不可以整包上架 前段控制;
        ReturnExpressDetail returnExpressDetailCheck = returnExpressMapper.selectById(expressUpdateDTO.getId());
        AssertUtil.notNull(returnExpressDetailCheck, "数据不存在!");
        boolean isOpenAndCheck = configStatus.getUnpackingInspection().equals(returnExpressDetailCheck.getProcessType());

        if (isOpenAndCheck) {
            //该次使用的processType
            String processType = expressUpdateDTO.getProcessType();
            //之前是拆包 拆包后只能销毁和按明细上架
            boolean unpackAndPutOnTheShelf = configStatus.getPutawayByDetail().equals(processType);
            if (unpackAndPutOnTheShelf) {
                // 如果是按明细上架，则所有的明细需要设置相对应的数量和sku sku 可以相同
                List<ReturnExpressGoodAddDTO> details = expressUpdateDTO.getGoodList();
                AssertUtil.isTrue(CollectionUtils.isNotEmpty(details), "按明细上架，明细列表不能为空!");
                Optional.of(details).filter(CollectionUtils::isNotEmpty).ifPresent(x -> {
                    AtomicBoolean mustHadOne = new AtomicBoolean(false);
                    x.forEach(detail -> {
                        detail.check();
                        if (detail.getPutawayQty() != null && detail.getPutawayQty() > 0) {
                            mustHadOne.set(true);
                        }
                    });
                    AssertUtil.isTrue(mustHadOne.get(), "明细中必须要存在一个sku大于0");
                });
            }
            boolean b = configStatus.getDestroy().equals(processType) || unpackAndPutOnTheShelf;
            AssertUtil.isTrue(b, "拆包检查后只能按明细上架/销毁");
        }
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
        log.info("推送商品处理信息 {}", processingUpdateReqDTO);
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
