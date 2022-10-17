package com.szmsd.finance.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.*;

public class HelipayAPIEncrypt {
    public static final Logger logger = LoggerFactory.getLogger(HelipayAPIEncrypt.class);

    public static String sign(Map<String, Object> paramValues, String secret) {
        return sign(paramValues, null, secret);
    }

    public static String sign(Map<String, Object> paramValues, List<String> ignoreParamNames, String secret) {
        Assert.notNull(paramValues, "请求参数缺失,请核实");
        Assert.notNull(secret, "密钥缺失,请核实");
        StringBuilder sb = new StringBuilder();
        List<String> paramNames = new ArrayList<String>(paramValues.size());
        paramNames.addAll(paramValues.keySet());
        if (ignoreParamNames != null && ignoreParamNames.size() > 0) {
            for (String ignoreParamName : ignoreParamNames) {
                paramNames.remove(ignoreParamName);
            }
        }
        Collections.sort(paramNames);

        sb.append(secret);
        for (String key : paramNames) {
            Object value = paramValues.get(key);
            if (value == null || StringUtils.isBlank(value.toString())) {
                continue;
            }
            String value_inner = value.toString();
            if (value_inner.startsWith("[")) {
                sb.append(",").append(key).append("=").append(parseJsonArray(value_inner));
            } else if (value_inner.startsWith("{")) {
                Map requestMap = JSON.parseObject(value_inner);
                sb.append(",").append(key).append("=").append(sign(requestMap, ignoreParamNames, secret));
            } else {
                sb.append(",").append(key).append("=").append(value.toString());
            }
        }
        sb.append(",").append(secret);
        logger.info(" \n 参数签名原文：{} \n", sb.toString());
        return Disguiser.apiDisguise(sb.toString());
    }


    private static JSONArray parseJsonArray(String jsonArrayString) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) JSONArray.parse(jsonArrayString);

        List<JSONObject> result = new LinkedList<JSONObject>();
        for (Map<String, Object> map : list) {
            List<String> paramNames = new ArrayList<String>(map.size());
            paramNames.addAll(map.keySet());
            Collections.sort(paramNames);

            //JSONObject.toJSONString(json, SerializerFeature.SortField)
            JSONObject jsonObject = new JSONObject(true);
            for (String key : paramNames) {
                Object value = map.get(key);
                if (value == null || StringUtils.isBlank(value.toString())) {
                    continue;
                }

                jsonObject.put(key, value);
            }

            result.add(jsonObject);
        }

        logger.info("\nJSONArray 内JSONObject排序：{}\n",result);

        Collections.sort(result, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                Integer o1_index = o1.getInteger("index");
                Integer o2_index = o2.getInteger("index");

                if(o1_index == null || o2_index==null){
                    throw new IllegalArgumentException("参数明细中需要指定INDEX。");
                }

                return o1_index.compareTo(o2_index);
            }
        });

        logger.info("\nJSONArray 排序：{}\n",result.toString());
        JSONArray jsonArray = new JSONArray();
        for(JSONObject jsonObject : result){
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }


}
