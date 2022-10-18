package com.szmsd.finance.service;

import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.Map;

public interface RemoteService {
    <T> T postRemoteInvoke(String remoteUrl, Object requestForm, Class clazz);


    <T> T remoteInvoke(String remoteUrl, Object requestForm, Class clazz, HttpMethod method, boolean needAuth, Map<String, ?> authParams);

    String httpRequestPostMethod (String content, String url) throws IOException;
}
