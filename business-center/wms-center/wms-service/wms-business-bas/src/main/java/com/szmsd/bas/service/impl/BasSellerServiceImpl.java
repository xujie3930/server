package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Producer;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.mapper.BasSellerMapper;
import com.szmsd.bas.service.IBasSellerCertificateService;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.util.ObjectUtil;
import com.szmsd.bas.vo.BasSellerCertificateVO;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.constant.UserConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.core.utils.ip.IpUtils;
import com.szmsd.common.core.utils.sign.Base64;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.SellerRequest;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.domain.dto.SysUserByTypeAndUserType;
import com.szmsd.system.api.domain.dto.SysUserDto;
import com.szmsd.system.api.feign.RemoteUserService;
import com.szmsd.system.api.model.UserInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
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

    @Autowired
    private RemoteAttachmentService remoteAttachmentService;

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
        public TableDataInfo<BasSellerSysDto> selectBasSellerList(BasSellerQueryDto basSeller)
        {
        QueryWrapper<BasSeller> where = new QueryWrapper<BasSeller>();
        if(basSeller.getIsActive()!=null){
            where.eq("is_active",basSeller.getIsActive());
        }
        QueryWrapperUtil.filter(where, SqlKeyword.EQ, "seller_code", basSeller.getSellerCode());
        QueryWrapperUtil.filter(where,SqlKeyword.LIKE,"user_name",basSeller.getUserName());
        int count = super.count(where);
            where.last("limit "+(basSeller.getPageNum()-1)*basSeller.getPageSize()+","+basSeller.getPageSize());
        List<BasSellerSysDto> basSellerSysDtos = BeanMapperUtil.mapList(baseMapper.selectList(where),BasSellerSysDto.class);
        for(BasSellerSysDto b:basSellerSysDtos){
            SysUserByTypeAndUserType sysUserByTypeAndUserType = new SysUserByTypeAndUserType();
            sysUserByTypeAndUserType.setUserType("01");
            sysUserByTypeAndUserType.setUsername(b.getUserName());
            UserInfo userInfo= remoteUserService.getUserInfo(sysUserByTypeAndUserType).getData();
            b.setSysId(userInfo.getSysUser().getUserId());
        }
            TableDataInfo<BasSellerSysDto> table = new TableDataInfo(basSellerSysDtos,count);
            table.setCode(200);
            return table;
        }

        @Override
        public List<BasSellerSysDto> getBasSellerList(BasSeller basSeller){
            QueryWrapper<BasSeller> where = new QueryWrapper<BasSeller>();
            if(basSeller.getIsActive()!=null){
                where.eq("is_active",basSeller.getIsActive());
            }
            QueryWrapperUtil.filter(where, SqlKeyword.EQ, "seller_code", basSeller.getSellerCode());
            QueryWrapperUtil.filter(where,SqlKeyword.LIKE,"user_name",basSeller.getUserName());
            List<BasSellerSysDto> basSellerSysDtos = BeanMapperUtil.mapList(baseMapper.selectList(where),BasSellerSysDto.class);
            return basSellerSysDtos;
        }

        /**
        * 新增模块
        *
        * @param dto 模块
        * @return 结果
        */
        @Transactional
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
            basSeller.setState(true);
            basSeller.setIsActive(true);
            basSeller.setEmail(basSeller.getInitEmail());

            //查询客户经理
            if(StringUtils.isNotEmpty(dto.getServiceManagerName())){
                SysUserByTypeAndUserType sysUserByTypeAndUserType = new SysUserByTypeAndUserType();
                sysUserByTypeAndUserType.setUsername(dto.getServiceManagerName());
                sysUserByTypeAndUserType.setUserType("00");
                R result = remoteUserService.getNameByUserName(sysUserByTypeAndUserType);
                basSeller.setServiceManagerName(null);
                if(result.getCode()==200){
                    SysUser sysUser = (SysUser)result.getData();
                    basSeller.setServiceManager(sysUser.getUserId().toString());
                    basSeller.setServiceManagerName(sysUser.getUserName());
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
            //注册到wms
            SellerRequest sellerRequest = BeanMapperUtil.map(dto,SellerRequest.class);
            sellerRequest.setIsActive(true);
            R sysUserResult = remoteUserService.baseCopyUserAdd(sysUserDto);
            if(sysUserResult.getCode() == -200){
                throw new BaseException("用户注册失败");
            }
            //注册信息到卖家表
            baseMapper.insert(basSeller);
            R<ResponseVO> result = htpBasFeignService.createSeller(sellerRequest);
            SysUser user = new SysUser();
            user.setEmail(dto.getInitEmail());
            if(result.getData().getSuccess()==null){
                if(result.getData().getErrors()!=null)
                {
                    //删除表中用户
                    remoteUserService.removeByemail(user);
                    throw new BaseException("传wms失败" + result.getData().getErrors());
                }
            }else{
                if(!result.getData().getSuccess())
                {
                    //删除表中用户
                    remoteUserService.removeByemail(user);
                    throw new BaseException("传wms失败" + result.getData().getMessage());
                }
            }
            r.setData(true);
            r.setMsg("注册成功");
            return r;
        }

        @Override
        public BasSellerInfoVO selectBasSeller(String userName){
            QueryWrapper<BasSeller> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_name",userName);
            BasSeller basSeller = super.getOne(queryWrapper);
           //查询用户证件信息
            QueryWrapper<BasSellerCertificate> BasSellerCertificateQueryWrapper = new QueryWrapper<>();
            BasSellerCertificateQueryWrapper.eq("seller_code",basSeller.getSellerCode());
            List<BasSellerCertificate> basSellerCertificateList = basSellerCertificateService.list(BasSellerCertificateQueryWrapper);
            List<BasSellerCertificateVO> basSellerCertificateVOS = BeanMapperUtil.mapList(basSellerCertificateList,BasSellerCertificateVO.class);
            basSellerCertificateVOS.forEach(b -> {
                if(b.getAttachment()!=null){
                    List<BasAttachment> attachment = ListUtils.emptyIfNull(remoteAttachmentService
                            .list(new BasAttachmentQueryDTO().setAttachmentType(AttachmentTypeEnum.SELLER_CERTIFICATE_DOCUMENT.getAttachmentType()).setBusinessNo(b.getAttachment()).setBusinessItemNo(null)).getData());
                    if (CollectionUtils.isNotEmpty(attachment)) {
                        List<AttachmentFileDTO> documentsFiles = new ArrayList();
                        for(BasAttachment a:attachment){
                            documentsFiles.add(new AttachmentFileDTO().setId(a.getId()).setAttachmentName(a.getAttachmentName()).setAttachmentUrl(a.getAttachmentUrl()));
                        }
                        b.setDocumentsFiles(documentsFiles);
                    }
                }
                });

            BasSellerInfoVO basSellerInfoVO = BeanMapperUtil.map(basSeller,BasSellerInfoVO.class);
            basSellerInfoVO.setBasSellerCertificateList(basSellerCertificateVOS);
            return basSellerInfoVO;
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
        redisTemplate.opsForValue().set(userAccountKey, checkCode, 120000, TimeUnit.MILLISECONDS);
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
        @Transactional
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
                throw new BaseException("传wms失败:"+r.getData().getMessage());
            }
            BasSeller basSeller = BeanMapperUtil.map(basSellerInfoDto,BasSeller.class);
            basSellerCertificateService.delBasSellerCertificateByPhysics(basSeller.getSellerCode());
            if(CollectionUtils.isNotEmpty(basSellerInfoDto.getBasSellerCertificateList())) {
                basSellerCertificateService.insertBasSellerCertificateList(basSellerInfoDto.getBasSellerCertificateList());
            }
            // 附件信息
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
                       throw new BaseException("传wms失败:"+r.getData().getMessage());
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

    @Override
    public String getLoginSellerCode(){
        QueryWrapper<BasSeller> queryWrapper = new QueryWrapper<>();
        if(SecurityUtils.getLoginUser()==null||SecurityUtils.getLoginUser().getUsername()==null){
            throw  new BaseException("无法获取当前登录用户信息");
        }
        queryWrapper.eq("user_name",SecurityUtils.getLoginUser().getUsername());
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

