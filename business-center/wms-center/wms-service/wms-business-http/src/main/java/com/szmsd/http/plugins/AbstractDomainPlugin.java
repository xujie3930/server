package com.szmsd.http.plugins;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public abstract class AbstractDomainPlugin implements DomainPlugin, Domain {

    protected String domain;
    @Autowired
    private DomainHeader domainHeader;

    @Override
    public Map<String, String> headers(Map<String, String> header) {
        return header;
    }

    @Override
    public String uri(String uri) {
        return uri;
    }

    @Override
    public String requestBody(String requestBody) {
        return requestBody;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Map<String, String> getDomainHeader() {
        return this.domainHeader.getHeaders(this.domain);
    }
}
