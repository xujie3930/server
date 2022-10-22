package com.szmsd.delivery.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.delivery.domain.BasShipmentRules;
import com.szmsd.delivery.dto.BasShipmentRulesDto;
import com.szmsd.delivery.mapper.BasShipmentRulesMapper;
import com.szmsd.delivery.service.BasShipmenRulesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BasShipmenRulesServiceImpl extends ServiceImpl<BasShipmentRulesMapper, BasShipmentRules> implements BasShipmenRulesService {
    @Override
    public List<BasShipmentRules> selectBasShipmentRules(BasShipmentRulesDto basShipmentRulesDto) {
        return baseMapper.selectLists(basShipmentRulesDto);
    }

    @Override
    public R addBasShipmentRules(BasShipmentRulesDto basShipmentRulesDto) {

        try {
            BasShipmentRules basShipmentRules=new BasShipmentRules();
            BeanUtils.copyProperties(basShipmentRulesDto,basShipmentRules);
            if (basShipmentRulesDto.getServiceChannelName()==null||basShipmentRulesDto.getServiceChannelName().equals("")){
                String serviceChannelName=baseMapper.selectserviceChannelName(basShipmentRulesDto.getProductCode());
                if (serviceChannelName!=null&&!serviceChannelName.equals("")){
                    basShipmentRules.setServiceChannelName(serviceChannelName);
                }
            }
            baseMapper.insertSelective(basShipmentRules);
            return R.ok("添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return R.failed("添加失败");
        }

    }

    @Override
    public R updeteBasShipmentRules(BasShipmentRulesDto basShipmentRulesDto) {
        try {
            BasShipmentRules basShipmentRules=new BasShipmentRules();
            BeanUtils.copyProperties(basShipmentRulesDto,basShipmentRules);
            if (basShipmentRulesDto.getServiceChannelName()==null||basShipmentRulesDto.getServiceChannelName().equals("")){
                String serviceChannelName=baseMapper.selectserviceChannelName(basShipmentRulesDto.getProductCode());
                if (serviceChannelName!=null&&!serviceChannelName.equals("")){
                    basShipmentRules.setServiceChannelName(serviceChannelName);
                }
            }
            baseMapper.updateByPrimaryKeySelective(basShipmentRules);
            return R.ok("修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return R.failed("修改失败");
        }
    }

    @Override
    public R deleteShipmentRules(BasShipmentRulesDto basShipmentRulesDto) {
        try {
            if (basShipmentRulesDto.getIds().size()>0) {
                basShipmentRulesDto.getIds().forEach(x -> {
                    baseMapper.deleteByPrimaryKey(x);
                });
            }else if (basShipmentRulesDto.getIds().size()==0){
                return R.failed("没有选择数据进行删除");
            }

            return R.ok("删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return R.failed("修改失败");
        }
    }
}
