package com.szmsd.common.plugin;

import com.szmsd.common.plugin.interfaces.CommonPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * @author zhangyuyuan
 * @date 2021-03-29 10:51
 */
public class CommonPluginContext {
    private final Map<String, List<CommonPlugin>> pluginMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(CommonPluginContext.class);

    private CommonPluginContext() {
    }

    public static CommonPluginContext getInstance() {
        return CommonPluginContextInstance.INSTANCE;
    }

    public void init(ApplicationContext applicationContext) {
        this.pluginMap.clear();
        try {
            Map<String, CommonPlugin> beansOfType = applicationContext.getBeansOfType(CommonPlugin.class);
            for (String key : beansOfType.keySet()) {
                CommonPlugin plugin = beansOfType.get(key);
                String supports = plugin.supports();
                if (this.pluginMap.containsKey(supports)) {
                    this.pluginMap.get(supports).add(plugin);
                } else {
                    List<CommonPlugin> list = new ArrayList<>();
                    list.add(plugin);
                    this.pluginMap.put(supports, list);
                }
            }
            for (List<CommonPlugin> plugins : this.pluginMap.values()) {
                if (plugins.size() < 2) {
                    continue;
                }
                plugins.sort(Comparator.comparingInt(CommonPlugin::getOrder));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取插件
     *
     * @param supports supports
     * @return List
     */
    public List<CommonPlugin> getPlugins(String supports) {
        return this.pluginMap.get(supports);
    }

    private static class CommonPluginContextInstance {
        private static final CommonPluginContext INSTANCE = new CommonPluginContext();
    }
}
