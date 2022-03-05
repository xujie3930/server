package com.szmsd.delivery.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelSrmCostDetail;
import com.szmsd.delivery.domain.DelSrmCostLog;
import com.szmsd.delivery.enums.DelSrmCostLogConstant;
import com.szmsd.delivery.mapper.DelSrmCostLogMapper;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.IDelSrmCostDetailService;
import com.szmsd.delivery.service.IDelSrmCostLogService;
import com.szmsd.http.api.service.IHtpSrmClientService;
import com.szmsd.http.dto.PackageCostRequest;
import com.szmsd.http.vo.OperationResultOfIListOfPackageCost;
import com.szmsd.http.vo.PackageCost;
import com.szmsd.http.vo.PackageCostItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    //                                            0   1   2   3   4   5   6   7   8    9    10   11
    private final int[] retryTimeConfiguration = {30, 30, 60, 60, 60, 60, 60, 60, 180, 180, 180, 180};
    public static final int retryCount = 10;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_SRM_REQUEST)
    @Override
    public void handler(DelSrmCostLog delSrmCostLog) {
        Long id = delSrmCostLog.getId();
        String lockName = applicationName + ":DelSrmCostLogServiceImpl:" + id;
        RLock lock = redissonClient.getLock(lockName);
        try {
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                String responseBody;
                int failCount = delSrmCostLog.getFailCount();
                String state;
                long st = System.currentTimeMillis();
                Date nextRetryTime = null;
                boolean success = false;
                DelSrmCostLogConstant.Type type = DelSrmCostLogConstant.Type.valueOf(delSrmCostLog.getType());
                OperationResultOfIListOfPackageCost httpResponseVO = null;
                try {

                    PackageCostRequest httpRequestDto = new PackageCostRequest();
                    httpRequestDto.setCurrency(delSrmCostLog.getCurrencyCode());
                    httpRequestDto.setProcessNoList(Arrays.asList(delSrmCostLog.getOrderNo()));
                    httpResponseVO = htpSrmClientService.packageCostBatch(httpRequestDto);
                    if (httpResponseVO.getSucceeded()){
                        success = true;
                    }
                    responseBody = (String) JSON.toJSONString(httpResponseVO);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    responseBody = e.getMessage();
                    if (null == responseBody) {
                        responseBody = "请求失败";
                    }
                }
                if (success) {
                    state = DelSrmCostLogConstant.State.SUCCESS.name();

                    DelOutbound delOutbound = this.delOutboundService.getByOrderNo(delSrmCostLog.getOrderNo());
                    if (null == delOutbound) {
                        return;
                    }


                    //生成成本明细,原来有的情况下，先删除老的数据
                    DelSrmCostDetail dataDelSrmCostDetail = delSrmCostDetailService.getByOrderNo(delSrmCostLog.getOrderNo());
                    if(dataDelSrmCostDetail != null){
                        delSrmCostDetailService.deleteDelSrmCostDetailById(String.valueOf(dataDelSrmCostDetail.getId()));
                    }
                    DelSrmCostDetail delSrmCostDetail = new DelSrmCostDetail();
                    delSrmCostDetail.setOrderNo(delOutbound.getOrderNo());
                    delSrmCostDetail.setOrderTime(delOutbound.getCreateTime());
                    delSrmCostDetail.setCreateTime(new Date());


                    //接口返回参数拼接
                    if(httpResponseVO.getData() != null && httpResponseVO.getData().size() > 0){
                        PackageCost packageCost = httpResponseVO.getData().get(0);
                        if(packageCost.getCostItems() != null && packageCost.getCostItems().size() > 0){
                            PackageCostItem packageCostItem = packageCost.getCostItems().get(0);
                            delSrmCostDetail.setProductCode(packageCostItem.getServiceName());
                            delSrmCostDetail.setPdCode(packageCostItem.getServiceCode());
                            delSrmCostDetail.setCuspriceCode(packageCost.getProcessNo());
                            delSrmCostDetail.setAmount(packageCostItem.getAmountCost().getAmount());
                            delSrmCostDetail.setCurrencyCode(packageCostItem.getAmountCost().getCurrencyCode());
                            java.util.Map<String, Object> responseBodyMap = new HashMap();
                            responseBodyMap.put("productCode", delSrmCostDetail.getProductCode());
                            responseBodyMap.put("pdCode", delSrmCostDetail.getPdCode());
                            responseBodyMap.put("cuspriceCode", delSrmCostDetail.getCuspriceCode());
                            responseBodyMap.put("amount", delSrmCostDetail.getAmount());
                            responseBodyMap.put("currencyCode", delSrmCostDetail.getCurrencyCode());
                            responseBody = (String) JSON.toJSONString(responseBody);
                        }


                    }
                    delSrmCostDetailService.insertDelSrmCostDetail(delSrmCostDetail);
                    
                    
                } else {
                    failCount++;
                    if (failCount >= retryCount) {
                        state = DelSrmCostLogConstant.State.FAIL.name();
                    } else {
                        state = DelSrmCostLogConstant.State.FAIL_CONTINUE.name();
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

