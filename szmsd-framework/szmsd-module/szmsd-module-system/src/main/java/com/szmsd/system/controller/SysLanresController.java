package com.szmsd.system.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.system.domain.SysLanres;
import com.szmsd.system.service.ISysLanresService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 多语言配置表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-08-06
 */

@Api(tags = {"多语言配置表"})
@RestController
@RequestMapping("/syslanres")
public class SysLanresController extends BaseController {


    @Resource
    private ISysLanresService sysLanresService;

    /**
     * 查询多语言配置表模块列表
     */
    @PreAuthorize("@ss.hasPermi('system:syslanres:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "查询多语言分页")
    public TableDataInfo list(SysLanres sysLanres) {
        startPage();
        if (StringUtils.isNotEmpty(sysLanres.getParm1())) {
            if (sysLanres.getParm1().equals("1")) {
                sysLanres.setStrid(sysLanres.getParm2());
            }
            if (sysLanres.getParm1().equals("2")) {
                sysLanres.setLan1(sysLanres.getParm2());
            }
            if (sysLanres.getParm1().equals("3")) {
                sysLanres.setLan2(sysLanres.getParm2());
            }
        }
        if (StringUtils.isNotEmpty(sysLanres.getApp()) && sysLanres.getApp().equals("0")){
            sysLanres.setApp(null);
        }
        List<SysLanres> list = sysLanresService.selectSysLanresList(sysLanres);
        return getDataTable(list);
    }


    /**
     * 查询多语言配置表模块列表
     */
    @PreAuthorize("@ss.hasPermi('system:syslanres:list')")
    @GetMapping("/applist")
    @ApiOperation(httpMethod = "GET", value = "查询多语言分页")
    public R applist(SysLanres sysLanres) {
        List<SysLanres> list = sysLanresService.selectSysLanresList(sysLanres);
        return R.ok(list);
    }
    /**
     * 查询多语言配置表模块列表
     */
//    @PreAuthorize("@ss.hasPermi('system:syslanres:list')")
    @GetMapping("/lists")
    @ApiOperation(httpMethod = "GET", value = "查询多语言")
    public R lists(SysLanres sysLanres) {
//    "ar": {
//        "100": "特殊",
//        "101": "特殊1"
//    },
//    "en": {
//        "100": "test",
//        "101": "test"
//    },
//    "zh": {
//        "100": "测试",
//        "101": "测试1"
//    }
        Map datas = new HashMap();
        Map zh = new HashMap();
        Map en = new HashMap();
        Map ar = new HashMap();
        List<SysLanres> list = sysLanresService.selectSysLanresList(sysLanres);
        for (SysLanres sys : list) {
            zh.put(sys.getStrid(), sys.getStrid());
        }
        for (SysLanres sys : list) {
            en.put(sys.getStrid(), sys.getLan1());
        }
        for (SysLanres sys : list) {
            ar.put(sys.getStrid(), sys.getLan2());
        }
        datas.put("zh", zh);
        datas.put("en", en);
        datas.put("ar", ar);
        return R.ok(datas);
    }


    /**
     * 导出多语言配置表模块列表
     */
    @PreAuthorize("@ss.hasPermi('system:syslanres:export')")
    @Log(title = "多语言配置表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, SysLanres sysLanres) throws IOException {
        List<SysLanres> list = sysLanresService.selectLanres(sysLanres);
        ExcelUtil<SysLanres> util = new ExcelUtil<SysLanres>(SysLanres.class);
        util.exportExcel(response, list, "SysLanres");
    }


    /**
     * 新增多语言配置表模块
     */
    @PreAuthorize("@ss.hasPermi('system:syslanres:add')")
    @Log(title = "多语言配置表模块", businessType = BusinessType.INSERT)
    @ApiOperation(httpMethod = "POST", value = "新增多语言")
    @PostMapping
    public R add(@RequestBody SysLanres sysLanres) {
        SysLanres sysLanres1 = new SysLanres();
        sysLanres1.setStrid(sysLanres.getStrid());
        List<SysLanres> list = sysLanresService.selectSysLanres(sysLanres1);
        if (list.size() != 0) {
            return R.failed("多语言重复" + sysLanres.getStrid());
        }
        String uid = UUID.randomUUID().toString().substring(0, 8);
        sysLanres.setCode(uid);
        sysLanres.setCreateTime(new Date());
        sysLanresService.insertSysLanres(sysLanres);
        return R.ok();
    }

    /**
     * 修改多语言
     */
    @PreAuthorize("@ss.hasPermi('BasProductType:basProductType:edit')")
    @Log(title = "修改产品类型列表", businessType = BusinessType.UPDATE)
    @ApiOperation(httpMethod = "PUT", value = "修改多语言")
    @PutMapping
    public R edit(@RequestBody SysLanres sysLanres) {
        sysLanres.setUpdateTime(new Date());

        return toOk(sysLanresService.updateSysLanres(sysLanres));
    }

    /**
     * 新增多语言配置表模块
     */
    @PreAuthorize("@ss.hasPermi('system:syslanres:add')")
    @Log(title = "多语言配置表模块", businessType = BusinessType.INSERT)
    @ApiOperation(httpMethod = "POST", value = "新增多语言")
    @PostMapping("/add")
    public R adds(@RequestBody List<SysLanres> sysLanresList) {
        for (SysLanres sysLanres : sysLanresList) {
            if (sysLanres.getStrid() == null) {
                continue;
            }
            SysLanres sysLanres1 = new SysLanres();
            sysLanres1.setStrid(sysLanres.getStrid());
            List<SysLanres> list = sysLanresService.selectSysLanres(sysLanres);
            if (list.size() != 0) {
                continue;
            }
            String uid = UUID.randomUUID().toString().substring(0, 8);
            sysLanres.setCode(uid);
            sysLanresService.insertSysLanres(sysLanres);
        }
        return R.ok();
    }


    /**
     * 删除多语言配置表模块
     */
    @PreAuthorize("@ss.hasPermi('system:syslanres:remove')")
    @Log(title = "多语言配置表模块", businessType = BusinessType.DELETE)
    @ApiOperation(httpMethod = "DELETE", value = "删除多语言")
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        if (StringUtils.isNotEmpty(ids)) {
            return toOk(sysLanresService.deleteSysLanresByIds(ids));
        }
        return R.ok();
    }

    @Log(title = "导入订单", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    @ApiOperation(httpMethod = "POST", value = "导入订单列表")
    public R importExcel(MultipartFile file) {
        try {
            return this.sysLanresService.importExcel(file);
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("", e);
            return R.failed(e.getMessage());
        }
    }

}
