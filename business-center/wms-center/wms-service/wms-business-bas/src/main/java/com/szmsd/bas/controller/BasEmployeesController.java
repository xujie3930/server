package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasEmployees;
import com.szmsd.bas.domain.dto.BasWeightDto;
import com.szmsd.bas.domain.dto.BasWeightSectionDto;
import com.szmsd.bas.driver.UpdateRedis;
import com.szmsd.bas.service.IBasEmployeesService;
import com.szmsd.bas.service.IBasWeightSectionService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.CodeToNameEnum;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.domain.dto.SysUserDto;
import com.szmsd.system.api.feign.RemoteUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-06-19
 */

@Api(tags = {"员工模块"})
@RestController
@RequestMapping("/bas-employees")
public class BasEmployeesController extends BaseController {


    @Resource
    private IBasEmployeesService basEmployeesService;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private IBasWeightSectionService weightSectionService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basemployees:list')")
    @ApiOperation(value = "查询员工列表", notes = "查询员工列表('bas:basemployees:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasEmployees basEmployees) {
        startPage();
        List<BasEmployees> list = basEmployeesService.selectBasEmployeesList(basEmployees);
        return getDataTable(list);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basemployees:export')")
    @ApiOperation(value = "导出员工列表", notes = "导出员工列表('bas:basemployees:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasEmployees basEmployees) throws IOException {
        List<BasEmployees> list = basEmployeesService.selectBasEmployeesList(basEmployees);
        ExcelUtil<BasEmployees> util = new ExcelUtil<BasEmployees>(BasEmployees.class);
        util.exportExcel(response, list, "BasEmployees");
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basemployees:add')")
    @ApiOperation(value = "新增员工列表", notes = "新增员工列表('bas:basemployees:add')")
    @UpdateRedis(type = CodeToNameEnum.BAS_EMPLOYEE)
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasEmployees basEmployees) {
        BasEmployees basEmployees1 = new BasEmployees();
        basEmployees1.setEmpCode(basEmployees.getEmpCode());
        List<BasEmployees> list = basEmployeesService.selectBasEmployeesList(basEmployees1);
        if (list.size() != 0) {
            return R.failed("员工编号重复");
        }
        BasEmployees basEmployees2 = new BasEmployees();
//        basEmployees2.setEmpTel(basEmployees.getEmpTel());
//        List<BasEmployees> lists = basEmployeesService.selectBasEmployeesList(basEmployees2);
//        if (lists.size() != 0) {
//            return R.failed("员工手机号重复");
//        }
        String s = SecurityUtils.encryptPassword(basEmployees.getPassword());
        basEmployees.setPassword(s);
        String uid = UUID.randomUUID().toString().substring(0, 8);
        basEmployees.setId(uid);
        basEmployees.setCreateTime(new Date());
        //同步用户表
        SysUserDto sysUserDto = new SysUserDto();
        sysUserDto.setUserName(basEmployees.getEmpCode());
        sysUserDto.setNickName(basEmployees.getEmpName());
        sysUserDto.setPhonenumber(basEmployees.getEmpTel());
        sysUserDto.setPassword(basEmployees.getPassword());
        sysUserDto.setCreateBy(basEmployees.getCreateByName());
        sysUserDto.setSiteCode(basEmployees.getSiteCode());
        if (StringUtils.isNotEmpty(basEmployees.getSpearPassword())){
            sysUserDto.setSpearPassword(SecurityUtils.encryptPassword(basEmployees.getSpearPassword()));
        }
        sysUserDto.setUserType("00");
        sysUserDto.setStatus("0");
        Long[] l = new Long[1];
        l[0] = 2L;
        sysUserDto.setRoleIds(l);
        R r = remoteUserService.baseCopyUserAdd(sysUserDto);
        Map map = (Map) r.getData();
        String userId = MapUtils.getString(map, "userId");
        if (StringUtils.isEmpty(userId)) {
            return R.failed(r.getMsg());
        }
        basEmployees.setUserId(Long.parseLong(userId));
        int i = basEmployeesService.insertBasEmployees(basEmployees);
        if(i > 0){
            //同时生成重量段位
            int i1 = weightSectionService.insertBasWeightSection(new BasWeightSectionDto()
                    .setWeightDto(Arrays.asList(new BasWeightDto().setWeightStart("0").setWeightEnd("1"),
                            new BasWeightDto().setWeightStart("1").setWeightEnd("5"),
                            new BasWeightDto().setWeightStart("5").setWeightEnd("10"),
                            new BasWeightDto().setWeightStart("10").setWeightEnd("1000")
                    )).setUserCode(basEmployees.getEmpCode()).setUserName(basEmployees.getEmpName()));
            if(i1 <= 0){
                log.info("生成重量段失败");
            }
        }
        return R.ok();
    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basemployees:edit')")
    @ApiOperation(value = "修改员工列表", notes = "修改员工列表('bas:basemployees:edit')")
    @UpdateRedis(type = CodeToNameEnum.BAS_EMPLOYEE)
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasEmployees basEmployees) {
        if (!basEmployees.getPassword().equals("******")) {
            if (StringUtils.isNotEmpty(basEmployees.getPassword())) {
                BasEmployees basEmployees1 = new BasEmployees();
                basEmployees1.setPassword(basEmployees.getPassword());
                List<BasEmployees> list = basEmployeesService.selectBasEmployeesList(basEmployees1);
                if (list.size() == 0) {
                    String s = SecurityUtils.encryptPassword(basEmployees.getPassword());
                    basEmployees.setPassword(s);
                }
            }
        } else {
            BasEmployees basEmployees2 = new BasEmployees();
            basEmployees2.setEmpCode(basEmployees.getEmpCode());
            List<BasEmployees> list = basEmployeesService.selectBasEmployeesList(basEmployees2);
            if (list.size() != 0) {
                basEmployees.setPassword(list.get(0).getPassword());
            }
        }
        SysUserDto sysUserDto = new SysUserDto();
        sysUserDto.setUserName(basEmployees.getEmpCode());
        sysUserDto.setPassword(basEmployees.getPassword());
        sysUserDto.setUserId(basEmployees.getUserId());
        sysUserDto.setNickName(basEmployees.getEmpName());
        sysUserDto.setPhonenumber(basEmployees.getEmpTel());
        sysUserDto.setUpdateBy(basEmployees.getUpdateByName());
        sysUserDto.setSiteCode(basEmployees.getSiteCode());
        if (StringUtils.isNotEmpty(basEmployees.getSpearPassword())){
            sysUserDto.setSpearPassword(SecurityUtils.encryptPassword(basEmployees.getSpearPassword()));
        }
//        Long[] l = new Long[1];
//        l[0] = 2L;
//        sysUserDto.setRoleIds(l);
        R rs = remoteUserService.baseCopyUserEdit(sysUserDto);
        if (rs.equals(null)) {
            return R.failed("修改失败");
        }
        basEmployees.setUpdateTime(new Date());
        return toOk(basEmployeesService.updateBasEmployees(basEmployees));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basemployees:remove')")
    @ApiOperation(value = "删除员工列表", notes = "删除员工列表('bas:basemployees:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @UpdateRedis(type = CodeToNameEnum.BAS_EMPLOYEE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        String id = ids.get(0);
        BasEmployees basEmployees=new BasEmployees();
        basEmployees.setId(id);
        List<BasEmployees> list = basEmployeesService.selectBasEmployeesList(basEmployees);
        Long userId = list.get(0).getUserId();
        R remove = remoteUserService.remove(userId);
        if (remove.getCode() !=200 ) {
            return R.failed(remove.getMsg());
        }
        int i = basEmployeesService.deleteBasEmployeesByIds(ids);
        if( i > 0){
            List<String> collect = ListUtils.emptyIfNull(list).stream().map(BasEmployees::getEmpCode).collect(Collectors.toList());
            int i1 = weightSectionService.deleteBasWeightSection(new BasWeightSectionDto().setUserCodes(collect));
            if(i1 <= 0 ){
                log.info("删除失败");
            }
        }
        return toOk(i);
    }

    @PreAuthorize("@ss.hasPermi('bas:basemployees:getEmpByCode')")
    @ApiOperation(value = "根据code获取员工信息", notes = "根据code获取员工信息('bas:basemployees:getEmpByCode')")
    @PostMapping("/getEmpByCode")
    R<BasEmployees> getEmpByCode(@RequestBody BasEmployees basEmployees){
        return this.basEmployeesService.getEmpByCode(basEmployees);
    }

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasEmployees:BasEmployees:list')")
    @ApiOperation(value = "查询员工列表fegin", notes = "查询员工列表fegin")
    @PostMapping("/empList")
    public R<List<BasEmployees>> empList(@RequestBody BasEmployees basEmployees) {
        List<BasEmployees> list = basEmployeesService.selectBasEmployeesList(basEmployees);
        return R.ok(list);
    }
}
