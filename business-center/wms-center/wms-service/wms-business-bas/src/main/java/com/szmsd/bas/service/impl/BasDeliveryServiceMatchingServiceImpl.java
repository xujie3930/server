package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.szmsd.bas.domain.BasDeliveryServiceMatching;
import com.szmsd.bas.dto.BasDeliveryServiceMatchingDto;
import com.szmsd.bas.mapper.BasDeliveryServiceMatchingMapper;
import com.szmsd.bas.service.IBasDeliveryServiceMatchingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import java.util.List;

/**
* <p>
    * 发货服务匹配 服务实现类
    * </p>
*
* @author Administrator
* @since 2022-05-12
*/
@Service
public class BasDeliveryServiceMatchingServiceImpl extends ServiceImpl<BasDeliveryServiceMatchingMapper, BasDeliveryServiceMatching> implements IBasDeliveryServiceMatchingService {


        /**
        * 查询发货服务匹配模块
        *
        * @param id 发货服务匹配模块ID
        * @return 发货服务匹配模块
        */
        @Override
        public BasDeliveryServiceMatching selectBasDeliveryServiceMatchingById(String id)
        {

            BasDeliveryServiceMatching vo = baseMapper.selectById(id);
            if(vo != null){
                dataDTO(vo);
            }
            return vo;
        }

        /**
        * 查询发货服务匹配模块列表
        *
        * @param basDeliveryServiceMatching 发货服务匹配模块
        * @return 发货服务匹配模块
        */
        @Override
        public List<BasDeliveryServiceMatching> selectBasDeliveryServiceMatchingList(BasDeliveryServiceMatching basDeliveryServiceMatching)
        {
            QueryWrapper<BasDeliveryServiceMatching> queryWrapper = new QueryWrapper<BasDeliveryServiceMatching>();
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", basDeliveryServiceMatching.getSellerCode());
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "del_flag", "0");
            List<BasDeliveryServiceMatching> list = baseMapper.selectList(queryWrapper);
            for (BasDeliveryServiceMatching vo: list){

                dataDTO(vo);
            }
            return list;
        }

        private void dataDTO(BasDeliveryServiceMatching vo){
            if(StringUtils.isNotEmpty(vo.getSku())){
                vo.setSku(StringUtils.replace(vo.getSku(), ",", "\n"));
            }

            if(StringUtils.isNotEmpty(vo.getCountry())){
                vo.setCountry(StringUtils.replace(vo.getCountry(), ",", "\n"));
            }

            if(StringUtils.isNotEmpty(vo.getBuyerService())){
                vo.setBuyerService(StringUtils.replace(vo.getBuyerService(), ",", "\n"));
            }

        }

    private void dtoData(BasDeliveryServiceMatching vo){
        if(StringUtils.isNotEmpty(vo.getSku())){
            vo.setSku(StringUtils.replace(vo.getSku(), "\n", ","));
        }

        if(StringUtils.isNotEmpty(vo.getCountry())){
            vo.setCountry(StringUtils.replace(vo.getCountry(), "\n", ","));
        }

        if(StringUtils.isNotEmpty(vo.getBuyerService())){
            vo.setBuyerService(StringUtils.replace(vo.getBuyerService(), "\n", ","));
        }

    }

        /**
        * 新增发货服务匹配模块
        *
        * @param basDeliveryServiceMatching 发货服务匹配模块
        * @return 结果
        */
        @Override
        public int insertBasDeliveryServiceMatching(BasDeliveryServiceMatching basDeliveryServiceMatching)
        {

            dtoData(basDeliveryServiceMatching);
            return baseMapper.insert(basDeliveryServiceMatching);
        }

        /**
        * 修改发货服务匹配模块
        *
        * @param basDeliveryServiceMatching 发货服务匹配模块
        * @return 结果
        */
        @Override
        public int updateBasDeliveryServiceMatching(BasDeliveryServiceMatching basDeliveryServiceMatching)
        {

            dtoData(basDeliveryServiceMatching);
            return baseMapper.updateById(basDeliveryServiceMatching);
        }

        /**
        * 批量删除发货服务匹配模块
        *
        * @param ids 需要删除的发货服务匹配模块ID
        * @return 结果
        */
        @Override
        public int deleteBasDeliveryServiceMatchingByIds(List<String> ids)
       {


           BasDeliveryServiceMatching update = new BasDeliveryServiceMatching();
           update.setDelFlag("2");

           LambdaQueryWrapper<BasDeliveryServiceMatching> queryWrapper = new LambdaQueryWrapper();
           queryWrapper.in(BasDeliveryServiceMatching::getId, ids);

           return baseMapper.update(update, queryWrapper);
       }

    @Override
    public List<BasDeliveryServiceMatching> getList(BasDeliveryServiceMatchingDto dto) {

            if(StringUtils.isEmpty(dto.getCountryCode())){
                return blankCountry(dto);
            }
        LambdaQueryWrapper<BasDeliveryServiceMatching> queryWrapper = new LambdaQueryWrapper<BasDeliveryServiceMatching>();
        queryWrapper.eq(BasDeliveryServiceMatching::getSellerCode, dto.getSellerCode());
        queryWrapper.eq(BasDeliveryServiceMatching::getDelFlag, "0");
        queryWrapper.eq(BasDeliveryServiceMatching::getCountryCode, dto.getCountryCode());
        queryWrapper.in(BasDeliveryServiceMatching::getSku, dto.getSkuList());
        List<BasDeliveryServiceMatching> list = baseMapper.selectList(queryWrapper);
        if(list.isEmpty()){
            return blankCountry(dto);
        }
        return list;
    }

    private  List<BasDeliveryServiceMatching> blankCountry(BasDeliveryServiceMatchingDto dto){
        LambdaQueryWrapper<BasDeliveryServiceMatching> queryWrapper = new LambdaQueryWrapper<BasDeliveryServiceMatching>();
        queryWrapper.eq(BasDeliveryServiceMatching::getSellerCode, dto.getSellerCode());
        queryWrapper.eq(BasDeliveryServiceMatching::getDelFlag, "0");
        queryWrapper.and(x -> x.isNull(BasDeliveryServiceMatching::getCountryCode).or().eq(BasDeliveryServiceMatching::getCountryCode, ""));
        queryWrapper.in(BasDeliveryServiceMatching::getSku, dto.getSkuList());
        List<BasDeliveryServiceMatching> list = baseMapper.selectList(queryWrapper);
        if(list.isEmpty()){
            return blankSku(dto);
        }
        return list;
    }
    private  List<BasDeliveryServiceMatching> blankSku(BasDeliveryServiceMatchingDto dto){
        LambdaQueryWrapper<BasDeliveryServiceMatching> queryWrapper = new LambdaQueryWrapper<BasDeliveryServiceMatching>();
        queryWrapper.eq(BasDeliveryServiceMatching::getSellerCode, dto.getSellerCode());
        queryWrapper.eq(BasDeliveryServiceMatching::getDelFlag, "0");
        queryWrapper.and(x -> x.isNull(BasDeliveryServiceMatching::getCountryCode).or().eq(BasDeliveryServiceMatching::getCountryCode, ""));
        queryWrapper.and(x -> x.isNull(BasDeliveryServiceMatching::getSku).or().eq(BasDeliveryServiceMatching::getSku, ""));
        List<BasDeliveryServiceMatching> list = baseMapper.selectList(queryWrapper);
        return list;
    }

}

