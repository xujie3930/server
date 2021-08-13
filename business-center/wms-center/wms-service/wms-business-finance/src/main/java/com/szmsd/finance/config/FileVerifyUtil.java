package com.szmsd.finance.config;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: FileVerifyUtil
 * @Description: 文件上传-文件校验
 * @Author: 11
 * @Date: 2021-04-10 11:42
 */
public final class FileVerifyUtil {

    /**
     * 校验文件名称是否合法
     *
     * @param file
     */
    public static void verifyFileName(MultipartFile file) {
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
     * 导入
     *
     * @param file
     * @param t
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> t) {
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
     * 参数校验
     *
     * @param i      第x条数据
     * @param groups 校验规则
     */
    public static void validate(Object object,AtomicInteger i, Class<?>... groups) {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        Validator validator = vf.getValidator();
        Set<ConstraintViolation<Object>> set = validator.validate(object, groups);
        List<String> errorMsg = new ArrayList<>();
        List<Integer> idList = new ArrayList<>();
        for (ConstraintViolation<Object> constraintViolation : set) {
            System.out.println(constraintViolation.getPropertyPath() + ":" + constraintViolation.getMessage());
            errorMsg.add(constraintViolation.getMessage());
            idList.add(i.getAndIncrement());
        }
        if (CollectionUtils.isNotEmpty(errorMsg)) {
            throw new BaseException(String.format("请检查第%s条数据\r", JSONObject.toJSONString(new HashSet<>(idList))) + errorMsg.toString());
        }
    }

}
