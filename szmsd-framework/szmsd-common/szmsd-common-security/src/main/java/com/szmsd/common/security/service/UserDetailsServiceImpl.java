package com.szmsd.common.security.service;

import com.szmsd.common.core.constant.SecurityConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.UserStatus;
import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.domain.dto.SysUserByTypeAndUserType;
import com.szmsd.system.api.feign.RemoteUserService;
import com.szmsd.system.api.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("用户{}加载loadUserByUsername方法", username);
        String clientId = ServletUtils.getParameter(SecurityConstants.DETAILS_CLIENT_ID, SecurityConstants.DETAILS_CLIENT_WEB);//获取令牌id
        String defaultUserType = null;
        HttpSession session = ServletUtils.getSession();
        if (null != session) {
            defaultUserType = (String) session.getAttribute(SecurityConstants.DETAILS_USER_TYPE);
        }
        if (null == defaultUserType) {
            defaultUserType = SecurityConstants.DETAILS_USER_TYPE_SYS;
        }
        String userType = ServletUtils.getParameter(SecurityConstants.DETAILS_USER_TYPE, defaultUserType);//获取用户类型 00-内部用户，01-vip用户
        SysUserByTypeAndUserType sysUserByTypeAndUserType = new SysUserByTypeAndUserType();
        if (SecurityConstants.DETAILS_USER_TYPE_CLIENT.equals(userType) &&
                SecurityConstants.DETAILS_CLIENT_CLIENT.equals(clientId)) {//如果是客户端用户
            sysUserByTypeAndUserType.setType(SecurityConstants.DETAILS_TYPE_CLIENT);
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
        try {
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
        } catch (BadCredentialsException badCredentialsException) {
            // 用户
            User user = new User(username, "", Collections.emptyList());
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
            AuthenticationFailureBadCredentialsEvent badCredentialsEvent = new AuthenticationFailureBadCredentialsEvent(authentication, badCredentialsException);
            applicationContext.publishEvent(badCredentialsEvent);
            throw badCredentialsException;
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
        if (dbAuthsSet.size() == 0) {
            dbAuthsSet.add("client");
        }
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils
                .createAuthorityList(dbAuthsSet.toArray(new String[0]));
        SysUser user = info.getSysUser();
        LoginUser loginUser = new LoginUser(user.getUserId(), user.getUserName(), user.getPassword(), true, true, true, true,
                authorities);
        loginUser.setSellerCode(user.getSellerCode());
        loginUser.setAllDataScope(user.isAllDataScope());
        loginUser.setPermissions(user.getPermissions());
        return loginUser;
        //如果是web端登录就走password ，如果是app就走spearPassword
//        if (SecurityConstants.DETAILS_CLIENT_WEB.equals(clientId)) {
//            return new LoginUser(user.getUserId(), user.getUserName(), user.getPassword(), true, true, true, true,
//                    authorities);
//        } else {
//            return new LoginUser(user.getUserId(), user.getUserName(), user.getSpearPassword(), true, true, true, true,
//                    authorities);
//        }
    }
}
