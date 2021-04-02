package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasMaterial;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BasMaterialQueryDto;
import com.szmsd.bas.mapper.BasMaterialMapper;
import com.szmsd.bas.service.IBasMaterialService;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.service.IBasSerialNumberService;
import com.szmsd.bas.util.ObjectUtil;
import com.szmsd.bas.vo.BasMaterialVO;
import com.szmsd.bas.vo.BaseProductVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.MaterialRequest;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author l
* @since 2021-03-12
*/
@Service
public class BasMaterialServiceImpl extends ServiceImpl<BasMaterialMapper, BasMaterial> implements IBasMaterialService {

    @Autowired
    private IBasSellerService basSellerService;
    @Resource
    private HtpBasFeignService htpBasFeignService;

    @Resource
    private IBasSerialNumberService baseSerialNumberService;

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public BasMaterial selectBasMaterialById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询模块列表
        *
        * @param basMaterialQueryDto 模块
        * @return 模块
        */
        @Override
        public List<BasMaterial> selectBasMaterialList(BasMaterialQueryDto basMaterialQueryDto)
        {
        QueryWrapper<BasMaterial> queryWrapper = new QueryWrapper<BasMaterial>();
            if(StringUtils.isNotEmpty(basMaterialQueryDto.getCodes())){
                String[] codes = basMaterialQueryDto.getCodes().split(",");
                queryWrapper.in("code",codes);
            }
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", basMaterialQueryDto.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", basMaterialQueryDto.getSellerCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "type_name", basMaterialQueryDto.getTypeName());
        if(basMaterialQueryDto.getIsActive()!=null){
            queryWrapper.eq("is_active", basMaterialQueryDto.getIsActive());
        }
        queryWrapper.orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
        }

    @Override
    public List<BasMaterialVO> selectBaseMaterialByCode(String code, String sellerCode){
        QueryWrapper<BasMaterial> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "code", code + "%");
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "seller_code", sellerCode);
        queryWrapper.eq("is_active", true);
        queryWrapper.orderByAsc("code");
        List<BasMaterialVO> basMaterialVOS = BeanMapperUtil.mapList(super.list(queryWrapper), BasMaterialVO.class);
        return basMaterialVOS;
    }

        /**
        * 新增模块
        *
        * @param basMaterial 模块
        * @return 结果
        */
        @Override
        public int insertBasMaterial(BasMaterial basMaterial)
        {
            QueryWrapper<BasMaterial> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code",basMaterial.getCode());
            if(super.count(queryWrapper)==1){
                throw new BaseException("包材编码重复");
            }
            basMaterial.setCategory("包材");
            basMaterial.setIsActive(true);
            MaterialRequest materialRequest = BeanMapperUtil.map(basMaterial,MaterialRequest.class);
            R<ResponseVO> r = htpBasFeignService.createMaterial(materialRequest);
            if(!r.getData().getSuccess()){
                throw new BaseException("传wms失败:" + r.getData().getMessage());
            }
            return baseMapper.insert(basMaterial);
        }

        /**
        * 修改模块
        *
        * @param basMaterial 模块
        * @return 结果
        */
        @Override
        public int updateBasMaterial(BasMaterial basMaterial) throws IllegalAccessException {
            MaterialRequest materialRequest = BeanMapperUtil.map(basMaterial,MaterialRequest.class);
            BasMaterial material = super.getById(basMaterial.getId());
            ObjectUtil.fillNull(materialRequest,material);
            R<ResponseVO> r = htpBasFeignService.createMaterial(materialRequest);
            if(!r.getData().getSuccess()){
                throw new BaseException("传wms失败:" + r.getData().getMessage());
            }
            return baseMapper.updateById(basMaterial);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public boolean deleteBasMaterialByIds(List<Long>  ids) throws IllegalAccessException {
           for(Long id: ids){
               MaterialRequest materialRequest = new MaterialRequest();
               materialRequest.setIsActive(false);
               BasMaterial material = super.getById(id);
               ObjectUtil.fillNull(materialRequest,material);
               R<ResponseVO> r = htpBasFeignService.createMaterial(materialRequest);
               if(!r.getData().getSuccess()){
                   throw new BaseException("传wms失败:" + r.getData().getMessage());
               }
           }
           UpdateWrapper<BasMaterial> updateWrapper = new UpdateWrapper();
           updateWrapper.in("id",ids);
           updateWrapper.set("is_active",false);
           return super.update(updateWrapper);
       }

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        @Override
        public int deleteBasMaterialById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

