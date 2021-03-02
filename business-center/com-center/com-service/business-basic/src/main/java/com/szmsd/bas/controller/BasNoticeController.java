package com.szmsd.bas.controller;

import com.alibaba.nacos.common.utils.UuidUtils;
import com.szmsd.bas.domain.*;
import com.szmsd.bas.service.IBasAppMesService;
import com.szmsd.bas.service.IBasE3MesService;
import com.szmsd.bas.service.IBasEmployeesService;
import com.szmsd.bas.service.IBasNoticeService;
import com.szmsd.common.core.domain.Files;
import com.szmsd.common.core.domain.FilesDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.FileTypeEnum;
import com.szmsd.common.core.utils.FileUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 公告通知明细 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-10-14
 */

@Api(tags = {"公告通知明细"})
@RestController
@RequestMapping("/bas-notice")
public class BasNoticeController extends BaseController {

    private final Environment env;

    @Autowired
    public BasNoticeController(Environment env) {
        this.env = env;
    }

    @Resource
    private IBasEmployeesService basEmployeesService;

    @Resource
    private IBasNoticeService basNoticeService;

    @Resource
    private IBasAppMesService basAppMesService;

    @Resource
    private IBasE3MesService basE3MesService;

    /**
     * 查询公告通知明细模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasNotice:BasNotice:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasNotice basNotice) {
        startPage();
        List<BasNotice> list = basNoticeService.selectBasNoticeList(basNotice);
        return getDataTable(list);
    }

    /**
     * 新增公告通知明细模块
     */
    @PreAuthorize("@ss.hasPermi('BasNotice:BasNotice:add')")
    @Log(title = "公告通知明细模块", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R add(@RequestBody BasNotice basNotice) {
        String id = UuidUtils.generateUuid().replace("-","");
        if (StringUtils.isNotEmpty(basNotice.getReceiveSiteCode())) {
            BasEmployees basEmployees = new BasEmployees();
            basEmployees.setSiteCodeList(basNotice.getReceiveSiteCode());
            List<BasEmployees> list = basEmployeesService.selectBasEmployeesList(basEmployees);
            List<Emp> empList=new ArrayList<>();
            for (BasEmployees basEmp : list) {
                Emp emp = new Emp();
                emp.setUserId(basEmp.getUserId());
                emp.setEmpCode(basEmp.getEmpCode());
                emp.setEmpName(basEmp.getEmpName());
                empList.add(emp);
            }
            basNotice.setReceiveList(empList);
        }
        //e3端
        if (basNotice.getReceivePlatform().contains("E3")) {
            List<BasE3Mes> list = new ArrayList<>();
            for (Emp emp : basNotice.getReceiveList()) {
                BasE3Mes basE3Mes = new BasE3Mes();
                basE3Mes.setSourceId(id);
                basE3Mes.setContent(basNotice.getContent());
                basE3Mes.setEmpCode(emp.getEmpCode());
                basE3Mes.setEmpName(emp.getEmpName());
                basE3Mes.setParentTypeName(basNotice.getParentTypeName());
                basE3Mes.setParentTypeCode(basNotice.getParentTypeCode());
                basE3Mes.setSubTypeCode(basNotice.getSubTypeCode());
                basE3Mes.setSubTypeName(basNotice.getSubTypeName());
                basE3Mes.setTitle(basNotice.getTitle());
                if(StringUtils.isNotEmpty(basNotice.getAttachmentUrl())){
                    basE3Mes.setAttachmentFlag("1");
                    basE3Mes.setAttachmentUrl(basNotice.getAttachmentUrl());
                }
                basE3Mes.setPushTime(new Date());
                list.add(basE3Mes);
            }
            basE3MesService.saveBatch(list);
        }
        //app端
        if (basNotice.getReceivePlatform().contains("APP")) {
            List<BasAppMes> list = new ArrayList<>();
            for (Emp emp : basNotice.getReceiveList()) {
                BasAppMes basAppMes = new BasAppMes();
                basAppMes.setSourceId(id);
                basAppMes.setContent(basNotice.getContent());
                basAppMes.setUserId(emp.getUserId());
                basAppMes.setEmpCode(emp.getEmpCode());
                basAppMes.setEmpName(emp.getEmpName());
                basAppMes.setParentTypeCode(basNotice.getParentTypeCode());
                basAppMes.setParentTypeName(basNotice.getParentTypeName());
                basAppMes.setSubTypeCode(basNotice.getSubTypeCode());
                basAppMes.setSubTypeName(basNotice.getSubTypeName());
                basAppMes.setTitle(basNotice.getTitle());
                if(StringUtils.isNotEmpty(basNotice.getAttachmentUrl())){
                    basAppMes.setImgUrl(basNotice.getAttachmentUrl());
                }
                basAppMes.setPushTime(new Date());
                list.add(basAppMes);
            }
            basAppMesService.saveBatch(list);
        }
        basNotice.setId(id);
        return toOk(basNoticeService.insertBasNotice(basNotice));
    }

    /**
     * 修改公告通知明细模块
     */
    @PreAuthorize("@ss.hasPermi('BasNotice:BasNotice:edit')")
    @Log(title = "公告通知明细模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasNotice basNotice) {
        basNotice.setUpdateTime(new Date());
        return toOk(basNoticeService.updateBasNotice(basNotice));
    }

    /**
     * 删除公告通知明细模块
     */
    @PreAuthorize("@ss.hasPermi('BasNotice:BasNotice:remove')")
    @Log(title = "公告通知明细模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        BasNotice basNotice = new BasNotice();
        basNotice.setId(ids.get(0));
        List<BasNotice> list = basNoticeService.selectBasNoticeList(basNotice);
        String id = list.get(0).getId();
        basAppMesService.deleteBySourceId(id);
        basE3MesService.deleteBySourceId(id);
        return toOk(basNoticeService.deleteBasNoticeByIds(ids));
    }


    /**
     * 文件上传
     */
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @PostMapping("/adddoc")
    public R addimg(@RequestParam("file") MultipartFile[] myfiles) {
        List<String> list = new ArrayList<>();
        for (MultipartFile myfile : myfiles) {
            log.info("文件上传****start***********");
            Files files = FileUtil.getFileUrl(new FilesDto()
                    .setUrl(env.getProperty("file.url"))
                    .setMyfile(myfile)
                    .setUploadFolder(env.getProperty("file.uploadFolder"))
                    .setType(FileTypeEnum.NOTICE)
                    .setMainUploadFolder(env.getProperty("file.mainUploadFolder")));
            log.info("文件上传****end***********,{}", files);
            list.add(files.getUrl());
        }
        return R.ok(list);
    }
}
