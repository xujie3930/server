package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.domain.*;
import com.szmsd.bas.api.factory.BasFeignServiceFallbackFactory;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-30 14:03
 * @Description
 */
@FeignClient(contextId = "basFeignService", value = ServiceNameConstants.BAS_SERVICE, fallbackFactory = BasFeignServiceFallbackFactory.class)
public interface BasFeignService {

    /**
     * 生成系统唯一订单号
     *
     * @param basCodeDto
     */
    @PostMapping(value = "/basCode/create")
    R<List<String>> create(@RequestBody BasCodeDto basCodeDto);

    /**
     * 客户报价
     *
     * @param
     * @param
     * @param
     * @param
     */
    @RequestMapping(value = "/bas-cusprice/select")
    R<BigDecimal> cus(@RequestParam("costCategory") String costCategory,
                      @RequestParam("productTypeCode") String productTypeCode,
                      @RequestParam("cusCode") String cusCode,
                      @RequestParam("heft") String heft,
                      @RequestParam("destinationCode") String destinationCode);

    /**
     * 查询主类别
     *
     * @param basMain
     */
    @GetMapping(value = "/bas-main/lists")
    R<List<BasMain>> lists(BasMain basMain);


    /**
     * 根据name，code查询子类别列表
     *
     * @param code
     * @param name
     * @return
     */
    @GetMapping(value = "/bas-sub/getSubName")
    R list(@RequestParam("code") String code, @RequestParam("name") String name);

    /**
     * 查询客户资料
     *
     * @param basCustomer
     * @return
     */
    @PostMapping("/bas-customer/lists")
    R<List<BasCustomer>> lists(@RequestBody BasCustomer basCustomer);

    /**
     * 查询客户员工资料
     *
     * @param basUser
     * @return
     */
    @PostMapping("/bas-user/lists")
    R<List<BasUser>> lists(@RequestBody BasUser basUser);

    /**
     * 查询 产品类型
     *
     * @param basProductType
     * @return
     */
    @PostMapping("/bas-product-type/lists")
    R<List<BasProductType>> lists(@RequestBody BasProductType basProductType);

    /**
     * 查询目的地
     *
     * @param basDestination
     * @return
     */
    @PostMapping("/bas-destination/getdstination")
    R<List<BasDestination>> getdstination(BasDestination basDestination);

    /**
     * 关键字查询
     *
     * @param basKeyword
     * @return
     */
    @PostMapping("/bas-keyword/lists")
    R<List<BasKeyword>> lists(@RequestBody BasKeyword basKeyword);

    /**
     * 查询子类别
     * @param basSub
     * @return
     */
    @PostMapping(value = "/bas-sub/getsub")
    R<List<BasSub>> getsub(@RequestBody BasSub basSub);


    /**
     * 删除目的地资料
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/bas-destination/{ids}")
    R remove(@RequestBody List<String> ids);

    /**
     * 根据网点删除目的地
     *
     * @param businesSiteCode
     * @return
     */
    @DeleteMapping("/bas-destination/delete/{businesSiteCode}")
    R removes(@PathVariable("businesSiteCode") String businesSiteCode);


    @PostMapping("/bas-route/lists")
    R<List<BasRoute>> lists(@RequestBody BasRoute basRoute);

    @GetMapping("/bas-app-mes/getPushAppMsgList")
    R<List<BasAppMes>> getPushAppMsgList();

    @PostMapping("/bas-app-mes/updateBasAppMesList")
    R updateBasAppMesList(@RequestBody List<BasAppMes> basAppMesList);

    /**
     * 查询重量区
     * @param userCode
     * @return
     */
    @GetMapping("/bas-weight-section/list")
    R<List<BasWeightSectionVo>> weightList(@RequestParam("userCode") String userCode);

    /**
     * 新增重量区间设置模块
     */
    @PostMapping("/bas-weight-section/save")
    R<Integer> weightSave(@RequestBody BasWeightSectionDto dto);

    /**
     * 根据用户编码 获取用户信息
     * @param basEmployees
     * @return
     */
    @PostMapping("/bas-employees/getEmpByCode")
    R<BasEmployees> getEmpByCode(@RequestBody BasEmployees basEmployees);

    /**
     * 查询第三方国家信息
     * @param name
     * @return
     */
    @GetMapping("/bas-api-country/getCountryByName")
    R<BasApiCountry> getCountryByName(@RequestParam("name") String name);

    /**
     * 查询第三方城市信息
     * @param provinceName
     * @param cityName
     * @param townName
     * @return
     */
    @GetMapping("/bas-api-city/getBasApiCity")
    R<BasApiCity> getBasApiCity(@RequestParam("provinceName") String provinceName,
                                @RequestParam("cityName") String cityName,
                                @RequestParam("townName") String townName);

    /**
     * 查询员工资料
     * @param basEmployees
     * @return
     */
    @PostMapping(value = "/bas-employees/empList")
    R<List<BasEmployees>> empList(@RequestBody BasEmployees basEmployees) ;

}
