package com.szmsd.system.controller;

import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.domain.dto.SysUserByTypeAndUserType;
import com.szmsd.system.api.domain.dto.SysUserDto;
import com.szmsd.system.api.model.UserInfo;
import com.szmsd.system.domain.dto.SysUserEditPsw;
import com.szmsd.system.service.ISysPermissionService;
import com.szmsd.system.service.ISysPostService;
import com.szmsd.system.service.ISysRoleService;
import com.szmsd.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户信息
 *
 * @author lzw
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户信息")
public class SysUserController extends BaseController {
    @Resource
    private ISysUserService userService;

    @Resource
    private ISysRoleService roleService;

    @Resource
    private ISysPostService postService;

    @Resource
    private ISysPermissionService permissionService;



    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "获取用户列表")
    public TableDataInfo list(SysUserDto userDto) throws Exception {
//        //判断是否是大客户，如果是大客户调用基础数据获取客户编号
//        Long userid = SecurityUtils.getLoginUser().getUserId();
//        if (UserConstants.USER_TYPE_VIP.equals(userService.selectUserById(userid).getUserType()) && userid != 2) {
//            BasUser basUser = new BasUser();
//            basUser.setUserId(userid);
//            log.info("获取用户列表，判断是大客户用户{}，调用基础数据获取客户编号", userid);
//            R<List<BasUser>> listR = null;
//            try {
//                listR = basFeignService.lists(basUser);
//            } catch (Exception e) {
//                log.info("调用基础数据获取客户编号失败", e);
//                throw new Exception("get business-bas feign error");
//            }
//            if (listR != null && listR.getData() != null && listR.getData().size() > 0) {
//                userDto.setSiteCode(listR.getData().get(0).getCusCode());
//            }
//        }
        //正常查询
        SysUser sysUser = new SysUser();
        BeanUtils.copyBeanProp(sysUser, userDto);
        startPage();
        List<SysUser> list = userService.selectUserList(sysUser);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    @ApiOperation(httpMethod = "POST", value = "导出数据")
    public void export(HttpServletResponse response, @RequestBody SysUserDto userDto) throws IOException {
        SysUser sysUser = new SysUser();
        BeanUtils.copyBeanProp(sysUser, userDto);
        List<SysUser> list = userService.selectUserList(sysUser);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    @ApiOperation(httpMethod = "POST", value = "导入用户数据")
    public R importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return R.ok(message);
    }

    @PostMapping("/importTemplate")
    @ApiOperation(httpMethod = "POST", value = "导出模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 获取当前用户信息
     *//*
    @GetMapping("/info")
    @ApiOperation(httpMethod = "GET", value = "获取当前用户信息")
    public R<UserInfo> info(@RequestParam("username") String username,@RequestParam("userType") String userType,@RequestParam("type") Integer type) {
        SysUserByTypeAndUserType sysUserByTypeAndUserType=new SysUserByTypeAndUserType();
        sysUserByTypeAndUserType.setUsername(username);
        sysUserByTypeAndUserType.setUserType(userType);
        sysUserByTypeAndUserType.setType(type);
        SysUser sysUser = userService.selectUserByUserName(sysUserByTypeAndUserType.getUsername(), sysUserByTypeAndUserType.getUserType());
        if (sysUser == null) {
            return R.failed(-200, "用户不存在");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId(), sysUserByTypeAndUserType.getType());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId(), sysUserByTypeAndUserType.getType());
        UserInfo sysUserVo = new UserInfo();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }*/


    /**
     * feign获取当前用户信息
     */
    @PostMapping("/info")
    @ApiOperation(httpMethod = "POST", value = "获取当前用户信息")
    public R<UserInfo> info(@RequestBody SysUserByTypeAndUserType sysUserByTypeAndUserType) {
        SysUser sysUser = userService.selectUserByUserName(sysUserByTypeAndUserType.getUsername(), sysUserByTypeAndUserType.getUserType());
        if (sysUser == null) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM014);
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId(), sysUserByTypeAndUserType.getType());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId(), sysUserByTypeAndUserType.getType());
        UserInfo sysUserVo = new UserInfo();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    /**
     * 登录获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    @ApiOperation(httpMethod = "GET", value = "获取用户信息")
    public R getInfo(@ApiParam("权限类型：1-PC，2-APP,3-VIP") @RequestParam(defaultValue = "1") @PathVariable("type") Integer type) {
        Long userId = SecurityUtils.getLoginUser().getUserId();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(userId, type);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(userId, type);

        Map map = new HashMap<>();
        // todo 此处修改ajax.put返回  R.ok(map)
        map.put("user", userService.selectUserById(userId));
        map.put("roles", roles);
        map.put("permissions", permissions);


        return R.ok(map);
    }

    /**
     * 根据用户编号获取详细信息
     */
    @GetMapping(value = {"/queryGetInfoByUserId/{userId}"})
    @ApiOperation(httpMethod = "GET", value = "根据用户编号获取详细信息")
    public R<SysUser> queryGetInfoByUserId(@PathVariable(value = "userId") Long userId) {
        SysUser sysUser = userService.selectUserById(userId);
        return R.ok(sysUser);
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    @ApiOperation(httpMethod = "GET", value = "根据用户编号获取详细信息")
    public R getInfo(@PathVariable(value = "userId", required = false) Long userId) {
//        R ajax = R.ok();
        Map map = new HashMap<>();
        // todo 此处修改ajax.put返回  R.ok(map)
        map.put("roles", roleService.selectRoleAll());
//        map.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId)) {

            map.put("user", userService.selectUserById(userId));
//            map.put("postIds", postService.selectPostListByUserId(userId));
            map.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return R.ok(map);
    }

    /**
     * 校验用户名是否重复
     *
     * @param userName
     * @return
     */
    @GetMapping("checkUserName/{userName}")
    @ApiOperation(httpMethod = "GET", value = "用户名校验")
    public R<Boolean> checkUserName(@PathVariable(value = "userName") String userName) {

        return R.ok(UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(userName)));
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(httpMethod = "POST", value = "新增用户")
    public R add(@Validated @RequestBody SysUserDto userDto) {
        SysUser user = new SysUser();
        BeanUtils.copyBeanProp(user, userDto);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM015, user.getUserName());
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM016, user.getUserName());
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM017, user.getUserName());
        }
        user.setCreateByName(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setSpearPassword(SecurityUtils.encryptPassword(user.getSpearPassword()));
        return userService.insertUser(user);
    }


    /**
     * 员工资料同步新增用户
     */

    @Log(title = "员工资料同步用户管理新增", businessType = BusinessType.INSERT)
    @PostMapping("baseCopyUserAdd")
    @ApiOperation(httpMethod = "POST", value = "员工资料同步用户管理新增")
    public R baseCopyUserAdd(@Validated @RequestBody SysUserDto userDto) {
        SysUser user = new SysUser();
        BeanUtils.copyBeanProp(user, userDto);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return R.failed("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return R.failed("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.failed("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }

        user.setCreateByName(userDto.getCreateByName());
//        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
//        user.setSpearPassword(SecurityUtils.encryptPassword(user.getSpearPassword()));
        return userService.insertUser(user);
    }

    /**
     * 员工资料同步修改用户
     */
    @Log(title = "员工资料同步修改用户", businessType = BusinessType.UPDATE)
    @PutMapping("baseCopyUserEdit")
    @ApiOperation(httpMethod = "PUT", value = "员工资料同步修改用户")
    public R baseCopyUserEdit(@Validated @RequestBody SysUserDto userDto) {
        if(userDto.getUserId()==null){
            return R.failed("baseCopyUserEdit userId is null");
        }
        SysUser user = new SysUser();
        BeanUtils.copyBeanProp(user, userDto);
        userService.checkUserAllowed(user);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return R.failed("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.failed("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }

        user.setUpdateByName(userDto.getUpdateByName());
//        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
//        user.setSpearPassword(SecurityUtils.encryptPassword(user.getSpearPassword()));
        return toOk(userService.baseCopyUserEdit(user));
    }





    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(httpMethod = "PUT", value = "修改用户")
    public R edit(@Validated @RequestBody SysUserDto userDto) {
        SysUser user = new SysUser();
        BeanUtils.copyBeanProp(user, userDto);
        userService.checkUserAllowed(user);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM016, user.getUserName());
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM017, user.getUserName());
        }
        user.setUpdateBy(SecurityUtils.getLoginUser().getUserId().toString());
        user.setUpdateByName(SecurityUtils.getUsername());
        return toOk(userService.updateUser(user));
    }


    /**
     * 修改E3 APP用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:editapp')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("editapp")
    @ApiOperation(httpMethod = "PUT", value = "APP绑定权限")
    public R editapp(@Validated @RequestBody SysUserDto userDto) {
        SysUser user = new SysUser();
        BeanUtils.copyBeanProp(user, userDto);
        userService.checkUserAllowed(user);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM016, user.getUserName());
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM017, user.getUserName());
        }
        user.setUpdateByName(SecurityUtils.getUsername());
        return toOk(userService.updateUserApp(user));
    }



    /**
     * 真实删除用户
     */
