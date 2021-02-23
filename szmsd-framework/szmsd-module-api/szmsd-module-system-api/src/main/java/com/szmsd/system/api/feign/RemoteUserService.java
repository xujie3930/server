package com.szmsd.system.api.feign;

import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.domain.dto.SysUserByTypeAndUserType;
import com.szmsd.system.api.domain.dto.SysUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.system.api.factory.RemoteUserFallbackFactory;
import com.szmsd.system.api.model.UserInfo;

/**
 * 用户服务
 *
 * @author szmsd
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {

    /**
     * 通过userId查询用户详情
     *
     * @param
     * @return 结果
     */
    @GetMapping(value = "/user/queryGetInfoByUserId/{userId}")
    R<SysUser> queryGetInfoByUserId(@PathVariable(value = "userId") Long userId);

    /**
     * 通过用户信息查询用户信息
     *
     * @param
     * @return 结果
     */
    @PostMapping(value = "/user/info")
//    R<UserInfo> getUserInfo(@RequestParam("username") String username,@RequestParam("userType") String userType,@RequestParam("type") Integer type);
    R<UserInfo> getUserInfo(@RequestBody SysUserByTypeAndUserType sysUserByTypeAndUserType);

    /**
     * 新增用户
     */
    @PostMapping(value = "/user/baseCopyUserAdd")
    R baseCopyUserAdd(@RequestBody SysUserDto userDto);

    /**
     * 修改用户
     */
    @PutMapping(value = "/user/baseCopyUserEdit")
    R baseCopyUserEdit(@RequestBody SysUserDto userDto);


    /**
     * 真实删除用户
     */
    @DeleteMapping("/user/remove/{userId}")
    R remove(@PathVariable("userId") Long userId);

    /**
     * 逻辑删除用户
     */
    @DeleteMapping("/user/delFlag/{userIds}")
    R delFlag(@PathVariable("userIds") Long[] userIds);

    /**
     * 修改用户状态
     *
     * @param userDto
     * @return
     */
    @PutMapping("/user/changeStatus")
    R changeStatus(@RequestBody SysUserDto userDto);
}
