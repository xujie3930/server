package com.szmsd.common.plugin;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import com.szmsd.common.plugin.interfaces.AbstractCommonParameter;
import com.szmsd.common.plugin.interfaces.CacheContext;
import com.szmsd.common.plugin.interfaces.CommonPlugin;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 10:48
 */
public class HandlerContext<T> {
    private final Logger logger = LoggerFactory.getLogger(HandlerContext.class);

    private final CacheContext cacheContext = new CacheContext.HandlerCacheContext();
    private final T t;

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
            for (Object object : iterable) {
                doJsonEncrypt(object);
            }
        } else if (source instanceof TableDataInfo) {
            TableDataInfo<?> tableDataInfo = (TableDataInfo<?>) source;
            List<?> rows = tableDataInfo.getRows();
            if (CollectionUtils.isNotEmpty(rows)) {
                for (Object row : rows) {
                    doJsonEncrypt(row);
                }
            }
        } else if (source instanceof R) {
            R<?> result = (R<?>) source;
            if (null != result.getData()) {
                doJsonEncrypt(result.getData());
            }
        } else {
            doJsonEncrypt(source);
        }
    }

    private void doJsonEncrypt(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        if (ArrayUtils.isNotEmpty(fields)) {
            for (Field field : fields) {
                AutoFieldValue autoFieldValue = field.getAnnotation(AutoFieldValue.class);
                if (null != autoFieldValue) {
                    doJsonEncrypt(field, autoFieldValue, object);
                }
            }
        }
    }

    private void doJsonEncrypt(Field field, AutoFieldValue autoFieldValue, Object object) {
        // get plugin
        List<CommonPlugin> plugins = CommonPluginContext.getInstance().getPlugins(autoFieldValue.supports());
        if (CollectionUtils.isEmpty(plugins)) {
            return;
        }
        try {
            field.setAccessible(true);
            Object val;
            try {
                val = MethodUtils.invokeMethod(object, ObjectUtil.convertToGetMethod(field.getName()));
            } catch (NoSuchMethodException e) {
                logger.error("对象中无方法可调用" + e.getMessage(), e);
                val = null;
            } catch (InvocationTargetException e) {
                logger.error("执行目标方法失败" + e.getMessage(), e);
                val = null;
            }
            if (null == val) {
                return;
            }
            // 需要赋值的字段
            String nameField = autoFieldValue.nameField();
            if ("".equals(nameField)) {
                nameField = field.getName() + "Name";
            }
            // 获取值
            Object setValue = null;
            if (this.cacheContext.containsKey(val)) {
                setValue = this.cacheContext.get(val);
            } else {
                for (CommonPlugin plugin : plugins) {
                    setValue = plugin.handlerValue(val, cp(autoFieldValue.cp(), autoFieldValue.code()), this.cacheContext);
                }
                this.cacheContext.set(val, setValue);
            }
            if (Objects.nonNull(setValue)) {
                try {
                    // 设置值
                    MethodUtils.invokeMethod(object, ObjectUtil.toNormalSetMethod(nameField), setValue);
                } catch (NoSuchMethodException e) {
                    logger.error("对象中无方法可调用" + e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    logger.error("执行目标方法失败" + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private AbstractCommonParameter cp(Class<? extends AbstractCommonParameter> cc, String code) {
        try {
            AbstractCommonParameter instance = cc.newInstance();
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
