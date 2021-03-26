package com.szmsd.bas.plugin;

import com.szmsd.bas.plugin.service.BasSubFeignPluginService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 10:48
 */
public class HandlerContext<T> {
    private final Logger logger = LoggerFactory.getLogger(HandlerContext.class);

    private final CacheContext cacheContext = new CacheContext.HandlerCacheContext();
    private final BasSubFeignPluginService basSubFeignPluginService;
    private final T t;

    public HandlerContext(BasSubFeignPluginService basSubFeignService, T t) {
        this.basSubFeignPluginService = basSubFeignService;
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

    @SuppressWarnings({"unchecked"})
    private void doJsonEncrypt(Field field, AutoFieldValue autoFieldValue, Object object) {
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
            // 从数据字典上对应值的字段
            String valueField = autoFieldValue.valueField();
            if ("".equals(valueField)) {
                valueField = "subCode";
            }
            // 需要赋值的字段
            String nameField = autoFieldValue.nameField();
            if ("".equals(nameField)) {
                nameField = field.getName() + "Name";
            }
            // 数据字典的主编码
            String code = autoFieldValue.code();
            Map<String, BasSubWrapperVO> map;
            if (this.cacheContext.containsKey(code)) {
                map = (Map<String, BasSubWrapperVO>) this.cacheContext.get(code);
            } else {
                map = new HashMap<>();
                R<Map<String, List<BasSubWrapperVO>>> r = this.basSubFeignPluginService.getSub(code);
                if (null != r) {
                    Map<String, List<BasSubWrapperVO>> voMap = r.getData();
                    if (null != voMap) {
                        List<BasSubWrapperVO> voList = voMap.get(code);
                        if (CollectionUtils.isNotEmpty(voList)) {
                            for (BasSubWrapperVO vo : voList) {
                                if ("subCode".equals(valueField)) {
                                    map.put(vo.getSubCode(), vo);
                                } else {
                                    map.put(vo.getSubValue(), vo);
                                }
                            }
                        }
                    }
                }
                this.cacheContext.set(code, map);
            }
            // 获取值
            BasSubWrapperVO vo = map.get(String.valueOf(val));
            if (null != vo) {
                String setValue = vo.getSubName();
                if (StringUtils.isNotEmpty(setValue)) {
                    try {
                        // 设置值
                        MethodUtils.invokeMethod(object, ObjectUtil.toSetMethod(nameField), setValue);
                    } catch (NoSuchMethodException e) {
                        logger.error("对象中无方法可调用" + e.getMessage(), e);
                    } catch (InvocationTargetException e) {
                        logger.error("执行目标方法失败" + e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
