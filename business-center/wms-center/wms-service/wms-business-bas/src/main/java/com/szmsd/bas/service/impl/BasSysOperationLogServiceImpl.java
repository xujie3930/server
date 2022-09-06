package com.szmsd.bas.service.impl;

import com.szmsd.bas.domain.BasSysOperationLog;
import com.szmsd.bas.mapper.BasSysOperationLogMapper;
import com.szmsd.bas.service.IBasSysOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import java.util.List;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author admin
* @since 2022-09-05
*/
@Service
public class BasSysOperationLogServiceImpl extends ServiceImpl<BasSysOperationLogMapper, BasSysOperationLog> implements IBasSysOperationLogService {


        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        @Override
        public BasSysOperationLog selectBasSysOperationLogById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询模块列表
        *
        * @param basSysOperationLog 模块
        * @return 模块
        */
        @Override
        public List<BasSysOperationLog> selectBasSysOperationLogList(BasSysOperationLog basSysOperationLog)
        {
        QueryWrapper<BasSysOperationLog> where = new QueryWrapper<BasSysOperationLog>();
        return baseMapper.selectList(where);
        }

        /**
        * 新增模块
        *
        * @param basSysOperationLog 模块
        * @return 结果
        */
        @Override
        public int insertBasSysOperationLog(BasSysOperationLog basSysOperationLog)
        {
        return baseMapper.insert(basSysOperationLog);
        }

        /**
        * 修改模块
        *
        * @param basSysOperationLog 模块
        * @return 结果
        */
        @Override
        public int updateBasSysOperationLog(BasSysOperationLog basSysOperationLog)
        {
        return baseMapper.updateById(basSysOperationLog);
        }

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        @Override
        public int deleteBasSysOperationLogByIds(List<String>  ids)
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
        public int deleteBasSysOperationLogById(String id)
        {
        return baseMapper.deleteById(id);
        }



    }

