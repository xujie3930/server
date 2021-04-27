package com.szmsd.http.service.http.resolver;

import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.enums.HttpUrlType;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-27 15:17
 */
@Component
public class ResponseResolverActuator implements Actuator {

    private final ResponseResolverBeanContainer responseResolverBeanContainer;

    public ResponseResolverActuator(ResponseResolverBeanContainer responseResolverBeanContainer) {
        this.responseResolverBeanContainer = responseResolverBeanContainer;
    }

    @Override
    public boolean execute(ActuatorParameter actuatorParameter) {
        if (null == actuatorParameter) {
            return false;
        }
        ResponseResolverActuatorParameter parameter = (ResponseResolverActuatorParameter) actuatorParameter;
        HttpUrlType httpUrlType = parameter.getHttpUrlType();
        HttpResponseBody httpResponseBody = parameter.getHttpResponseBody();
        if (null == httpUrlType || null == httpResponseBody) {
            return false;
        }
        Map<HttpUrlType, Collection<ResponseResolver>> map = responseResolverBeanContainer.getMap();
        if (null == map) {
            return false;
        }
        Collection<ResponseResolver> list = map.get(httpUrlType);
        if (null == list) {
            return false;
        }
        for (ResponseResolver resolver : list) {
            if (resolver.parser(httpResponseBody)) {
                return true;
            }
        }
        return false;
    }
}
