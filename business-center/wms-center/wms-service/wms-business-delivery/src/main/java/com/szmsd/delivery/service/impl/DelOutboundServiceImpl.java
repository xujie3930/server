package com.szmsd.delivery.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.szmsd.bas.api.domain.BasEmployees;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.*;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.constant.SerialNumberConstant;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.dto.EmailDto;
import com.szmsd.bas.dto.EmailObjectDto;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.chargerules.api.feign.ChargeFeignService;
import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.chargerules.domain.BasProductService;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.config.AsyncThreadObject;
import com.szmsd.delivery.domain.*;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.enums.*;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.mapper.*;
import com.szmsd.delivery.service.*;
import com.szmsd.delivery.service.wrapper.*;
import com.szmsd.delivery.util.PackageInfo;
import com.szmsd.delivery.util.PackageUtil;
import com.szmsd.delivery.util.Utils;
import com.szmsd.delivery.vo.*;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import com.szmsd.http.api.feign.HtpOutboundFeignService;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.api.service.IHtpRmiClientService;
import com.szmsd.http.dto.DirectExpressOrderApiDTO;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.ShipmentCancelRequestDto;
import com.szmsd.http.dto.ShipmentOrderResult;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.vo.HttpResponseVO;
import com.szmsd.http.vo.PricedProductInfo;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.domain.dto.QueryFinishListDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.QueryFinishListVO;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.BatchResult;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
@Slf4j
public class DelOutboundServiceImpl extends ServiceImpl<DelOutboundMapper, DelOutbound> implements IDelOutboundService {
    private Logger logger = LoggerFactory.getLogger(DelOutboundServiceImpl.class);

    @Value("${spring.application.name}")
    private String applicationName;

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

    @Autowired
    private IHtpRmiClientService htpRmiClientService;

    @Autowired
    private DelOutboundTarckOnMapper delOutboundTarckOnMapper;

    @Autowired
    private HtpOutboundFeignService htpOutboundFeignService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;

    @Autowired
    private BasFileMapper basFileMapper;

    @Autowired
    private DelOutboundTarckErrorMapper delOutboundTarckErrorMapper;

    @Autowired
    private BasFeignService basFeignService;
    @Autowired
    private EmailFeingService emailFeingService;

    @Autowired
    private HtpPricedProductFeignService htpPricedProductFeignService;
    @Resource
    private ChargeFeignService chargeFeignService;
    @Resource
    private BasSellerFeignService basSellerFeignService;

    @Autowired
    private BasShipmenRulesService basShipmenRulesService;

    @Autowired
    private BasTrackingPushMapper basTrackingPushMapper;

    @Autowired
    private RedissonClient redissonClient;

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
    public DelOutboundVO selectDelOutboundByOrderNous(String orderNo,int operationType) {
        LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
        if (operationType==1||operationType==3){
            queryWrapper.isNotNull(DelOutbound::getShipmentsTime);
            queryWrapper.isNotNull(DelOutbound::getTrackingTime);
        }

//        queryWrapper.eq(DelOutbound::getOrderNo, orderNo).or().eq(DelOutbound::getRefNo,orderNo).or().eq(DelOutbound::getTrackingNo,orderNo);
//        queryWrapper.and((wrapper)->{
//
//
//
//        });

        queryWrapper.and(wrapper ->
                wrapper.eq(DelOutbound::getOrderNo, orderNo).or().eq(DelOutbound::getRefNo,orderNo).or().eq(DelOutbound::getTrackingNo,orderNo)
        );


        queryWrapper.last("LIMIT 1");
        DelOutbound delOutbound = super.getOne(queryWrapper);
        if (operationType==3&&delOutbound==null){
            return null;
        }
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

        String amazonLogisticsRouteId = delOutboundThirdPartyVO.getAmazonLogisticsRouteId();

        if(StringUtils.isNotEmpty(amazonLogisticsRouteId)){
            delOutboundThirdPartyVO.setTrackingNo(amazonLogisticsRouteId);
        }

        return delOutboundThirdPartyVO;
    }


    private DelOutboundVO selectDelOutboundVO(DelOutbound delOutbound) {
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("400", "单据不存在");
        }
        //判断时间不为空的情况

        DelOutboundVO delOutboundVO = BeanMapperUtil.map(delOutbound, DelOutboundVO.class);

        if (Optional.ofNullable(delOutbound.getShipmentsTime()).isPresent()&&Optional.ofNullable(delOutbound.getTrackingTime()).isPresent()){
            Date a=new Date(System.currentTimeMillis());
            long s=(a.getTime()-delOutbound.getShipmentsTime().getTime());
            TimeUnit time=TimeUnit.DAYS;
            long day1=time.convert(s,TimeUnit.MILLISECONDS);

            long q=(a.getTime()-delOutbound.getTrackingTime().getTime());

            long day2=time.convert(q,TimeUnit.MILLISECONDS);
            delOutboundVO.setDelDays(day1);
            delOutboundVO.setTrackingDays(day2);

            //根据物流服务查询 配置列表的两个天数
            if (Optional.ofNullable(delOutbound.getShipmentRule()).isPresent()){

                Map  mapSettings=baseMapper.selectQuerySettings(delOutbound.getShipmentRule());
                logger.info("查件配置：{}",mapSettings);
                logger.info("查件配置单号：{}",delOutbound.getOrderNo());
                if (mapSettings!=null) {
                    //配置表的发货天数
                    Long queryseShipmentDays = Long.valueOf(mapSettings.get("shipmentDays").toString());
                    //配置表的轨迹天数
                    Long querysetrackStayDays = Long.valueOf(mapSettings.get("trackStayDays").toString());
                    delOutboundVO.setQueryseShipmentDays(queryseShipmentDays);
                    delOutboundVO.setQuerysetrackStayDays(querysetrackStayDays);
                    if ( queryseShipmentDays>delOutboundVO.getDelDays() && querysetrackStayDays>delOutboundVO.getTrackingDays() ) {
                        delOutboundVO.setCheckFlag(0L);
                    } else {
                        delOutboundVO.setCheckFlag(1L);
                    }
                }else {
                    delOutboundVO.setCheckFlag(1L);
                }

            }



        }
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

