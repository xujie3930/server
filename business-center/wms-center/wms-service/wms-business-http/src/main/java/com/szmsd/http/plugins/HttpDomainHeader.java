package com.szmsd.http.plugins;

import com.szmsd.http.config.DomainHeaderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HttpDomainHeader implements DomainHeader {

    @Autowired
    private DomainHeaderConfig domainHeaderConfig;

    @Override
    public Map<String, String> getHeaders(String uri) {
        if (null == domainHeaderConfig) {
            return null;
        }
        return domainHeaderConfig.getHeaders(uri);
    }
}
