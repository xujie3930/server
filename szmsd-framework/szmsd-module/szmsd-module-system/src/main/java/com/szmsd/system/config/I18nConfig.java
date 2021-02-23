package com.szmsd.system.config;


import com.szmsd.system.service.I18nService;
import com.szmsd.system.service.impl.I18nServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @ClassName:I18nConfig
 * @Description:多语言服务配置文件
 * @author GaoJunWen
 * @date 2020-04-22
 * @version V1.0
 */

@Configuration
@PropertySource("classpath:i18n.yml")
@ConfigurationProperties(prefix = "i18n")
public class I18nConfig {

    /**
     * 查询表名
     */
    @Value("${tableName}")
    private String tableName;

    /**
     * 语言1
     */
    @Value("${lang0}")
    private String lang0;

    /**
     * 语言2
     */
    @Value("${lang1}")
    private String lang1;

    /**
     * 语言3
     */
    @Value("${lang2}")
    private String lang2;

    /**
     * 语言4
     */
    @Value("${lang3}")
    private String lang3;

    /**
     * 语言5
     */
    @Value("${lang4}")
    private String lang4;

//    /**
//     * 初始化多语言列表
//     */
//    @Bean(initMethod = "initLang",name = "initSysLanguage")
//    I18nService i18nService()
//    {
//        return new I18nServiceImpl();
//    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getLang0() {
        return lang0;
    }

    public void setLang0(String lang0) {
        this.lang0 = lang0;
    }

    public String getLang1() {
        return lang1;
    }

    public void setLang1(String lang1) {
        this.lang1 = lang1;
    }

    public String getLang2() {
        return lang2;
    }

    public void setLang2(String lang2) {
        this.lang2 = lang2;
    }

    public String getLang3() {
        return lang3;
    }

    public void setLang3(String lang3) {
        this.lang3 = lang3;
    }

    public String getLang4() {
        return lang4;
    }

    public void setLang4(String lang4) {
        this.lang4 = lang4;
    }
}
