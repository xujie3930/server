package com.szmsd.putinstorage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.ObjectMapperUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.http.api.feign.HtpRmiFeignService;
import com.szmsd.http.api.feign.YcMeetingFeignService;
import com.szmsd.http.config.CkConfig;
import com.szmsd.http.config.CkThreadPool;
import com.szmsd.http.domain.YcAppParameter;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.HttpRequestSyncDTO;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.enums.RemoteConstant;
import com.szmsd.http.util.DomainInterceptorUtil;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.HttpResponseVO;
import com.szmsd.inventory.api.feign.InventoryInspectionFeignService;
import com.szmsd.inventory.domain.dto.InboundInventoryInspectionDTO;
import com.szmsd.pack.api.feign.PackageCollectionFeignService;
import com.szmsd.pack.domain.PackageCollection;
import com.szmsd.pack.domain.PackageCollectionDetail;
import com.szmsd.putinstorage.api.dto.CkCreateIncomingOrderDTO;
import com.szmsd.putinstorage.api.dto.CkGenCustomSkuNoDTO;
import com.szmsd.putinstorage.api.dto.CkPutawayDTO;
import com.szmsd.putinstorage.component.CheckTag;
import com.szmsd.putinstorage.component.RemoteComponent;
import com.szmsd.putinstorage.component.RemoteRequest;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.domain.InboundReceiptDetail;
import com.szmsd.putinstorage.domain.InboundTracking;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.*;
import com.szmsd.putinstorage.enums.InboundReceiptEnum;
import com.szmsd.putinstorage.mapper.InboundReceiptDetailMapper;
import com.szmsd.putinstorage.mapper.InboundReceiptMapper;
import com.szmsd.putinstorage.service.IInboundReceiptDetailService;
import com.szmsd.putinstorage.service.IInboundReceiptService;
import com.szmsd.putinstorage.service.IInboundTrackingService;
import com.szmsd.putinstorage.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class InboundReceiptServiceImpl extends ServiceImpl<InboundReceiptMapper, InboundReceipt> implements IInboundReceiptService {

    @Value("${file.mainUploadFolder}")
    private String mainUploadFolder;

    @Resource
    private RemoteRequest remoteRequest;

    @Resource
    private IInboundReceiptDetailService iInboundReceiptDetailService;

    @Resource
    private RemoteComponent remoteComponent;

    @Resource
    private InventoryInspectionFeignService inventoryInspectionFeignService;

    @Resource
    private IInboundTrackingService iInboundTrackingService;

    @Resource
    private RemoteAttachmentService remoteAttachmentService;

    @Resource
    private OperationFeignService operationFeignService;

    @Resource
    private RechargesFeignService rechargesFeignService;
    @Resource
    private HtpRmiFeignService htpRmiFeignService;
    @Resource
    private CkThreadPool ckThreadPool;
    @Resource
    private CkConfig ckConfig;
    @Resource
    private PackageCollectionFeignService packageCollectionFeignService;
    @Resource
    private DelOutboundClientService delOutboundClientService;
    @Autowired
    private InboundReceiptDetailMapper inboundReceiptDetailMapper;
    @Autowired
    private BasWarehouseFeignService basWarehouseFeignService;

    private String ycurl="http://pgl.yunwms.com/default/svc/web-service";

    /**
     * ???????????????
     *
     * @param queryDTO
     * @return
     */
    @Override
    public List<InboundReceiptVO> selectList(InboundReceiptQueryDTO queryDTO) {
        String warehouseNo = queryDTO.getWarehouseNo();
        if (StringUtils.isNotEmpty(warehouseNo)) {
            List<String> warehouseNoSplit = Arrays.asList(warehouseNo.split(","));
            List<String> warehouseNoList = ListUtils.emptyIfNull(queryDTO.getWarehouseNoList());
            queryDTO.setWarehouseNoList(Stream.of(warehouseNoSplit, warehouseNoList).flatMap(Collection::stream).distinct().collect(Collectors.toList()));
        }
        String orderNo = queryDTO.getOrderNo();
        if (StringUtils.isNotEmpty(orderNo)) {
            List<String> orderNoSplit = Arrays.asList(orderNo.split(","));
            List<String> orderNoList = ListUtils.emptyIfNull(queryDTO.getOrderNoList());
            queryDTO.setOrderNoList(Stream.of(orderNoSplit, orderNoList).flatMap(Collection::stream).distinct().collect(Collectors.toList()));
        }
        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
            if (StringUtils.isEmpty(queryDTO.getCusCode())) {
                queryDTO.setCusCode(cusCode);
            }
        }
        if (StringUtils.isNoneEmpty(queryDTO.getDeliveryNousD())){
            queryDTO.setDeliveryNousD(new StringBuilder("%").append(queryDTO.getDeliveryNousD()).append("%").toString());
        }

        List<String> otherQueryNoList = new ArrayList<>();
        if (StringUtils.isNotEmpty(queryDTO.getOrderNos())) {
            List<String> nos = splitToArray(queryDTO.getOrderNos(), "[\n,]");
            if (CollectionUtils.isNotEmpty(nos)) {
                for (String no : nos) {
                    otherQueryNoList.add(no);
                }


            }
            queryDTO.setOrderNosList(otherQueryNoList);

        }
        return baseMapper.selectListByCondiction(queryDTO);
    }

    public static List<String> splitToArray(String text, String split) {
        String[] arr = text.split(split);
        if (arr.length == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (com.szmsd.common.core.utils.StringUtils.isEmpty(s)) {
                continue;
            }
            list.add(s);
        }
        return list;
    }

    /**
     * ??????????????????
     *
     * @param warehouseNo
     * @return
     */
    @Override
    public InboundReceiptVO selectByWarehouseNo(String warehouseNo) {
        List<InboundReceiptVO> inboundReceiptVOS = this.selectList(new InboundReceiptQueryDTO().setWarehouseNo(warehouseNo));
        if (CollectionUtils.isNotEmpty(inboundReceiptVOS)) {
            return inboundReceiptVOS.get(0);
        }
        return null;
    }

    /**
     * ??????????????????????????????
     */
    private void packageTransferCheck(CreateInboundReceiptDTO createInboundReceiptDTO) {
        if (CheckTag.get()) {
            log.info("???????????????????????????????????????");
            List<InboundReceiptDetailDTO> inboundReceiptDetails = createInboundReceiptDTO.getInboundReceiptDetails();
            if (CollectionUtils.isNotEmpty(inboundReceiptDetails) && null == createInboundReceiptDTO.getId()) {
                String deliveryNo = Optional.ofNullable(inboundReceiptDetails.get(0)).map(InboundReceiptDetailDTO::getDeliveryNo).orElse("");
//                Integer integer = iInboundReceiptDetailService.getBaseMapper().selectCount(Wrappers.<InboundReceiptDetail>lambdaQuery().eq(InboundReceiptDetail::getDeliveryNo, deliveryNo));
                int count = iInboundReceiptDetailService.checkPackageTransfer(deliveryNo);
                AssertUtil.isTrue(count == 0, "???????????????????????????????????????!");
            }
        }
    }

    /**
     * ????????????????????????sku
     *
     * @param createInboundReceiptDTO
     */
    public void checkSkuPic(CreateInboundReceiptDTO createInboundReceiptDTO) {
        if (!"055003".equals(createInboundReceiptDTO.getWarehouseMethodCode())) return;
        List<String> skuList = createInboundReceiptDTO.getInboundReceiptDetails().stream().map(InboundReceiptDetailDTO::getSku).distinct().collect(Collectors.toList());
        BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
        basAttachmentQueryDTO.setBusinessNoList(skuList);
        basAttachmentQueryDTO.setAttachmentType(AttachmentTypeEnum.SKU_IMAGE.getAttachmentType());
        R<List<BasAttachment>> list = remoteAttachmentService.list(basAttachmentQueryDTO);
        List<BasAttachment> dataAndException = R.getDataAndException(list);
        List<String> collect = dataAndException.stream().map(BasAttachment::getBusinessNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        boolean b = skuList.removeAll(collect);
        AssertUtil.isTrue(CollectionUtils.isEmpty(skuList), String.format("???????????? SKU???????????????%s SKU???????????????", skuList));
    }

    /**
     * ???????????????
     * ?????????????????? OMS????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param createInboundReceiptDTO
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public InboundReceiptInfoVO saveOrUpdate(CreateInboundReceiptDTO createInboundReceiptDTO) {
        log.info("??????????????????{}", createInboundReceiptDTO);
        CheckTag.set(createInboundReceiptDTO.getOrderType());
        packageTransferCheck(createInboundReceiptDTO);
        Integer totalDeclareQty = createInboundReceiptDTO.getTotalDeclareQty();
        AssertUtil.isTrue(totalDeclareQty > 0, "???????????????????????????" + totalDeclareQty);
        //???????????? ??????????????????
        this.checkSkuPic(createInboundReceiptDTO);
        // ???????????????
        InboundReceipt inboundReceipt = this.saveOrUpdate((InboundReceiptDTO) createInboundReceiptDTO);
        String warehouseNo = inboundReceipt.getWarehouseNo();
        createInboundReceiptDTO.setWarehouseNo(warehouseNo);
        //????????????????????????
        List<String> deliveryNoList = createInboundReceiptDTO.getDeliveryNoList();
        checkDeliveryNoRepeat(createInboundReceiptDTO.getId(), warehouseNo, deliveryNoList);
        // ?????????????????????
        List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS = createInboundReceiptDTO.getInboundReceiptDetails();
        inboundReceiptDetailDTOS.forEach(item -> item.setWarehouseNo(warehouseNo));

        iInboundReceiptDetailService.saveOrUpdate(inboundReceiptDetailDTOS, createInboundReceiptDTO.getReceiptDetailIds());

        //?????????????????????
        BasWarehouse basWarehouse= basWarehouseFeignService.queryByWarehouseCode(inboundReceipt.getWarehouseCode()).getData();
        if (basWarehouse.getWarehouseSource()!=null&&basWarehouse.getWarehouseSource().equals("YC")){
            //??????????????????
            YcAppParameter ycAppParameter=new YcAppParameter();
            ycAppParameter.setAppKey(basWarehouse.getAppKey());
            ycAppParameter.setYcUrl(ycurl);
            ycAppParameter.setAppToken(basWarehouse.getAppToken());
            ycAppParameter.setService("createAsn");
            JSONObject jsonObject=YcInboundJson(inboundReceipt,inboundReceiptDetailDTOS,basWarehouse);
            ycAppParameter.setJsonObject(jsonObject);
            log.info("???????????????json?????????{}",jsonObject);
            YcMeetingFeignService ycMeetingFeignService= SpringUtils.getBean(YcMeetingFeignService.class);
          R<Map>  r= ycMeetingFeignService.YcApiri(ycAppParameter);
          Map mapsr=r.getData();
            log.info("????????????????????????????????????{}",r.getData());
           //????????????????????????
            if (mapsr.get("ask").equals("Success")){
                String date=String.valueOf(mapsr.get("data"));
                Map maps = mapStringToMap(date);
                inboundReceipt.setYcWarehouseNo(String.valueOf(maps.get("receiving_code")));
                baseMapper.upadateYcWarehouseNo(inboundReceipt);
            }

        }

        boolean isPackageTransfer = InboundReceiptEnum.OrderType.PACKAGE_TRANSFER.getValue().equals(createInboundReceiptDTO.getOrderType());
        // ??????????????????
        boolean inboundReceiptReview;
        // ??????????????????
        if (isPackageTransfer) {
            log.info("---?????????????????????---");
            inboundReceiptReview = true;
        } else {
            inboundReceiptReview = remoteComponent.inboundReceiptReview(createInboundReceiptDTO.getWarehouseCode());
        }

        if (inboundReceiptReview) {
            // ?????? ?????????????????????
            String localLanguage = LocalLanguageEnum.getLocalLanguageSplice(LocalLanguageEnum.INBOUND_RECEIPT_REVIEW_0);
            this.review(new InboundReceiptReviewDTO().setWarehouseNos(Arrays.asList(warehouseNo)).setStatus(InboundReceiptEnum.InboundReceiptStatus.REVIEW_PASSED.getValue()).setReviewRemark(localLanguage));

        }
        InboundReceiptInfoVO inboundReceiptInfoVO = this.queryInfo(warehouseNo, false);
        if (isPackageTransfer) {
            List<String> transferNoList = createInboundReceiptDTO.getTransferNoList();
            // ???????????????
            remoteRequest.createPackage(inboundReceiptInfoVO, transferNoList);
            // ?????? ?????????????????????????????????
            remoteComponent.createTracking(createInboundReceiptDTO);
        }

        log.info("??????????????????????????????");
        return inboundReceiptInfoVO;
    }

    private  Map<String, String> mapStringToMap(String str) {
        str = str.substring(1, str.length() - 1);
        String[] strs = str.split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (String string : strs) {
            String key = string.split("=")[0].trim();
            String value = string.split("=")[1];
            map.put(key, value);
        }
        return map;
    }

    private JSONObject YcInboundJson(InboundReceipt inboundReceipt, List<InboundReceiptDetailDTO> inboundReceiptDetailDTOS,BasWarehouse basWarehouse) {
        JSONObject jsonObject =new JSONObject();

        jsonObject.put("reference_no",inboundReceipt.getWarehouseNo());
        jsonObject.put("warehouse_code",inboundReceipt.getWarehouseCode());
         List<Map> list=new ArrayList<>();
        inboundReceiptDetailDTOS.forEach(x->{
            BaseProduct baseProduct=new BaseProduct();
            baseProduct.setCode(x.getSku());
            R<BaseProduct> r= baseProductFeignService.getSku(baseProduct);
            if (r.getData().getSkuSource()==null||!r.getData().getSkuSource().equals("YC")){
                //??????????????????(??????sku)
                YcAppParameter ycAppParameter=new YcAppParameter();
                ycAppParameter.setAppKey(basWarehouse.getAppKey());
                ycAppParameter.setAppToken(basWarehouse.getAppToken());
                ycAppParameter.setYcUrl(ycurl);
                ycAppParameter.setService("createProduct");
                JSONObject jsonObject1=new JSONObject();
                jsonObject1=createProductJson(r.getData());
                ycAppParameter.setJsonObject(jsonObject1);
                YcMeetingFeignService ycMeetingFeignService= SpringUtils.getBean(YcMeetingFeignService.class);
                R<Map>  r1= ycMeetingFeignService.YcApiri(ycAppParameter);
                if (r1.getData().get("ask").equals("Success")){
                    //??????sku???????????????
                    r.getData().setSkuSource("YC");
                    r.getData().setAppKey(basWarehouse.getAppKey());
                    r.getData().setAppToken(basWarehouse.getAppToken());
                    baseMapper.upadateSkuSource(r.getData());
                }

            }
           Map map1=new HashMap();
            map1.put("product_sku",x.getSku());
            map1.put("quantity",x.getDeclareQty());
            map1.put("box_no",x.getId());
            list.add(map1);
        });
        jsonObject.put("items",list);
        return  jsonObject;
    }

    private JSONObject createProductJson(BaseProduct baseProduct) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("product_sku",baseProduct.getCode());
        jsonObject.put("product_title",baseProduct.getProductName());
        jsonObject.put("product_weight",baseProduct.getWeight());
        jsonObject.put("product_length",baseProduct.getLength());
        jsonObject.put("product_width",baseProduct.getWidth());
        jsonObject.put("product_height",baseProduct.getHeight());
        jsonObject.put("product_declared_value",baseProduct.getDeclaredValue());
        jsonObject.put("product_declared_name",baseProduct.getProductName());
        jsonObject.put("product_declared_name_zh",baseProduct.getProductNameChinese());
        jsonObject.put("hs_code",baseProduct.getHsCode());

       return jsonObject;
    }

    /**
     * ????????????????????????
     *
     * @param
     * @param warehouseNo
     * @param deliveryNoList
     */
    private void checkDeliveryNoRepeat(Long id, String warehouseNo, List<String> deliveryNoList) {
        if (CollectionUtils.isNotEmpty(deliveryNoList)) {
            LambdaQueryWrapper<InboundReceipt> in = Wrappers.<InboundReceipt>lambdaQuery().ne(id != null, InboundReceipt::getId, id).ne(InboundReceipt::getWarehouseNo, warehouseNo).ne(InboundReceipt::getStatus, InboundReceiptEnum.InboundReceiptStatus.CANCELLED.getValue());
            String join = String.join(",", deliveryNoList);
            in.and(x -> x.apply("CONCAT(',',delivery_no,',') REGEXP(SELECT CONCAT(',',REPLACE({0}, ',', ',|,'),','))", join));
            List<InboundReceipt> inboundReceipts = baseMapper.selectList(in);
            String errorMsg = inboundReceipts.stream().map(x -> x.getWarehouseNo() + ":" + x.getDeliveryNo()).collect(Collectors.joining("\n", "?????????????????????", ""));
            AssertUtil.isTrue(CollectionUtils.isEmpty(inboundReceipts), errorMsg);
            List<InboundTracking> inboundTrackings = iInboundTrackingService.getBaseMapper().selectList(Wrappers.<InboundTracking>lambdaQuery().ne(InboundTracking::getOrderNo, warehouseNo)
                    .in(InboundTracking::getTrackingNumber, deliveryNoList).select(InboundTracking::getTrackingNumber, InboundTracking::getOrderNo));
            String errorMsg2 = inboundTrackings.stream().map(x -> x.getOrderNo() + ":" + x.getTrackingNumber()).collect(Collectors.joining("\n", "????????????????????????", ""));
            AssertUtil.isTrue(CollectionUtils.isEmpty(inboundTrackings), errorMsg2);
        }
    }

    @Resource
    private HttpServletRequest httpServletRequest;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTrackingNo(UpdateTrackingNoRequest updateTrackingNoRequest) {


        

        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            Enumeration<String> values = httpServletRequest.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                log.info(name + "--" + value);
            }
        }

        String sellerCode = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse(null);
        String sellerCode1 = updateTrackingNoRequest.getSellerCode();
        if (StringUtils.isNotBlank(sellerCode1)) sellerCode = sellerCode1;

        String warehouseNo = updateTrackingNoRequest.getWarehouseNo();
        List<String> deliveryNoList = updateTrackingNoRequest.getDeliveryNoList();
        InboundReceipt inboundReceipt = baseMapper.selectOne(Wrappers.<InboundReceipt>lambdaQuery()
                .eq(InboundReceipt::getWarehouseNo, warehouseNo)
                .eq(StringUtils.isNotBlank(sellerCode), InboundReceipt::getCusCode, sellerCode));
        AssertUtil.isTrue(inboundReceipt != null, "??????????????????!");
        this.checkDeliveryNoRepeat(inboundReceipt.getId(), warehouseNo, deliveryNoList);
        AssertUtil.isTrue(!inboundReceipt.getStatus().equals(InboundReceiptEnum.InboundReceiptStatus.CANCELLED.getValue()), "??????????????????!");
        AssertUtil.isTrue(!inboundReceipt.getStatus().equals(InboundReceiptEnum.InboundReceiptStatus.COMPLETED.getValue()), "??????????????????!");

        //?????????????????????
        BasWarehouse basWarehouse= basWarehouseFeignService.queryByWarehouseCode(inboundReceipt.getWarehouseCode()).getData();
        if (basWarehouse.getWarehouseSource()!=null&&basWarehouse.getWarehouseSource().equals("YC")){
            //??????????????????
            YcAppParameter ycAppParameter=new YcAppParameter();
            ycAppParameter.setAppKey(basWarehouse.getAppKey());
            ycAppParameter.setAppToken(basWarehouse.getAppToken());
            ycAppParameter.setYcUrl(ycurl);
            ycAppParameter.setService("updateAsnTracking");
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("receiving_code",inboundReceipt.getYcWarehouseNo());
            jsonObject.put("tracking_number",updateTrackingNoRequest.getDeliveryNo());
            ycAppParameter.setJsonObject(jsonObject);
            YcMeetingFeignService ycMeetingFeignService= SpringUtils.getBean(YcMeetingFeignService.class);
            R<Map>  r= ycMeetingFeignService.YcApiri(ycAppParameter);


        }

        int update = baseMapper.update(new InboundReceipt(), Wrappers.<InboundReceipt>lambdaUpdate()
                .eq(InboundReceipt::getWarehouseNo, warehouseNo)
                .set(InboundReceipt::getDeliveryNo, updateTrackingNoRequest.getDeliveryNo()));
        AssertUtil.isTrue(update == 1, "????????????");
        log.info("??????????????????-{} -{}???", updateTrackingNoRequest, update);
        remoteComponent.createTracking(updateTrackingNoRequest, inboundReceipt);
        return update;
    }

    /**
     * ?????????????????????
     *
     * @param inboundReceiptDTO
     * @return
     */
    @Override
    public InboundReceipt saveOrUpdate(InboundReceiptDTO inboundReceiptDTO) {
        log.info("??????????????????{}", inboundReceiptDTO);
        InboundReceipt inboundReceipt = null;

        try{
            inboundReceipt = BeanMapperUtil.map(inboundReceiptDTO, InboundReceipt.class);
            // ??????????????????
            String warehouseNo = inboundReceipt.getWarehouseNo();
            if (StringUtils.isEmpty(warehouseNo)) {
                warehouseNo = remoteComponent.getWarehouseNo(inboundReceiptDTO.getCusCode());
            }
            inboundReceipt.setWarehouseNo(warehouseNo);
            inboundReceiptDTO.setOrderNo(warehouseNo);
            this.saveOrUpdate(inboundReceipt);

            // ????????????
            asyncAttachment(warehouseNo, inboundReceiptDTO);

            log.info("??????????????????????????????");
        }catch (Exception e){
            log.info("????????????????????????"+e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return inboundReceipt;
    }

    /**
     * ??????
     *
     * @param warehouseNo
     */
    @Override
    public void cancel(String warehouseNo) {
        log.info("??????????????????warehouseNo={}", warehouseNo);

        InboundReceiptVO inboundReceiptVO = this.selectByWarehouseNo(warehouseNo);
        AssertUtil.notNull(inboundReceiptVO, "?????????[" + warehouseNo + "]?????????");

        //?????????????????????
        BasWarehouse basWarehouse= basWarehouseFeignService.queryByWarehouseCode(inboundReceiptVO.getWarehouseCode()).getData();
        if (basWarehouse.getWarehouseSource()!=null&&basWarehouse.getWarehouseSource().equals("YC")){
            //??????????????????
            YcAppParameter ycAppParameter=new YcAppParameter();
            ycAppParameter.setAppKey(basWarehouse.getAppKey());
            ycAppParameter.setYcUrl(ycurl);
            ycAppParameter.setAppToken(basWarehouse.getAppToken());
            ycAppParameter.setService("cancelAsn");
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("receiving_code",inboundReceiptVO.getYcWarehouseNo());
            ycAppParameter.setJsonObject(jsonObject);
            YcMeetingFeignService ycMeetingFeignService= SpringUtils.getBean(YcMeetingFeignService.class);
            R<Map>  r= ycMeetingFeignService.YcApiri(ycAppParameter);
             Map maps=r.getData();

        }

        /** ????????????????????????????????????3????????????????????????????????? **/
        String status = inboundReceiptVO.getStatus();
        if (InboundReceiptEnum.InboundReceiptStatus.REVIEW_PASSED.getValue().equals(status)
                || InboundReceiptEnum.InboundReceiptStatus.PROCESSING.getValue().equals(status)
                || InboundReceiptEnum.InboundReceiptStatus.COMPLETED.getValue().equals(status)) {
            // ?????????????????????
            remoteRequest.cancelInboundReceipt(inboundReceiptVO.getWarehouseNo(), inboundReceiptVO.getWarehouseName());
        }

        // ?????????????????????
        this.updateStatus(warehouseNo, InboundReceiptEnum.InboundReceiptStatus.CANCELLED);

        //????????? ???????????????????????????????????????
        DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
        delOutboundOperationVO.setOrderNo(warehouseNo);
        operationFeignService.delOutboundThaw(delOutboundOperationVO);
        log.info("??????????????????????????????");
    }

    /**
     * ???????????????
     *
     * @param warehouseNo
     * @return
     */
    @Override
    public InboundReceiptInfoVO queryInfo(String warehouseNo) {
        InboundReceiptInfoVO inboundReceiptInfoVO = queryInfo(warehouseNo, true);
        if (inboundReceiptInfoVO != null) {
            String deliveryNo = inboundReceiptInfoVO.getDeliveryNo();
            List<String> codeByArray = Optional.ofNullable(StringToolkit.getCodeByArray(deliveryNo)).orElse(new ArrayList<>());
            // ?????????????????? ??????????????????
            List<InboundTracking> inboundTrackings = iInboundTrackingService.selectInboundTrackingList(new InboundTracking().setOrderNo(warehouseNo));
            Map<String, InboundTracking> collect1 = inboundTrackings.stream().filter(x -> StringUtils.isNotBlank(x.getTrackingNumber())).collect(Collectors.toMap(InboundTracking::getTrackingNumber, x -> x));
            codeByArray.addAll(collect1.keySet());
            if (CollectionUtils.isNotEmpty(codeByArray)) {
                codeByArray = codeByArray.stream().distinct().collect(Collectors.toList());
                List<InboundTrackingVO> collect = codeByArray.stream()
                        .map(x -> {
                            InboundTrackingVO inboundTrackingVO = new InboundTrackingVO();
                            inboundTrackingVO.setTrackingNumber(x);
                            inboundTrackingVO.setOrderNo(warehouseNo);
                            InboundTracking inboundTracking = collect1.get(x);
                            if (null != inboundTracking) {
                                inboundTrackingVO.setArrivalStatus("1");
                                inboundTrackingVO.setOperateOn(inboundTracking.getOperateOn());
                            }
                            return inboundTrackingVO;
                        }).collect(Collectors.toList());
                inboundReceiptInfoVO.setInboundTrackingList(collect);
            }
        }
        return inboundReceiptInfoVO;
    }

    /**
     * ???????????????
     *
     * @param warehouseNo
     * @param isContainFile ????????????????????????
     * @return
     */
    public InboundReceiptInfoVO queryInfo(String warehouseNo, boolean isContainFile) {
        InboundReceiptInfoVO inboundReceiptInfoVO = baseMapper.selectInfo(null, warehouseNo);
        if (inboundReceiptInfoVO == null) {
            return null;
        }
        // ?????????
        List<InboundReceiptDetailVO> inboundReceiptDetailVOS = iInboundReceiptDetailService.selectList(new InboundReceiptDetailQueryDTO().setWarehouseNo(warehouseNo), isContainFile);
        inboundReceiptInfoVO.setInboundReceiptDetails(inboundReceiptDetailVOS);
        return inboundReceiptInfoVO;
    }

    /**
     * #B1 ?????????????????? ??????????????????
     *
     * @param receivingRequest
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void receiving(ReceivingRequest receivingRequest) {
        LoginUser loginUser= SecurityUtils.getLoginUser();
        InboundReceiptDetailQueryDTO inboundReceiptDetailQueryDTO=new InboundReceiptDetailQueryDTO();
        inboundReceiptDetailQueryDTO.setWarehouseNo(receivingRequest.getOrderNo());
        inboundReceiptDetailQueryDTO.setSku(receivingRequest.getSku());
        List<InboundReceiptDetailVO> inboundReceiptDetailVOSlist= inboundReceiptDetailMapper.selectList(inboundReceiptDetailQueryDTO);

        //?????????????????????sku????????? ????????????????????????
        if (inboundReceiptDetailVOSlist.size()==0){
            InboundReceiptDetail inboundReceiptDetail=new InboundReceiptDetail();
            inboundReceiptDetail.setSku(receivingRequest.getSku());
            inboundReceiptDetail.setWarehouseNo(receivingRequest.getOrderNo());
            inboundReceiptDetail.setPutQty(receivingRequest.getQty());
            inboundReceiptDetail.setSkuName(receivingRequest.getSku());
            inboundReceiptDetail.setCreateBy(receivingRequest.getOperator());
            inboundReceiptDetail.setCreateByName(receivingRequest.getOperator());
            inboundReceiptDetailMapper.insert(inboundReceiptDetail);

        }
        //?????????????????????sku????????? ???????????????
        if (inboundReceiptDetailVOSlist.size()>0) {
            log.info("#B1 ?????????????????????{}", receivingRequest);

            Integer qty = receivingRequest.getQty();
            AssertUtil.isTrue(qty != null && qty > 0, "?????????????????????" + qty);

            // ???????????????????????????????????????
            String refOrderNo = receivingRequest.getOrderNo();
            InboundReceiptVO inboundReceiptVO = selectByWarehouseNo(refOrderNo);
            AssertUtil.notNull(inboundReceiptVO, "????????????[" + refOrderNo + "]?????????????????????");
            // ?????????????????????
            String cusCode = inboundReceiptVO.getCusCode();
            receivingRequest.setWarehouseCode(inboundReceiptVO.getWarehouseCode());
            Integer beforeTotalPutQty = inboundReceiptVO.getTotalPutQty();
            InboundReceipt inboundReceipt = new InboundReceipt().setId(inboundReceiptVO.getId());
            inboundReceipt.setTotalPutQty(beforeTotalPutQty + qty);
            // ????????????????????? ?????????????????? 3?????????
            if (beforeTotalPutQty == 0) {
                inboundReceipt.setStatus(InboundReceiptEnum.InboundReceiptStatus.PROCESSING.getValue());

                // ?????????????????????
                // OMS????????????????????????????????????????????????????????????????????????????????????????????????????????????
                CompletableFuture<InboundReceiptVO> future = CompletableFuture
                        .supplyAsync(() -> queryInfo(refOrderNo, false))
                        .thenApplyAsync(inboundReceiptInfoDetailVO -> {
                            List<InboundReceiptDetailVO> inboundReceiptDetails = inboundReceiptInfoDetailVO.getInboundReceiptDetails();
                            for (InboundReceiptDetailVO inboundReceiptDetail : inboundReceiptDetails) {
                                HttpRequestSyncDTO httpRequestDto = new HttpRequestSyncDTO();
                                httpRequestDto.setMethod(HttpMethod.GET);
                                httpRequestDto.setBinary(false);
                                if (loginUser!=null){
                                    httpRequestDto.setUserName(loginUser.getUsername());
                                }
                                httpRequestDto.setHeaders(DomainInterceptorUtil.genSellerCodeHead(inboundReceiptInfoDetailVO.getCusCode()));
                                httpRequestDto.setUri(DomainEnum.Ck1OpenAPIDomain.wrapper(ckConfig.getGenSkuCustomStorageNo()));
                                httpRequestDto.setBody(CkGenCustomSkuNoDTO.createGenCustomSkuNoDTO(inboundReceiptInfoDetailVO, inboundReceiptDetail));
                                // ???????????????sku??????,???????????????sku?????????????????????
                                httpRequestDto.setRemoteTypeEnum(RemoteConstant.RemoteTypeEnum.SKU_ON_SELL);
                                R<HttpResponseVO> rmi = htpRmiFeignService.rmiSync(httpRequestDto);
                                log.info("?????????CK1???????????????????????????,???????????????????????????????????????{} ?????? {}", httpRequestDto, JSONObject.toJSONString(rmi));
                                HttpResponseVO dataAndException = R.getDataAndException(rmi);
                            }
                            return inboundReceiptInfoDetailVO;
                        }, ckThreadPool).thenApplyAsync(inboundReceiptInfoDetailVO -> {
                            HttpRequestSyncDTO httpRequestDto = new HttpRequestSyncDTO();
                            httpRequestDto.setMethod(HttpMethod.POST);
                            httpRequestDto.setBinary(false);
                            if (loginUser!=null){
                                httpRequestDto.setUserName(loginUser.getUsername());
                            }
                            httpRequestDto.setHeaders(DomainInterceptorUtil.genSellerCodeHead(inboundReceiptInfoDetailVO.getCusCode()));
                            httpRequestDto.setUri(DomainEnum.Ck1OpenAPIDomain.wrapper(ckConfig.getCreatePutawayOrderUrl()));
                            httpRequestDto.setBody(CkCreateIncomingOrderDTO.createIncomingOrderDTO(inboundReceiptInfoDetailVO));
                            // ???????????????sku??????,???????????????sku?????????????????????
                            httpRequestDto.setRemoteTypeEnum(RemoteConstant.RemoteTypeEnum.SKU_ON_SELL);
                            R<HttpResponseVO> rmi = htpRmiFeignService.rmiSync(httpRequestDto);
                            log.info("?????????CK1???????????????????????????,???????????????{} ?????? {}", httpRequestDto, JSONObject.toJSONString(rmi));
                            HttpResponseVO dataAndException = R.getDataAndException(rmi);
                            //dataAndException.checkStatus();
                            return null;
                        }, ckThreadPool);
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
            this.updateById(inboundReceipt);

            // ????????????????????????
            iInboundReceiptDetailService.receiving(receivingRequest.getOrderNo(), receivingRequest.getSku(), receivingRequest.getQty());

            // ?????? ????????????
            remoteComponent.inboundInventory(receivingRequest.setWarehouseCode(inboundReceiptVO.getWarehouseName()));

            log.info("#B1 ?????????????????????????????????");
            // ??????ck1 ????????????
            CompletableFuture<HttpRequestDto> httpRequestDtoCompletableFuture = CompletableFuture.supplyAsync(() -> {
                HttpRequestSyncDTO httpRequestDto = new HttpRequestSyncDTO();
                httpRequestDto.setMethod(HttpMethod.POST);
                httpRequestDto.setBinary(false);
                httpRequestDto.setHeaders(DomainInterceptorUtil.genSellerCodeHead(cusCode));
                if (loginUser!=null){
                    httpRequestDto.setUserName(loginUser.getUsername());
                }
                httpRequestDto.setUri(DomainEnum.Ck1OpenAPIDomain.wrapper(ckConfig.getPutawayUrl()));
                httpRequestDto.setBody(CkPutawayDTO.createCkPutawayDTO(receivingRequest, cusCode));
                httpRequestDto.setRemoteTypeEnum(RemoteConstant.RemoteTypeEnum.SKU_ON_SELL);
                R<HttpResponseVO> rmi = htpRmiFeignService.rmiSync(httpRequestDto);
                log.info("?????????CK1???????????????????????????,????????????SKU?????? {} ?????? {}", httpRequestDto, JSONObject.toJSONString(rmi));
                HttpResponseVO dataAndException = R.getDataAndException(rmi);
                //dataAndException.checkStatus();
                return httpRequestDto;
            }, ckThreadPool);
            try {
                HttpRequestDto httpRequestDto = httpRequestDtoCompletableFuture.get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Resource
    private BaseProductFeignService baseProductFeignService;


    /**
     * #B3 ??????????????????
     *
     * @param receivingCompletedRequest
     */
    @Override
    public void completed(ReceivingCompletedRequest receivingCompletedRequest) {

        LoginUser loginUser=SecurityUtils.getLoginUser();
        log.info("#B3 ?????????????????????{}", receivingCompletedRequest);
        String orderNo = receivingCompletedRequest.getOrderNo();
        updateStatus(orderNo, InboundReceiptEnum.InboundReceiptStatus.COMPLETED);
        //?????????????????????????????????code
        final InboundReceiptInfoVO inboundReceiptInfoVO = this.queryInfo(orderNo);
        log.info("#B3 ?????????????????????????????????");
        CompletableFuture<HttpRequestDto> httpRequestDtoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            HttpRequestSyncDTO httpRequestDto = new HttpRequestSyncDTO();
            httpRequestDto.setMethod(HttpMethod.PUT);
            httpRequestDto.setBinary(false);
            if (loginUser!=null){
                httpRequestDto.setUserName(loginUser.getUsername());
            }
            httpRequestDto.setHeaders(DomainInterceptorUtil.genSellerCodeHead(inboundReceiptInfoVO.getCusCode()));
            httpRequestDto.setUri(DomainEnum.Ck1OpenAPIDomain.wrapper(ckConfig.getIncomingOrderCompletedUrl(orderNo)));
            httpRequestDto.setRemoteTypeEnum(RemoteConstant.RemoteTypeEnum.WAREHOUSE_ORDER_COMPLETED);
            R<HttpResponseVO> rmi = htpRmiFeignService.rmiSync(httpRequestDto);
            log.info("?????????CK1?????????????????????{} ?????? {}", httpRequestDto, JSONObject.toJSONString(rmi));
            HttpResponseVO dataAndException = R.getDataAndException(rmi);
            // dataAndException.checkStatus();
            return httpRequestDto;
        }, ckThreadPool);
        try {
            HttpRequestDto httpRequestDto = httpRequestDtoCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        //????????????????????????????????????????????????
        //????????????????????????
        /*DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
        delOutboundOperationVO.setOrderNo(orderNo);
        operationFeignService.delOutboundThaw(delOutboundOperationVO);
        // ??????????????????
        InboundReceiptInfoVO inboundReceiptInfoVO = queryInfo(orderNo);
        List<InboundReceiptDetailVO> details = inboundReceiptInfoVO.getInboundReceiptDetails();
        Long count = details.stream().filter(x -> null != x.getPutQty()).mapToLong(InboundReceiptDetailVO::getPutQty).sum();
        *//**
         * #{@Like com.szmsd.chargerules.service.impl.OperationServiceImpl#frozenFeesForWarehousing(com.szmsd.delivery.vo.DelOutboundOperationVO, java.math.BigDecimal)}
         * ?????????bug ????????????????????? ?????????????????? ???????????????????????????????????????
         *//*
        Operation operation = new Operation();
        BigDecimal amount = BigDecimal.ZERO;

        List<String> skuList = details.stream().map(InboundReceiptDetailVO::getSku).collect(Collectors.toList());
        BaseProductBatchQueryDto baseProductBatchQueryDto = new BaseProductBatchQueryDto();
        baseProductBatchQueryDto.setSellerCode(delOutboundOperationVO.getCustomCode());
        baseProductBatchQueryDto.setCodes(skuList);
        R<List<BaseProductMeasureDto>> listR = baseProductFeignService.batchSKU(baseProductBatchQueryDto);
        List<BaseProductMeasureDto> dataAndException = R.getDataAndException(listR);
        Map<String, Double> skuWeightMap = dataAndException.stream().collect(Collectors.toMap(BaseProductMeasureDto::getCode, BaseProductMeasureDto::getWeight));

        for (InboundReceiptDetailVO vo : details) {
            OperationDTO operationDTO = new OperationDTO();
            operationDTO.setOperationType(DelOutboundOrderEnum.FREEZE_IN_STORAGE.getCode());
            operationDTO.setOrderType(OrderTypeEnum.Receipt.name());
            operationDTO.setWarehouseCode(inboundReceiptInfoVO.getWarehouseCode());
            double weight = Optional.ofNullable(skuWeightMap.get(vo.getSku())).orElse(0.00);
            operationDTO.setWeight(weight);
            R<Operation> operationR = operationFeignService.queryDetails(operationDTO);
            operation = R.getDataAndException(operationR);
            String unit = operation.getUnit();
            if ("kg".equalsIgnoreCase(unit)) {
                amount = operation.getFirstPrice().multiply(new BigDecimal(weight * vo.getPutQty() / 1000 + "").setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP).add(amount);
            } else {
                amount = this.calculate(operation.getFirstPrice(), operation.getNextPrice(), vo.getPutQty()).add(amount);
            }
        }
        CustPayDTO custPayDTO = new CustPayDTO();
        custPayDTO.setCurrencyCode(operation.getCurrencyCode());
        custPayDTO.setAmount(amount);
        custPayDTO.setNo(orderNo);
        custPayDTO.setOrderType(DelOutboundOrderEnum.FREEZE_IN_STORAGE.getCode());
        custPayDTO.setCusCode(inboundReceiptInfoVO.getCusCode());
        R r = rechargesFeignService.feeDeductions(custPayDTO);
        R.getDataAndException(r);*/

        // ?????????????????????
        String collectionNo = inboundReceiptInfoVO.getCollectionNo();
        if (StringUtils.isNotEmpty(collectionNo)) {
            // ?????????????????????
            PackageCollection queryPackageCollection = new PackageCollection();
            queryPackageCollection.setCollectionNo(collectionNo);
            R<PackageCollection> packageCollectionR = packageCollectionFeignService.getInfoByNo(queryPackageCollection);
            if (null != packageCollectionR && Constants.SUCCESS == packageCollectionR.getCode()) {
                PackageCollection packageCollection = packageCollectionR.getData();
                // ????????????????????????????????????????????????
                if (null != packageCollection && "destroy".equals(packageCollection.getHandleMode())) {
                    DelOutboundDto delOutboundDto = new DelOutboundDto();
                    delOutboundDto.setCustomCode(packageCollection.getSellerCode());
                    delOutboundDto.setSellerCode(packageCollection.getSellerCode());
                    delOutboundDto.setWarehouseCode(packageCollection.getWarehouseCode());
                    delOutboundDto.setOrderType("Destroy");
                    List<DelOutboundDetailDto> detailList = new ArrayList<>();
                    List<PackageCollectionDetail> packageCollectionDetailList = packageCollection.getDetailList();
                    for (PackageCollectionDetail packageCollectionDetail : packageCollectionDetailList) {
                        DelOutboundDetailDto delOutboundDetailDto = new DelOutboundDetailDto();
                        delOutboundDetailDto.setSku(packageCollectionDetail.getSku());
                        delOutboundDetailDto.setQty(Long.valueOf(packageCollectionDetail.getQty()));
                        delOutboundDetailDto.setProductNameChinese(packageCollectionDetail.getSkuName());
                        BigDecimal declaredValue = packageCollectionDetail.getDeclaredValue();
                        if (null == declaredValue) {
                            declaredValue = BigDecimal.ZERO;
                        }
                        delOutboundDetailDto.setDeclaredValue(declaredValue.doubleValue());
                        detailList.add(delOutboundDetailDto);
                    }
                    delOutboundDto.setDetails(detailList);
                    DelOutboundAddResponse outboundAddResponse = this.delOutboundClientService.addShipmentPackageCollection(delOutboundDto);
                    if (null != outboundAddResponse) {
                        PackageCollection updatePackageCollection = new PackageCollection();
                        updatePackageCollection.setOutboundNo(outboundAddResponse.getOrderNo());
                        updatePackageCollection.setCollectionNo(collectionNo);
                        packageCollectionFeignService.updateOutboundNo(updatePackageCollection);
                    }
                }
            }
        }
    }

    /**
     * #{@link: com.szmsd.chargerules.service.impl.PayServiceImpl#calculate(java.math.BigDecimal, java.math.BigDecimal, java.lang.Long)}
     *
     * @param firstPrice
     * @param nextPrice
     * @param qty
     * @return
     */
    public BigDecimal calculate(BigDecimal firstPrice, BigDecimal nextPrice, Integer qty) {
        if (qty <= 0) return BigDecimal.ZERO;
        return qty == 1 ? firstPrice : new BigDecimal(qty - 1).multiply(nextPrice).add(firstPrice);
    }

    /**
     * ????????????
     *
     * @param warehouseNo
     * @param status
     */
    @Override
    public void updateStatus(String warehouseNo, InboundReceiptEnum.InboundReceiptStatus status) {
        InboundReceipt inboundReceipt = new InboundReceipt();
        inboundReceipt.setWarehouseNo(warehouseNo);
        inboundReceipt.setStatus(status.getValue());
        this.updateByWarehouseNo(inboundReceipt);
        log.info("?????????{}???????????????:{}{}", warehouseNo, status.getValue(), status.getValue2());
    }

    @Override
    public void updateByWarehouseNo(InboundReceipt inboundReceipt) {
        this.update(inboundReceipt, new UpdateWrapper<InboundReceipt>().lambda().eq(InboundReceipt::getWarehouseNo, inboundReceipt.getWarehouseNo()));
    }

    /**
     * ???????????????
     *
     * @param inboundReceiptReviewDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void review(InboundReceiptReviewDTO inboundReceiptReviewDTO) {
        /* SysUser loginUserInfo = remoteComponent.getLoginUserInfo();*/
        InboundReceipt inboundReceipt = new InboundReceipt();
        InboundReceiptEnum.InboundReceiptEnumMethods anEnum = InboundReceiptEnum.InboundReceiptEnumMethods.getEnum(InboundReceiptEnum.InboundReceiptStatus.class, inboundReceiptReviewDTO.getStatus());
        anEnum = anEnum == null ? InboundReceiptEnum.InboundReceiptStatus.REVIEW_FAILURE : anEnum;
        inboundReceipt.setStatus(anEnum.getValue());
        inboundReceipt.setReviewRemark(inboundReceiptReviewDTO.getReviewRemark());
        Optional<LoginUser> loginUser = Optional.ofNullable(SecurityUtils.getLoginUser());
        String userId = loginUser.map(LoginUser::getUserId).map(String::valueOf).orElse("");
        String userName = loginUser.map(LoginUser::getUsername).orElse("");
        inboundReceipt.setReviewBy(userId);
        inboundReceipt.setReviewBy(userName);
        inboundReceipt.setReviewTime(new Date());
        List<String> warehouseNos = inboundReceiptReviewDTO.getWarehouseNos();
        log.info("???????????????: {},{},{}", anEnum.getValue2(), warehouseNos, inboundReceipt);

        StringBuffer sb = new StringBuffer();
        warehouseNos.forEach(warehouseNo -> {
            inboundReceipt.setWarehouseNo(warehouseNo);
            // ???????????? ?????????????????????
            if (!InboundReceiptEnum.InboundReceiptStatus.REVIEW_PASSED.getValue().equals(inboundReceiptReviewDTO.getStatus())) {
                this.updateByWarehouseNo(inboundReceipt);
                return;
            }
            InboundReceiptInfoVO inboundReceiptInfoVO = this.queryInfo(warehouseNo, false);

            // ??????????????????????????????????????????????????? ??????????????????????????????????????????
            log.info("?????????????????????{}", JSONObject.toJSONString(inboundReceiptReviewDTO));
            //remoteComponent.delOutboundCharge(inboundReceiptInfoVO);


            try {
                if (CheckTag.get()) {
                    log.info("-----??????????????????wms??????????????????????????? ????????????-?????? ??????????????????B3??????-----");
                    this.updateByWarehouseNo(inboundReceipt);
                } else {
                    this.updateByWarehouseNo(inboundReceipt);
                    R<CreateReceiptResponse> createReceiptResponseR = remoteRequest.createInboundReceipt(inboundReceiptInfoVO);
//                    // ?????????????????????????????????
//                    if (createReceiptResponseR.getCode()== HttpStatus.ERROR){
//                     baseMapper.updateInboundReceipt(warehouseNo);
//                    }


                    CreateInboundReceiptDTO createInboundReceiptDTO = new CreateInboundReceiptDTO();
                    BeanUtils.copyProperties(inboundReceiptInfoVO, createInboundReceiptDTO);
                    createInboundReceiptDTO.setWarehouseNo(inboundReceiptInfoVO.getWarehouseNo());
                    createInboundReceiptDTO.setWarehouseCode(inboundReceiptInfoVO.getWarehouseCode());
                    createInboundReceiptDTO.setDeliveryNo(inboundReceiptInfoVO.getDeliveryNo());

                    remoteComponent.createTracking(createInboundReceiptDTO);
                }

//                this.inbound(inboundReceiptInfoVO);
            } catch (Exception e) {
                log.error(e.getMessage());
                sb.append(e.getMessage().replace("???????????????", warehouseNo));
//                this.updateByWarehouseNo(new InboundReceipt().setWarehouseNo(warehouseNo).setStatus(InboundReceiptEnum.InboundReceiptStatus.REVIEW_FAILURE.getValue()).setReviewRemark(e.getMessage()));
            }
        });
        AssertUtil.isTrue(sb.length() == 0, sb::toString);
    }



    /**
     * ???????????????
     *
     * @param inboundReceiptReviewDTO
     */
