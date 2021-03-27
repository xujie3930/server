package com.szmsd.returnex.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.returnex.api.feign.client.IHttpFeignClientService;
import com.szmsd.returnex.config.BeanCopyUtil;
import com.szmsd.returnex.domain.ReturnExpressDetail;
import com.szmsd.returnex.dto.*;
import com.szmsd.returnex.mapper.ReturnExpressMapper;
import com.szmsd.returnex.service.IReturnExpressService;
import com.szmsd.returnex.vo.ReturnExpressListVO;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 退件单列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @Override
    public List<ReturnExpressListVO> selectReturnOrderList(ReturnExpressListQueryDTO queryDto) {
        //TODO 获取当前用户
        List<ReturnExpressDetail> returnExpressDetails = returnExpressMapper.selectList(Wrappers.<ReturnExpressDetail>lambdaQuery()
                .eq(StringUtil.isNotBlank(queryDto.getReturnType()), ReturnExpressDetail::getReturnType, queryDto.getReturnType())
                .eq(StringUtil.isNotBlank(queryDto.getApplyProcessMethod()), ReturnExpressDetail::getApplyProcessMethod, queryDto.getApplyProcessMethod())
                .between(queryDto.getCreateTimeStart() != null && queryDto.getCreateTimeEnd() != null, BaseEntity::getCreateTime, queryDto.getCreateTimeStart(), queryDto.getCreateTimeEnd())
                .eq(StringUtil.isNotBlank(queryDto.getReturnDestinationWarehouse()), ReturnExpressDetail::getReturnDestinationWarehouse, queryDto.getReturnDestinationWarehouse())
                .in(CollectionUtils.isNotEmpty(queryDto.getForecastNumberList()), ReturnExpressDetail::getFromOrderNo, queryDto.getForecastNumberList())
                .in(CollectionUtils.isNotEmpty(queryDto.getVmsProcessNumberList()), ReturnExpressDetail::getVmsProcessNumber, queryDto.getVmsProcessNumberList())
                .eq(StringUtil.isNotBlank(queryDto.getSellerCode()), ReturnExpressDetail::getSellerCode, queryDto.getSellerCode())

        );
        return BeanCopyUtil.copyListProperties(returnExpressDetails, ReturnExpressListVO::new);
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
    public int assignUsersForNoUserBindBatch(ReturnExpressAssignDTO expressAssignDTO) {
        return 0;
    }

    /**
     * 新建退件单
     *
     * @param returnExpressAddDTO 新增
     * @return 返回结果
     */
    @Override
    public int insertReturnExpressDetail(ReturnExpressAddDTO returnExpressAddDTO) {

        //创建退报单 推给VMS仓库
        httpFeignClient.expectedCreate(returnExpressAddDTO.convertThis(CreateExpectedReqDTO.class));

        //本地保存
        return saveReturnExpressDetail(returnExpressAddDTO.convertThis(ReturnExpressDetail.class));
    }

    /**
     * 本地保存用户发起的预报单数据
     *
     * @param returnExpressDetail
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int saveReturnExpressDetail(ReturnExpressDetail returnExpressDetail) {
        return returnExpressMapper.insert(returnExpressDetail);
    }

    /**
     * 接收VMS仓库到件信息
     * /api/return/arrival #G1-接收仓库退件到货
     *
     * @param returnArrivalReqDTO 接收VMS仓库到件信息
     * @return 操作结果
     */
    @Override
    public int saveArrivalInfoFormVms(ReturnArrivalReqDTO returnArrivalReqDTO) {

        //TODO 修改预报单状态？

        return 0;
    }

    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    @Override
    public int updateProcessingInfoFromVms(ReturnProcessingReqDTO returnProcessingReqDTO) {

        return 0;
    }

}
