package com.szmsd.bas;


import com.szmsd.bas.api.feign.EmailFeingService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.mapper.BasWarehouseMapper;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.http.api.feign.YcMeetingFeignService;
import com.szmsd.http.domain.YcAppParameter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessBasApplication.class)
public class TestYcUtil {
// @Autowired
//  private YcMeetingFeignService ycMeetingFeignService;

// @Autowired
// private BasWarehouseMapper basWarehouseMapper;
    @Test
    public void postTest(){
        BasWarehouseMapper basWarehouseMapper=SpringUtils.getBean(BasWarehouseMapper.class);
        YcMeetingFeignService  ycMeetingFeignService= SpringUtils.getBean(YcMeetingFeignService.class);
        List<YcAppParameter> list=ycMeetingFeignService.selectBasYcappConfig().getData();
        list.forEach(x->{
            x.setService("getWarehouse");
            Map map=ycMeetingFeignService.YcApiri(x).getData();
            List<Map> list1= (List<Map>) map.get("data");
            list1.forEach(s->{
                BasWarehouseQueryDTO basWarehouseQueryDTO=new BasWarehouseQueryDTO();
                basWarehouseQueryDTO.setWarehouseCode(String.valueOf(s.get("warehouse_code")));
                basWarehouseQueryDTO.setWarehouseSource("YC");
                List<BasWarehouseVO> list2=basWarehouseMapper.selectListVO(basWarehouseQueryDTO);
                if (list2.size()>0){
                    list2.forEach(q->{
                        basWarehouseMapper.deleteById(q.getId());
                    });
                }

                BasWarehouse basWarehouse=new BasWarehouse();
                basWarehouse.setWarehouseCode(String.valueOf(s.get("warehouse_code")));
                basWarehouse.setWarehouseNameCn(String.valueOf(s.get("warehouse_name")));
                basWarehouse.setCountryCode(String.valueOf(s.get("country_code")));
                basWarehouse.setProvince(String.valueOf(s.get("state")));
                basWarehouse.setCity(String.valueOf(s.get("city")));
                basWarehouse.setStreet1(String.valueOf(s.get("street_address1")));
                basWarehouse.setStreet2(String.valueOf(s.get("street_address2")));
                basWarehouse.setAppToken(x.getAppToken());
                basWarehouse.setAppKey(x.getAppKey());
                basWarehouse.setWarehouseSource("YC");
                basWarehouseMapper.insert(basWarehouse);
            });
        });

    }
}
