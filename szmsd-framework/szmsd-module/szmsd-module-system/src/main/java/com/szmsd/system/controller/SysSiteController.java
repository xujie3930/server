package com.szmsd.system.controller;

import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.system.api.domain.SysSite;
import com.szmsd.system.domain.dto.SysSiteDto;
import com.szmsd.system.service.ISysSiteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网点管理 前端控制器
 * </p>
 *
 * @author lzw
 * @since 2020-06-19
 */

@Api(tags = {"网点管理"})
@RestController
@RequestMapping("/site")
public class SysSiteController extends BaseController {

    @Resource
    private ISysSiteService sysSiteService;

    /**
     * 查询网点管理模块列表
     */
    @PreAuthorize("@ss.hasPermi('system:site:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "查询网点管理列表")
    public R list(SysSiteDto sysSiteDto) {
        SysSite sysSite = new SysSite();
        BeanUtils.copyBeanProp(sysSite, sysSiteDto);

        List<SysSite> list = sysSiteService.selectSysSiteList(sysSite);
        return R.ok(list);
    }

    /**
     * 查询所有网点列表
     */
    @PreAuthorize("@ss.hasPermi('system:site:listAll')")
    @GetMapping("/listAll")
    @ApiOperation(httpMethod = "GET", value = "查询网点管理列表")
    public R listAll() {
        List<SysSite> list = sysSiteService.listAll();
        return R.ok(list);
    }


    @GetMapping("/alertList")
    @ApiOperation(httpMethod = "GET", value = "弹出菜单网点列表查询")
    public TableDataInfo<SysSite> alertList(SysSiteDto sysSiteDto) {
        SysSite sysSite = new SysSite();
        BeanUtils.copyBeanProp(sysSite, sysSiteDto);
        startPage();
        List<SysSite> list = sysSiteService.selectAlertSysSiteList(sysSite);
        return getDataTable(list);
    }

    /**
     * 查询网点列表（排除节点）
     */
    @PreAuthorize("@ss.hasPermi('system:site:list')")
    @GetMapping("/list/exclude/{id}")
    @ApiOperation(httpMethod = "GET", value = "查询网点列表（排除节点）")
    public R excludeChild(@PathVariable(value = "id", required = false) Long id) {
        List<SysSite> sites = sysSiteService.selectSysSiteList(new SysSite());
        Iterator<SysSite> it = sites.iterator();
        while (it.hasNext()) {
            SysSite d = (SysSite) it.next();
            if (d.getId().intValue() == id
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), id + "")) {
                it.remove();
            }
        }
        return R.ok(sites);
    }

    /**
     * 导出网点管理模块列表
     */
