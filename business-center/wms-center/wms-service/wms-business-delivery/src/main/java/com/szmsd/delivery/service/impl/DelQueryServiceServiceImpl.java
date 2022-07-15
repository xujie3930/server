package com.szmsd.delivery.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.delivery.domain.*;
import com.szmsd.delivery.dto.DelQueryServiceDto;
import com.szmsd.delivery.enums.DelQueryServiceStateEnum;
import com.szmsd.delivery.mapper.DelQueryServiceMapper;
import com.szmsd.delivery.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.vo.DelOutboundVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
* <p>
    * 查件服务 服务实现类
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/
@Service
public class DelQueryServiceServiceImpl extends ServiceImpl<DelQueryServiceMapper, DelQueryService> implements IDelQueryServiceService {

    @Resource
    private IDelQuerySettingsService delQuerySettingsService;
    @Resource
    private IDelQueryServiceFeedbackService delQueryServiceFeedbackService;


    @Resource
    private IDelOutboundService delOutboundService;
    @Resource
    private BasSellerFeignService basSellerFeignService;
    @Resource
    private IDelTrackService delTrackService;

    /**
        * 查询查件服务模块
        *
        * @param id 查件服务模块ID
        * @return 查件服务模块
        */
        @Override
        public DelQueryServiceDto selectDelQueryServiceById(String id)
        {
            DelQueryService data =  baseMapper.selectById(id);
            DelQueryServiceDto dto = new DelQueryServiceDto();
            BeanUtils.copyProperties(data, dto);
            DelQueryServiceFeedback delQueryServiceFeedback = new DelQueryServiceFeedback();
            delQueryServiceFeedback.setMainId(Integer.parseInt(id));

            dto.setDelQueryServiceFeedbackList(
                    delQueryServiceFeedbackService.selectDelQueryServiceFeedbackList(delQueryServiceFeedback));
             return dto;
        }

        /**
        * 查询查件服务模块列表
        *
        * @param delQueryService 查件服务模块
        * @return 查件服务模块
        */
        @Override
        public List<DelQueryService> selectDelQueryServiceList(DelQueryServiceDto delQueryService)
        {

            QueryWrapper<DelQueryService> queryWrapper = new QueryWrapper<DelQueryService>();

            LambdaQueryWrapper<DelQueryService> where = queryWrapper.lambda();

            if(StringUtils.isNotEmpty(delQueryService.getOrderNo())){
                where.in(DelQueryService::getOrderNo, StringToolkit.getCodeByArray(delQueryService.getOrderNo()));
            }


            if(StringUtils.isNotEmpty(delQueryService.getTraceId())){
                where.in(DelQueryService::getTraceId, StringToolkit.getCodeByArray(delQueryService.getTraceId()));
            }
            if(StringUtils.isNotEmpty(delQueryService.getRefNo())){
                where.in(DelQueryService::getRefNo, StringToolkit.getCodeByArray(delQueryService.getRefNo()));
            }



            if(StringUtils.isNotEmpty(delQueryService.getSellerCode())){
                where.eq(DelQueryService::getSellerCode, delQueryService.getSellerCode());
            }
            if(StringUtils.isNotEmpty(delQueryService.getServiceStaff())){
                where.like(DelQueryService::getServiceStaff, delQueryService.getServiceStaff());
            }
            if(StringUtils.isNotEmpty(delQueryService.getServiceManager())){
                where.like(DelQueryService::getServiceManager, delQueryService.getServiceManager());
            }


            if(StringUtils.isNotEmpty(delQueryService.getShipmentRule())){
                where.eq(DelQueryService::getShipmentRule, delQueryService.getShipmentRule());
            }

            if(StringUtils.isNotEmpty(delQueryService.getCountry())){
                where.eq(DelQueryService::getCountry, delQueryService.getCountry());
            }


            if(StringUtils.isNotEmpty(delQueryService.getCreateBy())){
                where.eq(DelQueryService::getCreateBy, delQueryService.getCreateBy());
            }



            if(StringUtils.isNotEmpty(delQueryService.getState())){
                where.eq(DelQueryService::getState, delQueryService.getState());
            }
            QueryWrapperUtil.filterDate(queryWrapper, "create_time", delQueryService.getCreateTimes());



            return baseMapper.selectList(where);
        }

