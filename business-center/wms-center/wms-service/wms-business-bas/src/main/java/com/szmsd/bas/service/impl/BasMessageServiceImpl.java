package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.domain.BasMessage;
import com.szmsd.bas.dto.BasMessageDto;
import com.szmsd.bas.dto.BasMessageQueryDTO;
import com.szmsd.bas.mapper.BasMessageMapper;
import com.szmsd.bas.mapper.BasSellerMessageMapper;
import com.szmsd.bas.service.IBasMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.service.IBasSellerMessageService;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author l
* @since 2021-04-25
*/
@Service
public class BasMessageServiceImpl extends ServiceImpl<BasMessageMapper, BasMessage> implements IBasMessageService {

    @Resource
    private BasSellerMessageMapper basSellerMessageMapper;
    @Autowired
    private IBasSellerMessageService basSellerMessageService;

    @Autowired
    private RemoteAttachmentService remoteAttachmentService;

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public BasMessageDto selectBasMessageById(Long id)
        {
            BasMessageDto basMessageDto = BeanMapperUtil.map(baseMapper.selectById(id),BasMessageDto.class);
            List<BasAttachment> attachment = ListUtils.emptyIfNull(remoteAttachmentService
                            .list(new BasAttachmentQueryDTO().setAttachmentType(AttachmentTypeEnum.MESSAGE_IMAGE.getAttachmentType()).setBusinessNo(id.toString()).setBusinessItemNo(null)).getData());
                    if (CollectionUtils.isNotEmpty(attachment)) {
                        List<AttachmentFileDTO> documentsFiles = new ArrayList();
                        for (BasAttachment a : attachment) {
                            documentsFiles.add(new AttachmentFileDTO().setId(a.getId()).setAttachmentName(a.getAttachmentName()).setAttachmentUrl(a.getAttachmentUrl()));
                        }
                        basMessageDto.setRevealDocumentsFiles(documentsFiles);
                    }

            return basMessageDto;
        }

        @Override
        public List<BasMessageDto> selectBasMessageById(List<Long> ids){
            QueryWrapper<BasMessage> basMessageQueryWrapper = new QueryWrapper<>();
            basMessageQueryWrapper.in("id",ids);
            List<BasMessageDto> basMessageDtos = BeanMapperUtil.mapList(super.list(basMessageQueryWrapper),BasMessageDto.class);
            basMessageDtos.stream().forEach(b-> {
                List<BasAttachment> attachment = ListUtils.emptyIfNull(remoteAttachmentService
                        .list(new BasAttachmentQueryDTO().setAttachmentType(AttachmentTypeEnum.MESSAGE_IMAGE.getAttachmentType()).setBusinessNo(b.getId().toString()).setBusinessItemNo(null)).getData());
                if (CollectionUtils.isNotEmpty(attachment)) {
                    List<AttachmentFileDTO> documentsFiles = new ArrayList();
                    for (BasAttachment a : attachment) {
                        documentsFiles.add(new AttachmentFileDTO().setId(a.getId()).setAttachmentName(a.getAttachmentName()).setAttachmentUrl(a.getAttachmentUrl()));
                    }
                    b.setRevealDocumentsFiles(documentsFiles);
                }
            }

        );
            return basMessageDtos;
}

        /**
        * 查询模块列表
        *
        * @param dto 模块
        * @return 模块
        */
        @Override
        public List<BasMessage> selectBasMessageList(BasMessageQueryDTO dto)
        {
            QueryWrapper<BasMessage> where = new QueryWrapper<BasMessage>();
            queryHandle(where,dto);
            where.orderByDesc("create_time");
            return baseMapper.selectList(where);
        }

        /**
        * 新增模块
        *
        * @param basMessage 模块
        * @return 结果
        */
        @Override
        public void insertBasMessage(BasMessageDto basMessage)
        {
            /*basMessage.setCreateByName(SecurityUtils.getLoginUser().getUsername());*/
            baseMapper.insertBasMessage(basMessage);
            if (CollectionUtils.isNotEmpty(basMessage.getDocumentsFiles())) {
                AttachmentDTO attachmentDTO = AttachmentDTO.builder().businessNo(basMessage.getId().toString()).businessItemNo(null).fileList(basMessage.getDocumentsFiles()).attachmentTypeEnum(AttachmentTypeEnum.MESSAGE_IMAGE).build();
                this.remoteAttachmentService.saveAndUpdate(attachmentDTO);
            }
            //同步数据到用户
            basSellerMessageService.insertBasSellerMessage(basMessage.getId(),basMessage.getBullet());
        }

        /**
        * 修改模块
        *
        * @param basMessage 模块
        * @return 结果
        */
        @Override
        public int updateBasMessage(BasMessage basMessage)
        {
        return baseMapper.updateById(basMessage);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteBasMessageByIds(List<Long>  ids)
       {
           //删除用户消息表
            basSellerMessageMapper.deleteBasSellerMessage(ids);
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        @Override
        public int deleteBasMessageById(String id)
        {
        return baseMapper.deleteById(id);
        }

        private void queryHandle(QueryWrapper<BasMessage> queryWrapper, BasMessageQueryDTO basMessageQueryDTO){
            QueryWrapperUtil.filterDate(queryWrapper,"create_time",basMessageQueryDTO.getCreateTimes());
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ,"type",basMessageQueryDTO.getType());
            QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE,"title",basMessageQueryDTO.getTitle());
        }



    }