//    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("remove/{userId}")
    @ApiOperation(httpMethod = "DELETE", value = "真实删除用户")
    public R<Integer> remove(@PathVariable Long userId) {
        return toOk(userService.deleteUserById(userId));
    }


    /**
     * 逻辑删除用户
     */
//    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("delFlag/{userIds}")
    @ApiOperation(httpMethod = "DELETE", value = "逻辑删除用户")
    public R<Integer> delFlag(@PathVariable Long[] userIds) {
        return toOk(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
//    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    @ApiOperation(httpMethod = "PUT", value = "重置密码")
    public R<Integer> resetPwd(@RequestBody SysUserDto userDto) {
        SysUser user = new SysUser();
        BeanUtils.copyBeanProp(user, userDto);
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateByName(SecurityUtils.getUsername());
        return toOk(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
//    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    @ApiOperation(httpMethod = "PUT", value = "状态修改")
    public R<Integer> changeStatus(@RequestBody SysUserDto userDto) {
        SysUser user = new SysUser();
        BeanUtils.copyBeanProp(user, userDto);
        userService.checkUserAllowed(user);
        user.setUpdateByName(SecurityUtils.getUsername());
        return toOk(userService.updateUserStatus(user));
    }


    /**
     * 首页修改密码
     */
    @ApiOperation(httpMethod = "PUT", value = "首页修改密码")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/editPsw")
    public R<Integer> editPsw(@RequestBody SysUserEditPsw sysUserEditPsw) {
        //判断用户是否存在
        SysUser sysUser = userService.selectUserById(SecurityUtils.getLoginUser().getUserId());
        if (sysUser == null) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM014);
        }
        //判断原密码是否正确
        if (!SecurityUtils.matchesPassword(sysUserEditPsw.getOldPassword(), sysUser.getPassword())) {
            return R.failed(ExceptionMessageEnum.EXPSYSTEM018);
        }
        //修改密码

        SysUser user = new SysUser();
        user.setUserId(sysUser.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(sysUserEditPsw.getNewPassword()));
        user.setUpdateByName(SecurityUtils.getUsername());
        return toOk(userService.updateUserStatus(user));
    }


}
