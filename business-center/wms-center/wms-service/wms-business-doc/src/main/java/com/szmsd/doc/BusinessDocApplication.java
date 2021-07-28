package com.szmsd.doc;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableSwaggerBootstrapUI
@EnableFeignClients(basePackages = {"com.szmsd"})
@EnableHystrix
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BusinessDocApplication {

    /**
     * <blockquote><pre>
     *
     * 1.项目认证
     *   a.使用oauth2.0的授权码模式认证
     *   b.用户配置在yml中
     *   c.测试，密码认证（TestController中有例子）
     *      请求地址：http://127.0.0.1:17001/oauth/token
     *      请求参数：client_id:client
     *              client_secret:123456
     *              grant_type:password
     *              username:test
     *              password:123456
     *   d.访问测试接口
     *      请求地址：http://127.0.0.1:17001/echo
     *      请求头：Authorization:Bearer 62b60ccc-ee49-4cd5-980f-656d4622d68c
     *
     * 2.日志
     *   a.日志采用logback输出，不存DB。每一个请求都会有一个请求ID，会在日志中体现。
     *
     * 3.接口
     *   a.整合接口时，采用feign调用。基于不改动原接口的准则。如果中间逻辑有变，在此服务中做整合修改。
     *
     * 4.其它
     *   a.本模块没有api模块
     *   b.本模块不连db
     *   c.本模块需要redis，oauth2.0认证的存储服务采用redis
     *   d.为了避免doc模块和其它业务模块实体类冲突，doc模块中请求的实体类以Request为后缀，响应的实体类以Response为后缀
     *   e.Swagger相关配置看（DeliveryController，注意请求实体，注解等信息）
     *
     * </pre></blockquote>
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(BusinessDocApplication.class, args);
    }

}

