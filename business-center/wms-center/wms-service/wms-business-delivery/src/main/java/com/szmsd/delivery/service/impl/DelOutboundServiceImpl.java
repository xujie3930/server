package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.constant.SerialNumberConstant;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
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
import com.szmsd.delivery.vo.*;
import com.szmsd.http.dto.ShipmentCancelRequestDto;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    @Autowired
    private BaseProductClientService baseProductClientService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;
    @Autowired
    private InventoryFeignClientService inventoryFeignClientService;

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
        String orderNo = delOutbound.getOrderNo();
        DelOutboundAddress delOutboundAddress = delOutboundAddressService.getByOrderNo(orderNo);
        if (Objects.nonNull(delOutboundAddress)) {
            delOutboundVO.setAddress(BeanMapperUtil.map(delOutboundAddress, DelOutboundAddressVO.class));
        }
        List<DelOutboundDetail> delOutboundDetailList = delOutboundDetailService.listByOrderNo(orderNo);
        if (CollectionUtils.isNotEmpty(delOutboundDetailList)) {
            List<DelOutboundDetailVO> detailDtos = new ArrayList<>(delOutboundDetailList.size());
            List<String> skus = new ArrayList<>(delOutboundDetailList.size());
            for (DelOutboundDetail detail : delOutboundDetailList) {
                detailDtos.add(BeanMapperUtil.map(detail, DelOutboundDetailVO.class));
                skus.add(detail.getSku());
            }
            InventoryAvailableQueryDto inventoryAvailableQueryDto = new InventoryAvailableQueryDto();
            inventoryAvailableQueryDto.setWarehouseCode(delOutbound.getWarehouseCode());
            inventoryAvailableQueryDto.setSkus(skus);
            List<InventoryAvailableListVO> availableList = this.inventoryFeignClientService.queryAvailableList(inventoryAvailableQueryDto);
            Map<String, InventoryAvailableListVO> availableMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(availableList)) {
                for (InventoryAvailableListVO vo : availableList) {
                    availableMap.put(vo.getSku(), vo);
                }
            }
            for (DelOutboundDetailVO vo : detailDtos) {
                InventoryAvailableListVO available = availableMap.get(vo.getSku());
                if (null != available) {
                    BeanMapperUtil.map(available, vo);
                }
            }
            delOutboundVO.setDetails(detailDtos);
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
        QueryWrapperUtil.filter(queryWrapper, SqlLike.RIGHT, "o.order_no", queryDto.getOrderNo());
        QueryWrapperUtil.filter(queryWrapper, SqlLike.RIGHT, "o.purchase_no", queryDto.getPurchaseNo());
        QueryWrapperUtil.filter(queryWrapper, SqlLike.RIGHT, "o.tracking_no", queryDto.getTrackingNo());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.shipment_rule", queryDto.getShipmentRule());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.warehouse_code", queryDto.getWarehouseCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.state", queryDto.getState());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.order_type", queryDto.getOrderType());
        QueryWrapperUtil.filter(queryWrapper, SqlLike.DEFAULT, "o.custom_code", queryDto.getCustomCode());
        QueryWrapperUtil.filterDate(queryWrapper, "o.create_time", queryDto.getCreateTimes());
        // 按照创建时间倒序
        queryWrapper.orderByDesc("o.create_time");
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
        // 生成出库单号
        // 流水号规则：CK + 客户代码 + （年月日 + 8位流水）
        delOutbound.setOrderNo("CK" + delOutbound.getCustomCode() + this.serialNumberClientService.generateNumber(SerialNumberConstant.DEL_OUTBOUND_NO));
        // 冻结库存
        this.freeze(delOutbound.getOrderNo(), delOutbound.getWarehouseCode(), dto.getDetails());
        // 默认状态
        delOutbound.setState(DelOutboundStateEnum.REVIEWED.getCode());
        // 计算发货类型
        delOutbound.setShipmentType(this.buildShipmentType(dto));
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

    private String buildShipmentType(DelOutboundDto dto) {
        List<DelOutboundDetailDto> details = dto.getDetails();
        return this.baseProductClientService.buildShipmentType(dto.getWarehouseCode(), details.stream().map(DelOutboundDetailDto::getSku).collect(Collectors.toList()));
    }

    /**
     * 保存地址
     *
     * @param dto     dto
     * @param orderNo orderNo
     */
    private void saveAddress(DelOutboundDto dto, String orderNo) {
        DelOutboundAddressDto address = dto.getAddress();
        if (Objects.isNull(address)) {
            return;
        }
        DelOutboundAddress delOutboundAddress = BeanMapperUtil.map(address, DelOutboundAddress.class);
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
        List<DelOutboundDetailDto> details = dto.getDetails();
        if (CollectionUtils.isEmpty(details)) {
            return;
        }
        List<DelOutboundDetail> delOutboundDetailList = BeanMapperUtil.mapList(details, DelOutboundDetail.class);
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
        // 可以修改的状态：待提审，审核失败
        if (!(DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound.getState())
                || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound.getState()))) {
            throw new CommonException("999", "单据不能修改");
        }
        // 先取消冻结，再冻结
        // 取消冻结
        String orderNo = delOutbound.getOrderNo();
        String warehouseCode = delOutbound.getWarehouseCode();
        List<DelOutboundDetail> detailList = this.delOutboundDetailService.listByOrderNo(orderNo);
        this.unFreezeAndFreeze(orderNo, warehouseCode, detailList, dto.getDetails());
        // 先删后增
        this.deleteAddress(orderNo);
        this.deleteDetail(orderNo);
        // 保存地址
        this.saveAddress(dto, orderNo);
        // 保存明细
        this.saveDetail(dto, orderNo);
        // 计算发货类型
        inputDelOutbound.setShipmentType(this.buildShipmentType(dto));
        // 更新
        return baseMapper.updateById(inputDelOutbound);
    }

    private void unFreezeAndFreeze(String invoiceNo, String warehouseCode, List<DelOutboundDetail> details, List<DelOutboundDetailDto> detailDtos) {
        if (CollectionUtils.isEmpty(details) && CollectionUtils.isEmpty(detailDtos)) {
            return;
        }
        InventoryOperateListDto operateListDto = new InventoryOperateListDto();
        operateListDto.setInvoiceNo(invoiceNo);
        operateListDto.setWarehouseCode(warehouseCode);
        if (CollectionUtils.isNotEmpty(details)) {
            List<InventoryOperateDto> unOperateList = new ArrayList<>();
            for (DelOutboundDetail detail : details) {
                unOperateList.add(new InventoryOperateDto(String.valueOf(detail.getLineNo()), detail.getSku(), Math.toIntExact(detail.getQty())));
            }
            operateListDto.setUnOperateList(unOperateList);
        }
        if (CollectionUtils.isNotEmpty(detailDtos)) {
            List<InventoryOperateDto> operateList = new ArrayList<>();
            long lineNo = 1L;
            for (DelOutboundDetailDto detail : detailDtos) {
                detail.setLineNo(lineNo++);
                operateList.add(new InventoryOperateDto(String.valueOf(detail.getLineNo()), detail.getSku(), Math.toIntExact(detail.getQty())));
            }
            operateListDto.setOperateList(operateList);
        }
        this.inventoryFeignClientService.unFreezeAndFreeze(operateListDto);
    }

    private void freeze(String invoiceNo, String warehouseCode, List<DelOutboundDetailDto> details) {
        if (CollectionUtils.isEmpty(details)) {
            return;
        }
        InventoryOperateListDto operateListDto = new InventoryOperateListDto();
        operateListDto.setInvoiceNo(invoiceNo);
        operateListDto.setWarehouseCode(warehouseCode);
        List<InventoryOperateDto> operateList = new ArrayList<>();
        long lineNo = 1L;
        for (DelOutboundDetailDto detail : details) {
            detail.setLineNo(lineNo++);
            operateList.add(new InventoryOperateDto(String.valueOf(detail.getLineNo()), detail.getSku(), Math.toIntExact(detail.getQty())));
        }
        operateListDto.setOperateList(operateList);
        this.inventoryFeignClientService.freeze(operateListDto);
    }

    private void unFreeze(String invoiceNo, String warehouseCode, List<DelOutboundDetail> details) {
        if (CollectionUtils.isEmpty(details)) {
            return;
        }
        InventoryOperateListDto operateListDto = new InventoryOperateListDto();
        operateListDto.setInvoiceNo(invoiceNo);
        operateListDto.setWarehouseCode(warehouseCode);
        List<InventoryOperateDto> operateList = new ArrayList<>();
        for (DelOutboundDetail detail : details) {
            operateList.add(new InventoryOperateDto(String.valueOf(detail.getLineNo()), detail.getSku(), Math.toIntExact(detail.getQty())));
        }
        operateListDto.setOperateList(operateList);
        this.inventoryFeignClientService.unFreeze(operateListDto);
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
        Map<String, String> warehouseMap = new HashMap<>();
        for (int i = 1; i < list.size(); i++) {
            DelOutbound delOutbound1 = list.get(i);
            if (!delOutbound.getWarehouseCode().equals(delOutbound1.getWarehouseCode())) {
                throw new CommonException("999", "只能批量删除同一仓库下的出库单");
            }
            warehouseMap.put(delOutbound1.getOrderNo(), delOutbound1.getWarehouseCode());
        }
        List<String> orderNos = list.stream().map(DelOutbound::getOrderNo).collect(Collectors.toList());
        // 删除地址
        LambdaQueryWrapper<DelOutboundAddress> addressLambdaQueryWrapper = Wrappers.lambdaQuery();
        addressLambdaQueryWrapper.in(DelOutboundAddress::getOrderNo, orderNos);
        this.delOutboundAddressService.remove(addressLambdaQueryWrapper);
        // 取消冻结
        for (String orderNo : orderNos) {
            List<DelOutboundDetail> detailList = this.delOutboundDetailService.listByOrderNo(orderNo);
            this.unFreeze(orderNo, warehouseMap.get(orderNo), detailList);
        }
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
    public int shipmentPacking(ShipmentPackingMaterialRequestDto dto) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(DelOutbound::getWarehouseCode, dto.getWarehouseCode());
        updateWrapper.eq(DelOutbound::getOrderNo, dto.getOrderNo());
        updateWrapper.set(DelOutbound::getPackingMaterial, dto.getPackingMaterial());
        updateWrapper.set(DelOutbound::getLength, dto.getLength());
        updateWrapper.set(DelOutbound::getWidth, dto.getWidth());
        updateWrapper.set(DelOutbound::getHeight, dto.getHeight());
        updateWrapper.set(DelOutbound::getWeight, dto.getWeight());
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
        query.eq(DelOutbound::getOrderNo, orderId);
        DelOutbound delOutbound = baseMapper.selectOne(query);
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("999", "单据不存在");
        }
        return delOutbound;
    }

    @Transactional
    @Override
    public int bringVerify(Long id) {


        return 0;
    }

    @Transactional
    @Override
    public void updateState(Long id, DelOutboundStateEnum stateEnum) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(DelOutbound::getState, stateEnum.getCode());
        updateWrapper.eq(DelOutbound::getId, id);
        this.update(updateWrapper);
    }

    @Override
    public List<DelOutboundDetailListVO> getDelOutboundDetailsList(DelOutboundListQueryDto queryDto) {
        QueryWrapper<DelOutboundListQueryDto> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(queryDto.getOrderType())) {
            queryWrapper.eq("a.order_type", queryDto.getOrderType());
        }
        return baseMapper.getDelOutboundAndDetailsList(queryWrapper);
    }

    @Transactional
    @Override
    public void bringVerifyFail(Long id, String exceptionMessage) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.AUDIT_FAILED.getCode());
        updateWrapper.set(DelOutbound::getExceptionMessage, exceptionMessage);
        updateWrapper.eq(DelOutbound::getId, id);
        this.update(updateWrapper);
    }

    @Transactional
    @Override
    public void bringVerifySuccess(DelOutbound delOutbound) {
        delOutbound.setState(DelOutboundStateEnum.DELIVERED.getCode());
        this.updateById(delOutbound);
    }
}

