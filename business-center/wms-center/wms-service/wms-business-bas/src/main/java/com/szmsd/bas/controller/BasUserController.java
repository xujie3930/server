package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasCodeDto;
import com.szmsd.bas.domain.BasUser;
import com.szmsd.bas.service.BasCodeService;
import com.szmsd.bas.service.IBasUserService;
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
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.domain.dto.SysUserDto;
import com.szmsd.system.api.feign.RemoteUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-08-11
 */

@Api(tags = {"客户员工资料"})
@RestController
@RequestMapping("/bas-user")
public class BasUserController extends BaseController {


    @Resource
    private IBasUserService basUserService;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private BasCodeService basCodeService;

    //    @Resource
//    private IBasCustomersService basCustomersService;

    private static final String APP_ID = "cus";
    private static final String SYS_CODE = "CUS_CODE";

    private final Environment env;

    @Autowired
    public BasUserController(Environment env) {
        this.env = env;
    }

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basuser:list')")
    @ApiOperation(value = "查询客户员工资料", notes = "查询客户员工资料")
    @GetMapping("/list")
    public TableDataInfo list(BasUser basUser) {
        startPage();
        List<BasUser> list = basUserService.selectBasUserList(basUser);
        return getDataTable(list);
    }


    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basuser:list')")
    @ApiOperation(value = "查询客户员工资料", notes = "查询客户员工资料")
    @PostMapping("/lists")
    public R<List<BasUser>> lists(@RequestBody BasUser basUser) {
        List<BasUser> list = basUserService.selectBasUserList(basUser);
        return R.ok(list);
    }


    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:basuser:list')")
    @ApiOperation(value = "查询大客户账户员工", notes = "查询大客户账户员工")
    @GetMapping("/getuser")
    public TableDataInfo getuser(BasUser basUser) {
        List list1 = new ArrayList();
        List<BasUser> list = basUserService.selectBasUserList(basUser);
        if (list.size() == 0) {
            return getDataTable(list1);
        }
        startPage();
        BasUser basUser1 = new BasUser();
        basUser1.setCusCode(list.get(0).getCusCode());
        List<BasUser> list2 = basUserService.selectBasUserList(basUser1);
        if (CollectionUtils.isNotEmpty(list2) && list2.size() == 0) {
            return getDataTable(list1);
        }
        return getDataTable(list2);
    }

//    /**
//     * 查询模块列表
//     */
//    @PreAuthorize("@ss.hasPermi('bas:basuser:list')")
//    @ApiOperation(value = "查询客户员工资料", notes = "查询客户员工资料")
//    @GetMapping("/lists")
//    public R lists(BasUser basUser) {
//        if (StringUtils.isNotEmpty(basUser.getNickName())) {
//            String s = basUser.getPassword();
//            basUser.setPassword(null);
//            List<BasUser> list = basUserService.selectBasUserList(basUser);
//            boolean b = SecurityUtils.matchesPassword(s, list.get(0).getPassword());
//            if (b) {
//                BasCustomers basCustomers = new BasCustomers();
//                for (BasUser basUser1 : list) {
//                    basCustomers.setBelongCusName(basUser1.getCusName());
//                    List<BasCustomers> list1 = basCustomersService.selectBasCustomersList(basCustomers);
//                    return R.ok(list1);
//                }
//            } else {
//                return R.failed("用户名密码错误");
//            }
//        }
//
//        return R.ok();
//    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basuser:add')")
    @ApiOperation(value = "新增客户员工资料", notes = "新增客户员工资料")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @Transactional
    @PostMapping
    public R add(@RequestBody BasUser basUser) {
        basUser.setAdmIden("1");
        List data = new ArrayList();
        BasCodeDto sysCodeDto = new BasCodeDto();
        sysCodeDto.setAppId(APP_ID);
        sysCodeDto.setCode(SYS_CODE);
        data = (List) basCodeService.createCode(sysCodeDto).getData();

        basUser.setUserCode(data.get(0).toString());

        if (StringUtils.isNotEmpty(basUser.getPassword())) {
            String s1 = SecurityUtils.encryptPassword(basUser.getPassword());
            basUser.setPassword(s1);
        }
        SysUserDto sysUserDto = new SysUserDto();
        sysUserDto.setSiteCode(basUser.getCusCode());
        sysUserDto.setUserName(basUser.getNickName());
        sysUserDto.setNickName(basUser.getName());
        sysUserDto.setPhonenumber(basUser.getPhone());
        sysUserDto.setPassword(basUser.getPassword());
        sysUserDto.setSpearPassword("123456");
        sysUserDto.setCreateBy(SecurityUtils.getLoginUser().getUsername());
        sysUserDto.setUserType("01");
        sysUserDto.setStatus("0");
        if (basUser.getAdmIden().equals("1")) {
            Long[] l = new Long[1];
            l[0] = 114L;
            sysUserDto.setRoleIds(l);
        }
        R r = remoteUserService.baseCopyUserAdd(sysUserDto);
        Map map = (Map) r.getData();
        String userId = MapUtils.getString(map, "userId");
        if (StringUtils.isEmpty(userId)) {
            return R.failed(r.getMsg());

        }
        long l = Long.parseLong(userId);
        basUser.setUserId(l);
        basUser.setCreateTime(new Date());
        basUserService.insertBasUser(basUser);
        return R.ok();
    }


    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basuser:edit')")
    @ApiOperation(value = "修改客户员工资料", notes = "修改客户员工资料")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasUser basUser) {
        if (!basUser.getPassword().equals("******")) {
            if (StringUtils.isNotEmpty(basUser.getPassword())) {
                BasUser basUser1 = new BasUser();
                basUser1.setPassword(basUser.getPassword());
                List<BasUser> list = basUserService.selectBasUserList(basUser1);
                if (list.size() == 0) {
                    String s = SecurityUtils.encryptPassword(basUser.getPassword());
                    basUser.setPassword(s);
                }
            }
        } else {
            BasUser basUser2 = new BasUser();
            basUser2.setNickName(basUser.getNickName());
            List<BasUser> lists = basUserService.selectBasUserList(basUser2);
            if (lists.size() != 0) {
                basUser.setPassword(lists.get(0).getPassword());
            }
        }
        SysUserDto sysUserDto = new SysUserDto();
        sysUserDto.setUserId(basUser.getUserId());
        sysUserDto.setUserName(basUser.getNickName());
        sysUserDto.setNickName(basUser.getName());
        sysUserDto.setPhonenumber(basUser.getPhone());
        sysUserDto.setPassword(basUser.getPassword());
        sysUserDto.setStatus(basUser.getEnableIden());
        sysUserDto.setSpearPassword("123456");
        sysUserDto.setUpdateBy(basUser.getUpdateName());
//        if (basUser.getAdmIden().equals("1")) {
//            Long[] l = new Long[1];
//            l[0] = 114L;
//            sysUserDto.setRoleIds(l);
//        }
        R r = remoteUserService.baseCopyUserEdit(sysUserDto);
        if (r.equals(null)) {
            return R.failed(r.getMsg());
        }
        return toOk(basUserService.updateBasUser(basUser));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:basuser:remove')")
    @ApiOperation(value = "删除客户员工资料", notes = "删除客户员工资料")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<Integer> ids) {
        Integer id = ids.get(0);
        BasUser basUser1 = new BasUser();
        basUser1.setId(id);
        List<BasUser> list = basUserService.selectBasUserList(basUser1);
        Long userId = list.get(0).getUserId();
        R remove = remoteUserService.remove(userId);
        if (remove.getCode() != 200) {
            return R.failed(remove.getMsg());
        }
        return toOk(basUserService.deleteBasUserByIds(ids));
    }

    /**
     * 图片上传
     */
    @ApiOperation(value = "图片上传", notes = "图片上传")
    @PostMapping("/addimg")
    public R addimg(@RequestParam("file") MultipartFile[] myfiles) {
        List<String> list = new ArrayList<>();
//        String url = "http://192.168.100.100//";
//        String uploadFolder = "D:\\Program Files\\nginx-1.16.1\\nginx-1.16.1\\html";
        for (MultipartFile myfile : myfiles) {
            Files files = FileUtil.getFileUrl(new FilesDto()
                    .setUrl(env.getProperty("file.url"))
                    .setMyfile(myfile)
                    .setUploadFolder(env.getProperty("file.uploadFolder"))
                    .setType(FileTypeEnum.USER)
                    .setMainUploadFolder(env.getProperty("file.mainUploadFolder")));

            list.add(files.getUrl());
        }
        return R.ok(list);
    }
}
