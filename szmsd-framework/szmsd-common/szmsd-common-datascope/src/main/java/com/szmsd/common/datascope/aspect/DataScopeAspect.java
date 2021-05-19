package com.szmsd.common.datascope.aspect;

import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.common.datascope.plugins.SqlContext;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 数据过滤处理
 *
 * @author szmsd
 */
@Aspect
@Component
public class DataScopeAspect {
    public static final int DIMENSION_VALUE_COUNT = 900;
    private final Logger logger = LoggerFactory.getLogger(DataScopeAspect.class);

    /**
     * 数据范围过滤
     *
     * @param loginUser 用户
     * @param dataScope 注解
     */
    private void dataScopeFilter(LoginUser loginUser, DataScope dataScope) {
        StringBuilder sqlString = new StringBuilder();
        // 字段名称
        String fieldName = dataScope.value();
        // 处理数据权限
        // or (x.id in ('1', '2', '3') or x.id in ('4', '5', '6')) or (x.id2 in ('1', '2', '3') or x.id2 in ('4', '5', '6')) or (x.id3 in ('1', '2', '3') or x.id3 in ('4', '5', '6'))
        // or x.id in ('1', '2', '3') or x.id2 in ('1', '2', '3') or x.id3 in ('1', '2', '3')
        List<String> permissions = loginUser.getPermissions();
        if (CollectionUtils.isNotEmpty(permissions)) {
            // 处理纬度超过900
            boolean maxDimension = permissions.size() > DIMENSION_VALUE_COUNT;
            if (maxDimension) {
                sqlString.append(orExpress(fieldName, permissions));
            } else {
                sqlString.append(inExpress(fieldName, permissions));
            }
        }
        if (StringUtils.isNotBlank(sqlString.toString())) {
            SqlContext.getCurrentContext().setSql("(" + sqlString.substring(4) + ")");
        }
    }

    private String orExpress(String siteField, List<String> siteCodeList) {
        int listSize = siteCodeList.size();
        int count = DIMENSION_VALUE_COUNT;
        int batchSize = listSize / count;
        if (listSize % count != 0) {
            batchSize++;
        }
        StringBuilder dimensionValueStr = new StringBuilder();
        dimensionValueStr.append("(");
        for (int i = 0; i < batchSize; i++) {
            int startIndex = (i * count);
            List<String> subList;
            if ((i + 1) == batchSize) {
                int endIndex = siteCodeList.size();
                subList = siteCodeList.subList(startIndex, endIndex);
            } else {
                int endIndex = (i + 1) * count;
                subList = siteCodeList.subList(startIndex, endIndex);
            }
            if (i > 0) {
                dimensionValueStr.append(" or ");
            }
            dimensionValueStr.append(inExpress(siteField, subList));
        }
        // (x.id in ('1', '2', '3') or x.id in ('4', '5', '6'))
        return dimensionValueStr.append(")").toString();
    }

    private String inExpress(String siteField, List<String> siteCodeList) {
        StringBuilder dimensionValueStr = new StringBuilder();
        for (String dimensionValue : siteCodeList) {
            dimensionValueStr.append("'").append(dimensionValue).append("'").append(",");
        }
        if (!StringUtils.isEmpty(dimensionValueStr.toString())) {
            dimensionValueStr.delete(dimensionValueStr.length() - 1, dimensionValueStr.length());
        }
        // x.id in ('1', '2', '3')
        return (siteField + " in  (" + dimensionValueStr.toString() + ")");
    }

    // 配置织入点
    @Pointcut("@annotation(com.szmsd.common.datascope.annotation.DataScope)")
    public void dataScopePointCut() {
    }

    @Before("dataScopePointCut()")
    public void doBefore(JoinPoint point) throws Throwable {
        handleDataScope(point);
    }

    @After("dataScopePointCut()")
    public void doAfter(JoinPoint point) {
        SqlContext.getCurrentContext().clear();
    }

    protected void handleDataScope(final JoinPoint joinPoint) {
        // 获得注解
        DataScope dataScope = getAnnotationLog(joinPoint);
        if (dataScope == null) {
            logger.error("-------->>>切面注解无效");
            return;
        }
        // 判断value有没有值
        String value = dataScope.value();
        if (StringUtils.isEmpty(value)) {
            logger.error("-------->>>切面注解Value值为空");
            return;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();

        // 获取当前的用户
        if (null != loginUser) {
            // 如果是超级管理员，则不过滤数据
            if (!loginUser.isAllDataScope()) {
                dataScopeFilter(loginUser, dataScope);
            }
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private DataScope getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(DataScope.class);
        }
        return null;
    }
}
