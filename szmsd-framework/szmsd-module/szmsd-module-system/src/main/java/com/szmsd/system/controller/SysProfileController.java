package com.szmsd.system.controller;

import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.system.domain.dto.SysUserEditPsw;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.service.ISysUserService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息 业务处理
 *
 * @author lzw
 */
@RestController
@RequestMapping("/user/profile")
@Api(tags = "个人信息 业务处理")
public class SysProfileController extends BaseController {
    @Resource
    private ISysUserService userService;

    /**
     * 个人信息
     */
    @GetMapping
    @ApiOperation(httpMethod = "GET", value = "查询个人信息")
    public R profile(@ApiParam("用户类型（00-系统用户 01-VIP用户") @RequestParam(defaultValue = "00") String userType) {
        String username = SecurityUtils.getUsername();
        Map map = new HashMap();
        SysUser user = userService.selectUserByUserName(username, userType);
        map.put("user", user);
        map.put("roleGroup", userService.selectUserRoleGroup(username));
//        map.put("postGroup", userService.selectUserPostGroup(username));
        return R.ok(map);
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(httpMethod = "PUT", value = "修改用户")
    public R updateProfile(@RequestBody SysUser user) {
        if (userService.updateUserProfile(user) > 0) {
            return R.ok();
        }
        return R.failed(ExceptionMessageEnum.EXPSYSTEM006);
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    @ApiOperation(httpMethod = "PUT", value = "重置密码")
    public R updatePwd(@RequestBody SysUserEditPsw sysUserEditPsw) {
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username, sysUserEditPsw.getUserType());
        String password = user.getPassword();//PC密码
        String spearPassword = user.getSpearPassword();//APP密码
        //PC
        if (UserConstants.ROLE_TYPE_PC.equals(sysUserEditPsw.getType()) || UserConstants.ROLE_TYPE_VIP.equals(sysUserEditPsw.getType())) {
            if (!SecurityUtils.matchesPassword(sysUserEditPsw.getOldPassword(), password)) {//判断旧密码正确性
                return R.failed(ExceptionMessageEnum.EXPSYSTEM007);
            }
            if (SecurityUtils.matchesPassword(sysUserEditPsw.getNewPassword(), password)) {//比较新旧密码是否相同
                return R.failed(ExceptionMessageEnum.EXPSYSTEM008);
            }
            if (userService.resetUserPwd(username, SecurityUtils.encryptPassword(sysUserEditPsw.getNewPassword())) > 0) {//修改E3密码
                return R.ok();
            }
        }
        //APP
        if (UserConstants.ROLE_TYPE_APP.equals(sysUserEditPsw.getType())) {
            if (!SecurityUtils.matchesPassword(sysUserEditPsw.getOldPassword(), spearPassword)) {//判断旧密码正确性
                return R.failed(ExceptionMessageEnum.EXPSYSTEM007);
            }
            if (SecurityUtils.matchesPassword(sysUserEditPsw.getNewPassword(), spearPassword)) {//比较新旧密码是否相同
                return R.failed(ExceptionMessageEnum.EXPSYSTEM008);
            }
            if (userService.resetAppUserPwd(username, SecurityUtils.encryptPassword(sysUserEditPsw.getNewPassword())) > 0) {//修改E3密码
                return R.ok();
            }
        }
        return R.failed(ExceptionMessageEnum.EXPSYSTEM009);
    }
}
