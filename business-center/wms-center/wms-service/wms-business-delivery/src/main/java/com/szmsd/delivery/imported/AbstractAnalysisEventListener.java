package com.szmsd.delivery.imported;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象解析监听
 *
 * @param <T> 解析实体对象
 */
public abstract class AbstractAnalysisEventListener<T> extends AnalysisEventListener<T> {
    private final Logger logger = LoggerFactory.getLogger(AbstractAnalysisEventListener.class);

    private List<T> list;
    private boolean error;
    private List<ImportMessage> messageList;

    public AbstractAnalysisEventListener() {
        this.list = new ArrayList<>();
        this.error = false;
        this.messageList = new ArrayList<>();
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        this.list.add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (messageList.size() > 0) {
            this.error = true;
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        logger.error(exception.getMessage(), exception);
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException convertException = (ExcelDataConvertException) exception;
            Integer rowIndex = convertException.getRowIndex();
            Integer columnIndex = convertException.getColumnIndex();
            messageList.add(new ImportMessage(rowIndex + 1, columnIndex + 1, convertException.getCellData().toString(), "Data cannot be parsed"));
        }
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<ImportMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<ImportMessage> messageList) {
        this.messageList = messageList;
    }
}
