package com.szmsd.delivery.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.*;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelSrmCostLogEnum;
import com.szmsd.delivery.mapper.DelSrmCostLogMapper;
import com.szmsd.delivery.service.*;
import com.szmsd.http.api.feign.HtpWarMappingFeignService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.api.service.IHtpSrmClientService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.*;
import com.szmsd.http.vo.ChargeItem;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
* <p>
    * 出库单SRC成本调用日志 服务实现类
    * </p>
*
* @author Administrator
* @since 2022-03-04
*/
@Service
public class DelSrmCostLogServiceImpl extends ServiceImpl<DelSrmCostLogMapper, DelSrmCostLog> implements IDelSrmCostLogService {
    private final Logger logger = LoggerFactory.getLogger(DelSrmCostLogServiceImpl.class);

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IHtpSrmClientService htpSrmClientService;
    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelSrmCostDetailService delSrmCostDetailService;
    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;
    @Resource
    private HtpWarMappingFeignService htpWarMappingFeignService;
    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;

    @Autowired
    private IDelOutboundDetailService delOutboundDetailService;
    //                                            0   1   2   3   4   5   6   7   8    9    10   11
    private final int[] retryTimeConfiguration = {30, 30, 60, 60, 60, 60, 60, 60, 180, 180, 180, 180};
    public static final int retryCount = 10;


    /**
     * 成本更新时接口调用
     * @param delOutbound
     * @param delSrmCostDetail
     */
    private String processForUpdate(DelOutbound delOutbound, DelSrmCostDetail delSrmCostDetail){

        PackageCostRequest httpRequestDto = new PackageCostRequest();
        httpRequestDto.setProcessNoList(Arrays.asList(delOutbound.getOrderNo()));
        OperationResultOfIListOfPackageCost httpResponseVO = htpSrmClientService.packageCostBatch(httpRequestDto);
        if (httpResponseVO.getSucceeded() && httpResponseVO.getData().size() > 0 &&
                httpResponseVO.getData().get(0).getIsSucesss()){
            //接口返回参数拼接
            PackageCost packageCost = httpResponseVO.getData().get(0);
            if(packageCost.getCostItems() != null && packageCost.getCostItems().size() > 0){
                PackageCostItem packageCostItem = packageCost.getCostItems().get(0);
                delSrmCostDetail.setProductCode(packageCostItem.getServiceName());
                delSrmCostDetail.setPdCode(packageCostItem.getServiceCode());
                delSrmCostDetail.setCuspriceCode(packageCost.getProcessNo());
                delSrmCostDetail.setAmount(packageCostItem.getAmountCost().getAmount());
                delSrmCostDetail.setCurrencyCode(packageCostItem.getAmountCost().getCurrencyCode());
            }



            return null;
        }else{

            return JSON.toJSONString(httpResponseVO);
        }
    }


