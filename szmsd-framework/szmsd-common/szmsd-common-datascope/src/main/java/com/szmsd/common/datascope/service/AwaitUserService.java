package com.szmsd.common.datascope.service;

import com.szmsd.common.core.constant.SecurityConstants;
import com.szmsd.system.api.domain.dto.SysUserByTypeAndUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.feign.RemoteUserService;
import com.szmsd.system.api.model.UserInfo;

import javax.annotation.Resource;

/**
 * 同步调用用户服务
 * 
 * @author szmsd
 */
@Service
public class AwaitUserService
{
    private static final Logger log = LoggerFactory.getLogger(AwaitUserService.class);

    @Resource
    private RemoteUserService remoteUserService;

    /**
     * 查询当前用户信息
     * 
     * @return 用户基本信息
     */
    public UserInfo info()
    {
        String username = SecurityUtils.getUsername();
        SysUserByTypeAndUserType sysUserByTypeAndUserType= new SysUserByTypeAndUserType();
        sysUserByTypeAndUserType.setUsername(username);
        sysUserByTypeAndUserType.setUserType(SecurityConstants.DETAILS_USER_TYPE_SYS);
        sysUserByTypeAndUserType.setType(SecurityConstants.DETAILS_TYPE_PC);
        R<UserInfo> userResult = remoteUserService.getUserInfo(sysUserByTypeAndUserType);
        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData()))
        {
            log.info("数据权限范围查询用户：{} 不存在.", username);
            return null;
        }
        return userResult.getData();
    }
}
