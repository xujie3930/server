package com.szmsd.bas.controller;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.SyncReadListener;
import com.szmsd.bas.dto.BasSkuRuleMatchingImportDto;
import com.szmsd.bas.dto.BaseProductImportDto;
import com.szmsd.bas.enums.SkuRuleMatchingEnum;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.szmsd.bas.service.IBasSkuRuleMatchingService;
import com.szmsd.bas.domain.BasSkuRuleMatching;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import java.util.List;
import java.io.IOException;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;
import org.springframework.web.multipart.MultipartFile;


/**
* <p>
    * sku规则匹配表 前端控制器
    * </p>
*
* @author Administrator
* @since 2022-05-10
*/


@Api(tags = {"sku规则匹配表"})
@RestController
@RequestMapping("/bas-sku-rule-matching")
public class BasSkuRuleMatchingController extends BaseController{

     @Resource
     private IBasSkuRuleMatchingService basSkuRuleMatchingService;
     /**
       * 查询sku规则匹配表模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasSkuRuleMatching:BasSkuRuleMatching:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询sku规则匹配表模块列表",notes = "查询sku规则匹配表模块列表")
      public TableDataInfo list(BasSkuRuleMatching basSkuRuleMatching)
     {

         LoginUser loginUser = SecurityUtils.getLoginUser();
         if (null == loginUser) {
             throw new CommonException("500", "非法的操作");
         }
         // 获取登录用户的客户编码
         String sellerCode = loginUser.getSellerCode();

         basSkuRuleMatching.setSellerCode(sellerCode);
         basSkuRuleMatching.setSystemType(SkuRuleMatchingEnum.SHOPIFY.getCode());

            List<BasSkuRuleMatching> list = basSkuRuleMatchingService.selectBasSkuRuleMatchingList(basSkuRuleMatching);
            return getDataTable(list);
      }


    /**
    * 获取sku规则匹配表模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasSkuRuleMatching:BasSkuRuleMatching:getInfoBySourceSku')")
    @GetMapping(value = "getInfoBySourceSku")
    @ApiOperation(value = "获取sku规则匹配表模块详细信息",notes = "获取sku规则匹配表模块详细信息")
    public R getInfoBySourceSku(BasSkuRuleMatching basSkuRuleMatching)
    {
        /*LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CommonException("500", "非法的操作");
        }
        // 获取登录用户的客户编码
        String sellerCode = loginUser.getSellerCode();

        basSkuRuleMatching.setSellerCode(sellerCode);*/
        basSkuRuleMatching.setSystemType(SkuRuleMatchingEnum.SHOPIFY.getCode());
        List<BasSkuRuleMatching> list = basSkuRuleMatchingService.selectBasSkuRuleMatchingList(basSkuRuleMatching);
        if(list.size() == 0){
            return R.ok(basSkuRuleMatching);
        }else{
            return R.ok(list.get(0));
        }
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("import")
    @ApiOperation(value = "导入产品", notes = "导入产品")
    public R importData(MultipartFile file) throws Exception {

        List<BasSkuRuleMatchingImportDto> userList = EasyExcel.read(file.getInputStream(), BasSkuRuleMatchingImportDto.class, new SyncReadListener()).sheet().doReadSync();
        if (CollectionUtils.isEmpty(userList)) {
            throw new BaseException("导入内容为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser) {
            throw new CommonException("500", "非法的操作");
        }
        // 获取登录用户的客户编码
        String sellerCode = loginUser.getSellerCode();

        basSkuRuleMatchingService.importBaseProduct(userList, sellerCode);
        return R.ok();
    }

    /**
    * 新增sku规则匹配表模块
    */
    @PreAuthorize("@ss.hasPermi('BasSkuRuleMatching:BasSkuRuleMatching:edit')")
    @Log(title = "sku规则匹配表模块", businessType = BusinessType.UPDATE)
    @PostMapping("saveOrUpdate")
    @ApiOperation(value = "新增修改sku规则匹配表模块",notes = "新增修改sku规则匹配表模块")
    public R saveOrUpdate(@RequestBody @Validated BasSkuRuleMatching basSkuRuleMatching)
    {
        basSkuRuleMatching.setSystemType(SkuRuleMatchingEnum.SHOPIFY.getCode());
        return toOk(basSkuRuleMatchingService.updateBasSkuRuleMatching(basSkuRuleMatching));
    }

    /**
    * 删除sku规则匹配表模块
    */
    @PreAuthorize("@ss.hasPermi('BasSkuRuleMatching:BasSkuRuleMatching:remove')")
    @Log(title = "sku规则匹配表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除sku规则匹配表模块",notes = "删除sku规则匹配表模块")
    public R remove(@RequestBody List<BasSkuRuleMatching> ids)
    {

        for (BasSkuRuleMatching vo: ids){
            vo.setSystemType(SkuRuleMatchingEnum.SHOPIFY.getCode());

        }

        return toOk(basSkuRuleMatchingService.deleteBasSkuRuleMatchingByIds(ids));
    }

}
