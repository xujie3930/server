package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasSellerFeignFallback;
import com.szmsd.bas.api.factory.BaseProductFeignFallback;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.bas.vo.BasSellerWrapVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.BasSellerFeignFallback", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasSellerFeignFallback.class)
public interface BasSellerFeignService {
    @PostMapping(value = "/bas/seller/getSellerCode")
    R<String> getSellerCode(@RequestBody BasSeller basSeller);

    @PostMapping(value = "/bas/seller/getLoginSellerCode")
    R<String> getLoginSellerCode();

    @PostMapping("/bas/seller/list")
    @ApiOperation(value = "分页查询模块列表", notes = "分页查询模块列表")
    TableDataInfo<BasSellerSysDto> list(@RequestBody BasSellerQueryDto basSeller);

    /**
     * 查询客户验货要求
     *
     * @param sellerCode
     * @return
     */
    @PostMapping(value = "/bas/seller/getInspection")
    R<String> getInspection(@RequestBody String sellerCode);

    @PostMapping("/bas/seller/queryByServiceCondition")
    R<List<String>> queryByServiceCondition(@RequestBody ServiceConditionDto conditionDto);

    /**
     * 查询所有用户编码 和邮箱地址
     *
     * @return
     */
    @PostMapping("/bas/seller/queryAllSellerCodeAndEmail")
    R<List<BasSellerEmailDto>> queryAllSellerCodeAndEmail();

    @PostMapping("/bas/seller/getRealState")
    R<String> getRealState(@RequestBody String sellerCode);

    @PostMapping("/bas/sellerCertificate/listVAT")
    @ApiOperation(value = "查询VAT模块列表", notes = "查询VAT模块列表")
    R<List<BasSellerCertificate>> listVAT(@RequestBody VatQueryDto vatQueryDto);

    @GetMapping(value = "/bas/seller/getInfo/{userName}")
    @ApiOperation(value = "获取模块详细信息", notes = "获取模块详细信息userName:sellerCode")
    R<BasSellerInfoVO> getInfo(@PathVariable("userName") String userName);

    @GetMapping(value = "/bas/seller/getInfoBySellerCode/{sellerCode}")
    @ApiOperation(value = "获取模块详细信息", notes = "获取模块详细信息")
    R<BasSellerInfoVO> getInfoBySellerCode(@PathVariable("sellerCode") String sellerCode);

    @GetMapping("/bas/seller/queryCkPushFlag/{sellerCode}")
    @ApiOperation(value = "查询实名状态")
    R<BasSellerWrapVO> queryCkPushFlag(@PathVariable("sellerCode") String sellerCode);
}
