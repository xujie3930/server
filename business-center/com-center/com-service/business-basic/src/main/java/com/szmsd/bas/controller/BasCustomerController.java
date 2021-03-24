package com.szmsd.bas.controller;

import com.alibaba.fastjson.JSONArray;
import com.szmsd.bas.api.domain.BasCustomer;
import com.szmsd.bas.api.domain.BasUser;
import com.szmsd.bas.driver.UpdateRedis;
import com.szmsd.bas.service.IBasCustomerService;
import com.szmsd.common.core.domain.Files;
import com.szmsd.common.core.domain.FilesDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.CodeToNameEnum;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.enums.FileTypeEnum;
import com.szmsd.common.core.utils.FileUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-06-19
 */

@Api(tags = {"客户模块"})
@RestController
@RequestMapping("/bas-customer")
public class BasCustomerController extends BaseController {

    private final Environment env;

    @Autowired
    public BasCustomerController(Environment env) {
        this.env = env;
    }

    @Resource
    private IBasCustomerService basCustomerService;

    @Autowired
    BasUserController basUserController;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bascustomer:list')")
    @ApiOperation(value = "查询客户列表", notes = "查询客户列表('bas:bascustomer:list')")
    @GetMapping("/list")

    public TableDataInfo list(BasCustomer basCustomer) {
        startPage();
        List<BasCustomer> list = basCustomerService.selectBasCustomerList(basCustomer);
        return getDataTable(list);
    }

