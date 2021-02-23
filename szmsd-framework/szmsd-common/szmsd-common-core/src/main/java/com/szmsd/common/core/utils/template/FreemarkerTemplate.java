package com.szmsd.common.core.utils.template;

import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Locale;
import java.util.Map;

import static com.szmsd.common.core.web.controller.BaseController.getLen;

/**
 * @author liyingfeng
 * @date 2020/8/18 9:40
 */
@Slf4j
public class FreemarkerTemplate {

	private Configuration configuration = new Configuration(
			Configuration.VERSION_2_3_23);
	private String charset;

	public FreemarkerTemplate(String charset) {
		this.charset = charset;
		configuration.setEncoding(Locale.CHINA, charset);
		configuration.setClassicCompatible(true);//处理空值为空字符串  
	}

	public FreemarkerTemplate(Configuration configuration, String charset) {
		this.charset = charset;
		this.configuration = configuration;
		configuration.setEncoding(Locale.CHINA, charset);
		configuration.setClassicCompatible(true);//处理空值为空字符串
	}


	public void setTemplateClassPath(Class resourceLoaderClass,
			String basePackagePath) {
		configuration.setClassForTemplateLoading(resourceLoaderClass,
				basePackagePath);
	}

	public void setTemplateDirectoryPath(String templatePath)
			throws IOException {
		configuration.setDirectoryForTemplateLoading(new File(templatePath));
	}

	public void processToStream(String templateFileName,
			Map<String, Object> dataMap, Writer writer) throws Throwable {
		Template template = configuration.getTemplate(templateFileName);
		template.process(dataMap, writer);
	}

	public void processToFile(String templateFileName,
			Map<String, Object> dataMap, File outFile) throws Throwable {
		Writer writer = new OutputStreamWriter(new FileOutputStream(outFile),
				charset);
		try {
			processToStream(templateFileName, dataMap, writer);
		} catch (Throwable e) {
			log.info("生成html失败：{}",e);
			throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.FAIL, getLen());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public String processToString(String templateFileName,
			Map<String, Object> dataMap) throws Throwable {
		Writer writer = new StringWriter(2048);
		try {
			processToStream(templateFileName, dataMap, writer);
			return writer.toString();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
