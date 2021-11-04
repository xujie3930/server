package com.szmsd.finance.compont;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.dto.WarehouseKvDTO;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.enums.FssRefundConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName: RemoteApiImpl
 * @Description:
 * @Author: 11
 * @Date: 2021-09-09 13:44
 */
@Slf4j
@Component
public class RemoteApiImpl implements IRemoteApi {

    /**
     * 缓存 code 默认一个小时
     */
    TimedCache<String, List<BasSub>> codeCache = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis() * 3);
    TimedCache<Long, List<WarehouseKvDTO>> wareHouseCache = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis() * 3);
    @Resource
    private BasFeignService basFeignService;
    @Resource
    private BasSubFeignService basSubFeignService;
    @Resource
    private ConfigData configData;

    /**
     * 根据主类+子类别名称获取 子类别信息
     *
     * @param mainCode 主类编码 #{@link: com.szmsd.finance.compont.ConfigData}
     * @param subName  子列表名
     * @return 主子类别信息
     */
    @Override
    public BasSub getSubCodeObj(String mainCode, String subName) {
        if (StringUtils.isBlank(mainCode) || StringUtils.isBlank(subName)) return new BasSub();
        List<BasSub> basSubs = codeCache.get(mainCode);
        if (CollectionUtils.isEmpty(basSubs)) {
            R<List<BasSub>> subList = basSubFeignService.listByMain(mainCode, "");
            AssertUtil.isTrue(subList.getCode() == HttpStatus.SUCCESS, "获取code失败" + subList.getMsg());
            List<BasSub> data = subList.getData();
            if (CollectionUtils.isEmpty(data)) data = new ArrayList<>();
            codeCache.put(mainCode, data);
            basSubs = data;
        }
        return basSubs.stream().filter(x -> x.getSubName().equals(subName.trim())).findAny().orElse(new BasSub());
    }

    @Override
    public String getSubCodeObjSubCode(String mainCode, String subName) {
        BasSub subCodeObj = getSubCodeObj(mainCode, subName);
        return Optional.ofNullable(subCodeObj.getSubCode()).orElseThrow(() -> new RuntimeException("请检查" + subName + "是否存在"));
    }

    /**
     * 单号生成
     *
     * @return
     */
    @Override
    public List<String> genNo(Integer count) {
        String code = FssRefundConstant.GENERATE_CODE;
        String appId = FssRefundConstant.GENERATE_APP_ID;
        log.info("调用自动生成单号：code={}", code);
        R<List<String>> r = basFeignService.create(new BasCodeDto().setAppId(appId).setCode(code).setCount(count));
        AssertUtil.notNull(r, "单号生成失败");
        AssertUtil.isTrue(r.getCode() == HttpStatus.SUCCESS, code + "单号生成失败：" + r.getMsg());
        List<String> data = r.getData();
        log.info("调用自动生成单号：调用完成, {}-{}", code, data);
        return data;
    }

    /**
     * 获取子类别名，不存在则报错
     *
     * @param mainCode 主类别
     * @param subName  子类别名
     * @return 返回值
     */
    @Override
    public String getSubCode(String mainCode, String subName) {
        if (StringUtils.isBlank(mainCode) || StringUtils.isBlank(subName)) return "";
        BasSub subCodeObj = getSubCodeObj(mainCode, subName);
        AssertUtil.isTrue(StringUtils.isNotBlank(subCodeObj.getSubCode()), String.format("未找到%s该类别,请联系管理员", subName));
        return subCodeObj.getSubCode();
    }

    /**
     * 获取子类别名，不存在则返回空字符串
     *
     * @param mainCode 主类别
     * @param subName  子类别名
     * @return 返回值 “”
     */
    @Override
    public String getSubCodeOrElseBlack(String mainCode, String subName) {
        if (StringUtils.isBlank(mainCode) || StringUtils.isBlank(subName)) return "";
        BasSub subCodeObj = getSubCodeObj(mainCode, subName);
        return subCodeObj.getSubCode();
    }

    @Resource
    private BasWarehouseFeignService basWarehouseFeignService;

    @Override
    public String getWareHouseCode(String wareHouseName) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        List<WarehouseKvDTO> warehouseKvDTOS = wareHouseCache.get(userId);
        if (CollectionUtils.isEmpty(warehouseKvDTOS)) {
            R<List<WarehouseKvDTO>> listR = basWarehouseFeignService.queryCusInboundWarehouse();
            List<WarehouseKvDTO> dataAndException = R.getDataAndException(listR);
            wareHouseCache.put(userId, dataAndException);
            warehouseKvDTOS = dataAndException;
        }
        return warehouseKvDTOS.stream().filter(x -> x.getValue().equals(wareHouseName)).map(WarehouseKvDTO::getKey).findAny().orElseThrow(() -> new RuntimeException("请检查该用户是否存在仓库：" + wareHouseName));
    }
}
