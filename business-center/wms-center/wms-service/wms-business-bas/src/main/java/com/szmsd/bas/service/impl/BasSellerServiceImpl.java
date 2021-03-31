package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Producer;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.bas.dto.ActiveDto;
import com.szmsd.bas.dto.BasSellerDto;
import com.szmsd.bas.dto.BasSellerInfoDto;
import com.szmsd.bas.dto.BasSellerSysDto;
import com.szmsd.bas.mapper.BasSellerMapper;
import com.szmsd.bas.service.IBasSellerCertificateService;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.util.ObjectUtil;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.core.utils.ip.IpUtils;
import com.szmsd.common.core.utils.sign.Base64;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.SellerRequest;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.domain.dto.SysUserByTypeAndUserType;
import com.szmsd.system.api.domain.dto.SysUserDto;
import com.szmsd.system.api.feign.RemoteUserService;
import com.szmsd.system.api.model.UserInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;
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
    @Autowired
    private IBasSellerCertificateService basSellerCertificateService;
    @Resource
    private HtpBasFeignService htpBasFeignService;

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
        public List<BasSellerSysDto> selectBasSellerList(BasSeller basSeller)
        {
        QueryWrapper<BasSeller> where = new QueryWrapper<BasSeller>();
        if(basSeller.getIsActive()!=null){
            where.eq("is_active",basSeller.getIsActive());
        }
        QueryWrapperUtil.filter(where, SqlKeyword.EQ, "seller_code", basSeller.getSellerCode());
        QueryWrapperUtil.filter(where,SqlKeyword.LIKE,"user_name",basSeller.getUserName());
        List<BasSellerSysDto> basSellerSysDtos = BeanMapperUtil.mapList(baseMapper.selectList(where),BasSellerSysDto.class);
        for(BasSellerSysDto b:basSellerSysDtos){
            SysUserByTypeAndUserType sysUserByTypeAndUserType = new SysUserByTypeAndUserType();
            sysUserByTypeAndUserType.setUserType("01");
            sysUserByTypeAndUserType.setUsername(b.getUserName());
            UserInfo userInfo= remoteUserService.getUserInfo(sysUserByTypeAndUserType).getData();
            b.setSysId(userInfo.getSysUser().getUserId());
        }
        return basSellerSysDtos;
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
            //生成四位客户代码
            boolean b = false;
            while(b==false){
                String sellerCode = sellerCode();
                QueryWrapper<BasSeller> queryWrapperEmail = new QueryWrapper<>();
                queryWrapperEmail.eq("seller_code",sellerCode);
                int count = super.count(queryWrapperEmail);
                if(count==0){
                    dto.setSellerCode(sellerCode);
                    b = true;
                }
            }

            //生成四位验证码
            String ip = IpUtils.getIpAddr(request);
            String userAccountKey = ip + "-login";
            String s = (String) this.redisTemplate.opsForValue().get(userAccountKey);

            R r = new R();
            r.setCode(200);
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
            queryWrapperAccount.eq("user_name",dto.getUserName());
            count = super.count(queryWrapperAccount);
            if(count!=0){
                r.setData(false);
                r.setMsg("此用户名已经被注册请更换用户名");
                return r;
            }
            BasSeller basSeller = BeanMapperUtil.map(dto, BasSeller.class);
            basSeller.setState(false);
            basSeller.setIsActive(false);
            basSeller.setEmail(basSeller.getInitEmail());

            //查询客户经理
            if(StringUtils.isNotEmpty(dto.getServiceManagerName())){
                SysUserByTypeAndUserType sysUserByTypeAndUserType = new SysUserByTypeAndUserType();
                sysUserByTypeAndUserType.setNickName(dto.getServiceManagerName());
                R result = remoteUserService.getNameByNickName(sysUserByTypeAndUserType);
                if(result.getCode()==200){
                    SysUser sysUser = (SysUser)result.getData();
                    basSeller.setServiceManager(sysUser.getUserName());
                    basSeller.setServiceManagerName(sysUser.getNickName());
                }
            }
            //注册到系统用户表
            // 角色ID  121：认证之前客户角色 122：认证通过之后客户角色
            Long[] roleIds = {121L};
            SysUserDto sysUserDto = new SysUserDto();
            sysUserDto.setEmail(dto.getInitEmail());
            //账号状态正常
            sysUserDto.setStatus("0");
            String encryptPassword = SecurityUtils.encryptPassword(dto.getPassword());
            sysUserDto.setPassword(encryptPassword);
            sysUserDto.setUserName(dto.getUserName());
            sysUserDto.setUserType(UserConstants.USER_TYPE_CRS);
            sysUserDto.setRoleIds(roleIds);
            sysUserDto.setNickName(dto.getNickName());
            sysUserDto.setSellerCode(dto.getSellerCode());
            R sysUserResult = remoteUserService.baseCopyUserAdd(sysUserDto);
            if(sysUserResult.getCode() == -200){
                r.setData(false);
                r.setMsg(sysUserResult.getMsg());
            }
            //注册到wms
            SellerRequest sellerRequest = BeanMapperUtil.map(dto,SellerRequest.class);
            sellerRequest.setIsActive(true);
            R<ResponseVO> result = htpBasFeignService.createSeller(sellerRequest);
            if(!result.getData().getSuccess()){
                throw new BaseException("传wms失败"+r.getMsg());
            }
            //注册信息到卖家表
            baseMapper.insert(basSeller);
            r.setData(true);
            r.setMsg("注册成功");
            return r;
        }

        @Override
        public BasSellerInfoDto selectBasSeller(String userName){
            QueryWrapper<BasSeller> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_name",userName);
            BasSeller basSeller = super.getOne(queryWrapper);
           //查询用户证件信息
            QueryWrapper<BasSellerCertificate> BasSellerCertificateQueryWrapper = new QueryWrapper<>();
            BasSellerCertificateQueryWrapper.eq("seller_code",basSeller.getSellerCode());
            List<BasSellerCertificate> basSellerCertificateList = basSellerCertificateService.list(BasSellerCertificateQueryWrapper);
            BasSellerInfoDto basSellerInfoDto = BeanMapperUtil.map(basSeller,BasSellerInfoDto.class);
            basSellerInfoDto.setBasSellerCertificateList(basSellerCertificateList);
            return basSellerInfoDto;
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
        * @param basSellerInfoDto 模块
        * @return 结果
        */
        @Override
        public int updateBasSeller(BasSellerInfoDto basSellerInfoDto) throws IllegalAccessException {
            //查询表中信息
            QueryWrapper<BasSeller> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",basSellerInfoDto.getId());
            BasSeller bas = super.getOne(queryWrapper);
            //注册到wms
            SellerRequest sellerRequest = BeanMapperUtil.map(bas,SellerRequest.class);
            sellerRequest.setIsActive(true);
            ObjectUtil.fillNull(sellerRequest,bas);
            R<ResponseVO> r = htpBasFeignService.createSeller(sellerRequest);
            if(!r.getData().getSuccess()){
                throw new BaseException("传wms失败"+r.getMsg());
            }
            BasSeller basSeller = BeanMapperUtil.map(basSellerInfoDto,BasSeller.class);
            if(CollectionUtils.isNotEmpty(basSellerInfoDto.getBasSellerCertificateList())) {
                basSellerCertificateService.delBasSellerCertificateByPhysics(basSeller.getSellerCode());
                basSellerCertificateService.insertBasSellerCertificateList(basSellerInfoDto.getBasSellerCertificateList());
            }
            return baseMapper.updateById(basSeller);
        }


        /**
        * 批量删除模块
        *
        * @param
        * @return 结果
        */
        @Override
        public boolean deleteBasSellerByIds(ActiveDto activeDto) throws IllegalAccessException {
           UpdateWrapper<BasSeller> updateWrapper = new UpdateWrapper();
           updateWrapper.in("id",activeDto.getId());
           updateWrapper.set("is_active",activeDto.getIsActive());
           //同步wms
               QueryWrapper<BasSeller> queryWrapper = new QueryWrapper<>();
               queryWrapper.eq("id",activeDto.getId());
               BasSeller bas = super.getOne(queryWrapper);
               if(StringUtils.isNotEmpty(bas.getNameCn())) {
                   SellerRequest sellerRequest = BeanMapperUtil.map(bas, SellerRequest.class);
                   sellerRequest.setIsActive(activeDto.getIsActive());
                   ObjectUtil.fillNull(sellerRequest, bas);
                   R<ResponseVO> r = htpBasFeignService.createSeller(sellerRequest);
                   if(!r.getData().getSuccess()){
                       throw new BaseException("传wms失败"+r.getMsg());
                   }
               }
            SysUserDto userDto = new SysUserDto();
            userDto.setUserId(activeDto.getSysId());
               //禁用系统用户表
            if(activeDto.getIsActive()==true){
               userDto.setStatus("0");
            }else{
                userDto.setStatus("1");
            }
            remoteUserService.changeStatus(userDto);
           return super.update(updateWrapper);
       }

       @Override
       public String getSellerCode(BasSeller basSeller){
           QueryWrapper<BasSeller> queryWrapper = new QueryWrapper<>();
           queryWrapper.eq("user_name",basSeller.getUserName());
           BasSeller seller = super.getOne(queryWrapper);
           return seller.getSellerCode();
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

        private String sellerCode(){

            int  maxNum = 8;
            int  maxStr = 26;
            int i;
            int count = 0;
            char[] str = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                    'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                    'X', 'Y', 'Z'};
            char[] num = {'2', '3', '4', '5', '6', '7', '8', '9'};
            //int[] digit = {1,2,3};
            StringBuffer pwd = new StringBuffer("");
            Random r = new Random();
            int digit = (int)(Math.random()*3)+1;
            while(count < digit){
                i = Math.abs(r.nextInt(maxStr));
                if (i >= 0 && i < str.length) {
                    pwd.append(str[i]);
                    count ++;
                }
            }
            while(count < 4){
                i = Math.abs(r.nextInt(maxNum));
                if (i >= 0 && i < num.length) {
                    pwd.append(num[i]);
                    count ++;
                }
            }
            return pwd.toString();

        }

    }

