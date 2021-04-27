package com.szmsd.http.service.http.resolver;

import com.szmsd.common.core.utils.HttpResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * @author zhangyuyuan
 * @date 2021-04-27 14:23
 */
public interface ResponseResolver {

    /**
     * 解析返回结果是否为成功
     *
     * @param httpResponseBody httpResponseBody
     * @return boolean
     */
    boolean parser(HttpResponseBody httpResponseBody);

    /**
     * 默认解析器
     *
     * @see com.szmsd.common.core.utils.HttpResponseBody
     */
    class DefaultResponseResolver implements ResponseResolver {
        protected Logger logger = LoggerFactory.getLogger(this.getClass());

        @Override
        public boolean parser(HttpResponseBody httpResponseBody) {
            if (null == httpResponseBody) {
                return false;
            }
            return HttpStatus.OK.value() == httpResponseBody.getStatus();
        }
    }
}
