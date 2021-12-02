package com.szmsd.bas.plugin;

import com.szmsd.bas.plugin.service.BasSubFeignPluginService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.plugin.interfaces.CacheContext;
import com.szmsd.common.plugin.interfaces.CommonPlugin;
import com.szmsd.common.plugin.interfaces.DefaultCommonParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-29 11:26
 */
@Slf4j
@Component
public class BasSubCommonPlugin implements CommonPlugin {
    public static final String SUPPORTS = "BasSub";

    @Autowired
    private BasSubFeignPluginService basSubFeignPluginService;

    @Override
    public String supports() {
        return SUPPORTS;
    }

    @Override
    public Map<Object, Object> handlerValue(List<Object> list, DefaultCommonParameter cp, CacheContext cacheContext) {
        String code = (String) cp.getObject();
        String valueField;
        if (cp instanceof BasSubCodeCommonParameter) {
            valueField = "subCode";
        } else {
            valueField = "subValue";
        }
        Map<Object, Object> map = new HashMap<>();
        R<Map<String, List<BasSubWrapperVO>>> r = this.basSubFeignPluginService.getSub(code);
        if (null != r) {
            Map<String, List<BasSubWrapperVO>> voMap = r.getData();
            if (null != voMap) {
                List<BasSubWrapperVO> voList = voMap.get(code);
                if (CollectionUtils.isNotEmpty(voList)) {
                    for (BasSubWrapperVO vo : voList) {
                        if ("subCode".equals(valueField)) {
                            map.put(vo.getSubCode(), vo.getSubName());
                        } else {
                            map.put(vo.getSubValue(), vo.getSubName());
                        }
                    }
                }
            } else {
                log.info("加载语言失败：{}", r.getMsg());
            }
        }
        return map;
    }
}
