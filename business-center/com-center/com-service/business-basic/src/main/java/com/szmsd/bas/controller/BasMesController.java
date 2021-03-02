package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasCustomer;
import com.szmsd.bas.domain.BasMes;
import com.szmsd.bas.domain.Mes;
import com.szmsd.bas.service.IBasCustomerService;
import com.szmsd.bas.service.IBasMesService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-08-20
 */

@Api(tags = {"消息表"})
@RestController
@RequestMapping("/bas-mes")
public class BasMesController extends BaseController {

    @Resource
    private IBasCustomerService basCustomerService;


    @Resource
    private IBasMesService basMesService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basmes:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasMes basMes) {
        startPage();
        List<BasMes> list = basMesService.selectBasMesList(basMes);
        return getDataTable(list);
    }


    /**
     * 付费网点 查询列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basmes:list')")
    @GetMapping("/lists")
    public TableDataInfo lists(Mes basMes) {
        List<Mes> list = basMesService.list(basMes);
        return getDataTable(list);
    }


    /**
     * 导出汇总模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basmes:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, List<BasMes> basMes) throws IOException {
//        List<BasMes> list = basMesService.selectBasMesList(basMes);
        ExcelUtil<BasMes> util = new ExcelUtil<BasMes>(BasMes.class);
        util.exportExcel(response, basMes, "BasMes");
    }

    /**
     * 导出全部模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basmes:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/exports")
    public void exports(HttpServletResponse response, Mes basMes) throws IOException {
        List<Mes> list = basMesService.list(basMes);
        ExcelUtil<Mes> util = new ExcelUtil<Mes>(Mes.class);
        util.exportExcel(response, list, "Mes");
    }


    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basmes:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasMes basMes) {
        basMes.setCreateTime(new Date());
        basMes.setCompanyName("GFS");
        basMes.setMessageSource("WEB");
        basMes.setPassagewayCode("2");
        basMes.setPaySite(basMes.getCreateSite());
        basMes.setPaySiteCode(basMes.getCreateSiteCode());
        return toOk(basMesService.insertBasMes(basMes));
    }

    /**
     * 新增群发模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basmes:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R adds(@RequestBody List<BasMes> basMesList) {
        for (BasMes basMes : basMesList) {

            if (StringUtils.isNotEmpty(basMes.getCusCode())) {
                BasCustomer basCustomer = new BasCustomer();
                List<BasCustomer> list = basCustomerService.selectBasCustomerList(basCustomer);
                if (list.size() == 0) {
                    return R.failed("找不到该客户信息");
                }
                if (StringUtils.isNotEmpty(list.get(0).getCusTle())){
                    basMes.setIphone(list.get(0).getCusTle());
                    basMes.setMessage(basMes.getMessage());
                }
            }
            if (StringUtils.isNotEmpty(basMes.getReceiverTel())) {
                basMes.setIphone(basMes.getReceiverTel());
                basMes.setMessage(basMes.getMessage());
            }
            if (StringUtils.isNotEmpty(basMes.getSenderTel())) {
                basMes.setIphone(basMes.getSenderTel());
                basMes.setMessage(basMes.getMessage());
            }
            basMes.setCreateTime(new Date());
            basMes.setMessageType(basMes.getMessageType());
            basMes.setCompanyName("GFS");
            basMes.setMessageSource("E3 手工");
            basMes.setPassagewayCode("02");
            basMes.setPaySite(basMes.getCreateSite());
            basMes.setPaySiteCode(basMes.getCreateSiteCode());
            basMesService.insertBasMes(basMes);
        }
        return R.ok();
    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basmes:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasMes basMes) {
        return toOk(basMesService.updateBasMes(basMes));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basmes:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basMesService.deleteBasMesByIds(ids));
    }

}
