package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.config.inner.UrlGroupConfig;
import com.szmsd.http.mapper.HtpConfigMapper;
import com.szmsd.http.service.IHtpConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 外部接口服务配置
 */
@Slf4j
@Service
public class HtpConfigServiceImpl implements IHtpConfigService {

    @Resource
    private HtpConfigMapper htpConfigMapper;

    @Resource
    private HttpConfig httpConfig;

    /**
     * 加载服务配置
     */
    @Override
    public void loadHtpConfig() {
        List<Map<String, Object>> htpUrl = htpConfigMapper.selectHtpUrl();
        Map<String, List<Map<String, Object>>> group = htpUrl.stream().collect(Collectors.groupingBy(e -> e.get("group_id").toString()));
        Map<String, UrlGroupConfig> collect = group.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> {
            Map<String, Object> service = e.getValue().stream().collect(Collectors.toMap(f -> f.get("service_id").toString(), v -> {
                try {
                    Optional.ofNullable(v.get("headers")).ifPresent(item -> v.put("headers", JSONObject.parseObject(item + "")));
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
                return v;
            }));
            return JSONObject.parseObject(JSON.toJSONString(service), UrlGroupConfig.class);
        }));
        httpConfig.setUrlGroup(collect);

        List<Map<String, String>> htpWarehouse = htpConfigMapper.selectHtpWarehouse();
        Map<String, Set<String>> warehouseGroup = htpWarehouse.stream().collect(Collectors.groupingBy(e -> e.get("group_id"), Collectors.collectingAndThen(Collectors.toSet(), e -> e.stream().map(item -> item.get("warehouse_code")).collect(Collectors.toSet()))));
        httpConfig.setWarehouseGroup(warehouseGroup);

        List<Map<String, String>> maps = htpConfigMapper.selectHtpWarehouseUrlGroup();
        Map<String, String> mapperGroup = maps.stream().collect(Collectors.toMap(e -> e.get("warehouse_group_id"), v -> v.get("url_group_id")));
        httpConfig.setMapperGroup(mapperGroup);

        Map<String, String> defaultHtpUrlGroup = htpConfigMapper.selectDefaultHtpUrlGroup();
        httpConfig.setDefaultUrlGroup(defaultHtpUrlGroup.get("group_id"));

    }
}
