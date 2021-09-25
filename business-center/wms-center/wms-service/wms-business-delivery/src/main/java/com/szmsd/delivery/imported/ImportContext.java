package com.szmsd.delivery.imported;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 16:59
 */
public class ImportContext<T> extends CacheContext.MapCacheContext<String, Object> implements Serializable {

    /**
     * 导入数据
     */
    private final List<T> dataList;


    /**
     * 提示信息
     */
    private final List<ImportMessage> messageList;

    public ImportContext(List<T> dataList) {
        this.dataList = dataList;
        this.messageList = new ArrayList<>();
    }

    /**
     * 增加提示信息
     *
     * @param message message
     */
    public void addMessage(ImportMessage message) {
        this.messageList.add(message);
    }

    public boolean isNull(Object object, int rowIndex, int columnIndex, String value, String message) {
        if (Objects.isNull(object)) {
            this.addMessage(new ImportMessage(rowIndex, columnIndex, value, message));
            return true;
        }
        return false;
    }

    public boolean isEmpty(String text, int rowIndex, int columnIndex, String value, String message) {
        if (StringUtils.isEmpty(text)) {
            this.addMessage(new ImportMessage(rowIndex, columnIndex, value, message));
            return true;
        }
        return false;
    }

    public boolean stringLength(String text, int maxLength, int rowIndex, int columnIndex, String message) {
        if (null != text && text.length() > maxLength) {
            this.addMessage(new ImportMessage(rowIndex, columnIndex, null, message));
            return true;
        }
        return false;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public List<ImportMessage> getMessageList() {
        return messageList;
    }
}
