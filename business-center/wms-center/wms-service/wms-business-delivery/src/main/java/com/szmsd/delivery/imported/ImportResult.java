package com.szmsd.delivery.imported;

import com.szmsd.delivery.vo.DelOutboundAddResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 16:55
 */
@Data
@ApiModel(value = "ImportResult", description = "ImportResult对象")
public class ImportResult implements Serializable {

    @ApiModelProperty(value = "导入状态")
    private boolean status;

    @ApiModelProperty(value = "导入提示信息")
    private List<ImportMessage> messageList;

    @ApiModelProperty(value = "结果信息")
    private List<DelOutboundAddResponse> resultList;

    public ImportResult() {
    }

    public ImportResult(boolean status, List<ImportMessage> messageList) {
        this.status = status;
        this.messageList = messageList;
    }

    public static ImportResult buildSuccess() {
        return new ImportResult(true, null);
    }

    public static ImportResult buildSuccess(List<ImportMessage> messageList) {
        return new ImportResult(true, messageList);
    }

    public static ImportResult buildFail(List<ImportMessage> messageList) {
        return new ImportResult(false, messageList);
    }
}
