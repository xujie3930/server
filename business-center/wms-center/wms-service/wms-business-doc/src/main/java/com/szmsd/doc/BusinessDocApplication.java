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
     * 1.项目认证（密码）
     *   a.使用oauth2.0的密码模式认证
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
     * 1.1项目认证（授权码）
     *   a.授权码模式认证
     *   b.oms作为认证服务器
     *   c.doc作为资源服务器
     *   d.请求资源导doc，如果doc验证无权限则跳转导oms服务器
     *   e.http://127.0.0.1:9200/oauth/authorize?client_id=doc&response_type=code&scope=server&redirect_uri=http://www.baidu.com
     *   f.http://127.0.0.1:9200/oauth/authorize?client_id=doc&response_type=code&scope=server&redirect_uri=http://www.sogou.com
     *   g.现在配置的baidu sogou是测试地址，正式需要配置新的地址
     *   h.在认证系统认证成功之后，会根据回调地址传会code，第三方系统根据code到认证系统请求登录动作
     *     授权码认证地址：http://127.0.0.1:9200/oauth/token
     *     请求参数：client_id:doc
     *              client_secret:123456
     *              grant_type:authorization_code
     *              code:回调地址返回的code
     *              redirect_uri:回调地址
     *   i.在h步骤中，认证成功之后会返回token相关信息，请求资源接口时，在header中携带参数
     *          Authorization:Bearer 认证系统返回的 access_token
     *   j.验证token
     *      验证地址：http://127.0.0.1:9200/oauth/check_token
     *      请求参数：token:认证系统返回的 access_token
     *   k.刷新token
     *      刷新地址：http://127.0.0.1:9200/oauth/token
     *      请求参数：client_id:doc
     *               client_secret:123456
     *               grant_type:refresh_token
     *               refresh_token:认证系统返回的 refresh_token
     *      刷新成功之后会返回新的 access_token 再次请求资源接口时，需要携带新的 access_token ，refresh_token 不变
     *
     * 2.日志
     *   a.日志采用logback输出，不存DB。每一个请求都会有一个请求ID，会在日志中体现。
     *
     * 3.接口
     *   a.整合接口时，采用feign调用。基于不改动原接口的准则。如果中间逻辑有变，在此服务中做整合修改。
     *   b.调用时，在各个业务模块的api包中写feign（有的还没有写feign接口）调用
     *   c.接口验证，字段验证采用validator。详细看validator.md，validator.txt
     *   d.validator测试类在TestValidator，junit测试可以看出验证是否正常。
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

