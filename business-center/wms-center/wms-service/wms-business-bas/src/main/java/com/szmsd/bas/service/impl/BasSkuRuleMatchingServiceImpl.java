package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.szmsd.bas.api.enums.BaseMainEnum;
import com.szmsd.bas.constant.ProductConstant;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasSkuRuleMatching;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BasSkuRuleMatchingImportDto;
import com.szmsd.bas.dto.BasePackingDto;
import com.szmsd.bas.dto.BaseProductImportDto;
import com.szmsd.bas.enums.SkuRuleMatchingEnum;
import com.szmsd.bas.mapper.BasSkuRuleMatchingMapper;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.service.IBasSkuRuleMatchingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.service.IBaseProductService;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.dto.ProductRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
* <p>
    * sku规则匹配表 服务实现类
    * </p>
*
* @author Administrator
* @since 2022-05-10
*/
@Service
public class BasSkuRuleMatchingServiceImpl extends ServiceImpl<BasSkuRuleMatchingMapper, BasSkuRuleMatching> implements IBasSkuRuleMatchingService {


    @Resource
    private IBasSellerService basSellerService;

    @Resource
    private IBaseProductService baseProductService;
        /**
        * 查询sku规则匹配表模块列表
        *
        * @param basSkuRuleMatching sku规则匹配表模块
        * @return sku规则匹配表模块
        */
        @Override
        public List<BasSkuRuleMatching> selectBasSkuRuleMatchingList(BasSkuRuleMatching basSkuRuleMatching)
        {
            QueryWrapper<BasSkuRuleMatching> queryWrapper = new QueryWrapper<BasSkuRuleMatching>();
            queryWrapper.select("seller_code,source_sku,GROUP_CONCAT(oms_sku) oms_sku");
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", basSkuRuleMatching.getSellerCode());
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "system_type", basSkuRuleMatching.getSystemType());
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "source_sku", basSkuRuleMatching.getSourceSku());

            queryWrapper.groupBy("seller_code,source_sku");
            return baseMapper.selectList(queryWrapper);
        }


        /**
        * 修改sku规则匹配表模块
        *
        * @param basSkuRuleMatching sku规则匹配表模块
        * @return 结果
        */
        @Override
        @Transactional
        public int updateBasSkuRuleMatching(BasSkuRuleMatching basSkuRuleMatching)
        {

            List<String> checkData = new ArrayList<String>();
            for(String omsSku: StringUtils.split(basSkuRuleMatching.getOmsSku(), ",")){
                if(checkData.contains(omsSku)){
                    throw new RuntimeException("OMS SKU重复"+omsSku+",");
                }
                checkData.add(omsSku);
            }

            //1.删除原绑定关系的数据
            this.deleteBasSkuRuleMatchingByIds(Arrays.asList(basSkuRuleMatching));


            //2.新增数据
            List<BasSkuRuleMatching> saveList = new ArrayList<BasSkuRuleMatching>();
            for (String omsSku: StringUtils.split(basSkuRuleMatching.getOmsSku(), ",")){
                BasSkuRuleMatching newVO = new BasSkuRuleMatching();
                BeanUtils.copyProperties(basSkuRuleMatching, newVO);
                newVO.setOmsSku(omsSku);
                saveList.add(newVO);
            }

            this.saveBatch(saveList);


            return 1;
        }

        /**
        * 批量删除sku规则匹配表模块
        *
        * @param ids 需要删除的sku规则匹配表模块ID
        * @return 结果
        */
        @Override
        public int deleteBasSkuRuleMatchingByIds(List<BasSkuRuleMatching>  list){
           List<String> sourceSku = list.stream().map(s -> s.getSourceSku()).collect(Collectors.toList());
           QueryWrapper<BasSkuRuleMatching> queryWrapper = new QueryWrapper<BasSkuRuleMatching>();
           queryWrapper.eq("system_type", list.get(0).getSystemType());
           queryWrapper.eq("seller_code", list.get(0).getSellerCode());
           queryWrapper.in("source_sku", sourceSku);
            return baseMapper.delete(queryWrapper);
       }


    private void verifyBaseProductRequired(List<BasSkuRuleMatchingImportDto> list, String sellerCode) {

        StringBuilder s1 = new StringBuilder("");
        int count = 1;
        List<String> sourceSkuCheck = new ArrayList<String>();
        for (BasSkuRuleMatchingImportDto b : list) {
            StringBuilder s = new StringBuilder("");
            if(StringUtils.isEmpty(b.getSourceSku())){
                s.append("Shopify销售SKU不能为空,");
            }else{


                if(sourceSkuCheck.contains(b.getSourceSku())){
                    s.append("Shopify销售SKU重复"+b.getSourceSku()+",");
                }else{
                    sourceSkuCheck.add(b.getSourceSku());
                }
            }

            if(StringUtils.isEmpty(b.getSourceSku())){
                s.append("OMS SKU不能为空,");
            }else{
                List<String> checkData = new ArrayList<String>();
                for(String omsSku: StringUtils.split(b.getOmsSku(), ",")){
                    if(checkData.contains(omsSku)){
                        s.append("OMS SKU重复"+omsSku+",");
                        continue;
                    }
                    checkData.add(omsSku);
                }

                /*LambdaQueryWrapper<BaseProduct> queryWrapper = new LambdaQueryWrapper<BaseProduct>();

                queryWrapper.eq(BaseProduct::getSellerCode, sellerCode);
                queryWrapper.eq(BaseProduct::getIsActive, true);
                queryWrapper.in(BaseProduct::getCode, StringUtils.split(b.getOmsSku(), ","));

                List<BaseProduct> baseProductList = baseProductService.list(queryWrapper);
                List<String> baseSkuList = baseProductList.stream().map(vo -> vo.getCode()).collect(Collectors.toList());


                for(){

                }*/
            }

            if (!s.toString().equals("")) {
                s1.append("<br/>第" + count + "条数据：" + s);
            }
            count++;
        }

        if (!s1.toString().equals("")) {
            throw new RuntimeException(s1.toString());
        }
    }

    @Override
    public void importBaseProduct(List<BasSkuRuleMatchingImportDto> userList, String sellerCode) {

     /*   //判断是否必填
        QueryWrapper<BasSeller> basSellerQueryWrapper = new QueryWrapper<>();
        basSellerQueryWrapper.eq("user_name", SecurityUtils.getLoginUser().getUsername());
        BasSeller seller = basSellerService.getOne(basSellerQueryWrapper);*/
        verifyBaseProductRequired(userList, sellerCode);
        List<BasSkuRuleMatching> list = BeanMapperUtil.mapList(userList, BasSkuRuleMatching.class);
        for (BasSkuRuleMatching b : list) {
            b.setSellerCode(sellerCode);
            b.setSystemType(SkuRuleMatchingEnum.SHOPIFY.getCode());
        }


        //删除原绑定关系的数据
        List<String> sourceSku = list.stream().map(s -> s.getSourceSku()).collect(Collectors.toList());
        QueryWrapper<BasSkuRuleMatching> queryWrapper = new QueryWrapper<BasSkuRuleMatching>();
        queryWrapper.eq("system_type", list.get(0).getSystemType());
        queryWrapper.eq("seller_code", list.get(0).getSellerCode());
        queryWrapper.in("source_sku", sourceSku);
        baseMapper.delete(queryWrapper);



        //新增数据
        List<BasSkuRuleMatching> saveList = new ArrayList<BasSkuRuleMatching>();
        for (BasSkuRuleMatching basSkuRuleMatching: list){
            for (String omsSku: StringUtils.split(basSkuRuleMatching.getOmsSku(), ",")){
                BasSkuRuleMatching newVO = new BasSkuRuleMatching();
                BeanUtils.copyProperties(basSkuRuleMatching, newVO);
                newVO.setOmsSku(omsSku);
                saveList.add(newVO);
            }
        }


        this.saveBatch(saveList);



    }


}

