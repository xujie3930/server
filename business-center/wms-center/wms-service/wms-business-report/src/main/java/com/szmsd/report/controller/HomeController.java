package com.szmsd.report.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.report.service.HomeService;
import com.szmsd.returnex.domain.vo.CusWalletVO;
import com.szmsd.returnex.domain.vo.DocumentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 首页
 */
@Api(tags = {"首页"})
@RestController
@RequestMapping("/home")
public class HomeController {

    @Resource
    private HomeService homeService;

    @PreAuthorize("@ss.hasPermi('home:querycuswallet')")
    @GetMapping("/{cusCode}/queryCusWallet")
    @ApiOperation(value = "我的钱包", notes = "对应客户钱包")
    @AutoValue
    public R myWallet(@PathVariable("cusCode") String cusCode) {
        List<CusWalletVO> collect = homeService.selectCusWallet(cusCode);
        return R.ok(collect);
    }

    @PreAuthorize("@ss.hasPermi('home:querydocuments')")
    @GetMapping("/{cusCode}/queryDocuments")
    @ApiOperation(value = "订单数据", notes = "" +
            "当天提审量（当天出库订单的提审包裹数量）" +
            "当天到仓量（当天入库单的数量）" +
            "当天装运包裹到仓量（当天转运/集运包裹的到仓数量）" +
            "当天出库量（当天出库订单的数量）")
    public R<List<DocumentVO>> queryDocuments(@PathVariable("cusCode") String cusCode) {
        List<DocumentVO> documents = homeService.selectDocuments(cusCode);
        return R.ok(documents);
    }

    @PreAuthorize("@ss.hasPermi('home:querymsg')")
    @GetMapping("/{cusCode}/queryMsg")
    @ApiOperation(value = "消息通知", notes = "消息通知")
    public R queryMsg(@PathVariable("cusCode") String cusCode) {
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('home:queryproblem')")
    @GetMapping("/{cusCode}/queryProblem")
    @ApiOperation(value = "问题件处理", notes = "问题件处理")
    public R<List<DocumentVO>> queryProblem(@PathVariable("cusCode") String cusCode) {
        List<DocumentVO> documents = homeService.selectProblem(cusCode);
        return R.ok(documents);
    }

    @PreAuthorize("@ss.hasPermi('home:queryorder7report')")
    @GetMapping("/{cusCode}/queryOrder7Report")
    @ApiOperation(value = "近7天订单报表", notes = "近7天订单报表")
    public R<List<List<String>>> queryOrder7Report(@PathVariable("cusCode") String cusCode) {
        List<List<String>> report = homeService.queryOrder7Report(cusCode);
        return R.ok(report);
    }

}
