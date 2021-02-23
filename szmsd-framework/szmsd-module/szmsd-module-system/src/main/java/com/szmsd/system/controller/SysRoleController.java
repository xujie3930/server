package com.szmsd.system.controller;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.szmsd.common.core.annotation.CodeToName;
import com.szmsd.system.domain.dto.SysRoleMenuDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.domain.SysRole;
import com.szmsd.system.service.ISysRoleService;

/**
 * 角色信息
 * 
 * @author lzw
 */
@RestController
@RequestMapping("/role")
@Api(tags = "角色信息")
public class SysRoleController extends BaseController
{
    @Resource
    private ISysRoleService roleService;

    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "获取角色列表")
    @CodeToName
    public TableDataInfo list(SysRole role)
    {
        startPage();
        List<SysRole> list = roleService.selectRoleList(role);
        return getDataTable(list);
    }

    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    @ApiOperation(httpMethod = "POST", value = "导出角色列表数据")
    public void export(HttpServletResponse response, SysRole role) throws IOException
    {
        List<SysRole> list = roleService.selectRoleList(role);
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    @ApiOperation(httpMethod = "GET", value = "根据角色编号获取详细信息")
    public R getInfo(@PathVariable Long roleId)
    {
        return R.ok(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(httpMethod = "POST", value = "新增角色")
    public R add(@Validated @RequestBody SysRole role)
    {
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role)))
        {
            return R.failed("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
//        else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role)))
//        {
//            return R.failed("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
//        }
        role.setCreateByName(SecurityUtils.getUsername());
        return toOk(roleService.insertRole(role));

    }


    /**
     * 新增角色绑定菜单
     */
    @PreAuthorize("@ss.hasPermi('system:role:addRoleMenuByRoleId')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping("addRoleMenuByRoleId")
    @ApiOperation(httpMethod = "POST", value = "新增角色绑定菜单")
    public R addRoleMenuByRoleId(@Validated @RequestBody SysRoleMenuDto sysRoleMenuDto)
    {
        if (roleService.selectRoleById(sysRoleMenuDto.getRoleId())==null)
        {
            return R.failed("角色不存在");
        }
//        else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role)))
//        {
//            return R.failed("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
//        }
//        role.setCreateByName(SecurityUtils.getUsername());
        return toOk(roleService.insertRoleMenuByRoleId(sysRoleMenuDto));

    }

    /**
     * 修改角色绑定菜单
     */
    @PreAuthorize("@ss.hasPermi('system:role:editRoleMenuByRoleId')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PostMapping("editRoleMenuByRoleId")
    @ApiOperation(httpMethod = "POST", value = "修改角色绑定菜单")
    public R editRoleMenuByRoleId(@Validated @RequestBody SysRoleMenuDto sysRoleMenuDto)
    {
        if (roleService.selectRoleById(sysRoleMenuDto.getRoleId())==null)
        {
            return R.failed("角色不存在");
        }
//        else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role)))
//        {
//            return R.failed("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
//        }
//        role.setCreateByName(SecurityUtils.getUsername());
        return toOk(roleService.updateRoleMenuByRoleId(sysRoleMenuDto));

    }


    /**
     * 修改保存角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(httpMethod = "PUT", value = "修改保存角色")
    public R edit(@Validated @RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role)))
        {
            return R.failed("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
//        else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role)))
//        {
//            return R.failed("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
//        }
        role.setUpdateByName(SecurityUtils.getUsername());
        return toOk(roleService.updateRole(role));
    }




    /**TODO 沙特不需要
     * 修改保存数据权限
     */
//    @PreAuthorize("@ss.hasPermi('system:role:edit')")
//    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
//    @PutMapping("/dataScope")
//    @ApiOperation(httpMethod = "PUT", value = "修改保存数据权限")
//    public R dataScope(@RequestBody SysRole role)
//    {
//        roleService.checkRoleAllowed(role);
//        return toOk(roleService.authDataScope(role));
//    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    @ApiOperation(httpMethod = "PUT", value = "状态修改")
    public R changeStatus(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        role.setUpdateByName(SecurityUtils.getUsername());
        return toOk(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    @ApiOperation(httpMethod = "DELETE", value = "删除角色")
    public R remove(@PathVariable Long[] roleIds)
    {
        return toOk(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    @ApiOperation(httpMethod = "GET", value = "获取角色选择框列表")
    public R optionselect()
    {
        return R.ok(roleService.selectRoleAll());
    }
}