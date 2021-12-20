package com.szmsd.http.plugins;

import java.util.Map;

public interface DomainPlugin {

    Map<String, String> headers(Map<String, String> header);

    String uri(String uri);

    String requestBody(String requestBody);
}
