package com.szmsd.delivery.event.listener;

import com.szmsd.bas.api.feign.BasTranslateFeignService;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.OperationLog;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEvent;
import com.szmsd.delivery.event.OperationLogEnum;
import com.szmsd.delivery.service.IOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * @author zhangyuyuan
 * @date 2021-06-22 16:30
 */
@Component
public class DelOutboundOperationLogListener {

    @Autowired
    private IOperationLogService operationLogService;
    @Resource
    private BasTranslateFeignService basTranslateFeignService;


    @Async
    @EventListener
    public void onApplicationEvent(DelOutboundOperationLogEvent event) {


        Object source = event.getSource();
        OperationLogEnum operationLogEnum = event.getOperationLogEnum();
        String tid = event.getTid();
        String userCode = event.getUserCode();
        String userName = event.getUserName();
        String ip = event.getIp();
        OperationLog operationLog = new OperationLog();
        operationLog.setCreateBy(userCode);
        operationLog.setCreateByName(userName);
        operationLog.setCreateTime(event.getNowDate());
        operationLog.setTraceId(tid);
        operationLog.setInvoiceNo(operationLogEnum.getInvoiceNo(source));
        operationLog.setInvoiceType(operationLogEnum.getInvoiceType(source));
        operationLog.setIp(ip);



        try {
            //操作类型做翻译
            R<String> r=  basTranslateFeignService.Translate(operationLogEnum.getType());
            if (r.getCode()== HttpStatus.SUCCESS){
                operationLog.setType(r.getData());
            }else{
                operationLog.setType(operationLogEnum.getType());
            }

            //操作内容做翻译，接口需要格式化掉字符
            String oldPattern = operationLogEnum.getFormat().toPattern().replaceAll("\\{", "<").replaceAll("}", ">");
            r = basTranslateFeignService.Translate(oldPattern);
            if (r.getCode()== HttpStatus.SUCCESS && StringUtils.isNotEmpty(r.getData())){
                MessageFormat newMessageFormat = new MessageFormat(r.getData().replaceAll("<", "\\{").replaceAll(">", "}"));
                operationLog.setContent(operationLogEnum.getLog(source, newMessageFormat));
            }else{
                operationLog.setContent(operationLogEnum.getLog(source, operationLogEnum.getFormat()));
            }

        }catch (Exception e){
            e.printStackTrace();
            if(operationLog.getType() == null){
                operationLog.setType(operationLogEnum.getType());
            }
            operationLog.setContent(operationLogEnum.getLog(source, operationLogEnum.getFormat()));
        }




        this.operationLogService.insertOperationLog(operationLog);
    }

}
