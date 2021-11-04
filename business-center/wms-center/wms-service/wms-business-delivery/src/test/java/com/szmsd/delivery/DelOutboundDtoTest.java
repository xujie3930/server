package com.szmsd.delivery;

import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.dto.DelOutboundAddressDto;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.service.wrapper.IDelOutboundAsyncService;
import com.szmsd.http.dto.CreateShipmentRequestDto;
import com.szmsd.http.dto.ShipmentAddressDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 17:07
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DelOutboundDtoTest {

    @Test
    public void beanCopy() {
        DelOutboundDto dto = new DelOutboundDto();
        dto.setIsFirst(true);
        DelOutboundAddressDto addressDto = new DelOutboundAddressDto();
        addressDto.setCity("深圳");
        dto.setAddress(addressDto);

        CreateShipmentRequestDto requestDto = BeanMapperUtil.map(dto, CreateShipmentRequestDto.class);

        System.out.println(requestDto.getIsFirst());
        ShipmentAddressDto address = requestDto.getAddress();
        System.out.println(address.getCity());
    }
    @Resource
    IDelOutboundAsyncService iDelOutboundAsyncService;
    @Test
    public void testCo() {
        iDelOutboundAsyncService.completed("CKCNID7321110100000016");
    }
}