        /**
        * 新增查件服务模块
        *
        * @param delQueryService 查件服务模块
        * @return 结果
        */
        @Override
        @Transactional(rollbackFor = Exception.class)
        public int insertDelQueryService(DelQueryService delQueryService)
        {


            LambdaQueryWrapper<DelQuerySettings> delQuerySettingsQueryWrapper = new LambdaQueryWrapper();
            delQuerySettingsQueryWrapper.eq(DelQuerySettings::getCountryCode, delQueryService.getCountryCode());
            delQuerySettingsQueryWrapper.eq(DelQuerySettings::getShipmentRule, delQueryService.getShipmentRule());
            List<DelQuerySettings> dataDelQuerySettingsList = delQuerySettingsService.list(delQuerySettingsQueryWrapper);
            if(dataDelQuerySettingsList.size() > 0){

                DelQuerySettings delQuerySettings = dataDelQuerySettingsList.get(0) ;
                DelOutbound delOutbound = delOutboundService.getByOrderNo(delQueryService.getOrderNo());
                if(delOutbound == null){
                    throw new CommonException("400", "无效订单");
                }

                boolean bool = false;

                if(StringUtils.equals(delOutbound.getState(), delQuerySettings.getState())){
                    bool = true;
                }else if(delOutbound.getShipmentsTime() != null && DateUtil.betweenDay(delOutbound.getShipmentsTime(), new Date(),  true) <= delQuerySettings.getShipmentDays()){
                    bool = true;
                }else{
                    LambdaQueryWrapper<DelTrack> delTrackLambdaQueryWrapper = Wrappers.lambdaQuery();
                    delTrackLambdaQueryWrapper.eq(DelTrack::getOrderNo, delQueryService.getOrderNo());
                    delTrackLambdaQueryWrapper.orderByDesc(DelTrack::getTrackingTime);
                    delTrackLambdaQueryWrapper.last("LIMIT 1");
                    DelTrack dataDelTrack = delTrackService.getOne(delTrackLambdaQueryWrapper);
                    if(dataDelTrack != null && dataDelTrack.getTrackingTime() != null && DateUtil.betweenDay(dataDelTrack.getTrackingTime(), new Date(),  true) <= delQuerySettings.getTrackStayDays()){
                        bool = true;
                    }
                }
                if(!bool){
                    throw new CommonException("400", "此查件申请不满足查件条件");

                }
            }

            delQueryService.setState(DelQueryServiceStateEnum.SUBMITTED.getCode());
            delQueryService.setStateName(DelQueryServiceStateEnum.SUBMITTED.getName());
            return baseMapper.insert(delQueryService);
        }


        /**
        * 修改查件服务模块
        *
        * @param delQueryService 查件服务模块
        * @return 结果
        */
        @Override
        public int updateDelQueryService(DelQueryService delQueryService)
        {
        return baseMapper.updateById(delQueryService);
        }

        /**
        * 批量删除查件服务模块
        *
        * @param ids 需要删除的查件服务模块ID
        * @return 结果
        */
        @Override
        public int deleteDelQueryServiceByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除查件服务模块信息
        *
        * @param id 查件服务模块ID
        * @return 结果
        */
        @Override
        public int deleteDelQueryServiceById(String id)
        {
        return baseMapper.deleteById(id);
        }

    @Override
    public DelQueryServiceDto getOrderInfo(String orderNo) {

        DelOutboundVO delOutbound = delOutboundService.selectDelOutboundByOrderNo(orderNo);

        if(delOutbound == null){
            return new DelQueryServiceDto();
        }
        DelQueryServiceDto dto = new DelQueryServiceDto();
        BeanUtils.copyProperties(delOutbound, dto);
        dto.setTraceId(delOutbound.getTrackingNo());
        dto.setShipmentService(dto.getShipmentRule());
        if(delOutbound.getAddress() != null){
            dto.setCountry(delOutbound.getAddress().getCountry());
            dto.setCountryCode(delOutbound.getAddress().getCountryCode());
        }


        R<BasSellerInfoVO> info = basSellerFeignService.getInfoBySellerCode(delOutbound.getSellerCode());
        if(info.getData() != null){
            BasSellerInfoVO userInfo = R.getDataAndException(info);

            dto.setServiceStaff(userInfo.getServiceStaff());
            dto.setServiceStaffNickName(userInfo.getServiceStaffName());
            dto.setServiceStaffNickName(userInfo.getServiceStaffNickName());

            dto.setServiceManagerName(userInfo.getServiceManagerName());
            dto.setServiceManagerNickName(userInfo.getServiceManagerNickName());
            dto.setServiceManager(userInfo.getServiceManager());

        }



        return dto;
    }


}

