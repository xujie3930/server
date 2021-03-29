package com.szmsd.common.plugin;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import com.szmsd.common.plugin.interfaces.CacheContext;
import com.szmsd.common.plugin.interfaces.CommonPlugin;
import com.szmsd.common.plugin.interfaces.DefaultCommonParameter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 10:48
 */
public class HandlerContext<T> {
    private final Logger logger = LoggerFactory.getLogger(HandlerContext.class);

    private final CacheContext cacheContext = new CacheContext.HandlerCacheContext();
    private final T t;
    private boolean initCacheData = false;

    public HandlerContext(T t) {
        this.t = t;
    }

    public T handlerValue() {
        handlerAutoValue(t);
        return t;
    }

    private void handlerAutoValue(Object source) {
        if (source instanceof List) {
            Iterable<?> iterable = (Iterable<?>) source;
            doJsonEncrypt(iterable);
        } else if (source instanceof TableDataInfo) {
            TableDataInfo<?> tableDataInfo = (TableDataInfo<?>) source;
            List<?> rows = tableDataInfo.getRows();
            if (CollectionUtils.isNotEmpty(rows)) {
                doJsonEncrypt(rows);
            }
        } else if (source instanceof R) {
            R<?> result = (R<?>) source;
            Object data = result.getData();
            if (null != data) {
                handlerAutoValue(data);
            }
        } else {
            doJsonEncrypt(source);
        }
    }

    private void doJsonEncrypt(Iterable<?> iterable) {
        // 验证是否存在分组处理的数据
        getCacheData(iterable);
        for (Object object : iterable) {
            doJsonEncrypt(object);
        }
    }

    private void getCacheData(Iterable<?> iterable) {
        if (this.initCacheData) {
            return;
        }
        this.initCacheData = true;
        Object next = iterable.iterator().next();
        Field[] fields = getFields(next);
        if (ArrayUtils.isNotEmpty(fields)) {
            Map<String, AutoFieldValue> autoFieldValueMap = new HashMap<>();
            for (Field field : fields) {
                AutoFieldValue autoFieldValue = field.getAnnotation(AutoFieldValue.class);
                if (null != autoFieldValue) {
                    autoFieldValueMap.put(field.getName(), autoFieldValue);
                }
            }
            if (!autoFieldValueMap.isEmpty()) {
                String[] groupByField = autoFieldValueMap.keySet().toArray(new String[]{});
                Map<String, Set<Object>> stringSetMap = new HashMap<>(groupByField.length);
                for (Object object : iterable) {
                    for (String field : groupByField) {
                        Object value = getValue(object, field);
                        if (null == value) {
                            continue;
                        }
                        if (stringSetMap.containsKey(field)) {
                            stringSetMap.get(field).add(value);
                        } else {
                            Set<Object> set = new HashSet<>();
                            set.add(value);
                            stringSetMap.put(field, set);
                        }
                    }
                }
                for (String field : groupByField) {
                    // get plugin
                    AutoFieldValue autoFieldValue = autoFieldValueMap.get(field);
                    List<CommonPlugin> plugins = CommonPluginContext.getInstance().getPlugins(autoFieldValue.supports());
                    if (CollectionUtils.isEmpty(plugins)) {
                        return;
                    }
                    Set<Object> objectSet = stringSetMap.get(field);
                    Map<Object, Object> map = Collections.emptyMap();
                    for (CommonPlugin plugin : plugins) {
                        map = plugin.handlerValue(new ArrayList<>(objectSet), cp(autoFieldValue.cp(), autoFieldValue.code()), this.cacheContext);
                    }
                    this.cacheContext.set(field, map);
                }
            }
        }
    }

    private void doJsonEncrypt(Object object) {
        List<Object> list = new ArrayList<>();
        list.add(object);
        getCacheData(list);
        Field[] fields = getFields(object);
        if (ArrayUtils.isNotEmpty(fields)) {
            for (Field field : fields) {
                AutoFieldValue autoFieldValue = field.getAnnotation(AutoFieldValue.class);
                if (null != autoFieldValue) {
                    doJsonEncrypt(field, autoFieldValue, object);
                }
            }
        }
    }

    private Field[] getFields(Object object) {
        return object.getClass().getDeclaredFields();
    }

    private Object getValue(Object object, String fieldName) {
        Object val;
        try {
            val = MethodUtils.invokeMethod(object, ObjectUtil.convertToGetMethod(fieldName));
        } catch (NoSuchMethodException e) {
            logger.error("对象中无方法可调用" + e.getMessage(), e);
            val = null;
        } catch (InvocationTargetException e) {
            logger.error("执行目标方法失败" + e.getMessage(), e);
            val = null;
        } catch (IllegalAccessException e) {
            logger.error("没有访问权限" + e.getMessage(), e);
            val = null;
        }
        return val;
    }

    private void setValue(Object object, String fieldName, Object value) {
        try {
            // 设置值
            MethodUtils.invokeMethod(object, ObjectUtil.toNormalSetMethod(fieldName), value);
        } catch (NoSuchMethodException e) {
            logger.error("对象中无方法可调用" + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error("执行目标方法失败" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error("没有访问权限" + e.getMessage(), e);
        }
    }

    @SuppressWarnings({"unchecked"})
    private void doJsonEncrypt(Field field, AutoFieldValue autoFieldValue, Object object) {
        try {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object val = this.getValue(object, fieldName);
            if (null == val) {
                return;
            }
            // 需要赋值的字段
            String nameField = autoFieldValue.nameField();
            if ("".equals(nameField)) {
                nameField = fieldName + "Name";
            }
            // 获取值
            Object setValue = null;
            if (this.cacheContext.containsKey(fieldName)) {
                Map<Object, Object> map = (Map<Object, Object>) this.cacheContext.get(fieldName);
                if (null != map) {
                    setValue = map.get(val);
                }
            }
            if (Objects.nonNull(setValue)) {
                this.setValue(object, nameField, setValue);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private DefaultCommonParameter cp(Class<? extends DefaultCommonParameter> cc, String code) {
        try {
            DefaultCommonParameter instance = cc.newInstance();
            instance.setObject(code);
            return instance;
        } catch (InstantiationException e) {
            logger.error("InstantiationException:" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException:" + e.getMessage(), e);
        }
        return null;
    }
}