    /**
     * 成本第一次获取是调用-LogisticsService
     * @param delOutbound
     * @param delSrmCostDetail
     */
    private String processForService(DelOutbound delOutbound, DelSrmCostDetail delSrmCostDetail) {
        String warehouseCode = delOutbound.getWarehouseCode();
        R<String> r = this.htpWarMappingFeignService.getMappingWarCode(delOutbound.getWarehouseCode());
        if(r.getCode() == 200){
            warehouseCode = r.getData();
        }else{
            log.error("ck1仓库代码映射获取时失败:"+delOutbound.getWarehouseCode()+",msg:"+r.getMsg());
        }

        AnalysisInfo httpRequestDto = new AnalysisInfo()
        .setService(delOutbound.getSupplierCalcId())
        .setRefNo(delOutbound.getOrderNo())
        .setWarehouseCode(warehouseCode).setStartNode(warehouseCode);
        logger.info("processForService计算时原仓库代码:"+delOutbound.getWarehouseCode()+",转换后仓库代码:"+warehouseCode);


        // 包裹
        AnalysisInfoPackageInfo packages = new AnalysisInfoPackageInfo();
        packages.setWeight(new Weight().setUnit(delOutbound.getCalcWeightUnit()).setValue(new BigDecimal(delOutbound.getWeight())));
        packages.setQuantity(1);


        // 尺寸
        packages.setPacking(new AnalysisInfoPacking()
                .setHeight(new BigDecimal(delOutbound.getHeight()))
                .setLength(new BigDecimal(delOutbound.getLength()))
                .setWidth(new BigDecimal(delOutbound.getWidth()))
                .setUnit("cm")
        );

        httpRequestDto.setPackages(Arrays.asList(packages));


        LambdaQueryWrapper<DelOutboundAddress> outboundAddressLambdaQueryWrapper = Wrappers.lambdaQuery();
        outboundAddressLambdaQueryWrapper.eq(DelOutboundAddress::getOrderNo, delOutbound.getOrderNo());
        DelOutboundAddress delOutboundAddress = delOutboundAddressService.getOne(outboundAddressLambdaQueryWrapper);
        if (Objects.nonNull(delOutboundAddress)) {
            httpRequestDto.setToAddress(new AnalysisInfoAddress()
                    .setCountryCode(delOutboundAddress.getCountryCode())
                    .setCountryName(delOutboundAddress.getCountry())
                    .setCity(delOutboundAddress.getCity())
                    .setProvince(delOutboundAddress.getStateOrProvince())
                    .setPostcode(delOutboundAddress.getPostCode())
            );
        }

        OperationResultOfChargeWrapperOfPricingChargeInfo httpResponseVO = htpSrmClientService.pricingService(httpRequestDto);

        if (httpResponseVO.getSucceeded()  != null && httpResponseVO.getSucceeded() && httpResponseVO.getData() != null){
            ChargeWrapperOfPricingChargeInfo data = httpResponseVO.getData();

            if(data.getData() != null){
                delSrmCostDetail.setProductCode(data.getData().getServiceName());
                delSrmCostDetail.setPdCode(data.getData().getServiceId());
                delSrmCostDetail.setCuspriceCode(data.getData().getPricingSheet());


            }

            BigDecimal amount = new BigDecimal(0);
            String currencyCode = null;
            for (ChargeItem item : data.getCharges()){

                if(item.getMoney() != null){
                    amount = amount.add(item.getMoney().getAmount());
                    currencyCode = item.getMoney().getCurrencyCode();
                }
            }

            delSrmCostDetail.setAmount(amount);
            delSrmCostDetail.setCurrencyCode(currencyCode);

            return null;

        }else{

            return JSON.toJSONString(httpResponseVO);
        }
    }

