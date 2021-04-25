package com.szmsd.bas.service.impl;

import com.szmsd.bas.domain.BasSellerMessage;
import com.szmsd.bas.mapper.BasSellerMessageMapper;
import com.szmsd.bas.service.IBasSellerMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.service.IBasSellerService;
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
public class BasSellerMessageServiceImpl extends ServiceImpl<BasSellerMessageMapper, BasSellerMessage> implements IBasSellerMessageService {

    @Resource BasSellerMessageMapper basSellerMessageMapper;
    @Autowired
    private IBasSellerService basSellerService;

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

        /**
        * 查询模块列表
        *
        * @param basSellerMessage 模块
        * @return 模块
        */
        @Override
        public List<BasSellerMessage> selectBasSellerMessageList(BasSellerMessage basSellerMessage)
        {
        QueryWrapper<BasSellerMessage> where = new QueryWrapper<BasSellerMessage>();
        return baseMapper.selectList(where);
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
        public int updateBasSellerMessage(BasSellerMessage basSellerMessage)
        {
        return baseMapper.updateById(basSellerMessage);
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



    }

