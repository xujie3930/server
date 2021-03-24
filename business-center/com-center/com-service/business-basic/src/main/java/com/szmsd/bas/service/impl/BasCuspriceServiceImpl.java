package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasCusprice;
import com.szmsd.bas.dao.BasCuspriceMapper;
import com.szmsd.bas.service.IBasCuspriceService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-06-29
 */
@Service
public class BasCuspriceServiceImpl extends ServiceImpl<BasCuspriceMapper, BasCusprice> implements IBasCuspriceService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasCusprice selectBasCuspriceById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasCusprice 模块
     * @return 模块
     */
    @Override
    public List<BasCusprice> selectBasCuspriceList(BasCusprice basCusprice) {
        String idList = basCusprice.getId();
        QueryWrapper<BasCusprice> where = new QueryWrapper<BasCusprice>();
        if (StringUtils.isNotEmpty(basCusprice.getCusName() )) {
            where.like("cus_name", basCusprice.getCusName());
        }
        if (StringUtils.isNotEmpty(basCusprice.getCusCode())){
            where.eq("cus_code",basCusprice.getCusCode());
        }
        if (StringUtils.isNotEmpty(basCusprice.getProductTypeName())) {
            where.like("product_type_name", basCusprice.getProductTypeName());
        }
        if (StringUtils.isNotEmpty(basCusprice.getProductTypeCode())){
            where.eq("product_type_code",basCusprice.getProductTypeCode());
        }
        if(StringUtils.isNotEmpty(basCusprice.getCostCategory())){
            where.eq("cost_category",basCusprice.getCostCategory());
        }
        if(StringUtils.isNotEmpty(basCusprice.getTime())){
            where.ge("end_time",basCusprice.getTime());
            where.le("start_time",basCusprice.getTime());
        }
        if (StringUtils.isNotEmpty(basCusprice.getSiteCode())){
            where.eq("site_code",basCusprice.getSiteCode());
        }
        if (!StringUtils.isEmpty(basCusprice.getId())) {
            if (!StringUtils.isEmpty(basCusprice.getId())) {
                List<String> strings = new ArrayList<>();
                if (idList.contains(",")) {
                    String[] split = idList.split(",");
                    strings = Arrays.asList(split);
                } else {
                    strings.add(idList);
                }

                where.in("id", strings);
            }
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasCusprice 模块
     * @return 结果
     */
    @Override
    public int insertBasCusprice(BasCusprice basCusprice) {
        return baseMapper.insert(basCusprice);
    }

    /**
     * 修改模块
     *
     * @param BasCusprice 模块
     * @return 结果
     */
    @Override
    public int updateBasCusprice(BasCusprice basCusprice) {
        return baseMapper.updateById(basCusprice);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCuspriceByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCuspriceById(String id) {
        return baseMapper.deleteById(id);
    }
}
