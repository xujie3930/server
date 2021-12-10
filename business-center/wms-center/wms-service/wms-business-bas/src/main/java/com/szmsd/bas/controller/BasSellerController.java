package com.szmsd.bas.controller;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
* <p>
    *  前端控制器
    * </p>
*
* @author l
* @since 2021-03-09
*/


@Api(tags = {"卖家模块"})
@RestController
@RequestMapping("/bas/seller")
public class BasSellerController extends BaseController{

     @Autowired
     private IBasSellerService basSellerService;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
      @PostMapping("/list")
      @ApiOperation(value = "分页查询模块列表",notes = "分页查询模块列表")
      public TableDataInfo list(@RequestBody BasSellerQueryDto basSeller)
     {
            return basSellerService.selectBasSellerList(basSeller);
      }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
    @GetMapping("/listSeller")
    @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
    public R<List<BasSellerSysDto>> listSeller(BasSeller basSeller)
    {
        List<BasSellerSysDto> list = basSellerService.getBasSellerList(basSeller);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
    @PostMapping("/getSellerCode")
    @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
    public R<String> getSellerCode(@RequestBody BasSeller basSeller)
    {
        R r = new R();
        r.setCode(200);
        r.setData(basSellerService.getSellerCode(basSeller));
        r.setMsg("SUCCESS");
        return r;
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
    @PostMapping("/getLoginSellerCode")
    @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
    public R<String> getLoginSellerCode()
    {
        R r = new R();
        r.setCode(200);
        r.setData(basSellerService.getLoginSellerCode());
        r.setMsg("SUCCESS");
        return r;
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
    @PostMapping("/getInspection")
    @ApiOperation(value = "查询验货要求",notes = "查询验货要求")
    public R<String> getInspection(@RequestBody String sellerCode)
    {
        R r = new R();
        r.setCode(200);
        r.setData(basSellerService.getInspection(sellerCode));
        r.setMsg("SUCCESS");
        return r;
    }


    /**
    * 导出模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, BasSeller basSeller) throws IOException {
     /*List<BasSellerSysDto> list = basSellerService.selectBasSellerList(basSeller);
     ExcelUtil<BasSellerSysDto> util = new ExcelUtil<BasSellerSysDto>(BasSellerSysDto.class);
        util.exportExcel(response,list, "BasSeller");*/

     }

    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:query')")
    @GetMapping(value = "getInfo/{userName}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R<BasSellerInfoVO> getInfo(@PathVariable("userName") String userName)
    {
    return R.ok(basSellerService.selectBasSeller(userName));
    }
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:query')")
    @GetMapping(value = "getInfoBySellerCode/{sellerCode}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R<BasSellerInfoVO> getInfoBySellerCode(@PathVariable("sellerCode") String sellerCode)
    {
        return R.ok(basSellerService.selectBasSellerBySellerCode(sellerCode));
    }
    /**
    * 卖家注册模块 EmailCodeValid切面校验邮箱验证码
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("register")
    @ApiOperation(value = "注册模块",notes = "注册模块")
    public R<Boolean> register(HttpServletRequest request, @RequestBody BasSellerDto dto)
    {
    return basSellerService.insertBasSeller(request,dto);
    }

    @ApiOperation("获取验证码")
    @PostMapping("getCheckCode")
    public R getCheckCode(HttpServletRequest request) {
        return this.basSellerService.getCheckCode(request);
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody BasSellerInfoDto basSellerInfoDto) throws IllegalAccessException {
    return toOk(basSellerService.updateBasSeller(basSellerInfoDto));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @PutMapping("remove")
    @ApiOperation(value = "生效/失效模块",notes = "生效/失效模块")
    public R remove(@RequestBody ActiveDto activeDto) throws IllegalAccessException {
    return R.ok(basSellerService.deleteBasSellerByIds(activeDto));
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:queryByServiceCondition')")
    @PostMapping("/queryByServiceCondition")
    @ApiOperation(value = "查询业务经理/客服下所属的客户编码")
    public R<List<String>> queryByServiceCondition(@RequestBody ServiceConditionDto conditionDto) {
        return R.ok(this.basSellerService.queryByServiceCondition(conditionDto));
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:queryByServiceCondition')")
    @PostMapping("/queryAllSellerCodeAndEmail")
    @ApiOperation(value = "查询所有用户编码 和邮箱地址")
    public R<List<BasSellerEmailDto>> queryAllSellerCodeAndEmail() {
        return R.ok(this.basSellerService.queryAllSellerCodeAndEmail());
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:getRealState')")
    @PostMapping("/getRealState")
    @ApiOperation(value = "查询实名状态")
    public R<String> getRealState(@RequestBody String sellerCode) {
        return R.ok(this.basSellerService.getRealState(sellerCode));
    }
}
