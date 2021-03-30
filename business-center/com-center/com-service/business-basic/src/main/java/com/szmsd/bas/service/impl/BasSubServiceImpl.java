package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.dao.BasSubMapper;
import com.szmsd.bas.service.IBasSubService;
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
 * @since 2020-06-18
 */
@Service
public class BasSubServiceImpl extends ServiceImpl<BasSubMapper, BasSub> implements IBasSubService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasSub selectBasSubById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasSub 模块
     * @return 模块
     */
    @Override
    public List<BasSub> selectBasSubList(BasSub basSub) {
        String codeList = basSub.getMainCode();
        QueryWrapper<BasSub> where = new QueryWrapper<BasSub>();
        if (!StringUtils.isEmpty(basSub.getMainCode())) {
            List<String> strings = new ArrayList<>();
            if (codeList.contains(",")) {
                String[] split = codeList.split(",");
                strings = Arrays.asList(split);
            } else {
                strings.add(codeList);
            }
            where.in("main_code", strings);
        }
        if (StringUtils.isNotEmpty(basSub.getMainName()) ) {
            where.like("main_name", basSub.getMainName());
        }
        if (StringUtils.isNotEmpty(basSub.getSubName()) ) {
            where.like("sub_name", basSub.getSubName());
        }
        if (StringUtils.isNotEmpty(basSub.getSubCode()) ) {
            where.like("sub_code", basSub.getSubCode());
        }
        if (StringUtils.isNotEmpty(basSub.getSubValue()) ) {
            where.like("sub_value", basSub.getSubValue());
        }
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasSub 模块
     * @return 结果
     */
    @Override
    public int insertBasSub(BasSub basSub) {
        return baseMapper.insert(basSub);
    }

    /**
     * 修改模块
     *
     * @param BasSub 模块
     * @return 结果
     */
    @Override
    public int updateBasSub(BasSub basSub) {
        return baseMapper.updateById(basSub);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasSubByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasSubById(String id) {
        return baseMapper.deleteById(id);
    }
}
