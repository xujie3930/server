package com.szmsd.bas.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.component.RemoteRequest;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductDto;
import com.szmsd.bas.dto.BaseProductOms;
import com.szmsd.bas.dto.BaseProductQueryDto;
import com.szmsd.bas.dto.PricedProductsDTO;
import com.szmsd.bas.mapper.BaseProductMapper;
import com.szmsd.bas.service.IBaseProductService;
import com.szmsd.bas.util.RestTemplateUtils;
import com.szmsd.bas.vo.PricedProductsVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.http.vo.DirectServiceFeeData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.szmsd.bas.constant.UrlConstant.AddBasProductUrl;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author l
 * @since 2021-03-04
 */
@Service
public class BaseProductServiceImpl extends ServiceImpl<BaseProductMapper, BaseProduct> implements IBaseProductService {

    @Resource
    private RemoteRequest remoteRequest;


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
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", queryDto.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "product_name", queryDto.getProductName());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", queryDto.getSellerCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "product_attribute", queryDto.getProductAttribute());
        queryWrapper.eq("is_active", true);
        queryWrapper.orderByDesc("create_time");
        return super.list(queryWrapper);
    }

    @Override
    public List<BaseProduct> selectBaseProductByCode(String code){
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "code", code+"%");
        //queryWrapper.eq("warehouse_acceptance", true);
        queryWrapper.orderByAsc("code");
        return super.list(queryWrapper);
    }

    @Override
    public List<BaseProduct> listSku(BaseProduct baseProduct){
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
    public R<BaseProduct> getSku(BaseProduct baseProduct){
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(baseProduct.getCode())){
            queryWrapper.eq("code",baseProduct.getCode());
        }else {
           return R.failed("sku编码为空");
        }
       return  R.ok(super.getOne(queryWrapper));
    }

    /**
     * 新增模块
     *
     * @param baseProductDto 模块
     * @return 结果
     */
    @Override
    public int insertBaseProduct(BaseProductDto baseProductDto) {
        //默认激活
        baseProductDto.setIsActive(true);
        //TODO 卖家编码（暂无）
        //默认仓库没有验收
        baseProductDto.setWarehouseAcceptance(false);

        BaseProduct baseProduct = BeanMapperUtil.map(baseProductDto, BaseProduct.class);
        //包材不需要仓库测量尺寸
        baseProduct.setWarehouseAcceptance(true);

        //SKU需要仓库测量尺寸
        baseProduct.setWarehouseAcceptance(false);
        //传oms修改字段
        BaseProductOms baseProductOms = BeanMapperUtil.map(baseProductDto, BaseProductOms.class);
        //base64图片
        baseProductOms.setProductImage(baseProductDto.getProductImageBase64());
        //订单建议外包装材料
        baseProductOms.setSuggestPackingMaterial(baseProductDto.getSuggestPackingMaterialName());
        Map<String, String> user = new HashMap<>();
        user.put("UserId", "oms");
        user.put("Password", "666");
        ResponseEntity<String> result = RestTemplateUtils.post(AddBasProductUrl, baseProductOms, String.class, user);
        JSONObject object = JSONObject.parseObject(result.getBody());
        Boolean success = (Boolean) object.get("success");
        if (success == false) {
            throw new BaseException("传wms失败");
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
    public int updateBaseProduct(BaseProductDto baseProductDto) {
        return baseMapper.updateById(baseProductDto);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBaseProductByIds(List<String> ids) {
        //传删除给WMS
        return baseMapper.deleteBatchIds(ids);
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
        //查询是否有SKU
        int count = super.count(queryWrapper);
        R r = new R();
        if (count == 1) {
            r.setData(true);
            r.setMsg("success");
        } else {
            r.setData(false);
            r.setMsg("SKU不存在");
        }
        return r;
    }

    /**
     * 运费测算
     *
     * @param pricedProductsDTO
     * @return
     */
    @Override
    public List<PricedProductsVO> pricedProducts(PricedProductsDTO pricedProductsDTO) {
        List<DirectServiceFeeData> directServiceFeeData = remoteRequest.pricedProducts(pricedProductsDTO);
        return new ArrayList<>();
    }

}

