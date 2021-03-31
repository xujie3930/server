package com.szmsd.bas.service.impl;

import com.szmsd.bas.dao.BasCommonMapper;
import com.szmsd.bas.service.BasCommonService;
import com.szmsd.common.core.enums.CodeToNameEnum;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liyingfeng
 * @date 2020/11/19 10:31
 */
@Component
@Slf4j
public class BasCommonServiceImpl implements BasCommonService {

    @Resource
    private BasCommonMapper mapper;

    @Resource
    private RedisService redisService;

    @Override
    public Map<String, Map<String, String>> getCodeToName(CodeToNameEnum type) {
        if (null == type) {
            return null;
        }
        Map<String, Map<String, String>> res = null;
        switch (type) {
            case BAS_SUB:
                res = getBasSub();
                break;
            case BAS_REGION:
                res = getRegion();
                break;
            case BAS_PRODUCT:
                res = getBasProductType();
                break;
            case BAS_CUSTOMER:
                res = getCustomer();
                break;
            case BAS_EMPLOYEE:
                res = getBasEmployees();
                break;
            default:
                break;
        }
        return res;
    }

    @Override
    public void updateRedis(CodeToNameEnum type) {
            log.info("====================开始加载{}基础数据====================" , type.getType());
            Map<String, Map<String, String>> codeToName = getCodeToName(type);
            if (null != codeToName) {
                //测试
                redisService.setCacheObject("test123","liyinfeng");
                redisService.deleteObject(type.getType());
                redisService.setCacheMap(type.getType(), codeToName);
            }
            log.info("====================加载{}基础数据结束，总数量：{}====================" , type.getType(), null != codeToName ? codeToName.size() : 0);
    }

    /**
     * 查询客户资料
     */
    private Map<String, Map<String, String>> getCustomer() {
        List<Map<String, String>> customers = mapper.getCustomer();
        return ListUtils.emptyIfNull(customers).stream()
                .filter(e -> StringUtils.isNotEmpty(e.get("code"))).collect(Collectors.toMap(k -> k.get("code"), v -> v, (k1, k2) -> k1));
    }

    /**
     * 业务员
     *
     * @return
     */
    private Map<String, Map<String, String>> getBasEmployees() {
        List<Map<String, String>> employees = mapper.getBasEmployees();
        return ListUtils.emptyIfNull(employees).stream()
                .filter(e -> StringUtils.isNotEmpty(e.get("code"))).collect(Collectors.toMap(k -> k.get("code"), v -> v, (k1, k2) -> k1));
    }

    /**
     * 产品类型
     *
     * @return
     */
    private Map<String, Map<String, String>> getBasProductType() {
        List<Map<String, String>> productTypes = mapper.getBasProductType();
        return ListUtils.emptyIfNull(productTypes).stream()
                .filter(e -> StringUtils.isNotEmpty(e.get("code"))).collect(Collectors.toMap(k -> k.get("code"), v -> v, (k1, k2) -> k1));
    }

    /**
     * 字典
     *
     * @return
     */
    private Map<String, Map<String, String>> getBasSub() {
        List<Map<String, String>> basSubs = mapper.getBasSub();
        return ListUtils.emptyIfNull(basSubs).stream()
                .filter(e -> StringUtils.isNotEmpty(e.get("code"))).collect(Collectors.toMap(k -> k.get("code"), v -> v, (k1, k2) -> k1));
    }

    /**
     * 地区
     *
     * @return
     */
    private Map<String, Map<String, String>> getRegion() {
        List<Map<String, String>> countries = mapper.getRegion();
        return ListUtils.emptyIfNull(countries).stream()
                .filter(e -> StringUtils.isNotEmpty(e.get("code"))).collect(Collectors.toMap(k -> k.get("code"), v -> v, (k1, k2) -> k1));
    }

}
