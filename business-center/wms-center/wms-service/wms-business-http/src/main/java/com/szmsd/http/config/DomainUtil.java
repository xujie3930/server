package com.szmsd.http.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public final class DomainUtil {
    private static final Logger logger = LoggerFactory.getLogger(DomainUtil.class);

    public static String getDomain(String uri) {
        try {
            URI uri1 = new URI(uri);
            return uri1.getScheme() + "://" + uri1.getHost();
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }
        return uri;
    }
}
