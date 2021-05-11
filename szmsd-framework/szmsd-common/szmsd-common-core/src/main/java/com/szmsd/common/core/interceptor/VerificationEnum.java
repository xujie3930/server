package com.szmsd.common.core.interceptor;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 11:23
 */
public enum VerificationEnum {

    /**
     * 客户端模式
     * 前端创建表单后，由前端代码生成一个Token。提交表单时，在请求的Header添加Token。
     * Header添加参数：
     * T-Token-Verification: Client
     * T-Token: Token（生成的Token）
     * <p>
     * 后端处理规则
     * 获取header中的T-Token-Verification,T-Token的值，做业务逻辑处理
     */
    Client,

    /**
     * 服务端模式
     * 前端创建表单时，向后端请求令牌信息，后台返回一个Token值。
     * 前端处理提交表单时，将Token值一起传给后端。
     * Header添加参数：
     * T-Token-Verification: Server
     * T-Token: Token（从后端获取的Token）
     */
    Server,

    ;

    public static VerificationEnum valueOf2(String str) {
        return (VerificationEnum) EnumUtil.valueOf(VerificationEnum.class, str);
    }

}