    /**
     * 成本第一次获取是调用-LogisticsRoute
     * @param delOutbound
     * @param delSrmCostDetail
     */
    private String processForRoute(DelOutbound delOutbound, DelSrmCostDetail delSrmCostDetail) {
        String warehouseCode = delOutbound.getWarehouseCode();
        R<String> r = this.htpWarMappingFeignService.getMappingWarCode(delOutbound.getWarehouseCode());
        if(r.getCode() == 200){
            warehouseCode = r.getData();
        }else{
            log.error("ck1仓库代码映射获取时失败:"+delOutbound.getWarehouseCode()+",msg:"+r.getMsg());
        }
        AnalysisInfo httpRequestDto = new AnalysisInfo()
                .setRouteId(delOutbound.getSupplierCalcId())
                .setRefNo(delOutbound.getOrderNo())
                .setWarehouseCode(warehouseCode).setStartNode(warehouseCode);

        logger.info("processForRoute计算时原仓库代码:"+delOutbound.getWarehouseCode()+",转换后仓库代码:"+warehouseCode);

        // 包裹
        AnalysisInfoPackageInfo packages = new AnalysisInfoPackageInfo();
        packages.setWeight(new Weight().setUnit(delOutbound.getCalcWeightUnit()).setValue(new BigDecimal(delOutbound.getWeight())));
        packages.setQuantity(1);


        // 尺寸
        packages.setPacking(new AnalysisInfoPacking()
                .setHeight(new BigDecimal(delOutbound.getHeight()))
                .setLength(new BigDecimal(delOutbound.getLength()))
                .setWidth(new BigDecimal(delOutbound.getWidth()))
                .setUnit("cm")
        );


        httpRequestDto.setPackages(Arrays.asList(packages));


        LambdaQueryWrapper<DelOutboundAddress> outboundAddressLambdaQueryWrapper = Wrappers.lambdaQuery();
        outboundAddressLambdaQueryWrapper.eq(DelOutboundAddress::getOrderNo, delOutbound.getOrderNo());
        DelOutboundAddress delOutboundAddress = delOutboundAddressService.getOne(outboundAddressLambdaQueryWrapper);
        if (Objects.nonNull(delOutboundAddress)) {
            httpRequestDto.setToAddress(new AnalysisInfoAddress()
                    .setCountryCode(delOutboundAddress.getCountryCode())
                    .setCountryName(delOutboundAddress.getCountry())
                    .setCity(delOutboundAddress.getCity())
                    .setProvince(delOutboundAddress.getStateOrProvince())
                    .setPostcode(delOutboundAddress.getPostCode())
            );
        }

        OperationResultOfAnalysisRouteResult httpResponseVO = htpSrmClientService.routePathRoute(httpRequestDto);

        if (httpResponseVO.getSucceeded() != null && httpResponseVO.getSucceeded() && httpResponseVO.getData() != null && httpResponseVO.getData().getLinks().size() > 0){
            ChargeWrapperOfPricingChargeInfo data = httpResponseVO.getData().getLinks().get(0).getChargeModel();

            if(data.getData() != null){
                delSrmCostDetail.setProductCode(data.getData().getServiceName());
                delSrmCostDetail.setPdCode(data.getData().getServiceId());
                delSrmCostDetail.setCuspriceCode(data.getData().getPricingSheet());


            }

            BigDecimal amount = new BigDecimal(0);
            String currencyCode = null;
            for (ChargeItem item : data.getCharges()){

                if(item.getMoney() != null){
                    amount = amount.add(item.getMoney().getAmount());
                    currencyCode = item.getMoney().getCurrencyCode();
                }
            }

            delSrmCostDetail.setAmount(amount);
            delSrmCostDetail.setCurrencyCode(currencyCode);

            return null;

        }else{

            return JSON.toJSONString(httpResponseVO);
        }
    }

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_SRM_REQUEST)
    @Override
    public void handler(DelSrmCostLog delSrmCostLog) {
        Long id = delSrmCostLog.getId();
        String lockName = applicationName + ":DelSrmCostLogServiceImpl:" + id;
        RLock lock = redissonClient.getLock(lockName);
        try {
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                String responseBody = null;
                int failCount = delSrmCostLog.getFailCount();
                String state;
                long st = System.currentTimeMillis();
                Date nextRetryTime = null;
                boolean success = false;
                DelSrmCostLogEnum.Type type = DelSrmCostLogEnum.Type.valueOf(delSrmCostLog.getType());
                DelOutbound delOutbound = null;
                DelSrmCostDetail delSrmCostDetail = new DelSrmCostDetail();
                try {
                    delOutbound = this.delOutboundService.getByOrderNo(delSrmCostLog.getOrderNo());
                    if (null == delOutbound) {
                        throw new RuntimeException("单号不存在系统"+delSrmCostLog.getOrderNo());
                    }

                    logger.info("开始计算成本数据"+type+"："+JSON.toJSONString(delOutbound));

                    if(type == DelSrmCostLogEnum.Type.create){

                        if(DelOutboundConstant.SUPPLIER_CALC_TYPE_SERVICE.equals(delOutbound.getSupplierCalcType())){
                            responseBody = processForService(delOutbound, delSrmCostDetail);

                        }else/* if(DelOutboundConstant.SUPPLIER_CALC_TYPE_ROUTE_ID.equals(delOutbound.getSupplierCalcType()))*/{
                            responseBody = processForRoute(delOutbound, delSrmCostDetail);

                        }

                    }else if(type == DelSrmCostLogEnum.Type.update){

                        responseBody = processForUpdate(delOutbound, delSrmCostDetail);
                    }


                    if(responseBody == null){
                        success = true;
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    responseBody = e.getMessage();
                    if (null == responseBody) {
                        responseBody = "请求失败";
                    }
                }
                if (success) {
                    state = DelSrmCostLogEnum.State.SUCCESS.name();

                    //生成成本明细,原来有的情况下，先删除老的数据
                    DelSrmCostDetail dataDelSrmCostDetail = delSrmCostDetailService.getByOrderNo(delSrmCostLog.getOrderNo());
                    if(dataDelSrmCostDetail != null){
                        delSrmCostDetailService.deleteDelSrmCostDetailById(String.valueOf(dataDelSrmCostDetail.getId()));
                    }
                    delSrmCostDetail.setOrderNo(delOutbound.getOrderNo());
                    delSrmCostDetail.setOrderTime(delOutbound.getCreateTime());
                    delSrmCostDetail.setCreateTime(new Date());


                    //接口返回参数拼接
                    java.util.Map<String, Object> responseBodyMap = new HashMap();
                    responseBodyMap.put("productCode", delSrmCostDetail.getProductCode());
                    responseBodyMap.put("pdCode", delSrmCostDetail.getPdCode());
                    responseBodyMap.put("cuspriceCode", delSrmCostDetail.getCuspriceCode());
                    responseBodyMap.put("amount", delSrmCostDetail.getAmount());
                    responseBodyMap.put("currencyCode", delSrmCostDetail.getCurrencyCode());
                    responseBody = (String) JSON.toJSONString(responseBody);
                    delSrmCostDetailService.insertDelSrmCostDetail(delSrmCostDetail);
                    if(type == DelSrmCostLogEnum.Type.create) {
                        //D3 更新出库单一件多票的单据匹配关系
                        this.updateShipmentMultiboxrelation(delOutbound);
                    }




                } else {

                    failCount++;
                    if (failCount >= retryCount) {
                        state = DelSrmCostLogEnum.State.FAIL.name();
                    } else {
                        state = DelSrmCostLogEnum.State.FAIL_CONTINUE.name();
                        int t = retryTimeConfiguration[failCount];
                        nextRetryTime = DateUtils.addSeconds(delSrmCostLog.getNextRetryTime(), t);
                    }
                }
                int lastRequestConsumeTime = (int) (System.currentTimeMillis() - st);
                LambdaUpdateWrapper<DelSrmCostLog> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.set(DelSrmCostLog::getState, state);
                updateWrapper.set(DelSrmCostLog::getFailCount, failCount);
                updateWrapper.set(DelSrmCostLog::getResponseBody, responseBody);
                updateWrapper.set(DelSrmCostLog::getLastRequestConsumeTime, lastRequestConsumeTime);
                updateWrapper.set(DelSrmCostLog::getNextRetryTime, nextRetryTime);
                updateWrapper.eq(DelSrmCostLog::getId, delSrmCostLog.getId());
                super.update(updateWrapper);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void updateShipmentMultiboxrelation(DelOutbound delOutbound){
        ShipmentMultiboxrelationRequestDto dto = new ShipmentMultiboxrelationRequestDto();
        List<ShipmentMultiboxrelationDetailDto> details = new ArrayList<ShipmentMultiboxrelationDetailDto>();

        dto.setOrderNo(delOutbound.getOrderNo());
        dto.setWarehouseCode(delOutbound.getWarehouseCode());
        dto.setDetails(details);
        List<DelOutboundDetail> delOutboundDetailList = delOutboundDetailService.listByOrderNo(delOutbound.getOrderNo());

        for (DelOutboundDetail detail: delOutboundDetailList){
            ShipmentMultiboxrelationDetailDto detailDto = new ShipmentMultiboxrelationDetailDto();
            detailDto.setSku(detail.getSku());
            detailDto.setBoxNo(detail.getBoxMark());
            detailDto.setTrackingNo(detail.getTraceId());
            details.add(detailDto);

        }

        htpOutboundClientService.shipmentMultiboxrelation(dto);


    }
        /**
        * 查询出库单SRC成本调用日志模块
        *
        * @param id 出库单SRC成本调用日志模块ID
        * @return 出库单SRC成本调用日志模块
        */
        @Override
        public DelSrmCostLog selectDelSrmCostLogById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询出库单SRC成本调用日志模块列表
        *
        * @param delSrmCostLog 出库单SRC成本调用日志模块
        * @return 出库单SRC成本调用日志模块
        */
        @Override
        public List<DelSrmCostLog> selectDelSrmCostLogList(DelSrmCostLog delSrmCostLog)
        {
        QueryWrapper<DelSrmCostLog> where = new QueryWrapper<DelSrmCostLog>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增出库单SRC成本调用日志模块
        *
        * @param delSrmCostLog 出库单SRC成本调用日志模块
        * @return 结果
        */
        @Override
        public int insertDelSrmCostLog(DelSrmCostLog delSrmCostLog)
        {
        return baseMapper.insert(delSrmCostLog);
        }

        /**
        * 修改出库单SRC成本调用日志模块
        *
        * @param delSrmCostLog 出库单SRC成本调用日志模块
        * @return 结果
        */
        @Override
        public int updateDelSrmCostLog(DelSrmCostLog delSrmCostLog)
        {
        return baseMapper.updateById(delSrmCostLog);
        }

        /**
        * 批量删除出库单SRC成本调用日志模块
        *
        * @param ids 需要删除的出库单SRC成本调用日志模块ID
        * @return 结果
        */
        @Override
        public int deleteDelSrmCostLogByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除出库单SRC成本调用日志模块信息
        *
        * @param id 出库单SRC成本调用日志模块ID
        * @return 结果
        */
        @Override
        public int deleteDelSrmCostLogById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