//    @PreAuthorize("@ss.hasPermi('system:site:export')")
//    @Log(title = "网点管理模块", businessType = BusinessType.EXPORT)
//    @GetMapping("/export")
//    @ApiOperation(httpMethod = "GET", value = "导出网点管理列表")
//    public void export(HttpServletResponse response, SysSiteDto sysSiteDto) throws IOException {
//        SysSite sysSite=new SysSite();
//        BeanUtils.copyBeanProp(sysSite,sysSiteDto);
//        List<SysSite> list = sysSiteService.selectSysSiteList(sysSite);
//        ExcelUtil<SysSite> util = new ExcelUtil<SysSite>(SysSite.class);
//        util.exportExcel(response, list, "网点管理");
//    }

    /**
     * 获取网点管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:site:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(httpMethod = "GET", value = "获取网点管理详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(sysSiteService.selectSysSiteById(id));
    }


    /**
     * 根据siteCode获取网点管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:site:query')")
    @GetMapping(value = "info/{siteCode}")
    @ApiOperation(httpMethod = "GET", value = "根据siteCode获取网点管理详细信息")
    public R info(@PathVariable("siteCode") String siteCode) {
        return R.ok(sysSiteService.selectSysSiteBySiteCode(siteCode));
    }

    /**
     * 根据siteCode获取其下的所有子网点信息
     */
    @PreAuthorize("@ss.hasPermi('system:site:query')")
    @GetMapping(value = "getSubSite/{siteCode}")
    @ApiOperation(httpMethod = "GET", value = "根据siteCode获取其下的所有子网点信息")
    public R getSubSite(@PathVariable("siteCode") String siteCode) {
        return R.ok(sysSiteService.getSubSite(siteCode));
    }


    /**
     * 获取网点下拉树列表
     */
    @GetMapping("/treeselect")
    @ApiOperation(httpMethod = "GET", value = "获取网点下拉树列表")
    public R treeselect(SysSiteDto sysSiteDto) {
        SysSite sysSite = new SysSite();
        BeanUtils.copyBeanProp(sysSite, sysSiteDto);
        List<SysSite> sysSites = sysSiteService.selectSysSiteList(sysSite);
        return R.ok(sysSiteService.buildSiteTreeSelect(sysSites));
    }

    /**
     * 加载对应角色网点列表树
     */
    @GetMapping(value = "/roleSiteTreeselect/{roleId}")
    @ApiOperation(httpMethod = "GET", value = "加载对应角色网点列表树")
    public R roleSiteTreeselect(@PathVariable("roleId") Long roleId) {

        List<SysSite> sysSites = sysSiteService.selectSysSiteList(new SysSite());
        Map map = new HashMap<>();
        // todo 此处修改ajax.put返回  R.ok(map)
        map.put("checkedKeys", sysSiteService.selectSiteListByRoleId(roleId));
        map.put("sysSites", sysSiteService.buildSiteTreeSelect(sysSites));
        return R.ok(map);
    }

    /**
     * 新增网点管理模块
     */
    @PreAuthorize("@ss.hasPermi('system:site:add')")
    @Log(title = "网点管理模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(httpMethod = "POST", value = "新增网点管理")
    public R add(@RequestBody SysSiteDto sysSiteDto) {
        SysSite sysSite = new SysSite();
        BeanUtils.copyBeanProp(sysSite, sysSiteDto);
        if (UserConstants.NOT_UNIQUE.equals(sysSiteService.checkSiteNameUnique(sysSite))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM010, sysSite.getSiteNameChinese());
        }
//        sysSite.setCreateBy(SecurityUtils.getLoginUser().getUserId().toString());
//        sysSite.setCreateByName(SecurityUtils.getUsername());
        return sysSiteService.insertSysSite(sysSite);
    }

    /**
     * 修改网点管理模块
     */
    @PreAuthorize("@ss.hasPermi('system:site:edit')")
    @Log(title = "网点管理模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(httpMethod = "PUT", value = "修改网点管理")
    public R edit(@Validated @RequestBody SysSiteDto sysSiteDto) {
        if (sysSiteDto.getId() == null) {
            return R.failed("id不能为空");
        }
        SysSite sysSite = new SysSite();
        BeanUtils.copyBeanProp(sysSite, sysSiteDto);
        if (UserConstants.NOT_UNIQUE.equals(sysSiteService.checkSiteNameUnique(sysSite))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM010, sysSite.getSiteNameChinese());
        } else if (sysSite.getParentId().equals(sysSite.getId())) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM011, sysSite.getSiteNameChinese());
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, sysSite.getStatus())
                && sysSiteService.selectNormalChildrenSiteById(sysSite.getId()) > 0) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM012);
        }
//        sysSite.setUpdateByName(SecurityUtils.getUsername());
        return toOk(sysSiteService.updateSysSite(sysSite));
    }

    /**
     * 删除网点管理模块
     */
    @PreAuthorize("@ss.hasPermi('system:site:remove')")
    @Log(title = "网点管理模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove/{id}")
    @ApiOperation(httpMethod = "DELETE", value = "删除网点管理")
    public R remove(@PathVariable Long id) throws Exception {
        SysSite sysSite = new SysSite();
        sysSite.setParentId(id);
        sysSite.setDelFlag("1");
        List<SysSite> list = sysSiteService.selectSysSiteList(sysSite);
        if (list.size() > 0) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM013);
        }
        return toOk(sysSiteService.deleteSysSiteById(id));
    }


}
