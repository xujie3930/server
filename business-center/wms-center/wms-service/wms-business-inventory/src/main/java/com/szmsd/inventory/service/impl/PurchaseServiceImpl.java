package com.szmsd.inventory.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.component.RemoteRequest;
import com.szmsd.inventory.config.IBOConvert;
import com.szmsd.inventory.domain.Purchase;
import com.szmsd.inventory.domain.PurchaseDetails;
import com.szmsd.inventory.domain.PurchaseStorageDetails;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.excel.PurchaseInfoDetailExcle;
import com.szmsd.inventory.domain.excel.PurchaseInfoDetailExcleep;
import com.szmsd.inventory.domain.excel.PurchaseStorageDetailsExcle;
import com.szmsd.inventory.domain.excel.PurchaseStorageDetailsExclesp;
import com.szmsd.inventory.domain.vo.PurchaseInfoDetailVO;
import com.szmsd.inventory.domain.vo.PurchaseInfoListVO;
import com.szmsd.inventory.domain.vo.PurchaseInfoVO;
import com.szmsd.inventory.domain.vo.PurchaseStorageDetailsVO;
import com.szmsd.inventory.mapper.PurchaseDetailsMapper;
import com.szmsd.inventory.mapper.PurchaseMapper;
import com.szmsd.inventory.mapper.PurchaseStorageDetailsMapper;
import com.szmsd.inventory.service.IPurchaseDetailsService;
import com.szmsd.inventory.service.IPurchaseLogService;
import com.szmsd.inventory.service.IPurchaseService;
import com.szmsd.inventory.service.IPurchaseStorageDetailsService;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.enums.InboundReceiptEnum;
import com.szmsd.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>
 * ????????? ???????????????
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Slf4j
@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements IPurchaseService {
    @Resource
    private RemoteComponent remoteComponent;
    @Resource
    private SerialNumberClientService serialNumberClientService;
    @Resource
    private IPurchaseLogService iPurchaseLogService;
    @Resource
    private IPurchaseDetailsService iPurchaseDetailsService;
    @Resource
    private IPurchaseStorageDetailsService iPurchaseStorageDetailsService;
    @Resource
    private RemoteRequest remoteRequest;
    @Autowired
    private RemoteAttachmentService remoteAttachmentService;
    @Autowired
    private PurchaseDetailsMapper purchaseDetailsMapper;
    @Autowired
    private PurchaseStorageDetailsMapper purchaseStorageDetailsMapper;

    @Override
    public PurchaseInfoVO selectPurchaseByPurchaseNo(String purchaseNo) {
        PurchaseInfoVO purchaseInfoVO = baseMapper.selectPurchaseByPurchaseNo(purchaseNo);
        if (null != purchaseInfoVO) {
            List<PurchaseInfoDetailVO> purchaseDetailsAddList = purchaseInfoVO.getPurchaseDetailsAddList();
            //????????????
            purchaseDetailsAddList.forEach(x -> {
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO().setAttachmentType(AttachmentTypeEnum.SKU_IMAGE.getAttachmentType()).setBusinessNo(x.getSku());
                List<BasAttachment> attachment = ListUtils.emptyIfNull(remoteAttachmentService.list(basAttachmentQueryDTO).getData());
                if (CollectionUtils.isNotEmpty(attachment)) {
                    BasAttachment basAttachment = attachment.get(0);
                    String attachmentUrl = basAttachment.getAttachmentUrl();
                    x.setAttachmentUrl(attachmentUrl);
                    x.setEditionImage(new AttachmentFileDTO().setId(basAttachment.getId()).setAttachmentName(basAttachment.getAttachmentName()).setAttachmentUrl(basAttachment.getAttachmentUrl()));
                }
            });
        }
        return purchaseInfoVO;
    }

    /**
     * ???????????????????????????
     *
     * @param
     * @return ???????????????
     */
    @Override
    public List<PurchaseInfoListVO> selectPurchaseList(PurchaseQueryDTO purchaseQueryDTO) {
        return baseMapper.selectPurchaseList(purchaseQueryDTO);
    }

    @Override
    public List<PurchaseInfoListVO> selectPurchaseListClient(PurchaseQueryDTO purchaseQueryDTO) {
//        SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
//        purchaseQueryDTO.setCustomCode(loginUserInfo.getSellerCode());
        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
            if (com.szmsd.common.core.utils.StringUtils.isEmpty(purchaseQueryDTO.getCustomCode())) {
                purchaseQueryDTO.setCustomCode(cusCode);
            }
        }
        return selectPurchaseList(purchaseQueryDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelByWarehouseNo(String warehouseNo) {
        log.info("???????????????{}????????????????????????????????????", warehouseNo);
        //????????????????????? ????????????
        List<PurchaseStorageDetails> rollBackStorage = iPurchaseStorageDetailsService.list(Wrappers.<PurchaseStorageDetails>lambdaQuery()
                .eq(PurchaseStorageDetails::getWarehousingNo, warehouseNo)
                .eq(PurchaseStorageDetails::getDelFlag, 0)
        );
        if (CollectionUtils.isEmpty(rollBackStorage)) {
            log.info("???????????????");
            return 0;
        }
        //???????????????
        int sumCount = rollBackStorage.stream().mapToInt(PurchaseStorageDetails::getDeclareQty).sum();

        Map<String, Integer> skuAndNum = rollBackStorage.stream().collect(Collectors.toMap(PurchaseStorageDetails::getSku, PurchaseStorageDetails::getDeclareQty, Integer::sum));

        // ????????????sku????????? ?????????
        List<String> skuList = rollBackStorage.stream().map(PurchaseStorageDetails::getSku).collect(Collectors.toList());
        // ????????????
        List<PurchaseDetails> detailsList = iPurchaseDetailsService.list(Wrappers.<PurchaseDetails>lambdaQuery().in(PurchaseDetails::getSku, skuList));

        //???????????????????????????
        detailsList.forEach(x -> {
            String sku = x.getSku();
            Integer integer = skuAndNum.get(sku);
            Optional.ofNullable(integer).ifPresent(c -> {
                        x.setRemainingPurchaseQuantity(x.getRemainingPurchaseQuantity() + c);
                        x.setQuantityInStorageCreated(x.getQuantityInStorageCreated() - c);
                    }

            );
        });
        log.info("??????sku?????? {}", JSONObject.toJSONString(detailsList));
        iPurchaseDetailsService.saveOrUpdateBatch(detailsList);

        Integer associationId = rollBackStorage.get(0).getAssociationId();
        Purchase purchase = baseMapper.selectById(associationId);
        if (null == purchase) {
            log.info("???????????????");
            return 0;
        }

        int update = baseMapper.update(new Purchase(), Wrappers.<Purchase>lambdaUpdate().eq(Purchase::getId, associationId)
                .setSql("remaining_purchase_quantity = remaining_purchase_quantity +" + sumCount)
                .setSql("quantity_in_storage_created = quantity_in_storage_created -" + sumCount)
        );
        log.info("?????????????????? {}???", update);

        boolean update1 = iPurchaseStorageDetailsService.update(Wrappers.<PurchaseStorageDetails>lambdaUpdate()
                .set(PurchaseStorageDetails::getDelFlag, 2)
                .eq(PurchaseStorageDetails::getWarehousingNo, warehouseNo)
        );
        log.info("??????????????????????????? {}???", update1);

        iPurchaseLogService.addLog(warehouseNo, rollBackStorage, associationId, purchase);
        return 1;
    }

    /**
     * ???????????????????????????
     *
     * @param ids ??????????????????????????????ID
     * @return ??????
     */
    @Override
    public int deletePurchaseByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurchaseBatch(PurchaseAddDTO purchaseAddDTO) {
        log.info("????????????????????? {}", purchaseAddDTO);
        AssertUtil.notNull(purchaseAddDTO, ExceptionMessageEnum.CANNOTBENULL);
        Integer associationId;

        boolean present = Optional.of(purchaseAddDTO).map(PurchaseAddDTO::getPurchaseNo).isPresent();
        if (!present) {
            String purchaseNo = serialNumberClientService.generateNumber("PURCHASE_ORDER_CG");
            SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
            String customCode = loginUserInfo.getSellerCode();
            //???????????? CG + customCode+ yyyyMMdd+6
            purchaseNo = "CG" + customCode + purchaseNo;
            purchaseAddDTO.setCustomCode(customCode);
            purchaseAddDTO.setPurchaseNo(purchaseNo);
        }
        //??????????????????
        purchaseAddDTO.insertHandle();
        Purchase purchase = purchaseAddDTO.convertThis(Purchase.class);
        log.info("?????????????????????{}", purchase);
        boolean saveBoolean = this.saveOrUpdate(purchase);
        associationId = purchase.getId();

        //?????????????????????
        List<PurchaseDetailsAddDTO> purchaseDetailsAddList = purchaseAddDTO.getPurchaseDetailsAddList();
        if (CollectionUtils.isNotEmpty(purchaseDetailsAddList)) {
            Optional.of(purchaseDetailsAddList).filter(CollectionUtils::isNotEmpty).ifPresent(
                    purchaseDetailList -> {
                        List<PurchaseDetails> entityList = IBOConvert.copyListProperties(purchaseDetailList, PurchaseDetails::new);
                        entityList.forEach(x -> x.setAssociationId(associationId));
                        iPurchaseDetailsService.saveOrUpdateBatch(entityList);
                    });
            //???????????????????????????
            //??????????????????
            purchaseOrderStorage(associationId, purchaseAddDTO);
        }
        //??????
        iPurchaseLogService.addLog(associationId, purchaseAddDTO);

        //??????????????? ??????????????????
        remoteComponent.setPurchaseNo(purchaseAddDTO.getPurchaseNo(), purchaseAddDTO.getOrderNo());
        return saveBoolean ? 1 : 0;
    }

    private void purchaseOrderStorage(Integer associationId, PurchaseAddDTO purchaseAddDTO) {
        log.info("????????????????????????--");
        //???????????????
        List<PurchaseStorageDetailsAddDTO> purchaseStorageDetailsAddList = purchaseAddDTO.getPurchaseStorageDetailsAddList();
        List<PurchaseDetailsAddDTO> detailsList = purchaseAddDTO.getPurchaseDetailsAddList();
        //??????sku ??????????????????
        Map<String, List<PurchaseDetailsAddDTO>> collect1 = detailsList.stream().collect(Collectors.groupingBy(PurchaseDetailsAddDTO::getSku));
        List<PurchaseStorageDetailsAddDTO> waitStorage = purchaseStorageDetailsAddList.stream().filter(x -> !(null != x.getId() && x.getId() > 0)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(waitStorage)) {
            log.info("????????????????????????,????????????????????????");
        }
        SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
        String sellerCode = loginUserInfo.getSellerCode();
        //???????????? ????????????????????????????????????
        Map<String, List<PurchaseStorageDetailsAddDTO>> collect = waitStorage.stream().collect(Collectors.groupingBy(PurchaseStorageDetailsAddDTO::getDeliveryNo));
        collect.forEach((no, addList) -> {
            if (CollectionUtils.isEmpty(addList)) {
                log.info("???{}???????????????,???sku??????", no);
                return;
            }
            // ????????????sku??????
            addList = merge(addList);
            int sum = addList.stream().mapToInt(PurchaseStorageDetailsAddDTO::getDeclareQty).sum();
            // ??????????????????
            CreateInboundReceiptDTO createInboundReceiptDTO = new CreateInboundReceiptDTO();
            createInboundReceiptDTO
                    .setDeliveryNo(no)
                    // ?????????orderType ?????????????????? warehouseMethodCode
                    .setWarehouseMethodCode(purchaseAddDTO.getOrderType())
                    .setOrderNo(purchaseAddDTO.getPurchaseNo())
                    .setCusCode(sellerCode)
                    .setVat(purchaseAddDTO.getVat())
                    .setWarehouseCode(purchaseAddDTO.getWarehouseCode())
                    .setOrderType(InboundReceiptEnum.OrderType.COLLECTION.getValue())
                    .setWarehouseCategoryCode(purchaseAddDTO.getWarehouseCategoryCode())
                    .setDeliveryWayCode(purchaseAddDTO.getDeliveryWay())
                    .setTotalDeclareQty(sum)
                    .setTotalPutQty(0)
                    .setRemark(purchaseAddDTO.getRemark())
            ;
            //??????SKU????????????
            ArrayList<InboundReceiptDetailDTO> inboundReceiptDetailAddList = new ArrayList<>();
            addList.forEach(addSku -> {
                InboundReceiptDetailDTO inboundReceiptDetailDTO = new InboundReceiptDetailDTO();
                inboundReceiptDetailDTO
                        .setDeclareQty(addSku.getDeclareQty())
                        .setSku(addSku.getSku())
                        .setDeliveryNo(purchaseAddDTO.getPurchaseNo())
                        // ?????????????????????
                        .setSkuName(addSku.getProductName())
                ;
                Optional.ofNullable(collect1.get(addSku.getSku())).filter(CollectionUtils::isNotEmpty).ifPresent(x -> inboundReceiptDetailDTO.setSkuName(x.get(0).getProductName()));
                inboundReceiptDetailAddList.add(inboundReceiptDetailDTO);
            });
            createInboundReceiptDTO.setInboundReceiptDetails(inboundReceiptDetailAddList);

            InboundReceiptInfoVO inboundReceiptInfoVO = remoteComponent.orderStorage(createInboundReceiptDTO);
            String warehouseNo = inboundReceiptInfoVO.getWarehouseNo();
            //?????? ???????????? ?????? + ??????
            Optional.of(addList).filter(CollectionUtils::isNotEmpty).ifPresent(
                    purchaseStorageDetailsList -> {
                        List<PurchaseStorageDetails> entityList = IBOConvert.copyListProperties(purchaseStorageDetailsList, PurchaseStorageDetails::new);
                        entityList.forEach(x -> {
                            x.setAssociationId(associationId);
                            x.setWarehousingNo(warehouseNo);
                        });
                        iPurchaseStorageDetailsService.saveOrUpdateBatch(entityList);
                        //????????????-????????????
                        iPurchaseLogService.addLog(associationId, purchaseAddDTO, warehouseNo);
                    });
        });
        log.info("????????????");
    }

    /**
     * ???id????????????nums, sums ????????????????????????????????????Java8??????????????????
     */
    public static List<PurchaseStorageDetailsAddDTO> merge(List<PurchaseStorageDetailsAddDTO> list) {
        return new ArrayList<>(list.stream()
                .collect(Collectors.toMap(PurchaseStorageDetailsAddDTO::getSku, a -> a, (o1, o2) -> {
                    o1.setDeclareQty(o1.getDeclareQty() + o2.getDeclareQty());
                    return o1;
                })).values());
    }


    private static List<DelOutboundDetailVO> mergeTwo(List<DelOutboundDetailVO> transshipmentProductData) {
        return new ArrayList<>(transshipmentProductData.stream().collect(Collectors.toMap(DelOutboundDetailVO::getSku, a -> a, (o1, o2) -> {
            o1.setQty(o1.getQty() + o2.getQty());
            return o1;
        })).values());
    }

    @Resource
    private DelOutboundFeignService delOutboundFeignService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int transportWarehousingSubmit(TransportWarehousingAddDTO transportWarehousingAddDTO) {
        SecurityContext context = SecurityContextHolder.getContext();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            SecurityContextHolder.setContext(context);

            SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
            String sellerCode = loginUserInfo.getSellerCode();
            log.info("???????????????sellerCode:"+ sellerCode+",customCode:"+transportWarehousingAddDTO.getCustomCode());
            sellerCode = Optional.ofNullable(sellerCode).orElse(transportWarehousingAddDTO.getCustomCode());
            //??????sku??????
//            List<DelOutboundDetailVO> transshipmentProductData = new ArrayList<>();
            /*List<DelOutboundDetailVO> transshipmentProductData = remoteComponent.getTransshipmentProductData(transportWarehousingAddDTO.getIdList());
            if (CollectionUtils.isEmpty(transshipmentProductData)) {
                throw new RuntimeException("???????????????");
            }*/
            //????????????sku??????
            //transshipmentProductData = mergeTwo(transshipmentProductData);
            //???????????????
//            long sum = transshipmentProductData.stream().mapToLong(DelOutboundDetailVO::getQty).sum();
            String deliveryNo = transportWarehousingAddDTO.getDeliveryNo();
            CreateInboundReceiptDTO createInboundReceiptDTO = new CreateInboundReceiptDTO();

            createInboundReceiptDTO
                    .setDeliveryNo(deliveryNo)
                    .setCusCode(sellerCode)
//                .setCusCode("WS77")
                    .setVat(transportWarehousingAddDTO.getVat())
                    .setWarehouseCode(transportWarehousingAddDTO.getWarehouseCode())
                    .setWarehouseMethodCode(transportWarehousingAddDTO.getWarehouseMethodCode())
                    .setOrderType(InboundReceiptEnum.OrderType.PACKAGE_TRANSFER.getValue())
                    .setWarehouseCategoryCode(transportWarehousingAddDTO.getWarehouseCategoryCode())
                    .setDeliveryWayCode(transportWarehousingAddDTO.getDeliveryWay())
//                    .setTotalDeclareQty(Integer.parseInt(sum + ""))
                    .setTotalDeclareQty(1)
//                .setTotalDeclareQty(10)
                    .setTotalPutQty(0);
            //????????????sku??????
            List<String> transferNoList = transportWarehousingAddDTO.getTransferNoList();
            //??????SKU????????????
            List<InboundReceiptDetailDTO> inboundReceiptDetailAddList = transferNoList.stream().map(x -> {
                InboundReceiptDetailDTO inboundReceiptDetailDTO = new InboundReceiptDetailDTO();
                inboundReceiptDetailDTO.setDeliveryNo(x);
                inboundReceiptDetailDTO.setDeclareQty(1);
                return inboundReceiptDetailDTO;
            }).collect(Collectors.toList());
            /*ArrayList<InboundReceiptDetailDTO> inboundReceiptDetailAddList = new ArrayList<>();
            transshipmentProductData.forEach(addSku -> {
                InboundReceiptDetailDTO inboundReceiptDetailDTO = new InboundReceiptDetailDTO();
                inboundReceiptDetailDTO
                        .setDeclareQty(Integer.parseInt(addSku.getQty() + ""))
//                    .setDeclareQty(10)
                        // ??????sku??????????????? 0526-????????????sku
//                    .setSku(addSku.getOrderNo())
                        //????????????
                        .setDeliveryNo(addSku.getOrderNo())
                        .setSkuName(addSku.getProductName())
                ;
                inboundReceiptDetailAddList.add(inboundReceiptDetailDTO);
            });*/
            //????????????????????????????????????1
            Collection<InboundReceiptDetailDTO> values = inboundReceiptDetailAddList.stream().collect(Collectors.toMap(InboundReceiptDetailDTO::getDeliveryNo, x -> x, (x1, x2) -> x1)).values();
            List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS = new ArrayList<>(values);
            inboundReceiptDetailDTOS.forEach(x -> x.setDeclareQty(1));
            createInboundReceiptDTO.setInboundReceiptDetails(inboundReceiptDetailDTOS);
            Integer integer = inboundReceiptDetailDTOS.stream().map(InboundReceiptDetailDTO::getDeclareQty).reduce(Integer::sum).orElse(1);
            createInboundReceiptDTO.setTotalDeclareQty(integer);
            createInboundReceiptDTO.setTransferNoList(transferNoList);
            InboundReceiptInfoVO inboundReceiptInfoVO = remoteComponent.orderStorage(createInboundReceiptDTO);
            // ??????????????????????????????
            log.info("?????????????????????????????? {}", transportWarehousingAddDTO.getIdList());
            List<Long> callBackOrderIdList = transportWarehousingAddDTO.getIdList().stream().filter(StringUtils::isNotBlank).map(Long::valueOf).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(callBackOrderIdList)){
                R<Boolean> booleanR = delOutboundFeignService.updateInStockList(callBackOrderIdList);
                log.info("?????????????????????????????? {}", booleanR);
            }

        });
        return 0;
    }

    @Override
    public List<PurchaseInfoDetailExcle> selectPurchaseInfoDetailEx(Integer id) {
        List<PurchaseInfoDetailExcle> list=purchaseDetailsMapper.selectPurchaseInfoDetailExcleListByAssId(id);
        list.forEach(x->{
            List<PurchaseStorageDetailsExcle> list1= purchaseStorageDetailsMapper.selectPurchaseStorageDetailsExcleListByAssId(x.getSku(),id);
            x.setPurchaseStorageDetailsExcles(list1);
        });
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map importPurchaseInfoDetailExcle(List<PurchaseInfoDetailExcle> list,String associationId,String purchaseNo) {
        List<PurchaseStorageDetailsExcle> purchaseStorageDetailsExcleList=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            String sku=list.get(i).getSku();
            list.get(i).getPurchaseStorageDetailsExcles().forEach(x->{
                x.setSku(sku);
                x.setAssociationId(associationId);
                purchaseStorageDetailsExcleList.add(x);
            });
        }
        PurchaseInfoVO purchaseInfoVO = baseMapper.selectPurchaseByPurchaseNo(purchaseNo);
        List<PurchaseStorageDetailsVO> purchaseStorageDetailsList1=purchaseInfoVO.getPurchaseStorageDetailsAddList();
        //?????????
        int totalNumber=purchaseStorageDetailsExcleList.size();
        int successNumber=0;
        int failNumber=0;
        //????????????????????????????????????
        for (int x=0;x<purchaseStorageDetailsExcleList.size();x++){
            List<PurchaseStorageDetailsVO> purchaseStorageDetailsVOs= purchaseStorageDetailsMapper.selectPurchaseStorageDetailsVO(purchaseStorageDetailsExcleList.get(x));
            PurchaseStorageDetailsVO purchaseStorageDetailsVO=new PurchaseStorageDetailsVO();
            PurchaseStorageDetails purchaseStorageDetails=new PurchaseStorageDetails();
            BeanUtils.copyProperties(purchaseStorageDetailsExcleList.get(x), purchaseStorageDetails);
            BeanUtils.copyProperties(purchaseStorageDetailsExcleList.get(x), purchaseStorageDetailsVO);

            if (purchaseStorageDetailsVOs.size()>0){
               purchaseStorageDetails.setImportFlag("0");
               purchaseStorageDetails.setAssociationId(Integer.parseInt(associationId));
               purchaseStorageDetails.setImportRemark("??????????????????:"+purchaseStorageDetailsExcleList.get(x).getDeliveryNo());
               purchaseStorageDetailsMapper.insertSelectiveus(purchaseStorageDetails);
               failNumber=failNumber+1;
           }
           if (purchaseStorageDetailsVOs.size()==0){
               if (purchaseStorageDetailsVO.getDeliveryNo()!=null&&purchaseStorageDetailsVO.getDeclareQty()!=null){
                   purchaseStorageDetailsList1.add(purchaseStorageDetailsVO);
                   successNumber=successNumber+1;
               }


           }


        }

        purchaseInfoVO.setPurchaseStorageDetailsAddList(purchaseStorageDetailsList1);

        PurchaseAddDTO  purchaseAddDTO=new PurchaseAddDTO();

        BeanUtils.copyProperties(purchaseInfoVO, purchaseAddDTO);

        List<PurchaseDetailsAddDTO> purchaseDetailsAddDTOS=new ArrayList<>();
        List<PurchaseStorageDetailsAddDTO> purchaseStorageDetailsAddDTOS=new ArrayList<>();
        purchaseInfoVO.getPurchaseDetailsAddList().forEach(x->{
            PurchaseDetailsAddDTO purchaseDetailsAddDTO=new PurchaseDetailsAddDTO();
            BeanUtils.copyProperties(x, purchaseDetailsAddDTO);
            purchaseDetailsAddDTOS.add(purchaseDetailsAddDTO);
        });
        purchaseAddDTO.setPurchaseDetailsAddList(purchaseDetailsAddDTOS);
        purchaseInfoVO.getPurchaseStorageDetailsAddList().forEach(x->{
            PurchaseStorageDetailsAddDTO purchaseStorageDetailsAddDTO=new PurchaseStorageDetailsAddDTO();
            BeanUtils.copyProperties(x, purchaseStorageDetailsAddDTO);
            purchaseStorageDetailsAddDTOS.add(purchaseStorageDetailsAddDTO);
        });
        purchaseAddDTO.setPurchaseStorageDetailsAddList(purchaseStorageDetailsAddDTOS);
        String orderNo=purchaseInfoVO.getOrderNo();
        orderNo = StringEscapeUtils.unescapeHtml4(orderNo);
        orderNo = orderNo.substring(1,orderNo .length()-1);
        orderNo = orderNo .replaceAll("\"", "");
        String[] orderNos = orderNo .split(",");
        List<String> orderNolist = Arrays.asList(orderNos);
        purchaseAddDTO.setOrderNo(orderNolist);
        this.insertPurchaseBatch(purchaseAddDTO);

        Map map=new HashMap();
        map.put("totalNumber",totalNumber);
        map.put("successNumber",successNumber);
        map.put("failNumber",failNumber);
        return  map;
    }


    @Override
    public List<PurchaseInfoDetailExcleep> exportusAbnormal(Integer id) {
        List<PurchaseInfoDetailExcleep> list=purchaseDetailsMapper.selectPurchaseInfoDetailExcleListByAssIdep(id);
        list.forEach(x->{
            List<PurchaseStorageDetailsExclesp> list1= purchaseStorageDetailsMapper.selectPurchaseStorageDetailsExcleListByAssIdsp(x.getSku(),id);
            x.setPurchaseStorageDetailsExclesp(list1);
        });
        return list;
    }

    @Override
    public void deletePurchaseStorageDetails(Integer id) {
        purchaseStorageDetailsMapper.deletePurchaseStorageDetails(id);
    }
}

