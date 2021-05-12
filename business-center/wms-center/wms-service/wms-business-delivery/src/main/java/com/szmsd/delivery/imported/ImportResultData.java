package com.szmsd.delivery.imported;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-05-12 13:54
 */
@ApiModel(value = "ImportResultData", description = "ImportResultData对象")
public class ImportResultData<T> extends ImportResult {

    @ApiModelProperty(value = "数据")
    private T data;

    public ImportResultData(T data) {
        this.data = data;
    }

    public ImportResultData(boolean status, List<ImportMessage> messageList, T data) {
        super(status, messageList);
        this.data = data;
    }

    public static <T> ImportResultData<T> buildSuccessData() {
        return new ImportResultData<>(true, null, null);
    }

    public static <T> ImportResultData<T> buildSuccessData(T data) {
        return new ImportResultData<>(true, null, data);
    }

    public static <T> ImportResultData<T> buildFailData(List<ImportMessage> messageList) {
        return new ImportResultData<>(false, messageList, null);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
