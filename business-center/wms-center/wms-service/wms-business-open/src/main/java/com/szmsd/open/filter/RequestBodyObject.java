package com.szmsd.open.filter;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 10:01
 */
public class RequestBodyObject {

    private String requestBody;
    private String transactionId;

    public RequestBodyObject() {
    }

    public RequestBodyObject(String requestBody, String transactionId) {
        this.requestBody = requestBody;
        this.transactionId = transactionId;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
