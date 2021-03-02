package com.szmsd.bas.controller;

import com.szmsd.bas.domain.*;
import com.szmsd.bas.service.IBasCuspriceCodeService;
import com.szmsd.bas.service.IBasCuspriceService;
import com.szmsd.bas.service.IBasFormulaService;
import com.szmsd.common.core.annotation.CodeToName;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-06-29
 */

@Api(tags = {"客户报价模块"})
@RestController
@RequestMapping("/bas-cusprice")
public class BasCuspriceController extends BaseController {


    @Resource
    private IBasCuspriceService basCuspriceService;

    @Resource
    private IBasFormulaService basFormulaService;

    @Resource
    private IBasCuspriceCodeService basCuspriceCodeService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bascusprice:list')")
    @ApiOperation(value = "查询客户报价列表", notes = "查询客户报价列表('bas:bascusprice:list')")
    @GetMapping("/list")
    @CodeToName
    public TableDataInfo list(BasCusprice basCusprice) {
        startPage();
        List<BasCusprice> list = basCuspriceService.selectBasCuspriceList(basCusprice);
        for (BasCusprice basCusprice1 : list) {
            //客户list
            BasCuspriceCode basCuspriceCode = new BasCuspriceCode();
            basCuspriceCode.setCuspriceId(basCusprice1.getId());
            basCuspriceCode.setTypes("1");
            List<BasCuspriceCode> list1 = basCuspriceCodeService.selectBasCuspriceCodeList(basCuspriceCode);
            basCusprice1.setCusList(list1);

            //目的地 list
            BasCuspriceCode basCuspriceCodes = new BasCuspriceCode();
            basCuspriceCodes.setCuspriceId(basCusprice1.getId());
            basCuspriceCodes.setTypes("2");
            List<BasCuspriceCode> lists = basCuspriceCodeService.selectBasCuspriceCodeList(basCuspriceCodes);
            basCusprice1.setDesList(lists);
        }
        return getDataTable(list);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bascusprice:export')")
    @ApiOperation(value = "导出客户报价列表", notes = "导出客户报价列表('bas:bascusprice:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasCusprice basCusprice) throws IOException {
        List<BasCusprice> list = basCuspriceService.selectBasCuspriceList(basCusprice);
        ExcelUtil<BasCusprice> util = new ExcelUtil<BasCusprice>(BasCusprice.class);
        util.exportExcel(response, list, "BasCusprice");
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascusprice:add')")
    @ApiOperation(value = "新增客户报价列表", notes = "新增客户报价列表('bas:bascusprice:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody List<BasCusprice> basCuspriceList) {
        for (BasCusprice basCusprice : basCuspriceList) {
            if (!StringUtils.isNotEmpty(basCusprice.getId())) {
                String uid = UUID.randomUUID().toString().substring(0, 32);
                basCusprice.setCreateTime(new Date());
                basCusprice.setId(uid);
                basCusprice.setCreateBy(SecurityUtils.getUsername());
                for (BasCuspriceCode basCuspriceCode : basCusprice.getCusList()) {
                    String sid = UUID.randomUUID().toString().substring(0, 20);
                    basCuspriceCode.setId(sid);
                    basCuspriceCode.setCuspriceId(uid);
                    basCuspriceCodeService.insertBasCuspriceCode(basCuspriceCode);
                }
                for (BasCuspriceCode basCuspriceCodes : basCusprice.getDesList()) {
                    String tid = UUID.randomUUID().toString().substring(0, 20);
                    basCuspriceCodes.setId(tid);
                    basCuspriceCodes.setCuspriceId(uid);
                    basCuspriceCodeService.insertBasCuspriceCode(basCuspriceCodes);
                }
                basCuspriceService.insertBasCusprice(basCusprice);
            }
        }
        return R.ok();
    }

    /**
     * 修改客户报价列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bascusprice:edit')")
    @Log(title = "修改子类别列表", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改客户报价列表", notes = "修改客户报价列表('bas:bascusprice:edit')")
    @PutMapping
    public R edit(@RequestBody BasCusprice basCusprice) {
        basCuspriceCodeService.deleteByCusId(basCusprice.getId());
        for (BasCuspriceCode basCuspriceCode : basCusprice.getCusList()) {
            basCuspriceCode.setCuspriceId(null);
            basCuspriceCode.setId(null);
            String sid = UUID.randomUUID().toString().substring(0, 20);
            basCuspriceCode.setId(sid);
            basCuspriceCode.setCuspriceId(basCusprice.getId());
            basCuspriceCodeService.insertBasCuspriceCode(basCuspriceCode);
        }
        for (BasCuspriceCode basCuspriceCodes : basCusprice.getDesList()) {
            basCuspriceCodes.setCuspriceId(null);
            basCuspriceCodes.setId(null);
            String tid = UUID.randomUUID().toString().substring(0, 20);
            basCuspriceCodes.setId(tid);
            basCuspriceCodes.setCuspriceId(basCusprice.getId());
            basCuspriceCodeService.insertBasCuspriceCode(basCuspriceCodes);
        }
        basCusprice.setUpdateTime(new Date());
        return toOk(basCuspriceService.updateBasCusprice(basCusprice));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascusprice:remove')")
    @ApiOperation(value = "删除客户报价列表", notes = "删除客户报价列表('bas:bascusprice:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {

        return toOk(basCuspriceService.deleteBasCuspriceByIds(ids));
    }


    /**
     * 报价试算模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bascusprice:cus')")
    @ApiOperation(value = "客户报价试算", notes = "客户报价试算('bas:bascusprice:cus')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @RequestMapping("/select")
    public R cus(Cus cus) {
        if (!StringUtils.isNotEmpty(cus.getCusCode())) {
            return R.failed(ExceptionMessageEnum.EXPBASIS007);
        }
        if (!StringUtils.isNotEmpty(cus.getDestinationCode())) {
            return R.failed(ExceptionMessageEnum.EXPBASIS008);
        }
        if (!StringUtils.isNotEmpty(cus.getProductTypeCode())) {
            return R.failed(ExceptionMessageEnum.EXPBASIS009);
        }
        if (!StringUtils.isNotEmpty(cus.getCostCategory())) {
            return R.failed(ExceptionMessageEnum.EXPBASIS010);
        }
        BasCusprice basCusprice = new BasCusprice();
        //根据客户id查询
        BasCuspriceCode basCuspriceCode = new BasCuspriceCode();
        basCuspriceCode.setCode(cus.getCusCode());
        List<BasCuspriceCode> listes = basCuspriceCodeService.selectBasCuspriceCodeList(basCuspriceCode);
        //根据目的地id查询
        BasCuspriceCode basCuspriceCodes = new BasCuspriceCode();
        basCuspriceCodes.setCode(cus.getDestinationCode());
        List<BasCuspriceCode> listss = basCuspriceCodeService.selectBasCuspriceCodeList(basCuspriceCodes);
        List<String> listess = new ArrayList();
        for (BasCuspriceCode basCuspriceC : listes) {
            for (BasCuspriceCode basCuspriceCS : listss) {
                if (basCuspriceC.getCuspriceId().equals(basCuspriceCS.getCuspriceId())) {
                    listess.add(basCuspriceC.getCuspriceId());
                    continue;
                }
            }
        }
        BasFormula basFormula = new BasFormula();
        basCusprice.setProductTypeCode(cus.getProductTypeCode());
        basCusprice.setCostCategory(cus.getCostCategory());
        SimpleDateFormat sim = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        basCusprice.setTime(sim.format(new Date()));
        List<BasCusprice> list = basCuspriceService.selectBasCuspriceList(basCusprice);

        List<BasCusprice> list1 = new ArrayList();
        for (BasCusprice basCusprice1 : list) {
            for (String id : listess) {
                if (id.equals(basCusprice1.getId())) {
                    list1.add(basCusprice1);
                    break;
                }
            }
        }
        if (list1.size() == 0) {
            return R.failed(ExceptionMessageEnum.EXPBASIS011);
        } else {
            basCusprice = list1.get(0);
            String h = basCusprice.getChargingPatternCode();
            if (StringUtils.isEmpty(h)) {
                return R.failed(ExceptionMessageEnum.EXPBASIS012);
            }
            BigDecimal bigDecimals = new BigDecimal(cus.getHeft());
            //判断进位 021001=实际重量， 021002=进位舍一 ，021003=进位舍二 021004=0.5进位
            Double res = null;
            if (h.equals("021001")) {
                res = bigDecimals.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
            } else if (h.equals("021002")) {
                res = bigDecimals.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            } else if (h.equals("021003")) {
                res = bigDecimals.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            } else if (h.equals("021004")) {
                double weight = Double.parseDouble(cus.getHeft());
                double floor = Math.floor(weight);
                if (floor + 0.5 < weight) {
                    weight = floor + 1;
                } else if (floor + 0.5 == weight + 0.5) {
                    weight = floor;
                } else {
                    weight = floor + 0.5;
                }
                res = weight;
            }
            cus.setHeft(res.toString());
            String s = basCusprice.getId();
            basFormula.setCuspriceId(s);
            BigDecimal bigDecimal = null;
            List<BasFormula> lists = basFormulaService.selectBasFormulaList(basFormula);
            if (lists.size() == 0) {
                return R.failed(ExceptionMessageEnum.EXPBASIS012);
            }
            for (BasFormula basFormula1 : lists) {
                String weight = basFormula1.getWeight();
                System.out.println("==============" + Calculation.analyticalMathematicalFormula(weight, res.toString()));
                Boolean o = (Boolean) Calculation.analyticalMathematicalFormula(weight, res.toString());

                if (o) {
                    Object o1 = Calculation.analyticalMathematicalFormula(basFormula1.getCalculateFormula(), res.toString());
                    bigDecimal = new BigDecimal(o1.toString());
                    break;
                }
            }
            return R.ok(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }

    }

}
