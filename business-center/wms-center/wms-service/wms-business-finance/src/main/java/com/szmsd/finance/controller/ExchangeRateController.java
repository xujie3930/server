package com.szmsd.finance.controller;

import com.szmsd.bas.plugin.service.BasSubFeignPluginService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.finance.domain.ExchangeRate;
import com.szmsd.finance.dto.ExchangeRateDTO;
import com.szmsd.finance.service.IExchangeRateService;
import com.szmsd.finance.util.DownloadTemplateUtil;
import com.szmsd.finance.util.ExcelFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author liulei
 */
@Api(tags = {"汇率管理"})
@RestController
@RequestMapping("/exchangeRate")
public class ExchangeRateController extends FssBaseController {

    @Autowired
    IExchangeRateService exchangeRateService;

    @Autowired
    private BasSubFeignPluginService basSubFeignPluginService;


    @AutoValue
    @PreAuthorize("@ss.hasPermi('ExchangeRate:listPage')")
    @ApiOperation(value = "分页查询汇率信息")
    @GetMapping("/listPage")
    public TableDataInfo listPage(ExchangeRateDTO dto){
        startPage();
        List<ExchangeRate> list =exchangeRateService.listPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:save')")
    @ApiOperation(value = "保存汇率")
    @PostMapping("/save")
    public R save(@RequestBody ExchangeRateDTO dto){
        return exchangeRateService.save(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:update')")
    @ApiOperation(value = "更新汇率")
    @PutMapping("/update")
    public R update(@RequestBody ExchangeRateDTO dto){
        return exchangeRateService.update(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:delete')")
    @ApiOperation(value = "删除汇率")
    @DeleteMapping("/delete")
    public R delete(@RequestParam("id") Long id){
        return exchangeRateService.delete(id);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:selectRate')")
    @ApiOperation(value = "汇率转换查询")
    @GetMapping("/selectRate")
    @AutoValue
    public R selectRate(@RequestParam("currencyFromCode") String currencyFromCode,@RequestParam("currencyToCode") String currencyToCode){
        return exchangeRateService.selectRate(currencyFromCode,currencyToCode);
    }

    @ApiOperation(value = "汇率业务 - 下载模版")
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "exchangeRateTemplate");
    }



   @PostMapping("/uploadExchangeRate")
   @ApiOperation(value = "汇率导入")
   @Transactional(rollbackFor = Exception.class)
    public R uploadExchangeRate(@RequestPart("file") MultipartFile file) throws IOException {
        //定义参数
        ArrayList<String> addList= new ArrayList<>();
//        ArrayList<String> updateList= new ArrayList<>();
        Collections.addAll(addList,"exchangeFrom","exchangeTo","rate","expireTime","remark");
//        Collections.addAll(updateList,"id","expropriatedPerson","fileNumber","certificateType","certificateNumber","propertyAddress","liveAddress","telephone","prepareStyle","tenementRemarks","ownerName","telephone","certificateType","certificateNumber");

        //把字段名和导入数据匹配对应 存入数据库
        try {
            //查询币别
            R<Map<String, List<BasSubWrapperVO>>> maps=basSubFeignPluginService.getSub("008");
            List<BasSubWrapperVO>  baslist=maps.getData().get("008");

            //汇率专用
            List<Map> mapList = ExcelFile.getExcelDataFinance(file,addList);

            for (int x=0;x<mapList.size();x++) {

                    if (String.valueOf(mapList.get(x).get("exchangeFrom")).equals("") || String.valueOf(mapList.get(x).get("exchangeTo")).equals("") || String.valueOf(mapList.get(x).get("rate")).equals("") || String.valueOf(mapList.get(x).get("expireTime")).equals("")) {
                        throw new BaseException("第" + (x + 2) + "行的导入数据需要填写必填项，必填项为（原币别，现币别，比率，失效时间）");
                    }
                String expireTimes=String.valueOf(mapList.get(x).get("expireTime"));
                String str = expireTimes.substring(0, 10);
                String expireTime=str+" "+"23:59:59";
                mapList.get(x).put("expireTime",expireTime);
                    for (int i = 0; i < baslist.size(); i++) {
                        //根据中英文匹配现有的货币code
                        if (String.valueOf(mapList.get(x).get("exchangeFrom")).equals(baslist.get(i).getSubName()) || String.valueOf(mapList.get(x).get("exchangeFrom")).equals(baslist.get(i).getSubNameEn())) {
                            mapList.get(x).put("exchangeFromCode", baslist.get(i).getSubValue());
                        }
                        if (String.valueOf(mapList.get(x).get("exchangeTo")).equals(baslist.get(i).getSubName()) || String.valueOf(mapList.get(x).get("exchangeTo")).equals(baslist.get(i).getSubNameEn())) {
                            mapList.get(x).put("exchangeToCode", baslist.get(i).getSubValue());
                        }
                    }

                //验证数据库是否存在(需针对原币别+现币别+失败时间，进行唯一判断)
                List<ExchangeRateDTO> list=exchangeRateService.selectRates(mapList.get(x));
                if (list.size()>0){
                    exchangeRateService.deleteExchangeRate(mapList.get(x));
                    //throw new BaseException("第" + (x + 2) + "行的导入数据已存在数据库");
                }
            }
            exchangeRateService.insertExchangeRate(mapList);
            return R.ok("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed(((BaseException) e).getDefaultMessage());
        }

    }

}
