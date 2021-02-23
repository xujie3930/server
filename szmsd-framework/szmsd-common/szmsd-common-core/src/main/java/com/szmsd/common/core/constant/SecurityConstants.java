package com.szmsd.common.core.constant;

/**
 * 权限相关通用常量
 *
 * @author szmsd
 */
public class SecurityConstants {
    /**
     * 授权token url
     */
    public static final String AUTH_TOKEN = "/oauth/token";
    /**
     * 注销token url
     */
    public static final String TOKEN_LOGOUT = "/token/logout";

    /**
     * 用户ID字段
     */
    public static final String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";

    /**
     * client_id字段
     */
    public static final String DETAILS_CLIENT_ID = "client_id";

    /**
     * user_type 字段
     */
    public static final String DETAILS_USER_TYPE = "user_type";

    /**
     * client_id 为app
     */
    public static final String DETAILS_CLIENT_ID_APP = "app";
    /**
     * client_id 为web
     */
    public static final String DETAILS_CLIENT_WEB = "web";

    /**
     * userType =内部用户
     */
    public static final String DETAILS_USER_TYPE_SYS = "00";

    /**
     * userType =VIP用户
     */
    public static final String DETAILS_USER_TYPE_VIP = "01";


    /**
     * 类型：type= 1-PC
     */
    public static final Integer DETAILS_TYPE_PC = 1;
    /**
     * 类型：type= 2-APP
     */
    public static final Integer DETAILS_TYPE_APP = 2;
    /**
     * 类型：type= 3-VIP
     */
    public static final Integer DETAILS_TYPE_VIP = 3;


    /**
     * sys_oauth_client_details 表的字段，不包括client_id、client_secret
     */
    public static final String CLIENT_FIELDS = "client_id, client_secret, resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";

    /**
     * JdbcClientDetailsService 查询语句
     */
    public static final String BASE_FIND_STATEMENT = "select " + CLIENT_FIELDS + " from sys_oauth_client_details";

    /**
     * 按条件client_id 查询
     */
    public static final String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ?";

    /**
     * 默认的查询语句
     */
    public static final String DEFAULT_FIND_STATEMENT = BASE_FIND_STATEMENT + " order by client_id";
}
