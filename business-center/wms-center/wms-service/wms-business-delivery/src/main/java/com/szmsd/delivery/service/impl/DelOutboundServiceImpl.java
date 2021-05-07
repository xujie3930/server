package com.szmsd.delivery.service.impl;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.constant.SerialNumberConstant;
import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.*;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.delivery.service.*;
import com.szmsd.delivery.service.wrapper.BringVerifyEnum;
import com.szmsd.delivery.service.wrapper.IDelOutboundAsyncService;
import com.szmsd.delivery.util.PackageInfo;
import com.szmsd.delivery.util.PackageUtil;
import com.szmsd.delivery.util.Utils;
import com.szmsd.delivery.vo.*;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.ShipmentCancelRequestDto;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private Logger logger = LoggerFactory.getLogger(DelOutboundServiceImpl.class);

    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;
    @Autowired
    private IDelOutboundDetailService delOutboundDetailService;
    @Autowired
    private SerialNumberClientService serialNumberClientService;
    @Autowired
    private BaseProductClientService baseProductClientService;
    @Autowired
    private InventoryFeignClientService inventoryFeignClientService;
    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;
    @Autowired
    private RemoteAttachmentService remoteAttachmentService;
    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;
    @Autowired
    private IDelOutboundChargeService delOutboundChargeService;
    @Autowired
    private IDelOutboundAsyncService delOutboundAsyncService;
    @Autowired
    private OperationFeignService operationFeignService;
    @Autowired
    private IDelOutboundPackingService delOutboundPackingService;

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
            inventoryAvailableQueryDto.setCusCode(delOutbound.getSellerCode());
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
        // 批量出库
        if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {
            // 查询装箱信息
            delOutboundVO.setPackings(this.delOutboundPackingService.listByOrderNo(orderNo, DelOutboundPackingTypeConstant.TYPE_1));
            // 查询装箱信息
            Integer containerState = delOutbound.getContainerState();
            if (null == containerState) {
                containerState = DelOutboundConstant.CONTAINER_STATE_0;
            }
            if (DelOutboundConstant.CONTAINER_STATE_1 == containerState) {
                delOutboundVO.setContainerList(this.delOutboundPackingService.listByOrderNo(orderNo, DelOutboundPackingTypeConstant.TYPE_2));
            }
        }
        return delOutboundVO;
    }

    @Override
    public List<DelOutboundDetailVO> getTransshipmentProductData(List<String> idList) {
        return createPurchaseOrderListByIdList(idList, DelOutboundOrderTypeEnum.PACKAGE_TRANSFER);
    }

    @Override
    public List<DelOutboundDetailVO> createPurchaseOrderListByIdList(List<String> idList) {
        return createPurchaseOrderListByIdList(idList, DelOutboundOrderTypeEnum.COLLECTION);
    }

    /**
     * 出库-创建采购的单
     *
     * @param idList
     * @return
     */
    private List<DelOutboundDetailVO> createPurchaseOrderListByIdList(List<String> idList, DelOutboundOrderTypeEnum typeEnum) {
        //只查询集运类型的顶单
        List<DelOutbound> delOutbounds = baseMapper.selectList(Wrappers.<DelOutbound>lambdaQuery()
                .in(DelOutbound::getId, idList)
                .eq(DelOutbound::getOrderType, typeEnum.getCode())
        );
        if (CollectionUtils.isEmpty(delOutbounds)) {
            return new ArrayList<DelOutboundDetailVO>();
        }

        //客户端 sellerCode相同
        String sellerCode = delOutbounds.stream().map(DelOutbound::getSellerCode).findAny().orElseThrow(() -> new BaseException("获取该批数据的sellerCode失败"));
        Map<String, List<DelOutbound>> baseInfoList = delOutbounds.stream().collect(Collectors.groupingBy(DelOutbound::getWarehouseCode));

        //查询订单中的sku集合
        List<String> collect1 = delOutbounds.stream().map(DelOutbound::getOrderNo).collect(Collectors.toList());
        List<DelOutboundDetail> delOutboundDetailList = delOutboundDetailService.listByOrderNos(collect1);

        List<DelOutboundDetailVO> resultList = new ArrayList<>();
        //不同仓库 要获取其他信息需要仓库编码和用户code查询
        baseInfoList.forEach((warehouseCode, dealOutBoundList) -> {

            //获取sku其他信息
            // 返回sku 列表集合
            List<DelOutboundDetailVO> detailDtos = new ArrayList<>(delOutboundDetailList.size());
            List<String> skus = new ArrayList<>(delOutboundDetailList.size());
            for (DelOutboundDetail detail : delOutboundDetailList) {
                detailDtos.add(BeanMapperUtil.map(detail, DelOutboundDetailVO.class));
                skus.add(detail.getSku());
            }
            //获取 入库里面的商品实际库存
            InventoryAvailableQueryDto inventoryAvailableQueryDto = new InventoryAvailableQueryDto();
            inventoryAvailableQueryDto.setWarehouseCode(warehouseCode);
            inventoryAvailableQueryDto.setCusCode(sellerCode);
            inventoryAvailableQueryDto.setSkus(skus);
            //如果没有库存不会塞数据 先方判断外面
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
            resultList.addAll(detailDtos);
        });

        logger.info("获取其他sku信息{}", resultList);
        return resultList;
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
        DelOutboundServiceImplUtil.handlerQueryWrapper(queryWrapper, queryDto);
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
        // 来源为新增
        dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_ADD);
        return this.createDelOutbound(dto);
    }

    private int createDelOutbound(DelOutboundDto dto) {
        int stepValue = 0x00;
        String orderNo;
        // 创建出库单
        try {
            DelOutbound delOutbound = BeanMapperUtil.map(dto, DelOutbound.class);
            // 生成出库单号
            // 流水号规则：CK + 客户代码 + （年月日 + 8位流水）
            delOutbound.setOrderNo(orderNo = ("CK" + delOutbound.getCustomCode() + this.serialNumberClientService.generateNumber(SerialNumberConstant.DEL_OUTBOUND_NO)));
            // 冻结操作费用
            stepValue = DelOutboundServiceImplUtil.joinKey(stepValue, 0x02);
            // 默认状态
            delOutbound.setState(DelOutboundStateEnum.REVIEWED.getCode());
            // 默认异常状态
            delOutbound.setExceptionState(DelOutboundExceptionStateEnum.NORMAL.getCode());
            // 计算发货类型
            delOutbound.setShipmentType(this.buildShipmentType(dto));
            // 计算包裹大小
            this.countPackageSize(delOutbound, dto);
            // 保存出库单
            int insert = baseMapper.insert(delOutbound);
            if (insert == 0) {
                throw new CommonException("999", "保存出库单失败！");
            }
            // 保存地址
            this.saveAddress(dto, delOutbound.getOrderNo());
            // 保存明细
            this.saveDetail(dto, delOutbound.getOrderNo());
            // 附件信息
            AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(dto.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT).build();
            this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
            // 批量出库保存装箱信息
            if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {
                // 装箱信息
                List<DelOutboundPackingDto> packings = dto.getPackings();
                this.delOutboundPackingService.save(orderNo, packings, false);
                // 箱标文件
                AttachmentDTO batchLabel = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(dto.getBatchLabels()).attachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL).build();
                this.remoteAttachmentService.saveAndUpdate(batchLabel);
            }
            return insert;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 异常传播
            throw e;
        }
    }

    /**
     * 取消冻结操作费用
     *
     * @param orderNo orderNo
     */
    private void unfreezeOperation(String orderNo) {
        DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
        delOutboundOperationVO.setOrderNo(orderNo);
        R<?> ur = this.operationFeignService.delOutboundThaw(delOutboundOperationVO);
        DelOutboundServiceImplUtil.thawOperationThrowCommonException(ur);
    }

    @Override
    public int insertDelOutbounds(List<DelOutboundDto> dtoList) {
        int result = 0;
        for (DelOutboundDto dto : dtoList) {
            result += this.createDelOutbound(dto);
        }
        return result;
    }

    /**
     * 计算包裹大小
     *
     * @param delOutbound delOutbound
     * @param dto         dto
     */
    private void countPackageSize(DelOutbound delOutbound, DelOutboundDto dto) {
        // 转运特殊处理
        if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
            double length = Utils.defaultValue(dto.getLength());
            double width = Utils.defaultValue(dto.getWidth());
            double height = Utils.defaultValue(dto.getHeight());
            double weight = Utils.defaultValue(dto.getWeight());
            List<DelOutboundDetailDto> details = dto.getDetails();
            long boxNumber = 0L;
            for (DelOutboundDetailDto detail : details) {
                boxNumber += Utils.defaultValue(detail.getQty());
            }
            delOutbound.setLength(length);
            delOutbound.setWidth(width);
            delOutbound.setHeight(height);
            delOutbound.setWeight(weight);
            // 规格，长*宽*高
            delOutbound.setSpecifications(length + "*" + width + "*" + height);
            delOutbound.setBoxNumber(boxNumber);
        } else {
            double weight = 0.0;
            List<DelOutboundDetailDto> details = dto.getDetails();
            List<PackageInfo> packageInfoList = new ArrayList<>();
            long boxNumber = 0L;
            for (DelOutboundDetailDto detail : details) {
                weight += Utils.defaultValue(detail.getWeight());
                packageInfoList.add(new PackageInfo(detail.getLength(), detail.getWidth(), detail.getHeight()));
                boxNumber += Utils.defaultValue(detail.getQty());
            }
            delOutbound.setWeight(weight);
            PackageInfo packageInfo = PackageUtil.count(packageInfoList);
            delOutbound.setLength(packageInfo.getLength());
            delOutbound.setWidth(packageInfo.getWidth());
            delOutbound.setHeight(packageInfo.getHeight());
            // 规格，长*宽*高
            delOutbound.setSpecifications(packageInfo.getLength() + "*" + packageInfo.getWidth() + "*" + packageInfo.getHeight());
            delOutbound.setBoxNumber(boxNumber);
        }
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
        int stepValue = 0x00;
        List<DelOutboundDetailDto> details = dto.getDetails();
        // 查询现有的库存
        List<DelOutboundDetail> detailList = this.delOutboundDetailService.listByOrderNo(orderNo);
        try {
            stepValue = DelOutboundServiceImplUtil.joinKey(stepValue, 0x02);
            // 先删后增
            this.deleteAddress(orderNo);
            this.deleteDetail(orderNo);
            // 保存地址
            this.saveAddress(dto, orderNo);
            // 保存明细
            this.saveDetail(dto, orderNo);
            // 计算发货类型
            inputDelOutbound.setShipmentType(this.buildShipmentType(dto));
            // 附件信息
            AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(delOutbound.getOrderNo()).businessItemNo(null).fileList(dto.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT).build();
            this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
            // 计算包裹大小
            this.countPackageSize(inputDelOutbound, dto);
            // 批量出库保存装箱信息
            if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {
                // 装箱信息
                List<DelOutboundPackingDto> packings = dto.getPackings();
                this.delOutboundPackingService.save(orderNo, packings, true);
                // 箱标文件
                AttachmentDTO batchLabel = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(dto.getBatchLabels()).attachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL).build();
                this.remoteAttachmentService.saveAndUpdate(batchLabel);
            }
            // 更新
            return baseMapper.updateById(inputDelOutbound);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    private void unFreezeAndFreeze(String orderType, String invoiceNo, String warehouseCode, List<DelOutboundDetail> details, List<DelOutboundDetailDto> detailDtos) {
        if (DelOutboundServiceImplUtil.noOperationInventory(orderType)) {
            return;
        }
        if (CollectionUtils.isEmpty(details) && CollectionUtils.isEmpty(detailDtos)) {
            return;
        }
        InventoryOperateListDto operateListDto = new InventoryOperateListDto();
        operateListDto.setInvoiceNo(invoiceNo);
        operateListDto.setWarehouseCode(warehouseCode);
        if (CollectionUtils.isNotEmpty(details)) {
            Map<String, InventoryOperateDto> inventoryOperateDtoMap = new HashMap<>();
            for (DelOutboundDetail detail : details) {
                DelOutboundServiceImplUtil.handlerInventoryOperate(detail, inventoryOperateDtoMap);
            }
            List<InventoryOperateDto> unOperateList = new ArrayList<>(inventoryOperateDtoMap.values());
            operateListDto.setUnOperateList(unOperateList);
        }
        if (CollectionUtils.isNotEmpty(detailDtos)) {
            Map<String, InventoryOperateDto> inventoryOperateDtoMap = new HashMap<>();
            long lineNo = 1L;
            for (DelOutboundDetailDto detail : detailDtos) {
                detail.setLineNo(lineNo++);
                DelOutboundServiceImplUtil.handlerInventoryOperate(detail, inventoryOperateDtoMap);
            }
            List<InventoryOperateDto> operateList = new ArrayList<>(inventoryOperateDtoMap.values());
            operateListDto.setOperateList(operateList);
        }
        this.inventoryFeignClientService.unFreezeAndFreeze(operateListDto);
    }

    private void freezeNoWrapper(String orderType, String invoiceNo, String warehouseCode, List<DelOutboundDetail> details) {
        if (CollectionUtils.isEmpty(details)) {
            return;
        }
        this.freeze(orderType, invoiceNo, warehouseCode, BeanMapperUtil.mapList(details, DelOutboundDetailDto.class));
    }

    private void freeze(String orderType, String invoiceNo, String warehouseCode, List<DelOutboundDetailDto> details) {
        if (DelOutboundServiceImplUtil.noOperationInventory(orderType)) {
            return;
        }
        if (CollectionUtils.isEmpty(details)) {
            return;
        }
        InventoryOperateListDto operateListDto = new InventoryOperateListDto();
        operateListDto.setInvoiceNo(invoiceNo);
        operateListDto.setWarehouseCode(warehouseCode);
        long lineNo = 1L;
        Map<String, InventoryOperateDto> inventoryOperateDtoMap = new HashMap<>();
        for (DelOutboundDetailDto detail : details) {
            detail.setLineNo(lineNo++);
            DelOutboundServiceImplUtil.handlerInventoryOperate(detail, inventoryOperateDtoMap);
        }
        List<InventoryOperateDto> operateList = new ArrayList<>(inventoryOperateDtoMap.values());
        operateListDto.setOperateList(operateList);
        this.inventoryFeignClientService.freeze(operateListDto);
    }

    /**
     * 取消冻结
     *
     * @param orderType     orderType
     * @param orderNo       orderNo
     * @param warehouseCode warehouseCode
     */
    private void unFreeze(String orderType, String orderNo, String warehouseCode) {
        if (DelOutboundServiceImplUtil.noOperationInventory(orderType)) {
            return;
        }
        // 查询明细
        List<DelOutboundDetail> details = this.delOutboundDetailService.listByOrderNo(orderNo);
        InventoryOperateListDto inventoryOperateListDto = new InventoryOperateListDto();
        Map<String, InventoryOperateDto> inventoryOperateDtoMap = new HashMap<>();
        for (DelOutboundDetail detail : details) {
            DelOutboundServiceImplUtil.handlerInventoryOperate(detail, inventoryOperateDtoMap);
        }
        inventoryOperateListDto.setInvoiceNo(orderNo);
        inventoryOperateListDto.setWarehouseCode(warehouseCode);
        List<InventoryOperateDto> operateList = new ArrayList<>(inventoryOperateDtoMap.values());
        inventoryOperateListDto.setOperateList(operateList);
        // 取消冻结
        Integer deduction = this.inventoryFeignClientService.unFreeze(inventoryOperateListDto);
        if (null == deduction || deduction < 1) {
            throw new CommonException("999", "取消冻结库存失败");
        }
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
        queryWrapper.select(DelOutbound::getOrderNo, DelOutbound::getWarehouseCode, DelOutbound::getState);
        queryWrapper.in(DelOutbound::getId, ids);
        List<DelOutbound> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        if (list.size() != ids.size()) {
            throw new CommonException("999", "记录信息已被修改，请刷新后再试");
        }
        DelOutbound delOutbound = list.get(0);
        Map<String, DelOutbound> delOutboundMap = new HashMap<>();
        for (DelOutbound delOutbound1 : list) {
            // 只能删除待提审，提审失败的单据
            if (!(DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound1.getState())
                    || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound1.getState()))) {
                throw new CommonException("999", "只能删除待提审，提审失败的单据");
            }
            if (!delOutbound.getWarehouseCode().equals(delOutbound1.getWarehouseCode())) {
                throw new CommonException("999", "只能批量删除同一仓库下的出库单");
            }
            delOutboundMap.put(delOutbound1.getOrderNo(), delOutbound1);
        }
        List<String> orderNos = list.stream().map(DelOutbound::getOrderNo).collect(Collectors.toList());
        // 删除地址
        LambdaQueryWrapper<DelOutboundAddress> addressLambdaQueryWrapper = Wrappers.lambdaQuery();
        addressLambdaQueryWrapper.in(DelOutboundAddress::getOrderNo, orderNos);
        this.delOutboundAddressService.remove(addressLambdaQueryWrapper);
        // 删除装箱信息
        this.delOutboundPackingService.deleted(orderNos);
        // 取消冻结
        for (String orderNo : orderNos) {
            DelOutbound delOutbound1 = delOutboundMap.get(orderNo);
            // 提审状态
            String bringVerifyState = delOutbound.getBringVerifyState();
            if (StringUtils.isNotEmpty(bringVerifyState)) {
                // 判断要不要取消冻结库存
                if (BringVerifyEnum.gt(BringVerifyEnum.FREEZE_INVENTORY, BringVerifyEnum.get(bringVerifyState))) {
                    // 取消冻结库存
                    this.unFreeze(delOutbound1.getOrderType(), orderNo, delOutbound1.getWarehouseCode());
                }
                // 判断要不要取消冻结操作费用
                if (BringVerifyEnum.gt(BringVerifyEnum.FREEZE_OPERATION, BringVerifyEnum.get(bringVerifyState))) {
                    // 取消冻结操作费用
                    this.unfreezeOperation(orderNo);
                }
            }
        }
        // 删除明细
        LambdaQueryWrapper<DelOutboundDetail> detailLambdaQueryWrapper = Wrappers.lambdaQuery();
        detailLambdaQueryWrapper.in(DelOutboundDetail::getOrderNo, orderNos);
        this.delOutboundDetailService.remove(detailLambdaQueryWrapper);
        int i = baseMapper.deleteBatchIds(ids);
        if (i != ids.size()) {
            throw new CommonException("999", "操作记录异常，请刷新后再试");
        }
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
        List<String> orderNos = dto.getShipmentList();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("999", "出库单集合不能为空");
        }
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        // 条件
        if (StringUtils.isNotEmpty(dto.getWarehouseCode())) {
            updateWrapper.eq(DelOutbound::getWarehouseCode, dto.getWarehouseCode());
        }
        updateWrapper.in(DelOutbound::getOrderNo, orderNos);
        // 赋值
        updateWrapper.set(DelOutbound::getOperationType, dto.getOperationType());
        updateWrapper.set(DelOutbound::getOperationTime, dto.getOperationTime());
        updateWrapper.set(DelOutbound::getRemark, dto.getRemark());
        // 仓库开始处理
        if (DelOutboundOperationTypeEnum.PROCESSING.getCode().equals(dto.getOperationType())) {
            updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.WHSE_PROCESSING.getCode());
            updateWrapper.set(DelOutbound::getArrivalTime, new Date());
        }
        // 仓库已发货
        else if (DelOutboundOperationTypeEnum.SHIPPED.getCode().equals(dto.getOperationType())) {
            updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.WHSE_COMPLETED.getCode());
            // 增加出库单已完成记录
            this.delOutboundCompletedService.add(orderNos, DelOutboundOperationTypeEnum.SHIPPED.getCode());
        }
        // 仓库取消
        else if (DelOutboundOperationTypeEnum.CANCELED.getCode().equals(dto.getOperationType())) {
            updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.WHSE_CANCELLED.getCode());
            // 增加出库单已取消记录
            this.delOutboundCompletedService.add(orderNos, DelOutboundOperationTypeEnum.CANCELED.getCode());
        }
        return this.baseMapper.update(null, updateWrapper);
    }

    @Transactional
    @Override
    public int shipmentPacking(ShipmentPackingMaterialRequestDto dto) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        if (StringUtils.isNotEmpty(dto.getWarehouseCode())) {
            updateWrapper.eq(DelOutbound::getWarehouseCode, dto.getWarehouseCode());
        }
        updateWrapper.eq(DelOutbound::getOrderNo, dto.getOrderNo());
        updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.PROCESSING.getCode());
        updateWrapper.set(DelOutbound::getPackingMaterial, dto.getPackingMaterial());
        updateWrapper.set(DelOutbound::getLength, dto.getLength());
        // 处理空值问题
        Double width = Utils.defaultValue(dto.getWidth());
        Double height = Utils.defaultValue(dto.getHeight());
        Double weight = Utils.defaultValue(dto.getWeight());
        updateWrapper.set(DelOutbound::getWidth, width);
        updateWrapper.set(DelOutbound::getHeight, height);
        updateWrapper.set(DelOutbound::getWeight, weight);
        // 规格，长*宽*高
        updateWrapper.set(DelOutbound::getSpecifications, dto.getLength() + "*" + width + "*" + height);
        return this.baseMapper.update(null, updateWrapper);
    }

    @Transactional
    @Override
    public int shipmentContainers(ShipmentContainersRequestDto dto) {
        // 保存装箱信息
        List<ContainerInfoDto> containerList = dto.getContainerList();
        this.delOutboundPackingService.save(dto.getOrderNo(), containerList);
        // 修改装箱状态
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(DelOutbound::getOrderNo, dto.getOrderNo());
        updateWrapper.eq(DelOutbound::getContainerState, DelOutboundConstant.CONTAINER_STATE_1);
        return this.baseMapper.update(null, updateWrapper);
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
    public void updateState(Long id, DelOutboundStateEnum stateEnum) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(DelOutbound::getState, stateEnum.getCode());
        updateWrapper.eq(DelOutbound::getId, id);
        this.update(updateWrapper);
    }

    @Override
    public List<DelOutboundDetailListVO> getDelOutboundDetailsList(DelOutboundListQueryDto queryDto) {
        QueryWrapper<DelOutboundListQueryDto> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("a.order_type", queryDto.getOrderType());
        queryWrapper.eq("a.warehouse_code", queryDto.getWarehouseCode());
        if (StringUtils.isNotBlank(queryDto.getState())) {
            queryWrapper.eq("a.state", queryDto.getState());
        }
        if (StringUtils.isNotBlank(queryDto.getUpdateTime())) {
            queryWrapper.ge("a.update_time", queryDto.getUpdateTime());
        }
        return baseMapper.getDelOutboundAndDetailsList(queryWrapper);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void bringVerifyFail(Long id, String exceptionMessage) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.AUDIT_FAILED.getCode());
        updateWrapper.set(DelOutbound::getExceptionState, DelOutboundExceptionStateEnum.ABNORMAL.getCode());
        updateWrapper.set(DelOutbound::getExceptionMessage, StringUtils.substring(exceptionMessage, 0, 255));
        updateWrapper.eq(DelOutbound::getId, id);
        this.update(updateWrapper);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void exceptionMessage(Long id, String exceptionMessage) {
        DelOutbound modifyDelOutbound = new DelOutbound();
        modifyDelOutbound.setId(id);
        modifyDelOutbound.setExceptionState(DelOutboundExceptionStateEnum.ABNORMAL.getCode());
        exceptionMessage = org.apache.commons.lang3.StringUtils.substring(exceptionMessage, 0, 255);
        modifyDelOutbound.setExceptionMessage(exceptionMessage);
        this.updateById(modifyDelOutbound);
    }

    @Transactional
    @Override
    public void exceptionFix(Long id) {
        DelOutbound modifyDelOutbound = new DelOutbound();
        modifyDelOutbound.setId(id);
        modifyDelOutbound.setExceptionState(DelOutboundExceptionStateEnum.NORMAL.getCode());
        modifyDelOutbound.setExceptionMessage("");
        this.updateById(modifyDelOutbound);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void bringVerifyFail(DelOutbound delOutbound) {
        delOutbound.setState(DelOutboundStateEnum.AUDIT_FAILED.getCode());
        delOutbound.setExceptionState(DelOutboundExceptionStateEnum.ABNORMAL.getCode());
        String exceptionMessage = delOutbound.getExceptionMessage();
        if (StringUtils.isNotEmpty(exceptionMessage)) {
            exceptionMessage = org.apache.commons.lang3.StringUtils.substring(exceptionMessage, 0, 255);
            delOutbound.setExceptionMessage(exceptionMessage);
        }
        // 设置提审时间
        delOutbound.setBringVerifyTime(new Date());
        this.updateById(delOutbound);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void bringVerifySuccess(DelOutbound delOutbound) {
        delOutbound.setState(DelOutboundStateEnum.DELIVERED.getCode());
        delOutbound.setExceptionState(DelOutboundExceptionStateEnum.NORMAL.getCode());
        // 清空异常信息
        delOutbound.setExceptionMessage("");
        // 设置提审时间
        delOutbound.setBringVerifyTime(new Date());
        this.updateById(delOutbound);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void shipmentFail(DelOutbound delOutbound) {
        delOutbound.setState(DelOutboundStateEnum.PROCESSING.getCode());
        delOutbound.setExceptionState(DelOutboundExceptionStateEnum.ABNORMAL.getCode());
        this.updateById(delOutbound);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void shipmentSuccess(DelOutbound delOutbound) {
        delOutbound.setState(DelOutboundStateEnum.NOTIFY_WHSE_PROCESSING.getCode());
        delOutbound.setExceptionState(DelOutboundExceptionStateEnum.NORMAL.getCode());
        delOutbound.setExceptionMessage("");
        this.updateById(delOutbound);
    }

    @Override
    public DelOutbound getByOrderNo(String orderNo) {
        LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutbound::getOrderNo, orderNo);
        return this.getOne(queryWrapper);
    }

    @Transactional
    @Override
    public void completed(Long id) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.COMPLETED.getCode());
        updateWrapper.set(DelOutbound::getShipmentsTime, new Date());
        updateWrapper.eq(DelOutbound::getId, id);
        this.update(updateWrapper);
    }

    @Transactional
    @Override
    public void updateCompletedState(Long id, String completedState) {
        DelOutbound modifyDelOutbound = new DelOutbound();
        modifyDelOutbound.setId(id);
        modifyDelOutbound.setCompletedState(completedState);
        this.updateById(modifyDelOutbound);
    }

    @Transactional
    @Override
    public void updateCancelledState(Long id, String cancelledState) {
        DelOutbound modifyDelOutbound = new DelOutbound();
        modifyDelOutbound.setId(id);
        modifyDelOutbound.setCancelledState(cancelledState);
        this.updateById(modifyDelOutbound);
    }

    @Transactional
    @Override
    public int canceled(DelOutboundCanceledDto dto) {
        List<Long> ids = dto.getIds();
        // 参数ids为空，直接返回
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        // 根据ids查询单据为空，直接返回
        List<DelOutbound> outboundList = this.listByIds(ids);
        if (CollectionUtils.isEmpty(outboundList)) {
            return 0;
        }
        List<String> orderNos = new ArrayList<>();
        String warehouseCode = outboundList.get(0).getWarehouseCode();
        List<String> reviewedList = new ArrayList<>();
        Map<String, DelOutbound> delOutboundMap = new HashMap<>();
        for (DelOutbound outbound : outboundList) {
            if (!warehouseCode.equals(outbound.getWarehouseCode())) {
                throw new CommonException("999", "只能同一个仓库下的出库单");
            }
            // 处理已完成，已取消的
            if (DelOutboundStateEnum.COMPLETED.getCode().equals(outbound.getState())
                    || DelOutboundStateEnum.CANCELLED.getCode().equals(outbound.getState())) {
                // 已完成，已取消的单据不做处理
                continue;
            }
            // 处理未提审，提审失败的
            String orderNo = outbound.getOrderNo();
            if (DelOutboundStateEnum.REVIEWED.getCode().equals(outbound.getState())
                    || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(outbound.getState())) {
                // 未提审的，提审失败的
                reviewedList.add(orderNo);
                continue;
            }
            // 通知WMS处理的
            orderNos.add(orderNo);
            delOutboundMap.put(orderNo, outbound);
        }
        // 判断有没有处理未提审，提审失败的
        if (CollectionUtils.isNotEmpty(reviewedList)) {
            // 修改状态未已取消
            LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.CANCELLED.getCode());
            updateWrapper.in(DelOutbound::getOrderNo, reviewedList);
            this.update(updateWrapper);
            // 取消冻结的数据
            for (String orderNo : reviewedList) {
                DelOutbound delOutbound = delOutboundMap.get(orderNo);
                // 提审状态
                String bringVerifyState = delOutbound.getBringVerifyState();
                if (StringUtils.isNotEmpty(bringVerifyState)) {
                    // 判断要不要取消冻结库存
                    if (BringVerifyEnum.gt(BringVerifyEnum.FREEZE_INVENTORY, BringVerifyEnum.get(bringVerifyState))) {
                        // 取消冻结库存
                        this.unFreeze(delOutbound.getOrderType(), orderNo, delOutbound.getWarehouseCode());
                    }
                    // 判断要不要取消冻结操作费用
                    if (BringVerifyEnum.gt(BringVerifyEnum.FREEZE_OPERATION, BringVerifyEnum.get(bringVerifyState))) {
                        // 取消冻结操作费用
                        this.unfreezeOperation(orderNo);
                    }
                }
            }
        }
        // 判断是否需要WMS处理
        if (CollectionUtils.isEmpty(orderNos)) {
            return reviewedList.size();
        }
        // 通知WMS取消单据
        ShipmentCancelRequestDto shipmentCancelRequestDto = new ShipmentCancelRequestDto();
        shipmentCancelRequestDto.setOrderNoList(orderNos);
        shipmentCancelRequestDto.setWarehouseCode(warehouseCode);
        shipmentCancelRequestDto.setRemark("");
        ResponseVO responseVO = this.htpOutboundClientService.shipmentDelete(shipmentCancelRequestDto);
        if (null == responseVO || null == responseVO.getSuccess()) {
            throw new CommonException("999", "取消出库单失败");
        }
        if (!responseVO.getSuccess()) {
            throw new CommonException("999", Utils.defaultValue(responseVO.getMessage(), "取消出库单失败2"));
        }
        // 修改单据状态为【仓库取消中】
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.WHSE_CANCELING.getCode());
        updateWrapper.in(DelOutbound::getOrderNo, orderNos);
        return this.baseMapper.update(null, updateWrapper);
    }

    @Override
    public int handler(DelOutboundHandlerDto dto) {
        List<Long> ids = dto.getIds();
        // 参数ids为空，直接返回
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        int result = 0;
        for (Long id : ids) {
            result = result + this.delOutboundAsyncService.shipmentPacking(id);
        }
        return result;
    }

    @Override
    public void label(HttpServletResponse response, DelOutboundLabelDto dto) {
        DelOutbound delOutbound = this.getById(dto.getId());
        if (null == delOutbound) {
            throw new CommonException("999", "单据不存在");
        }
        if (StringUtils.isEmpty(delOutbound.getShipmentOrderNumber())) {
            throw new CommonException("999", "未获取承运商标签");
        }
        String pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber();
        File labelFile = new File(pathname);
        if (!labelFile.exists()) {
            throw new CommonException("999", "标签文件不存在");
        }
        ServletOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = response.getOutputStream();
            //response为HttpServletResponse对象
            response.setContentType("application/pdf;charset=utf-8");
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + delOutbound.getShipmentOrderNumber() + ".pdf");
            inputStream = new FileInputStream(labelFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "读取标签文件失败");
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }
    }

    @Override
    public List<DelOutboundDetailVO> importDetail(String warehouseCode, String sellerCode, List<DelOutboundDetailImportDto> dtoList) {
        // 查询sku
        List<String> skus = dtoList.stream().map(DelOutboundDetailImportDto::getSku).distinct().collect(Collectors.toList());
        InventoryAvailableQueryDto inventoryAvailableQueryDto = new InventoryAvailableQueryDto();
        inventoryAvailableQueryDto.setWarehouseCode(warehouseCode);
        inventoryAvailableQueryDto.setCusCode(sellerCode);
        inventoryAvailableQueryDto.setSkus(skus);
        List<InventoryAvailableListVO> availableList = this.inventoryFeignClientService.queryAvailableList(inventoryAvailableQueryDto);
        Map<String, InventoryAvailableListVO> availableMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(availableList)) {
            for (InventoryAvailableListVO vo : availableList) {
                availableMap.put(vo.getSku(), vo);
            }
        }
        List<DelOutboundDetailVO> voList = new ArrayList<>();
        for (DelOutboundDetailImportDto dto : dtoList) {
            InventoryAvailableListVO available = availableMap.get(dto.getSku());
            if (null != available) {
                DelOutboundDetailVO vo = BeanMapperUtil.map(available, DelOutboundDetailVO.class);
                vo.setQty(dto.getQty());
                voList.add(vo);
            }
        }
        return voList;
    }

    @Override
    public List<QueryChargeVO> getDelOutboundCharge(QueryChargeDto queryDto) {
        List<QueryChargeVO> list = baseMapper.selectDelOutboundList(queryDto);
        for (QueryChargeVO queryChargeVO : list) {
            String orderNo = queryChargeVO.getOrderNo();

            List<DelOutboundDetail> delOutboundDetails = delOutboundDetailService.selectDelOutboundDetailList(new DelOutboundDetail().setOrderNo(orderNo));
            //计算数量 = 多个SKU的数量+包材（1个）
            queryChargeVO.setQty(ListUtils.emptyIfNull(delOutboundDetails).stream().map(value -> StringUtils.isBlank(value.getBindCode())
                    ? value.getQty() : value.getQty() + 1).reduce(Long::sum).orElse(0L));

            List<DelOutboundCharge> delOutboundCharges = delOutboundChargeService.listCharges(orderNo);

            this.setAmount(queryChargeVO, delOutboundCharges);
        }
        return list;
    }

    @Override
    public List<DelOutboundExportListDto> exportList(DelOutboundListQueryDto queryDto) {
        QueryWrapper<DelOutboundListQueryDto> queryWrapper = new QueryWrapper<>();
        DelOutboundServiceImplUtil.handlerQueryWrapper(queryWrapper, queryDto);
        return this.baseMapper.exportList(queryWrapper);
    }

    /**
     * 查询物流基础费、偏远地区费、超大附加费、燃油附加费
     *
     * @param queryChargeVO      delOutboundChargeListVO
     * @param delOutboundCharges delOutboundCharges
     */
    private void setAmount(QueryChargeVO queryChargeVO, List<DelOutboundCharge> delOutboundCharges) {
        ListUtils.emptyIfNull(delOutboundCharges).forEach(item -> {
            String chargeNameEn = item.getChargeNameEn();
            if (chargeNameEn != null) {
                switch (chargeNameEn) {
                    case "Base Shipping Fee":
                        queryChargeVO.setBaseShippingFee(item.getAmount());
                        break;
                    case "Remote Area Surcharge":
                        queryChargeVO.setRemoteAreaSurcharge(item.getAmount());
                        break;
                    case "Over-Size Surcharge":
                        queryChargeVO.setOverSizeSurcharge(item.getAmount());
                        break;
                    case "Fuel Charge":
                        queryChargeVO.setFuelCharge(item.getAmount());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int setPurchaseNo(String purchaseNo, List<String> orderNoList) {
        if (CollectionUtils.isEmpty(orderNoList)) return 1;
        int update = baseMapper.update(new DelOutbound(), Wrappers.<DelOutbound>lambdaUpdate()
                .in(DelOutbound::getOrderNo, orderNoList)
                .set(DelOutbound::getPurchaseNo, purchaseNo)
        );
        logger.info("回写出库单{}-采购单号{},修改条数{}", JSONObject.toJSONString(orderNoList), purchaseNo, update);
        return update;
    }
}

