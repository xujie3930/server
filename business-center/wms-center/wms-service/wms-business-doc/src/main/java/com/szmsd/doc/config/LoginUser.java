package com.szmsd.doc.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

/**
 * 登录用户身份权限
 *
 * @author szmsd
 */
public class LoginUser extends User {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 客户编码
     */
    private String sellerCode;

    /**
     * 判断角色里面有没有全部数据权限的
     */
    private boolean allDataScope;
    /**
     * 允许查询的数据范围
     */
    private List<String> permissions;

    public LoginUser(Long userId, String username, String password, boolean enabled, boolean accountNonExpired,
                     boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public boolean isAllDataScope() {
        return allDataScope;
    }

    public void setAllDataScope(boolean allDataScope) {
        this.allDataScope = allDataScope;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
