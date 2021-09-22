package com.szmsd.doc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerAutoConfiguration {

    @Bean
    public Docket api() {
        List<Parameter> pars = new ArrayList<>();
        pars.add(new ParameterBuilder().name("Authorization")
                .description("请求令牌")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true)
                .scalarExample("Bearer 62b60ccc-ee49-4cd5-980f-656d4622d68c")
                .build());
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .apiInfo(apiInfo())
                .select()
                // 为当前包路径
                .apis(RequestHandlerSelectors.basePackage("com.szmsd.doc"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
    }

    // 构建 api文档的详细信息函数,注意这里的注解引用的是哪个
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 页面标题
                .title("DM FULFILLMENT RESTful API")
                // 创建人信息
                .contact(new Contact("admin", "", ""))
                // 版本号
                .version("1.0")
                // 描述
                .description("API 描述")
                .build();
    }

}

