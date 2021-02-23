package com.szmsd.common.core.aspect;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.CodeToNameElement;
import com.szmsd.common.core.annotation.CodeToNameObjElement;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.CodeToNameEnum;
import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Aspect
@Component
@Slf4j
@Order(0)
public class CodeToNameAspect {

    @Resource
    private RedisService redisService;

    /**
     * @annotation(com.szmsd.common.security.annotation.CodeToName)
     */
    @Pointcut("(execution(public * com.szmsd.*.controller.*.*(..)) || execution(public * com.szmsd.*.service.impl.*.*(..))) " +
            "&& @annotation(com.szmsd.common.core.annotation.CodeToName)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        log.info("CodeToNameAspect运行前 : " + Arrays.toString(joinPoint.getArgs()));

    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "pointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        log.info("CodeToNameAspect运行后,{}", null != jsonResult ? jsonResult.toString() : "");
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "pointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) throws Exception {
        log.info("CodeToNameAspect运行异常,{}", e.toString());
        throw e;
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        log.info("CodeToNameAspect运行环绕start.....");
        Object[] args = pjp.getArgs();
        Object o = null;
        try {
            //如果不执行这句，会不执行切面的Before方法及controller的业务方法
            o = pjp.proceed(args);
            log.info("CodeToNameAspect结果转换前：{}", null != o ? o.toString() : "");
            //返回对象为TableDataInfo
            if (o instanceof TableDataInfo) {
                TableDataInfo a = (TableDataInfo) o;
                List rows = a.getRows();
                if (!CollectionUtils.isEmpty(rows)) {
                    ListUtils.emptyIfNull(rows).forEach(row -> {
                        //不考虑子对象
                        handVal(row);
                    });
                }
            } else if (o instanceof R) {
                R res = (R) o;
                Object data = res.getData();
                if (null != data) {
                    if (data instanceof List) {
                        List list = (List) data;
                        ListUtils.emptyIfNull(list).forEach(row -> {
                            handVal(row);
                        });
                    } else {
                        handVal(data);
                    }
                }
            }else if(o instanceof List){
                List list = (List) o;
                ListUtils.emptyIfNull(list).forEach(row -> {
                    handVal(row);
                });
            }
            log.info("CodeToNameAspect结果转换后：{}", null != o ? o.toString() : "");
            return o;
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("CodeToNameAspect运行环绕end，失败：{}", e);
            throw e;
        }
    }

    private void handVal(Object row) {
        if (null != row) {
//            Map<String, Object> stringObjectMap = BeanMapperUtil.objectToMap(row);
            String s = JSON.toJSON(row).toString();
            Map<String, Object> stringObjectMap = JSONObject.parseObject(s, Map.class);
//            log.info("obj对象转换成map：{}", stringObjectMap);
            Field[] fields = ReflectUtil.getFields(row.getClass());
//            log.info("测试redis数据test123：{}", null != redisService.getCacheObject("test123") ? redisService.getCacheObject("test123").toString() : "");
            ListUtils.emptyIfNull(Arrays.asList(fields)).forEach(field -> {
                field.setAccessible(true);
                CodeToNameElement annotation = field.getAnnotation(CodeToNameElement.class);
                CodeToNameObjElement objAnnotation = field.getAnnotation(CodeToNameObjElement.class);
                try {
                    String filedType = field.getType().toString();
                    if (null != annotation) {
                        if (getType(filedType) && StringUtils.isNotNull(stringObjectMap.get(field.getName()))) {
                            //有注解，该字段还有值，则需要转换成当前语种
                            String code = annotation.keyCode(); //字段对应的code
                            String name = annotation.name(); //默认值
                            CodeToNameEnum type = annotation.type(); //类型
                            if (StringUtils.isNotEmpty(name)) {
                                field.set(row, name);
                            } else if (StringUtils.isNotEmpty(code) && null != type) {
                                String newName = getName(type, stringObjectMap.get(code.trim()));
                                if (StringUtils.isNotEmpty(newName)) {
                                    field.set(row, newName);
                                }
                            }
                        }
                    } else if (filedType.endsWith("List")) {
                        List o = (List) field.get(row);
                        ListUtils.emptyIfNull(o).forEach(son -> handVal(son));

                    } else if (!getType(filedType) && null != objAnnotation) {
                        handVal(field.get(row));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    log.info("Field操作失败:{}", e.toString());
                    throw new RuntimeException(e);
                }
            });
        }
    }


    private String getName(CodeToNameEnum type, Object code) {
        Map<String, Map<String, String>> cacheMap = redisService.getCacheMap(type.getType());
//        log.info("查询{} redis返回数量{}", type.getType(), null != cacheMap ? cacheMap.size() : 0);
        String res = null;
        if (null != cacheMap && cacheMap.size() > 0) {
            switch (getLen().trim().toLowerCase()) {
                case "zh":
                    res = null != cacheMap.get(code) ? cacheMap.get(code).get("zhName") : null;
                    break;
                case "en":
                    res = null != cacheMap.get(code) ? cacheMap.get(code).get("enName") : null;
                    break;
                case "ar":
                    res = null != cacheMap.get(code) ? cacheMap.get(code).get("arName") : null;
                    break;
                default:
                    break;
            }
        }
//        log.info("getName方法：code:{}，返回:{}",code,res);
        return res;
    }

    private String getLen() {
        String len = ServletUtils.getHeaders("Langr");
        if (StringUtils.isEmpty(len)) {
            len = "zh";
        }
        return len;
    }


    private Boolean getType(String type) {
        if (type.endsWith("int") || type.endsWith("long") || type.endsWith("boolean") || type.endsWith("String")
                || type.endsWith("BigDecimal") || type.endsWith("Date") || type.endsWith("Boolean")) {
            return true;
        }
        return false;
    }

}
