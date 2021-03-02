package com.szmsd.bas.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 查询树形
 *
 * @author chenanze
 * @date 2020-07-08
 * @description
 */
public class Tree {
    //查询树形
    public static JSONArray treeRecursionDataList(List<Map<String, Object>> treeList, String parentId) {
        JSONArray childMenu = new JSONArray();
        for (Object object : treeList) {
            JSONObject jsonMenu = JSONObject.parseObject(JSON.toJSONString(object));
            String category_code = jsonMenu.getString("current_id");
            String parent_category_code = jsonMenu.getString("father_id");
            if (parentId.equals(parent_category_code)) {
                JSONArray c_node = treeRecursionDataList(treeList, category_code);
                if (!c_node.isEmpty()) {
                    jsonMenu.put("isLast", false);
                } else {
                    jsonMenu.put("isLast", true);
                }
                childMenu.add(jsonMenu);
                if (!c_node.toJSONString().trim().equals("[]")) {
                    jsonMenu.put("childList", c_node);
                }
            }
        }
        return childMenu;
    }

}
