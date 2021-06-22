package com.szmsd.inventory.job;

import freemarker.template.Template;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class EmailUtil {

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    @Getter
    private String fromEmail;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    public boolean sendTemplateMail(String to, String subject, String template, Map<String, Object> model) {
        try {
            Template template1 = freeMarkerConfigurer.getConfiguration().getTemplate(template);
            String templateHtml = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            this.sendHtmlMail(to, subject, templateHtml);
            return true;
        } catch (Exception e) {
            log.error("发送邮件时发生异常！", e);
        }
        return false;
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

