package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.bas.dto.BasSellerCertificateDto;
import com.szmsd.bas.dto.VatQueryDto;
import com.szmsd.bas.mapper.BasSellerCertificateMapper;
import com.szmsd.bas.service.IBasSellerCertificateService;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.service.IBasSerialNumberService;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author l
* @since 2021-03-10
*/
@Service
public class BasSellerCertificateServiceImpl extends ServiceImpl<BasSellerCertificateMapper, BasSellerCertificate> implements IBasSellerCertificateService {

    @Resource
    private BasSellerCertificateMapper basSellerCertificateMapper;
    @Resource
    private IBasSerialNumberService baseSerialNumberService;

    @Autowired
    private RemoteAttachmentService remoteAttachmentService;
        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public BasSellerCertificate selectBasSellerCertificateById(String id)
        {
        return baseMapper.selectById(id);
        }

        @Override
        public int delBasSellerCertificateByPhysics(String sellerCode)
        {
            return basSellerCertificateMapper.delBasSellerCertificateByPhysics(sellerCode);
        }
        @Override
        public int countVaildBasSellerCertificate(String sellerCode){
            QueryWrapper<BasSellerCertificate> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("seller_code",sellerCode);
            queryWrapper.eq("vaild","0");
            return super.count(queryWrapper);
        }
        /**
        * 查询模块列表
        *
        * @param basSellerCertificate 模块
        * @return 模块
        */
        @Override
        public List<BasSellerCertificate> selectBasSellerCertificateList(BasSellerCertificate basSellerCertificate)
        {
        QueryWrapper<BasSellerCertificate> where = new QueryWrapper<BasSellerCertificate>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增模块
        *
        * @param basSellerCertificate 模块
        * @return 结果
        */
        @Override
        public int insertBasSellerCertificate(BasSellerCertificate basSellerCertificate)
        {
        return baseMapper.insert(basSellerCertificate);
        }

        @Override
        public List<BasSellerCertificate> listVAT(VatQueryDto vatQueryDto){
            QueryWrapper<BasSellerCertificate> vatQueryWrapper = new QueryWrapper();
            vatQueryWrapper.eq("seller_code",vatQueryDto.getSellerCode());
            vatQueryWrapper.eq("country_code",vatQueryDto.getCountryCode());
            vatQueryWrapper.eq("is_active",true);
            vatQueryWrapper.eq("vaild","1");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(new Date());
            vatQueryWrapper.apply("'"+ dateString +"' < registration_date");
            return super.list(vatQueryWrapper);
        }

    /**
     * 批量新增
     * @param basSellerCertificateList
     * @return
     */
    @Override
        public Boolean insertBasSellerCertificateList(List<BasSellerCertificateDto> basSellerCertificateList){

        for(BasSellerCertificateDto b:basSellerCertificateList){
            if(CollectionUtils.isNotEmpty(b.getDocumentsFiles())){
                String imageCode;
                if(StringUtils.isNotEmpty(b.getAttachment())){
                    imageCode = b.getAttachment();
                }else{
                    imageCode = b.getSellerCode()+baseSerialNumberService.generateNumber("CERTIFICATE");
                }
                b.setAttachment(imageCode);
                AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(imageCode).businessItemNo(null).fileList(b.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.SELLER_CERTIFICATE_DOCUMENT).build();
                this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
            }
        }

            return super.saveBatch(BeanMapperUtil.mapList(basSellerCertificateList,BasSellerCertificate.class));
        }

        /**
        * 修改模块
        *
        * @param basSellerCertificate 模块
        * @return 结果
        */
        @Override
        public int updateBasSellerCertificate(BasSellerCertificate basSellerCertificate)
        {
        return baseMapper.updateById(basSellerCertificate);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteBasSellerCertificateByIds(List<String>  ids)
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
        public int deleteBasSellerCertificateById(String id)
        {
        return baseMapper.deleteById(id);
        }


        @Override
        public boolean review(BasSellerCertificate basSellerCertificate){
            return super.updateById(basSellerCertificate);
        }

    }

