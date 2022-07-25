package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasChildParentChild;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.service.IBasChildParentChildService;
import com.szmsd.bas.vo.BasChildParentChildQueryVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * 子母单
 *
 * @author: taoJie
 * @since: 2022-07-13
 */
@Api(tags = {"子母单"})
@RestController
@RequestMapping("/bas-child-parent-child")
public class BasChildParentChildController extends BaseController {


    @Resource
    private IBasChildParentChildService basChildParentChildService;

    /**
     * 详情
     *
     * @param basChildParentChild
     * @return: BasSystemRules
     * @author: taoJie
     * @since: 2022-07-13
     */
    @PostMapping("/detail")
    @ApiOperation(value = "详情", notes = "详情")
    public R<BasSeller> detail(@RequestBody BasChildParentChildQueryVO basChildParentChild) {
        return R.ok(basChildParentChildService.detail(basChildParentChild));
    }

    /**
     * 判断客户是否可以加入
     *
     * @param basSeller
     * @return: BasSystemRules
     * @author: taoJie
     * @since: 2022-07-13
     */
    @PostMapping("/sellerAdd")
    @ApiOperation(value = "判断客户是否可以加入", notes = "判断客户是否可以加入")
    public R<BasSeller> sellerAdd(@RequestBody BasChildParentChildQueryVO basSeller) {
        return R.ok(basChildParentChildService.sellerAdd(basSeller));
    }

    /**
     * 分页查询列表
     *
     * @param queryVo
     * @return: TableDataInfo
     * @author: taoJie
     * @since: 2022-07-13
     */
    @PostMapping("/pageList")
    @ApiOperation(value = "分页查询", notes = "查询列表")
    public TableDataInfo<BasChildParentChild> pageList(@RequestBody BasChildParentChildQueryVO queryVo) {
        startPage(queryVo);
        return getDataTable(basChildParentChildService.pageList(queryVo));
    }


    /**
     * 管理新增
     *
     * @param basChildParentChild
     * @author: taoJie
     * @since: 2022-07-13
     */
    @PostMapping("/submit")
    @ApiOperation(value = "管理新增", notes = "管理新增")
    public R submit(@RequestBody BasChildParentChild basChildParentChild) {
        return R.ok(basChildParentChildService.submit(basChildParentChild));
    }

    /**
     * 客户新增
     *
     * @param basSeller
     * @author: taoJie
     * @since: 2022-07-13
     */
    @PostMapping("/submitList")
    @ApiOperation(value = "客户新增", notes = "客户新增")
    public R submitList(@RequestBody BasSeller basSeller) {
        return R.ok(basChildParentChildService.submitList(basSeller));
    }


    /**
     * 处理操作
     *
     * @author: taoJie
     * @since: 2022-07-13
     */
    @PostMapping("/dealOperation")
    @ApiOperation(value = "处理操作", notes = "处理操作")
    public R dealOperation(@RequestBody BasChildParentChild basChildParentChild) {
        return R.ok(basChildParentChildService.dealOperation(basChildParentChild));
    }


    /**
     * 通过主单查询子单
     *
     * @param sellerCode
     * @return: R
     * @author: taoJie
     * @since: 2022-07-14
     */
    @PostMapping("/getChildCodeList")
    @ApiOperation(value = "处理操作", notes = "处理操作")
    public R<List<String>> getChildCodeList(@RequestBody String sellerCode) {
        return R.ok(basChildParentChildService.getChildCodeList(sellerCode));
    }


}
