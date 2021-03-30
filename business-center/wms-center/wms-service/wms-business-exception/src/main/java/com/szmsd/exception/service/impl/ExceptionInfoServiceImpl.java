package com.szmsd.exception.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.exception.domain.ExceptionInfo;
import com.szmsd.exception.dto.ExceptionInfoQueryDto;
import com.szmsd.exception.dto.NewExceptionRequest;
import com.szmsd.exception.dto.ProcessExceptionRequest;
import com.szmsd.exception.enums.ExceptionTypeEnum;
import com.szmsd.exception.enums.OrderTypeEnum;
import com.szmsd.exception.enums.ProcessTypeEnum;
import com.szmsd.exception.mapper.ExceptionInfoMapper;
import com.szmsd.exception.service.IExceptionInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.http.api.feign.HtpExceptionFeignService;
import com.szmsd.http.dto.ExceptionProcessRequest;
import com.szmsd.http.vo.ResponseVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author l
* @since 2021-03-30
*/
@Service
public class ExceptionInfoServiceImpl extends ServiceImpl<ExceptionInfoMapper, ExceptionInfo> implements IExceptionInfoService {

    @Resource
    private HtpExceptionFeignService htpExceptionFeignService;
        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public ExceptionInfo selectExceptionInfoById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询模块列表
        *
        * @param exceptionInfo 模块
        * @return 模块
        */
        @Override
        public List<ExceptionInfo> selectExceptionInfoList(ExceptionInfo exceptionInfo)
        {
        QueryWrapper<ExceptionInfo> where = new QueryWrapper<ExceptionInfo>();
        return baseMapper.selectList(where);
        }

    @Override
    public List<ExceptionInfo> selectExceptionInfoPage(ExceptionInfoQueryDto dto){
        QueryWrapper<ExceptionInfo> where = new QueryWrapper<ExceptionInfo>();
        QueryWrapperUtil.filter(where, SqlKeyword.EQ, "exception_type", dto.getExceptionType());
        QueryWrapperUtil.filterDate(where,"create_time",dto.getCreateTimes());
        if(CollectionUtils.isNotEmpty(dto.getExceptionNos())){
            where.in("exception_no",dto.getExceptionNos());
        }
        if(CollectionUtils.isNotEmpty(dto.getOrderNos())){
            where.in("order_no",dto.getOrderNos());
        }
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

        /**
        * 新增模块
        *
        * @param newExceptionRequest 模块
        * @return 结果
        */
        @Override
        public void insertExceptionInfo(NewExceptionRequest newExceptionRequest)
        {
            String operationOn = newExceptionRequest.getOperateOn();
            newExceptionRequest.setOperateOn(null);
            ExceptionInfo exceptionInfo = BeanMapperUtil.map(newExceptionRequest, ExceptionInfo.class);
            if(StringUtils.isNotEmpty(operationOn)){
                Date  d = dealUTZTime(operationOn);
                exceptionInfo.setOperateOn(d);
            }
            //赋值
            exceptionInfo.setExceptionTypeName(ExceptionTypeEnum.get(exceptionInfo.getExceptionType()).getName());
            exceptionInfo.setOrderTypeName(OrderTypeEnum.get(exceptionInfo.getOrderType()).getName());
            exceptionInfo.setState(false);
            exceptionInfo.setDeal(false);
            baseMapper.insert(exceptionInfo);
        }
        @Override
        public void processExceptionInfo(@RequestBody ProcessExceptionRequest processExceptionRequest){
            QueryWrapper<ExceptionInfo> queryWrapper = new QueryWrapper();
            queryWrapper.eq("exception_no",processExceptionRequest.getExceptionNo());
            if(super.count(queryWrapper)!=1){
                throw  new BaseException("异常单号不存在");
            }
            String operationOn = processExceptionRequest.getOperateOn();
            processExceptionRequest.setOperateOn(null);
            ExceptionInfo exceptionInfo = BeanMapperUtil.map(processExceptionRequest, ExceptionInfo.class);
            if(StringUtils.isNotEmpty(operationOn)){
                Date d = dealUTZTime(operationOn);
                exceptionInfo.setOperateOn(d);
            }
            exceptionInfo.setSolveRemark(processExceptionRequest.getRemark());
            exceptionInfo.setState(true);
            UpdateWrapper<ExceptionInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("exception_no",processExceptionRequest.getExceptionNo());
            super.update(exceptionInfo,updateWrapper);
        }

        /**
        * 修改模块
        *
        * @param exceptionInfo 模块
        * @return 结果
        */
        @Override
        public int updateExceptionInfo(ExceptionInfo exceptionInfo)
        {
            ExceptionInfo exception =  super.getById(exceptionInfo.getId());
            ExceptionProcessRequest exceptionProcessRequest = BeanMapperUtil.map(exceptionInfo,ExceptionProcessRequest.class);
            exceptionProcessRequest.setWarehouseCode(exception.getWarehouseCode());
            exceptionProcessRequest.setExceptionNo(exception.getExceptionNo());
            R<ResponseVO> r = htpExceptionFeignService.processing(exceptionProcessRequest);
            if(!r.getData().getSuccess()){
                throw new BaseException("传wms失败" + r.getMsg());
            }
            exceptionInfo.setProcessTypeName(ProcessTypeEnum.get(exceptionInfo.getProcessType()).getName());
            exceptionInfo.setDeal(true);
            return baseMapper.updateById(exceptionInfo);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteExceptionInfoByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        @Override
        public int deleteExceptionInfoById(String id)
        {
        return baseMapper.deleteById(id);
        }


        private Date dealUTZTime(String time){
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

