package com.szmsd.http.plugins;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component("DefaultDomainPlugin")
public class DefaultDomainPlugin extends AbstractDomainPlugin implements DomainPlugin {

    @Override
    public Map<String, String> headers(Map<String, String> header) {
        // 处理header配置问题，优先级别：domainHeaderConfig < dto.getHeaders
        Map<String, String> domainHeader = super.getDomainHeader();
        if (null == domainHeader) {
            domainHeader = new LinkedHashMap<>();
        }
        if (null != header) {
            domainHeader.putAll(header);
        }
        return domainHeader;
    }

}
