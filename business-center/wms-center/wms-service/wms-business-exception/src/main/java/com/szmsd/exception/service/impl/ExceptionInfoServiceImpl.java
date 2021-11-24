package com.szmsd.exception.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.DelOutboundFurtherHandlerDto;
import com.szmsd.delivery.vo.DelOutboundListExceptionMessageVO;
import com.szmsd.exception.domain.ExceptionInfo;
import com.szmsd.exception.dto.*;
import com.szmsd.exception.enums.ExceptionTypeEnum;
import com.szmsd.exception.enums.OrderTypeEnum;
import com.szmsd.exception.enums.ProcessTypeEnum;
import com.szmsd.exception.enums.StateSubEnum;
import com.szmsd.exception.mapper.ExceptionInfoMapper;
import com.szmsd.exception.service.IExceptionInfoService;
import com.szmsd.http.api.feign.HtpExceptionFeignService;
import com.szmsd.http.dto.ExceptionProcessRequest;
import com.szmsd.http.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author l
 * @since 2021-03-30
 */
@Service
@Slf4j
public class ExceptionInfoServiceImpl extends ServiceImpl<ExceptionInfoMapper, ExceptionInfo> implements IExceptionInfoService {

    @Resource
    private HtpExceptionFeignService htpExceptionFeignService;

    @SuppressWarnings({"all"})
    @Autowired
    private RemoteAttachmentService remoteAttachmentService;

    @Autowired
    private DelOutboundClientService delOutboundClientService;

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public ExceptionInfo selectExceptionInfoById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param exceptionInfo 模块
     * @return 模块
     */
    @Override
    public List<ExceptionInfo> selectExceptionInfoList(ExceptionInfo exceptionInfo) {
        QueryWrapper<ExceptionInfo> where = new QueryWrapper<ExceptionInfo>();
        return baseMapper.selectList(where);
    }

    @Override
    @DataScope("seller_code")
    public List<ExceptionInfo> selectExceptionInfoPage(ExceptionInfoQueryDto dto) {
        QueryWrapper<ExceptionInfo> where = new QueryWrapper<ExceptionInfo>();
        QueryWrapperUtil.filter(where, SqlKeyword.EQ, "exception_type", dto.getExceptionType());
        QueryWrapperUtil.filter(where, SqlKeyword.EQ, "seller_code", dto.getSellerCode());
        QueryWrapperUtil.filter(where, SqlKeyword.EQ, "state", dto.getState());
        QueryWrapperUtil.filterDate(where, "create_time", dto.getCreateTimes());
        if (CollectionUtils.isNotEmpty(dto.getExceptionNos())) {
            where.in("exception_no", dto.getExceptionNos());
        }
        if (CollectionUtils.isNotEmpty(dto.getOrderNos())) {
            where.in("order_no", dto.getOrderNos());
        }
        where.orderByDesc("create_time");
        List<ExceptionInfo> exceptionInfoList = baseMapper.selectList(where);
        if (CollectionUtils.isNotEmpty(exceptionInfoList)) {
            // 查询异常描述信息
            List<String> orderNos = exceptionInfoList.stream().map(ExceptionInfo::getOrderNo).collect(Collectors.toList());
            List<DelOutboundListExceptionMessageVO> exceptionMessageList = this.delOutboundClientService.exceptionMessageList(orderNos);
            if (CollectionUtils.isNotEmpty(exceptionInfoList)) {
                Map<String, String> exceptionMessageMap = exceptionMessageList.stream().collect(Collectors.toMap(DelOutboundListExceptionMessageVO::getOrderNo, DelOutboundListExceptionMessageVO::getExceptionMessage));
                for (ExceptionInfo exceptionInfo : exceptionInfoList) {
                    exceptionInfo.setExceptionMessage(exceptionMessageMap.get(exceptionInfo.getOrderNo()));
                }
            }
        }
        return exceptionInfoList;
    }

    /**
     * 新增模块
     *
     * @param newExceptionRequest 模块
     * @return 结果
     */
    @Override
    public void insertExceptionInfo(NewExceptionRequest newExceptionRequest) {
        String operationOn = newExceptionRequest.getOperateOn();
        newExceptionRequest.setOperateOn(null);
        ExceptionInfo exceptionInfo = BeanMapperUtil.map(newExceptionRequest, ExceptionInfo.class);
        if (StringUtils.isNotEmpty(operationOn)) {
            Date d = dealUTZTime(operationOn);
            exceptionInfo.setOperateOn(d);
        }
        //赋值
        if (ExceptionTypeEnum.get(exceptionInfo.getExceptionType()) == null) {
            throw new BaseException("没有查找到对应异常类型");
        }
        if (OrderTypeEnum.get(exceptionInfo.getOrderType()) == null) {
            throw new BaseException("没有查找到对应订单类型");
        }
        exceptionInfo.setExceptionTypeName(ExceptionTypeEnum.get(exceptionInfo.getExceptionType()).getName());
        exceptionInfo.setOrderTypeName(OrderTypeEnum.get(exceptionInfo.getOrderType()).getName());
        exceptionInfo.setState(StateSubEnum.DAICHULI.getCode());
        baseMapper.insert(exceptionInfo);
    }