    /**
     * 查询模块列表fegin
     */
    @ApiOperation(value = "查询客户列表", notes = "查询客户列表")
    @PostMapping("/lists")
    public R<List<BasCustomer>> lists(@RequestBody BasCustomer basCustomer) {
        List<BasCustomer> list = basCustomerService.selectBasCustomerList(basCustomer);
        return R.ok(list);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bascustomer:export')")
    @ApiOperation(value = "导出客户列表", notes = "导出客户列表('bas:bascustomer:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasCustomer basCustomer) throws IOException {
        List<BasCustomer> list = basCustomerService.selectBasCustomerList(basCustomer);
        ExcelUtil<BasCustomer> util = new ExcelUtil<BasCustomer>(BasCustomer.class);
        util.exportExcel(response, list, "BasCustomer");
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascustomer:add')")
    @ApiOperation(value = "新增客户列表", notes = "新增客户列表('bas:bascustomer:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @UpdateRedis(type = CodeToNameEnum.BAS_CUSTOMER)
    @PostMapping
    public R add(@RequestBody BasCustomer basCustomer) {
        if (StringUtils.isNotEmpty(basCustomer.getCusCode())) {
            BasCustomer basCustomer1 = new BasCustomer();
            basCustomer1.setCusCode(basCustomer.getCusCode());
            List<BasCustomer> list = basCustomerService.selectBasCustomerList(basCustomer1);
            if (list.size() != 0) {
                return R.failed("客户编号重复");
            }
        }
        basCustomer.setCusName(basCustomer.getCusAbbverviation());
        String uid = UUID.randomUUID().toString().substring(0, 8);
        if (basCustomer.getPicture() != null) {
            basCustomer.setPicture1(JSONArray.toJSON(basCustomer.getPicture()).toString());
        }
        basCustomer.setId(uid);
        Date date = new Date();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String str = sim.format(date);
        try {
            Date da1 = sim.parse(str);
            basCustomer.setCreateTime(da1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return toOk(basCustomerService.insertBasCustomer(basCustomer));
    }

    /**
     * 导入模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascustomer:add')")
    @ApiOperation(value = "导入客户列表", notes = "导入客户列表('bas:bascustomer:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @UpdateRedis(type = CodeToNameEnum.BAS_CUSTOMER)
    @PostMapping("/listadd")
    public R add(@RequestBody List<BasCustomer> basCustomerList) {
        R<Object> r = R.ok();
        List<BasCustomer> basCustomerArrayList = new ArrayList<BasCustomer>();
        for (BasCustomer basCustomer : basCustomerList) {
            if (!StringUtils.isNotEmpty(basCustomer.getCusCode())) {
                basCustomer.setMessage(ExceptionMessageEnum.EXPBASIS013.toString());
                basCustomerArrayList.add(basCustomer);
                continue;
            }
            BasCustomer basCustomer1 = new BasCustomer();
            basCustomer1.setCusCode(basCustomer.getCusCode());
            List<BasCustomer> list = basCustomerService.selectBasCustomerList(basCustomer1);
            if (list.size() != 0) {
                basCustomer.setMessage(ExceptionMessageEnum.EXPBASIS013.toString());
                basCustomerArrayList.add(basCustomer);
                continue;
            }
            if (!StringUtils.isNotEmpty(basCustomer.getCusAbbverviation())) {
                basCustomer.setMessage("客户简称不能为空");
                basCustomerArrayList.add(basCustomer);
                continue;
            }
            String uid = UUID.randomUUID().toString().substring(0, 8);
            basCustomer.setId(uid);
            basCustomer.setCusName(basCustomer.getCusAbbverviation());
            basCustomer.setCreateTime(new Date());
            basCustomer.setCreateId(SecurityUtils.getLoginUser().getUserId().toString());
            basCustomer.setCreateByName(SecurityUtils.getLoginUser().getUsername());

            BasUser basUser = new BasUser();
            basUser.setCusCode(basCustomer.getCusCode());
            basUser.setCusName(basCustomer.getCusAbbverviation());
            basUser.setSiteCode(basCustomer.getSiteCode());
            basUser.setSiteName(basCustomer.getSiteName());
            basUser.setNickName(basCustomer.getCusCode());
            basUser.setPassword("123456");
            basUser.setAdmIden("1");
            basUser.setPhone(basCustomer.getCusTle());
            basUser.setParm("1");
            basUser.setName(basCustomer.getCusAbbverviation());
            R r1 = basUserController.add(basUser);
            if (r1.getCode() != 200) {
                basCustomer.setMessage(r1.getMsg());
                basCustomerArrayList.add(basCustomer);
                continue;
            }
            if (r1.getCode()==200){
                basCustomerService.insertBasCustomer(basCustomer);
            }
        }
        if (basCustomerArrayList.size() == 0) {
            return R.ok();
        }
        r = R.failed();
        r.setData(basCustomerArrayList);
        return r;
    }

    @GetMapping("/downloadImportTemplate")
    @ApiOperation(httpMethod = "GET", value = "下载订单导入模板")
    public void downloadImportTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment; filename=" +
                new String("Customer information template.xls".getBytes("gb2312"), "iso8859-1"));
        InputStream is = this.getClass().getResourceAsStream("/static/cus/template/Customer information template.xls");
        int i;
        while ((i = is.read()) != -1) {
            response.getOutputStream().write(i);
        }
        //response.sendRedirect("template/order import template.xls");
        response.getOutputStream().flush();
    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascustomer:edit')")
    @ApiOperation(value = "修改客户列表", notes = "修改客户列表('bas:bascustomer:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @UpdateRedis(type = CodeToNameEnum.BAS_CUSTOMER)
    @PutMapping
    public R edit(@RequestBody BasCustomer basCustomer) {
        Date date = new Date();
        if (basCustomer.getPicture().size() == 0) {
            basCustomer.setPicture1("[]");
        }
        if (StringUtils.isNotEmpty(basCustomer.getPicture())) {
            basCustomer.setPicture1(JSONArray.toJSON(basCustomer.getPicture()).toString());
        }
        SimpleDateFormat sim = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String str = sim.format(date);
        try {
            Date da = sim.parse(str);
            basCustomer.setUpdateTime(da);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        basCustomer.setCusName(basCustomer.getCusAbbverviation());
        basCustomer.setUpdateBy(SecurityUtils.getUsername());
        return toOk(basCustomerService.updateBasCustomer(basCustomer));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascustomer:remove')")
    @ApiOperation(value = "删除客户列表", notes = "删除客户列表('bas:bascustomer:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @UpdateRedis(type = CodeToNameEnum.BAS_CUSTOMER)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basCustomerService.deleteBasCustomerByIds(ids));
    }


    /**
     * 图片上传
     */
    @ApiOperation(value = "图片上传", notes = "图片上传")
    @PostMapping("/addimg")
    public R addimg(@RequestParam("file") MultipartFile[] myfiles) {
        List<String> list = new ArrayList<>();
        for (MultipartFile myfile : myfiles) {
            log.info("图片上传****start***********");
            Files files = FileUtil.getFileUrl(new FilesDto()
                    .setUrl(env.getProperty("file.url"))
                    .setMyfile(myfile)
                    .setUploadFolder(env.getProperty("file.uploadFolder"))
                    .setType(FileTypeEnum.USER)
                    .setMainUploadFolder(env.getProperty("file.mainUploadFolder")));
            log.info("图片上传****end***********,{}", files);
            list.add(files.getUrl());
        }
        return R.ok(list);
    }

    @Log(title = "导入客户资料excel", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    @ApiOperation(httpMethod = "POST", value = "导入订单列表")
    public R importExcel(MultipartFile file) {
        try {
            return this.basCustomerService.importExcel(file);
        } catch (Exception e) {
            log.error("", e);
            return R.failed(e.getMessage());
        }
    }
}
