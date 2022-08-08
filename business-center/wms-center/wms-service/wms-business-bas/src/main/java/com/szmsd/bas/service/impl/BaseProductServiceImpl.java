package com.szmsd.bas.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.enums.BaseMainEnum;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.constant.ProductConstant;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.mapper.BaseProductMapper;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.service.IBasSerialNumberService;
import com.szmsd.bas.service.IBasePackingService;
import com.szmsd.bas.service.IBaseProductService;
import com.szmsd.bas.util.ObjectUtil;
import com.szmsd.bas.vo.BasProductMultipleTicketDTO;
import com.szmsd.bas.vo.BaseProductVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.api.feign.HtpRmiFeignService;
import com.szmsd.http.config.CkThreadPool;
import com.szmsd.http.dto.HttpRequestSyncDTO;
import com.szmsd.http.dto.ProductRequest;
import com.szmsd.bas.api.dto.CkSkuCreateDTO;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.enums.RemoteConstant;
import com.szmsd.http.vo.HttpResponseVO;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author l
 * @since 2021-03-04
 */
@Service
@Slf4j
public class BaseProductServiceImpl extends ServiceImpl<BaseProductMapper, BaseProduct> implements IBaseProductService {

    private static final String regex = "^[a-z0-9A-Z]+$";
    @Autowired
    private IBasSellerService basSellerService;
    @Resource
    private HtpBasFeignService htpBasFeignService;
    @Resource
    private IBasSerialNumberService baseSerialNumberService;
    @Autowired
    private RemoteAttachmentService remoteAttachmentService;
    @Resource
    private BaseProductMapper baseProductMapper;
    @Resource
    private BasSubFeignService basSubFeignService;
    @Autowired
    private IBasePackingService basePackingService;
    @Resource
    private CkThreadPool ckThreadPool;
    @Resource
    private HtpRmiFeignService htpRmiFeignService;
    @Autowired
    private BasSubClientService basSubClientService;

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BaseProduct selectBaseProductById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param baseProduct 模块
     * @return 模块
     */
    @Override
    public List<BaseProduct> selectBaseProductList(BaseProduct baseProduct) {
        QueryWrapper<BaseProduct> where = new QueryWrapper<BaseProduct>();
        return baseMapper.selectList(where);
    }

