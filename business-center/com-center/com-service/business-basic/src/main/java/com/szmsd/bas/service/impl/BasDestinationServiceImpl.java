package com.szmsd.bas.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasDestination;
import com.szmsd.bas.dao.BasDestinationMapper;
import com.szmsd.bas.service.IBasDestinationService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.szmsd.bas.domain.Tree.treeRecursionDataList;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-07-07
 */
@Service
public class BasDestinationServiceImpl extends ServiceImpl<BasDestinationMapper, BasDestination> implements IBasDestinationService {

    @Resource
    private BasKeywordServiceImpl basKeywordService;


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasDestination selectBasDestinationById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasDestination 模块
     * @return 模块
     */
    @Override
    public List<BasDestination> selectBasDestinationList(BasDestination basDestination) {
        QueryWrapper<BasDestination> where = new QueryWrapper<BasDestination>();
        if (StringUtils.isNotEmpty(basDestination.getId())) {
            where.eq("id", basDestination.getId());
        }
        if (StringUtils.isNotEmpty(basDestination.getRegionName())) {
            where.like("region_name", basDestination.getRegionName());
        }
        if (StringUtils.isNotEmpty(basDestination.getRegionCode())) {
            where.eq("region_code", basDestination.getRegionCode());
        }
        if (StringUtils.isNotEmpty(basDestination.getBusinesSiteCode())) {
            where.eq("busines_site_code", basDestination.getBusinesSiteCode());
        }
        if (StringUtils.isNotEmpty(basDestination.getBusinesSite())) {
            where.like("busines_site", basDestination.getBusinesSite());
        }
        if (StringUtils.isNotEmpty(basDestination.getDisSite())) {
            where.like("dis_site", basDestination.getDisSite());
        }
        if (StringUtils.isNotEmpty(basDestination.getDisSiteCode())) {
            where.eq("dis_site_code", basDestination.getDisSiteCode());
        }
        if (StringUtils.isNotEmpty(basDestination.getCityName())) {
            where.eq("city_name", basDestination.getCityName());
        }
        if (StringUtils.isNotEmpty(basDestination.getDestinationTypeCode())){
            where.eq("destination_type_code",basDestination.getDestinationTypeCode());
        }
        if (StringUtils.isNotEmpty(basDestination.getDestinationTypeName())){
            where.like("destination_type_name",basDestination.getDestinationTypeCode());
        }
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasDestination 模块
     * @return 结果
     */
    @Override
    public int insertBasDestination(BasDestination basDestination) {
        return baseMapper.insert(basDestination);
    }

    /**
     * 修改模块
     *
     * @param BasDestination 模块
     * @return 结果
     */
    @Override
    public int updateBasDestination(BasDestination basDestination) {
        return baseMapper.updateById(basDestination);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasDestinationByIds(List<String> ids) {
        BasDestination basDestination = new BasDestination();
        basDestination.setId(ids.get(0));
        List<BasDestination> basDestinationList = this.selectBasDestinationList(basDestination);
        int i = basKeywordService.deleteBydestination(basDestinationList.get(0).getRegionCode());
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasDestinationById(String id) {
        return baseMapper.deleteById(id);
    }

    /**
     * 查询树型
     *
     * @param basDestination
     * @return
     */
    @Override
    public JSONArray selectTree(BasDestination basDestination) {
        List<Map<String, Object>> list = baseMapper.selectTree(basDestination);
        JSONArray objects = treeRecursionDataList(list, "0");
        return objects;
    }

    @Override
    public int deleteBySiteCode(String businesSiteCode) {

        BasDestination basDestination = new BasDestination();
        basDestination.setBusinesSiteCode(businesSiteCode);
        List<BasDestination> basDestinationList = this.selectBasDestinationList(basDestination);
        for (BasDestination basDestination1:basDestinationList){
            int i = basKeywordService.deleteBydestination(basDestination1.getRegionCode());
        }
        return baseMapper.deleteBySiteCode(businesSiteCode);
    }


}
