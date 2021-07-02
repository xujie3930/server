package com.szmsd.system.controller;

import java.util.*;

import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.system.domain.dto.SysMenuDto;
import com.szmsd.system.domain.dto.SysMenuRoleDto;
import com.szmsd.system.domain.vo.RouterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private BasSellerFeignService basSellerFeignService;


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
        List<RouterVo> list = menuService.buildMenus(menus);
        if (CollectionUtils.isEmpty(list)) {
            return R.ok();
        }
        // 查询实名认证信息，没有实名认证或者实名不通过的客户，不能查看仓储菜单和财务菜单
        // 实名状态 1未实名 2审核中 3实名通过
        String sellerCode = loginUser.getSellerCode();
        boolean filter = false;
        // 又卖家编号的时候才处理
        if (StringUtils.isNotEmpty(sellerCode)) {
            // 查询实名状态
            R<String> r = this.basSellerFeignService.getRealState(sellerCode);
            // 如果不是实名通过，就需要过滤
            if (null != r && !"3".equals(r.getData())) {
                filter = true;
            }
            if (filter) {
                Set<String> set = new HashSet<>(8);
                set.add("storage");
                set.add("bill");
                for (RouterVo routerVo : list) {
                    List<RouterVo> children = routerVo.getChildren();
                    if (CollectionUtils.isNotEmpty(children)) {
                        for (int i = 0; i < children.size(); i++) {
                            RouterVo child = children.get(i);
                            if (set.contains(child.getPath())) {
                                children.remove(i);
                                i--;
                            }
                        }
                    }
                }
            }
        }
        return R.ok(list);
    }
}