package com.szmsd.system.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.domain.SysDept;
import com.szmsd.system.service.ISysDeptServiceCopy;

import javax.annotation.Resource;

/**
 * 部门信息
 * 
 * @author lzw
 */
@RestController
@RequestMapping("/dept1")
@Api(tags = "部门信息")
public class SysDeptControllerCopy extends BaseController
{
    @Resource
    private ISysDeptServiceCopy deptService;

    /**
     * 获取部门列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept1:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "获取部门列表")
    public R list(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return R.ok(depts);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @PreAuthorize("@ss.hasPermi('system:dept1:list')")
    @GetMapping("/list/exclude/{deptId}")
    @ApiOperation(httpMethod = "GET", value = "查询部门列表（排除节点）")
    public R excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        Iterator<SysDept> it = depts.iterator();
        while (it.hasNext())
        {
            SysDept d = (SysDept) it.next();
            if (d.getId().intValue() == deptId
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""))
            {
                it.remove();
            }
        }
        return R.ok(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:dept1:query')")
    @GetMapping(value = "/{deptId}")
    @ApiOperation(httpMethod = "GET", value = "根据部门编号获取详细信息")
    public R getInfo(@PathVariable Long deptId)
    {
        return R.ok(deptService.selectDeptById(deptId));
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    @ApiOperation(httpMethod = "GET", value = "获取部门下拉树列表")
    public R treeselect(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return R.ok(deptService.buildDeptTreeSelect(depts));
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    @ApiOperation(httpMethod = "GET", value = "加载对应角色部门列表树")
    public R roleDeptTreeselect(@PathVariable("roleId") Long roleId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        Map map=new HashMap<>();
        // todo 此处修改ajax.put返回  R.ok(map)
        map.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        map.put("depts", deptService.buildDeptTreeSelect(depts));
        return R.ok(map);
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept1:add')")
    @ApiOperation(httpMethod = "POST", value = "新增部门")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody SysDept dept)
    {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return R.failed("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateByName(SecurityUtils.getUsername());
        return toOk(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept1:edit')")
    @ApiOperation(httpMethod = "PUT", value = "修改部门")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody SysDept dept)
    {
        if (UserConstants.NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return R.failed("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        else if (dept.getParentId().equals(dept.getId()))
        {
            return R.failed("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && deptService.selectNormalChildrenDeptById(dept.getId()) > 0)
        {
            return R.failed("该部门包含未停用的子部门！");
        }
        dept.setUpdateByName(SecurityUtils.getUsername());
        return toOk(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept1:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    @ApiOperation(httpMethod = "DELETE", value = "删除部门")
    public R remove(@PathVariable Long deptId)
    {
        if (deptService.hasChildByDeptId(deptId))
        {
            return R.failed("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return R.failed("部门存在用户,不允许删除");
        }
        return toOk(deptService.deleteDeptById(deptId));
    }
}
