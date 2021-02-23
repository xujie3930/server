package com.szmsd.gen.util;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.szmsd.common.core.web.domain.BaseEntity;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author szmsd
 * @date 2020/06/10 16:41
 */
public class GenPlusUtils {


    /**
     * <p>
     * 读取控制台内容
     * </p>
     */

    public static void codeGenerate(Map<String, Object> map) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(MapUtils.getString(map,"projectPath") + "/src/main/java");
        gc.setAuthor(System.getenv().get("USERNAME")); //自定义的创建人
        gc.setOpen(false);
        gc.setFileOverride(true);//是否覆盖文件
        gc.setBaseResultMap(true); // xml resultmap
        gc.setBaseColumnList(true); // xml columlist
        gc.setSwagger2(true);  //实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(MapUtils.getString(map, "url"));
        dsc.setDriverName(MapUtils.getString(map, "driver"));
        dsc.setUsername(MapUtils.getString(map, "usr"));
        dsc.setPassword(MapUtils.getString(map, "pwd"));
        dsc.setTypeConvert(new MySqlTypeConvert() {
            // 自定义数据库表字段类型转换【可选】
            @Override
            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                if (fieldType.toLowerCase().contains("datetime")) {
                    return DbColumnType.DATE;
                }
                return super.processTypeConvert(globalConfig, fieldType);
            }

        });
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();

        String packgeName=MapUtils.getString(map,"packgeName");
        pc.setParent(packgeName);
        pc.setController("controller");
        pc.setEntity("domain");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return MapUtils.getString(map,"projectPath") + "/src/main/resources/mapper/"
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(""); //不生成xml文件
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setSuperEntityClass(BaseEntity.class);
        strategy.setSuperControllerClass("import com.szmsd.common.core.web.controller.BaseController");
        // 公共父类
        // 写于父类中的公共字段
        strategy.setInclude(MapUtils.getString(map,"tableName").split(","));
        strategy.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
        System.out.println("执行完毕！");
    }

    public static void main(String[] args) {


        /**
         * 指定数据库配置信息
         * url     数据库连接地址
         * driver    驱动
         * usr       用户名
         * pwd       密码
         */
        String   url="jdbc:mysql://139.159.218.145:8090/gfs?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8";
        String driver="com.mysql.cj.jdbc.Driver";
        String usr="root";
        String pwd="msd123456";
        Map<String, Object> map = new HashMap<>();
        map.put("url",url );
        map.put("driver", driver);
        map.put("usr",usr);
        map.put("pwd", pwd);


        /**
         * 指定代码生成规则
         *
         * projectPath  项目所在的绝对路径
         * packgeName   项目的包名称
         * tableName  数据交库表名称  多个使用,隔开
         */
        String projectPath = "F:\\work\\api-mysql-new\\api_mysql\\business-center\\business-bil";
        String packgeName = "com.szmsd.bil";
        String tableName = "bil_third_tracking";
        map.put("projectPath",projectPath);
        map.put("packgeName", packgeName);
        map.put("tableName",tableName);

        codeGenerate(map);


    }
}
