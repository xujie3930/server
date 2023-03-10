package com.szmsd.bas.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.FileUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.szmsd.bas.dto.EmailDelOutboundError;
import com.szmsd.bas.dto.EmailDelOutboundSuccess;
import com.szmsd.bas.dto.EmailObjectDto;
import com.szmsd.bas.dto.EmailObjectDtoVs;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class EmailUtil {

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    public void sendTemplateMail(String to, String subject, String template, Map<String, Object> model) throws Exception {
        try {
            Template template1 = freeMarkerConfigurer.getConfiguration().getTemplate(template);
            String templateHtml = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            this.sendHtmlMail(to, subject, templateHtml);
        } catch (TemplateException e) {
            log.error("??????????????????????????????", e);
            throw e;
        } catch (IOException e) {
            log.error("??????????????????????????????", e);
            throw e;
        }
    }

    public void sendHtmlMail(String to, String subject, String content) throws Exception {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("??????????????????????????????", e);
            throw e;
        }
    }


    public void sendEmailError(String to, String subject, String content) throws Exception {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("??????????????????????????????", e);
            throw e;
        }
    }

    /**
     * ?????? ????????? ?????? - ???????????????
     * @param to        ?????????
     * @param subject   ??????
     * @param text      ????????????
     * @param filePath  ????????????
     * @throws MessagingException
     */
    public void sendAttachmentMail(String to, String subject, String text, List<EmailObjectDtoVs> list){


            //????????????
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            //??????????????????
            MimeMessageHelper helper =null;

            //????????????
            File excelWriter = attachmentInfo(list);
            try {
                helper =  new MimeMessageHelper(mimeMessage,true);
                //?????????
                helper.setFrom(fromEmail);
                //???????????????
                //?????????
                helper.setTo(to);
                //??????
                helper.setSubject(subject);
                //????????????
                helper.setText(text,true);
                //????????????
                helper.addAttachment(MimeUtility.encodeWord("Update trackingNo.xlsx"), excelWriter);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("??????????????????{}",e.getMessage());
            }


            //??????
            javaMailSender.send(mimeMessage);
            log.info("?????????????????????????????????????????????{} ?????????{} ???????????????{} ???????????????{}",to,subject,text);

    }

    File attachmentInfo(List<EmailObjectDtoVs> list){
        ExcelWriter excelWriter = null;
        //????????????
        File cacheTmpFile = FileUtils.createTmpFile("Update trackingNo.xlsx");
        try {
            excelWriter = new ExcelWriterBuilder()
                    .file(cacheTmpFile)
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(true)
                    .build();
            //???????????????sheet??????
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "sheet1").head(EmailObjectDtoVs.class).build();
            excelWriter.write(list,writeSheet);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("??????????????????{}",e.getMessage());
        }finally {
            excelWriter.finish();
        }
        return cacheTmpFile;
    }

    public void sendAttachmentMaildelOut(String to, String subject, String text, List<EmailDelOutboundError> list,List<EmailDelOutboundSuccess> list2){


        //????????????
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        //??????????????????
        MimeMessageHelper helper =null;

        //????????????
        File excelWriter = attachmentInfodelout(list,list2);
        try {
            helper =  new MimeMessageHelper(mimeMessage,true);
            //?????????
            helper.setFrom(fromEmail);
            //???????????????
            //?????????
            helper.setTo(to);
            //??????
            helper.setSubject(subject);
            //????????????
            helper.setText(text,true);
            //????????????
//            Date date=new Date();
//            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
//            String fileName=simpleDateFormat.format(date).concat("??????????????????????????????.xlsx");
            //String filename = MimeUtility.encodeText(simpleDateFormat.format(date)+"??????????????????????????????"  + ".xlsx");
            helper.addAttachment(MimeUtility.encodeWord("??????????????????????????????.xlsx"), excelWriter);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("??????????????????{}",e.getMessage());
        }


        //??????
        javaMailSender.send(mimeMessage);

    }


    File attachmentInfodelout(List<EmailDelOutboundError> list,List<EmailDelOutboundSuccess> list2){
        ExcelWriter excelWriter = null;
        //????????????
//        Date date=new Date();
//        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd");
//        String fileName=simpleDateFormat.format(date)+"??????????????????????????????.xlsx";
        File cacheTmpFile = FileUtils.createTmpFile("??????????????????????????????.xlsx");
        try {
            excelWriter = new ExcelWriterBuilder()
                    .file(cacheTmpFile)
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(true)
                    .build();
            //???????????????sheet??????
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "sheet1").head(EmailDelOutboundError.class).build();
            excelWriter.write(list,writeSheet);
            WriteSheet writeSheet1 = EasyExcel.writerSheet(1, "sheet2").head(EmailDelOutboundSuccess.class).build();
            excelWriter.write(list2,writeSheet1);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("??????????????????{}",e.getMessage());
        }finally {
            excelWriter.finish();
        }
        return cacheTmpFile;
    }




    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        String regEx1 = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

}

