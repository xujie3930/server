package com.szmsd.http.service.http.resolver;

import com.szmsd.http.enums.HttpUrlType;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-27 15:01
 */
@Component
public class ResponseResolverBeanContainer implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(ResponseResolverBeanContainer.class);
    private final ApplicationContext applicationContext;
    private Map<HttpUrlType, Collection<ResponseResolver>> map;

    public ResponseResolverBeanContainer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.map = new HashMap<>();
        ResponseResolver rr = new ResponseResolver.DefaultResponseResolver();
        this.initMapBean(HttpUrlType.WMS, WmsResponseResolver.class, rr);
        this.initMapBean(HttpUrlType.PRICED_PRODUCT, PricedProductResponseResolver.class, rr);
        this.initMapBean(HttpUrlType.CARRIER_SERVICE, CarrierServiceResponseResolver.class, rr);
        this.initMapBean(HttpUrlType.PRODUCT_REMOTE_AREA, ProductRemoteAreaResponseResolver.class, rr);
        this.initMapBean(HttpUrlType.THIRD_PAYMENT, ThirdPaymentResponseResolver.class, rr);
    }

    private void initMapBean(HttpUrlType httpUrlType, Class<? extends ResponseResolver> clazz, ResponseResolver rr) {
        this.map.put(httpUrlType, this.getBeans(httpUrlType, clazz, rr));
    }

    private Collection<ResponseResolver> getBeans(HttpUrlType httpUrlType, Class<? extends ResponseResolver> clazz, ResponseResolver rr) {
        Collection<? extends ResponseResolver> beans = this.getBeans(clazz);
        Collection<ResponseResolver> collection;
        if (CollectionUtils.isEmpty(beans)) {
            logger.info("解析器{}未实现，使用默认解析器", httpUrlType);
            collection = new ArrayList<>();
            collection.add(rr);
        } else {
            collection = new ArrayList<>(beans);
        }
        return collection;
    }

    private Collection<? extends ResponseResolver> getBeans(Class<? extends ResponseResolver> clazz) {
        Collection<? extends ResponseResolver> beans = null;
        try {
            Map<String, ? extends ResponseResolver> map = this.applicationContext.getBeansOfType(clazz);
            beans = map.values();
        } catch (BeansException e) {
            logger.info("Bean未配置, {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return beans;
    }

    public Map<HttpUrlType, Collection<ResponseResolver>> getMap() {
        return map;
    }
}
