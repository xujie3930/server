package com.szmsd.system.mapper;

import com.szmsd.system.api.domain.SysSite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 网点管理 Mapper 接口
 * </p>
 *
 * @author lzw
 * @since 2020-06-19
 */
public interface SysSiteMapper extends BaseMapper<SysSite> {
    /**
     * 查询网点管理数据
     *
     * @param sysSite 部门信息
     * @return 部门信息集合
     */
    List<SysSite> selectSiteList(SysSite sysSite);


    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    public List<Integer> selectSiteListByRoleId(Long roleId);


    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    public int selectNormalChildrenSiteById(Long deptId);



    int selectCountNum(Long siteId);


    SysSite selectSysSiteBySiteCode(@Param("siteCode") String siteCode);

    List<SysSite> getSiteSub(@Param("siteCode") String siteCode);
}
