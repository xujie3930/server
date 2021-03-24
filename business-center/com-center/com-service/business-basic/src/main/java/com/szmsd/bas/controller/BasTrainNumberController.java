package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.domain.BasTrainNumber;
import com.szmsd.bas.service.BasCodeService;
import com.szmsd.bas.service.IBasTrainNumberService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 车次表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-11-18
 */

@Api(tags = {"车次表"})
@RestController
@RequestMapping("/bas-train-number")
public class BasTrainNumberController extends BaseController {

    @Resource
    private BasCodeService basCodeService;

    @Resource
    private IBasTrainNumberService basTrainNumberService;

    private static final String APP_ID = "gfs";

    private static final String CODE = "car";
    /**
     * 查询车次表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasTrainNumber:BasTrainNumber:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasTrainNumber basTrainNumber) {
        startPage();
        List<BasTrainNumber> list = basTrainNumberService.selectBasTrainNumberList(basTrainNumber);
        return getDataTable(list);
    }

    /**
     * 查询车次表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasTrainNumber:BasTrainNumber:getTrainNumber')")
    @ApiOperation(value = "查询最新车次", notes = "查询最新车次")
    @GetMapping("/getTrainNumber")
    public R<BasTrainNumber> getTrainNumber(BasTrainNumber basTrainNumber) {
        List<BasTrainNumber> list = basTrainNumberService.selectBasTrainNumberList(basTrainNumber);
        return R.ok(list.get(0));
    }


    /**
     * 查询车次表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasTrainNumber:BasTrainNumber:lists')")
    @ApiOperation(value = "APP查询", notes = "APP查询")
    @GetMapping("/lists")
    public R lists(BasTrainNumber basTrainNumber) {
        List<BasTrainNumber> list = basTrainNumberService.selectBasTrainNumberList(basTrainNumber);
        return CollectionUtils.isEmpty(list)? R.ok(): R.ok(list.get(0));
    }

    /**
     * 导出车次表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasTrainNumber:BasTrainNumber:export')")
    @Log(title = "车次表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasTrainNumber basTrainNumber) throws IOException {
        List<BasTrainNumber> list = basTrainNumberService.selectBasTrainNumberList(basTrainNumber);
        ExcelUtil<BasTrainNumber> util = new ExcelUtil<BasTrainNumber>(BasTrainNumber.class);
        util.exportExcel(response, list, "BasTrainNumber");
    }

    /**
     * 新增车次表模块
     */
    @PreAuthorize("@ss.hasPermi('BasTrainNumber:BasTrainNumber:add')")
    @Log(title = "车次表模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasTrainNumber basTrainNumber) {
        basTrainNumber.setCreateTime(new Date());
        String routeCode = basTrainNumber.getRouteCode();
//        Date date = new Date();
//        SimpleDateFormat format = new SimpleDateFormat("MMdd");
//        String s = format.format(date);
        BasCodeDto basCode=new BasCodeDto();
        basCode.setAppId(APP_ID);
        basCode.setCode(CODE);
        R code = basCodeService.createCode(basCode);
        String string = code.getData().toString();
        String substring = string.substring(1, string.length() - 1);
        String carCode=routeCode+"-"+substring;
        basTrainNumber.setTrainNumber(carCode);
        return toOk(basTrainNumberService.insertBasTrainNumber(basTrainNumber));
    }



    /**
     * 修改车次表模块
     */
    @PreAuthorize("@ss.hasPermi('BasTrainNumber:BasTrainNumber:edit')")
    @Log(title = "车次表模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasTrainNumber basTrainNumber) {
        basTrainNumber.setUpdateTime(new Date());
        return toOk(basTrainNumberService.updateBasTrainNumber(basTrainNumber));
    }

    /**
     * 删除车次表模块
     */
    @PreAuthorize("@ss.hasPermi('BasTrainNumber:BasTrainNumber:remove')")
    @Log(title = "车次表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basTrainNumberService.deleteBasTrainNumberByIds(ids));
    }

}
