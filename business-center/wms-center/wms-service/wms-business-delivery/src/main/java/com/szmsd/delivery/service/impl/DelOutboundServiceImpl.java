package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.constant.SerialNumberConstant;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundHttpWrapperService;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.http.dto.ShipmentCancelRequestDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 出库单 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
@Service
public class DelOutboundServiceImpl extends ServiceImpl<DelOutboundMapper, DelOutbound> implements IDelOutboundService {

    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;
    @Autowired
    private IDelOutboundDetailService delOutboundDetailService;
    @Autowired
    private SerialNumberClientService serialNumberClientService;
    @Autowired
    private IDelOutboundHttpWrapperService delOutboundHttpWrapperService;

    /**
     * 查询出库单模块
     *
     * @param id 出库单模块ID
     * @return 出库单模块
     */
    @Override
    public DelOutboundVO selectDelOutboundById(String id) {
        DelOutbound delOutbound = baseMapper.selectById(id);
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("999", "单据不存在");
        }
        DelOutboundVO delOutboundVO = BeanMapperUtil.map(delOutbound, DelOutboundVO.class);
        LambdaQueryWrapper<DelOutboundAddress> outboundAddressLambdaQueryWrapper = Wrappers.lambdaQuery();
        outboundAddressLambdaQueryWrapper.eq(DelOutboundAddress::getOrderNo, delOutbound.getOrderNo());
        DelOutboundAddress delOutboundAddress = delOutboundAddressService.getOne(outboundAddressLambdaQueryWrapper);
        if (Objects.nonNull(delOutboundAddress)) {
            delOutboundVO.setAddress(BeanMapperUtil.map(delOutboundAddress, DelOutboundAddressDto.class));
        }
        LambdaQueryWrapper<DelOutboundDetail> outboundDetailLambdaQueryWrapper = Wrappers.lambdaQuery();
        outboundDetailLambdaQueryWrapper.eq(DelOutboundDetail::getOrderNo, delOutbound.getOrderNo());
        List<DelOutboundDetail> delOutboundDetailList = delOutboundDetailService.list(outboundDetailLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(delOutboundDetailList)) {
            delOutboundVO.setDetails(BeanMapperUtil.mapList(delOutboundDetailList, DelOutboundDetailDto.class));
        }
        return delOutboundVO;
    }

    /**
     * 查询出库单模块列表
     *
     * @param queryDto 出库单模块
     * @return 出库单模块
     */
    @Override
    public List<DelOutboundListVO> selectDelOutboundList(DelOutboundListQueryDto queryDto) {
        QueryWrapper<DelOutboundListQueryDto> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "shipment_rule", queryDto.getShipmentRule());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "warehouse_code", queryDto.getWarehouseCode());
        return baseMapper.pageList(queryWrapper);
    }

    /**
     * 新增出库单模块
     *
     * @param dto 出库单模块
     * @return 结果
     */
    @Transactional
    @Override
    public int insertDelOutbound(DelOutboundDto dto) {
        if (!DelOutboundOrderTypeEnum.has(dto.getOrderType())) {
            throw new CommonException("999", "订单类型不存在");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            throw new CommonException("999", "获取登录用户信息失败");
        }
        DelOutbound delOutbound = BeanMapperUtil.map(dto, DelOutbound.class);
        // 从登录人信息中获取客户代码
        delOutbound.setCustomCode("");
        // 生成出库单号
        // 流水号规则：CK + 客户代码 + （年月日 + 8位流水）
        delOutbound.setOrderNo("CK" + delOutbound.getCustomCode() + this.serialNumberClientService.generateNumber(SerialNumberConstant.DEL_OUTBOUND_NO));
        // 默认状态
        delOutbound.setState(DelOutboundStateEnum.REVIEWED.getCode());
        // 保存出库单
        int insert = baseMapper.insert(delOutbound);
        if (insert == 0) {
            throw new CommonException("999", "保存出库单失败！");
        }
        // 保存地址
        this.saveAddress(dto, delOutbound.getOrderNo());
        // 保存明细
        this.saveDetail(dto, delOutbound.getOrderNo());
        return insert;
    }

    /**
     * 保存地址
     *
     * @param dto     dto
     * @param orderNo orderNo
     */
    private void saveAddress(DelOutboundDto dto, String orderNo) {
        DelOutboundAddress delOutboundAddress = BeanMapperUtil.map(dto.getAddress(), DelOutboundAddress.class);
        if (Objects.nonNull(delOutboundAddress)) {
            delOutboundAddress.setOrderNo(orderNo);
            this.delOutboundAddressService.save(delOutboundAddress);
        }
    }

    /**
     * 保存明细
     *
     * @param dto     dto
     * @param orderNo orderNo
     */
    private void saveDetail(DelOutboundDto dto, String orderNo) {
        List<DelOutboundDetail> delOutboundDetailList = BeanMapperUtil.mapList(dto.getDetails(), DelOutboundDetail.class);
        if (CollectionUtils.isNotEmpty(delOutboundDetailList)) {
            for (DelOutboundDetail delOutboundDetail : delOutboundDetailList) {
                delOutboundDetail.setOrderNo(orderNo);
            }
            this.delOutboundDetailService.saveBatch(delOutboundDetailList);
        }
    }

    /**
     * 删除地址
     *
     * @param orderNo orderNo
     */
    private void deleteAddress(String orderNo) {
        LambdaQueryWrapper<DelOutboundAddress> addressLambdaQueryWrapper = Wrappers.lambdaQuery();
        addressLambdaQueryWrapper.eq(DelOutboundAddress::getOrderNo, orderNo);
        this.delOutboundAddressService.remove(addressLambdaQueryWrapper);
    }

    /**
     * 删除明细
     *
     * @param orderNo orderNo
     */
    private void deleteDetail(String orderNo) {
        LambdaQueryWrapper<DelOutboundDetail> detailLambdaQueryWrapper = Wrappers.lambdaQuery();
        detailLambdaQueryWrapper.eq(DelOutboundDetail::getOrderNo, orderNo);
        this.delOutboundDetailService.remove(detailLambdaQueryWrapper);
    }

    /**
     * 修改出库单模块
     *
     * @param dto 出库单模块
     * @return 结果
     */
    @Transactional
    @Override
    public int updateDelOutbound(DelOutboundDto dto) {
        DelOutbound inputDelOutbound = BeanMapperUtil.map(dto, DelOutbound.class);
        DelOutbound delOutbound = this.getById(inputDelOutbound.getId());
        if (null == delOutbound) {
            throw new CommonException("999", "单据不存在");
        }
        // 先删后增
        String orderNo = delOutbound.getOrderNo();
        this.deleteAddress(orderNo);
        this.deleteDetail(orderNo);
        // 保存地址
        this.saveAddress(dto, orderNo);
        // 保存明细
        this.saveDetail(dto, orderNo);
        // 更新
        return baseMapper.updateById(inputDelOutbound);
    }

    /**
     * 批量删除出库单模块
     *
     * @param ids 需要删除的出库单模块ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDelOutboundByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(DelOutbound::getOrderNo);
        queryWrapper.in(DelOutbound::getId, ids);
        List<DelOutbound> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        if (list.size() != ids.size()) {
            throw new CommonException("999", "记录信息已被修改，请刷新后再试");
        }
        DelOutbound delOutbound = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            DelOutbound delOutbound1 = list.get(i);
            if (!delOutbound.getWarehouseCode().equals(delOutbound1.getWarehouseCode())) {
                throw new CommonException("999", "只能批量删除同一仓库下的出库单");
            }
        }
        List<String> orderNos = list.stream().map(DelOutbound::getOrderNo).collect(Collectors.toList());
        // 删除地址
        LambdaQueryWrapper<DelOutboundAddress> addressLambdaQueryWrapper = Wrappers.lambdaQuery();
        addressLambdaQueryWrapper.in(DelOutboundAddress::getOrderNo, orderNos);
        this.delOutboundAddressService.remove(addressLambdaQueryWrapper);
        // 删除明细
        LambdaQueryWrapper<DelOutboundDetail> detailLambdaQueryWrapper = Wrappers.lambdaQuery();
        detailLambdaQueryWrapper.in(DelOutboundDetail::getOrderNo, orderNos);
        this.delOutboundDetailService.remove(detailLambdaQueryWrapper);
        int i = baseMapper.deleteBatchIds(ids);
        if (i != ids.size()) {
            throw new CommonException("999", "操作记录异常，请刷新后再试");
        }
        // 调用wms取消出库单接口
        ShipmentCancelRequestDto requestDto = new ShipmentCancelRequestDto();
        requestDto.setWarehouseCode(delOutbound.getWarehouseCode());
        requestDto.setOrderNoList(orderNos);
        this.delOutboundHttpWrapperService.shipmentDelete(requestDto);
        // 返回处理结果
        return i;
    }

    /**
     * 删除出库单模块信息
     *
     * @param id 出库单模块ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDelOutboundById(String id) {
        DelOutbound delOutbound = this.getById(id);
        if (null == delOutbound) {
            throw new CommonException("999", "单据不存在");
        }
        // 先删后增
        String orderNo = delOutbound.getOrderNo();
        this.deleteAddress(orderNo);
        this.deleteDetail(orderNo);
        return baseMapper.deleteById(id);
    }

    @Transactional
    @Override
    public int shipmentOperationType(ShipmentRequestDto dto) {
        if (CollectionUtils.isEmpty(dto.getShipmentList())) {
            throw new CommonException("999", "出库单集合不能为空");
        }
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(DelOutbound::getWarehouseCode, dto.getWarehouseCode());
        updateWrapper.in(DelOutbound::getOrderNo, dto.getShipmentList());
        updateWrapper.set(DelOutbound::getOperationType, dto.getOperationType());
        updateWrapper.set(DelOutbound::getOperationTime, dto.getOperationTime());
        updateWrapper.set(DelOutbound::getRemark, dto.getRemark());
        return this.baseMapper.update(null, updateWrapper);
    }

    @Transactional
    @Override
    public int shipmentMeasure(PackageMeasureRequestDto dto) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(DelOutbound::getWarehouseCode, dto.getWarehouseCode());
        updateWrapper.eq(DelOutbound::getOrderNo, dto.getOrderNo());
        updateWrapper.set(DelOutbound::getLength, dto.getLength());
        updateWrapper.set(DelOutbound::getWidth, dto.getWidth());
        updateWrapper.set(DelOutbound::getHeight, dto.getHeight());
        updateWrapper.set(DelOutbound::getWeight, dto.getWeight());
        return this.baseMapper.update(null, updateWrapper);
    }

    @Transactional
    @Override
    public int shipmentPacking(ShipmentPackingMaterialRequestDto dto) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(DelOutbound::getWarehouseCode, dto.getWarehouseCode());
        updateWrapper.eq(DelOutbound::getOrderNo, dto.getOrderNo());
        updateWrapper.set(DelOutbound::getPackingMaterial, dto.getPackingMaterial());
        return this.baseMapper.update(null, updateWrapper);
    }

    @Transactional
    @Override
    public int shipmentContainers(ShipmentContainersRequestDto dto) {
        return 0;
    }

    @Override
    public int underReview(DelOutboundUnderReviewDto dto) {

        // 验证状态

        // 计算PRC价格，获取供应商

        // 获取挂号，获取标签

        // 调用WMS创建出库单

        // 保存WMS返回的出库单号，更新出库单状态

        return 0;
    }

    @Override
    public DelOutbound selectDelOutboundByOrderId(String orderId) {
        LambdaQueryWrapper<DelOutbound> query = Wrappers.lambdaQuery();
        query.eq(DelOutbound::getOrderNo,orderId);
        DelOutbound delOutbound = baseMapper.selectOne(query);
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("999", "单据不存在");
        }
        return delOutbound;
    }
}

