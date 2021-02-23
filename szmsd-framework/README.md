## 系统简介

* 采用前后端分离的模式，微服务版本前端基于vue版本开发。
* 后端采用Spring Boot、Spring Cloud & Alibaba。
* 注册中心、配置中心选型Nacos，为权限认证使用OAuth2。

## 系统模块


~~~
com.szmsd     
├── business-center         // 业务模块
├── business-center-api     //业务模块对应的api[FeignClient、公共dto等写在这个里面，其他模块引入maven直接调用service层]
├── szmsd-gateway           // 网关模块 [8080]
├── szmsd-auth              // 认证中心 [9200]
├── szmsd-center            // 业务模块
│       └── szmsd-system                              // 系统模块 [9201]
                 └── controller                       //接口层
                 └── domain                           //域 
                        └── vo                        //返回数据VO    
                 └── enums                            //枚举    
                 └── mapper                           //mapper层    
                 └── service                          //service接口 
                     └── impl                         //业务实现类    
│       └── szmsd-gen                                 // 代码生成 [9202]
│       └── szmsd-job                                 // 定时任务 [9203]
├── szmsd-center-api         // 接口模块
│       └── szmsd-module-system-api                          // 系统接口
                 └── domain                           //域
                 └── factory                          //fallback 服务降级处理类，对应的Feign接口
                 └── feign                            //feign 接口
                 └── model                            //数据库表实体，可提供对外公用的【否则写业务模块】
                 └── util                             //工具类
├── szmsd-common            // 通用模块
│       └── szmsd-common-core                         // 核心模块
│       └── szmsd-common-datascope                    // 权限范围
│       └── szmsd-common-log                          // 日志记录
│       └── szmsd-common-redis                        // 缓存服务
│       └── szmsd-common-security                     // 安全模块
│       └── szmsd-common-swagger                      // 系统接口

├── szmsd-visual          // 图形化管理模块
│       └── szmsd-visual-monitor                      // 监控中心 [9100]
├──pom.xml                // 公共依赖
~~~

## 架构图


## 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构网点，树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构网点进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
12. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
13. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

## 演示体验


演示地址：http://szmsd.com
- 账号密码
- admin/admin123  

## 测试环境

- 数据库

         url: jdbc:mysql://116.63.66.153:5810/db_xzlfwl_website?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
         username: website
         password: website
- redis

        host: 183.3.221.237
        port: 26379
        password: 

