package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.component.RemoteRequest;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductDto;
import com.szmsd.bas.dto.BaseProductOms;
import com.szmsd.bas.dto.BaseProductQueryDto;
import com.szmsd.bas.dto.PricedProductsDTO;
import com.szmsd.bas.mapper.BaseProductMapper;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.service.IBaseProductService;
import com.szmsd.bas.util.ObjectUtil;
import com.szmsd.bas.vo.BaseProductVO;
import com.szmsd.bas.vo.PricedProductsVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.ProductRequest;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private IBasSellerService basSellerService;
    @Resource
    private HtpBasFeignService htpBasFeignService;


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
        queryWrapper.orderByDesc("create_time");
        return super.list(queryWrapper);
    }
    @Override
    public List<BaseProduct> listSkuBySeller(BaseProductQueryDto queryDto){
        QueryWrapper<BasSeller> basSellerQueryWrapper = new QueryWrapper<>();
        basSellerQueryWrapper.eq("user_name", SecurityUtils.getLoginUser().getUsername());
        BasSeller basSeller = basSellerService.getOne(basSellerQueryWrapper);
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seller_code",basSeller.getSellerCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", queryDto.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "product_name", queryDto.getProductName());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "product_attribute", queryDto.getProductAttribute());
        queryWrapper.orderByDesc("create_time");
        return super.list(queryWrapper);
    }

    @Override
    public List<BaseProductVO> selectBaseProductByCode(String code){
        QueryWrapper<BasSeller> basSellerQueryWrapper = new QueryWrapper<>();
        basSellerQueryWrapper.eq("user_name", SecurityUtils.getLoginUser().getUsername());
        BasSeller basSeller = basSellerService.getOne(basSellerQueryWrapper);
        QueryWrapper<BaseProduct> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "code", code+"%");
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", basSeller.getSellerCode());
        queryWrapper.eq("is_active",true);
        queryWrapper.orderByAsc("code");
        List<BaseProductVO> baseProductVOList = BeanMapperUtil.mapList(super.list(queryWrapper),BaseProductVO.class);
        return baseProductVOList;
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
            queryWrapper.eq("is_active",true);
        }else {
           return R.failed("有效sku编码为空");
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
    public int  insertBaseProduct(BaseProductDto baseProductDto) {
        //默认激活
        baseProductDto.setIsActive(true);
        //卖家编码
        QueryWrapper<BasSeller> basSellerQueryWrapper = new QueryWrapper<>();
        basSellerQueryWrapper.eq("user_name", SecurityUtils.getLoginUser().getUsername());
        BasSeller basSeller = basSellerService.getOne(basSellerQueryWrapper);
        baseProductDto.setSellerCode(basSeller.getSellerCode());
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
        ProductRequest productRequest = BeanMapperUtil.map(baseProductDto,ProductRequest.class);
        R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
        if(r.getCode()!=200){
            throw new BaseException("传wms失败"+r.getMsg());
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
        ProductRequest productRequest = BeanMapperUtil.map(baseProductDto,ProductRequest.class);
        BaseProduct baseProduct = super.getById(baseProductDto.getId());
        ObjectUtil.fillNull(productRequest,baseProduct);
        R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
        if(r.getCode()!=200){
            throw new BaseException("传wms失败"+r.getMsg());
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
    public int deleteBaseProductByIds(List<Long> ids) throws IllegalAccessException {
        //传删除给WMS
       for(Long id: ids){
           ProductRequest productRequest  = new ProductRequest();
           productRequest.setIsActive(false);
           BaseProduct baseProduct = super.getById(id);
           ObjectUtil.fillNull(productRequest,baseProduct);
           R<ResponseVO> r = htpBasFeignService.createProduct(productRequest);
           if(r.getCode()!=200){
               throw new BaseException("传wms失败"+r.getMsg());
           }
       }
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
        queryWrapper.eq("is_active",true);
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

