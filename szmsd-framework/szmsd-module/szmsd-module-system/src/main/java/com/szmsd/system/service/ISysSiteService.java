package com.szmsd.system.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.system.api.domain.SysSite;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.system.domain.vo.TreeSelect;
import com.szmsd.system.domain.vo.TreeSelectCode;

import java.util.List;

/**
 * <p>
 * 网点管理 服务类
 * </p>
 *
 * @author lzw
 * @since 2020-06-19
 */
public interface ISysSiteService extends IService<SysSite> {

    /**
     * 查询网点管理模块
     *
     * @param id 网点管理模块ID
     * @return 网点管理模块
     */
    public SysSite selectSysSiteById(String id);

    /**
     * 根据网点编码获取网点信息
     * @param siteCode
     * @return
     */
    SysSite selectSysSiteBySiteCode(String siteCode);

    /**
     * 根据siteCode获取其下的所有子网点信息
     * @param siteCode
     * @return
     */
    List<SysSite> getSubSite(String siteCode);

    /**
     * 根据ID查询所有子网点（正常状态）
     *
     * @param deptId 部门ID
     * @return 子网点数
     */
    public int selectNormalChildrenSiteById(Long deptId);

    /**
     * 分页查询网点管理模块列表
     *
     * @param sysSite 网点管理模块
     * @return 网点管理模块集合
     */
    public List<SysSite> selectSysSiteList(SysSite sysSite);

    /**
     * 查询所有网点
     * @return
     */
    List<SysSite> listAll();

    /**
     * 弹窗组件查询网点管理模块列表
     *
     * @param sysSite 网点管理模块
     * @return 网点管理模块集合
     */
    public List<SysSite> selectAlertSysSiteList(SysSite sysSite);



    /**
     * 构建前端所需要树结构
     *
     * @param sysSites 网点列表
     * @return 树结构列表
     */
    public List<SysSite> buildSiteTree(List<SysSite> sysSites);
    /**
     * 构建前端所需要下拉树结构
     *
     * @param sysSites 网点列表
     * @return 下拉树结构列表
     */
    public List<TreeSelectCode> buildSiteTreeSelect(List<SysSite> sysSites);


    /**
     * 根据角色ID查询网点树信息
     *
     * @param roleId 角色ID
     * @return 选中网点列表
     */
    public List<Integer> selectSiteListByRoleId(Long roleId);


    /**
     * 校验网点名称是否唯一
     *s
     * @param sysSite 网点信息
     * @return 结果
     */
    public String checkSiteNameUnique(SysSite sysSite);

    /**
     * 新增网点管理模块
     *
     * @param sysSite 网点管理模块
     * @return 结果
     */
    public R insertSysSite(SysSite sysSite);

    /**
     * 修改网点管理模块
     *
     * @param sysSite 网点管理模块
     * @return 结果
     */
    public int updateSysSite(SysSite sysSite);

    /**
     * 批量删除网点管理模块
     *
     * @param ids 需要删除的网点管理模块ID
     * @return 结果
     */
    public int deleteSysSiteByIds(List<Long> ids);

    /**
     * 删除网点管理模块信息
     *
     * @param id 网点管理模块ID
     * @return 结果
     */
    public int deleteSysSiteById(Long id) throws Exception;

}

