package com.szmsd.doc.component;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.doc.utils.AuthenticationUtil;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: RemoterApiImpl
 * @Description:
 * @Author: 11
 * @Date: 2021-09-11 14:23
 */
@Slf4j
@Component
public class RemoterApiImpl implements IRemoterApi {

    @Autowired
    private BaseProductClientService baseProductClientService;
    @Resource
    private BasWarehouseClientService basWarehouseClientService;
    @Resource
    private BasSubClientService basSubClientService;
    @Resource
    private RemoteUserService remoteUserService;
    @Resource
    private RemoteAttachmentService remoteAttachmentService;

    @Override
    public RemoteAttachmentService getRemoteAttachmentService() {
        return this.remoteAttachmentService;
    }

    @Override
    public void getUserInfo() {
        R info = remoteUserService.getInfo(1);
        Map data = (Map) info.getData();
        // todo 此处修改ajax.put返回  R.ok(map)
        Object userObj = data.get("user");
        String jsonString = JSONObject.toJSONString(userObj);
        SysUser user = JSONObject.parseObject(jsonString, SysUser.class);

//        map.put("user", userService.selectUserById(userId));//SysUser
//        SysUser
//        map.put("roles", roles);
//        map.put("permissions", permissions);
//        CurrentUserInfo.setSysUser(user);
    }

    @Override
    public boolean verifyWarehouse(String warehouse) {
        List<BasWarehouse> basWarehouses = basWarehouseClientService.queryByWarehouseCodes(Collections.singletonList(warehouse));
        if (CollectionUtils.isNotEmpty(basWarehouses)) return true;
        return false;
    }

    @Override
    public boolean checkSkuBelong(String sellerCode, String warehouse, String sku) {
        return this.checkSkuBelong(sellerCode, warehouse, Collections.singletonList(sku));
    }

    @Override
    public boolean checkSkuBelong(String sku) {
        return this.checkSkuBelong(AuthenticationUtil.getSellerCode(), null, Collections.singletonList(sku));
    }

    @Override
    public boolean checkSkuBelong(String sellerCode, String warehouse, List<String> sku) {
        BaseProductConditionQueryDto baseProductConditionQueryDto = new BaseProductConditionQueryDto();
        baseProductConditionQueryDto.setSkus(sku);
        baseProductConditionQueryDto.setSellerCode(sellerCode);
//        baseProductConditionQueryDto.setWarehouseCode(warehouse);
        List<BaseProduct> baseProducts = baseProductClientService.queryProductList(baseProductConditionQueryDto);
        if (CollectionUtils.isNotEmpty(baseProducts)) return true;
        return false;
    }

    //  mainCode, subCode, Info
    TimedCache<String, Map<String, BasSubWrapperVO>> timedCache = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis() * 30);

    @Override
    public Map<String, BasSubWrapperVO> getSubNameByCode(String mainCode) {
        Map<String, BasSubWrapperVO> subCodeWithInfo = timedCache.get(mainCode);
        if (subCodeWithInfo == null) {
            Map<String, List<BasSubWrapperVO>> sub = basSubClientService.getSub(mainCode);
            sub.forEach((x, y) -> {
                Map<String, BasSubWrapperVO> collect = y.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubCode, subCodeInfo -> subCodeInfo, (x1, x2) -> x1));
                Map<String, BasSubWrapperVO> collect2 = y.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubValue, subCodeInfo -> subCodeInfo, (x1, x2) -> x1));
                collect.putAll(collect2);
                timedCache.put(x, collect);
            });
            subCodeWithInfo = timedCache.get(mainCode);
        }
        return subCodeWithInfo;
    }
}
