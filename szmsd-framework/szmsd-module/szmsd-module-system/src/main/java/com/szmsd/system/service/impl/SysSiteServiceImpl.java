package com.szmsd.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.system.api.domain.SysSite;
import com.szmsd.system.domain.vo.TreeSelectCode;
import com.szmsd.system.mapper.SysSiteMapper;
import com.szmsd.system.service.ISysSiteService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 网点管理 服务实现类
 * </p>
 *
 * @author lzw
 * @since 2020-06-19
 */
@Service
public class SysSiteServiceImpl extends ServiceImpl<SysSiteMapper, SysSite> implements ISysSiteService {


    @Resource
    private SysSiteMapper sysSiteMapper;


    // @Resource
    // private BasFeignService basFeignService;

    /**
     * 查询网点管理模块
     *
     * @param id 网点管理模块ID
     * @return 网点管理模块
     */
    @Override
    public SysSite selectSysSiteById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    public SysSite selectSysSiteBySiteCode(String siteCode) {
        return sysSiteMapper.selectSysSiteBySiteCode(siteCode);
    }


    @Override
    public List<SysSite> getSubSite(String siteCode) {
        return sysSiteMapper.getSiteSub(siteCode);
    }

    /**
     * 根据ID查询所有子网点（正常状态）
     *
     * @param siteId 网点ID
     * @return 子网点数
     */
    @Override
    public int selectNormalChildrenSiteById(Long siteId) {
        return sysSiteMapper.selectNormalChildrenSiteById(siteId);
    }

    /**
     * 查询网点管理模块列表
     *
     * @param sysSite 网点管理模块
     * @return 网点管理模块
     */
    @Override
//    @DataScope(siteAlias = "d")
    public List<SysSite> selectSysSiteList(SysSite sysSite) {

//        QueryWrapper<SysSite> where = new QueryWrapper<SysSite>();
////
////        return baseMapper.selectList(where);
        return sysSiteMapper.selectSiteList(sysSite);
    }

    @Override
    public List<SysSite> listAll() {
        QueryWrapper<SysSite> where = new QueryWrapper<>();
        where.orderByAsc("site_code");
        return sysSiteMapper.selectList(where);
    }


    /**
     * 弹窗组件查询网点管理模块列表
     *
     * @param sysSite 网点管理模块
     * @return 网点管理模块
     */
    @Override
    public List<SysSite> selectAlertSysSiteList(SysSite sysSite) {

        QueryWrapper<SysSite> where = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(sysSite.getSiteCode())) {
            where.likeRight("site_code", sysSite.getSiteCode());
        }
        if (StringUtils.isNotEmpty(sysSite.getSiteNameChinese())) {
            where.likeRight("site_name_chinese", sysSite.getSiteNameChinese());
        }

        if(ObjectUtils.isNotEmpty(sysSite.getTypeCode())){
            where.eq("type_code",sysSite.getTypeCode());
        }

        if(ObjectUtils.isNotEmpty(sysSite.getFinCenterFlag())){
            where.eq("fin_center_flag",sysSite.getFinCenterFlag());
        }
        if(ObjectUtils.isNotEmpty(sysSite.getAllocateCenterFlag())){
            where.eq("allocate_center_flag",sysSite.getAllocateCenterFlag());
        }
        if(ObjectUtils.isNotEmpty(sysSite.getParentId())){
            where.eq("parent_id",sysSite.getParentId());
            //todo 临时加的判断 是否需要包含父级id的数据
            if("include".equals(sysSite.getRemark())) {
                where.or().eq("id", sysSite.getParentId());
            }
        }

//        where.setEntity(sysSite);
        return baseMapper.selectList(where);
    }


    /**
     * 递归列表
     */
    private void recursionFn(List<SysSite> list, SysSite t) {
        // 得到子节点列表
        List<SysSite> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysSite tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<SysSite> it = childList.iterator();
                while (it.hasNext()) {
                    SysSite n = (SysSite) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysSite> getChildList(List<SysSite> list, SysSite t) {
        List<SysSite> tlist = new ArrayList<SysSite>();
        Iterator<SysSite> it = list.iterator();
        while (it.hasNext()) {
            SysSite n = (SysSite) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysSite> list, SysSite t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    /**
     * 构建前端所需要树结构
     *
     * @param sysSites 网点列表
     * @return 树结构列表
     */
    @Override
    public List<SysSite> buildSiteTree(List<SysSite> sysSites) {
        List<SysSite> returnList = new ArrayList<SysSite>();
        List<Long> tempList = new ArrayList<Long>();
        for (SysSite site : sysSites) {
            tempList.add(site.getId());
        }
        for (Iterator<SysSite> iterator = sysSites.iterator(); iterator.hasNext(); ) {
            SysSite sysSite = (SysSite) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(sysSite.getParentId())) {
                recursionFn(sysSites, sysSite);
                returnList.add(sysSite);
            }
        }
        if (returnList.isEmpty()) {
            returnList = sysSites;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param sysSites 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelectCode> buildSiteTreeSelect(List<SysSite> sysSites) {
        List<SysSite> siteTrees = buildSiteTree(sysSites);
        return siteTrees.stream().map(TreeSelectCode::new).collect(Collectors.toList());
    }


    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Integer> selectSiteListByRoleId(Long roleId) {
        return sysSiteMapper.selectSiteListByRoleId(roleId);
    }

    /**
     * 校验网点名称是否唯一
     *
     * @param sysSite 部门信息
     * @return 结果
     */
    @Override
    public String checkSiteNameUnique(SysSite sysSite) {
        Long sysSiteId = StringUtils.isNull(sysSite.getId()) ? -1L : sysSite.getId();

//        SysSite sysSite1=new SysSite();
//        sysSite1.setSiteNameChinese(sysSite.getSiteNameChinese());
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("site_code", sysSite.getSiteCode());
        queryWrapper.or();
        queryWrapper.eq("site_name_chinese", sysSite.getSiteNameChinese());
        SysSite info = baseMapper.selectOne(queryWrapper);
        if (StringUtils.isNotNull(info) && info.getId().longValue() != sysSiteId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 新增网点管理模块
     *
     * @param sysSite 网点管理模块
     * @return 结果
     */
    @Override
    public R insertSysSite(SysSite sysSite) {
        int n = baseMapper.insert(sysSite);
        if (n > 0) {
            return R.ok(sysSite.getId());
        } else {
            return R.failed();
        }

    }

    /**
     * 修改网点管理模块
     *
     * @param sysSite 网点管理模块
     * @return 结果
     */
    @Override
    public int updateSysSite(SysSite sysSite) {
        return baseMapper.updateById(sysSite);
    }

    /**
     * 批量删除网点管理模块
     *
     * @param ids 需要删除的网点管理模块ID
     * @return 结果
     */
    @Override
    public int deleteSysSiteByIds(List<Long> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除网点管理模块信息
     *
     * @param id 网点管理模块ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysSiteById(Long id) throws Exception{
        try {
            SysSite site = baseMapper.selectById(id);
            if (site == null) {
                return 0;
            }
            // int i = baseMapper.deleteById(id);
            //
            // R r = basFeignService.removes(id.toString());
            // if (i == 1 && r != null && r.getCode() == 200) {
            //     return 1;
            // }
            return baseMapper.deleteById(id);
        } catch (Exception e) {
            log.error("删除网点 异常 ：", e);
            throw new Exception(e);
        }
    }

}

