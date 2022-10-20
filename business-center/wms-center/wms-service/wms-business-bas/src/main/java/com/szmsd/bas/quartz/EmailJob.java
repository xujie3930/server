package com.szmsd.bas.quartz;

import com.szmsd.bas.domain.BasEmail;
import com.szmsd.bas.dto.EmailObjectDto;
import com.szmsd.bas.mapper.BasEmailMapper;
import com.szmsd.bas.util.EmailUtil;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;


import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class EmailJob extends QuartzJobBean {
    private final Logger logger = LoggerFactory.getLogger(EmailJob.class);
      @Autowired
      private BasEmailMapper basEmailMapper;
    @Resource
    private EmailUtil emailUtil;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
         List<BasEmail> list=basEmailMapper.selectByPrimaryKey();
//        BasEmail basEmail = new BasEmail();
//        basEmail.setEmpTo(entry.getKey());
        basEmailMapper.updateByPrimaryKey();
        //将组合的数据 分解成Map<List> (邮箱为key,组合这个邮箱下的所有信息)
        Map<String,List<BasEmail>> basMap=list.stream().collect(Collectors.groupingBy(BasEmail::getEmpTo));
        //循环map，得到每一组的数据 然后生成excel
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        if (list.size()>0) {
            for (Map.Entry<String, List<BasEmail>> entry : basMap.entrySet()) {

                List<EmailObjectDto> emailObjectDtoList = BeanMapperUtil.mapList(entry.getValue(), EmailObjectDto.class);
                logger.info("组合参数：{}", entry.getValue());

                emailUtil.sendAttachmentMail(entry.getKey(), "【DM】挂号更新/" + simpleDateFormat.format(new Date()) + "", "您好，\n" +
                        "\n" +
                        "请查收昨天中午12:00至今天中午12:00订单挂号的更新情况，如附件所示。\n" +
                        "\n" +
                        "谢谢！", emailObjectDtoList);

            }
        }




    }



}
