package com.szmsd.system.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.service.ISysMenuService;
import com.szmsd.system.service.ISysPermissionService;
import com.szmsd.system.service.ISysRoleService;

import javax.annotation.Resource;

@Service
public class SysPermissionServiceImpl implements ISysPermissionService
{
    @Resource
    private ISysRoleService roleService;

    @Resource
    private ISysMenuService menuService;

    /**
     * 获取角色数据权限
     * 
     * @param user 用户信息
     * @return 角色权限信息
     */
    @Override
    public Set<String> getRolePermission(Long userId,Integer type)
    {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (SysUser.isAdmin(userId))
        {
            roles.add("admin");
        }
        else
        {
            roles.addAll(roleService.selectRolePermissionByUserId(userId,type));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     * 
     * @param userId 用户信息
     * @return 菜单权限信息
     */
    @Override
    public Set<String> getMenuPermission(Long userId,Integer type)
    {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (SysUser.isAdmin(userId))
        {
            perms.add("*:*:*");
        }
        else
        {
            perms.addAll(menuService.selectMenuPermsByUserId(userId,type));
        }
        return perms;
    }
}
