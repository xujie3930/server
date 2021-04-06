package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.domain.BasMaterial;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.mapper.BaseProductMapper;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.service.IBasSerialNumberService;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", queryDto.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "product_name", queryDto.getProductName());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", queryDto.getSellerCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "product_attribute", queryDto.getProductAttribute());
        if (queryDto.getIsActive() != null) {
            queryWrapper.eq("is_active", queryDto.getIsActive());
        }
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
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", queryDto.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "product_name", queryDto.getProductName());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "product_attribute", queryDto.getProductAttribute());
        queryWrapper.orderByDesc("create_time");
        return super.list(queryWrapper);
    }

    @Override
    public List<BaseProductVO> selectBaseProductByCode(String code, String sellerCode) {
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "code", code + "%");
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", sellerCode);
        queryWrapper.eq("is_active", true);
        queryWrapper.orderByAsc("code");
        List<BaseProductVO> baseProductVOList = BeanMapperUtil.mapList(super.list(queryWrapper), BaseProductVO.class);
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
    public void measuringProduct(MeasuringProductRequest request) {
        log.info("更新sku测量值: {}", request);
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", request.getCode());
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
    public List<BaseProductExportDto> exportProduceList(BaseProductQueryDto queryDto){
        List<BaseProduct> list = selectBaseProductPage(queryDto);
        List<BaseProductExportDto> exportList = BeanMapperUtil.mapList(list,BaseProductExportDto.class);
        Iterator<BaseProductExportDto> iterable = exportList.iterator();
        int count = 1;
        while (iterable.hasNext()){
            BaseProductExportDto b = iterable.next();
            b.setNo(count++);
            b.setWarehouseAcceptanceValue(b.getWarehouseAcceptance()==true ? "是": "否");
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
        if(StringUtils.isEmpty(baseProductDto.getCode())) {
            String skuCode = "S" + baseProductDto.getSellerCode() + baseSerialNumberService.generateNumber("SKU");
            baseProductDto.setCode(skuCode);
        }
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",baseProductDto.getCode());
        if(super.count(queryWrapper)==1){
            throw new BaseException("sku编码重复");
        }
        //默认激活
        baseProductDto.setIsActive(true);
        baseProductDto.setCategory("SKU");
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
        //传oms修改字段
        BaseProductOms baseProductOms = BeanMapperUtil.map(baseProductDto, BaseProductOms.class);
        //base64图片
        baseProductOms.setProductImage(baseProductDto.getProductImageBase64());
        ProductRequest productRequest = BeanMapperUtil.map(baseProductDto, ProductRequest.class);
        R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
        if (!r.getData().getSuccess()) {
            throw new BaseException("传wms失败:" + r.getData().getMessage());
        }
        if(CollectionUtils.isNotEmpty(baseProductDto.getDocumentsFiles())){
            AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(baseProductDto.getCode()).businessItemNo(null).fileList(baseProductDto.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.SKU_IMAGE).build();
            this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
        }
        return baseMapper.insert(baseProduct);
    }

    /**
     * 修改模块
     *
     * @param baseProductDto 模块
     * @return 结果
     */
    @Override
    public int updateBaseProduct(BaseProductDto baseProductDto) throws IllegalAccessException {
        ProductRequest productRequest = BeanMapperUtil.map(baseProductDto, ProductRequest.class);
        BaseProduct baseProduct = super.getById(baseProductDto.getId());
        ObjectUtil.fillNull(productRequest, baseProduct);
        if(CollectionUtils.isNotEmpty(baseProductDto.getDocumentsFiles())){
            AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(baseProduct.getCode()).businessItemNo(null).fileList(baseProductDto.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.SKU_IMAGE).build();
            this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
        }
        baseProduct.setProductImage(baseProductDto.getProductImageBase64());
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
    public boolean deleteBaseProductByIds(List<Long> ids) throws IllegalAccessException {
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
        queryWrapper.eq("category", "SKU");
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
}

