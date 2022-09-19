package com.szmsd.bas.baiduTranslate;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public class Main {

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20210427000803458";
    private static final String SECURITY_KEY = "LK_PxfD9s2ADgtpk400w";



//    public static void main(String[] args) {
//        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
//
//        String query = "有问题 报错了，快给我处理";
//        System.out.println(api.getTransResult(query, "auto", "en"));
//        JSONObject jsonObject= JSON.parseObject(api.getTransResult(query, "auto", "en"));
//        List<Map> list= (List<Map>) jsonObject.get("trans_result");
//        System.out.println(list);
//        String dst=String.valueOf(list.get(0).get("dst"));
//        System.out.println("中文:"+String.valueOf(list.get(0).get("src")));
//        System.out.println("英文:"+dst);
//    }

}
