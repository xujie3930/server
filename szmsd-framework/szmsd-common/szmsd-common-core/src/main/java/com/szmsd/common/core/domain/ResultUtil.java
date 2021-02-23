package com.szmsd.common.core.domain;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.constant.CommonConstant;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @FileName ResultUtil.java
 * @Description 规范化封装统一返回数据格式
 * @Date 2020-06-24 15:13
 * @Author Yan Hua
 * @Version 1.0
 */
public class ResultUtil {

    /**
     *  参数验证通用返回方法<br>
     *
     * @param result
     * @param <T>
     * @return
     */
    public static <T> Result<T> getValidResponseDto(BindingResult result) {
        Result res = new Result();
        res.setCode(CommonConstant.METHOD_ARGUMENT_NOT_VALID_CODE);
        res.setMessage("请补充必填参数");

        List<ValidResult> results = result.getAllErrors()
                .stream()
                .map(error -> new ValidResult(error.getDefaultMessage(), ((FieldError) error).getField()))
                .collect(Collectors.toList());
        res.setData(results);
        return res;
    }

    /**
     * 参数验证成功默认返回方法<br>
     *
     * @param data T
     * @return ResponseDto
     */
    public static <T> Result<T> getSuccessResponseDto(T data) {
        return new Result<>(CommonConstant.SUCCESS_CODE, CommonConstant.SUCCESS_MSG, data);
    }

    public static void main(String[] args) {
        JSONObject jo= (JSONObject) JSONObject.toJSON(getSuccessResponseDto(null));
        System.out.println(jo.toString());
    }

}
