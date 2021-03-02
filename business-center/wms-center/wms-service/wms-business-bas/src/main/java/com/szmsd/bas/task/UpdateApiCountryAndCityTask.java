package com.szmsd.bas.task;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.bas.domain.BasApiCity;
import com.szmsd.bas.domain.BasApiCountry;
import com.szmsd.bas.domain.dto.BasApiCityDto;
import com.szmsd.bas.service.IBasApiCityService;
import com.szmsd.bas.service.IBasApiCountryService;
import com.szmsd.common.core.constant.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 定时任务 - 更新第三方接口国家和城市信息
 */
@Component("updateApiCountryAndCityTask")
@Slf4j
public class UpdateApiCountryAndCityTask {
    @Resource
    private Environment env;
    @Resource
    private IBasApiCountryService basApiCountryService;
    @Resource
    private IBasApiCityService basApiCityService;

    private static boolean isRunning = false;

    //每天凌晨2点更新一次
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateApiCountryAndCityTask() {
        if (!isRunning) {
            // 开始时间
            isRunning = true;
            log.info("开始定时任务==============================================");
            long start = System.currentTimeMillis();
            //更新国家信息
            //请求第三方接口
            try {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Map> responseEntity = restTemplate.getForEntity(env.getProperty("third_server_url") + "/composite/country", Map.class);
                log.info("=============第三方下单接口返回结果=========>" + JSONObject.toJSONString(responseEntity));
                if (responseEntity == null) {
                    log.error("调用第三方接口(获取国家信息)失败，返回结果为空");
                }
                if (HttpStatus.SUCCESS == responseEntity.getStatusCodeValue()) {
                    Map body = responseEntity.getBody();
                    if (HttpStatus.SUCCESS == Integer.valueOf(body.get("code").toString())) {
                        List<LinkedHashMap<String, Object>> data = (List<LinkedHashMap<String, Object>>) body.get("data");
                        Map<Integer, BasApiCountry> idMap = new HashMap<>();
                        if (CollectionUtils.isNotEmpty(data)) {
                            List<BasApiCountry> countryList = new ArrayList<>();
                            for (LinkedHashMap<String, Object> map : data) {
                                BasApiCountry basApiCountry = new BasApiCountry();
                                if (map.get("id") == null || idMap.containsKey(map.get("id"))) {
                                    continue;
                                }
                                basApiCountry.setId(Integer.valueOf(map.get("id").toString()));
                                basApiCountry.setName(map.get("name") == null ? null : map.get("name").toString());
                                basApiCountry.setLat(map.get("lon") == null ? null : BigDecimal.valueOf(Double.valueOf(map.get("lon").toString())));
                                basApiCountry.setLon(map.get("lat") == null ? null : BigDecimal.valueOf(Double.valueOf(map.get("lat").toString())));
                                basApiCountry.setDescription(map.get("description") == null ? null : map.get("description").toString());

                                idMap.put(basApiCountry.getId(), basApiCountry);
                                countryList.add(basApiCountry);
                            }
                            if (CollectionUtils.isNotEmpty(countryList)) {
                                log.info("==============开始更新第三方接口国家表数据==============>条数：" + countryList.size());
                                QueryWrapper<BasApiCountry> wrapper = new QueryWrapper<>();
                                wrapper.isNotNull("id");
                                basApiCountryService.remove(wrapper);
                                //去重
                                //List<BasApiCountry> collect = countryList.stream().filter(e-> null!=e.getId()).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BasApiCountry -> BasApiCountry.getId(),Comparator.nullsLast(Integer::compareTo)))), ArrayList::new));
                                //List<BasApiCountry> collect1 = countryList.stream().filter(distinctByKey(BasApiCountry::getId)).collect(Collectors.toList());
                                boolean res = basApiCountryService.saveBatch(countryList);
                                log.info("==============更新第三方接口国家表数据返回结果==============>" + res);
                                //更新城市信息
                                if (res) {
                                    for (BasApiCountry basApiCountry : countryList) {
                                        ResponseEntity<Map> cityResponseEntity = restTemplate.getForEntity(env.getProperty("third_server_url") + "/composite/city?countryId={1}", Map.class, basApiCountry.getId().toString());
                                        log.info("=============第三方下单接口返回结果=========>" + JSONObject.toJSONString(cityResponseEntity));
                                        if (cityResponseEntity == null) {
                                            log.error("调用第三方接口(获取城市信息)失败，返回结果为空");
                                        }
                                        if (HttpStatus.SUCCESS == cityResponseEntity.getStatusCodeValue()) {
                                            Map cityBody = cityResponseEntity.getBody();
                                            if (HttpStatus.SUCCESS == Integer.valueOf(cityBody.get("code").toString())) {
                                                if (cityBody.get("data") != null) {
                                                    List<BasApiCityDto> dataList = JSONArray.parseArray(cityBody.get("data").toString(), BasApiCityDto.class);
                                                    Map<Integer, BasApiCity> cityIdMap = new HashMap<>();
                                                    if (CollectionUtils.isNotEmpty(dataList)) {
                                                        List<BasApiCity> cityList = new ArrayList<>();
                                                        for (BasApiCityDto dto : dataList) {
                                                            BasApiCity basApiCity = new BasApiCity();
                                                            if (dto.getId() == null || cityIdMap.containsKey(dto.getId())) {
                                                                continue;
                                                            }
                                                            basApiCity.setId(dto.getId());
                                                            basApiCity.setName(dto.getName());
                                                            basApiCity.setLat(dto.getLat());
                                                            basApiCity.setLon(dto.getLon());
                                                            basApiCity.setDescription(dto.getDescription());
                                                            basApiCity.setActualPlaceName(dto.getActual_place_name());
                                                            basApiCity.setCountryId(basApiCountry.getId());

                                                            cityIdMap.put(basApiCity.getId(), basApiCity);
                                                            cityList.add(basApiCity);
                                                        }

                                                        log.info("==============开始更新第三方接口城市表数据==============>条数：" + cityList.size());
                                                        QueryWrapper<BasApiCity> wrapper1 = new QueryWrapper<>();
                                                        wrapper1.eq("country_id", basApiCountry.getId());
                                                        basApiCityService.remove(wrapper1);
                                                        boolean res1 = basApiCityService.saveBatch(cityList);
                                                        log.info("==============更新第三方接口城市表数据返回结果==============>" + res1);
                                                    }
                                                }
                                            } else {
                                                log.error("调用第三方接口(获取城市信息)失败，" + body.get("desc").toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        log.error("调用第三方接口(获取国家信息)失败，" + body.get("desc").toString());
                    }
                }
                log.info("---->执行任务消耗了 ：" + (System.currentTimeMillis() - start) + "毫秒");
                isRunning = false; // 任务执行完成将标志设为false
                log.info("结束定时任务================================");
            }catch (Exception e){
                log.error("[当前线程：" + Thread.currentThread().getName() + "]-->【更新第三方接口基础数据】(updateApiCountryAndCityTask)异常==>", e);
                isRunning = false; // 任务执行完成将标志设为false
            }
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
