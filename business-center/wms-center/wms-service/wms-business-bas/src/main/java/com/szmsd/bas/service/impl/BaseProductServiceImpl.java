package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
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
import com.szmsd.bas.vo.BaseProductVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.ProductRequest;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
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

    private static final String  regex = "^[a-z0-9A-Z]+$";


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
    public List<BaseProduct> selectBaseProductPage(BaseProductQueryDto queryDto) {
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(queryDto.getCodes())) {
            String[] codes = queryDto.getCodes().split(",");
            queryWrapper.in("code", codes);
        }
        if (StringUtils.isNotEmpty(queryDto.getSellerCodes())) {
            String[] sellerCodes = queryDto.getSellerCodes().split(",");
            queryWrapper.in("seller_code", sellerCodes);
        }
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "category", queryDto.getCategory());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", queryDto.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "product_name", queryDto.getProductName());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "product_name_chinese", queryDto.getProductNameChinese());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", queryDto.getSellerCode());
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
    public List<BaseProductVO> selectBaseProductByCode(String code, String sellerCode,String category) {
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "category", category);
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "code", code);
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", sellerCode);
        queryWrapper.eq("is_active", true);
        queryWrapper.orderByAsc("code");
        List<BaseProductVO> baseProductVOList = BeanMapperUtil.mapList(super.list(queryWrapper), BaseProductVO.class);
        baseProductVOList.forEach(b -> {
            if(b.getCode()!=null){
                List<BasAttachment> attachment = ListUtils.emptyIfNull(remoteAttachmentService
                        .list(new BasAttachmentQueryDTO().setAttachmentType(AttachmentTypeEnum.SKU_IMAGE.getAttachmentType()).setBusinessNo(b.getCode()).setBusinessItemNo(null)).getData());
                if (CollectionUtils.isNotEmpty(attachment)) {
                    List<AttachmentFileDTO> documentsFiles = new ArrayList();
                    for(BasAttachment a:attachment){
                        documentsFiles.add(new AttachmentFileDTO().setId(a.getId()).setAttachmentName(a.getAttachmentName()).setAttachmentUrl(a.getAttachmentUrl()));
                    }
                    b.setDocumentsFiles(documentsFiles);
                }
            }
        });
        return baseProductVOList;
    }

    @Override
    public List<BaseProductMeasureDto> batchSKU(List<String> codes) {
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptyList();
        } else {
            queryWrapper.eq("is_active", true);
            queryWrapper.in("code", codes);
        }
        return BeanMapperUtil.mapList(super.list(queryWrapper), BaseProductMeasureDto.class);
    }

    @Override
    @Transactional
    public void  importBaseProduct(List<BaseProductImportDto> list)
    {

        //判断是否必填
        QueryWrapper<BasSeller> basSellerQueryWrapper = new QueryWrapper<>();
        basSellerQueryWrapper.eq("user_name",SecurityUtils.getLoginUser().getUsername());
        BasSeller seller = basSellerService.getOne(basSellerQueryWrapper);
        verifyBaseProductRequired(list,seller.getSellerCode());
        for(BaseProductImportDto b:list)
        {
            b.setHavePackingMaterial(b.getHavePackingMaterialName().equals("是")?true:false);
        }
        List<BaseProduct> baseProductList = BeanMapperUtil.mapList(list,BaseProduct.class);

        for(BaseProduct b:baseProductList)
        {
            b.setCategory(ProductConstant.SKU_NAME);
            b.setCategoryCode(ProductConstant.SKU);
            b.setSellerCode(seller.getSellerCode());
            b.setInitHeight(new BigDecimal(b.getInitHeight()).setScale(2,ROUND_HALF_UP).doubleValue());
            b.setInitLength(new BigDecimal(b.getInitLength()).setScale(2,ROUND_HALF_UP).doubleValue());
            b.setInitWidth(new BigDecimal(b.getInitWidth()).setScale(2,ROUND_HALF_UP).doubleValue());
            b.setInitWeight(new BigDecimal(b.getInitWeight()).setScale(2,ROUND_HALF_UP).doubleValue());
            b.setHeight(b.getInitHeight());
            b.setLength(b.getInitLength());
            b.setWidth(b.getInitWidth());
            b.setWeight(b.getInitWeight());
            b.setInitVolume(new BigDecimal(b.getInitHeight()*b.getInitLength()*b.getInitWidth()).setScale(2,ROUND_HALF_UP));
            b.setVolume(b.getInitVolume());
            b.setIsActive(true);
            b.setWarehouseAcceptance(false);
            ProductRequest productRequest = BeanMapperUtil.map(b, ProductRequest.class);
            R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
            if (!r.getData().getSuccess()) {
                throw new BaseException("传wms失败:" + r.getData().getMessage());
            }
        }
        super.saveBatch(baseProductList);

    }

    @Override
    public void measuringProduct(MeasuringProductRequest request) {
        log.info("更新sku测量值: {}", request);
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", request.getCode());
        queryWrapper.eq("category", ProductConstant.SKU_NAME);
        if (super.count(queryWrapper) != 1) {
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
    public List<BaseProductExportDto> exportProduceList(BaseProductQueryDto queryDto) {
        List<BaseProduct> list = selectBaseProductPage(queryDto);
        List<BaseProductExportDto> exportList = BeanMapperUtil.mapList(list, BaseProductExportDto.class);
        Iterator<BaseProductExportDto> iterable = exportList.iterator();
        int count = 1;
        while (iterable.hasNext()) {
            BaseProductExportDto b = iterable.next();
            b.setNo(count++);
            b.setWarehouseAcceptanceValue(b.getWarehouseAcceptance() == true ? "是" : "否");
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
            if(ProductConstant.SKU_NAME.equals(baseProductDto.getCategory())){
                String skuCode = "S" + baseProductDto.getSellerCode() + baseSerialNumberService.generateNumber(ProductConstant.SKU_NAME);
                baseProductDto.setCode(skuCode);
            }else{
                baseProductDto.setCode("WL"+baseProductDto.getSellerCode()+baseSerialNumberService.generateNumber("MATERIAL"));
            }
        }else{
            if(baseProductDto.getCode().length()<2){
                throw new BaseException(baseProductDto.getCategory()+"编码长度不能小于两个字符");
            }
        }
        //验证 填写信息
        verifyBaseProduct(baseProductDto);
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", baseProductDto.getCode());
        if (super.count(queryWrapper) == 1) {
            throw new BaseException(baseProductDto.getCategory()+"编码重复");
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
        baseProduct.setWeight(baseProduct.getInitWeight());
        baseProduct.setWidth(baseProduct.getInitWidth());
        baseProduct.setLength(baseProduct.getInitLength());
        baseProduct.setHeight(baseProduct.getInitHeight());
        baseProduct.setVolume(baseProduct.getInitVolume());
        ProductRequest productRequest = BeanMapperUtil.map(baseProductDto, ProductRequest.class);
        productRequest.setProductImage(baseProductDto.getProductImageBase64());
        R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
        if (!r.getData().getSuccess()) {
            throw new BaseException("传wms失败:" + r.getData().getMessage());
        }
        if (CollectionUtils.isNotEmpty(baseProductDto.getDocumentsFiles())) {
            AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(baseProductDto.getCode()).businessItemNo(null).fileList(baseProductDto.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.SKU_IMAGE).build();
            this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
        }
        return baseMapper.insert(baseProduct);
    }

    @Override
    public List<BaseProduct> BatchInsertBaseProduct(List<BaseProductDto> baseProductDtos){
        baseProductDtos.stream().forEach(o->{
            if (StringUtils.isEmpty(o.getCode())) {
                if(ProductConstant.SKU_NAME.equals(o.getCategory())){
                    String skuCode = "S" + o.getSellerCode() + baseSerialNumberService.generateNumber(ProductConstant.SKU_NAME);
                    o.setCode(skuCode);
                    o.setCategoryCode("SKUtype");
                }else{
                    o.setCode("WL"+o.getSellerCode()+baseSerialNumberService.generateNumber("MATERIAL"));
                    o.setCategoryCode("packagetype");
                }
            }else{
                if(o.getCode().length()<2){
                    throw new BaseException(o.getCategory()+"编码长度不能小于两个字符");
                }
            }
            //验证 填写信息
            verifyBaseProduct(o);
            QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", o.getCode());
            if (super.count(queryWrapper) == 1) {
                throw new BaseException(o.getCategory()+"编码重复");
            }
            //默认激活
            o.setIsActive(true);
            //默认仓库没有验收
            o.setWarehouseAcceptance(false);
        });
        List<BaseProduct> baseProducts = BeanMapperUtil.mapList(baseProductDtos, BaseProduct.class);

        baseProducts.stream().forEach(o->{
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
            R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
            if (!r.getData().getSuccess()) {
                throw new BaseException("传wms失败:" + r.getData().getMessage());
            }
        });
         super.saveBatch(baseProducts);
         return baseProducts;
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
        if(bp.getCategory().equals(ProductConstant.SKU_NAME)){
            baseProductDto.setCategory(ProductConstant.SKU_NAME);
        }
        verifyBaseProduct(baseProductDto);
        ProductRequest productRequest = BeanMapperUtil.map(baseProductDto, ProductRequest.class);
        BaseProduct baseProduct = super.getById(baseProductDto.getId());
        ObjectUtil.fillNull(productRequest, baseProduct);
        if (CollectionUtils.isNotEmpty(baseProductDto.getDocumentsFiles())) {
            AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(baseProduct.getCode()).businessItemNo(null).fileList(baseProductDto.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.SKU_IMAGE).build();
            this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
        }
        productRequest.setProductImage(baseProductDto.getProductImageBase64());
        R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
        if (!r.getData().getSuccess()) {
            throw new BaseException("传wms失败:" + r.getData().getMessage());
        }
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
            if (!r.getData().getSuccess()) {
                throw new BaseException("传wms失败:" + r.getData().getMessage());
            }
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
    public List<BaseProduct> queryProductList(BaseProductConditionQueryDto conditionQueryDto) {
        List<BaseProduct> list = this.queryConditionList(conditionQueryDto);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list;
    }

    private void verifyBaseProduct(BaseProductDto baseProductDto){

        //判断填的值是否符合需求

        if(ProductConstant.SKU_NAME.equals(baseProductDto.getCategory())) {
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

    private void verifyBaseProductRequired(List<BaseProductImportDto> list,String sellerCode)
    {

        StringBuilder s1 = new StringBuilder("");
        BasePacking basePacking = new BasePacking();
        BaseProduct baseProduct = new BaseProduct();
        //查询包材
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category",ProductConstant.BC_NAME);
        queryWrapper.eq("seller_code",sellerCode);
        //查询主子类别
        Map<String, String> typeMap= basSubFeignService.getSubList(BaseMainEnum.SKU_TYPE.getCode()).getData();
        Map<String, String> eleMap= basSubFeignService.getSubList(BaseMainEnum.SKU_ELE.getCode()).getData();
        Map<String, String> elePackageMap= basSubFeignService.getSubList(BaseMainEnum.SKU_ELEPACKAGE.getCode()).getData();
        int count = 1;
        for(BaseProductImportDto b:list)
        {
            StringBuilder s = new StringBuilder("");
            if(StringUtils.isEmpty(b.getCode()))
            {

                s.append("SKU未填写，");
            }else{
                if(!Pattern.matches(regex, b.getCode()))
                {
                    s.append("SKU不允许出现除了数字字母之外的其它字符，");
                }
                if(b.getCode().length()<2){

                    s.append("SKU长度小于两字符，");
                }
                QueryWrapper<BaseProduct> queryWrapper1 = new QueryWrapper<>();
                queryWrapper.eq("code", b.getCode());
                if (super.count(queryWrapper) == 1) {
                    s.append(b.getCode()+"编码重复,");
                }

            }
            if(StringUtils.isEmpty(b.getProductName()))
            {

                s.append("申报品名未填写,");
            }
            if(StringUtils.isEmpty(b.getProductAttributeName()))
            {

                s.append("产品属性未填写,");
            }else{
                if ("带电".equals(b.getProductAttributeName())) {
                    if (StringUtils.isEmpty(b.getElectrifiedModeName()) || StringUtils.isEmpty(b.getBatteryPackagingName())) {
                        s.append("未填写完整带电信息,");
                    }else{

                        if(!eleMap.isEmpty()){
                            if(eleMap.containsKey(b.getElectrifiedModeName())){
                                b.setElectrifiedMode(eleMap.get(b.getElectrifiedModeName()));
                            }else{
                                s.append("未找到对应电池类型，");
                            }
                        }else{
                            s.append("未找到对应电池类型，");
                        }

                        if(!elePackageMap.isEmpty()){
                            if(elePackageMap.containsKey(b.getBatteryPackagingName())){
                                b.setBatteryPackaging(elePackageMap.get(b.getBatteryPackagingName()));
                            }else{
                                s.append("未找到对应电池包装，");
                            }
                        }else{
                            s.append("未找到对应电池包装，");
                        }
                    }
                } else {
                    if (!StringUtils.isEmpty(b.getElectrifiedModeName()) || !StringUtils.isEmpty(b.getBatteryPackagingName())) {
                        s.append("不能填写带电信息,");
                    }
                }
                if(!typeMap.isEmpty()){
                    if(typeMap.containsKey(b.getProductAttributeName())){
                        b.setProductAttribute(typeMap.get(b.getProductAttributeName()));
                    }else{
                        s.append("未找到对应产品属性，");
                    }
                }else{
                    s.append("未找到对应产品属性，");
                }
            }
            if(StringUtils.isEmpty(b.getHavePackingMaterialName()))
            {
                s.append("是否自备包材未填写,");
            }else{
                if ("是".equals(b.getHavePackingMaterialName())) {
                    if (StringUtils.isEmpty(b.getBindCode())) {
                        s.append("未填写附带包材，");
                    }else{
                        queryWrapper.eq("code",b.getBindCode());
                        baseProduct = super.getOne(queryWrapper);
                        if(baseProduct!=null){
                            b.setBindCodeName(baseProduct.getProductName());
                        }else{
                            s.append("未找到附带包材信息，");
                        }
                    }

                } else {
                    if (StringUtils.isNotEmpty(b.getBindCode())) {
                        s.append("不能填写附带包材，");
                    }
                }
            }
            if(StringUtils.isEmpty(b.getSuggestPackingMaterial()))
            {
                s.append("物流包装未填写,");
            }else{
                basePacking.setName(b.getSuggestPackingMaterial());
                List<BasePacking> basePackings = basePackingService.selectBasePackingList(basePacking);
                if(CollectionUtils.isNotEmpty(basePackings)){
                    b.setSuggestPackingMaterialCode(basePackings.get(0).getCode());
                }else{
                    s.append("未找到对应的物流包装,");
                }
            }
            if(b.getInitLength()==null)
            {
                s.append("长未填写,");
            }
            if(b.getInitHeight()==null)
            {
                s.append("高未填写,");
            }
            if(b.getInitWeight()==null)
            {
                s.append("重量未填写,");
            }
            if(b.getInitWidth()==null)
            {
                s.append("宽未填写,");
            }
            if(b.getInitWidth()==null)
            {
                s.append("宽未填写,");
            }
            if(b.getDeclaredValue()==null)
            {
                s.append("申报价值未填写,");
            }
            if(b.getProductDescription().length()>10){
                s.append("产品说明超过十个字符,");
            }
            if(!s.toString().equals("")){
                s1.append("第"+count+"条数据："+s+"\n");
            }
            count++;
        }

        Map<String,String> map = new HashMap<>();
        for(BaseProductImportDto b:list){
            map.put(b.getCode(),b.getCode());
        }
        if(map.size()!=list.size()){
            s1.append("sku有重复\n");
        }
        if(!s1.toString().equals("")){
            throw new BaseException(s1.toString());
        }
    }


}

