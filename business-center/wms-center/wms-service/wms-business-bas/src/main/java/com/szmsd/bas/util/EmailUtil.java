package com.szmsd.bas.util;

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
import java.io.File;
import java.io.IOException;
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

    /**
     * 发送 带附件 邮件 - 单附件发送
     * @param to        收件人
     * @param subject   主题
     * @param text      文本内容
     * @param filePath  文件路径
     * @throws MessagingException
     */
    public void sendAttachmentMail(String to,String subject,String text,String filePath){

        try {
            //邮件对象
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            //邮件处理对象
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);

            //发件人
            helper.setFrom("DM");

            //收件人
            helper.setTo(to);

            //主题
            helper.setSubject(subject);

            //文本内容
            helper.setText(text,true);

            //文件资源
            FileSystemResource resource = new FileSystemResource(new File(filePath));

            //文件名称
            String filename = resource.getFilename();

            //设置文件资源
            helper.addAttachment(filename,resource);

            //发送
            javaMailSender.send(mimeMessage);
            log.info("【单附件邮件发送成功】收件人：{} 主题：{} 文本内容：{} 文件路径：{}",to,subject,text,filePath);
        } catch (MessagingException e) {
            log.error("单附件邮件发送失败：{}",e);
        }
    }

    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

}

