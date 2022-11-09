package com.szmsd.delivery.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.delivery.domain.BasShipmentRules;
import com.szmsd.delivery.dto.BasShipmentRulesDto;
import com.szmsd.delivery.mapper.BasShipmentRulesMapper;
import com.szmsd.delivery.service.BasShipmenRulesService;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.dto.PricedProductSearchCriteria;
import com.szmsd.http.vo.PricedProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class BasShipmenRulesServiceImpl extends ServiceImpl<BasShipmentRulesMapper, BasShipmentRules> implements BasShipmenRulesService {

    @Resource
    private HtpPricedProductFeignService htpPricedProductFeignService;
    @Override
    public List<BasShipmentRules> selectBasShipmentRules(BasShipmentRulesDto basShipmentRulesDto) {
        return baseMapper.selectLists(basShipmentRulesDto);
    }

    @Override
    public R addBasShipmentRules(BasShipmentRulesDto basShipmentRulesDto) {

        try {
            List<String> customCodelist= Arrays.asList(basShipmentRulesDto.getCustomCode().split(","));


            customCodelist.forEach(x->{
                BasShipmentRules basShipmentRules=new BasShipmentRules();
                BeanUtils.copyProperties(basShipmentRulesDto,basShipmentRules);
                if (basShipmentRulesDto.getServiceChannelName()==null||basShipmentRulesDto.getServiceChannelName().equals("")){
                    String serviceChannelName=baseMapper.selectserviceChannelName(basShipmentRulesDto.getProductCode());
                    if (serviceChannelName!=null&&!serviceChannelName.equals("")){
                        basShipmentRules.setServiceChannelName(serviceChannelName);
                    }
                }

                if (basShipmentRulesDto.getProductCodeSon()!=null&&!basShipmentRulesDto.getProductCodeSon().equals("")) {
                    String serviceChannelSub = selectserviceChannelSub(basShipmentRulesDto.getProductCodeSon());
                    basShipmentRules.setServiceChannelSub(serviceChannelSub);
                }
                basShipmentRules.setCustomCode(x);
                //根据客户code和服务渠道查询是否有重复的数据
                List<BasShipmentRules> list1=baseMapper.selectListus(basShipmentRules);
                if (list1.size()==0){
                    baseMapper.insertSelective(basShipmentRules);
                }

            });

            return R.ok("添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return R.failed("添加失败");
        }

    }
    private  String selectserviceChannelSub(String productCodeSon){
        List<String> list= Arrays.asList(productCodeSon.split(","));
        List<String> serviceChannelSub=new ArrayList<>();
        list.forEach(x->{
            PricedProductSearchCriteria pricedProductSearchCriteria = new PricedProductSearchCriteria();
            pricedProductSearchCriteria.setPageNumber(1);
            pricedProductSearchCriteria.setPageSize(1);
            pricedProductSearchCriteria.setCode(x);
            //调用第三方获取物流服务
            R<PageVO<PricedProduct>> pageResult = htpPricedProductFeignService.pageResult(pricedProductSearchCriteria);
            serviceChannelSub.add(pageResult.getData().getData().get(0).getLogisticsRouteId());
        });
        return  StringUtils.join(serviceChannelSub, ",");
    }

    @Override
    public R updeteBasShipmentRules(BasShipmentRulesDto basShipmentRulesDto) {
        try {
            List<String> customCodelist= Arrays.asList(basShipmentRulesDto.getCustomCode().split(","));
            customCodelist.forEach(x->{
                BasShipmentRules basShipmentRules=new BasShipmentRules();
                BeanUtils.copyProperties(basShipmentRulesDto,basShipmentRules);
                if (basShipmentRulesDto.getServiceChannelName()==null||basShipmentRulesDto.getServiceChannelName().equals("")){
                    String serviceChannelName=baseMapper.selectserviceChannelName(basShipmentRulesDto.getProductCode());
                    if (serviceChannelName!=null&&!serviceChannelName.equals("")){
                        basShipmentRules.setServiceChannelName(serviceChannelName);
                    }
                }
                if (basShipmentRulesDto.getProductCodeSon()!=null&&!basShipmentRulesDto.getProductCodeSon().equals("")) {
                    String serviceChannelSub = selectserviceChannelSub(basShipmentRulesDto.getProductCodeSon());
                    basShipmentRules.setServiceChannelSub(serviceChannelSub);
                }
                basShipmentRules.setCustomCode(x);
                baseMapper.deleteByPrimaryKeyus(basShipmentRules);
                basShipmentRules.setId(null);
                baseMapper.insertSelective(basShipmentRules);
            });

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

    @Override
    public boolean selectbasShipmentRules(BasShipmentRulesDto basShipmentRulesDto) {
        BasShipmentRules basShipmentRules= baseMapper.selectbasShipmentRules(basShipmentRulesDto);
        if (basShipmentRules!=null){
            if (basShipmentRules.getIossType().equals("1")){
                return true;
            }else {
                return false;
            }
        }
        if (basShipmentRules==null){
            BasShipmentRules basShipmentRulesu= baseMapper.selectbasShipmentRulesu(basShipmentRulesDto);
            if (basShipmentRulesu!=null){
                if (basShipmentRulesu.getIossType().equals("1")){
                    return true;
                }else {
                    return false;
                }
            }
        }

        return false;
    }
}
