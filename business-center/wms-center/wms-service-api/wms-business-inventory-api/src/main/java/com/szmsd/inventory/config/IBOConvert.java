package com.szmsd.inventory.config;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @ClassName: IBOConvert
 * @Description: DTO VO  entity 类转换
 * @Author: 11
 * @Date: 2021/3/26 19:33
 */
public interface IBOConvert {

    /**
     * 实体类继承转换
     *
     * @param s   被转换的类型
     * @param t   要转换的类型
     * @param <T> 要转换的类型
     * @return 转换结果
     */
    default <S, T> T convert(S s, Class<T> t) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(s), t);
    }

    /**
     * 实体类继承转换
     *
     * @param t   要转换的类型
     * @param <T> 要转换的类型
     * @return 转换结果
     */
    default <T> T convertThis(Class<T> t) {
        Assert.notNull(this);
        return JSONUtil.toBean(JSONUtil.toJsonStr(this), t);
    }

    /**
     * 参数校验
     *
     * @param i      第x条数据
     * @param groups 校验规则
     */
    default void validate(AtomicInteger i, Class<?>... groups) {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        Validator validator = vf.getValidator();
        Set<ConstraintViolation<Object>> set = validator.validate(this, groups);
        List<String> errorMsg = new ArrayList<>();
        List<Integer> idList = new ArrayList<>();
        for (ConstraintViolation<Object> constraintViolation : set) {
            System.out.println(constraintViolation.getPropertyPath() + ":" + constraintViolation.getMessage());
            errorMsg.add(constraintViolation.getMessage());
            idList.add(i.get());
        }

        if (CollectionUtils.isNotEmpty(errorMsg)) {
            throw new BaseException(String.format("请检查第%s条数据\r", JSONObject.toJSONString(new HashSet<>(idList))) + errorMsg.toString());
        }
    }

    /**
     * 请求校验
     */
    default void verify() {
    }

    /**
     * 处理
     */
    default void handle() {
    }

    /**
     * string转换list
     *
     * @param s ,
     * @return
     */
    default List<String> splitListStr(String s) {
        s = Optional.ofNullable(s)
                .filter(StringUtils::isNotBlank)
                .map(x -> x.replace(" ", ","))
                .map(x -> x.replace("，", ","))
                .map(x -> x.replace(";", ",")).orElse("");
        return Arrays.asList(s.split(","));
    }

    /**
     * 校验文件名称是否合法
     *
     * @param file
     */
    static void verifyFileName(MultipartFile file) {
        AssertUtil.isTrue(ObjectUtils.allNotNull(file), "上传文件不存在");
        String originalFilename = file.getOriginalFilename();
        originalFilename = Optional.ofNullable(originalFilename).orElse("");
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf + 1);
        boolean isXls = "xls".equals(suffix);
        boolean isXlsx = "xlsx".equals(suffix);
        AssertUtil.isTrue(isXls || isXlsx, "请上传xls或xlsx文件");
    }

    /**
     * 导出
     *
     * @param file
     * @param t
     * @return
     */
    static <T> List<T> export(MultipartFile file, Class<T> t) {
        verifyFileName(file);
        try {
            ExcelUtil<T> excelUtil = new ExcelUtil<>(t);
            return excelUtil.importExcel(file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("文件解析异常");
        }
    }

    /**
     * 集合数据的拷贝
     *
     * @param sources: 数据源类
     * @param target:  目标类::new(eg: UserVO::new)
     * @return
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
        return copyListProperties(sources, target, null);
    }


    /**
     * 带回调函数的集合数据的拷贝（可自定义字段拷贝规则）
     *
     * @param sources:  数据源类
     * @param target:   目标类::new(eg: UserVO::new)
     * @param callBack: 回调函数
     * @return
     */
    static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target, BeanCopyUtilCallBack<S, T> callBack) {
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.get();
            BeanUtils.copyProperties(source, t);
            list.add(t);
            if (callBack != null) {
                // 回调
                callBack.callBack(source, t);
            }
        }
        return list;
    }
}

interface ExportValid {
}

@FunctionalInterface
interface BeanCopyUtilCallBack<S, T> {

    /**
     * 定义默认回调方法
     *
     * @param t
     * @param s
     */
    void callBack(S t, T s);
}
