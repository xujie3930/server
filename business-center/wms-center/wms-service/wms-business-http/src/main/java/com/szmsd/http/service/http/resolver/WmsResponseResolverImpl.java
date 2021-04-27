package com.szmsd.http.service.http.resolver;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-04-27 15:07
 */
@Component
public class WmsResponseResolverImpl extends ResponseResolver.DefaultResponseResolver implements WmsResponseResolver {

    @Override
    public boolean parser(HttpResponseBody httpResponseBody) {
        if (super.parser(httpResponseBody)) {
            String body = httpResponseBody.getBody();
            // 返回结果集为空
            if (StringUtils.isEmpty(body)) {
                return false;
            }
            // 把对象解析成ResponseVO
            try {
                ResponseVO responseVO = JSON.parseObject(body, ResponseVO.class);
                return (null != responseVO
                        && null != responseVO.getSuccess()
                        && responseVO.getSuccess());
            } catch (Exception e) {
                this.logger.error(e.getMessage(), e);
                // 解析失败
                return false;
            }
        }
        return false;
    }
}
