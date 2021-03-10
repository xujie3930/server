package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.dto.BasSellerDto;
import com.szmsd.bas.mapper.BasSellerMapper;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.ip.IpUtils;
import com.szmsd.common.core.utils.sign.Base64;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.domain.dto.SysUserByTypeAndUserType;
import com.szmsd.system.api.domain.dto.SysUserDto;
import com.szmsd.system.api.feign.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.google.code.kaptcha.*;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author l
* @since 2021-03-09
*/
@Service
public class BasSellerServiceImpl extends ServiceImpl<BasSellerMapper, BasSeller> implements IBasSellerService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private RemoteUserService remoteUserService;
    @Resource
    private Producer producer;

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public BasSeller selectBasSellerById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询模块列表
        *
        * @param basSeller 模块
        * @return 模块
        */
        @Override
        public List<BasSeller> selectBasSellerList(BasSeller basSeller)
        {
        QueryWrapper<BasSeller> where = new QueryWrapper<BasSeller>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增模块
        *
        * @param dto 模块
        * @return 结果
        */
        @Override
        public R<Boolean> insertBasSeller(HttpServletRequest request, BasSellerDto dto)
        {
            //生成四位验证码
            String ip = IpUtils.getIpAddr(request);
            String userAccountKey = ip + "-login";
            String s = (String) this.redisTemplate.opsForValue().get(userAccountKey);

            R r = new R();
            //判断验证码是否有效
            if (org.apache.commons.lang.StringUtils.isBlank(s)) {
                r.setData(false);
                r.setMsg("验证码过期请重新输入");
                return r;
            } else {
                if (!s.equals(dto.getCaptcha())) {
                    r.setData(false);
                    r.setMsg("验证码错误请重新输入");
                    return r;
                }
            }
            //有效的话删除验证码
            if (this.redisTemplate.hasKey(userAccountKey)) {
                this.redisTemplate.delete(userAccountKey);
            }
            //判断是否存在init_email
            QueryWrapper<BasSeller> queryWrapperEmail = new QueryWrapper<>();
            queryWrapperEmail.eq("init_email",dto.getInitEmail());
            int count = super.count(queryWrapperEmail);
            if(count!=0){
                r.setData(false);
                r.setMsg("此邮箱已经被注册请更换邮箱");
                return r;
            }
            //判断是否存在用户名
            QueryWrapper<BasSeller> queryWrapperAccount = new QueryWrapper<>();
            queryWrapperAccount.eq("account",dto.getUserName());
            count = super.count(queryWrapperAccount);
            if(count!=0){
                r.setData(false);
                r.setMsg("此用户名已经被注册请更换用户名");
                return r;
            }
            BasSeller basSeller = BeanMapperUtil.map(dto, BasSeller.class);
            basSeller.setState(false);
            basSeller.setIsActive(false);

            //查询客户经理
            SysUserByTypeAndUserType sysUserByTypeAndUserType = new SysUserByTypeAndUserType();
            sysUserByTypeAndUserType.setNickName(dto.getServiceManagerName());
            R result = remoteUserService.getNameByNickName(sysUserByTypeAndUserType);
            if((Boolean)result.getData()==true){
                basSeller.setServiceManager(result.getMsg());
            }

            //注册到系统用户表
            // 角色ID  121：认证之前客户角色 122：认证通过之后客户角色
            Long[] roleIds = {121L};
            SysUserDto sysUserDto = new SysUserDto();
            sysUserDto.setEmail(dto.getInitEmail());
            //账号状态正常
            sysUserDto.setStatus("0");
            String encryptPassword = SecurityUtils.encryptPassword(sysUserDto.getPassword());
            sysUserDto.setPassword(encryptPassword);
            sysUserDto.setUserName(dto.getUserName());
            sysUserDto.setUserType(UserConstants.USER_TYPE_CRS);
            sysUserDto.setRoleIds(roleIds);
            R sysUserResult = remoteUserService.baseCopyUserAdd(sysUserDto);
            if(sysUserResult.getCode() == -200){
                r.setData(false);
                r.setMsg(sysUserResult.getMsg());
            }
            //注册信息到卖家表
            baseMapper.insert(basSeller);

            r.setData(true);
            r.setMsg("注册成功");
            return r;
        }

    /**
     * 获取验证码
     * @return
     */
    @Override
        public R getCheckCode(HttpServletRequest request) {
        String ip = IpUtils.getIpAddr(request);
        String userAccountKey = ip + "-login";
        String checkCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        BufferedImage image = producer.createImage(checkCode);
        if (redisTemplate.hasKey(userAccountKey)) {
            redisTemplate.delete(userAccountKey);
        }
        redisTemplate.opsForValue().set(userAccountKey, checkCode, 60000, TimeUnit.MILLISECONDS);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return R.failed(e.getMessage());
        }
        R r = new R();
        r.setCode(HttpStatus.SUCCESS);
        r.setMsg("success");
        r.setData(Base64.encode(os.toByteArray()));
        return r;
    }

        /**
        * 修改模块
        *
        * @param basSeller 模块
        * @return 结果
        */
        @Override
        public int updateBasSeller(BasSeller basSeller)
        {
        return baseMapper.updateById(basSeller);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteBasSellerByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        @Override
        public int deleteBasSellerById(String id)
        {
        return baseMapper.deleteById(id);
        }

    }

