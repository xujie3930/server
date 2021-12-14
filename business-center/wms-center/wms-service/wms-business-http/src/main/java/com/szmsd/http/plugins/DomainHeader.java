package com.szmsd.http.plugins;

import java.util.Map;

public interface DomainHeader {

    Map<String, String> getHeaders(String uri);
}