//    public void reviews(InboundReceiptReviewDTO inboundReceiptReviewDTO) {
//        /* SysUser loginUserInfo = remoteComponent.getLoginUserInfo();*/
//        InboundReceipt inboundReceipt = new InboundReceipt();
//        InboundReceiptEnum.InboundReceiptEnumMethods anEnum = InboundReceiptEnum.InboundReceiptEnumMethods.getEnum(InboundReceiptEnum.InboundReceiptStatus.class, inboundReceiptReviewDTO.getStatus());
//        anEnum = anEnum == null ? InboundReceiptEnum.InboundReceiptStatus.REVIEW_FAILURE : anEnum;
//        inboundReceipt.setStatus(anEnum.getValue());
//        inboundReceipt.setReviewRemark(inboundReceiptReviewDTO.getReviewRemark());
//        Optional<LoginUser> loginUser = Optional.ofNullable(SecurityUtils.getLoginUser());
//        String userId = loginUser.map(LoginUser::getUserId).map(String::valueOf).orElse("");
//        String userName = loginUser.map(LoginUser::getUsername).orElse("");
//        inboundReceipt.setReviewBy(userId);
//        inboundReceipt.setReviewBy(userName);
//        inboundReceipt.setReviewTime(new Date());
//        List<String> warehouseNos = inboundReceiptReviewDTO.getWarehouseNos();
//        log.info("???????????????: {},{},{}", anEnum.getValue2(), warehouseNos, inboundReceipt);
//
//        StringBuffer sb = new StringBuffer();
//        warehouseNos.forEach(warehouseNo -> {
//            inboundReceipt.setWarehouseNo(warehouseNo);
//            // ???????????? ?????????????????????
//            if (!InboundReceiptEnum.InboundReceiptStatus.REVIEW_PASSED.getValue().equals(inboundReceiptReviewDTO.getStatus())) {
//                this.updateByWarehouseNo(inboundReceipt);
//                return;
//            }
//            InboundReceiptInfoVO inboundReceiptInfoVO = this.queryInfo(warehouseNo, false);
//
//            // ??????????????????????????????????????????????????? ??????????????????????????????????????????
//            log.info("?????????????????????{}", JSONObject.toJSONString(inboundReceiptReviewDTO));
//            //remoteComponent.delOutboundCharge(inboundReceiptInfoVO);
//
//
//            try {
//                if (CheckTag.get()) {
//                    log.info("-----??????????????????wms??????????????????????????? ????????????-?????? ??????????????????B3??????-----");
//                    this.updateByWarehouseNo(inboundReceipt);
//                } else {
//                    this.updateByWarehouseNo(inboundReceipt);
//                    R<CreateReceiptResponse> createReceiptResponseR = remoteRequest.createInboundReceipt(inboundReceiptInfoVO);
//
//                    if (createReceiptResponseR.getCode()== HttpStatus.ERROR){
//                        baseMapper.updateInboundReceipt(warehouseNo);
//                    }
//
//                    CreateInboundReceiptDTO createInboundReceiptDTO = new CreateInboundReceiptDTO();
//                    BeanUtils.copyProperties(inboundReceiptInfoVO, createInboundReceiptDTO);
//                    createInboundReceiptDTO.setWarehouseNo(inboundReceiptInfoVO.getWarehouseNo());
//                    createInboundReceiptDTO.setWarehouseCode(inboundReceiptInfoVO.getWarehouseCode());
//                    createInboundReceiptDTO.setDeliveryNo(inboundReceiptInfoVO.getDeliveryNo());
//
//                    remoteComponent.createTracking(createInboundReceiptDTO);
//                    // ?????????????????????????????????
//
//
//                }
//
////                this.inbound(inboundReceiptInfoVO);
//            } catch (Exception e) {
//                log.error(e.getMessage());
//                sb.append(e.getMessage().replace("???????????????", warehouseNo));
////                this.updateByWarehouseNo(new InboundReceipt().setWarehouseNo(warehouseNo).setStatus(InboundReceiptEnum.InboundReceiptStatus.REVIEW_FAILURE.getValue()).setReviewRemark(e.getMessage()));
//            }
//        });
//        AssertUtil.isTrue(sb.length() == 0, sb::toString);
//
//    }



    /**
     * ??????????????? ????????????
     *
     * @param warehouseNo
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(String warehouseNo) {
        log.info("???????????????, warehouseNo={}", warehouseNo);
        InboundReceiptVO inboundReceiptVO = this.selectByWarehouseNo(warehouseNo);
        AssertUtil.notNull(inboundReceiptVO, "?????????[" + warehouseNo + "]?????????");
        List<String> statues = Arrays.asList(InboundReceiptEnum.InboundReceiptStatus.INIT.getValue(), InboundReceiptEnum.InboundReceiptStatus.REVIEW_FAILURE.getValue());
        String status = inboundReceiptVO.getStatus();
        String statusName = InboundReceiptEnum.InboundReceiptEnumMethods.getValue2(InboundReceiptEnum.InboundReceiptStatus.class, status);
        AssertUtil.isTrue(statues.contains(status), "?????????[" + warehouseNo + "]" + statusName + "????????????");
        baseMapper.deleteById(inboundReceiptVO.getId());

        // ????????????
        iInboundReceiptDetailService.deleteAndFileByWarehouseNo(warehouseNo);

        // ????????????
        asyncDeleteAttachment(warehouseNo);
    }

    /**
     * ???????????????????????????
     *
     * @param queryDTO
     */
    @Override
    public List<InboundReceiptExportVO> selectExport(InboundReceiptQueryDTO queryDTO) {
        if (StringUtils.isNotEmpty(queryDTO.getWarehouseNo())) {
            List<String> warehouseNoSplit = Arrays.asList(queryDTO.getWarehouseNo().split(","));
            List<String> warehouseNoList = ListUtils.emptyIfNull(queryDTO.getWarehouseNoList());
            queryDTO.setWarehouseNoList(Stream.of(warehouseNoSplit, warehouseNoList).flatMap(Collection::stream).distinct().collect(Collectors.toList()));
        }
        List<InboundReceiptExportVO> inboundReceiptExportVOS = baseMapper.selectExport(queryDTO);
        return inboundReceiptExportVOS;
        /*List<InboundReceiptExportVO> serialize = ObjectMapperUtils.serialize(inboundReceiptExportVOS);
        return BeanMapperUtil.mapList(serialize, InboundReceiptExportVO.class);*/
    }

    /**
     * ??????sku
     *
     * @param excel
     * @param details
     */
    @Override
    public void exportSku(Workbook excel, List<InboundReceiptDetailVO> details) {
        // ??????sheet
        Sheet sheet = excel.createSheet();
        // ??????
        List<InboundReceiptSkuExcelVO> sheetList = new ArrayList<>();

        // ??????
        sheetList.add(new InboundReceiptSkuExcelVO().setColumn0("SKU").setColumn1("??????????????????").setColumn2("????????????").setColumn3("????????????").setColumn4("???????????????").setColumn5("????????????").setColumn6("??????"));

        // ???????????????SKU
        sheetList.addAll(details.stream().map(detail -> {
            InboundReceiptSkuExcelVO vo = new InboundReceiptSkuExcelVO();
            vo.setColumn0(detail.getSku());
            vo.setColumn1(detail.getSkuName());
            vo.setColumn2(detail.getDeclareQty() + "");
            vo.setColumn3(detail.getPutQty() + "");
            vo.setColumn4(detail.getOriginCode());
            vo.setColumn5(detail.getEditionImage() == null ? "" : detail.getEditionImage().getAttachmentUrl());
            vo.setColumn6(detail.getRemark());
            return vo;
        }).collect(Collectors.toList()));
        log.info("??????sku: {}", details);

        // ?????????
        for (int i = 0; i < sheetList.size(); i++) {
            Row row = sheet.createRow(i);
            InboundReceiptSkuExcelVO vo = sheetList.get(i);
            Class<? extends InboundReceiptSkuExcelVO> aClass = vo.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (int i1 = 0; i1 < declaredFields.length; i1++) {
                sheet.setColumnWidth(i1, 100 * 40);
                // ????????????value
                String value;
                try {
                    Field declaredField = declaredFields[i1];
                    declaredField.setAccessible(true);
                    value = (String) declaredField.get(vo);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    value = "";
                }

                // ???2??? ??? ???????????? ???5???????????????
                if ((i > 0) && (i1 == 5) && StringUtils.isNotEmpty(value) && !"null".equals(value)) {
                    String file = value;
                    try {
                        file = mainUploadFolder + File.separator + new URL(value).getFile();
                        ExcelUtil.insertImage(excel, sheet, i, i1, file);
                    } catch (Exception e) {
                        log.error("???{}?????????????????????, imageUrl={}, {}", i, file, e.getMessage());
                    }
                    continue;
                }

                // ???????????????
                row.createCell(i1).setCellValue(value);
            }
            // ????????????
            ExcelUtil.bord(excel, row, i, 6);
        }
    }

    /**
     * ???????????????
     *
     * @param queryDTO
     * @return
     */
    @Override
    public List<InboundCountVO> statistics(InboundReceiptQueryDTO queryDTO) {
        return baseMapper.statistics(queryDTO);
    }

    /**
     * ??????
     *
     * @param warehouseNos
     */
    @Override
    public void arraigned(List<String> warehouseNos) {
        if (warehouseNos == null) {
            return;
        }
        warehouseNos.forEach(warehouseNo -> this.updateByWarehouseNo(new InboundReceipt().setWarehouseNo(warehouseNo).setStatus(InboundReceiptEnum.InboundReceiptStatus.ARRAIGNED.getValue())));
    }

    /**
     * ??????????????????
     *
     * @param warehouseNo
     */
    private void asyncDeleteAttachment(String warehouseNo) {
        CompletableFuture.runAsync(() -> {
            AttachmentTypeEnum inboundReceiptDocuments = AttachmentTypeEnum.INBOUND_RECEIPT_DOCUMENTS;
            log.info("???????????????[{}]{}", warehouseNo, inboundReceiptDocuments.getAttachmentType());
            remoteComponent.deleteAttachment(inboundReceiptDocuments, warehouseNo, null);
        });
    }

    /**
     * ??????????????????
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param warehouseNo
     * @param inboundReceiptDTO
     */
    private void asyncAttachment(String warehouseNo, InboundReceiptDTO inboundReceiptDTO) {
        CompletableFuture.runAsync(() -> {
            List<AttachmentFileDTO> documentsFile = inboundReceiptDTO.getDocumentsFile();
            if (documentsFile != null) {
                log.info("???????????????????????????{}", documentsFile);
                remoteComponent.saveAttachment(warehouseNo, documentsFile, AttachmentTypeEnum.INBOUND_RECEIPT_DOCUMENTS);
            }
        });
    }

    @Override
    public void tracking(ReceivingTrackingRequest receivingCompletedRequest) {
        InboundTracking inboundTracking = new InboundTracking();
        BeanUtils.copyProperties(receivingCompletedRequest, inboundTracking);
        iInboundTrackingService.save(inboundTracking);
    }

    @Override
    public List<SkuInventoryStockRangeVo> querySkuStockByRange(InventoryStockByRangeDTO inventoryStockByRangeDTO) {
        return iInboundReceiptDetailService.querySkuStockByRange(inventoryStockByRangeDTO);
    }

    /**
     * ????????????????????????
     *
     * @param packageCollection ???????????????
     * @return ????????????
     */
    @Override
    public InboundReceiptInfoVO collectAndInbound(PackageCollection packageCollection) {
        CreateInboundReceiptDTO createInboundReceiptDTO = new CreateInboundReceiptDTO();

        createInboundReceiptDTO.setReceiptDetailIds(Lists.newArrayList());
        createInboundReceiptDTO.setDeliveryNo(packageCollection.getTrackingNo());
        createInboundReceiptDTO.setDeliveryNoList(Arrays.asList(packageCollection.getTrackingNo()));
        createInboundReceiptDTO.setOrderNo("");
        createInboundReceiptDTO.setCusCode(packageCollection.getSellerCode());
        createInboundReceiptDTO.setOrderType(InboundReceiptEnum.OrderType.NORMAL.getValue());
        createInboundReceiptDTO.setWarehouseCode(packageCollection.getWarehouseCode());
        // SKU????????????
        createInboundReceiptDTO.setWarehouseMethodCode("055001");
        // SKU
        createInboundReceiptDTO.setWarehouseCategoryCode("056001");
        createInboundReceiptDTO.setVat("");
        // ????????????
        createInboundReceiptDTO.setDeliveryWayCode("053003");

        createInboundReceiptDTO.setGoodsSourceCode("");
        createInboundReceiptDTO.setTrackingNumber("");
        createInboundReceiptDTO.setRemark("Source From " + packageCollection.getCollectionNo());
        createInboundReceiptDTO.setDocumentsFile(Lists.newArrayList());
        createInboundReceiptDTO.setStatus("0");
        createInboundReceiptDTO.setSourceType("CK1");
        createInboundReceiptDTO.setCollectionNo(packageCollection.getCollectionNo());
        createInboundReceiptDTO.setTransferNoList(Lists.newArrayList());
        List<PackageCollectionDetail> detailList = packageCollection.getDetailList();
        AtomicInteger qtyTotal= new AtomicInteger();
        List<InboundReceiptDetailDTO> detailDTOList = detailList.stream().map(detail -> {
            Integer qty = Optional.ofNullable(detail.getQty()).orElse(0);
            qtyTotal.addAndGet(qty);
            InboundReceiptDetailDTO inboundReceiptDetailDTO = new InboundReceiptDetailDTO();
            inboundReceiptDetailDTO.setWarehouseNo("");
            inboundReceiptDetailDTO.setSku(detail.getSku());
            inboundReceiptDetailDTO.setSkuName(detail.getSku());
            inboundReceiptDetailDTO.setDeclareQty(detail.getQty());
            inboundReceiptDetailDTO.setPutQty(0);
            inboundReceiptDetailDTO.setOriginCode(detail.getSku());
            inboundReceiptDetailDTO.setRemark("");
            inboundReceiptDetailDTO.setEditionImage(new AttachmentFileDTO());
            inboundReceiptDetailDTO.setDeliveryNo(packageCollection.getOutboundNo());
            return inboundReceiptDetailDTO;
        }).collect(Collectors.toList());
        createInboundReceiptDTO.setInboundReceiptDetails(detailDTOList);

        createInboundReceiptDTO.setTotalDeclareQty(qtyTotal.get());
        createInboundReceiptDTO.setTotalPutQty(0);
        log.info("??????????????????????????????{}", JSONObject.toJSONString(createInboundReceiptDTO));
        return saveOrUpdate(createInboundReceiptDTO);
    }

    @Override
    public void receipt(ReceiptRequest receiptRequest) {
        try {
        InboundReceiptDetail inboundReceiptDetail =inboundReceiptDetailMapper.selectReceiptDeta(receiptRequest);
        if (inboundReceiptDetail!=null){
            //????????????????????????1
            inboundReceiptDetail.setPutQty(inboundReceiptDetail.getPutQty()+1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date warehouseToTime =sdf.parse(receiptRequest.getOperateOn());
            inboundReceiptDetail.setWarehouseToTime(warehouseToTime);
            //???????????????????????????
          int a=  inboundReceiptDetailMapper.updateReceiptDeta(inboundReceiptDetail);
          InboundReceipt inboundReceipt=baseMapper.selectwarehouseNo(inboundReceiptDetail.getWarehouseNo());
            if (inboundReceipt!=null){
                inboundReceipt.setTotalPutQty(inboundReceipt.getTotalPutQty()+inboundReceiptDetail.getPutQty());
                baseMapper.updateById(inboundReceipt);
            }
        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateInboundReceipt(String warehouseNo) {
        baseMapper.updateInboundReceipt(warehouseNo);
    }
}

