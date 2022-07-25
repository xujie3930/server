package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.google.common.collect.Lists;
import com.szmsd.bas.domain.BasMessage;
import com.szmsd.bas.domain.BasSellerMessage;
import com.szmsd.bas.dto.BasMessageDto;
import com.szmsd.bas.dto.BasMessageQueryDTO;
import com.szmsd.bas.dto.BasSellerDto;
import com.szmsd.bas.dto.BasSellerMessageQueryDTO;
import com.szmsd.bas.mapper.BasSellerMessageMapper;
import com.szmsd.bas.service.IBasMessageService;
import com.szmsd.bas.service.IBasSellerMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author l
* @since 2021-04-25
*/
@Service
public class BasSellerMessageServiceImpl extends ServiceImpl<BasSellerMessageMapper, BasSellerMessage> implements IBasSellerMessageService {

    @Resource BasSellerMessageMapper basSellerMessageMapper;
    @Autowired
    private IBasSellerService basSellerService;

    @Resource
    private IBasMessageService basMessageService;

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public BasSellerMessage selectBasSellerMessageById(String id)
        {
        return baseMapper.selectById(id);
        }

        @Override
        public List<BasMessageDto> getBulletMessage(String sellerCode){
            QueryWrapper<BasSellerMessage> queryWrapper = new QueryWrapper<>();
            List<String> cusCodeList = StringUtils.isNotEmpty(sellerCode) ? Arrays.asList(sellerCode.split(",")) : Lists.newArrayList();
            queryWrapper.in("seller_code",cusCodeList);
            queryWrapper.eq("bullet",true);
            queryWrapper.orderByDesc("create_time");
            List<BasSellerMessage> list = super.list(queryWrapper);
            List<Long> ids = list.stream().map(o->o.getMessageId()).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(list)){
                List<BasMessageDto> basMessageDtos = basMessageService.selectBasMessageById(ids);
                return basMessageDtos;
            }
            return Collections.EMPTY_LIST;
        }

        /**
        * 查询模块列表
        *
        * @param dto 模块
        * @return 模块
        */
        @Override
        public List<BasMessageDto> selectBasSellerMessageList(BasSellerMessageQueryDTO dto)
        {
            QueryWrapper<BasSellerMessage> where = new QueryWrapper<BasSellerMessage>();
            queryHandle(where,dto);
            where.orderByDesc("m.create_time");
            return baseMapper.selectBasSellerMessage(where);
        }

        /**
        * 新增模块
        *
        * @param basSellerMessage 模块
        * @return 结果
        */
        @Override
        public int insertBasSellerMessage(BasSellerMessage basSellerMessage)
        {
        return baseMapper.insert(basSellerMessage);
        }

        @Override
        public void insertBasSellerMessage(Long id,Boolean bullet){
            List<String> sellerCodes = basSellerService.getAllSellerCode();
            for(String s:sellerCodes){
                BasSellerMessage basSellerMessage = new BasSellerMessage();
                basSellerMessage.setSellerCode(s);
                basSellerMessage.setMessageId(id);
                basSellerMessage.setBullet(bullet);
                basSellerMessage.setReadable(false);
                super.save(basSellerMessage);
            }
        }

        /**
        * 修改模块
        *
        * @param basSellerMessage 模块
        * @return 结果
        */
        @Override
        public void updateBasSellerMessage(BasSellerMessage basSellerMessage)
        {
            UpdateWrapper<BasSellerMessage> updateWrapper = new UpdateWrapper<BasSellerMessage>();
            updateWrapper.eq("seller_code",basSellerMessage.getSellerCode());
            updateWrapper.eq("message_id",basSellerMessage.getMessageId());
            if(basSellerMessage.getBullet()!=null) {
                updateWrapper.set("bullet", basSellerMessage.getBullet());
            }
            updateWrapper.set("readable",basSellerMessage.getReadable());
             super.update(updateWrapper);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteBasSellerMessageByIds(List<String>  ids)
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
        public int deleteBasSellerMessageById(String id)
        {
        return baseMapper.deleteById(id);
        }

    private void queryHandle(QueryWrapper<BasSellerMessage> queryWrapper, BasSellerMessageQueryDTO dto){
        QueryWrapperUtil.filterDate(queryWrapper,"m.create_time",dto.getCreateTimes());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ,"m.type",dto.getType());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE,"m.title",dto.getTitle());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ,"s.seller_code",dto.getSellerCode());
    }

    }

