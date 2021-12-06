package com.szmsd.chargerules.config;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.cache.impl.WeakCache;
import cn.hutool.core.date.DateUnit;
import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.bas.dto.BasSellerQueryDto;
import com.szmsd.bas.dto.WarehouseKvDTO;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.enums.FssRefundConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    TimedCache<String, List<BasSub>> codeCache = CacheUtil.newWeakCache(DateUnit.MINUTE.getMillis() * 30);
    TimedCache<Long, List<WarehouseKvDTO>> wareHouseCache = CacheUtil.newWeakCache(DateUnit.MINUTE.getMillis() * 30);
    WeakCache<String, Map<String, BasSellerCertificate>> cusCodeCache = CacheUtil.newWeakCache(DateUnit.MINUTE.getMillis() * 30);

    @Resource
    private BasFeignService basFeignService;
    @Resource
    private BasSellerFeignService basSellerFeignService;
    @Resource
    private BasSubFeignService basSubFeignService;

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
        List<BasSub> basSubs = getBasSubByMainCode(mainCode);
        return basSubs.stream().filter(x -> x.getSubName().equals(subName.trim())).findAny().orElse(new BasSub());
    }

    /**
     * 查询主类别下的数据
     *
     * @param mainCode
     * @return
     */
    private List<BasSub> getBasSubByMainCode(String mainCode) {
        List<BasSub> basSubs = codeCache.get(mainCode);
        if (CollectionUtils.isEmpty(basSubs)) {
            R<List<BasSub>> subList = basSubFeignService.listByMain(mainCode, "");
            AssertUtil.isTrue(subList.getCode() == HttpStatus.SUCCESS, "获取code失败" + subList.getMsg());
            List<BasSub> data = subList.getData();
            if (CollectionUtils.isEmpty(data)) data = new ArrayList<>();
            codeCache.put(mainCode, data);
            basSubs = data;
        }
        return basSubs;
    }

    /**
     * 通过字类别的value 查名字
     *
     * @param mainCode
     * @param subValue
     * @return
     */
    @Override
    public String getSubNameByValue(String mainCode, String subValue) {
        List<BasSub> basSubByMainCode = getBasSubByMainCode(mainCode);
        return basSubByMainCode.stream().filter(x -> x.getSubValue().equals(subValue)).findAny().map(BasSub::getSubName).orElseThrow(() -> new RuntimeException("请检查是否存在:" + subValue));
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
        AssertUtil.isTrue(StringUtils.isNotBlank(subCodeObj.getSubCode()), String.format("未找到%s该类别", subName));
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

    /**
     * UA查
     *
     * @param wareHouseName
     * @return
     */
    @Override
    public String getWareHouseName(String wareHouseName) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        AssertUtil.notNull(loginUser, "获取用户信息失败");
        Long userId = loginUser.getUserId();
        List<WarehouseKvDTO> warehouseKvDTOS = wareHouseCache.get(userId);
        if (CollectionUtils.isEmpty(warehouseKvDTOS)) {
            R<List<WarehouseKvDTO>> listR = basWarehouseFeignService.queryCusInboundWarehouse();
            List<WarehouseKvDTO> dataAndException = R.getDataAndException(listR);
            wareHouseCache.put(userId, dataAndException);
            warehouseKvDTOS = dataAndException;
        }
        return warehouseKvDTOS.stream().filter(x -> x.getKey().equals(wareHouseName)).map(WarehouseKvDTO::getValue).findAny().orElseThrow(() -> new RuntimeException("请检查该用户是否存在仓库：" + wareHouseName));
    }

    @Override
    public boolean checkCusCode(String cusCode) {
        if (StringUtils.isBlank(cusCode)) return false;
        Map<String, BasSellerCertificate> cusCodeCacheMap = cusCodeCache.get("");
        if (MapUtils.isEmpty(cusCodeCacheMap)) {
            BasSellerQueryDto basSellerQueryDto = new BasSellerQueryDto();
            basSellerQueryDto.setPageNum(1);
            basSellerQueryDto.setPageSize(999);
            TableDataInfo<BasSellerCertificate> list = basSellerFeignService.list(basSellerQueryDto);
            AssertUtil.isTrue(list != null && list.getCode() == HttpStatus.SUCCESS, "获取用户列表失败");
            List<BasSellerCertificate> resultList = list.getRows();
            Map<String, BasSellerCertificate> resultMap = resultList.stream().collect(Collectors.toMap(BasSellerCertificate::getSellerCode, x -> x, (x1, x2) -> x2));
            cusCodeCacheMap = resultMap;
            cusCodeCache.put("", resultMap);
        }
        return null != cusCodeCacheMap.get(cusCode);
    }
}
