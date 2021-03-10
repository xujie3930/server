package com.szmsd.common.core.constant;

/**
 * 用户常量信息
 * 
 * @author szmsd
 */
public class UserConstants
{
    /**
     * 平台内系统用户的唯一标志
     */
    public static final String SYS_USER = "SYS_USER";

    /** 正常状态 */
    public static final String NORMAL = "0";

    /** 异常状态 */
    public static final String EXCEPTION = "1";

    /** 用户封禁状态 */
    public static final String USER_DISABLE = "1";

    /** 角色封禁状态 */
    public static final String ROLE_DISABLE = "1";

    /** 部门正常状态 */
    public static final String DEPT_NORMAL = "0";

    /** 部门停用状态 */
    public static final String DEPT_DISABLE = "1";

    /** 字典正常状态 */
    public static final String DICT_NORMAL = "0";

    /** 是否为系统默认（是） */
    public static final String YES = "Y";

    /** 校验返回结果码 */
    public final static String UNIQUE = "0";
    public final static String NOT_UNIQUE = "1";

    /** E3用户类型 */
    public final static  String USER_TYPE_E3="00";

    /** vip大客户用户类型 */
    public final static  String USER_TYPE_VIP="01";
    /** 客户用户 */
    public final static  String USER_TYPE_CRS="01";


    /** E3系统PC角色类型 */
    public final static  Integer ROLE_TYPE_PC=1;

    /** app角色类型 */
    public final static  Integer ROLE_TYPE_APP=2;

    /** vip大客户角色类型 */
    public final static  Integer ROLE_TYPE_VIP=3;

}
