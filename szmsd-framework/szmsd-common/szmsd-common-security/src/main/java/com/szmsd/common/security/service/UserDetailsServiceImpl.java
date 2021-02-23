package com.szmsd.common.security.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.szmsd.common.core.constant.SecurityConstants;
import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.domain.dto.SysUserByTypeAndUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.stereotype.Service;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.UserStatus;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.system.api.feign.RemoteUserService;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.model.UserInfo;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息处理
 *
 * @author szmsd
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Resource
    private RemoteUserService remoteUserService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("用户{}加载loadUserByUsername方法", username);
        String clientId = ServletUtils.getParameter(SecurityConstants.DETAILS_CLIENT_ID, SecurityConstants.DETAILS_CLIENT_WEB);//获取令牌id
        String userType = ServletUtils.getParameter(SecurityConstants.DETAILS_USER_TYPE, SecurityConstants.DETAILS_USER_TYPE_SYS);//获取用户类型 00-内部用户，01-vip用户
        SysUserByTypeAndUserType sysUserByTypeAndUserType = new SysUserByTypeAndUserType();
        if (SecurityConstants.DETAILS_USER_TYPE_VIP.equals(userType) &&
                SecurityConstants.DETAILS_CLIENT_WEB.equals(clientId)) {//如果是vip用户
            sysUserByTypeAndUserType.setType(SecurityConstants.DETAILS_TYPE_VIP);
        } else if (SecurityConstants.DETAILS_USER_TYPE_SYS.equals(userType) &&
                SecurityConstants.DETAILS_CLIENT_WEB.equals(clientId)) {//如果是E3 web
            sysUserByTypeAndUserType.setType(SecurityConstants.DETAILS_TYPE_PC);
        } else {//E3 app
            sysUserByTypeAndUserType.setType(SecurityConstants.DETAILS_TYPE_APP);
        }

        sysUserByTypeAndUserType.setUsername(username);
        sysUserByTypeAndUserType.setUserType(userType);
        R<UserInfo> userResult = remoteUserService.getUserInfo(sysUserByTypeAndUserType);
        checkUser(userResult, username);
        log.info("校验获取用户成功：{}", username);
        return getUserDetails(userResult, clientId);
    }


    public void checkUser(R<UserInfo> userResult, String username) {
        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData())) {
            log.info("登录用户：{} 不存在.", username);
            throw new BadCredentialsException("登录用户：" + username + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(userResult.getData().getSysUser().getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new BadCredentialsException("对不起，您的账号：" + username + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(userResult.getData().getSysUser().getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new BadCredentialsException("对不起，您的账号：" + username + " 已停用");
        }
    }

    private UserDetails getUserDetails(R<UserInfo> result, String clientId) {
        UserInfo info = result.getData();
        Set<String> dbAuthsSet = new HashSet<String>();
        if (StringUtils.isNotEmpty(info.getRoles())) {
            // 获取角色
            dbAuthsSet.addAll(info.getRoles());
            // 获取权限
            dbAuthsSet.addAll(info.getPermissions());
        }

        Collection<? extends GrantedAuthority> authorities = AuthorityUtils
                .createAuthorityList(dbAuthsSet.toArray(new String[0]));
        SysUser user = info.getSysUser();

        //如果是web端登录就走password ，如果是app就走spearPassword
        if (SecurityConstants.DETAILS_CLIENT_WEB.equals(clientId)) {
            return new LoginUser(user.getUserId(), user.getUserName(), user.getPassword(), true, true, true, true,
                    authorities);
        } else {
            return new LoginUser(user.getUserId(), user.getUserName(), user.getSpearPassword(), true, true, true, true,
                    authorities);
        }

    }
}