            if(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())){
                //转运出库单，详情界面按客户录入的显示


            }else if(DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(delOutbound.getOrderType())){
                //集运按sku带出来
                BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
                conditionQueryDto.setSkus(skus);
                conditionQueryDto.setSellerCode(delOutbound.getSellerCode());
                List<BaseProduct> baseProductList = this.baseProductClientService.queryProductList(conditionQueryDto);
                if (CollectionUtils.isNotEmpty(baseProductList)) {
                    Map<String, BaseProduct>  baseProductMap = baseProductList.stream().collect(Collectors.toMap(BaseProduct::getCode, v -> v, (a, b) -> a));
                    for (DelOutboundDetailVO vo : detailDtos) {
                        BaseProduct available = baseProductMap.get(vo.getSku());
                        if (null != available) {
                            BeanMapperUtil.map(available, vo);
                        }
                    }
                }
            }else {
                InventoryAvailableQueryDto inventoryAvailableQueryDto = new InventoryAvailableQueryDto();
                inventoryAvailableQueryDto.setWarehouseCode(delOutbound.getWarehouseCode());
                inventoryAvailableQueryDto.setCusCode(delOutbound.getSellerCode());
                inventoryAvailableQueryDto.setSkus(skus);
                // 可用库存为0的也需要查询出来
                inventoryAvailableQueryDto.setQueryType(2);
               /* // 集运出库的
                if (DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(delOutbound.getOrderType())) {
                    inventoryAvailableQueryDto.setSource("084002");
                }*/
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
            String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
            if (StringUtils.isEmpty(queryDto.getCustomCode())) {
                queryDto.setCustomCode(cusCode);
            }
        }
        DelOutboundServiceImplUtil.handlerQueryWrapper(queryWrapper, queryDto);

        List<DelOutboundListVO> delOutboundListVOS = baseMapper.pageList(queryWrapper);

        for(DelOutboundListVO outboundVO : delOutboundListVOS){

            BigDecimal amount = outboundVO.getAmount();

            if(amount != null) {
                BigDecimal changeAmount = amount.setScale(2,BigDecimal.ROUND_UP);
                outboundVO.setAmount(changeAmount);
            }

        }

        return delOutboundListVOS;
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
        if(StringUtils.isEmpty(dto.getSourceType())){
            // 来源为新增
            dto.setSourceType(DelOutboundConstant.SOURCE_TYPE_ADD);
        }
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


            if (org.apache.commons.lang3.StringUtils.isNotEmpty(dto.getWarehouseCode())) {
                String warehouseCode = dto.getWarehouseCode();

                Long s = System.currentTimeMillis();

                BasWarehouse warehouse = this.basWarehouseClientService.queryByWarehouseCode(warehouseCode);

                Long e = System.currentTimeMillis();

                logger.info("basWarehouseClientService.queryByWarehouseCode,{}",e-s);

                if (null == warehouse) {
                    throw new CommonException("400", "仓库信息不存在");
                }else if(!"1".equals(warehouse.getStatus())){
                    throw new CommonException("400", "Warehouse not enabled");
                }
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

                Long s = System.currentTimeMillis();
                boolean f = !this.delOutboundDocService.inServiceValid(inServiceDto, shipmentRule);
                Long e = System.currentTimeMillis();

                logger.info("delOutboundDocService.inServiceValid,{}",e-s);
                if (f) {
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

            Long s = System.currentTimeMillis();
            List<BaseProduct> productList = this.baseProductClientService.queryProductList(productConditionQueryDto);
            Long e = System.currentTimeMillis();

            logger.info("baseProductClientService.queryProductList,{}",e-s);

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

                Long st = System.currentTimeMillis();
                List<InventoryAvailableListVO> availableList = this.inventoryFeignClientService.queryAvailableList2(inventoryAvailableQueryDto);
                Long en = System.currentTimeMillis();
                logger.info("inventoryFeignClientService.queryAvailableList2,{}",en-st);

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
                if (DelOutboundOrderTypeEnum.NORMAL.getCode().equals(dto.getOrderType())
                        || DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(dto.getOrderType())
                        || DelOutboundOrderTypeEnum.DESTROY.getCode().equals(dto.getOrderType())) {

                    //设置产品名称，英文名称，货物价值
                    for (DelOutboundDetailDto detail : details) {
                        BaseProduct product = productMap.get(detail.getSku());
                        if(StringUtils.isEmpty(detail.getProductName())){
                            detail.setProductName(product.getProductName());
                        }
                        if(StringUtils.isEmpty(detail.getProductName())){
                            detail.setProductNameChinese(product.getProductNameChinese());
                        }
                        if(detail.getDeclaredValue() != null){
                            detail.setDeclaredValue(product.getDeclaredValue());
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
        StopWatch stopWatch = new StopWatch();
        logger.info(">>>>>[创建出库单]1.0 开始创建出库单");
        TimeInterval timer = DateUtil.timer();
        DelOutboundAddResponse response = new DelOutboundAddResponse();
        String orderNo;
        if (StringUtils.equals(dto.getSourceType(), DelOutboundConstant.SOURCE_TYPE_ADD)) {
            //单数据处理直接抛异常
            logger.info(">>>>>[创建出库单]1.1 校验Refno");
            try {
                this.checkRefNo(dto, null);
                logger.info(">>>>>[创建出库单]1.2 校验Refno完成，{}", timer.intervalRestart());
            }catch (CommonException e){
                logger.info(">>>>>[创建出库单]1.2 校验Refno失败，{}", timer.intervalRestart());
                response.setStatus(false);
                response.setMessage(e.getMessage());
                return response;
            }
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

            if(StringUtils.isNotEmpty(dto.getShipmentRule())){
                //获取发货规则名称
                R<PricedProductInfo> info = htpPricedProductFeignService.info(dto.getShipmentRule());
                if(info.getCode() == 200 && info.getData() != null){
                    delOutbound.setShipmentRuleName(info.getData().getName());
                }
                //校验产品服务是否可下单
                R<List<BasProductService>> r= chargeFeignService.selectBasProductService(Arrays.asList(dto.getShipmentRule()));
                if(r.getCode() == 200 && r.getData()!= null && r.getData().size() > 0){
                    Boolean isInService = r.getData().get(0).getInService();
                    if(isInService != null && isInService == false){
                        throw new CommonException("400", "Logistics services are not available");
                    }
                }
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
            long shettTime = System.currentTimeMillis();

            delOutbound.setOrderNo(orderNo = (prefix + sellerCode + this.serialNumberClientService.generateNumber(SerialNumberConstant.DEL_OUTBOUND_NO)));
            logger.info(">>>>>[创建出库单{}]3.0创建出库单流水号时间，耗时{}", delOutbound.getOrderNo(), System.currentTimeMillis()-shettTime);


            // 默认状态
            delOutbound.setState(DelOutboundStateEnum.REVIEWED.getCode());
            // 默认异常状态
            delOutbound.setExceptionState(DelOutboundExceptionStateEnum.NORMAL.getCode());
//            logger.info(">>>>>[创建出库单{}]3.1 出库单对象赋值，生成流水号，{}", delOutbound.getOrderNo(), timer.intervalRestart());
            // 计算发货类型
            long shipmentTypeTime = System.currentTimeMillis();

            delOutbound.setShipmentType(this.buildShipmentType(dto));
            logger.info(">>>>>[创建出库单{}]3.1.5sku属性获取时间，耗时{}", delOutbound.getOrderNo(), System.currentTimeMillis()-shipmentTypeTime);

            logger.info(">>>>>[创建出库单{}]3.2 计算发货类型，{}", delOutbound.getOrderNo(), timer.intervalRestart());
            // 计算包裹大小
            this.countPackageSize(delOutbound, dto);
            logger.info(">>>>>[创建出库单{}]3.3 计算包裹大小，{}", delOutbound.getOrderNo(), timer.intervalRestart());

            //同步更新计泡拦截重量
            delOutbound.setForecastWeight(delOutbound.getWeight());
            
            // 保存出库单
            int insert = baseMapper.insert(delOutbound);
            logger.info(">>>>>[创建出库单{}]3.4 保存出库单，{}", delOutbound.getOrderNo(), timer.intervalRestart());
            if (insert == 0) {
                throw new CommonException("400", "保存出库单失败！");
            }
            DelOutboundOperationLogEnum.CREATE.listener(delOutbound);
            // 保存地址
            this.saveAddress(dto, delOutbound.getOrderNo());
            logger.info(">>>>>[创建出库单{}]3.5 保存地址信息，{}", delOutbound.getOrderNo(), timer.intervalRestart());

            if (DelOutboundOrderTypeEnum.MULTIPLE_PIECES.getCode().equals(delOutbound.getOrderType())) {
                //如果明细中商标为空，系统自动生成
                stopWatch.start();

                for (DelOutboundDetailDto detailDto : dto.getDetails()) {
                    if (StringUtils.isEmpty(detailDto.getBoxMark())) {
                        detailDto.setBoxMark(this.serialNumberClientService.generateNumber(SerialNumberConstant.BOX_MARK));
                    }
                }
                stopWatch.stop();
                logger.info(">>>>>[创建出库单{}]3.55一票多件，{}, 耗时{}", delOutbound.getOrderNo(), timer.intervalRestart(), stopWatch.getLastTaskInfo().getTimeMillis());

            }

            // 保存明细
            stopWatch.start();
            this.saveDetail(dto, delOutbound.getOrderNo());
            stopWatch.stop();
            logger.info(">>>>>[创建出库单{}]3.6 保存明细信息，{}, 耗时{}", delOutbound.getOrderNo(), timer.intervalRestart(), stopWatch.getLastTaskInfo().getTimeMillis());


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
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            // 返回异常错误信息
            response.setStatus(false);
            response.setMessage(e.getMessage());
            if(StringUtils.contains(e.getMessage(), "必须唯一值")){
                return response;
            }
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
            logger.info(">>>>>[创建出库单]3.4.5  计算包裹大小，转运特殊处理");

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
            logger.info(">>>>>[创建出库单]3.4.5  计算包裹大小，转运特殊处理else。。。。。");

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

        if(StringUtils.isNotEmpty(address.getCountryCode())){
            //创建/修改出库单，地址统一用英文
            R<BasRegionSelectListVO> countryR = this.basRegionFeignService.queryByCountryCode(address.getCountryCode());
            BasRegionSelectListVO country = R.getDataAndException(countryR);
            if (null == country) {
                throw new CommonException("400", "国家信息不存在");
            }
            address.setCountry(country.getEnName());
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

        if(StringUtils.isNotEmpty(dto.getShipmentRule())){
            //获取发货规则名称
            R<PricedProductInfo> info = htpPricedProductFeignService.info(dto.getShipmentRule());
            if(info.getCode() == 200 && info.getData() != null){
                delOutbound.setShipmentRuleName(info.getData().getName());
            }
            //校验产品服务是否可下单
            R<List<BasProductService>> r= chargeFeignService.selectBasProductService(Arrays.asList(dto.getShipmentRule()));
            if(r.getCode() == 200 && r.getData()!= null && r.getData().size() > 0){
                Boolean isInService = r.getData().get(0).getInService();
                if(isInService != null && isInService == false){
                    throw new CommonException("400", "Logistics services are not available");
                }
            }
        }

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
            //同步更新计泡拦截重量
            inputDelOutbound.setForecastWeight(inputDelOutbound.getWeight());

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
        Map map1=new HashMap();
        //成功之后的挂号
        List<DelOutboundBatchUpdateTrackingNoDto> list1=new ArrayList<>();
        int a=0;
        int b=0;
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
            //更新业务明细对应出库单的挂号
            baseMapper.updateFssAccountSerial(list.get(i));

            //导入挂号记录表
            DelOutbound delOutbound=baseMapper.selectTrackingNo(updateTrackingNoDto.getOrderNo());
            if (delOutbound!=null){
                a=a+1;
                DelOutboundTarckOn delOutboundTarckOn=new DelOutboundTarckOn();
                delOutboundTarckOn.setOrderNo(delOutbound.getOrderNo());
                delOutboundTarckOn.setTrackingNo(delOutbound.getTrackingNo());
                delOutboundTarckOn.setUpdateTime(new Date());
                delOutboundTarckOn.setTrackingNoNew(updateTrackingNoDto.getTrackingNo());
                delOutboundTarckOnMapper.insertSelective(delOutboundTarckOn);
                BasTrackingPush basTrackingPush=new BasTrackingPush();
                basTrackingPush.setTrackingNo(updateTrackingNoDto.getTrackingNo());
                basTrackingPush.setOrderNo(delOutbound.getOrderNo());
                basTrackingPush.setWarehouseCode(delOutbound.getWarehouseCode());
                basTrackingPushMapper.insertSelective(basTrackingPush);
                updateTrackingNoDto.setRefNo(delOutbound.getRefNo());
                list1.add(updateTrackingNoDto);
//                ShipmentTrackingChangeRequestDto
//                R<ResponseVO> r= htpOutboundFeignService.shipmentTracking(shipmentTrackingChangeRequestDto);
            }else if (delOutbound==null){
                b=b+1;
                DelOutboundTarckError delOutboundTarckError=new DelOutboundTarckError();
                BeanUtils.copyProperties(updateTrackingNoDto,delOutboundTarckError);
                delOutboundTarckError.setErrorReason("出库单号不存在");
                delOutboundTarckErrorMapper.insertSelective(delOutboundTarckError);
            }
            //成功的挂号
            map1.put("list1",list1);


//            if (u < 1) {
//                Map<String, Object> result = new HashMap<>();
//                result.put("msg", "第 " + (i + 1) + " 行，出库单号不存在。");
//                resultList.add(result);
//            }
        }
        Map map=new HashMap();
        map.put("successNumber",a);
        map.put("errorNumber",b);
        resultList.add(map);
        resultList.add(map1);
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
     * 发送邮箱
     * @param list list
     */
    @Override
    public void emailBatchUpdateTrackingNo(List<Map<String, Object>> list,String filepath) {
        //拿到成功的单号
        logger.info("发送邮箱进来-----");
        Map map=list.get(1);
        List<DelOutboundBatchUpdateTrackingNoDto> list1= (List<DelOutboundBatchUpdateTrackingNoDto>) map.get("list1");
        if (list1.size()>0) {
            //收集成功的单号去查询出库单的表数据
            List<String> orderNos = list1.stream().map(DelOutboundBatchUpdateTrackingNoDto::getOrderNo).collect(Collectors.toList());
            List<DelOutbound> delOutboundlist2 = baseMapper.selectorderNos(orderNos);
            logger.info("delOutboundlist2的查询结果:{}",delOutboundlist2);
            //更新成功的单号和出库单号做比较，拿到客户code
           for (DelOutboundBatchUpdateTrackingNoDto dto:list1){
               delOutboundlist2.stream().filter(x->x.getOrderNo().equals(dto.getOrderNo())).findFirst().ifPresent(i -> {
                   if (i.getCustomCode()!=null&&!i.getCustomCode().equals("")){
                       dto.setCustomCode(i.getCustomCode());
                   }
                   if (i.getTrackingNo()!=null&&!i.getTrackingNo().equals("")){
                       dto.setNoTrackingNo(i.getTrackingNo());
                   }


               });



               }

           //查询用户，客户关系表
            List<BasSeller> basSellerList= baseMapper.selectdelsellerCodes();
           List<DelOutboundBatchUpdateTrackingNoEmailDto> delOutboundBatchUpdateTrackingNoEmailDtoList=new ArrayList<>();

            for (DelOutboundBatchUpdateTrackingNoDto dto:list1) {

                basSellerList.stream().filter(x -> x.getSellerCode().equals(dto.getCustomCode())).findFirst().ifPresent(basSeller -> {

                    DelOutboundBatchUpdateTrackingNoEmailDto delOutboundBatchUpdateTrackingNoEmailDto = new DelOutboundBatchUpdateTrackingNoEmailDto();
                    BeanUtils.copyProperties(dto, delOutboundBatchUpdateTrackingNoEmailDto);
                    //屏蔽添加客户邮箱
                    //delOutboundBatchUpdateTrackingNoEmailDto.setSellerEmail(basSeller.getEmail());

                    if (basSeller.getServiceManagerName() != null && !basSeller.getServiceManagerName().equals("")) {
                        if (!basSeller.getServiceManagerName().equals(basSeller.getServiceStaffName())) {




//                            delOutboundBatchUpdateTrackingNoEmailDto.setEmpCode(basSeller.getServiceManagerName());
//                            delOutboundBatchUpdateTrackingNoEmailDto.setServiceManagerName(basSeller.getServiceManagerName());
                            if (basSeller.getServiceStaffName() != null && !basSeller.getServiceStaffName().equals("")) {
                                delOutboundBatchUpdateTrackingNoEmailDto.setServiceStaffName(basSeller.getServiceStaffName());
                            }

                        }
                    }
                    if (basSeller.getServiceStaffName() != null && !basSeller.getServiceStaffName().equals("")) {

//                        delOutboundBatchUpdateTrackingNoEmailDto.setEmpCode(basSeller.getServiceStaffName());
//                        delOutboundBatchUpdateTrackingNoEmailDto.setServiceStaffName(basSeller.getServiceStaffName());

                        if (basSeller.getServiceManagerName() != null && !basSeller.getServiceManagerName().equals("")) {
                            delOutboundBatchUpdateTrackingNoEmailDto.setServiceManagerName(basSeller.getServiceManagerName());
                        }
//                        delOutboundBatchUpdateTrackingNoEmailDtoList.add(delOutboundBatchUpdateTrackingNoEmailDto);

                    }



                    delOutboundBatchUpdateTrackingNoEmailDtoList.add(delOutboundBatchUpdateTrackingNoEmailDto);


                });


                boolean a = basSellerList.stream().filter(x -> x.getSellerCode().equals(dto.getCustomCode())).findFirst().isPresent();

                if (a == false) {
                        DelOutboundBatchUpdateTrackingNoEmailDto delOutboundBatchUpdateTrackingNoEmailDto = new DelOutboundBatchUpdateTrackingNoEmailDto();
                        delOutboundBatchUpdateTrackingNoEmailDto.setOrderNo(dto.getOrderNo());
                        delOutboundBatchUpdateTrackingNoEmailDto.setTrackingNo(dto.getTrackingNo());
                        int u = super.baseMapper.updateTrackingNo(delOutboundBatchUpdateTrackingNoEmailDto);
                    //manualTrackingYees(dto.getOrderNo());


                }
                if (delOutboundBatchUpdateTrackingNoEmailDtoList.size()==0){
                    DelOutboundBatchUpdateTrackingNoEmailDto delOutboundBatchUpdateTrackingNoEmailDto = new DelOutboundBatchUpdateTrackingNoEmailDto();
                    delOutboundBatchUpdateTrackingNoEmailDto.setOrderNo(dto.getOrderNo());
                    delOutboundBatchUpdateTrackingNoEmailDto.setTrackingNo(dto.getTrackingNo());
                    int u = super.baseMapper.updateTrackingNo(delOutboundBatchUpdateTrackingNoEmailDto);
                    //manualTrackingYees(dto.getOrderNo());
                }

            }

            BasEmployees basEmployees=new BasEmployees();
            //查询员工表
            R<List<BasEmployees>> basEmployeesR= basFeignService.empList(basEmployees);

            List<BasEmployees> basEmployeesList=basEmployeesR.getData();
            //找到员工中的邮箱
            for (DelOutboundBatchUpdateTrackingNoEmailDto dto:delOutboundBatchUpdateTrackingNoEmailDtoList){
                List<String> serviceManagerStaffName=new ArrayList<>();
                basEmployeesList.stream().filter(x->x.getEmpCode().equals(dto.getServiceManagerName())).findFirst().ifPresent(i -> {
                    if (i.getEmail()!=null&&!i.getEmail().equals("")){
                        serviceManagerStaffName.add(i.getEmail());
                    }
                    dto.setServiceManagerName(i.getEmpName());
                });
                basEmployeesList.stream().filter(x->x.getEmpCode().equals(dto.getServiceStaffName())).findFirst().ifPresent(i -> {
                    if (i.getEmail()!=null&&!i.getEmail().equals("")){
                        serviceManagerStaffName.add(i.getEmail());
                    }


                    dto.setServiceStaffName(i.getEmpName());

                });
                //屏蔽添加客户邮箱
                //serviceManagerStaffName.add(dto.getSellerEmail());
                //去除重复邮箱
                List<String> listWithoutDuplicates = serviceManagerStaffName.stream().distinct().collect(Collectors.toList());

                String email = StringUtils.join(listWithoutDuplicates,",");
                dto.setEmail(email);
               boolean a= basEmployeesList.stream().filter(x->x.getEmpCode().equals(dto.getEmpCode())).findFirst().isPresent();
                if (a==false){
                    DelOutboundBatchUpdateTrackingNoEmailDto delOutboundBatchUpdateTrackingNoEmailDto=new DelOutboundBatchUpdateTrackingNoEmailDto();
                    delOutboundBatchUpdateTrackingNoEmailDto.setOrderNo(dto.getOrderNo());
                    delOutboundBatchUpdateTrackingNoEmailDto.setTrackingNo(dto.getTrackingNo());
                    int u = super.baseMapper.updateTrackingNo(delOutboundBatchUpdateTrackingNoEmailDto);

                    //manualTrackingYees(dto.getOrderNo());
                }
                if (dto.getEmail()==null){
                    DelOutboundBatchUpdateTrackingNoEmailDto delOutboundBatchUpdateTrackingNoEmailDto=new DelOutboundBatchUpdateTrackingNoEmailDto();
                    delOutboundBatchUpdateTrackingNoEmailDto.setOrderNo(dto.getOrderNo());
                    delOutboundBatchUpdateTrackingNoEmailDto.setTrackingNo(dto.getTrackingNo());
                    int u = super.baseMapper.updateTrackingNo(delOutboundBatchUpdateTrackingNoEmailDto);

                    //manualTrackingYees(dto.getOrderNo());
                }
            }

            //将组合的数据 分解成Map<List> (邮箱为key,组合这个员工下的所有信息)
           Map<String,List<DelOutboundBatchUpdateTrackingNoEmailDto>> delOutboundBatchUpdateTrackingNoEmailDtoMap=delOutboundBatchUpdateTrackingNoEmailDtoList.stream().collect(Collectors.groupingBy(DelOutboundBatchUpdateTrackingNoEmailDto::getCustomCode));
            //循环map，得到每一组的数据 然后生成excel需要数据
            for (Map.Entry<String, List<DelOutboundBatchUpdateTrackingNoEmailDto>> entry : delOutboundBatchUpdateTrackingNoEmailDtoMap.entrySet()) {
                System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
                logger.info("组合参数：{}",entry.getValue());
                ExcleDelOutboundBatchUpdateTracking(entry.getValue(),entry.getKey(), filepath);
            }

        }

    }

    public void ExcleDelOutboundBatchUpdateTracking(List<DelOutboundBatchUpdateTrackingNoEmailDto> list,String customCode,String filepath){
        logger.info("更新挂号参数1：{}",list);
        EmailDto emailDto=new EmailDto();
        //功能模块
        emailDto.setModularType(0);
        //emailDto.setSubject("Notice on Update of Tracking Number-"+list.get(0).getCustomCode()+"-"+simpleDateFormat.format(new Date()));
        //邮箱接收人
        //emailDto.setTo(email);
        //emailDto.setText("customer:"+list.get(0).getCustomCode()+"on"+"["+simpleDateFormat.format(new Date())+"]"+"Total number of updated tracking numbers"+"["+list.size()+"]"+"Please download the attachment for details");
        List<EmailObjectDto> emailObjectDtoList= BeanMapperUtil.mapList(list, EmailObjectDto.class);
        emailDto.setList(emailObjectDtoList);
        if(customCode!=null&&!customCode.equals("")){
            R r= emailFeingService.sendEmail(emailDto);
            if (r.getCode()== HttpStatus.SUCCESS){

            }
        }
        logger.info("更新挂号参数2：{}",list);
        list.forEach(x->{
            int u = super.baseMapper.updateTrackingNo(x);

            //manualTrackingYees(x.getOrderNo());

        });

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
    public int shipmentPacking(ShipmentPackingMaterialRequestDto dto, String orderType,ShipmentEnum shipmentState) {
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

        if (shipmentState != null) {
            updateWrapper.set(DelOutbound::getShipmentState, shipmentState);
        }
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateByIdTransactional(DelOutbound delOutbound) {
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

            if("有部分单号不存在".equals(responseVO.getMessage())){
                this.delOutboundCompletedService.add(orderNos, DelOutboundOperationTypeEnum.CANCELED.getCode());
                // 修改单据状态为【仓库取消】
                LambdaUpdateWrapper<DelOutbound> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.set(DelOutbound::getState, DelOutboundStateEnum.WHSE_CANCELLED.getCode());
                updateWrapper.in(DelOutbound::getOrderNo, orderNos);
                return this.baseMapper.update(null, updateWrapper);
            }else{
                throw new CommonException("400", Utils.defaultValue(responseVO.getMessage(), "取消出库单失败2"));
            }

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
                    this.delOutboundAsyncService.completed(delOutbound.getOrderNo(), null);
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
            this.delOutboundAsyncService.completed(delOutbound.getOrderNo(), null);
            result = 1;
        } else if (DelOutboundStateEnum.WHSE_CANCELLED.getCode().equals(delOutbound.getState())) {
            // 仓库取消，调用取消的接口
            this.delOutboundAsyncService.cancelled(delOutbound.getOrderNo());
            result = 1;
        } else {
            dto.setExecShipmentShipping(true);
            result = this.delOutboundAsyncService.shipmentPacking(delOutbound.getId(), dto.isShipmentShipping(), dto);
        }
        return result;
    }

    @Override
    public R label(HttpServletResponse response, DelOutboundLabelDto dto) {
        DelOutbound delOutbound = this.getById(dto.getId());
        if (null == delOutbound) {
            R r = R.ok();
            r.setMsg("单据不存在");
            return r;
        }
        if("0".equals(dto.getType())){

            String pathname = DelOutboundServiceImplUtil.getPackageTransferLabelFilePath(delOutbound) + "/" + delOutbound.getOrderNo() + ".pdf";
            File labelFile = new File(pathname);
            if (!labelFile.exists()) {
                String orderNo = delOutbound.getOrderNo();
                // 查询地址信息
                DelOutboundAddress delOutboundAddress = this.delOutboundAddressService.getByOrderNo(orderNo);
                try {
                    // 查询SKU信息
                    List<String> nos = new ArrayList<>();
                    nos.add(orderNo);
                    Map<String, String> skuLabelMap = this.delOutboundDetailService.queryDetailsLabelByNos(nos);
                    String skuLabel = skuLabelMap.get(orderNo);
                    ByteArrayOutputStream byteArrayOutputStream = DelOutboundServiceImplUtil.renderPackageTransfer(delOutbound, delOutboundAddress, skuLabel);
                    byte[] fb = null;
                    FileUtils.writeByteArrayToFile(labelFile, fb = byteArrayOutputStream.toByteArray(), false);
                    ServletOutputStream outputStream = null;
                    InputStream inputStream = null;
                    try {
                        outputStream = response.getOutputStream();
                        //response为HttpServletResponse对象
                        response.setContentType("application/pdf;charset=utf-8");
                        //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
                        response.setHeader("Content-Disposition", "attachment;filename=" + delOutbound.getOrderNo() + ".pdf");
                        IOUtils.copy(new ByteArrayInputStream(fb), outputStream);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        R r = R.ok();
                        r.setMsg("读取标签文件失败");
                        return r;
                    } finally {
                        IoUtil.flush(outputStream);
                        IoUtil.close(outputStream);
                        IoUtil.close(inputStream);
                    }
                    return null;

                } catch (Exception e) {
                    R r = R.ok();
                    r.setMsg("标签文件不存在");
                    return r;
                }

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
                R r = R.ok();
                r.setMsg("读取标签文件失败");
                return r;
            } finally {
                IoUtil.flush(outputStream);
                IoUtil.close(outputStream);
                IoUtil.close(inputStream);
            }
        }else{
            if (StringUtils.isEmpty(delOutbound.getShipmentOrderNumber())) {
                R r = R.ok();
                r.setMsg("未获取承运商标签");
                return r;
            }
            String pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber() + ".pdf";
            File labelFile = new File(pathname);
            if (!labelFile.exists()) {
                R r = R.ok();
                r.setMsg("标签文件不存在");
                return r;
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
                R r = R.ok();
                r.setMsg("读取标签文件失败");
                return r;
            } finally {
                IoUtil.flush(outputStream);
                IoUtil.close(outputStream);
                IoUtil.close(inputStream);
            }
        }

        return null;
    }


    @Override
    public void labelSelfPick(HttpServletResponse response, DelOutboundLabelDto dto) {
        DelOutbound delOutbound = this.getById(dto.getId());
        if (null == delOutbound) {
            throw new CommonException("400", "单据不存在");
        }
        String pathname = DelOutboundServiceImplUtil.getSelfPickLabelFilePath(delOutbound) + "/" + delOutbound.getOrderNo() + ".pdf";
        File labelFile = new File(pathname);
        if (!labelFile.exists()) {
            String orderNo = delOutbound.getOrderNo();
            // 查询地址信息
            List<DelOutboundDetail> delOutboundDetailList = this.delOutboundDetailService.listByOrderNo(delOutbound.getOrderNo());
            try {
                ByteArrayOutputStream byteArrayOutputStream = DelOutboundServiceImplUtil.renderSelfPick(delOutbound, delOutboundDetailList, null);
                byte[] fb = null;
                FileUtils.writeByteArrayToFile(labelFile, fb = byteArrayOutputStream.toByteArray(), false);
                ServletOutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    outputStream = response.getOutputStream();
                    //response为HttpServletResponse对象
                    response.setContentType("application/pdf;charset=utf-8");
                    //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
                    response.setHeader("Content-Disposition", "attachment;filename=" + delOutbound.getOrderNo() + ".pdf");
                    IOUtils.copy(new ByteArrayInputStream(fb), outputStream);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new CommonException("500", "读取自提标签文件失败");
                } finally {
                    IoUtil.flush(outputStream);
                    IoUtil.close(outputStream);
                    IoUtil.close(inputStream);
                }
                return;

            } catch (Exception e) {
                throw new CommonException("400", "自提标签文件不存在");
            }

        }

        if(response == null){
            return;
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
            throw new CommonException("500", "读取自提标签文件失败");
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }


    }

    @Override
    public List<DelOutboundTarckOn> selectDelOutboundTarckList(DelOutboundTarckOn delOutboundTarckOn) {
        // 跟踪号，refNo
        List<String> otherQueryNoList = new ArrayList<>();
        if (StringUtils.isNotEmpty(delOutboundTarckOn.getOrderNos())) {
            List<String> nos = splitToArray(delOutboundTarckOn.getOrderNos(), "[\n,]");
            if (CollectionUtils.isNotEmpty(nos)) {
                for (String no : nos) {
                    // CK/RECK开头的是出库单号
//                    if (no.startsWith("CK") || no.startsWith("RECK")) {
//                        delOutboundNoList.add(no);
//                    } else {
                        otherQueryNoList.add(no);
//                    }
                }


            }
           delOutboundTarckOn.setOrderNosList(otherQueryNoList);

        }


        return delOutboundTarckOnMapper.selectByPrimaryKey(delOutboundTarckOn);
    }

    @Override
    public Integer selectDelOutboundCount(DelOutboundListQueryDto queryDto) {
        QueryWrapper<DelOutboundListQueryDto> queryWrapper = new QueryWrapper<>();
        // 界面上选择了记录进行导出
        if (CollectionUtils.isNotEmpty(queryDto.getSelectIds())) {
            queryWrapper.in("o.id", queryDto.getSelectIds());
            // 按照创建时间倒序
            queryWrapper.orderByDesc("o.create_time");
        } else {
            DelOutboundServiceImplUtil.handlerQueryWrapper(queryWrapper, queryDto);
        }
        return basFileMapper.selectDelOutboundCount(queryWrapper);

    }

    @Override
    public List<DelOutboundTarckError> selectbatchTrackingexport() {
        List<DelOutboundTarckError> list=delOutboundTarckErrorMapper.selectByPrimaryKey();
        delOutboundTarckErrorMapper.deleteByPrimaryKey();
        return list;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void carrierRegister(DelOutbound delOutbound) {
        //查询最新的挂号数据
        try {

            String referenceNumber = delOutbound.getReferenceNumber();

            if(StringUtils.isEmpty(referenceNumber)){
                log.error("订单号["+delOutbound.getOrderNo()+"] referenceNumber 为空");
                return;
            }

            R<ShipmentOrderResult> r = htpOutboundFeignService.shipmentOrderRealResult(referenceNumber);

            if (r.getCode() != 200) {
                log.error("订单号["+delOutbound.getOrderNo()+"]调用根据参考号返回真实的挂号和标签数据接口失败,{}",r.getMsg());
                throw new RuntimeException(r.getData().getError().getMessage());
            }

            if(!r.getData().getSuccess()){
                throw new RuntimeException(r.getData().getError().getMessage());
            }
            ShipmentOrderResult data = r.getData();
            if (!StringUtils.equals(delOutbound.getTrackingNo(), data.getMainTrackingNumber())) {
                //新老挂号不一样，更新数据库
                DelOutbound dataDelOutbound = this.getById(delOutbound.getId());
                dataDelOutbound.setTrackingNo(data.getMainTrackingNumber());
                this.updateById(dataDelOutbound);


                //新增挂号修改记录
                DelOutboundTarckOn delOutboundTarckOn = new DelOutboundTarckOn();
                delOutboundTarckOn.setOrderNo(dataDelOutbound.getOrderNo());
                delOutboundTarckOn.setTrackingNo(delOutbound.getTrackingNo());
                delOutboundTarckOn.setUpdateTime(new Date());
                delOutboundTarckOn.setTrackingNoNew(data.getMainTrackingNumber());
                delOutboundTarckOnMapper.insertSelective(delOutboundTarckOn);


                List<String> orders = new ArrayList<String>();
                orders.add(dataDelOutbound.getOrderNo());
                // 推送ty系统
                manualTrackingYee(orders);


                //推邮箱
                R<BasSellerInfoVO> info = basSellerFeignService.getInfoBySellerCode(dataDelOutbound.getSellerCode());
                if(info.getData() == null) {
                    throw new RuntimeException("客户信息获取失败");
                }
                BasSellerInfoVO userInfo = R.getDataAndException(info);

                EmailDto emailDto=new EmailDto();
                emailDto.setModularType(1);
                emailDto.setTo(userInfo.getEmail());
                EmailObjectDto emailDtoDetail=new EmailObjectDto();
                emailDtoDetail.setCustomCode(dataDelOutbound.getSellerCode());
                emailDtoDetail.setOrderNo(dataDelOutbound.getOrderNo());
                emailDtoDetail.setServiceManagerName(userInfo.getServiceManagerName());
                emailDtoDetail.setServiceStaffName(userInfo.getServiceStaffName());
                emailDtoDetail.setNoTrackingNo(delOutbound.getTrackingNo());
                emailDtoDetail.setTrackingNo(data.getMainTrackingNumber());
                emailDto.setList(Arrays.asList(emailDtoDetail));
                emailFeingService.sendEmail(emailDto);


            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean serviceChannelNamePushWMS(DelOutbound delOutbound, DelOutbound updateDelOutbound) {


        BasShipmentRulesDto paramBasShipmentRulesDto = new BasShipmentRulesDto();
        paramBasShipmentRulesDto.setCustomCode(delOutbound.getSellerCode());
        paramBasShipmentRulesDto.setServiceChannelName(delOutbound.getShipmentService());
        paramBasShipmentRulesDto.setDelFlag("1");
        List<BasShipmentRules> list = basShipmenRulesService.selectBasShipmentRules(paramBasShipmentRulesDto);
        if(list.isEmpty()){
            return false;
        }

        //直接变成仓库发货状态
        updateDelOutbound.setState(DelOutboundStateEnum.WHSE_COMPLETED.getCode());
        updateDelOutbound.setExceptionState(DelOutboundExceptionStateEnum.NORMAL.getCode());
        // 清空异常信息
        updateDelOutbound.setExceptionMessage("");
        // 设置提审时间
        updateDelOutbound.setBringVerifyTime(new Date());



        updateDelOutbound.setOperationTime(new Date());
        updateDelOutbound.setOperationType(DelOutboundOperationTypeEnum.SHIPPED.getCode());

        this.updateById(updateDelOutbound);
        // 增加出库单已完成记录，异步处理，定时任务

        Date pushDate = DateUtils.parseDate(DateUtil.format(new Date(), "yyyy-MM-dd") + " " + list.get(0).getPushDate());
        if(pushDate.getTime() < System.currentTimeMillis()){
            //推送时间大于当前时间的明日推送
            pushDate = DateUtils.parseDate(DateUtil.format(tomorrow(new Date()), "yyyy-MM-dd") + " " + list.get(0).getPushDate());
        }
        this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.SHIPPED.getCode(), pushDate);

        return true;
    }

    @Override
    public void doDirectExpressOrders() {

        Integer totalRecord = baseMapper.selectCount(Wrappers.<DelOutbound>query().lambda()
                        .eq(DelOutbound::getState,DelOutboundStateEnum.DELIVERED.getCode()));

        if(totalRecord == 0){
            return ;
        }

        Integer pageSize = 500;

        Integer totalPage = (totalRecord + pageSize -1) / pageSize;

        for(int i= 0;i<totalPage;i++) {


            List<DelOutbound> delOutbounds = baseMapper.selectByState(DelOutboundStateEnum.DELIVERED.getCode(), i, pageSize);


            //R<DirectExpressOrderApiDTO> directExpressOrderApiDTOR = htpOutboundFeignService.getDirectExpressOrder("CKCNFGZ622101800000018");

            List<DelOutbound> updateData = new ArrayList<>();

            for (DelOutbound delOutbound : delOutbounds) {

                R<DirectExpressOrderApiDTO> directExpressOrderApiDTOR = htpOutboundFeignService.getDirectExpressOrder(delOutbound.getOrderNo());

                if (directExpressOrderApiDTOR.getCode() != 200) {
                    continue;
                }

                DirectExpressOrderApiDTO directExpressOrderApiDTO = directExpressOrderApiDTOR.getData();

                String handleStatus = directExpressOrderApiDTO.getHandleStatus();

                if (handleStatus.equals(DelOutboundOperationTypeEnum.SHIPPED.getCode())) {

                    this.bringThridPartyAsync(delOutbound);

                    DelOutbound updatedata = new DelOutbound();
                    updatedata.setId(delOutbound.getId());

                    Double length = directExpressOrderApiDTO.getPacking().getLength().doubleValue();
                    Double width = directExpressOrderApiDTO.getPacking().getWidth().doubleValue();
                    Double height = directExpressOrderApiDTO.getPacking().getHeight().doubleValue();

                    Double weight = null;
                    Integer w = directExpressOrderApiDTO.getWeight();
                    if (w != null) {
                        weight = Double.parseDouble(w.toString());
                    }

                    String specifications = length + "*" + width + "*" + height;

                    updatedata.setLength(length);
                    updatedata.setWidth(width);
                    updatedata.setHeight(height);
                    updatedata.setWeight(weight);
                    updatedata.setSpecifications(specifications);
                    updatedata.setThridPartStatus(1);

                    updateData.add(updatedata);
                }
            }

            if (CollectionUtils.isNotEmpty(updateData)) {
                super.updateBatchById(updateData);
            }

        }

    }

    public void bringThridPartyAsync(DelOutbound delOutbound) {

        String key = applicationName + ":DelOutbound:bringThridPartyAsync:" + delOutbound.getOrderNo();
        RLock lock = this.redissonClient.getLock(key);
        try {
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                if (DelOutboundStateEnum.DELIVERED.getCode().equals(delOutbound.getState())) {
                    bringThridPartyAsync(delOutbound, AsyncThreadObject.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            throw new CommonException("500", "提审操作失败，" + e.getMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void bringThridPartyAsync(DelOutbound delOutbound,AsyncThreadObject asyncThreadObject){

        StopWatch stopWatch = new StopWatch();
        Thread thread = Thread.currentThread();
        // 开始时间
        long startTime = System.currentTimeMillis();
        boolean isAsyncThread = !asyncThreadObject.isAsyncThread();
        logger.info("(1)任务开始执行，当前任务名称：{}，当前任务ID：{}，是否为异步任务：{}，任务相关参数：{}", thread.getName(), thread.getId(), isAsyncThread, JSON.toJSONString(asyncThreadObject));
        if (isAsyncThread) {
            asyncThreadObject.loadTid();
        }
        stopWatch.start();
        DelOutboundWrapperContext context = this.delOutboundBringVerifyService.initContext(delOutbound);
        stopWatch.stop();
        logger.info(">>>>>[创建出库单{}]初始化出库对象 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

        ThridPartyEnum currentState;
        String bringVerifyState = delOutbound.getBringVerifyState();
        if (org.apache.commons.lang3.StringUtils.isEmpty(bringVerifyState)) {
            currentState = ThridPartyEnum.BEGIN;
        } else {
            currentState = ThridPartyEnum.get(bringVerifyState);
            // 兼容
            if (null == currentState) {
                currentState = ThridPartyEnum.BEGIN;
            }
        }
        logger.info("(2)提审异步操作开始，出库单号：{}", delOutbound.getOrderNo());
        ApplicationContainer applicationContainer = new ApplicationContainer(context, currentState, ThridPartyEnum.END, ThridPartyEnum.BEGIN);
        try {
            applicationContainer.action();

            //if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
            // 增加出库单已取消记录，异步处理，定时任务
            this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.SHIPPED.getCode());
            //}

            logger.info("(3)提审异步操作成功，出库单号：{}", delOutbound.getOrderNo());
        } catch (CommonException e) {
            // 回滚操作
            applicationContainer.setEndState(ThridPartyEnum.BEGIN);
            applicationContainer.rollback();
            // 异步屏蔽异常，将异常打印到日志中
            // 异步错误在单据里面会显示错误信息
            this.logger.error("(4)提审异步操作失败，出库单号：" + delOutbound.getOrderNo() + "，错误原因：" + e.getMessage(), e);
        } finally {
            if (isAsyncThread) {
                asyncThreadObject.unloadTid();
            }
        }
        this.logger.info("(5)提审操作完成，出库单号：{}，执行耗时：{}", delOutbound.getOrderNo(), (System.currentTimeMillis() - startTime));

    }

    public static Date tomorrow(Date today) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        return calendar.getTime();
    }

    public static List<String> splitToArray(String text, String split) {
        String[] arr = text.split(split);
        if (arr.length == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            list.add(s);
        }
        return list;
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
        DelOutboundFurtherHandlerDto furtherHandlerDto = new DelOutboundFurtherHandlerDto();

        boolean update = delOutboundExceptionService.againTrackingNo(delOutbound, dto, furtherHandlerDto);
        if (update) {
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

        int i = 0;
        for (DelOutboundDetail detail: dataDelOutboundDetailList){
            if("Completed".equals(detail.getOperationType())){
                i++;
            }
        }
        if(i == dataDelOutboundDetailList.size()){
            //该订单全部接收完成后，调用PRC
            ApplicationContext context = delOutboundBringVerifyService.initContext(this.getByOrderNo(dto.getOrderNo()));
            ApplicationContainer applicationContainer = new ApplicationContainer(context, BringVerifyEnum.PRC_PRICING, BringVerifyEnum.PRODUCT_INFO, BringVerifyEnum.PRC_PRICING);
            applicationContainer.action();

        }
        return 1;
    }


    @Override
    public void manualTrackingYee(List<String> list) {
        list.forEach(x->{
            DelOutboundListQueryDto delOutboundListQueryDto=baseMapper.pageLists(x);
            TraYee(delOutboundListQueryDto);
        });

    }

    public void TraYee(DelOutboundListQueryDto delOutboundListQueryDto){
        boolean success = false;
        String responseBody;
        logger.info("手动推送TY{}");
        try {
            Map<String, Object> requestBodyMap = new HashMap<>();
            List<Map<String, Object>> shipments = new ArrayList<>();
            Map<String, Object> shipment = new HashMap<>();
            shipment.put("trackingNo", delOutboundListQueryDto.getTrackingNo());
            shipment.put("carrierCode", delOutboundListQueryDto.getLogisticsProviderCode());
            shipment.put("logisticsServiceProvider", delOutboundListQueryDto.getLogisticsProviderCode());
            shipment.put("logisticsServiceName", delOutboundListQueryDto.getLogisticsProviderCode());
            shipment.put("platformCode", "DM");
            shipment.put("shopName", "");
            Date createTime = delOutboundListQueryDto.getCreateTime();
            if (null != createTime) {
                shipment.put("OrdersOn", DateFormatUtils.format(createTime, "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));
            }
            shipment.put("paymentTime", "");
            shipment.put("shippingOn", "");
            List<String> searchTags = new ArrayList<>();
            searchTags.add("");
            searchTags.add("");
            shipment.put("searchTags", searchTags);
            shipment.put("orderNo", delOutboundListQueryDto.getOrderNo());
            Map<String, Object> senderAddress = new HashMap<>();
            senderAddress.put("country", delOutboundListQueryDto.getCountry());
            senderAddress.put("province", delOutboundListQueryDto.getStateOrProvince());
            senderAddress.put("city", delOutboundListQueryDto.getCity());
            senderAddress.put("postcode", delOutboundListQueryDto.getPostCode());
            senderAddress.put("street1", delOutboundListQueryDto.getStreet1());
            senderAddress.put("street2", delOutboundListQueryDto.getStreet2());
            senderAddress.put("street3", delOutboundListQueryDto.getStreet3());
            shipment.put("senderAddress", senderAddress);
            Map<String, Object> destinationAddress = new HashMap<>();
            destinationAddress.put("country", delOutboundListQueryDto.getCountry());
            destinationAddress.put("province", delOutboundListQueryDto.getStateOrProvince());
            destinationAddress.put("city", delOutboundListQueryDto.getCity());
            destinationAddress.put("postcode", delOutboundListQueryDto.getPostCode());
            destinationAddress.put("street1", delOutboundListQueryDto.getStreet1());
            destinationAddress.put("street2",delOutboundListQueryDto.getStreet2());
            destinationAddress.put("street3", delOutboundListQueryDto.getStreet3());
            shipment.put("destinationAddress", destinationAddress);
            Map<String, Object> recipientInfo = new HashMap<>();
            recipientInfo.put("recipient", delOutboundListQueryDto.getConsignee());
            recipientInfo.put("phoneNumber", delOutboundListQueryDto.getPhoneNo());
            recipientInfo.put("email", "");
            shipment.put("recipientInfo", recipientInfo);
            Map<String, Object> customFieldInfo = new HashMap<>();
            customFieldInfo.put("fieldOne", delOutboundListQueryDto.getOrderNo());
            customFieldInfo.put("fieldTwo", "");
            customFieldInfo.put("fieldThree", "");
            shipment.put("customFieldInfo", customFieldInfo);
            shipments.add(shipment);
            requestBodyMap.put("shipments", shipments);
            HttpRequestDto httpRequestDto = new HttpRequestDto();
            httpRequestDto.setMethod(HttpMethod.POST);
            String url = DomainEnum.TrackingYeeDomain.wrapper("/tracking/v1/shipments");
            httpRequestDto.setUri(url);
            httpRequestDto.setBody(requestBodyMap);
            HttpResponseVO httpResponseVO = htpRmiClientService.rmi(httpRequestDto);
            if (200 == httpResponseVO.getStatus() ||
                    201 == httpResponseVO.getStatus()) {
                success = true;
            }
            responseBody = (String) httpResponseVO.getBody();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseBody = e.getMessage();
            if (null == responseBody) {
                responseBody = "请求失败";
            }
        }
        // 请求成功，解析响应报文
        if (success) {
            try {
                // 解析响应报文，获取响应参数信息
                JSONObject jsonObject = JSON.parseObject(responseBody);
                // 判断状态是否为OK
                if ("OK".equals(jsonObject.getString("status"))) {
                    // 判断结果明细是不是成功的
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (1 != data.getIntValue("successNumber")) {
                        // 返回的成功数量不是1，判定为异常
                        success = false;
                        // 获取异常信息
                        int failNumber = data.getIntValue("failNumber");
                        if (failNumber > 0) {
                            JSONArray failImportRowResults = data.getJSONArray("failImportRowResults");
                            JSONObject failImportRowResult = failImportRowResults.getJSONObject(0);
                            JSONObject errorInfo = failImportRowResult.getJSONObject("errorInfo");
                            String errorCode = errorInfo.getString("errorCode");
                            String errorMessage = errorInfo.getString("errorMessage");
                            throw new CommonException("500", "[" + errorCode + "]" + errorMessage);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                if (e instanceof CommonException) {
                    throw e;
                }
                // 解析失败，判定为异常
                success = false;
            }
        }
        if (!success) {
            throw new CommonException("500", "创建TrackingYee失败");
        }
    }

    public void manualTrackingYees(String orderNo) {

            DelOutboundListQueryDto delOutboundListQueryDto=baseMapper.pageLists(orderNo);
            TraYees(delOutboundListQueryDto);


    }

    public void TraYees(DelOutboundListQueryDto delOutboundListQueryDto){
        boolean success = false;
        String responseBody;
        try {
            Map<String, Object> requestBodyMap = new HashMap<>();
            List<Map<String, Object>> shipments = new ArrayList<>();
            Map<String, Object> shipment = new HashMap<>();
            shipment.put("trackingNo", delOutboundListQueryDto.getTrackingNo());
            shipment.put("carrierCode", delOutboundListQueryDto.getLogisticsProviderCode());
            shipment.put("logisticsServiceProvider", delOutboundListQueryDto.getLogisticsProviderCode());
            shipment.put("logisticsServiceName", delOutboundListQueryDto.getLogisticsProviderCode());
            shipment.put("platformCode", "DM");
            shipment.put("shopName", "");
            Date createTime = delOutboundListQueryDto.getCreateTime();
            if (null != createTime) {
                shipment.put("OrdersOn", DateFormatUtils.format(createTime, "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));
            }
            shipment.put("paymentTime", "");
            shipment.put("shippingOn", "");
            List<String> searchTags = new ArrayList<>();
            searchTags.add("");
            searchTags.add("");
            shipment.put("searchTags", searchTags);
            shipment.put("orderNo", delOutboundListQueryDto.getOrderNo());
            Map<String, Object> senderAddress = new HashMap<>();
            senderAddress.put("country", delOutboundListQueryDto.getCountry());
            senderAddress.put("province", delOutboundListQueryDto.getStateOrProvince());
            senderAddress.put("city", delOutboundListQueryDto.getCity());
            senderAddress.put("postcode", delOutboundListQueryDto.getPostCode());
            senderAddress.put("street1", delOutboundListQueryDto.getStreet1());
            senderAddress.put("street2", delOutboundListQueryDto.getStreet2());
            senderAddress.put("street3", delOutboundListQueryDto.getStreet3());
            shipment.put("senderAddress", senderAddress);
            Map<String, Object> destinationAddress = new HashMap<>();
            destinationAddress.put("country", delOutboundListQueryDto.getCountry());
            destinationAddress.put("province", delOutboundListQueryDto.getStateOrProvince());
            destinationAddress.put("city", delOutboundListQueryDto.getCity());
            destinationAddress.put("postcode", delOutboundListQueryDto.getPostCode());
            destinationAddress.put("street1", delOutboundListQueryDto.getStreet1());
            destinationAddress.put("street2",delOutboundListQueryDto.getStreet2());
            destinationAddress.put("street3", delOutboundListQueryDto.getStreet3());
            shipment.put("destinationAddress", destinationAddress);
            Map<String, Object> recipientInfo = new HashMap<>();
            recipientInfo.put("recipient", delOutboundListQueryDto.getConsignee());
            recipientInfo.put("phoneNumber", delOutboundListQueryDto.getPhoneNo());
            recipientInfo.put("email", "");
            shipment.put("recipientInfo", recipientInfo);
            Map<String, Object> customFieldInfo = new HashMap<>();
            customFieldInfo.put("fieldOne", delOutboundListQueryDto.getOrderNo());
            customFieldInfo.put("fieldTwo", "");
            customFieldInfo.put("fieldThree", "");
            shipment.put("customFieldInfo", customFieldInfo);
            shipments.add(shipment);
            requestBodyMap.put("shipments", shipments);
            HttpRequestDto httpRequestDto = new HttpRequestDto();
            httpRequestDto.setMethod(HttpMethod.POST);
            String url = DomainEnum.TrackingYeeDomain.wrapper("/tracking/v1/shipments");
            httpRequestDto.setUri(url);
            httpRequestDto.setBody(requestBodyMap);
            HttpResponseVO httpResponseVO = htpRmiClientService.rmi(httpRequestDto);
            if (200 == httpResponseVO.getStatus() ||
                    201 == httpResponseVO.getStatus()) {
                success = true;
            }
            responseBody = (String) httpResponseVO.getBody();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseBody = e.getMessage();
            if (null == responseBody) {
                responseBody = "请求失败";
            }
        }
        // 请求成功，解析响应报文
        if (success) {
            try {
                // 解析响应报文，获取响应参数信息
                JSONObject jsonObject = JSON.parseObject(responseBody);
                // 判断状态是否为OK
                if ("OK".equals(jsonObject.getString("status"))) {
                    // 判断结果明细是不是成功的
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (1 != data.getIntValue("successNumber")) {
                        // 返回的成功数量不是1，判定为异常
                        success = false;
                        // 获取异常信息
                        int failNumber = data.getIntValue("failNumber");
                        if (failNumber > 0) {
                            JSONArray failImportRowResults = data.getJSONArray("failImportRowResults");
                            JSONObject failImportRowResult = failImportRowResults.getJSONObject(0);
                            JSONObject errorInfo = failImportRowResult.getJSONObject("errorInfo");
                            String errorCode = errorInfo.getString("errorCode");
                            String errorMessage = errorInfo.getString("errorMessage");
                            //throw new CommonException("500", "[" + errorCode + "]" + errorMessage);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                if (e instanceof CommonException) {
                    //throw e;
                }
                // 解析失败，判定为异常
                success = false;
            }
        }
        if (!success) {
            //throw new CommonException("500", "创建TrackingYee失败");
        }
    }
}

