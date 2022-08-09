package com.szmsd.delivery.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.constant.SerialNumberConstant;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundExceptionStateEnum;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundPackingTypeConstant;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundCombinationService;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundDocService;
import com.szmsd.delivery.service.IDelOutboundPackingService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.BringVerifyEnum;
import com.szmsd.delivery.service.wrapper.IDelOutboundAsyncService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.service.wrapper.IDelOutboundExceptionService;
import com.szmsd.delivery.util.PackageInfo;
import com.szmsd.delivery.util.PackageUtil;
import com.szmsd.delivery.util.Utils;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundAddressVO;
import com.szmsd.delivery.vo.DelOutboundDetailListVO;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.delivery.vo.DelOutboundLabelResponse;
import com.szmsd.delivery.vo.DelOutboundListExceptionMessageExportVO;
import com.szmsd.delivery.vo.DelOutboundListExceptionMessageVO;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import com.szmsd.delivery.vo.DelOutboundReassignExportListVO;
import com.szmsd.delivery.vo.DelOutboundThirdPartyVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.ShipmentCancelRequestDto;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.domain.dto.QueryFinishListDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.QueryFinishListVO;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.BatchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    @SuppressWarnings({"all"})
    @Autowired
    private RemoteAttachmentService remoteAttachmentService;
    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;
    @Autowired
    private IDelOutboundChargeService delOutboundChargeService;
    @Autowired
    private IDelOutboundAsyncService delOutboundAsyncService;
    @SuppressWarnings({"all"})
    @Autowired
    private OperationFeignService operationFeignService;
    @Autowired
    private IDelOutboundPackingService delOutboundPackingService;
    @Resource
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private IDelOutboundCombinationService delOutboundCombinationService;
    @Autowired
    private IDelOutboundExceptionService delOutboundExceptionService;
    @Autowired
    private IDelOutboundDocService delOutboundDocService;

    @Autowired
    private BasSubClientService basSubClientService;
    @Autowired
    @Lazy
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    /**
     * 查询出库单模块
     *
     * @param id 出库单模块ID
     * @return 出库单模块
     */
    @Override
    public DelOutboundVO selectDelOutboundById(String id) {
        DelOutbound delOutbound = baseMapper.selectById(id);
        return this.selectDelOutboundVO(delOutbound);
    }

    @Override
    public DelOutboundVO selectDelOutboundByOrderNo(String orderNo) {
        LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutbound::getOrderNo, orderNo);
        DelOutbound delOutbound = super.getOne(queryWrapper);
        return this.selectDelOutboundVO(delOutbound);
    }

    @Override
    public DelOutboundThirdPartyVO getInfoForThirdParty(DelOutboundVO vo) {
        LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutbound::getSellerCode, vo.getSellerCode());
        queryWrapper.eq(DelOutbound::getOrderNo, vo.getOrderNo());
        DelOutbound delOutbound = super.getOne(queryWrapper);
        if(delOutbound == null){
            throw new CommonException("400", "单据不存在");
        }
        DelOutboundThirdPartyVO delOutboundThirdPartyVO =
                BeanMapperUtil.map(delOutbound, DelOutboundThirdPartyVO.class);

        Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("065");
        Map<String, String> map = listMap.get("065").stream().collect(Collectors.toMap(BasSubWrapperVO::getSubValue,
                BasSubWrapperVO::getSubName, (key1, key2) -> key2));
        delOutboundThirdPartyVO.setStateName(map.get(delOutboundThirdPartyVO.getState()));
        return delOutboundThirdPartyVO;
    }

    private DelOutboundVO selectDelOutboundVO(DelOutbound delOutbound) {
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("400", "单据不存在");
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
            // 可用库存为0的也需要查询出来
            inventoryAvailableQueryDto.setQueryType(2);
            // 集运出库的
            if (DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(delOutbound.getOrderType())) {
                inventoryAvailableQueryDto.setSource("084002");
            }
            List<InventoryAvailableListVO> availableList = this.inventoryFeignClientService.queryAvailableList2(inventoryAvailableQueryDto);
            // 去查询SKU的信息，集运出库需要查看产品详情，需要单独去查询
            Map<String, BaseProduct> baseProductMap = null;
            if (DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(delOutbound.getOrderType())) {
                BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
                conditionQueryDto.setSkus(skus);
                List<BaseProduct> baseProductList = this.baseProductClientService.queryProductList(conditionQueryDto);
                if (CollectionUtils.isNotEmpty(baseProductList)) {
                    baseProductMap = baseProductList.stream().collect(Collectors.toMap(BaseProduct::getCode, v -> v, (a, b) -> a));
                }
            }
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
                if (DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(delOutbound.getOrderType()) && null != baseProductMap) {
                    BaseProduct baseProduct = baseProductMap.get(vo.getSku());
                    if (null != baseProduct) {
                        vo.setProductDescription(baseProduct.getProductDescription());
                    }
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
        // 组合，拆分SKU
        if (DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())
                || DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(delOutbound.getOrderType())) {
            delOutboundVO.setCombinations(this.delOutboundCombinationService.listByOrderNo(orderNo));
            // 查询SKU可用数量
            if (DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(delOutbound.getOrderType())) {
                InventoryAvailableQueryDto inventoryAvailableQueryDto = new InventoryAvailableQueryDto();
                inventoryAvailableQueryDto.setWarehouseCode(delOutbound.getWarehouseCode());
                inventoryAvailableQueryDto.setCusCode(delOutbound.getSellerCode());
                List<String> skus = new ArrayList<>();
                skus.add(delOutbound.getNewSku());
                inventoryAvailableQueryDto.setSkus(skus);
                // 可用库存为0的也需要查询出来
                inventoryAvailableQueryDto.setQueryType(2);
                List<InventoryAvailableListVO> availableList = this.inventoryFeignClientService.queryAvailableList2(inventoryAvailableQueryDto);
                if (CollectionUtils.isNotEmpty(availableList)) {
                    InventoryAvailableListVO availableListVO = availableList.get(0);
                    if (null != availableListVO) {
                        delOutboundVO.setAvailableInventory(availableListVO.getAvailableInventory());
                    }
                }
            }
        }
        if (DelOutboundOrderTypeEnum.BULK_ORDER.getCode().equals(delOutbound.getOrderType())) {
            BasAttachmentQueryDTO dto = new BasAttachmentQueryDTO();
            dto.setBusinessNo(delOutbound.getOrderNo());
            dto.setAttachmentType(AttachmentTypeEnum.BULK_ORDER_DETAIL.getAttachmentType());
            List<BasAttachment> attachment = ListUtils.emptyIfNull(remoteAttachmentService.list(dto).getData());
            for (BasAttachment basAttachment : attachment) {
                for (DelOutboundDetailVO detailVO : delOutboundVO.getDetails()) {
                    if (StringUtils.equals("" + detailVO.getId(), basAttachment.getBusinessItemNo())) {
                            detailVO.setSkuFile(Arrays.asList(
                            new AttachmentFileDTO().setId(basAttachment.getId()).setAttachmentName(basAttachment.getAttachmentName()).
                                    setAttachmentUrl(basAttachment.getAttachmentUrl())));

                    }
                }
            }


        }

        if (DelOutboundOrderTypeEnum.MULTIPLE_PIECES.getCode().equals(delOutbound.getOrderType())) {
            //一票多件
            BasAttachmentQueryDTO dto = new BasAttachmentQueryDTO();
            dto.setBusinessNo(delOutbound.getOrderNo());
            List<BasAttachment> attachment = ListUtils.emptyIfNull(remoteAttachmentService.list(dto).getData());

            List<AttachmentDataDTO> attachmentDataDTOList = new ArrayList();
            for (BasAttachment basAttachment : attachment) {
                if (AttachmentTypeEnum.MULTIPLE_PIECES_BOX_MARK.getAttachmentType().equals(basAttachment.getAttachmentType())
                        || AttachmentTypeEnum.MULTIPLE_PIECES_INVOICE.getAttachmentType().equals(basAttachment.getAttachmentType())) {
                    attachmentDataDTOList.add(new AttachmentDataDTO().setId(basAttachment.getId()).setAttachmentName(basAttachment.getAttachmentName())
                            .setAttachmentType(basAttachment.getAttachmentType())
                            .setAttachmentUrl(basAttachment.getAttachmentUrl()));
                } else {

                    //明细相关附件
                    for (DelOutboundDetailVO detailVO : delOutboundVO.getDetails()) {
                        if (StringUtils.equals("" + detailVO.getId(), basAttachment.getBusinessItemNo())) {
                            if (AttachmentTypeEnum.MULTIPLE_PIECES_BOX_DETAIL.getAttachmentType().equals(basAttachment.getAttachmentType())) {
                                detailVO.setBoxMarkFile(Arrays.asList(
                                        new AttachmentFileDTO().setId(basAttachment.getId()).setAttachmentName(basAttachment.getAttachmentName()).
                                                setAttachmentUrl(basAttachment.getAttachmentUrl())));
                            } else if (AttachmentTypeEnum.MULTIPLE_PIECES_SKU.getAttachmentType().equals(basAttachment.getAttachmentType())) {
                                detailVO.setSkuFile(Arrays.asList(new AttachmentFileDTO().setId(basAttachment.getId()).
                                        setAttachmentName(basAttachment.getAttachmentName()).setAttachmentUrl(basAttachment.getAttachmentUrl())));
                            }
                        }
                    }


                }
            }

            delOutboundVO.setDocumentsFiles(attachmentDataDTOList);
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
                .eq(DelOutbound::getDelFlag, "0")
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
            inventoryAvailableQueryDto.setQueryType(2);
            //如果没有库存不会塞数据 先方判断外面
            List<InventoryAvailableListVO> availableList = this.inventoryFeignClientService.queryAvailableList2(inventoryAvailableQueryDto);

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
        //添加图片
        resultList.forEach(x -> {
            BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO().setAttachmentType(AttachmentTypeEnum.SKU_IMAGE.getAttachmentType()).setBusinessNo(x.getSku());
            List<BasAttachment> attachment = ListUtils.emptyIfNull(remoteAttachmentService.list(basAttachmentQueryDTO).getData());
            if (CollectionUtils.isNotEmpty(attachment)) {
                x.setEditionImage(new AttachmentFileDTO().setId(attachment.get(0).getId()).setAttachmentName(attachment.get(0).getAttachmentName()).setAttachmentUrl(attachment.get(0).getAttachmentUrl()));
            }
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
        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = CollectionUtils.isNotEmpty(SecurityUtils.getLoginUser().getPermissions()) ? SecurityUtils.getLoginUser().getPermissions().get(0) : "";
            if (StringUtils.isEmpty(queryDto.getCustomCode())) {
                queryDto.setCustomCode(cusCode);
            }
        }
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
    public DelOutboundAddResponse insertDelOutbound(DelOutboundDto dto) {
        if (!DelOutboundOrderTypeEnum.has(dto.getOrderType())) {
            throw new CommonException("400", "订单类型不存在");
        }
        // 来源为新增
        dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_ADD);
        return this.createDelOutbound(dto);
    }

    @Transactional
    @Override
    public DelOutboundAddResponse insertDelOutboundShopify(DelOutboundDto dto) {
        if (!DelOutboundOrderTypeEnum.has(dto.getOrderType())) {
            throw new CommonException("400", "订单类型不存在");
        }
        // 来源为Shopify
        dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_SHOPIFY);
        return this.createDelOutbound(dto);
    }

    private void docValid(DelOutboundDto dto) {
        if (DelOutboundConstant.SOURCE_TYPE_DOC.equals(dto.getSourceType())) {
            List<DelOutboundDetailDto> details = dto.getDetails();
            if (CollectionUtils.isEmpty(details)) {
                throw new CommonException("400", "明细信息不能为空");
            }
            List<String> skus = details.stream().map(DelOutboundDetailDto::getSku).distinct().collect(Collectors.toList());
            // 判断地址信息上的国家是否存在
            DelOutboundAddressDto addressDto = dto.getAddress();
            if (null != addressDto && StringUtils.isNotEmpty(dto.getShipmentRule()) && StringUtils.isNotEmpty(addressDto.getCountryCode())) {
                // 验证产品是否有效
                DelOutboundOtherInServiceDto inServiceDto = new DelOutboundOtherInServiceDto();
                inServiceDto.setClientCode(dto.getSellerCode());
                inServiceDto.setCountryCode(addressDto.getCountryCode());
                inServiceDto.setWarehouseCode(dto.getWarehouseCode());
                inServiceDto.setSkus(skus);
                String shipmentRule = dto.getShipmentRule();
                if (!this.delOutboundDocService.inServiceValid(inServiceDto, shipmentRule)) {
                    throw new CommonException("400", "发货规则[" + shipmentRule + "]不存在");
                }
            }
            if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(dto.getOrderType())) {
                // 如果是转运出库，后续的SKU验证不需要
                return;
            }
            // 查询sku
            BaseProductConditionQueryDto productConditionQueryDto = new BaseProductConditionQueryDto();
            productConditionQueryDto.setSkus(skus);
            productConditionQueryDto.setSellerCode(dto.getSellerCode());
            if (DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(dto.getOrderType())) {
                // 集运出库只查询集运的SKU
                productConditionQueryDto.setSource("084002");
            }
            List<BaseProduct> productList = this.baseProductClientService.queryProductList(productConditionQueryDto);
            if (CollectionUtils.isEmpty(productList)) {
                throw new CommonException("400", "查询SKU信息失败");
            }
            Map<String, BaseProduct> productMap = new HashMap<>(productList.size());
            for (BaseProduct product : productList) {
                productMap.put(product.getCode(), product);
                if (StringUtils.isNotEmpty(product.getBindCode())) {
                    // 将SKU的包材也添加到查询条件中
                    skus.add(product.getBindCode());
                }
            }
            if (DelOutboundOrderTypeEnum.NORMAL.getCode().equals(dto.getOrderType())
                    || DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(dto.getOrderType())
                    || DelOutboundOrderTypeEnum.BATCH.getCode().equals(dto.getOrderType())
                    || DelOutboundOrderTypeEnum.DESTROY.getCode().equals(dto.getOrderType())) {

                InventoryAvailableQueryDto inventoryAvailableQueryDto = new InventoryAvailableQueryDto();
                String warehouseCode = dto.getWarehouseCode();
                inventoryAvailableQueryDto.setWarehouseCode(warehouseCode);
                inventoryAvailableQueryDto.setCusCode(dto.getSellerCode());
                inventoryAvailableQueryDto.setSkus(skus);
                // 库存是0的也查询出来，自行做数量验证。同时也能验证SKU是不是自己的
                inventoryAvailableQueryDto.setQueryType(2);
                List<InventoryAvailableListVO> availableList = this.inventoryFeignClientService.queryAvailableList2(inventoryAvailableQueryDto);
                Map<String, InventoryAvailableListVO> availableMap = new HashMap<>();
                Map<String, Integer> availableInventoryMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(availableList)) {
                    for (InventoryAvailableListVO vo : availableList) {
                        availableMap.put(vo.getSku(), vo);
                    }
                }
                for (DelOutboundDetailDto detail : details) {
                    String sku = detail.getSku();
                    BaseProduct product = productMap.get(sku);
                    if (null == product) {
                        throw new CommonException("400", "SKU[" + sku + "]不属于当前客户");
                    }
                    detail.setLength(product.getLength());
                    detail.setWidth(product.getWidth());
                    detail.setHeight(product.getHeight());
                    detail.setWeight(product.getWeight());
                    InventoryAvailableListVO vo = availableMap.get(sku);
                    if (null == vo) {
                        throw new CommonException("400", "SKU[" + sku + "]在[" + warehouseCode + "]仓库没有库存信息");
                    }
                    int aiq = availableInventoryMap.getOrDefault(sku, 0);
                    Integer inventory = vo.getAvailableInventory();
                    Integer outQty = Math.toIntExact(detail.getQty() + aiq);
                    if (outQty > inventory) {
                        throw new CommonException("400", "SKU[" + sku + "]库存数量不足，出库数量：" + outQty + "，库存数量：" + inventory);
                    }
                    availableInventoryMap.put(sku, outQty);
                    // 验证包材数量
                    String bindCode = product.getBindCode();
                    if (StringUtils.isNotEmpty(bindCode)) {
                        // 添加包材绑定信息
                        detail.setBindCode(bindCode);
                        vo = availableMap.get(bindCode);
                        if (null == vo) {
                            throw new CommonException("400", "SKU[" + sku + "]的包材[" + bindCode + "]不存在");
                        }
                        if (outQty > vo.getAvailableInventory()) {
                            throw new CommonException("400", "SKU[" + sku + "]的包材[" + bindCode + "]库存数量不足，出库数量：" + outQty + "，库存数量：" + vo.getAvailableInventory());
                        }
                    }
                }
            } else if (DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(dto.getOrderType())) {
                for (DelOutboundDetailDto detail : details) {
                    String sku = detail.getSku();
                    BaseProduct product = productMap.get(sku);
                    if (null == product) {
                        throw new CommonException("400", "SKU[" + sku + "]不属于当前客户");
                    }
                    detail.setLength(product.getLength());
                    detail.setWidth(product.getWidth());
                    detail.setHeight(product.getHeight());
                    detail.setWeight(product.getWeight());
                    detail.setBindCode(product.getBindCode());
                }
            }
        }
    }

    private void checkRefNo(DelOutboundDto dto, Long id) {
        if (StringUtils.isNotEmpty(dto.getRefNo())) {
            LambdaQueryWrapper<DelOutbound> queryWrapper = new LambdaQueryWrapper<DelOutbound>();
            queryWrapper.eq(DelOutbound::getRefNo, dto.getRefNo());
            if (id != null) {
                queryWrapper.ne(DelOutbound::getId, dto.getId());
            }
            queryWrapper.ne(DelOutbound::getState, DelOutboundStateEnum.CANCELLED.getCode());
            queryWrapper.eq(DelOutbound::getDelFlag, "0");
            Integer size = baseMapper.selectCount(queryWrapper);
            if (size > 0) {
                throw new CommonException("400", "Refno 必须唯一值" + dto.getRefNo());
            }
        }
    }

    private DelOutboundAddResponse createDelOutbound(DelOutboundDto dto) {
        logger.info(">>>>>[创建出库单]1.0 开始创建出库单");
        TimeInterval timer = DateUtil.timer();
        DelOutboundAddResponse response = new DelOutboundAddResponse();
        String orderNo;
        if (StringUtils.equals(dto.getSourceType(), DelOutboundConstant.SOURCE_TYPE_ADD)) {
            //单数据处理直接抛异常
            logger.info(">>>>>[创建出库单]1.1 校验Refno");
            this.checkRefNo(dto, null);
            logger.info(">>>>>[创建出库单]1.2 校验Refno完成，{}", timer.intervalRestart());
        }
        // 创建出库单
        try {
            // DOC的验证SKU
            logger.info(">>>>>[创建出库单]2.0 doc校验");
            this.docValid(dto);
            logger.info(">>>>>[创建出库单]2.1 doc校验完成，{}", timer.intervalRestart());

            if (!StringUtils.equals(dto.getSourceType(), DelOutboundConstant.SOURCE_TYPE_ADD)) {
                //批量数据处理记录异常
                logger.info(">>>>>[创建出库单]2.2 校验Refno");
                this.checkRefNo(dto, null);
                logger.info(">>>>>[创建出库单]2.3 校验Refno完成，{}", timer.intervalRestart());
            }
            logger.info(">>>>>[创建出库单]3.0 开始初始化出库单属性");
            DelOutbound delOutbound = BeanMapperUtil.map(dto, DelOutbound.class);
            if (null == delOutbound.getCodAmount()) {
                delOutbound.setCodAmount(BigDecimal.ZERO);
            }
            // 生成出库单号
            // 流水号规则：CK + 客户代码 + （年月日 + 8位流水）
            String sellerCode;
            if (StringUtils.isNotEmpty(delOutbound.getCustomCode())) {
                sellerCode = delOutbound.getCustomCode();
            } else {
                sellerCode = delOutbound.getSellerCode();
                // 兼容
                delOutbound.setCustomCode(sellerCode);
            }
            if (StringUtils.isEmpty(sellerCode)) {
                throw new CommonException("400", "操作失败，客户代码不能为空");
            }
            String prefix = "CK";
            if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(dto.getReassignType())) {
                prefix = "RE" + prefix;
            }
            delOutbound.setOrderNo(orderNo = (prefix + sellerCode + this.serialNumberClientService.generateNumber(SerialNumberConstant.DEL_OUTBOUND_NO)));
            // 默认状态
            delOutbound.setState(DelOutboundStateEnum.REVIEWED.getCode());
            // 默认异常状态
            delOutbound.setExceptionState(DelOutboundExceptionStateEnum.NORMAL.getCode());
            logger.info(">>>>>[创建出库单]3.1 出库单对象赋值，生成流水号，{}", timer.intervalRestart());
            // 计算发货类型
            delOutbound.setShipmentType(this.buildShipmentType(dto));
            logger.info(">>>>>[创建出库单]3.2 计算发货类型，{}", timer.intervalRestart());
            // 计算包裹大小
            this.countPackageSize(delOutbound, dto);
            logger.info(">>>>>[创建出库单]3.3 计算包裹大小，{}", timer.intervalRestart());
            // 保存出库单
            int insert = baseMapper.insert(delOutbound);
            logger.info(">>>>>[创建出库单]3.4 保存出库单，{}", timer.intervalRestart());
            if (insert == 0) {
                throw new CommonException("400", "保存出库单失败！");
            }
            DelOutboundOperationLogEnum.CREATE.listener(delOutbound);
            // 保存地址
            this.saveAddress(dto, delOutbound.getOrderNo());
            logger.info(">>>>>[创建出库单]3.5 保存地址信息，{}", timer.intervalRestart());

            if (DelOutboundOrderTypeEnum.MULTIPLE_PIECES.getCode().equals(delOutbound.getOrderType())) {
                //如果明细中商标为空，系统自动生成

                for (DelOutboundDetailDto detailDto : dto.getDetails()) {
                    if (StringUtils.isEmpty(detailDto.getBoxMark())) {
                        detailDto.setBoxMark(this.serialNumberClientService.generateNumber(SerialNumberConstant.BOX_MARK));
                    }
                }
            }

            // 保存明细
            this.saveDetail(dto, delOutbound.getOrderNo());
            logger.info(">>>>>[创建出库单]3.6 保存明细信息，{}", timer.intervalRestart());


            if(DelOutboundOrderTypeEnum.BULK_ORDER.getCode().equals(delOutbound.getOrderType())){
                //保存箱标数据
                AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(dto.getBatchLabels()).attachmentTypeEnum(AttachmentTypeEnum.BULK_ORDER_BOX).build();
                this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
                //保存明细附件数据
                for (DelOutboundDetailDto detail : dto.getDetails()) {
                    if (detail.getSkuFile() != null && detail.getSkuFile().size() > 0) {
                        AttachmentDataDTO attachmentDataDTO = detail.getSkuFile().get(0);
                        // 箱标明细
                        AttachmentDTO boxMarkDetailFiels = AttachmentDTO.builder().businessNo(orderNo).businessItemNo("" + detail.getId()).fileList(
                                        Arrays.asList(new AttachmentDataDTO().setAttachmentUrl(attachmentDataDTO.getAttachmentUrl())
                                                .setAttachmentName(attachmentDataDTO.getAttachmentName()))).
                                attachmentTypeEnum(AttachmentTypeEnum.BULK_ORDER_DETAIL).build();
                        this.remoteAttachmentService.saveAndUpdate(boxMarkDetailFiels);

                    }
                }
            }
            // 附件信息
            if (!DelOutboundOrderTypeEnum.MULTIPLE_PIECES.getCode().equals(delOutbound.getOrderType())
            && !DelOutboundOrderTypeEnum.BULK_ORDER.getCode().equals(delOutbound.getOrderType())
            ) {
                AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(dto.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT).build();
                this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
            }
            logger.info(">>>>>[创建出库单]3.7 保存附件信息，{}", timer.intervalRestart());

            // 批量出库保存装箱信息
            if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.NORMAL.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())
            ) {
                // 装箱信息
                List<DelOutboundPackingDto> packings = dto.getPackings();
                this.delOutboundPackingService.save(orderNo, packings, false);
                // 箱标文件
                AttachmentDTO batchLabel = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(dto.getBatchLabels()).attachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL).build();
                this.remoteAttachmentService.saveAndUpdate(batchLabel);
                logger.info(">>>>>[创建出库单]3.8 保存批量出库相关信息，{}", timer.intervalRestart());
            }
            if (DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(delOutbound.getOrderType())) {
                // 组合信息
                this.delOutboundCombinationService.save(orderNo, dto.getCombinations());
                logger.info(">>>>>[创建出库单]3.9 保存NEW_SKU/SPLIT_SKU相关信息，{}", timer.intervalRestart());
            }

            if (DelOutboundOrderTypeEnum.MULTIPLE_PIECES.getCode().equals(delOutbound.getOrderType())) {
                //一票多件出库
                //箱标
                List<AttachmentDataDTO> batchLabels = new ArrayList();
                //发货单
                List<AttachmentDataDTO> documentsFiles = new ArrayList();
                if (dto.getDocumentsFiles() != null) {
                    for (AttachmentDataDTO vo : dto.getDocumentsFiles()) {
                        if (AttachmentTypeEnum.MULTIPLE_PIECES_BOX_MARK.getAttachmentType().equals(vo.getAttachmentType())) {
                            batchLabels.add(vo);
                        } else if (AttachmentTypeEnum.MULTIPLE_PIECES_INVOICE.getAttachmentType().equals(vo.getAttachmentType())) {
                            documentsFiles.add(vo);
                        }
                    }
                }
                // 箱标文件
                AttachmentDTO batchLabel = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(batchLabels)
                        .attachmentTypeEnum(AttachmentTypeEnum.MULTIPLE_PIECES_BOX_MARK).build();
                this.remoteAttachmentService.saveAndUpdate(batchLabel);
                // 发货单文件
                AttachmentDTO invoiceBatchLabel = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(documentsFiles)
                        .attachmentTypeEnum(AttachmentTypeEnum.MULTIPLE_PIECES_INVOICE).build();
                this.remoteAttachmentService.saveAndUpdate(invoiceBatchLabel);
                for (DelOutboundDetailDto detail : dto.getDetails()) {
                    if (detail.getBoxMarkFile() != null && detail.getBoxMarkFile().size() > 0) {
                        AttachmentDataDTO attachmentDataDTO = detail.getBoxMarkFile().get(0);

                        // 箱标明细
                        AttachmentDTO boxMarkDetailFiels = AttachmentDTO.builder().businessNo(orderNo).businessItemNo("" + detail.getId()).fileList(
                                Arrays.asList(new AttachmentDataDTO().setAttachmentUrl(attachmentDataDTO.getAttachmentUrl())
                                        .setAttachmentName(attachmentDataDTO.getAttachmentName()))).
                                attachmentTypeEnum(AttachmentTypeEnum.MULTIPLE_PIECES_BOX_DETAIL).build();
                        this.remoteAttachmentService.saveAndUpdate(boxMarkDetailFiels);

                    }
                    if (detail.getSkuFile() != null && detail.getSkuFile().size() > 0) {
                        AttachmentDataDTO attachmentDataDTO = detail.getSkuFile().get(0);
                        // SKU
                        AttachmentDTO skuFiles = AttachmentDTO.builder().businessNo(orderNo).businessItemNo("" + detail.getId()).fileList(
                                Arrays.asList(new AttachmentDataDTO().setAttachmentUrl(attachmentDataDTO.getAttachmentUrl()).
                                        setAttachmentName(attachmentDataDTO.getAttachmentName()))).
                                attachmentTypeEnum(AttachmentTypeEnum.MULTIPLE_PIECES_SKU).build();
                        this.remoteAttachmentService.saveAndUpdate(skuFiles);
                    }

                }
                logger.info(">>>>>[创建出库单]3.10 保存一票多件相关信息，{}", timer.intervalRestart());
            }
            response.setStatus(true);
            response.setId(delOutbound.getId());
            response.setOrderNo(orderNo);
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 返回异常错误信息
            response.setStatus(false);
            response.setMessage(e.getMessage());
            // return response;
            // 返回错误，事务回滚
            throw e;
        }
    }

    /**
     * 取消冻结操作费用
     *
     * @param orderNo   orderNo
     * @param orderType orderType
     */
    private void unfreezeOperation(String orderNo, String orderType) {
        DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
        delOutboundOperationVO.setOrderType(orderType);
        delOutboundOperationVO.setOrderNo(orderNo);
        R<?> ur = this.operationFeignService.delOutboundThaw(delOutboundOperationVO);
        DelOutboundServiceImplUtil.thawOperationThrowCommonException(ur);
    }

    @Transactional
    @Override
    public List<DelOutboundAddResponse> insertDelOutbounds(List<DelOutboundDto> dtoList) {
        List<DelOutboundAddResponse> result = new ArrayList<>();
        int index = 1;
        List<String> refNoList = new ArrayList<String>();
        for (DelOutboundDto dto : dtoList) {
            DelOutboundAddResponse delOutbound = null;
            if (StringUtils.isNotEmpty(dto.getRefNo()) && refNoList.contains(dto.getRefNo())) {
                delOutbound = new DelOutboundAddResponse();
                logger.error("本次操作数据中Refno 必须唯一值" + dto.getRefNo());
                // 返回异常错误信息
                delOutbound.setStatus(false);
                delOutbound.setMessage("本次操作数据中Refno 必须唯一值" + dto.getRefNo());
            } else {
                refNoList.add(dto.getRefNo());
                delOutbound = this.createDelOutbound(dto);
            }
            delOutbound.setIndex(index);
            result.add(delOutbound);
            index++;
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
        // 重派不计算长宽高重量
        if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
            return;
        }
        // 转运特殊处理
        List<DelOutboundDetailDto> details = dto.getDetails();
        if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
            double length = Utils.defaultValue(dto.getLength());
            double width = Utils.defaultValue(dto.getWidth());
            double height = Utils.defaultValue(dto.getHeight());
            double weight = Utils.defaultValue(dto.getWeight());
            delOutbound.setLength(length);
            delOutbound.setWidth(width);
            delOutbound.setHeight(height);
            delOutbound.setWeight(weight);
            // 规格，长*宽*高
            delOutbound.setSpecifications(length + "*" + width + "*" + height);
        } else if (DelOutboundOrderTypeEnum.MULTIPLE_PIECES.getCode().equals(delOutbound.getOrderType())) {
            delOutbound.setLength(0D);
            delOutbound.setWidth(0D);
            delOutbound.setHeight(0D);
            delOutbound.setWeight(0D);
            // 规格，长*宽*高
            delOutbound.setSpecifications(0 + "*" + 0 + "*" + 0);
        } else if(DelOutboundOrderTypeEnum.BULK_ORDER.getCode().equals(delOutbound.getOrderType())){
            BigDecimal length = BigDecimal.ZERO;
            BigDecimal width = BigDecimal.ZERO;
            BigDecimal height = BigDecimal.ZERO;
            BigDecimal weight = BigDecimal.ZERO;
            Set<String> set = new HashSet<>();

            for (DelOutboundDetailDto detail : details) {
                if (!set.contains(detail.getBoxMark())) {
                    length = length.add(new BigDecimal(detail.getBoxLength()));
                    width = width.add(new BigDecimal(detail.getBoxWidth()));
                    height = height.add(new BigDecimal(detail.getBoxHeight()));
                    weight = weight.add(new BigDecimal(detail.getBoxWeight()));
                    set.add(detail.getBoxMark());
                }
            }
            delOutbound.setLength(length.doubleValue());
            delOutbound.setWidth(width.doubleValue());
            delOutbound.setHeight(height.doubleValue());
            delOutbound.setWeight(weight.doubleValue());

        }else {
            // 查询包材的信息
            Set<String> skus = new HashSet<>();
            for (DelOutboundDetailDto detail : details) {
                // sku包材信息
                if (StringUtils.isNotEmpty(detail.getBindCode())) {
                    skus.add(detail.getBindCode());
                }
            }
            Map<String, BaseProduct> productMap = null;
            if (!skus.isEmpty()) {
                BaseProductConditionQueryDto baseProductConditionQueryDto = new BaseProductConditionQueryDto();
                baseProductConditionQueryDto.setSkus(new ArrayList<>(skus));
                List<BaseProduct> productList = this.baseProductClientService.queryProductList(baseProductConditionQueryDto);
                if (CollectionUtils.isNotEmpty(productList)) {
                    productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, v -> v, (v1, v2) -> v1));
                }
            }
            if (null == productMap) {
                productMap = Collections.emptyMap();
            }
            double weight = 0.0;
            List<PackageInfo> packageInfoList = new ArrayList<>();
            for (DelOutboundDetailDto detail : details) {
                // sku信息
                weight += Utils.defaultValue(detail.getWeight());
                packageInfoList.add(new PackageInfo(detail.getLength(), detail.getWidth(), detail.getHeight()));
                // 包材信息
                if (StringUtils.isNotEmpty(detail.getBindCode())) {
                    BaseProduct product = productMap.get(detail.getBindCode());
                    if (null != product) {
                        weight += Utils.defaultValue(product.getWeight());
                        packageInfoList.add(new PackageInfo(product.getLength(), product.getWidth(), product.getHeight()));
                    }
                }
            }
            delOutbound.setWeight(weight);
            PackageInfo packageInfo = PackageUtil.count(packageInfoList);
            delOutbound.setLength(packageInfo.getLength());
            delOutbound.setWidth(packageInfo.getWidth());
            delOutbound.setHeight(packageInfo.getHeight());
            // 规格，长*宽*高
            delOutbound.setSpecifications(packageInfo.getLength() + "*" + packageInfo.getWidth() + "*" + packageInfo.getHeight());
        }
    }

    private String buildShipmentType(DelOutboundDto dto) {
        List<DelOutboundDetailDto> details = dto.getDetails();
        return this.baseProductClientService.buildShipmentType(dto.getSellerCode(), details.stream().map(DelOutboundDetailDto::getSku).collect(Collectors.toList()));
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
            for (int i = 0; i < delOutboundDetailList.size(); i++) {
                details.get(i).setId(delOutboundDetailList.get(i).getId());
            }
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
            throw new CommonException("400", "单据不存在");
        }
        // 可以修改的状态：待提审，审核失败
        if (!(DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound.getState())
                || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound.getState()))) {
            throw new CommonException("400", "单据不能修改");
        }
        if (null == delOutbound.getCodAmount()) {
            delOutbound.setCodAmount(BigDecimal.ZERO);
        }
        //单数据处理直接抛异常
        this.checkRefNo(dto, dto.getId());

        // 先取消冻结，再冻结
        // 取消冻结
        String orderNo = delOutbound.getOrderNo();
        try {
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
            if (!DelOutboundOrderTypeEnum.MULTIPLE_PIECES.getCode().equals(delOutbound.getOrderType()) && !DelOutboundOrderTypeEnum.BULK_ORDER.getCode().equals(delOutbound.getOrderType())) {
                AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(delOutbound.getOrderNo()).businessItemNo(null).fileList(dto.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT).build();
                this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
            }

            if(DelOutboundOrderTypeEnum.BULK_ORDER.getCode().equals(delOutbound.getOrderType())){
                //保存箱标数据
                AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(dto.getBatchLabels()).attachmentTypeEnum(AttachmentTypeEnum.BULK_ORDER_BOX).build();
                this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
                //保存明细附件数据
                for (DelOutboundDetailDto detail : dto.getDetails()) {
                    if (detail.getSkuFile() != null && detail.getSkuFile().size() > 0) {
                        AttachmentDataDTO attachmentDataDTO = detail.getSkuFile().get(0);
                        // 箱标明细
                        AttachmentDTO boxMarkDetailFiels = AttachmentDTO.builder().businessNo(orderNo).businessItemNo("" + detail.getId()).fileList(
                                        Arrays.asList(new AttachmentDataDTO().setAttachmentUrl(attachmentDataDTO.getAttachmentUrl())
                                                .setAttachmentName(attachmentDataDTO.getAttachmentName()))).
                                attachmentTypeEnum(AttachmentTypeEnum.BULK_ORDER_DETAIL).build();
                        this.remoteAttachmentService.saveAndUpdate(boxMarkDetailFiels);

                    }
                }
            }

            // 计算包裹大小
            this.countPackageSize(inputDelOutbound, dto);
            // 批量出库保存装箱信息
            if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.NORMAL.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
                // 装箱信息
                List<DelOutboundPackingDto> packings = dto.getPackings();
                this.delOutboundPackingService.save(orderNo, packings, true);
                // 箱标文件
                AttachmentDTO batchLabel = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(dto.getBatchLabels()).attachmentTypeEnum(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL).build();
                this.remoteAttachmentService.saveAndUpdate(batchLabel);
            }
            if (DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(delOutbound.getOrderType())) {
                // 组合信息
                this.delOutboundCombinationService.update(orderNo, dto.getCombinations());
            }

            if (DelOutboundOrderTypeEnum.MULTIPLE_PIECES.getCode().equals(delOutbound.getOrderType())) {
                //一票多件出库
                //箱标
                List<AttachmentDataDTO> batchLabels = new ArrayList();
                //发货单
                List<AttachmentDataDTO> documentsFiles = new ArrayList();
                if (dto.getDocumentsFiles() != null) {
                    for (AttachmentDataDTO vo : dto.getDocumentsFiles()) {
                        if (AttachmentTypeEnum.MULTIPLE_PIECES_BOX_MARK.getAttachmentType().equals(vo.getAttachmentType())) {
                            batchLabels.add(vo);
                        } else if (AttachmentTypeEnum.MULTIPLE_PIECES_INVOICE.getAttachmentType().equals(vo.getAttachmentType())) {
                            documentsFiles.add(vo);
                        }
                    }
                }
                // 箱标文件
                AttachmentDTO batchLabel = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(batchLabels)
                        .attachmentTypeEnum(AttachmentTypeEnum.MULTIPLE_PIECES_BOX_MARK).build();
                this.remoteAttachmentService.saveAndUpdate(batchLabel);
                // 发货单文件
                AttachmentDTO invoiceBatchLabel = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(documentsFiles)
                        .attachmentTypeEnum(AttachmentTypeEnum.MULTIPLE_PIECES_INVOICE).build();
                this.remoteAttachmentService.saveAndUpdate(invoiceBatchLabel);

                // 删除之前的文件
                this.remoteAttachmentService.deleteByBusinessNo(AttachmentDTO.builder().businessNo(orderNo).
                        attachmentTypeEnum(AttachmentTypeEnum.MULTIPLE_PIECES_BOX_DETAIL).build());
                this.remoteAttachmentService.deleteByBusinessNo(AttachmentDTO.builder().businessNo(orderNo).
                        attachmentTypeEnum(AttachmentTypeEnum.MULTIPLE_PIECES_SKU).build());


                for (DelOutboundDetailDto detail : dto.getDetails()) {
                    if (detail.getBoxMarkFile() != null && detail.getBoxMarkFile().size() > 0) {
                        AttachmentDataDTO attachmentDataDTO = detail.getBoxMarkFile().get(0);
                        attachmentDataDTO.setId(null);
                        // 箱标明细
                        AttachmentDTO boxMarkDetailFiels = AttachmentDTO.builder().businessNo(orderNo).businessItemNo("" + detail.getId()).fileList(
                                Arrays.asList(attachmentDataDTO)).attachmentTypeEnum(AttachmentTypeEnum.MULTIPLE_PIECES_BOX_DETAIL).build();
                        this.remoteAttachmentService.saveAndUpdate(boxMarkDetailFiels);

                    }
                    if (detail.getSkuFile() != null && detail.getSkuFile().size() > 0) {
                        AttachmentDataDTO attachmentDataDTO = detail.getSkuFile().get(0);
                        attachmentDataDTO.setId(null);
                        // SKU
                        AttachmentDTO skuFiles = AttachmentDTO.builder().businessNo(orderNo).businessItemNo("" + detail.getId()).fileList(
                                Arrays.asList(attachmentDataDTO)).attachmentTypeEnum(AttachmentTypeEnum.MULTIPLE_PIECES_SKU).build();
                        this.remoteAttachmentService.saveAndUpdate(skuFiles);
                    }

                }
            }

            // 更新
            int i = baseMapper.updateById(inputDelOutbound);
            DelOutboundOperationLogEnum.UPDATE.listener(delOutbound);
            return i;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 取消冻结
     *
     * @param orderType     orderType
     * @param orderNo       orderNo
     * @param warehouseCode warehouseCode
     * @param cusCode       cusCode
     */
    @Override
    public void unFreeze(String orderType, String orderNo, String warehouseCode, String cusCode) {
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
        if (DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(orderType)) {
            inventoryOperateListDto.setFreeType(1);
        }
        inventoryOperateListDto.setCusCode(cusCode);
        this.unFreeze(inventoryOperateListDto);
    }

    @Transactional
    @Override
    public void unFreeze(DelOutbound delOutbound) {
        String orderType = delOutbound.getOrderType();
        String orderNo = delOutbound.getOrderNo();
        String warehouseCode = delOutbound.getWarehouseCode();
        if (DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(orderType)) {
            InventoryOperateDto inventoryOperateDto = new InventoryOperateDto();
            inventoryOperateDto.setSku(delOutbound.getNewSku());
            inventoryOperateDto.setNum(Math.toIntExact(delOutbound.getBoxNumber()));
            List<InventoryOperateDto> operateList = new ArrayList<>(1);
            operateList.add(inventoryOperateDto);
            InventoryOperateListDto inventoryOperateListDto = new InventoryOperateListDto();
            inventoryOperateListDto.setInvoiceNo(orderNo);
            inventoryOperateListDto.setWarehouseCode(warehouseCode);
            inventoryOperateListDto.setOperateList(operateList);
            inventoryOperateListDto.setCusCode(delOutbound.getSellerCode());
            this.unFreeze(inventoryOperateListDto);
        } else {
            this.unFreeze(orderType, orderNo, warehouseCode, delOutbound.getSellerCode());
        }
    }

    @Transactional
    @Override
    public boolean toPrint(DelOutboundToPrintDto dto) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(DelOutbound::getIsPrint, true);
        if (dto.isBatch()) {
            updateWrapper.in(DelOutbound::getId, dto.getIds());
        } else {
            updateWrapper.eq(DelOutbound::getId, dto.getId());
        }
        return super.update(updateWrapper);
    }

    @Transactional
    @Override
    public List<Map<String, Object>> batchUpdateTrackingNo(List<DelOutboundBatchUpdateTrackingNoDto> list) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DelOutboundBatchUpdateTrackingNoDto updateTrackingNoDto = list.get(i);
            if (StringUtils.isEmpty(updateTrackingNoDto.getOrderNo())) {
                Map<String, Object> result = new HashMap<>();
                result.put("msg", "第 " + (i + 1) + " 行，出库单号不能为空。");
                resultList.add(result);
                continue;
            }
            if (StringUtils.isEmpty(updateTrackingNoDto.getTrackingNo())) {
                Map<String, Object> result = new HashMap<>();
                result.put("msg", "第 " + (i + 1) + " 行，挂号不能为空。");
                resultList.add(result);
                continue;
            }
            int u = super.baseMapper.updateTrackingNo(updateTrackingNoDto);
            if (u < 1) {
                Map<String, Object> result = new HashMap<>();
                result.put("msg", "第 " + (i + 1) + " 行，出库单号不存在。");
                resultList.add(result);
            }
        }
        /*
        int size = list.size();
        executeBatch(sqlSession -> {
            int i = 0;
            for (DelOutboundBatchUpdateTrackingNoDto dto : list) {
                sqlSession.update("updateTrackingNo", dto);
                if ((i % 100 == 0) || i == size) {
                    sqlSession.flushStatements();
                }
                i++;
            }
        });*/
        return resultList;
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
            throw new CommonException("400", "记录信息已被修改，请刷新后再试");
        }
        DelOutbound delOutbound = list.get(0);
        Map<String, DelOutbound> delOutboundMap = new HashMap<>();
        for (DelOutbound delOutbound1 : list) {
            // 只能删除待提审，提审失败的单据
            if (!(DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound1.getState())
                    || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound1.getState()))) {
                throw new CommonException("400", "只能删除待提审，提审失败的单据");
            }
            if (!delOutbound.getWarehouseCode().equals(delOutbound1.getWarehouseCode())) {
                throw new CommonException("400", "只能批量删除同一仓库下的出库单");
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
                    this.unFreeze(delOutbound1);
                }
                // 判断要不要取消冻结操作费用
                if (BringVerifyEnum.gt(BringVerifyEnum.FREEZE_OPERATION, BringVerifyEnum.get(bringVerifyState))) {
                    // 取消冻结操作费用
                    this.unfreezeOperation(orderNo, delOutbound1.getOrderType());
                }
            }
            DelOutboundOperationLogEnum.DELETE.listener(delOutbound1);
        }
        // 删除明细
        LambdaQueryWrapper<DelOutboundDetail> detailLambdaQueryWrapper = Wrappers.lambdaQuery();
        detailLambdaQueryWrapper.in(DelOutboundDetail::getOrderNo, orderNos);
        this.delOutboundDetailService.remove(detailLambdaQueryWrapper);
        int i = baseMapper.deleteBatchIds(ids);
        if (i != ids.size()) {
            throw new CommonException("400", "操作记录异常，请刷新后再试");
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
            throw new CommonException("400", "单据不存在");
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
            throw new CommonException("400", "出库单集合不能为空");
        }
        // 取消的单独处理
        if (DelOutboundOperationTypeEnum.CANCELED.getCode().equals(dto.getOperationType())) {
            LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
            if (StringUtils.isNotEmpty(dto.getWarehouseCode())) {
                updateWrapper.eq(DelOutbound::getWarehouseCode, dto.getWarehouseCode());
            }
            updateWrapper.in(DelOutbound::getOrderNo, orderNos);
            updateWrapper.set(DelOutbound::getOperationType, dto.getOperationType());
            updateWrapper.set(DelOutbound::getOperationTime, dto.getOperationTime());
            updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.WHSE_CANCELLED.getCode());
            // 批量修改取消状态
            this.baseMapper.update(null, updateWrapper);
            // 增加出库单已取消记录，异步处理，定时任务
            this.delOutboundCompletedService.add(orderNos, DelOutboundOperationTypeEnum.CANCELED.getCode());
        } else if (DelOutboundOperationTypeEnum.PROCESSING.getCode().equals(dto.getOperationType())) {
            // 异步处理，定时任务
            this.delOutboundCompletedService.add(orderNos, DelOutboundOperationTypeEnum.PROCESSING.getCode());
        } else if (DelOutboundOperationTypeEnum.SHIPPED.getCode().equals(dto.getOperationType())) {
            LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
            if (StringUtils.isNotEmpty(dto.getWarehouseCode())) {
                updateWrapper.eq(DelOutbound::getWarehouseCode, dto.getWarehouseCode());
            }
            updateWrapper.in(DelOutbound::getOrderNo, orderNos);
            updateWrapper.set(DelOutbound::getOperationType, dto.getOperationType());
            updateWrapper.set(DelOutbound::getOperationTime, dto.getOperationTime());
            updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.WHSE_COMPLETED.getCode());
            this.baseMapper.update(null, updateWrapper);
            // 增加出库单已完成记录，异步处理，定时任务
            this.delOutboundCompletedService.add(orderNos, DelOutboundOperationTypeEnum.SHIPPED.getCode());
        }
        return orderNos.size();
    }

    @Transactional
    @Override
    public int shipmentPackingMaterial(ShipmentPackingMaterialRequestDto dto) {
        return this.shipmentPackingMaterial(dto, DelOutboundStateEnum.PROCESSING);
    }

    private int shipmentPackingMaterial(ShipmentPackingMaterialRequestDto dto, DelOutboundStateEnum stateEnum) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        if (StringUtils.isNotEmpty(dto.getWarehouseCode())) {
            updateWrapper.eq(DelOutbound::getWarehouseCode, dto.getWarehouseCode());
        }
        updateWrapper.eq(DelOutbound::getOrderNo, dto.getOrderNo());
        if (null != stateEnum) {
            updateWrapper.set(DelOutbound::getState, stateEnum.getCode());
        }
        updateWrapper.set(DelOutbound::getPackingMaterial, dto.getPackingMaterial());
        int i = this.baseMapper.update(null, updateWrapper);
        return i;
    }

    @Transactional
    @Override
    public int shipmentPackingMaterialIgnoreState(ShipmentPackingMaterialRequestDto dto) {
        return this.shipmentPackingMaterial(dto, null);
    }

    @Transactional
    @Override
    public int shipmentPacking(ShipmentPackingMaterialRequestDto dto, String orderType) {
        LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
        if (StringUtils.isNotEmpty(dto.getWarehouseCode())) {
            updateWrapper.eq(DelOutbound::getWarehouseCode, dto.getWarehouseCode());
        }
        updateWrapper.eq(DelOutbound::getOrderNo, dto.getOrderNo());
        if (StringUtils.isNotEmpty(dto.getPackingMaterial())) {
            updateWrapper.set(DelOutbound::getPackingMaterial, dto.getPackingMaterial());
        }
        // 处理空值问题
        Double length = Utils.defaultValue(dto.getLength());
        Double width = Utils.defaultValue(dto.getWidth());
        Double height = Utils.defaultValue(dto.getHeight());
        Double weight = Utils.defaultValue(dto.getWeight());
        updateWrapper.set(DelOutbound::getLength, length);
        updateWrapper.set(DelOutbound::getWidth, width);
        updateWrapper.set(DelOutbound::getHeight, height);
        updateWrapper.set(DelOutbound::getWeight, weight);
        // 规格，长*宽*高
        updateWrapper.set(DelOutbound::getSpecifications, length + "*" + width + "*" + height);
        // 修改状态为处理中
        updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.PROCESSING.getCode());
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
        updateWrapper.set(DelOutbound::getContainerState, DelOutboundConstant.CONTAINER_STATE_1);
        updateWrapper.eq(DelOutbound::getOrderNo, dto.getOrderNo());
        return this.baseMapper.update(null, updateWrapper);
    }

    @Override
    public DelOutbound selectDelOutboundByOrderId(String orderId) {
        LambdaQueryWrapper<DelOutbound> query = Wrappers.lambdaQuery();
        query.eq(DelOutbound::getOrderNo, orderId);
        DelOutbound delOutbound = baseMapper.selectOne(query);
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("400", "单据不存在");
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
        List<String> orderNos1 = dto.getOrderNos();
        if (CollectionUtils.isEmpty(ids) && CollectionUtils.isEmpty(orderNos1)) {
            throw new CommonException("400", "操作异常，请求参数不合法");
        }
        List<DelOutbound> outboundList;
        int cancelSize;
        if (CollectionUtils.isNotEmpty(ids)) {
            // 根据ids查询单据
            outboundList = this.listByIds(ids);
            cancelSize = ids.size();
        } else {
            // 根据订单号查询单据
            LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.in(DelOutbound::getOrderNo, orderNos1);
            if (null != dto.getOrderType()) {
                queryWrapper.eq(DelOutbound::getOrderType, dto.getOrderType().getCode());
            }
            // 只能取消自己的单据
            if (null != dto.getSellerCode()) {
                queryWrapper.eq(DelOutbound::getSellerCode, dto.getSellerCode());
            }
            outboundList = this.list(queryWrapper);
            cancelSize = orderNos1.size();
        }
        if (CollectionUtils.isEmpty(outboundList)) {
            throw new CommonException("400", "操作失败，订单不存在");
        }
        if (cancelSize != outboundList.size()) {
            throw new CommonException("400", "操作失败，部分订单不存在");
        }
        List<String> orderNos = new ArrayList<>();
        String warehouseCode = outboundList.get(0).getWarehouseCode();
        List<String> reviewedList = new ArrayList<>();
        Map<String, DelOutbound> delOutboundMap = new HashMap<>();
        for (DelOutbound outbound : outboundList) {
            if (!warehouseCode.equals(outbound.getWarehouseCode())) {
                throw new CommonException("400", "只能同一个仓库下的出库单");
            }
            String orderNo = outbound.getOrderNo();
            delOutboundMap.put(orderNo, outbound);
            // 处理已完成，已取消的
            if (DelOutboundStateEnum.COMPLETED.getCode().equals(outbound.getState())) {
                // 已完成，已取消的单据不做处理
                // continue;
                throw new CommonException("400", "操作失败，已完成的订单不能取消");
            }
            if (DelOutboundStateEnum.CANCELLED.getCode().equals(outbound.getState())) {
                throw new CommonException("400", "操作失败，已取消的订单不能取消");
            }
            // 处理未提审，提审失败的
            if (DelOutboundStateEnum.REVIEWED.getCode().equals(outbound.getState())
                    || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(outbound.getState())) {
                // 未提审的，提审失败的
                reviewedList.add(orderNo);
                continue;
            }
            // 通知WMS处理的
            orderNos.add(orderNo);
        }
        // 判断有没有处理未提审，提审失败的
        if (CollectionUtils.isNotEmpty(reviewedList)) {
            // 修改状态未已取消
            LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.CANCELLED.getCode());
            // 把异常信息也清空
            updateWrapper.set(DelOutbound::getExceptionState, DelOutboundExceptionStateEnum.NORMAL.getCode());
            updateWrapper.set(DelOutbound::getExceptionMessage, "");
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
                        this.unFreeze(delOutbound);
                    }
                    // 判断要不要取消冻结操作费用
                    if (BringVerifyEnum.gt(BringVerifyEnum.FREEZE_OPERATION, BringVerifyEnum.get(bringVerifyState))) {
                        // 取消冻结操作费用
                        this.unfreezeOperation(orderNo, delOutbound.getOrderType());
                    }
                }
                DelOutboundOperationLogEnum.CANCEL.listener(delOutbound);
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
            throw new CommonException("400", "取消出库单失败");
        }
        if (!responseVO.getSuccess()) {
            throw new CommonException("400", Utils.defaultValue(responseVO.getMessage(), "取消出库单失败2"));
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
            try {
                DelOutbound delOutbound = this.getById(id);
                DelOutboundOperationLogEnum.HANDLER.listener(delOutbound);
                if (DelOutboundStateEnum.WHSE_COMPLETED.getCode().equals(delOutbound.getState())) {
                    // 仓库发货，调用完成的接口
                    this.delOutboundAsyncService.completed(delOutbound.getOrderNo());
                    result++;
                } else if (DelOutboundStateEnum.WHSE_CANCELLED.getCode().equals(delOutbound.getState())) {
                    // 仓库取消，调用取消的接口
                    this.delOutboundAsyncService.cancelled(delOutbound.getOrderNo());
                    result++;
                } else {
                    result = result + this.delOutboundAsyncService.shipmentPacking(id);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    @Override
    public int furtherHandler(DelOutboundFurtherHandlerDto dto) {
        DelOutbound delOutbound = this.getByOrderNo(dto.getOrderNo());
        if (null == delOutbound) {
            throw new CommonException("400", "单据不存在");
        }
        DelOutboundOperationLogEnum.FURTHER_HANDLER.listener(delOutbound);
        int result;
        if (DelOutboundStateEnum.WHSE_COMPLETED.getCode().equals(delOutbound.getState())) {
            // 仓库发货，调用完成的接口
            this.delOutboundAsyncService.completed(delOutbound.getOrderNo());
            result = 1;
        } else if (DelOutboundStateEnum.WHSE_CANCELLED.getCode().equals(delOutbound.getState())) {
            // 仓库取消，调用取消的接口
            this.delOutboundAsyncService.cancelled(delOutbound.getOrderNo());
            result = 1;
        } else {
            result = this.delOutboundAsyncService.shipmentPacking(delOutbound.getId(), dto.isShipmentShipping());
        }
        return result;
    }

    @Override
    public void label(HttpServletResponse response, DelOutboundLabelDto dto) {
        DelOutbound delOutbound = this.getById(dto.getId());
        if (null == delOutbound) {
            throw new CommonException("400", "单据不存在");
        }
        if("1".equals(dto.getType())){

            if(StringUtils.isEmpty(delOutbound.getShipmentRetryLabel())){
                throw new CommonException("400", "标签文件不存在");

            }
            File labelFile = new File(delOutbound.getShipmentRetryLabel());
            if (!labelFile.exists()) {
                throw new CommonException("400", "标签文件不存在");
            }
            ServletOutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                outputStream = response.getOutputStream();
                //response为HttpServletResponse对象
                response.setContentType("application/pdf;charset=utf-8");
                //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
                response.setHeader("Content-Disposition", "attachment;filename=" + delOutbound.getOrderNo() + ".pdf");
                inputStream = new FileInputStream(labelFile);
                IOUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new CommonException("500", "读取标签文件失败");
            } finally {
                IoUtil.flush(outputStream);
                IoUtil.close(outputStream);
                IoUtil.close(inputStream);
            }
        }else{
            if (StringUtils.isEmpty(delOutbound.getShipmentOrderNumber())) {
                throw new CommonException("400", "未获取承运商标签");
            }
            String pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber() + ".pdf";
            File labelFile = new File(pathname);
            if (!labelFile.exists()) {
                throw new CommonException("400", "标签文件不存在");
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
                throw new CommonException("500", "读取标签文件失败");
            } finally {
                IoUtil.flush(outputStream);
                IoUtil.close(outputStream);
                IoUtil.close(inputStream);
            }
        }

    }

    @Override
    public List<DelOutboundLabelResponse> labelBase64(DelOutboundLabelDto dto) {
        List<String> orderNos = dto.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            return null;
        }
        LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(DelOutbound::getOrderNo, orderNos);
        List<DelOutbound> outboundList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(outboundList)) {
            return null;
        }
        Map<String, DelOutbound> outboundMap = outboundList.stream().collect(Collectors.toMap(DelOutbound::getOrderNo, v -> v, (a, b) -> a));
        List<DelOutboundLabelResponse> responseList = new ArrayList<>();
        for (String orderNo : orderNos) {
            DelOutboundLabelResponse response = new DelOutboundLabelResponse();
            response.setOrderNo(orderNo);
            DelOutbound outbound = outboundMap.get(orderNo);
            if (null == outbound) {
                response.setStatus(false);
                response.setMessage("单据不存在");
                responseList.add(response);
                continue;
            }
            String pathname = null;
            byte[] fb = null;
            if("1".equals(dto.getType())){
                pathname = outbound.getShipmentRetryLabel();
                File labelFile = new File(pathname);
                if (labelFile.exists()) {
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(labelFile);
                        fb = IOUtils.toByteArray(fileInputStream);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        response.setStatus(false);
                        response.setMessage("获取标签文件失败," + e.getMessage());
                        responseList.add(response);
                        continue;
                    } finally {
                        IOUtils.closeQuietly(fileInputStream);
                    }
                }

            }else{

                if (!(DelOutboundStateEnum.DELIVERED.getCode().equals(outbound.getState())
                        || DelOutboundStateEnum.PROCESSING.getCode().equals(outbound.getState())
                        || DelOutboundStateEnum.COMPLETED.getCode().equals(outbound.getState())
                        || DelOutboundStateEnum.WHSE_PROCESSING.getCode().equals(outbound.getState())
                        || DelOutboundStateEnum.WHSE_COMPLETED.getCode().equals(outbound.getState())
                        || DelOutboundStateEnum.NOTIFY_WHSE_PROCESSING.getCode().equals(outbound.getState())
                )) {
                    throw new CommonException("400", "订单当前状态不允许获取");
                }

                pathname = DelOutboundServiceImplUtil.getPackageTransferLabelFilePath(outbound) + "/" + orderNo + ".pdf";
                File labelFile = new File(pathname);

                if (labelFile.exists()) {
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(labelFile);
                        fb = IOUtils.toByteArray(fileInputStream);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        response.setStatus(false);
                        response.setMessage("获取标签文件失败," + e.getMessage());
                        responseList.add(response);
                        continue;
                    } finally {
                        IOUtils.closeQuietly(fileInputStream);
                    }
                } else {
                    // 查询地址信息
                    DelOutboundAddress delOutboundAddress = this.delOutboundAddressService.getByOrderNo(orderNo);
                    try {
                        // 查询SKU信息
                        List<String> nos = new ArrayList<>();
                        nos.add(orderNo);
                        Map<String, String> skuLabelMap = this.delOutboundDetailService.queryDetailsLabelByNos(nos);
                        String skuLabel = skuLabelMap.get(orderNo);
                        ByteArrayOutputStream byteArrayOutputStream = DelOutboundServiceImplUtil.renderPackageTransfer(outbound, delOutboundAddress, skuLabel);
                        FileUtils.writeByteArrayToFile(labelFile, fb = byteArrayOutputStream.toByteArray(), false);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        response.setStatus(false);
                        response.setMessage("生成标签文件失败," + e.getMessage());
                        responseList.add(response);
                        continue;
                    }
                }
            }
            if(fb == null){
                continue;
            }
            String base64Str = Base64Utils.encodeToString(fb);
            response.setBase64(base64Str);
            response.setFileName(orderNo + ".pdf");
            response.setStatus(true);
            response.setId(outbound.getId());
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public int uploadBoxLabel(DelOutboundUploadBoxLabelDto dto) {
        // 删除之前的文件
        String orderNo = dto.getOrderNo();
        AttachmentTypeEnum attachmentTypeEnum = dto.getAttachmentTypeEnum();
        this.remoteAttachmentService.deleteByBusinessNo(AttachmentDTO.builder().businessNo(orderNo).attachmentTypeEnum(attachmentTypeEnum).build());
        // 箱标文件
        AttachmentDTO batchLabel = AttachmentDTO.builder().businessNo(orderNo).businessItemNo(null).fileList(dto.getBatchLabels()).attachmentTypeEnum(attachmentTypeEnum).build();
        this.remoteAttachmentService.saveAndUpdate(batchLabel);
        return 1;
    }

    @Deprecated
    @Override
    public List<DelOutboundDetailVO> importDetail(String warehouseCode, String sellerCode, List<DelOutboundDetailImportDto> dtoList) {
        // 查询sku
        List<String> skus = dtoList.stream().map(DelOutboundDetailImportDto::getSku).distinct().collect(Collectors.toList());
        InventoryAvailableQueryDto inventoryAvailableQueryDto = new InventoryAvailableQueryDto();
        inventoryAvailableQueryDto.setWarehouseCode(warehouseCode);
        inventoryAvailableQueryDto.setCusCode(sellerCode);
        inventoryAvailableQueryDto.setSkus(skus);
        // 这里是导入，导入的时候，可用库存必须大于0才查询出来
        inventoryAvailableQueryDto.setQueryType(1);
        List<InventoryAvailableListVO> availableList = this.inventoryFeignClientService.queryAvailableList2(inventoryAvailableQueryDto);
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
            queryChargeVO.setCountry(getCountryName(queryChargeVO.getCountry()));
            List<DelOutboundDetail> delOutboundDetails = delOutboundDetailService.selectDelOutboundDetailList(new DelOutboundDetail().setOrderNo(orderNo));
            //计算数量 = 多个SKU的数量+包材（1个）
            queryChargeVO.setQty(ListUtils.emptyIfNull(delOutboundDetails).stream().map(value -> StringUtils.isBlank(value.getBindCode())
                    ? value.getQty() : value.getQty() + 1).reduce(Long::sum).orElse(0L));

            List<DelOutboundCharge> delOutboundCharges = delOutboundChargeService.listCharges(orderNo);

            this.setAmount(queryChargeVO, delOutboundCharges);
        }
        return list;
    }

    private String getCountryName(String country) {
        if (StringUtils.isEmpty(country)) return country;
        R<BasRegionSelectListVO> result = basRegionFeignService.queryByCountryCode(country);
        if (result.getCode() == 200 && result.getData() != null) {
            return result.getData().getName();
        }
        return "";
    }

    @Override
    public List<DelOutboundExportListDto> exportList(DelOutboundListQueryDto queryDto) {
        QueryWrapper<DelOutboundListQueryDto> queryWrapper = new QueryWrapper<>();
        // 界面上选择了记录进行导出
        if (CollectionUtils.isNotEmpty(queryDto.getSelectIds())) {
            queryWrapper.in("o.id", queryDto.getSelectIds());
            // 按照创建时间倒序
            queryWrapper.orderByDesc("o.create_time");
        } else {
            DelOutboundServiceImplUtil.handlerQueryWrapper(queryWrapper, queryDto);
        }
        return this.baseMapper.exportList(queryWrapper);
    }

    @Override
    public List<DelOutboundReassignExportListDto> reassignExportList(DelOutboundListQueryDto queryDto) {
        QueryWrapper<DelOutboundListQueryDto> queryWrapper = new QueryWrapper<>();
        // 界面上选择了记录进行导出
        if (CollectionUtils.isNotEmpty(queryDto.getSelectIds())) {
            queryWrapper.in("o.id", queryDto.getSelectIds());
            // 按照创建时间倒序
            queryWrapper.orderByDesc("o.create_time");
        } else {
            DelOutboundServiceImplUtil.handlerQueryWrapper(queryWrapper, queryDto);
        }
        return this.baseMapper.reassignExportList(queryWrapper);
    }

    /**
     * 取消冻结库存
     *
     * @param inventoryOperateListDto inventoryOperateListDto
     */
    private void unFreeze(InventoryOperateListDto inventoryOperateListDto) {
        // 取消冻结
        Integer deduction = this.inventoryFeignClientService.unFreeze(inventoryOperateListDto);
        if (null == deduction || deduction < 1) {
            throw new CommonException("500", "取消冻结库存失败");
        }
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

    @Override
    public int againTrackingNo(DelOutboundAgainTrackingNoDto dto) {
        // 1.更新地址信息，物流规则
        // 核重之后的操作
        // 2.调用出库发货流程
        // 3.增加操作日志
        // 4.异常状态变更[call]
        Long id = dto.getId();
        String orderNo = dto.getOrderNo();
        if (null == id && StringUtils.isEmpty(orderNo)) {
            throw new CommonException("400", "ID或单号不能为空");
        }
        DelOutbound delOutbound;
        if (null != id) {
            delOutbound = super.getById(id);
        } else {
            delOutbound = this.getByOrderNo(orderNo);
        }
        if (null == delOutbound) {
            throw new CommonException("400", "单据不存在");
        }
        // 重新获取挂号场景：
        // 报异常，核重后的异常。
        // 在提审的时候异常，会审核失败他们自己会修改。只有核重后的，不能修改表单。
        boolean update = delOutboundExceptionService.againTrackingNo(delOutbound, dto);
        if (update) {
            DelOutboundFurtherHandlerDto furtherHandlerDto = new DelOutboundFurtherHandlerDto();
            furtherHandlerDto.setOrderNo(delOutbound.getOrderNo());
            this.furtherHandler(furtherHandlerDto);
        }
        return update ? 1 : 0;
    }

    @Override
    public List<DelOutboundListExceptionMessageVO> exceptionMessageList(List<String> orderNos) {
        if (CollectionUtils.isEmpty(orderNos)) {
            return Collections.emptyList();
        }
        return super.baseMapper.exceptionMessageList(orderNos);
    }

    @Override
    public List<DelOutboundListExceptionMessageExportVO> exceptionMessageExportList(List<String> orderNos) {
        if (CollectionUtils.isEmpty(orderNos)) {
            return Collections.emptyList();
        }
        return super.baseMapper.exceptionMessageExportList(orderNos);
    }

    @Override
    public List<QueryFinishListVO> queryFinishList(QueryFinishListDTO queryFinishListDTO) {
        return baseMapper.queryFinishList(queryFinishListDTO);
    }

    @Override
    public DelOutboundAddResponse reassign(DelOutboundDto dto) {
        if (!DelOutboundOrderTypeEnum.has(dto.getOrderType())) {
            throw new CommonException("400", "订单类型不存在");
        }
        // 来源为新增
        dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_ADD);
        dto.setReassignType(DelOutboundConstant.REASSIGN_TYPE_Y);
        return this.createDelOutbound(dto);
    }

    @Override
    public void deleteFlag(DelOutbound delOutbound) {
        DelOutbound updateDelOutbound = new DelOutbound();
        updateDelOutbound.setId(delOutbound.getId());
        updateDelOutbound.setDelFlag("2");
        super.updateById(updateDelOutbound);
    }

    @Override
    public void importBoxLabel(List<DelOutboundBoxLabelDto> list, String sellerCode, String attachmentType) {
        List<String> businessNos = list.stream().map(vo -> vo.getBusinessNo()).collect(Collectors.toList());
        List<String> orders = list.stream().map(vo -> vo.getRemark()).collect(Collectors.toList());

        LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutbound::getSellerCode, sellerCode);
        queryWrapper.in(DelOutbound::getOrderNo, orders);
        List<DelOutbound> delOutboundList = this.list(queryWrapper);
        if(delOutboundList.size() != orders.size()){
            throw new CommonException("400", "存在无效的出库单数据");
        }


        BasAttachmentQueryDTO queryDTO = new BasAttachmentQueryDTO();
        queryDTO.setBusinessNoList(businessNos);
        queryDTO.setBusinessCode(attachmentType);
        List<BasAttachment> basAttachmentList = ListUtils.emptyIfNull(remoteAttachmentService.list(queryDTO).getData());
        basAttachmentList =basAttachmentList.stream().filter(distinctByKey(person -> person.getBusinessNo())).collect(Collectors.toList());

        Map<String, BasAttachment> uuidNameMap = basAttachmentList.stream().collect(Collectors.toMap(BasAttachment::getBusinessNo, account -> account));

        List<String> oldOrders = new ArrayList<String>();
        for (DelOutboundBoxLabelDto dto: list){
            BasAttachment data = uuidNameMap.get(dto.getBusinessNo());
            if(data == null){
                throw new CommonException("400", "未找到上传的项标");
            }
            if(!orders.contains(data.getRemark())){
                oldOrders.add(data.getRemark());
            }
            data.setRemark(dto.getRemark());

        }
        R r = remoteAttachmentService.update(basAttachmentList);
        if(r != null && r.getCode() == 200){
            if(oldOrders.size() > 0){
                LambdaUpdateWrapper<DelOutbound> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(DelOutbound::getUploadBoxLabel, "N");
                lambdaUpdateWrapper.in(DelOutbound::getOrderNo, oldOrders);
                this.update(lambdaUpdateWrapper);
            }


            LambdaUpdateWrapper<DelOutbound> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(DelOutbound::getUploadBoxLabel, "Y");
            lambdaUpdateWrapper.in(DelOutbound::getOrderNo, orders);
            this.update(lambdaUpdateWrapper);
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T,?> keyExtractor){
        Map<Object,Boolean> map=new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t),Boolean.TRUE)==null;
    }

    @Transactional
    @Override
    public int updateReassignImportedData(List<DelOutboundReassignExportListVO> list) {
        List<LambdaUpdateWrapper<DelOutbound>> delOutboundList = new ArrayList<>();
        List<LambdaUpdateWrapper<DelOutboundAddress>> delOutboundAddressList = new ArrayList<>();
        for (DelOutboundReassignExportListVO vo : list) {
            // 修改出库单上的信息
            LambdaUpdateWrapper<DelOutbound> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
            lambdaUpdateWrapper.set(DelOutbound::getCodAmount, vo.getCodAmount());
            lambdaUpdateWrapper.set(DelOutbound::getShipmentRule, vo.getShipmentRule());
            lambdaUpdateWrapper.set(DelOutbound::getIoss, vo.getIoss());
            lambdaUpdateWrapper.eq(DelOutbound::getOrderNo, vo.getOrderNo());
            delOutboundList.add(lambdaUpdateWrapper);
            // 修改地址信息
            LambdaUpdateWrapper<DelOutboundAddress> addressLambdaUpdateWrapper = Wrappers.lambdaUpdate();
            addressLambdaUpdateWrapper.set(DelOutboundAddress::getCountry, vo.getCountry());
            addressLambdaUpdateWrapper.set(DelOutboundAddress::getCountryCode, vo.getCountryCode());
            addressLambdaUpdateWrapper.set(DelOutboundAddress::getStateOrProvince, vo.getStateOrProvince());
            addressLambdaUpdateWrapper.set(DelOutboundAddress::getCity, vo.getCity());
            addressLambdaUpdateWrapper.set(DelOutboundAddress::getStreet1, vo.getStreet1());
            addressLambdaUpdateWrapper.set(DelOutboundAddress::getStreet2, vo.getStreet1());
            addressLambdaUpdateWrapper.set(DelOutboundAddress::getPostCode, vo.getPostCode());
            addressLambdaUpdateWrapper.set(DelOutboundAddress::getPhoneNo, vo.getPhoneNo());
            addressLambdaUpdateWrapper.set(DelOutboundAddress::getEmail, vo.getEmail());
            addressLambdaUpdateWrapper.eq(DelOutboundAddress::getOrderNo, vo.getOrderNo());
            delOutboundAddressList.add(addressLambdaUpdateWrapper);
        }
        String sqlStatement = sqlStatement(SqlMethod.UPDATE);
        AtomicInteger results = new AtomicInteger(0);
        int size = delOutboundList.size();
        executeBatch(sqlSession -> {
            int i = 1;
            for (LambdaUpdateWrapper<DelOutbound> wrapper : delOutboundList) {
                MapperMethod.ParamMap<LambdaUpdateWrapper<DelOutbound>> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, null);
                param.put(Constants.WRAPPER, wrapper);
                sqlSession.update(sqlStatement, param);
                if ((i % 100 == 0) || i == size) {
                    List<BatchResult> batchResults = sqlSession.flushStatements();
                    if (CollectionUtils.isNotEmpty(batchResults)) {
                        for (BatchResult batchResult : batchResults) {
                            int[] updateCounts = batchResult.getUpdateCounts();
                            results.getAndSet(IntStream.of(updateCounts).sum());
                        }
                    }
                }
                i++;
            }
        });
        this.delOutboundAddressService.updateReassignImportedData(delOutboundAddressList);
        return results.get();
    }

    @Override
    public int receiveLabel(DelOutboundReceiveLabelDto dto) {

        LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(dto.getOrderNo())){
            queryWrapper.eq(DelOutbound::getOrderNo, dto.getOrderNo());

        }else if(StringUtils.isNotEmpty(dto.getOrderNo())){
            queryWrapper.eq(DelOutbound::getRefNo, dto.getRefNo());

        }else if(StringUtils.isNotEmpty(dto.getOrderNo())){
            queryWrapper.eq(DelOutbound::getTrackingNo, dto.getTrackingNo());
        }else{
            throw new CommonException("400", "唯一标识必须有值");
        }
        DelOutbound data = this.getOne(queryWrapper);
        if(data == null){
            throw new CommonException("400", "出库单未匹配");
        }
        if(StringUtils.isNotEmpty(dto.getTraceId())){
            data.setTraceId(dto.getTraceId());
        }
        if(StringUtils.isNotEmpty(dto.getRemark())){
            data.setRemark(dto.getRemark());

        }

        data.setShipmentOrderLabelUrl(delOutboundBringVerifyService.saveShipmentLabel(dto.getFileStream(), data));
        this.updateById(data);
        return 1;
    }

    @Override
    public int boxStatus(DelOutboundBoxStatusDto dto) {
        LambdaQueryWrapper<DelOutboundDetail> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutboundDetail::getOrderNo, dto.getOrderNo());
        queryWrapper.eq(DelOutboundDetail::getBoxMark, dto.getBoxNo());
        List<DelOutboundDetail> dataDelOutboundDetailList = delOutboundDetailService.list(queryWrapper);
        if(dataDelOutboundDetailList.size() == 0){
            throw new CommonException("400", "没有匹配的箱号数据");
        }
        for (DelOutboundDetail detail: dataDelOutboundDetailList){
            detail.setOperationType(dto.getOperationType());
        }
        delOutboundDetailService.updateBatchById(dataDelOutboundDetailList);
        return 1;
    }
}

