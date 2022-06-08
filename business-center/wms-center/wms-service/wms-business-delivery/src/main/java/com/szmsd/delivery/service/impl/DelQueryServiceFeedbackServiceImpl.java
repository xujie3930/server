package com.szmsd.delivery.service.impl;

import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelQueryServiceFeedback;
import com.szmsd.delivery.enums.DelQueryServiceFeedbackEnum;
import com.szmsd.delivery.mapper.DelQueryServiceFeedbackMapper;
import com.szmsd.delivery.service.IDelQueryServiceFeedbackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import java.util.List;

/**
* <p>
    * 查件服务反馈 服务实现类
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/
@Service
public class DelQueryServiceFeedbackServiceImpl extends ServiceImpl<DelQueryServiceFeedbackMapper, DelQueryServiceFeedback> implements IDelQueryServiceFeedbackService {


        /**
        * 查询查件服务反馈模块
        *
        * @param id 查件服务反馈模块ID
        * @return 查件服务反馈模块
        */
        @Override
        public DelQueryServiceFeedback selectDelQueryServiceFeedbackById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询查件服务反馈模块列表
        *
        * @param delQueryServiceFeedback 查件服务反馈模块
        * @return 查件服务反馈模块
        */
        @Override
        public List<DelQueryServiceFeedback> selectDelQueryServiceFeedbackList(DelQueryServiceFeedback delQueryServiceFeedback)
        {
        QueryWrapper<DelQueryServiceFeedback> where = new QueryWrapper<DelQueryServiceFeedback>();
            where.eq("main_id", delQueryServiceFeedback.getMainId());
        return baseMapper.selectList(where);
        }

        /**
        * 新增查件服务反馈模块
        *
        * @param delQueryServiceFeedback 查件服务反馈模块
        * @return 结果
        */
        @Override
        public int insertDelQueryServiceFeedback(DelQueryServiceFeedback delQueryServiceFeedback)
        {



            if(delQueryServiceFeedback.getMainId() == null){
                throw new CommonException("400", "mainId不能为空");

            }

            DelQueryServiceFeedback paramDelQueryServiceFeedback = new DelQueryServiceFeedback();
            paramDelQueryServiceFeedback.setMainId(delQueryServiceFeedback.getMainId());
            List<DelQueryServiceFeedback>  dataList =
                    this.selectDelQueryServiceFeedbackList(paramDelQueryServiceFeedback);

            if(dataList.size() == 0){
                delQueryServiceFeedback.setType(DelQueryServiceFeedbackEnum.CREATE.getName());
            }else{
                delQueryServiceFeedback.setType(DelQueryServiceFeedbackEnum.FEEDBACK.getName());

            }
             return baseMapper.insert(delQueryServiceFeedback);
        }

        /**
        * 修改查件服务反馈模块
        *
        * @param delQueryServiceFeedback 查件服务反馈模块
        * @return 结果
        */
        @Override
        public int updateDelQueryServiceFeedback(DelQueryServiceFeedback delQueryServiceFeedback)
        {
        return baseMapper.updateById(delQueryServiceFeedback);
        }

        /**
        * 批量删除查件服务反馈模块
        *
        * @param ids 需要删除的查件服务反馈模块ID
        * @return 结果
        */
        @Override
        public int deleteDelQueryServiceFeedbackByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除查件服务反馈模块信息
        *
        * @param id 查件服务反馈模块ID
        * @return 结果
        */
        @Override
        public int deleteDelQueryServiceFeedbackById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

