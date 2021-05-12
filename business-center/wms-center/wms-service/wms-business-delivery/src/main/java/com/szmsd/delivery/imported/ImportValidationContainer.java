package com.szmsd.delivery.imported;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 20:00
 */
public class ImportValidationContainer<T> {

    private final ImportContext<T> importContext;

    private final List<ImportValidation<T>> validationList;

    public ImportValidationContainer(ImportContext<T> importContext, List<ImportValidation<T>> validationList) {
        this.importContext = importContext;
        this.validationList = validationList;
    }

    /**
     * 验证
     *
     * @return ImportResult
     */
    public ImportResult valid() {
        List<T> dataList = this.importContext.getDataList();
        if (CollectionUtils.isEmpty(dataList)
                || CollectionUtils.isEmpty(this.validationList)) {
            return ImportResult.buildSuccess();
        }
        boolean isBreak = false;
        for (ImportValidation<T> validation : this.validationList) {
            if (!validation.before()) {
                isBreak = true;
            }
        }
        if (isBreak) {
            return ImportResult.buildFail(this.importContext.getMessageList());
        }
        for (int i = 0; i < dataList.size(); i++) {
            T object = dataList.get(i);
            for (ImportValidation<T> validation : this.validationList) {
                validation.valid(i + 1, object);
            }
        }
        for (ImportValidation<T> validation : this.validationList) {
            validation.after();
        }
        if (CollectionUtils.isEmpty(this.importContext.getMessageList())) {
            return ImportResult.buildSuccess();
        }
        return ImportResult.buildFail(this.importContext.getMessageList());
    }

    /**
     * 验证
     *
     * @return ImportResult
     */
    public ImportResultData<T> validData() {
        List<T> dataList = this.importContext.getDataList();
        if (CollectionUtils.isEmpty(dataList)
                || CollectionUtils.isEmpty(this.validationList)) {
            return ImportResultData.buildSuccessData();
        }
        boolean isBreak = false;
        for (ImportValidation<T> validation : this.validationList) {
            if (!validation.before()) {
                isBreak = true;
            }
        }
        if (isBreak) {
            return ImportResultData.buildFailData(this.importContext.getMessageList());
        }
        for (int i = 0; i < dataList.size(); i++) {
            T object = dataList.get(i);
            for (ImportValidation<T> validation : this.validationList) {
                validation.valid(i + 1, object);
            }
        }
        for (ImportValidation<T> validation : this.validationList) {
            validation.after();
        }
        if (CollectionUtils.isEmpty(this.importContext.getMessageList())) {
            return ImportResultData.buildSuccessData();
        }
        return ImportResultData.buildFailData(this.importContext.getMessageList());
    }
}
