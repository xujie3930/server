package com.szmsd.system.api.model;

import java.io.Serializable;
import java.util.Set;

import com.szmsd.system.api.domain.SysUser;
import lombok.Data;

/**
 * 用户信息
 *
 * @author szmsd
 */
@Data
public class UserInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * 用户基本信息
     */
    private SysUser sysUser;

    /**
     * 权限标识集合
     */
    private Set<String> permissions;

    /**
     * 角色集合
     */
    private Set<String> roles;


}
