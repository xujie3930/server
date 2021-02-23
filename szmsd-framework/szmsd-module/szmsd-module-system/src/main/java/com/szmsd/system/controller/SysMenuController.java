package com.szmsd.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.system.domain.dto.SysMenuDto;
import com.szmsd.system.domain.dto.SysMenuRoleDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.domain.SysMenu;
import com.szmsd.system.service.ISysMenuService;

import javax.annotation.Resource;

/**
 * 菜单信息
 *
 * @author lzw
 */
@RestController
@RequestMapping("/menu")
@Api(tags = "菜单信息")
public class SysMenuController extends BaseController {
    @Resource
    private ISysMenuService menuService;


//    @Resource
//    private BasFeignService basFeignService;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "获取菜单列表")
    public R list(SysMenuDto sysMenuDto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        SysMenu menu = new SysMenu();
        BeanUtils.copyBeanProp(menu, sysMenuDto);
//        Long userId = 1L;
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
//todo 存储了数据字典键值对，不调用基础数据feign
     /*   R r = basFeignService.list(BaseConstants.SUB_CODE, BaseConstants.SUB_NAME);
        //封装数据字典 机构级别名称 到菜单列表展示
        if (menus.size() > 0 && r != null && r.getData() != null) {
            JSONArray jsonArray = JSON.parseObject(JSONObject.toJSONString(r.getData())).getJSONArray(BaseConstants.SUB_NAME);
            List<BasSub> list = jsonArray.toJavaList(BasSub.class);
            for (SysMenu sysMenu : menus) {
                for (BasSub bas : list) {
                    if (bas.getSubCode().equals(sysMenu.getSiteRankCode())) {
                        sysMenu.setSiteRankCode(bas.getSubName());
                    }
                }
            }
        }*/
        return R.ok(menus);
    }


    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    @ApiOperation(httpMethod = "GET", value = "根据菜单编号获取详细信息")
    public R getInfo(@PathVariable Long menuId) {
        return R.ok(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    @ApiOperation(httpMethod = "GET", value = "获取菜单下拉树列表")
    public R treeselect(SysMenuDto sysMenuDto) {

        SysMenu menu = new SysMenu();
        BeanUtils.copyBeanProp(menu, sysMenuDto);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
//        Long userId = 1L;
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return R.ok(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @ApiOperation(httpMethod = "GET", value = "加载对应角色菜单列表树")
    @GetMapping(value = "/roleMenuTreeselect")
    public R roleMenuTreeselect(SysMenuRoleDto sysMenuRoleDto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
//        Long userId = 1L;
        List<SysMenu> menus = menuService.selectMenuList(userId, sysMenuRoleDto.getType());
        Map map = new HashMap();
        map.put("checkedKeys", menuService.selectMenuListByRoleId(sysMenuRoleDto.getRoleId(), sysMenuRoleDto.getType()));
        map.put("menus", menuService.buildMenuTreeSelect(menus));
        return R.ok(map);
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(httpMethod = "POST", value = "新增菜单")
    public R add(@Validated @RequestBody SysMenuDto sysMenuDto) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyBeanProp(menu, sysMenuDto);
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM003, menu.getMenuName());
        }
        menu.setCreateByName(SecurityUtils.getUsername());
        return toOk(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(httpMethod = "PUT", value = "修改菜单")
    public R edit(@Validated @RequestBody SysMenuDto sysMenuDto) {

        SysMenu menu = new SysMenu();
        BeanUtils.copyBeanProp(menu, sysMenuDto);
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM003, menu.getMenuName());
        }
        menu.setUpdateByName(SecurityUtils.getUsername());
        return toOk(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    @ApiOperation(httpMethod = "DELETE", value = "删除菜单")
    public R remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM004);
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM005);
        }
        return toOk(menuService.deleteMenuById(menuId));
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    @ApiOperation(httpMethod = "GET", value = "获取路由信息")
    public R getRouters(@ApiParam("权限类型：1-PC，2-APP,3-VIP") @RequestParam(defaultValue = "1") @PathVariable("type") Integer type) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
//        Long userId = 140L;
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId, type);
        return R.ok(menuService.buildMenus(menus));
    }
}