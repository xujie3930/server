package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasChannelWarehouse;
import com.szmsd.bas.api.domain.BasChannels;
import com.szmsd.bas.api.domain.dto.BasChannelsDTO;
import com.szmsd.bas.dao.BasChannelDateMapper;
import com.szmsd.bas.dao.BasChannelWarehouseMapper;
import com.szmsd.bas.dao.BasChannelsMapper;
import com.szmsd.bas.service.IBasChannelsService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IBasChannelsServiceImpl extends ServiceImpl<BasChannelsMapper, BasChannels> implements IBasChannelsService {

    @Autowired
    private BasChannelDateMapper basChannelDateMapper;
    @Autowired
    private BasChannelWarehouseMapper basChannelWarehouseMapper;


    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED,readOnly = false)
    @Override
    public R insertBasChannels(BasChannels basChannels) {

        try {
            //代表是新增
            if (basChannels.getId()==null){
                basChannels.setCreateTime(new Date());
                basChannels.setCreateBy(SecurityUtils.getUsername());
                basChannels.setCreateByName(SecurityUtils.getLoginUser().getUsername());
                baseMapper.insertSelective(basChannels);
                if (basChannels.getBasChannelDateList().size()>0){
                    basChannels.getBasChannelDateList().forEach(x->{
                        x.setChannelId(basChannels.getId());
                        x.setCreateTime(new Date());
                        //新增从表
                        basChannelDateMapper.insertSelective(x);
                    });

                }

                if (basChannels.getWarehouseList().size()>0){
                    basChannels.getWarehouseList().forEach(y->{
                        y.setChannelId(basChannels.getId());
                        //新增仓库中间表
                        basChannelWarehouseMapper.insertSelective(y);
                    });

                }

            }
            //修改
            if (basChannels.getId()!=null){
                basChannels.setUpdateTime(new Date());
                basChannels.setUpdateBy(SecurityUtils.getUsername());
                basChannels.setUpdateByName(SecurityUtils.getLoginUser().getUsername());
//                basChannels.setWarehouseCode(String.join(",", basChannels.getWarehouseCodeList()));
//                basChannels.setWarehouseName(String.join(",", basChannels.getWarehouseNameList()));
                baseMapper.updateByPrimaryKeySelective(basChannels);
                //新增从表时先删除原有数据
                basChannelDateMapper.deleteByPrimaryKey(basChannels.getId());
                if (basChannels.getBasChannelDateList().size()>0){
                    basChannels.getBasChannelDateList().forEach(x->{
                        x.setChannelId(basChannels.getId());
                        //新增从表
                        x.setCreateTime(new Date());
                        basChannelDateMapper.insertSelective(x);
                    });

                }

                //新增仓库中间表时先删除原有中间表数据
                basChannelWarehouseMapper.deleteByPrimaryKey(basChannels.getId());
                if (basChannels.getWarehouseList().size()>0){
                    basChannels.getWarehouseList().forEach(y->{
                        y.setChannelId(basChannels.getId());
                        //新增仓库中间表
                        basChannelWarehouseMapper.insertSelective(y);
                    });

                }
            }
            return R.ok("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.failed("操作失败");
        }
    }

    @Override
    public R<List<BasChannels>> selectBasChannels(BasChannelsDTO basFbaDTO) {
        try {
          List<BasChannels> list=baseMapper.selectBasChannels(basFbaDTO);
          //根据id查询关联仓库和从表数据
            list.forEach(x->{
                x.setBasChannelDateList(basChannelDateMapper.selectListChannelDate(x.getId()));
                List<BasChannelWarehouse> list1=basChannelWarehouseMapper.selectListWarehouseList(x.getId());
                x.setWarehouseList(list1);
                List<String> list2=list1.stream().map(s->String.valueOf(s.getWarehouseName())).collect(Collectors.toList());
                x.setWarehouseName(String.join(",", list2));
            });
            return   R.ok(list);
        }catch (Exception e){
            e.printStackTrace();
            return R.failed("查询失败");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED,readOnly = false)
    @Override
    public R deleteBasChannels(BasChannelsDTO basChannelsDTO) {
        try {

            List<Integer> list=basChannelsDTO.getIds();
            list.forEach(x->{
                baseMapper.deleteByPrimaryKey(x);
                basChannelDateMapper.deleteByPrimaryKey(x);
                basChannelWarehouseMapper.deleteByPrimaryKey(x);
            });


            return   R.ok("删除成功");
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.failed("删除失败");
        }
    }


}
