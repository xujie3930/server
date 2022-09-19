package com.szmsd.bas.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.baiduTranslate.TransApi;
import com.szmsd.bas.domain.BasTranslate;
import com.szmsd.bas.service.ITranslateService;
import com.szmsd.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/TranslateController")
public class TranslateController {
    @Autowired
    private ITranslateService iTranslateService;
    private static final String APP_ID = "20210427000803458";
    private static final String SECURITY_KEY = "LK_PxfD9s2ADgtpk400w";


    @ApiOperation(value = "异常中英文转换", notes = "异常中英文转换")
    @GetMapping("/Translate")
    public R<String> Translate(@RequestParam("query") String query) {
        try {
            TransApi api = new TransApi(APP_ID, SECURITY_KEY);
            String en="";
            String zh="";
            BasTranslate basTranslate=iTranslateService.selectBasTranslate(query);
            if (basTranslate!=null){
                en=basTranslate.getEnName();
            }
            if (basTranslate==null){
                JSONObject jsonObject= JSON.parseObject(api.getTransResult(query, "auto", "en"));

                List<Map> list= (List<Map>) jsonObject.get("trans_result");

                en=String.valueOf(list.get(0).get("dst"));
                zh=String.valueOf(list.get(0).get("src"));

                int a=  iTranslateService.insetBasTranslate(en,zh);
            }
            return R.ok(en);
        }catch (Exception e){
            e.printStackTrace();
            return R.failed("执行失败");
        }


    }

}
