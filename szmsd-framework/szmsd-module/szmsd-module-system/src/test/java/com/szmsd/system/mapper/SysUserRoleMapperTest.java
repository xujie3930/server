package com.szmsd.system.mapper;//package com.szmsd.system.mapper;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
//import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//
//import static org.junit.Assert.*;
//
//@RunWith(SpringRunner.class)
////@SpringBootTest
//@MybatisTest
////1.使用@MybatisTest默认会使用虚拟的数据源替代你配置的，如果想使用你配置的数据库则使用@AutoConfigureTestDatabase
////(replace =AutoConfigureTestDatabase.Replace.NONE) Replace.NONE 标识不替换数据源配置
//@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
//public class SysUserRoleMapperTest {
//   @Resource
//   private SysUserRoleMapper sysUserRoleMapper;
//
//    @Test
//    @Rollback(false)
//    public void deleteUserRoleByUserId() {
//
//       int i= sysUserRoleMapper.deleteUserRoleByUserId(9999l);
//        System.out.println("deleteUserRoleByUserId:"+i);
//    }
//
//    @Test
//    @Rollback(false)
//    public void deleteUserRoleAppByUserId() {
//    }
//}
