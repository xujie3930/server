package com.szmsd.doc.validator;

import com.szmsd.system.api.domain.SysUser;
import lombok.Data;

/**
 * @ClassName: CurrentUserInfo
 * @Description: 线程用户信息
 * @Author: 11
 * @Date: 2021-09-15 18:09
 */
public class CurrentUserInfo {

    private SysUser sysUser;

    private static final ThreadLocal<CurrentUserInfo> CURRENT = new ThreadLocal<>();

    public static void remove() {
        CURRENT.remove();
    }

    public static CurrentUserInfo getUserInfo() {
        return CURRENT.get();
    }
    public static String getSellerCode() {
        return CURRENT.get().sysUser.getSellerCode();
    }
    public CurrentUserInfo(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public static CurrentUserInfo setSysUser(SysUser sysUser) {
        CurrentUserInfo currentUserInfo = new CurrentUserInfo(sysUser);
        CURRENT.set(currentUserInfo);
        return CURRENT.get();
    }
}