    @Override
//    @DataScope("seller_code")
    public List<BaseProduct> selectBaseProductPage(BaseProductQueryDto queryDto) {
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(queryDto.getCodes())) {
            //String[] codes = queryDto.getCodes().split(",");
            queryWrapper.in("code", queryDto.getCodesList());
        }
        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = org.apache.commons.collections4.CollectionUtils.isNotEmpty(SecurityUtils.getLoginUser().getPermissions()) ? SecurityUtils.getLoginUser().getPermissions().get(0) : "";
            if (com.szmsd.common.core.utils.StringUtils.isEmpty(queryDto.getSellerCodes())) {
                queryDto.setSellerCodes(cusCode);
            }
        }
        if (StringUtils.isNotEmpty(queryDto.getSellerCodes())) {
            String[] sellerCodes = queryDto.getSellerCodes().split(",");
            queryWrapper.in("seller_code", sellerCodes);
        }
        if (CollectionUtils.isNotEmpty(queryDto.getIds())) {
            queryWrapper.in("id", queryDto.getIds());
        }
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "category", queryDto.getCategory());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "source", queryDto.getSource());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", queryDto.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "product_name", queryDto.getProductName());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "product_name_chinese", queryDto.getProductNameChinese());
//        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", queryDto.getSellerCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "product_attribute", queryDto.getProductAttribute());
       /* if (queryDto.getIsActive() != null) {
            queryWrapper.eq("is_active", queryDto.getIsActive());
        }*/
        queryWrapper.orderByDesc("create_time");
        return super.list(queryWrapper);
    }

    @Override
    public List<BaseProduct> listSkuBySeller(BaseProductQueryDto queryDto) {
        QueryWrapper<BasSeller> basSellerQueryWrapper = new QueryWrapper<>();
        basSellerQueryWrapper.eq("user_name", SecurityUtils.getLoginUser().getUsername());
        BasSeller basSeller = basSellerService.getOne(basSellerQueryWrapper);
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seller_code", basSeller.getSellerCode());
        queryWrapper.eq("is_active", true);
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "category", queryDto.getCategory());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", queryDto.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "product_name", queryDto.getProductName());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "product_attribute", queryDto.getProductAttribute());
        queryWrapper.orderByDesc("create_time");
        return super.list(queryWrapper);
    }

    @Override
    public TableDataInfo<BaseProductVO> selectBaseProductByCode(String code, String sellerCode, String category, int current, int size) {
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "category", category);
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "code", code);
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", sellerCode);
        queryWrapper.eq("is_active", true);
        int total = super.count(queryWrapper);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit " + (current - 1) * size + "," + size);
        List<BaseProductVO> baseProductVOList = BeanMapperUtil.mapList(super.list(queryWrapper), BaseProductVO.class);
        baseProductVOList.forEach(b -> {
            if (b.getCode() != null) {
                List<BasAttachment> attachment = ListUtils.emptyIfNull(remoteAttachmentService
                        .list(new BasAttachmentQueryDTO().setAttachmentType(AttachmentTypeEnum.SKU_IMAGE.getAttachmentType()).setBusinessNo(b.getCode()).setBusinessItemNo(null)).getData());
                if (CollectionUtils.isNotEmpty(attachment)) {
                    List<AttachmentFileDTO> documentsFiles = new ArrayList();
                    for (BasAttachment a : attachment) {
                        documentsFiles.add(new AttachmentFileDTO().setId(a.getId()).setAttachmentName(a.getAttachmentName()).setAttachmentUrl(a.getAttachmentUrl()));
                    }
                    b.setDocumentsFiles(documentsFiles);
                }
            }
        });
        TableDataInfo table = new TableDataInfo();
        table.setTotal(total);
        table.setRows(baseProductVOList);
        table.setCode(200);
        return table;
    }

    @Override
    public List<BaseProductMeasureDto> batchSKU(BaseProductBatchQueryDto dto) {
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        if (CollectionUtils.isEmpty(dto.getCodes())) {
            return Collections.emptyList();
        } else {
            queryWrapper.eq("is_active", true);
            queryWrapper.in("code", dto.getCodes());
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", dto.getSellerCode());
        }
        return BeanMapperUtil.mapList(super.list(queryWrapper), BaseProductMeasureDto.class);
    }

    @Override
    public void importBaseProduct(List<BaseProductImportDto> list) {

        //判断是否必填
        QueryWrapper<BasSeller> basSellerQueryWrapper = new QueryWrapper<>();
        basSellerQueryWrapper.eq("user_name", SecurityUtils.getLoginUser().getUsername());
        BasSeller seller = basSellerService.getOne(basSellerQueryWrapper);
        verifyBaseProductRequired(list, seller.getSellerCode());
        for (BaseProductImportDto b : list) {
            b.setHavePackingMaterial(b.getHavePackingMaterialName().equals("是") ? true : false);
        }
        List<BaseProduct> baseProductList = BeanMapperUtil.mapList(list, BaseProduct.class);
        List<Tuple2<BaseProduct, ProductRequest>> waitSyncList = new ArrayList<>();
        for (BaseProduct b : baseProductList) {
            b.setCategory(ProductConstant.SKU_NAME);
            b.setCategoryCode(ProductConstant.SKU);
            b.setSellerCode(seller.getSellerCode());
            b.setInitHeight(new BigDecimal(b.getInitHeight()).setScale(2, ROUND_HALF_UP).doubleValue());
            b.setInitLength(new BigDecimal(b.getInitLength()).setScale(2, ROUND_HALF_UP).doubleValue());
            b.setInitWidth(new BigDecimal(b.getInitWidth()).setScale(2, ROUND_HALF_UP).doubleValue());
            b.setInitWeight(new BigDecimal(b.getInitWeight()).setScale(2, ROUND_HALF_UP).doubleValue());
            b.setHeight(b.getInitHeight());
            b.setLength(b.getInitLength());
            b.setWidth(b.getInitWidth());
            b.setWeight(b.getInitWeight());
            b.setInitVolume(new BigDecimal(b.getInitHeight() * b.getInitLength() * b.getInitWidth()).setScale(2, ROUND_HALF_UP));
            b.setVolume(b.getInitVolume());
            b.setIsActive(true);
            b.setSource(BaseMainEnum.NORMAL_IN.getCode());
            b.setWarehouseAcceptance(false);
            ProductRequest productRequest = BeanMapperUtil.map(b, ProductRequest.class);
            productRequest.setProductDesc(b.getProductDescription());
            waitSyncList.add(Tuples.of(b, productRequest));
//            R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
//            if (!r.getData().getSuccess()) {
//                throw new BaseException("传wms失败:" + r.getData().getMessage());
//            }
        }
        Tuple2<List<BaseProduct>, String> result = syncToWms(waitSyncList);
        super.saveBatch(result.getT1());
        AssertUtil.isTrue(StringUtils.isBlank(result.getT2()), result.getT2());

    }

    @Override
    public void attribute(EtSkuAttributeRequest etSkuAttributeRequest) {

        java.util.Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("059");
        java.util.Map<String, String> map059 = new HashMap();
        if(listMap.get("059") != null){
            map059 = listMap.get("059").stream()
                    .collect(Collectors.toMap(BasSubWrapperVO::getSubValue,
                            BasSubWrapperVO:: getSubName, (v1, v2) -> v1));
        }

        log.info("et验收sku属性: {}", etSkuAttributeRequest);
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", etSkuAttributeRequest.getSku());
        if (super.count(queryWrapper) < 1) {
            throw new BaseException("sku不存在");
        }
        String operationOn = etSkuAttributeRequest.getOperateOn();
        BaseProduct baseProduct = new BaseProduct();
        if (StringUtils.isNotEmpty(operationOn)) {
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = df.parse(operationOn);
                baseProduct.setOperateOn(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        baseProduct.setOperator(etSkuAttributeRequest.getOperator());

        if(!map059.containsKey(etSkuAttributeRequest.getSkuAttribute())){
            throw new BaseException("sku属性不存在"+etSkuAttributeRequest.getSkuAttribute());
        }
        baseProduct.setProductAttribute(etSkuAttributeRequest.getSkuAttribute());
        baseProduct.setProductAttributeName(map059.get(etSkuAttributeRequest.getSkuAttribute()));

        UpdateWrapper<BaseProduct> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("code", etSkuAttributeRequest.getSku());
        super.update(baseProduct, updateWrapper);
    }


    @Override
    public void measuringProduct(MeasuringProductRequest request) {
        log.info("更新sku测量值: {}", request);
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", request.getCode());
        //queryWrapper.eq("category", ProductConstant.SKU_NAME);
        if (super.count(queryWrapper) < 1) {
            throw new BaseException("sku不存在");
        }
        BigDecimal volume = new BigDecimal(request.getHeight()).multiply(new BigDecimal(request.getWidth()))
                .multiply(new BigDecimal(request.getLength()))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        String operationOn = request.getOperateOn();
        request.setOperateOn(null);
        BaseProduct baseProduct = BeanMapperUtil.map(request, BaseProduct.class);
        if (StringUtils.isNotEmpty(operationOn)) {
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = df.parse(operationOn);
                baseProduct.setOperateOn(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        baseProduct.setCode(null);
        baseProduct.setWarehouseAcceptance(true);
        baseProduct.setVolume(volume);
        UpdateWrapper<BaseProduct> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("code", request.getCode());
        super.update(baseProduct, updateWrapper);
    }

    @Override
    public List<BaseProduct> listSku(BaseProduct baseProduct) {
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", baseProduct.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "category", baseProduct.getCategory());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "product_name", baseProduct.getProductName());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", baseProduct.getSellerCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "product_attribute", baseProduct.getProductAttribute());
        queryWrapper.eq("is_active", true);
        queryWrapper.orderByDesc("create_time");
        return super.list(queryWrapper);
    }

    @Override
    public R<BaseProduct> getSku(BaseProduct baseProduct) {
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(baseProduct.getCode())) {
            queryWrapper.eq("code", baseProduct.getCode());
            queryWrapper.eq("is_active", true);
        } else {
            return R.failed("有效sku编码为空");
        }
        return R.ok(super.getOne(queryWrapper));
    }

    @Override
    public List<BaseProductExportDto> exportProduceList(BaseProductQueryDto queryDto, String len) {
        List<BaseProduct> list = selectBaseProductPage(queryDto);
        List<BaseProductExportDto> exportList = BeanMapperUtil.mapList(list, BaseProductExportDto.class);
        Iterator<BaseProductExportDto> iterable = exportList.iterator();
        int count = 1;
        while (iterable.hasNext()) {
            BaseProductExportDto b = iterable.next();
            b.setNo(count++);

            if("en".equals(len)){
                b.setWarehouseAcceptanceValue(b.getWarehouseAcceptance() == true ? "Yes" : "No");

            }else{
                b.setWarehouseAcceptanceValue(b.getWarehouseAcceptance() == true ? "是" : "否");

            }
        }

        return exportList;
    }

    /**
     * 新增模块
     *
     * @param baseProductDto 模块
     * @return 结果
     */
    @Override
    @Transactional
    public int insertBaseProduct(BaseProductDto baseProductDto) {

        if (StringUtils.isEmpty(baseProductDto.getCode())) {
            if (ProductConstant.SKU_NAME.equals(baseProductDto.getCategory())) {
                String skuCode = "S" + baseProductDto.getSellerCode() + baseSerialNumberService.generateNumber(ProductConstant.SKU_NAME);
                baseProductDto.setCode(skuCode);
            } else {
                baseProductDto.setCode("WL" + baseProductDto.getSellerCode() + baseSerialNumberService.generateNumber("MATERIAL"));
            }
        } else {
            if (baseProductDto.getCode().length() < 2) {
                throw new CommonException("400", baseProductDto.getCode() + "编码长度不能小于两个字符");
            }
        }
        //验证 填写信息
        verifyBaseProduct(baseProductDto);
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", baseProductDto.getCode());
        if (super.count(queryWrapper) == 1) {
            throw new CommonException("400", baseProductDto.getCode() + "编码重复");
        }
        //默认激活
        baseProductDto.setIsActive(true);
        //默认仓库没有验收
        baseProductDto.setWarehouseAcceptance(false);

        BaseProduct baseProduct = BeanMapperUtil.map(baseProductDto, BaseProduct.class);
        //包材不需要仓库测量尺寸
        baseProduct.setWarehouseAcceptance(true);

        //SKU需要仓库测量尺寸
        baseProduct.setWarehouseAcceptance(false);
        if (StringUtils.isBlank(baseProduct.getSource())) {
            baseProduct.setSource(BaseMainEnum.NORMAL_IN.getCode());
        }
        baseProduct.setWeight(baseProduct.getInitWeight());
        baseProduct.setWidth(baseProduct.getInitWidth());
        baseProduct.setLength(baseProduct.getInitLength());
        baseProduct.setHeight(baseProduct.getInitHeight());
        baseProduct.setVolume(baseProduct.getInitVolume());
        ProductRequest productRequest = BeanMapperUtil.map(baseProductDto, ProductRequest.class);
        productRequest.setProductImage(baseProductDto.getProductImageBase64());
        productRequest.setProductDesc(baseProductDto.getProductDescription());
        /*R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
        //验证wms
        toWms(r);*/

        Tuple2<List<BaseProduct>, String> result = syncToWms(Collections.singletonList(Tuples.of(baseProduct, productRequest)));
        AssertUtil.isTrue(StringUtils.isBlank(result.getT2()), result.getT2());
        if (CollectionUtils.isNotEmpty(baseProductDto.getDocumentsFiles())) {
            AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(baseProductDto.getCode()).businessItemNo(null).fileList(baseProductDto.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.SKU_IMAGE).build();
            this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
        }
        return baseMapper.insert(baseProduct);
    }

    public Tuple2<List<BaseProduct>, String> syncToWms(List<Tuple2<BaseProduct, ProductRequest>> requestTupleList) {
        List<CompletableFuture<BaseProduct>> futures = new ArrayList<>();

        requestTupleList.forEach(tuple -> {

            BaseProduct baseProduct = tuple.getT1();
            ProductRequest productRequest = tuple.getT2();
            CompletableFuture<BaseProduct> future = CompletableFuture.supplyAsync(() -> {
                R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
                R.getDataAndException(r);
                //验证wms
                toWms(r);
                log.info("【推送WMS】SKU创建推送 {} 返回 {}", productRequest, JSONObject.toJSONString(r));
                return baseProduct;
            }, ckThreadPool).thenApplyAsync(x -> {
                // 只推送sku
                if (StringUtils.isBlank(x.getCategory()) || !ProductConstant.SKU_NAME.equals(x.getCategory())) {
                    log.info("【推送CK1】非SKU创建推送不推送 {}", x);
                    return x;
                }

                CkSkuCreateDTO ckSkuCreateDTO = CkSkuCreateDTO.createCkSkuCreateDTO(x);
                //封装转换对象
                //TODO 调用推送wms
                HttpRequestSyncDTO httpRequestDto = new HttpRequestSyncDTO();
                httpRequestDto.setRemoteTypeEnum(RemoteConstant.RemoteTypeEnum.SKU_CREATE);
                httpRequestDto.setMethod(HttpMethod.POST);
                httpRequestDto.setBinary(false);
                httpRequestDto.setUri("${" + DomainEnum.Ck1OpenAPIDomain.name() + "}/v1/merchantSkus");
                httpRequestDto.setBody(ckSkuCreateDTO);
                R<HttpResponseVO> httpResponseVOR = htpRmiFeignService.rmiSync(httpRequestDto);
                R.getDataAndException(httpResponseVOR);
               /* R<HttpResponseVO> rmiR = htpRmiFeignService.rmi(httpRequestDto);
                log.info("【推送CK1】SKU创建推送 {} 返回 {}", httpRequestDto, JSONObject.toJSONString(rmiR));
                HttpResponseVO dataAndException = R.getDataAndException(rmiR);
                dataAndException.checkStatus();*/
                return baseProduct;
            }, ckThreadPool);
            futures.add(future);
        });
        List<BaseProduct> canAddList = new ArrayList<>();
        StringBuilder hasError = new StringBuilder("");
        AtomicInteger index = new AtomicInteger(1);
        futures.forEach(x -> {
            BaseProduct baseProduct = null;
            int indexThis = index.getAndIncrement();
            try {
                baseProduct = x.get();
            } catch (Exception e) {
                log.error("【SKU新增】异常：", e);
                hasError.append("第").append(indexThis).append("条数据处理异常:").append(e.getMessage()).append("\n");
            }
            if (Objects.nonNull(baseProduct))
                canAddList.add(baseProduct);
        });
        return Tuples.of(canAddList, hasError.toString());
    }

    @Override
    public List<BaseProduct> BatchInsertBaseProduct(List<BaseProductDto> baseProductDtos) {
        baseProductDtos.forEach(o -> {
            if (StringUtils.isEmpty(o.getCode())) {
                if (ProductConstant.SKU_NAME.equals(o.getCategory())) {
                    String skuCode = "S" + o.getSellerCode() + baseSerialNumberService.generateNumber(ProductConstant.SKU_NAME);
                    o.setCode(skuCode);
                    o.setCategoryCode("SKUtype");
                } else {
                    o.setCode("WL" + o.getSellerCode() + baseSerialNumberService.generateNumber("MATERIAL"));
                    o.setCategoryCode("packagetype");
                }
            } else {
                if (o.getCode().length() < 2) {
                    throw new CommonException("400", o.getCategory() + "编码长度不能小于两个字符");
                }
            }
            //验证 填写信息
            verifyBaseProduct(o);
            QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", o.getCode());
            if (super.count(queryWrapper) == 1) {
                throw new BaseException(o.getCategory() + "编码重复");
            }
            //默认激活
            o.setIsActive(true);
            o.setSource(BaseMainEnum.COLLECT_IN.getCode());
            //o.setSource("02");
            //默认仓库没有验收
            o.setWarehouseAcceptance(false);
        });
        List<BaseProduct> baseProducts = BeanMapperUtil.mapList(baseProductDtos, BaseProduct.class);
        List<Tuple2<BaseProduct, ProductRequest>> waitSyncList = new ArrayList<>();
        baseProducts.forEach(o -> {
            //包材不需要仓库测量尺寸
            o.setWarehouseAcceptance(true);
            //SKU需要仓库测量尺寸
            o.setWarehouseAcceptance(false);
            o.setWeight(o.getInitWeight());
            o.setWidth(o.getInitWidth());
            o.setLength(o.getInitLength());
            o.setHeight(o.getInitHeight());
            o.setVolume(o.getInitVolume());
            ProductRequest productRequest = BeanMapperUtil.map(o, ProductRequest.class);
            productRequest.setProductDesc(o.getProductDescription());
            waitSyncList.add(Tuples.of(o, productRequest));
        });
        Tuple2<List<BaseProduct>, String> result = syncToWms(waitSyncList);
        super.saveBatch(result.getT1());
        AssertUtil.isTrue(StringUtils.isBlank(result.getT2()), result.getT2());
        return baseProducts;
    }
    @Override
    public void rePushBaseProduct(String sku) {
        if (StringUtils.isBlank(sku)) return;
        log.info("【重推sku】{}", sku);
        BaseProduct baseProduct = baseMapper.selectOne(Wrappers.<BaseProduct>lambdaQuery().eq(BaseProduct::getCode, sku));
        if (baseProduct != null) {
            BaseProductDto baseProductDto = new BaseProductDto();
            BeanUtils.copyProperties(baseProduct, baseProductDto);
            BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
            basAttachmentQueryDTO.setBusinessNo(sku);
            basAttachmentQueryDTO.setAttachmentType("SKU图片");
            R<List<BasAttachment>> list = this.remoteAttachmentService.list(basAttachmentQueryDTO);
            List<BasAttachment> skuImage = R.getDataAndException(list);
            List<AttachmentDataDTO> attachmentList = skuImage.stream().map(x -> {
                AttachmentDataDTO attachmentDataDTO = new AttachmentDataDTO();
                BeanUtils.copyProperties(x, attachmentDataDTO);
                return attachmentDataDTO;
            }).collect(Collectors.toList());
            baseProductDto.setDocumentsFiles(attachmentList);
            try {
                log.info("【重推sku】{}---{}", sku, JSONObject.toJSONString(baseProductDto));
                this.updateBaseProduct(baseProductDto);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 修改模块
     *
     * @param baseProductDto 模块
     * @return 结果
     */
    @Override
    public int updateBaseProduct(BaseProductDto baseProductDto) throws IllegalAccessException {
        BaseProduct bp = super.getById(baseProductDto.getId());
        if (bp.getCategory().equals(ProductConstant.SKU_NAME)) {
            baseProductDto.setCategory(ProductConstant.SKU_NAME);
        }
        verifyBaseProduct(baseProductDto);
        ProductRequest productRequest = BeanMapperUtil.map(baseProductDto, ProductRequest.class);
        BaseProduct baseProduct = super.getById(baseProductDto.getId());
        ObjectUtil.fillNull(productRequest, baseProduct);
        AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(baseProduct.getCode()).businessItemNo(null).fileList(baseProductDto.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.SKU_IMAGE).build();
        this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
        productRequest.setProductImage(baseProductDto.getProductImageBase64());
        productRequest.setProductDesc(baseProductDto.getProductDescription());
        R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
        //验证wms
        toWms(r);
        return baseMapper.updateById(baseProductDto);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public Boolean deleteBaseProductByIds(List<Long> ids) throws IllegalAccessException {
        //传删除给WMS
        for (Long id : ids) {
            ProductRequest productRequest = new ProductRequest();
            productRequest.setIsActive(false);
            BaseProduct baseProduct = super.getById(id);
            ObjectUtil.fillNull(productRequest, baseProduct);
            R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
            //验证wms
            toWms(r);
        }
        UpdateWrapper<BaseProduct> updateWrapper = new UpdateWrapper();
        updateWrapper.in("id", ids);
        updateWrapper.set("is_active", false);
        return super.update(updateWrapper);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBaseProductById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public R<Boolean> checkSkuValidToDelivery(BaseProduct baseProduct) {
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", baseProduct.getCode());
        queryWrapper.eq("is_active", true);
        //查询是否有SKU
        int count = super.count(queryWrapper);
        R r = new R();
        if (count == 1) {
            r.setData(true);
            r.setCode(200);
            r.setMsg("success");
        } else {
            r.setData(false);
            r.setCode(-200);
            r.setMsg("SKU不存在");
        }
        return r;
    }

    private List<BaseProduct> queryConditionList(BaseProductConditionQueryDto conditionQueryDto) {
        if (CollectionUtils.isEmpty(conditionQueryDto.getSkus())) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BaseProduct> queryWrapper = Wrappers.lambdaQuery();
        if (null != conditionQueryDto.getWarehouseCode()) {
            queryWrapper.eq(BaseProduct::getWarehouseCode, conditionQueryDto.getWarehouseCode());
        }
        if (null != conditionQueryDto.getSellerCode()) {
            queryWrapper.eq(BaseProduct::getSellerCode, conditionQueryDto.getSellerCode());
        }
        if (null != conditionQueryDto.getSource()) {
            queryWrapper.eq(BaseProduct::getSource, conditionQueryDto.getSource());
        }
        queryWrapper.in(BaseProduct::getCode, conditionQueryDto.getSkus());
        return this.list(queryWrapper);
    }

    @Override
    public List<String> listProductAttribute(BaseProductConditionQueryDto conditionQueryDto) {
        List<BaseProduct> list = this.queryConditionList(conditionQueryDto);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(BaseProduct::getProductAttribute).collect(Collectors.toList());
    }

    @Override
    public String importMultipleTicket(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            ExcelUtil<BasProductMultipleTicketDTO> util = new ExcelUtil<>(BasProductMultipleTicketDTO.class);
            List<BasProductMultipleTicketDTO> basSellerMultipleTicketDTOS = util.importExcel(inputStream);
            if (CollectionUtils.isEmpty(basSellerMultipleTicketDTOS))
                return "导入数据为空";
            List<BasProductMultipleTicketDTO> updateList = basSellerMultipleTicketDTOS.parallelStream()
                    .filter(x -> StringUtils.isNotBlank(x.getSellerCode()) && StringUtils.isNotBlank(x.getMultipleTicketFlagStr()))
                    .collect(Collectors.toList());
            List<String> sellerCodeList = updateList.parallelStream().map(BasProductMultipleTicketDTO::getSellerCode).distinct().collect(Collectors.toList());
            List<String> skuList = updateList.parallelStream().map(BasProductMultipleTicketDTO::getSku).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(sellerCodeList) || CollectionUtils.isEmpty(skuList))
                return "导入数据内容异常";

            List<BaseProduct> canUpdateSellerCodeList = baseMapper.selectList(Wrappers.<BaseProduct>lambdaQuery()
                    .in(BaseProduct::getSellerCode, sellerCodeList)
                    .in(BaseProduct::getCode, skuList)
                    .select(BaseProduct::getSellerCode, BaseProduct::getId, BaseProduct::getCode));
            // 数据库 有的sku
            Map<String, List<BaseProduct>> canUpdateSellerCodeMap = canUpdateSellerCodeList.stream().collect(Collectors.groupingBy(BaseProduct::getSellerCode));
            sellerCodeList.clear();
            skuList.clear();
            List<String> errorMsg = new ArrayList<>();
            List<BaseProduct> updateResultList = new ArrayList<>();
            // 导入的数据
            Map<String, List<BasProductMultipleTicketDTO>> updateInfoMap = updateList.stream().collect(Collectors.groupingBy(BasProductMultipleTicketDTO::getSellerCode));
            updateInfoMap.forEach((sellerCode, infoList) -> {
                List<BaseProduct> baseProducts = canUpdateSellerCodeMap.get(sellerCode);
                if (CollectionUtils.isEmpty(baseProducts)) {
                    errorMsg.add("用户不存在：" + sellerCode);
                    return;
                }

                Map<String, BaseProduct> skuInfoMap = baseProducts.stream().collect(Collectors.toMap(BaseProduct::getCode, x -> x));
                infoList.forEach(x -> {
                    BaseProduct baseProduct = skuInfoMap.get(x.getSku());
                    if (null != baseProduct) {
                        BaseProduct baseProduct1 = new BaseProduct();
                        baseProduct1.setId(baseProduct.getId());
                        baseProduct1.setMultipleTicketFlag(x.getMultipleTicketFlag());
                        updateResultList.add(baseProduct1);
                    } else {
                        errorMsg.add("用户" + sellerCode + "不存在SKU：" + x.getSku());
                    }
                });
            });
            if (CollectionUtils.isNotEmpty(updateResultList))
                this.updateBatchById(updateResultList);
            return StringUtils.join(errorMsg, ";");
        } catch (Exception e) {
            log.error("", e);
            return "导入数据异常";
        }
    }

    @Override
    public List<BaseProduct> queryProductList(BaseProductConditionQueryDto conditionQueryDto) {
        List<BaseProduct> list = this.queryConditionList(conditionQueryDto);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list;
    }

    private void verifyBaseProduct(BaseProductDto baseProductDto) {

        //判断填的值是否符合需求

        if (ProductConstant.SKU_NAME.equals(baseProductDto.getCategory())) {
            if (StringUtils.isNotEmpty(baseProductDto.getProductAttribute())) {
                if ("Battery".equals(baseProductDto.getProductAttribute())) {
                    if (StringUtils.isEmpty(baseProductDto.getElectrifiedMode()) || StringUtils.isEmpty(baseProductDto.getBatteryPackaging())) {
                        throw new BaseException("未填写带电信息");
                    }
                } else {
                    if (StringUtils.isNotEmpty(baseProductDto.getElectrifiedMode()) || StringUtils.isNotEmpty(baseProductDto.getBatteryPackaging())) {
                        throw new BaseException("请勿填写带电信息");
                    }
                }

                if (baseProductDto.getHavePackingMaterial() == true) {
                    if (StringUtils.isEmpty(baseProductDto.getBindCode())) {
                        throw new BaseException("未填写附带包材");
                    }
                } else {
                    if (StringUtils.isNotEmpty(baseProductDto.getBindCode())) {
                        throw new BaseException("请勿填写附带包材");
                    }
                }
            }
        }
    }

    private void verifyBaseProductRequired(List<BaseProductImportDto> list, String sellerCode) {

        StringBuilder s1 = new StringBuilder("");
        BasePacking basePacking = new BasePacking();
        BaseProduct baseProduct = new BaseProduct();
        //查询包材
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", ProductConstant.BC_NAME);
        queryWrapper.eq("seller_code", sellerCode);
        //查询主子类别
        Map<String, String> typeMap = basSubFeignService.getSubList(BaseMainEnum.SKU_TYPE.getCode()).getData();
        Map<String, String> eleMap = basSubFeignService.getSubList(BaseMainEnum.SKU_ELE.getCode()).getData();
        Map<String, String> elePackageMap = basSubFeignService.getSubList(BaseMainEnum.SKU_ELEPACKAGE.getCode()).getData();
        int count = 1;
        for (BaseProductImportDto b : list) {
            StringBuilder s = new StringBuilder("");
            if (StringUtils.isEmpty(b.getCode())) {
                String skuCode = "S" + sellerCode + baseSerialNumberService.generateNumber(ProductConstant.SKU_NAME);
                b.setCode(skuCode);
            } else {
//                if (!Pattern.matches(regex, b.getCode())) {
//                    s.append("SKU不允许出现除了数字字母之外的其它字符，");
//                }
                if (b.getCode().length() < 2) {

                    s.append("SKU长度小于两字符，");
                }
                QueryWrapper<BaseProduct> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("code", b.getCode());
                if (super.count(queryWrapper1) > 0) {
                    s.append(b.getCode() + "编码重复录入,");
                }

            }
            if (StringUtils.isEmpty(b.getProductName())) {

                s.append("英文申报品名未填写,");
            }
            if (StringUtils.isEmpty(b.getProductAttributeName())) {

                s.append("产品属性未填写,");
            } else {
                if ("带电".equals(b.getProductAttributeName())) {
                    if (StringUtils.isEmpty(b.getElectrifiedModeName()) || StringUtils.isEmpty(b.getBatteryPackagingName())) {
                        s.append("未填写完整带电信息,");
                    } else {

                        if (!eleMap.isEmpty()) {
                            if (eleMap.containsKey(b.getElectrifiedModeName())) {
                                b.setElectrifiedMode(eleMap.get(b.getElectrifiedModeName()));
                            } else {
                                s.append("未找到对应电池类型，");
                            }
                        } else {
                            s.append("未找到对应电池类型，");
                        }

                        if (!elePackageMap.isEmpty()) {
                            if (elePackageMap.containsKey(b.getBatteryPackagingName())) {
                                b.setBatteryPackaging(elePackageMap.get(b.getBatteryPackagingName()));
                            } else {
                                s.append("未找到对应电池包装，");
                            }
                        } else {
                            s.append("未找到对应电池包装，");
                        }
                    }
                } else {
                    if (!StringUtils.isEmpty(b.getElectrifiedModeName()) || !StringUtils.isEmpty(b.getBatteryPackagingName())) {
                        s.append("不能填写带电信息,");
                    }
                }
                if (!typeMap.isEmpty()) {
                    if (typeMap.containsKey(b.getProductAttributeName())) {
                        b.setProductAttribute(typeMap.get(b.getProductAttributeName()));
                    } else {
                        s.append("未找到对应产品属性，");
                    }
                } else {
                    s.append("未找到对应产品属性，");
                }
            }
            if (StringUtils.isEmpty(b.getHavePackingMaterialName())) {
                s.append("是否自备包材未填写,");
            } else {
                if ("是".equals(b.getHavePackingMaterialName())) {
                    if (StringUtils.isEmpty(b.getBindCode())) {
                        s.append("未填写附带包材，");
                    } else {
                        queryWrapper.eq("code", b.getBindCode());
                        baseProduct = super.getOne(queryWrapper);
                        if (baseProduct != null) {
                            b.setBindCodeName(baseProduct.getProductName());
                        } else {
                            s.append("未找到附带包材信息，");
                        }
                    }

                } else {
                    if (StringUtils.isNotEmpty(b.getBindCode())) {
                        s.append("不能填写附带包材，");
                    }
                }
            }
            if (StringUtils.isEmpty(b.getSuggestPackingMaterial())) {
//                s.append("物流包装未填写,");
            } else {
                basePacking.setPackingMaterialType(b.getSuggestPackingMaterial());
                List<BasePackingDto> basePackings = basePackingService.selectBasePackingList(basePacking);
                if (CollectionUtils.isNotEmpty(basePackings)) {
                    b.setSuggestPackingMaterialCode(basePackings.get(0).getPackageMaterialCode());
                    b.setPriceRange(basePackings.get(0).getPriceRange());
                } else {
                    s.append("未找到对应的物流包装,");
                }
            }
            if (b.getInitLength() == null) {
                s.append("长未填写,");
            }
            if (b.getInitHeight() == null) {
                s.append("高未填写,");
            }
            if (b.getInitWeight() == null) {
                s.append("重量未填写,");
            }
            if (b.getInitWidth() == null) {
                s.append("宽未填写,");
            }
            if (b.getInitWidth() == null) {
                s.append("宽未填写,");
            }
            if (b.getDeclaredValue() == null) {
                s.append("申报价值未填写,");
            }
            if (StringUtils.isEmpty(b.getProductDescription())) {
                s.append("产品说明未填写,");
            } else {
                if (b.getProductDescription().length() > 40) {
                    s.append("产品说明超过四十个字符,");
                }
            }
            if (!s.toString().equals("")) {
                s1.append("<br/>第" + count + "条数据：" + s);
            }
            count++;
        }

        Map<String, Integer> map = new HashMap<>();
        for (BaseProductImportDto b : list) {
            if (map.containsKey(b.getCode())) {
                map.put(b.getCode(), map.get(b.getCode()) + 1);
            } else {
                map.put(b.getCode(), 1);
            }
        }
        if (map.size() != list.size()) {
            s1.append("<br/>文件内填写sku有重复:");
            for (Object key : map.keySet()) {
                Integer value = (Integer) map.get(key);
                if (value > 1) {
                    s1.append(key + ",\n");
                }
            }

        }
        if (!s1.toString().equals("")) {
            throw new RuntimeException(s1.toString());
        }
    }

    private void toWms(R<ResponseVO> r) {
        if (r == null) {
            throw new RuntimeException("wms服务调用失败");
        }
        if (r.getData() == null) {
            //throw new BaseException("wms服务调用失败");
        } else {
            if (r.getData().getSuccess() == null) {
                if (r.getData().getErrors() != null) {
                    throw new RuntimeException("传wms失败" + r.getData().getErrors());
                }
            } else {
                if (!r.getData().getSuccess()) {
                    throw new RuntimeException("传wms失败" + r.getData().getMessage());
                }
            }
        }
    }


}

