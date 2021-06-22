package com.szmsd.delivery.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author zhangyuyuan
 * @date 2021-06-22 16:29
 */
public class DelOutboundOperationLogEvent extends ApplicationEvent {

    private final OperationLogEnum operationLogEnum;
    private final String tid;
    private final String userCode;
    private final String userName;
    private final String ip;

    public DelOutboundOperationLogEvent(Object source, OperationLogEnum operationLogEnum, String tid, String userCode, String userName, String ip) {
        super(source);
        this.operationLogEnum = operationLogEnum;
        this.tid = tid;
        this.userCode = userCode;
        this.userName = userName;
        this.ip = ip;
    }

    public OperationLogEnum getOperationLogEnum() {
        return operationLogEnum;
    }

    public String getTid() {
        return tid;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getUserName() {
        return userName;
    }

    public String getIp() {
        return ip;
    }
}
