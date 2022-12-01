package com.szmsd.bas.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.FileUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.szmsd.bas.dto.EmailObjectDto;
import com.szmsd.bas.dto.EmailObjectDtoVs;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
            log.error("发送邮件时发生异常！", e);
            throw e;
        } catch (IOException e) {
            log.error("发送邮件时发生异常！", e);
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
            log.error("发送邮件时发生异常！", e);
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
            log.error("发送邮件时发生异常！", e);
            throw e;
        }
    }

    /**
     * 发送 带附件 邮件 - 单附件发送
     * @param to        收件人
     * @param subject   主题
     * @param text      文本内容
     * @param filePath  文件路径
     * @throws MessagingException
     */
    public void sendAttachmentMail(String to, String subject, String text, List<EmailObjectDtoVs> list){


            //邮件对象
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            //邮件处理对象
            MimeMessageHelper helper =null;

            //创建附件
            File excelWriter = attachmentInfo(list);
            try {
                helper =  new MimeMessageHelper(mimeMessage,true);
                //发件人
                helper.setFrom(fromEmail);
                //收信人账号
                //收件人
                helper.setTo(to);
                //主题
                helper.setSubject(subject);
                //文本内容
                helper.setText(text,true);
                //加入附件
                helper.addAttachment(MimeUtility.encodeWord("Update trackingNo.xlsx"), excelWriter);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("报表发送失败{}",e.getMessage());
            }


            //发送
            javaMailSender.send(mimeMessage);
            log.info("【单附件邮件发送成功】收件人：{} 主题：{} 文本内容：{} 文件路径：{}",to,subject,text);

    }

    File attachmentInfo(List<EmailObjectDtoVs> list){
        ExcelWriter excelWriter = null;
        //生成文件
        File cacheTmpFile = FileUtils.createTmpFile("Update trackingNo.xlsx");
        try {
            excelWriter = new ExcelWriterBuilder()
                    .file(cacheTmpFile)
                    .excelType(ExcelTypeEnum.XLSX)
                    .autoCloseStream(true)
                    .build();
            //将数据写入sheet页中
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "sheet1").head(EmailObjectDtoVs.class).build();
            excelWriter.write(list,writeSheet);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("报表导出失败{}",e.getMessage());
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