    @Override
    public void processExceptionInfo(@RequestBody ProcessExceptionRequest processExceptionRequest) {
        QueryWrapper<ExceptionInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("exception_no", processExceptionRequest.getExceptionNo());
        if (super.count(queryWrapper) != 1) {
            throw new BaseException("异常单号不存在");
        }
        String operationOn = processExceptionRequest.getOperateOn();
        processExceptionRequest.setOperateOn(null);
        ExceptionInfo exceptionInfo = BeanMapperUtil.map(processExceptionRequest, ExceptionInfo.class);
        if (StringUtils.isNotEmpty(operationOn)) {
            Date d = dealUTZTime(operationOn);
            exceptionInfo.setOperateOn(d);
        }
        exceptionInfo.setSolveRemark(processExceptionRequest.getRemark());
        exceptionInfo.setState(StateSubEnum.YIWANCHENG.getCode());
        UpdateWrapper<ExceptionInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("exception_no", processExceptionRequest.getExceptionNo());
        super.update(exceptionInfo, updateWrapper);
    }

    /**
     * 修改模块
     *
     * @param exceptionInfo 模块
     * @return 结果
     */
    @Override
    public int updateExceptionInfo(ExceptionInfoDto exceptionInfo) {
        ExceptionInfo exception = super.getById(exceptionInfo.getId());
        ExceptionProcessRequest exceptionProcessRequest = BeanMapperUtil.map(exceptionInfo, ExceptionProcessRequest.class);
        exceptionProcessRequest.setWarehouseCode(exception.getWarehouseCode());
        exceptionProcessRequest.setExceptionNo(exception.getExceptionNo());
        if (exceptionInfo.getProcessType().equals(ProcessTypeEnum.GOONSHIPPING.getCode())) {
            try {
                DelOutboundFurtherHandlerDto delOutboundFurtherHandlerDto = new DelOutboundFurtherHandlerDto();
                delOutboundFurtherHandlerDto.setOrderNo(exception.getOrderNo());
                delOutboundClientService.furtherHandler(delOutboundFurtherHandlerDto);
            } catch (Exception e) {
                log.info(exception.getOrderNo());
            }
        }
        R<ResponseVO> r = htpExceptionFeignService.processing(exceptionProcessRequest);
        if (r == null) {
            throw new BaseException("wms服务调用失败");
        }
        if (r.getData() == null) {
            throw new BaseException("wms服务调用失败");
        }
        if (r.getData().getSuccess() == null) {
            if (r.getData().getErrors() != null) {
                throw new BaseException("传wms失败" + r.getData().getErrors());
            }
        } else {
            if (!r.getData().getSuccess()) {
                throw new BaseException("传wms失败" + r.getData().getMessage());
            }
        }
        exceptionInfo.setProcessTypeName(ProcessTypeEnum.get(exceptionInfo.getProcessType()).getName());
        exceptionInfo.setState(StateSubEnum.YICHULI.getCode());
        if (CollectionUtils.isNotEmpty(exceptionInfo.getDocumentsFiles())) {
            AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(exception.getExceptionNo()).businessItemNo(null).fileList(exceptionInfo.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.EXCEPTION_DOCUMENT).build();
            this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
        }
        return baseMapper.updateById(exceptionInfo);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteExceptionInfoByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteExceptionInfoById(String id) {
        return baseMapper.deleteById(id);
    }

    @Transactional
    @Override
    public int againTrackingNo(ExceptionDelOutboundAgainTrackingNoDto dto) {
        int i = this.delOutboundClientService.againTrackingNo(dto);
        if (i != 1) {
            throw new CommonException("999", "重新获取挂号失败");
        }
        LambdaUpdateWrapper<ExceptionInfo> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.set(ExceptionInfo::getState, "085002");
        lambdaUpdateWrapper.eq(ExceptionInfo::getId, dto.getExceptionId());
        return super.baseMapper.update(null, lambdaUpdateWrapper);
    }

    @Transactional
    @Override
    public int ignore(ExceptionInfoDto exceptionInfo) {
        // 根据订单号查询异常信息
        String orderNo = exceptionInfo.getOrderNo();
        if (StringUtils.isEmpty(orderNo)) {
            return 0;
        }
        LambdaQueryWrapper<ExceptionInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ExceptionInfo::getOrderNo, orderNo);
        queryWrapper.eq(ExceptionInfo::getState, "085001");
        if (super.count(queryWrapper) > 0) {
            LambdaUpdateWrapper<ExceptionInfo> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.set(ExceptionInfo::getState, "085002");
            updateWrapper.set(ExceptionInfo::getProcessRemark, "系统自动处理");
            updateWrapper.eq(ExceptionInfo::getOrderNo, orderNo);
            updateWrapper.eq(ExceptionInfo::getState, "085001");
            return this.baseMapper.update(null, updateWrapper);
        }
        return 0;
    }

    private Date dealUTZTime(String time) {
        Date date = new Date();
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}

