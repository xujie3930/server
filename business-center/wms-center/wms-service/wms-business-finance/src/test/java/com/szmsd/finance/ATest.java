package com.szmsd.finance;

import com.szmsd.finance.dto.UserCreditDetailDTO;
import com.szmsd.finance.enums.CreditConstant;
import com.szmsd.finance.mapper.AccountBalanceMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @ClassName: ATest
 * @Description:
 * @Author: 11
 * @Date: 2021-10-17 10:58
 */
@SpringBootTest()
@RunWith(SpringRunner.class)
public class ATest {
    @Resource
    private AccountBalanceMapper accountBalanceMapper;

    @Test
    public void test() {
        ArrayList<UserCreditDetailDTO> userCreditDetailDTOS = new ArrayList<>();

        {
            UserCreditDetailDTO userCreditDetailDTO = new UserCreditDetailDTO();

            userCreditDetailDTO.setCurrencyCode("USD11");
            userCreditDetailDTO.setCreditLine(new BigDecimal("11"));
            userCreditDetailDTO.setCreditTimeInterval(1);
            userCreditDetailDTO.setCreditType(CreditConstant.CreditTypeEnum.TIME_LIMIT);
            userCreditDetailDTOS.add(userCreditDetailDTO);
        }


        {
            UserCreditDetailDTO userCreditDetailDTO = new UserCreditDetailDTO();

            userCreditDetailDTO.setCurrencyCode("USD121");
            userCreditDetailDTO.setCreditLine(new BigDecimal("113"));
            userCreditDetailDTO.setCreditTimeInterval(4);
            userCreditDetailDTOS.add(userCreditDetailDTO);
        }

        accountBalanceMapper.updateCreditBatch(userCreditDetailDTOS, "CUS",new ArrayList<>());
    }
}
