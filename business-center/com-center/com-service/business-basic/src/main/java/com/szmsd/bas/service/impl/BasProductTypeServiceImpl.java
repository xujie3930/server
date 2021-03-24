package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasProductType;
import com.szmsd.bas.dao.BasProductTypeMapper;
import com.szmsd.bas.domain.BasCode;
import com.szmsd.bas.service.BasCodeService;
import com.szmsd.bas.service.IBasProductTypeService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-06-13
 */
@Service
public class BasProductTypeServiceImpl extends ServiceImpl<BasProductTypeMapper, BasProductType> implements IBasProductTypeService {

    @Resource
    private BasCodeService basCodeService;

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasProductType selectBasProductTypeById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param basProductType 模块
     * @return 模块
     */
    @Override
    public List<BasProductType> selectBasProductTypeList(BasProductType basProductType) {
        String idList=basProductType.getId();
        QueryWrapper<BasProductType> where = new QueryWrapper<BasProductType>();
        if (!StringUtils.isEmpty(basProductType.getId())){
            List<String> strings=new ArrayList<>();
            if(idList.contains(","))
            {
                String[] split = idList.split(",");
                strings = Arrays.asList(split);
            }else{
                strings.add(idList);
            }
            where.in("id",strings);
        }
        if (StringUtils.isNotEmpty(basProductType.getProductTypeCode())){
            where.eq("product_type_code",basProductType.getProductTypeCode());
        }
        if (StringUtils.isNotEmpty(basProductType.getProductTypeName())){
            where.like("product_type_name",basProductType.getProductTypeName());
        }
        if (StringUtils.isNotEmpty(basProductType.getPrefixNumber())){
            where.eq("prefix_number",basProductType.getPrefixNumber());
        }
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param basProductType 模块
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasProductType(BasProductType basProductType) {
        int insert = baseMapper.insert(basProductType);
        if(insert > 0){
            basCodeService.saveBasCode(new BasCode().setCode(basProductType.getProductTypeCode()).setPrefix(basProductType.getPrefixNumber())
            .setSequenceName(basProductType.getProductTypeName()));
        }
        return insert;
    }

    /**
     * 修改模块
     *
     * @param basProductType 模块
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasProductType(BasProductType basProductType) {
        int i = baseMapper.updateById(basProductType);
        if(i>0){
            basCodeService.saveBasCode(new BasCode().setCode(basProductType.getProductTypeCode()).setPrefix(basProductType.getPrefixNumber())
            .setSequenceName(basProductType.getProductTypeName()));
        }
        return i;
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasProductTypeByIds(List<String> ids) {
        List<BasProductType> basProductTypes = baseMapper.selectBatchIds(ids);
        int i = baseMapper.deleteBatchIds(ids);
        if(i > 0){
            List<BasCode> collect = ListUtils.emptyIfNull(basProductTypes).stream().map(item -> new BasCode().setCode(item.getProductTypeCode())).collect(Collectors.toList());
            basCodeService.deleteBasCode(collect);
        }
        return i;
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasProductTypeById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public List<BasProductType> selectGenTableColumnListByTableIds(List<String> id) {
        return baseMapper.selectBatchIds(id);
    }


}
