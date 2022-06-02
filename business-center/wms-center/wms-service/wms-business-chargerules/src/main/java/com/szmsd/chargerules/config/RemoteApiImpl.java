package com.szmsd.chargerules.config;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.dto.BasSellerQueryDto;
import com.szmsd.bas.dto.BasSellerSysDto;
import com.szmsd.bas.dto.WarehouseKvDTO;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.enums.FssRefundConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.Resource;
import java.util.*;

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
    TimedCache<String, String> cusNameCodeCache = CacheUtil.newWeakCache(DateUnit.MINUTE.getMillis() * 30);
    TimedCache<String, String> cusCodeNameCache = CacheUtil.newWeakCache(DateUnit.MINUTE.getMillis() * 30);

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
    public BasSub getSubObjBySubName(String mainCode, String subName) {
        if (StringUtils.isBlank(mainCode) || StringUtils.isBlank(subName)) return new BasSub();
        List<BasSub> basSubs = getBasSubByMainCode(mainCode);
        return basSubs.stream().filter(x -> x.getSubName().equals(subName.trim())).findAny().orElse(new BasSub());
    }

    @Override
    public BasSub getSubObjBySubCode(String mainCode, String subCode) {
        if (StringUtils.isBlank(mainCode) || StringUtils.isBlank(subCode)) return new BasSub();
        List<BasSub> basSubs = getBasSubByMainCode(mainCode);
        return basSubs.stream().filter(x -> x.getSubCode().equals(subCode.trim())).findAny().orElse(new BasSub());
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
    public String getSubNameBySubCode(String mainCode, String subCode) {
        BasSub subCodeObj = getSubObjBySubCode(mainCode, subCode);
        return Optional.ofNullable(subCodeObj.getSubName()).orElse("");

    }

    @Override
    public String getSubCodeBySubName(String mainCode, String subName) {
        if (StringUtils.isBlank(subName) || StringUtils.isBlank(mainCode)) return "";
        List<BasSub> basSubByMainCode = getBasSubByMainCode(mainCode);
        return basSubByMainCode.stream().filter(x -> x.getSubName().equals(subName)).findAny().map(BasSub::getSubCode).orElseThrow(() -> new RuntimeException("请检查是否存在:" + subName));
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
//        log.info("调用自动生成单号：code={}", code);
        R<List<String>> r = basFeignService.create(new BasCodeDto().setAppId(appId).setCode(code).setCount(count));
        AssertUtil.notNull(r, "单号生成失败");
        AssertUtil.isTrue(r.getCode() == HttpStatus.SUCCESS, code + "单号生成失败：" + r.getMsg());
        List<String> data = r.getData();
//        log.info("调用自动生成单号：调用完成, {}-{}", code, data);
        return data;
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
        BasSub subCodeObj = getSubObjBySubName(mainCode, subName);
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
        return StringUtils.isNotBlank(getCusCodeAndCusName(cusCode,true).getT1());
    }

    @Override
    public String getCusCodeByCusName(String cusNameList) {
        return getCusCodeAndCusName(cusNameList, false).getT2();
    }

    @Override
    public String getCusNameByCusCode(String cusCodeList) {
        return getCusCodeAndCusName(cusCodeList, true).getT1();
    }

    /**
     * 返回存在的客户号 客户名称
     *
     * @param cusList cusList
     * @param type    true code find name ; false name find code
     * @return code  , name
     */
    @Override
    public Tuple2<String, String> getCusCodeAndCusName(String cusList, boolean type) {
        if (StringUtils.isBlank(cusList)) return Tuples.of("", "");
        if (cusNameCodeCache.isEmpty() || cusCodeNameCache.isEmpty()) {
            BasSellerQueryDto basSellerQueryDto = new BasSellerQueryDto();
            basSellerQueryDto.setPageNum(1);
            basSellerQueryDto.setPageSize(999);
            TableDataInfo<BasSellerSysDto> list = basSellerFeignService.list(basSellerQueryDto);
            AssertUtil.isTrue(list != null && list.getCode() == HttpStatus.SUCCESS, "获取用户列表失败");
            List<BasSellerSysDto> rows = list.getRows();
            rows.forEach(x -> {
                cusNameCodeCache.put(x.getUserName(), x.getSellerCode());
                cusCodeNameCache.put(x.getSellerCode(), x.getUserName());
            });
        }
        List<String> cusNewArr = Optional.ofNullable(StringToolkit.getCodeByArray(cusList)).orElse(new ArrayList<>());

        Set<String> cusNameNewList = new LinkedHashSet<>(cusNewArr.size());
        Set<String> cusCodeNewList = new LinkedHashSet<>(cusNewArr.size());
        if (type) {
            // code find name
            cusNewArr.forEach(cusCode -> {
                String cusName = cusCodeNameCache.get(cusCode);
                if (StringUtils.isNotBlank(cusName)) {
                    cusNameNewList.add(cusName);
                    cusCodeNewList.add(cusCode);
                }
            });
        } else {
            // name find code
            cusNewArr.forEach(cusName -> {
                String cusCode = cusNameCodeCache.get(cusName);
                if (StringUtils.isNotBlank(cusCode)) {
                    cusNameNewList.add(cusName);
                    cusCodeNewList.add(cusCode);
                }
            });
        }

        return Tuples.of(String.join(",", cusCodeNewList), String.join(",", cusNameNewList));
    }

}
